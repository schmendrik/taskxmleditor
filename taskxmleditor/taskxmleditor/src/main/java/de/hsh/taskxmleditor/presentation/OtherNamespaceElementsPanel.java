package de.hsh.taskxmleditor.presentation;

import de.hsh.taskxmleditor.application.Configuration;
import de.hsh.taskxmleditor.dom.NodeCreator;
import de.hsh.taskxmleditor.generic_editor.ElmentsDisplayTheirTextNodeTreeTableModel;
import de.hsh.taskxmleditor.generic_editor.GenericEditor;
import de.hsh.taskxmleditor.plugin.PluginEditor;
import de.hsh.taskxmleditor.plugin.PluginEditorModel;
import de.hsh.taskxmleditor.plugin.PluginEditorModelImpl;
import de.hsh.taskxmleditor.plugin.PluginManager;
import de.hsh.taskxmleditor.taskxml_v1_1.Task;
import de.hsh.taskxmleditor.utils.Str;
import net.java.balloontip.BalloonTip;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.jdesktop.swingx.JXTaskPane;
import org.jdesktop.swingx.JXTaskPaneContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.*;

import javax.swing.*;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;
import java.util.function.Consumer;

public class OtherNamespaceElementsPanel extends JPanel {
    /**
     *  Event class
     */
    @FunctionalInterface
    public interface ItemCountChangedListener {
        void itemCountChanged(int itemCount);
    }

    private ItemCountChangedListener itemCountListener;
    // this item count represents the items on the gui, not in the AnyObjectList
    private int itemCount = 0;

    public void setItemCountChangedListener(ItemCountChangedListener l) {
        itemCountListener = l;
    }

    final static Logger log = LoggerFactory.getLogger(OtherNamespaceElementsPanel.class);
    private Task model;
    private JButton addAnyBtn;

    private JXTaskPaneContainer taskPaneContainer = new JXTaskPaneContainer();
    private HashMap<String, JXTaskPane> taskPaneMap = new HashMap<>();
    private HashMap<String, AddRemoveItemsPanel> addRemItemMap = new HashMap<>();

    private java.util.List<Object> anies;

    public OtherNamespaceElementsPanel(Task model, java.util.List<Object> existingAnies) {
        this(model, existingAnies, Str.get("Other.Name"));
    }

