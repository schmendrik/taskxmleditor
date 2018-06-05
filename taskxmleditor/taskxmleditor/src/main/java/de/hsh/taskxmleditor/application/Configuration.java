package de.hsh.taskxmleditor.application;

import de.hsh.taskxmleditor.plugin.PluginEditor;
import de.hsh.taskxmleditor.plugin.PluginManager;
import de.hsh.taskxmleditor.taskxml_v1_1.Task;
import de.hsh.taskxmleditor.utils.FileOperations;
import de.hsh.taskxmleditor.xsd.XsdIntrospection;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.tika.utils.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.util.JAXBSource;
import javax.xml.namespace.QName;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.*;
import java.net.URL;
import java.nio.file.Paths;
import java.util.*;


public final class Configuration extends Observable {
  final static Logger log = LoggerFactory.getLogger(Configuration.class);
  public static final String TASK_XML_ENCODING = "UTF-8";
  private static Configuration c;
  private File taskXml;
  private Document xmlDocument;
  private Task task;
  private URL taskXsdUrl;
  private File taskXsdFile;

  private static Properties props = null;

  public static File SCHEMA_DIRECTORY;
  public static File PLUGIN_DIRECTORY;
  public static File WORKING_DIRECTORY;
  public static File CONFIG_FILE_PATH;

  private HashMap<String, File> targetNsToFileMapping = new HashMap<>();

  // this one contains info on all xsd files
  private XsdIntrospection xsd; // TODO: replace with ApplicationWindow.xsd


  /**
   * This is also the ProFormA version that this editor supports.
   */
  public static final String TASK_XSD_TARGET_NAMESPACE = "urn:proforma:task:v1.1";

  public HashMap<String, File> getTargetNsToFileMapping() {
    return targetNsToFileMapping;
  }

  public File getTaskXsdFile() {
    return taskXsdFile;
  }

  /**
   * @return a list of xsd extensions
   */
  public XsdIntrospection getXsd() {
    return xsd;
  }

  public void setXsd(XsdIntrospection xsd) {
    this.xsd = xsd;
  }

  public Configuration() {
    //            // Create temp file to use from resource
//            // Many libs require a File object, so converting the resource
//            // to a File object requires saving the resource on the hard drive...
//            String resource = "/taskxml_v1_1.xsd";
//            try (InputStream input = Gui.class.getResourceAsStream(resource)) {
//                taskXsdFile = FileOperations.createTempFileFromResource(input, ".xml");
//            }
//            if (null == taskXsdFile || !taskXsdFile.exists()) {
//                throw new RuntimeException("File " + resource + " not found!");
//            }
//            try {
//                taskXsdTargetNamespace = XsdIntrospection.extractTargetNamespace(taskXsdFile);
//            } catch (Exception e) {
//                log.error(ExceptionUtils.getStackTrace(e));
//            }
  }

  public void init() throws Exception {
    URL url = TaskxmlEditor.class.getProtectionDomain().getCodeSource().getLocation();

    WORKING_DIRECTORY = new File(url.toURI());
    if (WORKING_DIRECTORY.isFile()) // running a .jar
      WORKING_DIRECTORY = WORKING_DIRECTORY.getParentFile();

    SCHEMA_DIRECTORY = Paths.get(WORKING_DIRECTORY.getAbsolutePath(), "schema").toFile();
    PLUGIN_DIRECTORY = Paths.get(WORKING_DIRECTORY.getAbsolutePath(), "plugin").toFile();
    CONFIG_FILE_PATH = Paths.get(WORKING_DIRECTORY.getAbsolutePath(), "config.properties").toFile();

    SCHEMA_DIRECTORY.mkdir();
    PLUGIN_DIRECTORY.mkdir();

    if (!CONFIG_FILE_PATH.exists()) {
      log.info("Cannot find config file, creating one...");
      FileOperations.createFileFromResource("/default_config.properties", CONFIG_FILE_PATH);
    }

    log.info("Working directory: " + WORKING_DIRECTORY);
    log.info("NamespacePlugin directory: " + PLUGIN_DIRECTORY);
    log.info("Schema directory: " + SCHEMA_DIRECTORY);
    log.info("Config file path: " + CONFIG_FILE_PATH);
  }

