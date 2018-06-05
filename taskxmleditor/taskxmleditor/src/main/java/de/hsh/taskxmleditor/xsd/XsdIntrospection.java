package de.hsh.taskxmleditor.xsd;

import de.hsh.taskxmleditor.application.Configuration;
import de.hsh.taskxmleditor.dom.DomHelper;
import org.apache.xmlbeans.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Attr;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.namespace.QName;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

/***
 * Used to inspect xml type defenitions using xsd schemas.
 *
 */
public class XsdIntrospection {
  final static Logger log = LoggerFactory.getLogger(XsdIntrospection.class);
  public SchemaTypeSystem sts;
  private SchemaTypeLoader stl;

  private HashMap<QName, SchemaGlobalElement> globalElements = new HashMap<>();
  private HashMap<QName, SchemaType> globalTypes = new HashMap<>();
  private HashMap<QName, SchemaProperty> attributes = new HashMap<>();
  private HashMap<QName, SchemaProperty> childElements = new HashMap<>();
  private HashMap<QName, Boolean> nillableElements = new HashMap<>();

  private HashMap<QName, BigInteger> minOccurs = new HashMap<>();
  private HashMap<QName, BigInteger> maxOccurs = new HashMap<>();
  private HashMap<QName, Integer> positionInAllChoiceSequence = new HashMap<>();

  // key=element, value = the element's container
  private HashMap<QName, SchemaParticle> parentParticles = new HashMap<>();

  public XsdIntrospection(XmlObject[] schemas) throws Exception {
    sts = XmlBeans.compileXsd(
            schemas, XmlBeans.getBuiltinTypeSystem(), null);
    stl = XmlBeans.typeLoaderUnion(new SchemaTypeLoader[]{sts, XmlBeans.getBuiltinTypeSystem()});

    mapAllRelevantInfo();
  }

  /**
   * an element's position in a sequence matters.
   * this method returns the element's position in any kind of
   * "container", starting with 0 ofc.
   *
   * @param qName
   * @return
   */
  public Integer getPositionInAllChoiceSequence(QName qName) {
    return positionInAllChoiceSequence.get(qName);
  }

  public HashMap<QName, SchemaGlobalElement> getGlobalElements() {
    return globalElements;
  }

  public BigInteger getMinOccurs(QName qName) {
    return minOccurs.get(qName);
  }

  /**
   * If maxOccurs is 'unbounded', the return value is null.
   * @param qName
   * @return
   */
  public BigInteger getMaxOccurs(QName qName) {
    return maxOccurs.get(qName);
  }

  public static String extractTargetNamespace(File f) throws Exception {
    try (InputStream is = new FileInputStream(f)) {
      XmlObject obj = XmlObject.Factory.parse(is);
      return extractTargetNamespace(obj);
    }
  }


  public static String extractTargetNamespace(XmlObject schema) throws Exception {
    // TODO: make it work with any namespace prefix, not just xs
    final String nsDecl = "declare namespace xs='http://www.w3.org/2001/XMLSchema';";
    XmlObject[] targetNs = schema.selectPath(nsDecl + " xs:schema/@targetNamespace");
    if (targetNs.length == 1) {
      Attr attr = (Attr) targetNs[0].getDomNode();
      return attr.getValue();
    }
    throw new Exception("Could not retrieve the targetNamespace value from schema file.");
  }

  public Boolean isElementNillable(QName element) {
    for (Map.Entry<QName, Boolean> e : nillableElements.entrySet()) {
      if (DomHelper.qNamesAreEqual(e.getKey(), element))
        return e.getValue();
    }
    return null;
  }

//  public SchemaType getType(QName qName) {
//    return stl.findType(qName);
//  }

