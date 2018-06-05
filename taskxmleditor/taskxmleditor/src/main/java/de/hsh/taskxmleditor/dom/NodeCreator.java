package de.hsh.taskxmleditor.dom;

import de.hsh.taskxmleditor.application.Configuration;
import de.hsh.taskxmleditor.xsd.XsdIntrospection;
import org.apache.xmlbeans.*;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.xml.namespace.QName;
import java.util.List;

public class NodeCreator {

    public static Attr createAttributeWithPreconfiguration(Element owningElement, QName attrName) {
        QName parentQn = DomHelper.getQName(owningElement);
        XsdIntrospection xsd = Configuration.getInstance().getXsd();
        SchemaProperty attr = xsd.getAttributeInfo(parentQn, attrName.getLocalPart());

        SchemaType elemType = xsd.getType(parentQn);
        SchemaAttributeModel aModel = elemType.getAttributeModel();
        SchemaLocalAttribute sAttr = aModel.getAttribute(attr.getName());
        String strDefVal = attr.getDefaultText();
        // We need to have a valid default value.
        // Since the JaxB binder throws when it doesn't get a valid value for a primitive type,
        // we need to create valid default values.
        // It cannot be null, and for non-string types, it cannot be an empty string either.
        if (null == strDefVal)
            strDefVal = getDefaultValueFor(attr.getType());
        return owningElement.getOwnerDocument().createAttributeNS(attrName.getNamespaceURI(),
                attrName.getLocalPart());
    }

    public static Node createElementNodeWithPreconfiguredChildren(Document doc, QName element) {
        SchemaType t = Configuration.getInstance().getXsd().getType(element);//.getElementType(element);
        return createElementNodeWithPreconfiguredChildren(doc, element, t);
    }

    /***
     * Use schema introspection to find out how an element is defined, then
     * create a node with sub nodes according to that definition
     *
     * @param elemProp
     *            the element definition to create a dom node for
     * @return
     */
    public static Node createElementNodeWithPreconfiguredChildren(Document doc, SchemaProperty elemProp) {
        return createElementNodeWithPreconfiguredChildren(doc, elemProp.getName(), elemProp.getType());
    }

    public static Node createElementNodeWithPreconfiguredChildren(Document doc, QName qName, SchemaType elemType) {
        System.out.println("Creating node " + qName);
        Element elem = doc.createElementNS(qName.getNamespaceURI(), qName.getLocalPart());

        // depending on whether it's a simple type or a complex one, we take
        // different actions
        if (elemType.isSimpleType()) {
            // check restrictions
            // throws: System.out.println("is posit: " +
            // elemType.getFacet(SchemaType.BTC_POSITIVE_INTEGER).getStringValue());

            // Since this element is a simple type (i.e. it's based on a simple,
            // primitve type, we must set its text value (by adding a text node)
            // TODO: add some kind of default value according to its primitive java type
            elem.appendChild(doc.createTextNode(""));

            // do not proceed with inspecting this element's children.
            // it's a simple type, there are no children.
            return elem;
        } else if (elemType.isAbstract()) {
            Node n = doc.createTextNode("I AM ABSTRACT - REPLACE ME");
            elem.appendChild(n);
            // TODO: list replacement types in context menu
            return elem;
        }

        // Handle *element* children (attributes are further below)
        // Repeat this process recursively for every child
        List<SchemaProperty> children = Configuration.getInstance().getXsd().getChildElements(qName);
        for (SchemaProperty childElem : children) {
            // Make sure infinite recursion doesn't happen.
            // Stop if a child has exactly the same QName as its parent.
            // Example:
            // <xs:complexType name="SomeNeverEndingType">
            //     <xs:sequence>
            //         <xs:element name="childElement" type="tns:SomeNeverEndingType"/>
            //     </xs:sequence>
            // </xs:complexType>
            if (DomHelper.qNamesAreEqual(childElem.getName(), qName))
                continue;

            for (int i = 0; i < childElem.getMinOccurs().intValue(); i++) {
                Node newChild = createElementNodeWithPreconfiguredChildren(doc, childElem);
                elem.appendChild(newChild);
            }

        }

        // Handle attribute children
        List<SchemaProperty> attrList = Configuration.getInstance().getXsd().getChildAttributes(qName);
        attrList.forEach(attr -> {
            SchemaAttributeModel aModel = elemType.getAttributeModel();
            SchemaLocalAttribute sAttr = aModel.getAttribute(attr.getName());
            String strDefVal = attr.getDefaultText();
            // We need to have a valid default value.
            // Since the JaxB binder throws when it doesn't get a valid value for a primitive type,
            // we need to create valid default values.
            // It cannot be null, and for non-string types, it cannot be an empty string either.
            if (null == strDefVal)
                strDefVal = getDefaultValueFor(attr.getType());

            // Add required attributes
            if (SchemaLocalAttribute.REQUIRED == sAttr.getUse()) {
                Attr attrNode = doc.createAttributeNS(attr.getName().getNamespaceURI(), attr.getName().getLocalPart());
                attrNode.setValue(strDefVal);
                elem.setAttributeNodeNS(attrNode);
            } else if (SchemaLocalAttribute.OPTIONAL == sAttr.getUse()) {
                Attr attrNode = doc.createAttributeNS(attr.getName().getNamespaceURI(), attr.getName().getLocalPart());
                attrNode.setValue(strDefVal);
                elem.setAttributeNode(attrNode);
            }
        });

        return elem;
    }

    private static String getDefaultValueFor(SchemaType type) {
        // Cannot use SchemaProperty.getType().getPrimitiveType().getBuiltinTypeCode(), because it always returns 10 (String), no matter the actual type (e.g. double)
        switch (type.getPrimitiveType().getShortJavaName().toUpperCase()) {
            case "XMLDOUBLE":
            case "XMLDECIMAL":
                return "0.0";
            case "xmlinteger":
            case "xmlint":
            case "xmllong":
            case "xmlshort":
            case "xmlbyte":
                return "0";
            case "XMLBOOLEAN":
                return "false";
            default:
                return "";
        }
    }
}