  public void setTaskXsdFile(File taskXsdFile) {
    this.taskXsdFile = taskXsdFile;
  }

  public File getTaskXmlFile() {
    return taskXml;
  }

  public static Configuration getInstance() {
    if (null == c) {
      c = new Configuration();
    }
    return c;
  }

  public String getTaskXmlPath() {
    return taskXml.getAbsolutePath();
  }

  public Task getTask() {
    return task;
  }

  public void loadTaskXml(File file) throws Exception {
    this.taskXml = file;

    JAXBContext jaxbContext = JAXBContext.newInstance(Task.class);

    Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

//        if (validate) {
//            SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
//            Schema schema = sf.newSchema(taskXsdUrl);
//            jaxbUnmarshaller.setSchema(schema);
//        }

    try (InputStream inputStream = new FileInputStream(file)) {
      Reader reader = new InputStreamReader(inputStream, TASK_XML_ENCODING);
      task = (Task) jaxbUnmarshaller.unmarshal(reader);
    } catch (Exception e) {
      log.error(ExceptionUtils.getStackTrace(e));
      throw e;
    }

    setChanged();
    notifyObservers();
  }

  /**
   * All the xsd files that are actually used in the task, excluding
   * the proforma one.
   * <b>Note that the value (file) parameter can be null if the xsd file is not available.</b>
   *
   * @return a map with the key being the namespace uri and the value the xsd file. note that while that the file parameter can be null if the xsd file was not available when the app loaded
   */
  public HashMap<String, File> getAllExtensionXsdFilesThatTheTaskActuallyUses() {
    // gather a unique list of targetNamespaces that are actually used in this task
    HashSet<String> usedTargetNs = new HashSet<>();
    for (Pair<QName, PluginEditor> p : PluginManager.getCriculatingEditorList()) {
      usedTargetNs.add(p.getLeft().getNamespaceURI());
    }

    HashMap<String, File> inUse = new HashMap<>();

    for (String tarNs : usedTargetNs)
      inUse.put(tarNs, targetNsToFileMapping.get(tarNs));

    return inUse;
  }

  /**
   * Save a task object to a file.
   * This method doesn't validate, nor should it, since we need to
   * be able to save 'blank' task with not yet filled out elements and attributes
   * for creating new task files.
   *
   * @param taskObj
   * @param destinationFile
   * @throws Exception
   */
  public void saveTaskXml(Task taskObj, OutputStream os) throws Exception {
    if (null == taskObj)
      throw new IllegalArgumentException("Task object is null");

    PluginManager.saveAllEditorChanges();

    // construct the values for xsi:schemaLocation
    HashMap<String, File> inUse = getAllExtensionXsdFilesThatTheTaskActuallyUses();
    StringBuilder sb = new StringBuilder(TASK_XSD_TARGET_NAMESPACE + " " + taskXsdFile.getName());

    for (Map.Entry<String, File> e : inUse.entrySet()) {
      if (null != e.getValue())
        sb.append(String.format(" %s %s", e.getKey(), e.getValue().getName()));
      else
        log.debug("Cannot put xsd file for namespace uri '{}' into the schemaLocation attribute, since the file is not available", e.getKey());
    }

    JAXBContext context = JAXBContext.newInstance(Task.class);
    Marshaller m = context.createMarshaller();
    m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
    m.setProperty(javax.xml.bind.Marshaller.JAXB_ENCODING, TASK_XML_ENCODING);
//        m.setProperty("com.sun.xml.bind.namespacePrefixMapper", new AllNamespacesGoToTheRoot());
    m.setProperty(Marshaller.JAXB_SCHEMA_LOCATION, sb.toString());


    m.marshal(taskObj, os);

//    try (FileOutputStream fos = new FileOutputStream(destinationFile)) {
//      m.marshal(taskObj, fos);
//    } catch (Exception e) {
//      log.error(ExceptionUtils.getStackTrace(e));
//      throw e;
//    }
  }

  public Document getXmlDocument() {
    return xmlDocument;
  }