  public SchemaType getType(QName qName) {
    boolean xsdFileExistsForThisQName = Configuration.getInstance()
            .getTargetNsToFileMapping()
            .keySet().stream()
            .anyMatch(ns -> ns.equals(qName.getNamespaceURI()));
    if (!xsdFileExistsForThisQName) {
      // don't log this, or the log file will be spammed with this message,
      // because this method gets called constantly when using the GenericEditor control.
      return null;
    }

    for (Map.Entry<QName, SchemaGlobalElement> e : globalElements.entrySet()) {
      if (DomHelper.qNamesAreEqual(e.getKey(), qName))
        return e.getValue().getType();
    }

    for (Map.Entry<QName, SchemaType> e : globalTypes.entrySet()) {
      if (DomHelper.qNamesAreEqual(e.getKey(), qName))
        return e.getValue();
    }

    for (Map.Entry<QName, SchemaProperty> e : childElements.entrySet()) {
      if (DomHelper.qNamesAreEqual(e.getKey(), qName))
        return e.getValue().getType();
    }

    // TODO: attributes have no namespace uri, consult the parent element's namespace instead
//        for (Map.Entry<QName, SchemaProperty> e : attributes.entrySet()) {
//            if (DomHelper.qNamesAreEqual(e.getKey(), qName))
//                return e.getValue().getType();
//        }

    log.error("Could not find SchemaType for QName: " + qName);

    return null;
  }

  public void printAvailabelAnnotations() {
    globalTypes.entrySet().forEach(gt -> System.out.println("Global Type: " + gt));

    for (Map.Entry<QName, String> entry : elementAnnotations.entrySet()) {
      if (null != entry.getValue())
        System.out.println("Annot for name: " + entry.getKey().getLocalPart() + " is : " + entry.getValue());
    }
  }

  private HashMap<QName, String> elementAnnotations = new HashMap<>();

  /**
   * @param qName
   * @return a valid string annotation (documentation), or null if none is available
   */
  public String getElementAnnotation(QName qName) {
    if (null == qName)
      return null;

    for (Map.Entry<QName, String> entry : elementAnnotations.entrySet()) {
      if (DomHelper.qNamesAreEqual(entry.getKey(), qName))
        return entry.getValue();
    }
    return null;
  }

  /**
   * Returns the container info for an element that is nested within that container.
   * That is, if you have an element that is contained by an all/choice/sequence type
   * of particle and you need to access the that particle's min or maxOccurs, get
   * it from this map).
   * @param element
   * @return
   */
  public SchemaParticle getContainerInfoForElement(QName element) {
    for (Map.Entry<QName, SchemaParticle> info : parentParticles.entrySet()) {
      if (DomHelper.qNamesAreEqual(info.getKey(), element))
        return info.getValue();
    }
    return null;
  }

  /**
   * @param parentElement
   * @param attributeLocalName
   * @return a valid string annotation (documentation), or null if none is available
   */
  public String getAttributeAnnotation(QName parentElement, String attributeLocalName) {
    SchemaType t = getType(parentElement);

    if (null != t) {
      SchemaAttributeModel attrModel = t.getAttributeModel();
      if (null != attrModel) {
        SchemaLocalAttribute[] attributes = t.getAttributeModel().getAttributes();
        if (attributes.length > 0) {
          SchemaLocalAttribute attr = Arrays.stream(attributes)
                  .filter(a -> a.getName().getLocalPart().equals(attributeLocalName))
                  .findFirst().orElse(null);
          if (null != attr) {
            String annotationDoc = XsdIntrospection.getAnnotationDocumentation(attr.getAnnotation());
            return annotationDoc;
          }
        }
      }
    }

    return null;
  }

