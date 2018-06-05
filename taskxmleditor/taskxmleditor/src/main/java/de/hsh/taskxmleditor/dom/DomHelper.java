package de.hsh.taskxmleditor.dom;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.*;

import javax.xml.namespace.QName;

public class DomHelper {
    final static Logger log = LoggerFactory.getLogger(DomHelper.class);
    private String namespace;
    private Element root;

    public DomHelper(Element root, String namespace) {
        this.namespace = namespace;
        this.root = root;
    }

    /**
     * There are several different implementations of javax.xml.namespace.QName,
     * like xerces and xmlbeans.
     * <p>
     * Depending on their QName.equals() implementation, equals() might just fail
     * if the incoming QName is of a different implementation type than the QName
     * that equals() is being called on. They might compare references instead of
     * the namespace uri and local part.
     *
     * (This was actually the case, so I had to resort to this method)
     * <p>
     * To avoid such a case, this method checks for namespace uri and local part
     * instead, both of which are strings.
     *
     * @param lhs left hand side qname
     * @param rhs right hand side qname
     * @return true if both qnames are equal, otherwise false
     */
    public static boolean qNamesAreEqual(QName lhs, QName rhs) { // TODO: use this for equality checks
        if(null == lhs || null == rhs)
            return false;
        return lhs.getNamespaceURI().equals(rhs.getNamespaceURI())
                && lhs.getLocalPart().equals(rhs.getLocalPart());
    }

    public String getNamespace() {
        return namespace;
    }

    public Element getRoot() {
        return root;
    }

    public static Element getOwnerElement(Node n) {
        if (n instanceof Element)
            return (Element) n;
        if (n instanceof Attr)
            return ((Attr) n).getOwnerElement();
        throw new IllegalStateException();
    }

    public static void removeChildren(Node n) {
        Node child = n.getFirstChild();
        while (child != null)
            n.removeChild(child);
    }

    // TODO: move to XmlTreemodel
    public static String findNodesNamespace(Node n) {
        if (n.getNamespaceURI() != null)
            return n.getNamespaceURI();
        if (n instanceof Attr) {
            // since attribute nodes have no parents according to the docs,
            // they need special treatment
            return findNodesNamespace(((Attr) n).getOwnerElement());
        }
        return findNodesNamespace(n);
    }

    public Element getElement(String name) {
        NodeList list = root.getElementsByTagNameNS(namespace, name);
        if (list.getLength() == 1)
            return (Element) list.item(0);
        else if (list.getLength() > 1)
            log.warn("getElement({}) returned {} items", name, list.getLength());
        return null;
    }

    public Attr getAttr(String ownerElementName, String attrName) {
        Element owner = getElement(ownerElementName);
        return owner.getAttributeNode(attrName);
    }

    public static QName getQName(Node node) {
        return new QName(node.getNamespaceURI(), node.getLocalName());
    }

    public static int getElementChildCountExcludingAnyTextNode(Element parent) {
        int count = 0;
        NodeList list = parent.getChildNodes();
        for (int i = 0; i < list.getLength(); i++) {
            Node item = list.item(i);
            if(item instanceof Element)
                count++;
        }
        return count;
    }
}