  public String getTaskXmlFileEncoding() throws Exception {
    return FileOperations.detectEncoding(taskXml);
  }

  public void validate() throws Exception {
    // save all changes from plugin editors to the task object so we can validate it
    // with all the changes the user has made
    PluginManager.saveAllEditorChanges();

//    // another way to validate: http://stackoverflow.com/questions/23288853/jaxb-how-to-validate-xml-against-more-schemas
//    DOMParser parser = new DOMParser();
//    parser.setFeature("http://xml.org/sax/features/validation", true);
//    try(ByteArrayOutputStream os = new ByteArrayOutputStream()) {
//      saveTaskXml(task, os);
//      String xml = new String(os.toByteArray(),TASK_XML_ENCODING);
//      InputSource inputSource = new InputSource( new StringReader(xml));
//      parser.parse(inputSource);
//    }

    Schema schema = createCombinedSchema();
    if (null == schema)
      throw new CombiningSchemaException("Combining the available schemas for validation went wrong. See the log file for more details.");
    JAXBContext jc = JAXBContext.newInstance(Task.class);
    JAXBSource source = new JAXBSource(jc, task);
    Validator validator = schema.newValidator();
    validator.validate(source);
  }

  public static class CombiningSchemaException extends Exception {
    public CombiningSchemaException(String str) {
      super(str);
    }
  }


  /**
   * combines all available schemas into a single schema so we don't have to deal with
   * SchemaFactory.newSchema(Source[]) and the correct (to us unknown) order
   * of multiple schema files that depend on one another
   * the idea comes from this guy: http://stackoverflow.com/a/23289220/4765331
   *
   * @param xsdUrls all xsd files except the proforma one
   * @return
   * @throws Exception
   */
  private Schema createCombinedSchema() throws Exception {
    log.debug("Combining multiple schema files into one big chunk of xsd");
    //Arrays.stream(xsdUrls).forEach(url -> log.debug("Combining schema file: " + url.getPath()));

    SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

    StringBuilder sb = new StringBuilder();
    sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n");
    sb.append("<xs:schema xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" targetNamespace=\"urn:proforma:task:v1.1\" elementFormDefault=\"qualified\">\n");

    sb.append("<xs:include schemaLocation=\"" + taskXsdFile.getAbsolutePath() + "\"/>\n");

    // we import because we known there aren't going to be any other schema files that use the same
    // targetNamespace as task.xml. I think.
    for (Map.Entry<String, File> e : getAllExtensionXsdFilesThatTheTaskActuallyUses().entrySet()) {
      if (null != e.getValue()) {
        sb.append(String.format("<xs:import namespace=\"%s\" schemaLocation=\"%s\"/>\n",
                e.getKey(), e.getValue().getAbsolutePath()));
      } else
        log.warn("Validation: Don't have a schema file for namespace URI '{}'", e.getKey());
    }
    sb.append("</xs:schema>");

    String W3C_XSD_TOP_ELEMENT = sb.toString();
    log.debug("Combined schema looks like this:\n{}", W3C_XSD_TOP_ELEMENT);

    Schema schema = null;
    try {
      StreamSource xsdTop = new StreamSource(new StringReader(W3C_XSD_TOP_ELEMENT), "xsdTop");//taskXsd.getPath());
      schema = schemaFactory.newSchema(xsdTop);
    } catch (org.xml.sax.SAXException e) {
      log.error(org.apache.commons.lang3.exception.ExceptionUtils.getStackTrace(e));
    }
    return schema;
  }

  public static String getPropertiesKey(String key, String defaultValue) {
    if (null == props) {
      props = new Properties();
      try (InputStream is = new FileInputStream(CONFIG_FILE_PATH)) {
        props.load(is);
      } catch (Exception e) {
        log.error(org.apache.commons.lang3.exception.ExceptionUtils.getStackTrace(e));
      }
    }
    String val = props.getProperty(key);
    if (null != val)
      return val;
    log.info("Key {} not found in config file {}, returning default value {}",
            key, CONFIG_FILE_PATH, defaultValue);
    return defaultValue;
  }
}
