package de.hsh.taskxmleditor.presentation;

import de.hsh.taskxmleditor.application.Configuration;
import de.hsh.taskxmleditor.taskxml_v1_1.Task;
import de.hsh.taskxmleditor.xsd.XsdIntrospection;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.xmlbeans.SchemaAnnotation;
import org.apache.xmlbeans.SchemaGlobalElement;
import org.apache.xmlbeans.XmlObject;
import org.jdesktop.swingx.JXTaskPane;
import org.jdesktop.swingx.JXTaskPaneContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.swing.*;
import javax.xml.namespace.QName;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.awt.*;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

public class AddOtherNamespaceElementDialog extends JPanel {
    public final static Logger log = LoggerFactory.getLogger(AddOtherNamespaceElementDialog.class);

    private Task model;
    private JTextArea elemInfo;
    HashMap<JRadioButton, QName> map = new HashMap<>();

    private ButtonGroup bg = new ButtonGroup();

    public QName getSelectedAny() {
        AbstractButton b = Gui.getSelectedButton(bg);
        if (null != b)
            return map.get(b);
        return null;
    }

    public AddOtherNamespaceElementDialog(Task model) {
        this.model = model;

        elemInfo = new JTextArea();
        //elemInfo.setEnabled(false);
        elemInfo.setEditable(false);
        elemInfo.setLineWrap(true);
        elemInfo.setWrapStyleWord(true);
        elemInfo.setBorder(null);
        elemInfo.setBackground(new Color(222, 225, 229));

        Dimension anyDim = new Dimension(500, 500);
        Dimension infoDim = new Dimension(300, 500);

        try {
            JXTaskPaneContainer taskPaneContainer = new JXTaskPaneContainer();
            addExtensions(taskPaneContainer);

            JScrollPane extensionPane = new JScrollPane(taskPaneContainer);
            extensionPane.setPreferredSize(anyDim);

            JScrollPane infoPane = new JScrollPane(elemInfo);
            infoPane.setPreferredSize(infoDim);

            JSplitPane split = new JSplitPane();
            split.setLeftComponent(extensionPane);
            split.setRightComponent(infoPane);

            setLayout(new BorderLayout());
            add(split, BorderLayout.CENTER);
        } catch (Exception e) {
            log.error(ExceptionUtils.getStackTrace(e));
            add(new JLabel(e.getMessage()));
        }
    }

    private void addExtensions(JXTaskPaneContainer container) throws Exception {
        XsdIntrospection xsd = Configuration.getInstance().getXsd();
        if (null == xsd)
            throw new Exception("No XSD files available.");
        //Set<Map.Entry<QName, SchemaGlobalElement>> elems = xsd.getGlobalElements();
        HashMap<QName, SchemaGlobalElement> elems = xsd.getGlobalElements();

        HashMap<String, JXTaskPane> panes = new HashMap<>();


        for (Map.Entry<QName, SchemaGlobalElement> elem : elems.entrySet()) {
            String namespaceURI = elem.getKey().getNamespaceURI();

            // Skip any elements from the proforma schema
            if(namespaceURI.equals(Configuration.TASK_XSD_TARGET_NAMESPACE))
                continue;

            if (!panes.containsKey(namespaceURI)) {
                JXTaskPane pane = new JXTaskPane();
                pane.setTitle(namespaceURI);
                panes.put(namespaceURI, pane);
//                container.add(pane);
            }

            //JCheckBox cb = new JCheckBox(elem.getKey().getLocalPart());
            JRadioButton cb = new JRadioButton(elem.getKey().getLocalPart());
            map.put(cb, elem.getKey());
            bg.add(cb);

            cb.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    StringBuilder sb = new StringBuilder();
                    SchemaAnnotation an = elem.getValue().getAnnotation();
                    if (null != an) {
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
                                        sb.append(System.getProperty("line.separator"));
                                        sb.append(System.getProperty("line.separator"));
                                        break;
                                    }
                                }
                            }
                        }
                    }
                    elemInfo.setText(sb.toString());
                }

                public void mouseExited(java.awt.event.MouseEvent evt) {
                    elemInfo.setText("");
                }
            });

            JXTaskPane pane = panes.get(namespaceURI);
            pane.add(cb);
        }

        SortedSet<String> sortedNs = new TreeSet<String>(panes.keySet());
        sortedNs.forEach(ns -> container.add(panes.get(ns)));
    }

    private String nodeToString(Node node) {
        StringWriter sw = new StringWriter();
        try {
            Transformer t = TransformerFactory.newInstance().newTransformer();
            t.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            t.setOutputProperty(OutputKeys.INDENT, "yes");
            t.transform(new DOMSource(node), new StreamResult(sw));
        } catch (TransformerException te) {
            log.error(ExceptionUtils.getStackTrace(te));
        }
        return sw.toString();
    }
}