  public static String getAnnotationDocumentation(SchemaAnnotation an) {
    if (null != an) {
      StringBuilder sb = new StringBuilder();
      XmlObject[] userInformation = an.getUserInformation();
      if (null != userInformation & userInformation.length > 0) {
        for (XmlObject obj : userInformation) {
          Node docInfo = obj.getDomNode();
          NodeList list = docInfo.getChildNodes();
          for (int i = 0; i < list.getLength(); i++) {
            Node c = list.item(i);
            if (c.getNodeType() == Node.TEXT_NODE) {
              String str = c.getNodeValue();
              sb.append(str.trim());
//                            sb.append(System.getProperty("line.separator"));
//                            sb.append(System.getProperty("line.separator"));
              break;
            }
          }
        }
      }
      return sb.toString();
    }
    return null;
  }

//    public String getAnnotationDocumentation(SchemaType info) {
//        SchemaAnnotation an = info.getAnnotation();
//        if (null != an) {
//            StringBuilder sb = new StringBuilder();
//            XmlObject[] userInformation = an.getUserInformation();
//            if (null != userInformation & userInformation.length > 0) {
//                for (XmlObject obj : userInformation) {
//                    Node docInfo = obj.getDomNode();
//                    NodeList list = docInfo.getChildNodes();
//                    for (int i = 0; i < list.getLength(); i++) {
//                        Node c = list.item(i);
//                        if (c.getNodeType() == Node.TEXT_NODE) {
//                            String str = c.getNodeValue();
//                            sb.append(str.trim());
//                            sb.append(System.getProperty("line.separator"));
//                            sb.append(System.getProperty("line.separator"));
//                            break;
//                        }
//                    }
//                }
//            }
//            return sb.toString();
//        }
//        return null;
//    }

  private void mapAllRelevantInfo() {
//        for (SchemaAttributeGroup attributeGroup : sts.attributeGroups()) {
//        }

//        for (SchemaGlobalAttribute globAttr : sts.globalAttributes()) {
//        }

    for (SchemaType st : sts.globalTypes()) {
      globalTypes.put(st.getName(), st);
      inspectElement(st);
    }

    for (SchemaGlobalElement globElem : sts.globalElements()) {
      globalElements.put(globElem.getName(), globElem);
      elementAnnotations.put(globElem.getName(), getAnnotationDocumentation(globElem.getAnnotation()));

      // Map sub/child elements
      for (SchemaProperty prop : globElem.getType().getElementProperties()) {
        mapSubElement(prop);
      }

      // Map attributes
      for (SchemaProperty prop : globElem.getType().getAttributeProperties()) {
        //System.out.println("Mapping Attribute: " + prop.getName());
        QName qn = prop.getName();
        attributes.put(qn, prop);
        // annotation mapping works differently here.
        // Refer to getAttributeAnnotation()
      }

      inspectElement(globElem.getType());
    }
  }

  private void mapSubElement(SchemaProperty elem) {

    childElements.put(elem.getName(), elem);
    for (SchemaProperty prop : elem.getType().getElementProperties()) {
      //System.out.println("mapping SchemaProperty: " + prop.getName());
      if (!childElements.containsKey(prop.getName())) {
        childElements.put(prop.getName(), prop);
        mapSubElement(prop);
      }
    }
  }

  /**
   * Inspects and maps all kinds of relevant element information
   * (like min/maxOccurs, annotations etc) to hash maps for fast retrieval.
   * Attribute annotation mapping works differently.
   * Refer to getAttributeAnnotation()
   *
   * @param t
   * @param alreadyMappedTempList for avoiding already processed, recursive elements
   */
  private void inspectElement(SchemaType t) {
    if (null == t)
      return;

    if(inspected.contains(t))
      return;
    inspected.add(t);

    if (t.getContentType() == SchemaType.ELEMENT_CONTENT ||
            t.getContentType() == SchemaType.MIXED_CONTENT) {
      SchemaParticle p = t.getContentModel();
      navigateParticle(t, p);
    }
  }

  private ArrayList<SchemaType> inspected = new ArrayList<>();

