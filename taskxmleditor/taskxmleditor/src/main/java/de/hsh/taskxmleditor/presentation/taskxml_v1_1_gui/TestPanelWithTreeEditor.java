//package de.hsh.taskxmleditor.presentation.taskxml_v1_1_gui;
//
//import de.hsh.taskxmleditor.application.Configuration;
//import de.hsh.taskxmleditor.generic_editor.GenericTreeTableXmlEditor;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.w3c.dom.Document;
//import org.w3c.dom.Element;
//
//import javax.swing.*;
//import java.awt.*;
//
//public class TestPanelWithTreeEditor extends JPanel implements LoadSavePanel {
//    public final static Logger log = LoggerFactory.getLogger(TestPanelWithTreeEditor.class);
//    private static final long serialVersionUID = 1L;
//
//    public TestPanelWithTreeEditor() {
//
//        Document doc = Configuration.getInstance().getXmlDocument();
//        String namespace = "urn:proforma:task:v1.1";// CurrentSession.getInstance().getTempXsd().getTargetNamespace();
//        Element root = (Element) doc.getElementsByTagNameNS(namespace, "tests").item(0);
//        GenericTreeTableXmlEditor editor = new GenericTreeTableXmlEditor(doc, root, false);
//
//        setLayout(new BorderLayout());
//        setBackground(Color.green);
//        // setLayout(new FlowLayout());
//        // setBorder(BorderFactory.createTitledBorder(Lang.get("GUI.GroupSubmissionRestrictions")));
//        // add(p, BorderLayout.CENTER);
//        add(editor, BorderLayout.CENTER);
//    }
//
//    // private void addFileRefsListView() {
//    // JList<String> fileRefsView = new JList<>(fileRefsModel);
//    // Dimension d = fileRefsView.getPreferredSize();
//    // d.height = 50;
//    // d.width = 100;
//    // fileRefsView.setPreferredSize(d);
//    // JScrollPane scroll = new JScrollPane(fileRefsView);
//    // add(scroll);
//    // }
//
//    @Override
//    public void loadModel() {
//        log.info("Loading model data");
//        // Filerefs fileRefs = model.getFilerefs();
//        // if (null != fileRefs)
//        // fileRefs.getFileref().forEach(ref ->
//        // fileRefsModel.addElement(ref.getRefid()));
//
//    }
//
//    @Override
//    public void saveModel() {
//        log.info("Saving model data");
//
//    }
//}