    public OtherNamespaceElementsPanel(Task model, java.util.List<Object> existingAnies, String title) {
        this.model = model;
        this.anies = existingAnies;
        addAnyBtn = new JButton(Str.get("Other.Add"));

        addAnyBtn.addActionListener(l -> {
            AddOtherNamespaceElementDialog selectDialog = new AddOtherNamespaceElementDialog(model);

            int reply = JOptionPane.showConfirmDialog(null,
                    selectDialog,
                    "Add Extension",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE);

            if (reply == JOptionPane.OK_OPTION) {
                if (null != selectDialog.getSelectedAny())
                    addAnyElement(selectDialog.getSelectedAny(), true);
            }
        });

        for (Object obj : existingAnies) {
            Node n = (Node) obj;
            QName qn = new QName(n.getNamespaceURI(), n.getLocalName());
            addAnyElement(qn, n, false);
        }


        JPanel addAnyBtnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        addAnyBtnPanel.add(addAnyBtn);

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.add(taskPaneContainer);

        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder(title));
        add(addAnyBtnPanel, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);
    }

    private void addAnyElement(QName qname, boolean fromAddButton) {
        Document doc = null;
        try {
            DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
            f.setNamespaceAware(true);
            doc = f.newDocumentBuilder().newDocument();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Node defaultValue = NodeCreator.createElementNodeWithPreconfiguredChildren(doc, qname);
        doc.appendChild(defaultValue);
        addAnyElement(qname, doc.getDocumentElement(), fromAddButton);
    }

    private void addAnyElement(QName qName, Node initData, boolean fromAddButton) {
        QName selected = qName;
        String key = selected.getNamespaceURI();

        JXTaskPane taskPane = taskPaneMap.get(key);
        AddRemoveItemsPanel addRemPanel = addRemItemMap.get(key);
        if (null == taskPane) {
            taskPane = new JXTaskPane();
            taskPane.setTitle(String.format("%s", selected.getNamespaceURI()));
            taskPaneMap.put(key, taskPane);
            taskPaneContainer.add(taskPane);

            addRemPanel = new AddRemoveItemsPanel(null, false);
            addRemPanel.setItemRemoved(new RemoveEditorAndHideTaskPane(addRemPanel, taskPane, anies));
            // The removing of an item happens in the OtherNamespaceElementsPanel class, not the
            // AddRemoveItemsPanel, so we don't subscribe to the ItemAdded event
            addRemItemMap.put(key, addRemPanel);
            taskPane.add(addRemPanel);
        }

        // Make any previously hidden taskPanes visible again
        taskPane.setVisible(true);


        PluginEditorModel editorModel = new PluginEditorModelImpl(anies, qName, initData, Configuration.getInstance().getTask());

        // remove namespace attributes like xmlns, xmlns:foo, xmlns:bar, ...
        // the user doesn't care about them, they don't belong to the model data either
        // Jaxb will re-add them when they are marshalled to xml, so no worries there
        removeNamespaceAttributes(editorModel.getNode(), true);

        PluginEditor editor = PluginManager.createEditor(qName, editorModel);
        EditorComponent ec = new EditorComponent(editor, editorModel, qName.getLocalPart());//initData.getLocalName());

        if (editor instanceof GenericEditor) {
            ec.setPreferredSize(getGenericEditorPreferredSize());
//            Dimension d = getGenericEditorPreferredSize();
//            ((GenericEditor)editor).setPreferredSize(d);
//            ((GenericEditor)editor).resizeTableHeight(d.height);
        }

        addRemPanel.addItem(ec);
        ++itemCount;

        // the actual node is inserted into the Any Object List ('anies') at some later point
        // when the editor saves his data using PluginEditorModel.saveNode().

        //getContainer(originialComponent).scrollRectToVisible(originialComponent.getBounds());
        //Gui.focus(ec); // throws nullpointer at some point...
        ec.scrollRectToVisible(ec.getBounds());

        if (fromAddButton)
            new TimedBalloon(new BalloonTip(ec, qName.getLocalPart() + " added"), 4000);

        if(null != itemCountListener)
            itemCountListener.itemCountChanged(itemCount);
    }

    private Dimension getGenericEditorPreferredSize() {
        Dimension d = new Dimension(900, 500);
        try {
            String w = Configuration.getPropertiesKey("GenericEditor.preferredWidth",
                    Integer.toString(d.width));
            String h = Configuration.getPropertiesKey("GenericEditor.preferredHeight",
                    Integer.toString(d.height));
            return new Dimension(Integer.parseInt(w), Integer.parseInt(h));
        } catch (Exception oops) {
            log.error(ExceptionUtils.getStackTrace(oops));
        }
        return d;
    }

    private static void removeNamespaceAttributes(Node n, boolean removeFromChildrenToo) {
        if (!(n instanceof Element))
            return;

        String[] staticNs = new String[] {
                Configuration.TASK_XSD_TARGET_NAMESPACE,
                "http://www.w3.org/2001/XMLSchema-instance",
                "http://www.w3.org/2001/XMLSchema"
        };

        Set<String> dynamicNs = Configuration.getInstance().getTargetNsToFileMapping().keySet();

        NamedNodeMap attributes = n.getAttributes();
        Element e = (Element) n;
        ArrayList<Node> rem = new ArrayList<>();
        for (int i = 0; i < attributes.getLength(); i++) {
            Node item = attributes.item(i);
            String ns = ElmentsDisplayTheirTextNodeTreeTableModel.staticGetNodeTextContent(item);

            if (Arrays.stream(staticNs).anyMatch(r -> r.equalsIgnoreCase(ns)))
                rem.add(item);
            else if (dynamicNs.stream().anyMatch(r -> r.equalsIgnoreCase(ns)))
                rem.add(item);
        }

        rem.forEach(attr -> e.removeAttributeNS(attr.getNamespaceURI(), attr.getLocalName()));

        if (removeFromChildrenToo && n.getChildNodes().getLength() > 0) {
            NodeList l = n.getChildNodes();
            for (int i = 0; i < l.getLength(); i++)
                removeNamespaceAttributes(l.item(i), removeFromChildrenToo);
        }
    }

    private class EditorComponent extends JPanel {
        public PluginEditor editor;
        public PluginEditorModel model;

        public EditorComponent(PluginEditor innerEditor, PluginEditorModel model, String title) {
            this.model = model;
            this.editor = innerEditor;
            setLayout(new BorderLayout());
            setBorder(BorderFactory.createTitledBorder(title));
            add(innerEditor, BorderLayout.CENTER);
        }
    }

    private abstract class MyConsumer implements Consumer<Component> {
        protected AddRemoveItemsPanel items;
        protected JXTaskPane taskPane;

        public MyConsumer(AddRemoveItemsPanel items, JXTaskPane taskPane) {
            this.items = items;
            this.taskPane = taskPane;
        }
    }

    private class RemoveEditorAndHideTaskPane extends MyConsumer {
        private java.util.List<Object> anies;

        public RemoveEditorAndHideTaskPane(AddRemoveItemsPanel items, JXTaskPane taskPane,
                                           java.util.List<Object> anies) {
            super(items, taskPane);
            this.anies = anies;
        }

        @Override
        public void accept(Component item) {
            System.out.print("Removing Any item... ");
            EditorComponent ec = (EditorComponent) item;
            boolean remove = anies.remove(ec.model.getNode());
            System.out.println(" actually removed: " + remove);
            // It's alright if anies.remove() returns false. Extensions that are added by the
            // user don't get stored in the Any Object List immediately. Rather, they get
            // saved by their corresponding editors via PluginEditorModel.saveNode() when
            // the user issues a save or an xml validation command.
            PluginManager.removeEditorFromCirculation(ec.editor);

            if (items.getItemCount() == 0) {
                taskPane.setVisible(false);
                revalidate();
                repaint();
            }

            --itemCount;

            if(null != itemCountListener)
                itemCountListener.itemCountChanged(itemCount);
        }
    }
}