  private void navigateParticle(SchemaType t, SchemaParticle p) {
    if(null == p)
      return;

    switch (p.getParticleType()) {
      case SchemaParticle.ALL:
      case SchemaParticle.CHOICE:
      case SchemaParticle.SEQUENCE:
        SchemaParticle[] children = p.getParticleChildren();
        for (int i = 0; i < children.length; i++) {
          SchemaParticle c = children[i];
          parentParticles.put(c.getName(), p);
//          System.out.println(c.getName() + " has pos " + i);
          positionInAllChoiceSequence.put(c.getName(), i);
          navigateParticle(c.getType(), c);
        }
        break;
      case SchemaParticle.ELEMENT:
        SchemaLocalElement localElement = (SchemaLocalElement) p;
        QName qName = p.getName();
        nillableElements.put(qName, p.isNillable());
        minOccurs.put(qName, p.getMinOccurs());
        // If maxOccurs is null, it's 'unbounded'
        maxOccurs.put(qName, p.getMaxOccurs());
        // SchemaLocalElement has all kinds of info!
        SchemaAnnotation annotation = localElement.getAnnotation();
        String annotStr = getAnnotationDocumentation(annotation);
        // inspect only if it's a locally defined type (i.e. it's not a global type,
        // since they get taken care of already
        if (null != annotStr) {
          log.debug("Found Annotation for element '{}': {}", p.getName(), annotStr);
          String html = String.format("<html><body>%s</body></html>", annotStr);
          elementAnnotations.put(qName, annotStr);
        }

        inspectElement(p.getType());
        break;
    }
  }

  /***
   * Use SchemaProperty.getType() for type information on an element
   * defenition.
   *
   * @param parentName
   * @return a list of SchemaProperty, which contains the name, ...
   */
  public List<SchemaProperty> getChildElements(QName parentName) {
    System.out.println("Getting children for parent element: " + parentName.getLocalPart());
    SchemaType elementType = getType(parentName);//getElementType(parentName);
    if (null != elementType) {
      if (null != elementType.getName())
        System.out.println("\t" + parentName.getLocalPart() + " is of type: " + elementType.getName().getLocalPart());
      return getChildElements(elementType);
    }
    throw new NullPointerException("Element could not be found: " + parentName);
  }

  private List<SchemaProperty> getChildElements(SchemaType elementType) {
    return Arrays.stream(elementType.getElementProperties()).collect(Collectors.toList());
  }

  public List<SchemaProperty> getChildAttributes(QName elementTypeName) {
    SchemaType elementType = getType(elementTypeName);
    return Arrays.stream(elementType.getAttributeProperties()).collect(Collectors.toList());
  }

  /***
   * Access the SchemaProperty's type definition via getType(). Refer to the
   * documentation for SchemaType:
   * https://xmlbeans.apache.org/docs/2.4.0/reference/org/apache/xmlbeans/SchemaType.html
   *
   * @param parentElement
   * @param attrName
   * @return
   */
  public SchemaProperty getAttributeInfo(QName parentElement, String attrName) {

    // strange behavior: all qnames of an attribute are mapped with a proper
    // namespace uri, but the incoming QName for finding an attribute has its
    // namespace uri set to null (e.g. the CreateChildNodesContextMenu.canRemoveAttribute() call)
    SchemaType parent = getType(parentElement);
    // So let's get the parentElement

    if (null != parent) {
      for (SchemaProperty sp : parent.getAttributeProperties()) {
        if (sp.getName().getLocalPart().equals(attrName))
          return sp;
      }
    }
    return null;
  }

  public static void nodeToXml(Node n, OutputStream out) throws IOException, TransformerException {
    TransformerFactory tf = TransformerFactory.newInstance();
    Transformer transformer = tf.newTransformer();
    transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
    transformer.setOutputProperty(OutputKeys.METHOD, "xml");
    transformer.setOutputProperty(OutputKeys.INDENT, "yes");
    transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
    transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
    transformer.transform(new DOMSource(n), new StreamResult(new OutputStreamWriter(out, "UTF-8")));
  }

  /***
   *
   * @param attr
   * @return the attribute's default value, or null if there is no default
   *         value
   */
  public static String getAttributeDefaultValue(SchemaProperty attr) {
    if (SchemaProperty.CONSISTENTLY == attr.hasDefault())
      attr.getDefaultText();
    return null;
  }
}
