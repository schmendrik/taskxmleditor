package de.hsh.taskxmleditor.plugin;

import de.hsh.taskxmleditor.utils.FileOperations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.xml.XMLConstants;
import javax.xml.bind.Binder;
import javax.xml.bind.JAXB;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.File;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;

/**
 * Convinience class for marshalling and unmarshalling "any" task xml elements
 */
public class PluginHelper {
    final static Logger log = LoggerFactory.getLogger(PluginHelper.class);

    public static <T> T unmarshal(Class<T> clazz, Node inputNode) throws Exception {
        JAXBContext jc = JAXBContext.newInstance(clazz);
        Binder<Node> binder = jc.createBinder();
        JAXBElement<T> jst = binder.unmarshal(inputNode, clazz);
        return jst.getValue();
    }

    public static <T> Node marshal(Class<T> clazz, QName qName, T inputObj) throws Exception {
//        JAXBElement<T> jaxbElement = new JAXBElement<T>(qName, clazz, inputObj);
//        Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
//        //JAXB.marshal(jaxbElement, new DOMResult(document));
//        JAXBContext context = JAXBContext.newInstance(clazz);
//        javax.xml.bind.PluginHelper m = context.createMarshaller();
//        m.setProperty(javax.xml.bind.PluginHelper.JAXB_FORMATTED_OUTPUT, true);
//        m.setProperty(javax.xml.bind.PluginHelper.JAXB_ENCODING, TASK_XML_ENCODING);
//        m.setProperty("com.sun.xml.bind.namespacePrefixMapper", new AllNamespacesGoToTheRoot());
//        m.marshal(jaxbElement, new DOMResult(document));
//        Element element = document.getDocumentElement();
//        return element;

        JAXBElement<T> jaxbElement = new JAXBElement<T>(qName, clazz, inputObj);
        Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        JAXB.marshal(jaxbElement, new DOMResult(document));
        Element element = document.getDocumentElement();
        return element;
    }

    public static void validateNode(Node node, InputStream xsdInputSource) throws Exception {
        validateNode(node, FileOperations.createTempFileFromResource(xsdInputSource, ".xsd"));
    }

    public static void validateNode(Node node, File xsdFile) throws Exception {
        log.debug("Validating node '{}' against schema file '{}'", node.getLocalName(), xsdFile.getName());
        SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Schema schema = null;
        try {
            schema = schemaFactory.newSchema(xsdFile);
        } catch (org.xml.sax.SAXException e) {
            log.error(org.apache.commons.lang3.exception.ExceptionUtils.getStackTrace(e));
            throw e;
        }

        final Validator validator = schema.newValidator();

        Source source = new DOMSource(node);
        StringWriter stringWriter = new StringWriter();
        Result result = new StreamResult(stringWriter);
        TransformerFactory factory = TransformerFactory.newInstance();
        Transformer transformer = factory.newTransformer();
        transformer.transform(source, result);
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        String xml =  stringWriter.getBuffer().toString();

        try(StringReader r = new StringReader(xml)) {
            validator.validate(new StreamSource(r));
        }
    }
}
