package de.hsh.taskxmleditor.generic_editor;

import de.hsh.taskxmleditor.application.Configuration;
import de.hsh.taskxmleditor.dom.DomHelper;
import de.hsh.taskxmleditor.dom.NodeCreator;
import de.hsh.taskxmleditor.xsd.XsdIntrospection;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.xmlbeans.*;
import org.netbeans.swing.outline.Outline;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.*;

import javax.swing.*;
import javax.xml.namespace.QName;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigInteger;
import java.util.List;

public class CreateChildNodesContextMenu extends MouseAdapter {
    final static Logger log = LoggerFactory.getLogger(CreateChildNodesContextMenu.class);

    private Outline outline;
    private ExtendedTreeTableModel model;

    public CreateChildNodesContextMenu(Outline outline, ExtendedTreeTableModel model) {
        this.outline = outline;
        this.model = model;
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        int r = outline.rowAtPoint(e.getPoint());
        if (r >= 0 && r < outline.getRowCount()) {
            outline.setRowSelectionInterval(r, r);
        } else {
            outline.clearSelection();
        }

        int rowIndex = outline.getSelectedRow();
        if (rowIndex < 0)
            return;

        if (e.isPopupTrigger() && e.getComponent() instanceof Outline) {
            Node n = (Node) outline.getModel().getValueAt(rowIndex, 0);
            JPopupMenu popup = createMenuForNodeManipulation(n, (Outline)e.getComponent(), rowIndex);
            popup.show(e.getComponent(), e.getX(), e.getY());
        }
    }


    private JComponent createHeader(String header) {
        JLabel label = new JLabel(header, SwingConstants.LEFT);
        label.setFont(label.getFont().deriveFont(Font.ITALIC));
        label.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        return label;
    }

    private JPopupMenu createMenuForNodeManipulation(Node selectedNode, Outline outline, int rowIndex) {
        JPopupMenu menu = new JPopupMenu();

        // Make sure there is a xsd file to introspect for this namespace uri
        String namespace = selectedNode.getNamespaceURI();

        // attributes don't have a namespace, so see if its an attribute and if it is, get its parent's namespace
        if ((null == namespace || namespace.isEmpty()) && selectedNode instanceof Attr)
            namespace = ((Attr)selectedNode).getOwnerElement().getNamespaceURI();

        if (null == namespace || !Configuration.getInstance()
                .getTargetNsToFileMapping().containsKey(namespace)) {
            // we don't have an xsd file for this namespace
            JMenuItem notPossible = new JMenuItem("No schema information available for this node");
            notPossible.setEnabled(false);
            menu.add(notPossible);
            return menu;
        }

        menu.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;

        if (selectedNode instanceof Element) {
            menu.add(createHeader("Element"), gbc);
            gbc.gridwidth = 1;
            gbc.gridy++;

            JMenu addChildElementsMenu = createAddElementsMenu(selectedNode, outline);
            JMenu addChildAttributesMenu = createAddAttributeMenu(selectedNode, outline);
            menu.add(addChildElementsMenu, gbc);
            gbc.gridy++;
            menu.add(addChildAttributesMenu, gbc);
            gbc.gridy++;
        } else if (selectedNode instanceof Attr) {
            menu.add(createHeader("Attribute"), gbc);
            gbc.gridy++;
        }

        // Not neccessary: cannot insert elements wherever we want. they have a defined order
        // JMenu insertNodeBeforeMenu = this.createAddBeforeMenu(selectedNode, outline);
        // menu.add(insertNodeBeforeMenu);

        JMenuItem removeThisNodeMenu = createRemoveThisNodeMenu(selectedNode, outline, rowIndex);
        menu.add(new JSeparator(), gbc);
        gbc.gridy++;
        menu.add(removeThisNodeMenu, gbc);
        return menu;
    }

    private JMenuItem createRemoveThisNodeMenu(Node selectedNode, Outline outline, int rowIndex) {
        JMenuItem removeNodeMenu = new JMenuItem();

        QName qName = DomHelper.getQName(selectedNode);

        if (selectedNode instanceof Attr) {
            Attr attr = (Attr) selectedNode;
            Node parentNode = attr.getOwnerElement();
            ActionListener remNodeAction = event -> {
                log.debug("Removing attribute " + selectedNode);
                model.removeChild(parentNode, selectedNode);
                outline.expandPath(ElmentsDisplayTheirTextNodeTreeTableModel.getTreePath(parentNode, false));
            };
            if (canRemoveAttribute(parentNode, qName)) {
                removeNodeMenu.setText("Remove Attribute");
                removeNodeMenu.addActionListener(remNodeAction);
            }
            else {
                removeNodeMenu.setText("Cannot Remove Required Attribute");
                removeNodeMenu.setEnabled(false);
            }
        } else if (selectedNode instanceof Element) {
            Node parentNode = selectedNode.getParentNode();
            ActionListener remNodeAction = event -> {
                log.debug("Removing element " + selectedNode);
                model.removeChild(parentNode, selectedNode);
                outline.expandPath(ElmentsDisplayTheirTextNodeTreeTableModel.getTreePath(parentNode, false));
            };

            if(parentNode instanceof Document) {
                // Cannot remove root elements this way.
                // The 'remove' button of AddRemoveItemsPanel is
                // how elements of foreign namenspaces are removed
                // from a task
                removeNodeMenu.setEnabled(false);
            } else {
                if(canRemoveElement((Element)parentNode, qName)) {
                    removeNodeMenu.setText("Remove Element");
                    removeNodeMenu.addActionListener(remNodeAction);
                }
                else {
                    int minCount = getMinCountForElement(qName);
                    if(1 == minCount)
                        removeNodeMenu.setText(String.format("Cannot Remove Element (at least 1 element of this type is required)"));
                    if(1 < minCount)
                        removeNodeMenu.setText(String.format("Cannot Remove Element (at least %d elements of this type are required)", minCount));
                    removeNodeMenu.setEnabled(false);
                }
            }
        }
        else System.out.println("Node is of type " + selectedNode);

        return removeNodeMenu;
    }

    private boolean canRemoveAttribute(Node parent, QName attrQName) {
        QName parentQn = new QName(parent.getNamespaceURI(), parent.getLocalName());
        SchemaType parentType = Configuration.getInstance().getXsd().getType(parentQn);
        if (null != parentType) {
            SchemaAttributeModel aModel = parentType.getAttributeModel();
            SchemaLocalAttribute sAttr = aModel.getAttribute(attrQName);
            if (SchemaLocalAttribute.REQUIRED == sAttr.getUse())
                return false;
        } else {
            // TODO: only the root element's (task) attributes have no
            // schemaProp for some reason
            log.warn("Weird attribute {} has no SchemaProperty", attrQName);
            return false;
        }
        return true;
    }

    private boolean canRemoveElement(Element parent, QName elemQName) {
        BigInteger min = Configuration.getInstance().getXsd().getMinOccurs(elemQName);

        if(null != min) {
            int currCountOfThisElementType = 0;
            NodeList list = parent.getChildNodes();
            for (int i = 0; i < list.getLength(); i++) {
                Node item = list.item(i);
                if(item instanceof Element) {
                    QName siblingQn = DomHelper.getQName(item);
                    if (DomHelper.qNamesAreEqual(elemQName, siblingQn))
                        currCountOfThisElementType++;
                }
            }

            return currCountOfThisElementType > min.intValue();
        }

        return true;
    }

    private int getMinCountForElement(QName elemQName) {
        BigInteger min = Configuration.getInstance().getXsd().getMinOccurs(elemQName);
        if(null != min)
            return min.intValue();
        return -1;
    }

    private int getMaxCountForElement(QName elemQName) {
        BigInteger max = Configuration.getInstance().getXsd().getMaxOccurs(elemQName);
        if(null != max)
            return max.intValue();
        return -1;
    }

    /**
     * create an 'intelligent' menu to create child nodes according to the
     * parent node's schema definition. Takes min/max occurs into account.
     *
     * @param selectedNode
     * @param children
     * @return
     */
    private JMenu createAddElementsMenu(Node selectedNode, Outline outline) {
        JMenu addNodes = new JMenu("Add Element");

        if(selectedNode instanceof Element) {
            QName qName = DomHelper.getQName(selectedNode);

            List<SchemaProperty> children = null;
            try {
                children = Configuration.getInstance().getXsd().getChildElements(qName);
            } catch(Exception e) {
                log.error(ExceptionUtils.getStackTrace(e));
                addNodes.setText("There is no schema information available for this node");
                addNodes.setEnabled(false);
                return addNodes;
            }
            for (SchemaProperty child : children) {
                if (canAddElement((Element)selectedNode, child.getName())) {
                    JMenuItem childElement = new JMenuItem(child.getName().getLocalPart());
                    childElement.addActionListener(e -> { // TODO: move to class
                        // AddChildNode
                        Document doc = selectedNode.getOwnerDocument();
                        Node complexNode = NodeCreator.createElementNodeWithPreconfiguredChildren(doc, child);
                        // JMenuItem sub = new JMenuItem(prop.getName().toString());

                        QName newChildQn = DomHelper.getQName(complexNode);
                        Integer pos = Configuration.getInstance().getXsd().getPositionInAllChoiceSequence(newChildQn);
                        System.out.println(qName + " pos is " + pos);
                        if (null == pos)
                            pos = 0;
                        model.insertChild(selectedNode, complexNode, pos);
                        outline.expandPath(ElmentsDisplayTheirTextNodeTreeTableModel.getTreePath(selectedNode, false));
                    });

                    String doc = Configuration.getInstance().getXsd().getElementAnnotation(child.getName());
                    if(null != doc && !doc.isEmpty())
                        childElement.setToolTipText(doc);

                    addNodes.add(childElement);
                }
            }
        }

        if (addNodes.getItemCount() == 0) {
            // Use some sort of indication that nodes can't be added anymore so
            // the user doesn't think it's a buggy menu
            //JMenuItem nothing = new JMenuItem("There is nothing to add");
            //nothing.setEnabled(false);
            //addNodes.add(nothing);
            addNodes.setEnabled(false);
        }

        return addNodes;
    }

    private JMenu createAddAttributeMenu(Node selectedNode, Outline outline) {
        JMenu addNodes = new JMenu("Add Optional Attributes");

        if(selectedNode instanceof Element) {
            Element element = (Element) selectedNode;
            QName qName = DomHelper.getQName(selectedNode);
            List<SchemaProperty> children = null;
            try {
                children = Configuration.getInstance().getXsd().getChildAttributes(qName);
            } catch(Exception e) {
                log.error(ExceptionUtils.getStackTrace(e));
                addNodes.setText("There is no schema information available for this node");
                addNodes.setEnabled(false);
                return addNodes;
            }
            for (SchemaProperty child : children) {
                if (parentCanAddAttribute(element, child)) {
                    JMenuItem childAttribute = new JMenuItem(child.getName().getLocalPart());
                    childAttribute.addActionListener(e -> {
                        Attr attr = NodeCreator.createAttributeWithPreconfiguration(element, child.getName());
                        model.setAttribute(element, attr);
                        outline.expandPath(ElmentsDisplayTheirTextNodeTreeTableModel.getTreePath(selectedNode, false));
                    });

                    QName parentQn = DomHelper.getQName(element);
                    String doc = Configuration.getInstance().getXsd()
                            .getAttributeAnnotation(parentQn, child.getName().getLocalPart());
                    if(null != doc && !doc.isEmpty())
                        childAttribute.setToolTipText(doc);

                    addNodes.add(childAttribute);
                }
            }
        }

        if (addNodes.getItemCount() == 0) {
            // Use some sort of indication that nodes can't be added anymore so
            // the user doesn't think it's a buggy menu
//            JMenuItem nothing = new JMenuItem("There is nothing to add");
//            nothing.setEnabled(false);
//            addNodes.add(nothing);
            addNodes.setEnabled(false);
        }

        return addNodes;
    }

    private boolean parentCanAddAttribute(Element element, SchemaProperty child) {
        // TODO: make actually sure the child is part of the element according to xsd
        return !element.hasAttribute(child.getName().getLocalPart());
    }

//    private JMenu createAddBeforeMenu(Node selectedNode, Outline outline) {
//        // TODO: right now, only node types that have been right-clicked on can
//        // be added (what about the other child element types?)
//
//        JMenu addBefore = new JMenu("Add Before");
//
//        // Do not display this menu for anything other than an element node
//        // (excluding text element nodes)
//        if (selectedNode instanceof Element && null != selectedNode.getParentNode()) {
//            // Steps to take:
//            // Inspect the selected nodes parent and find out if the selected
//            // node can be duplicated/added
//
//            Node parent = selectedNode.getParentNode();
//            // If the parent node is of type org.w3c.dom.Document, then this is
//            // a global element and there is no info on how many of those are allowed
//            // (well, there can be an unlimited number of global elements, actually)
//            if(!(parent instanceof Document)) {
//
//
////            if (parent instanceof Document) {
////                Document d = (Document) parent;
////                System.out.println("======> local name: " + d.getLocalName());
////            }
//
//                QName childQn = DomHelper.getQName(selectedNode);
//
////                String parentNsUri = parent.getNamespaceURI();
////                String parentLocalName = parent.getLocalName();
//                // if the parent is of type Document (which is the root node
//                // in ##other namespaces), then the local name is null for some reason
////                if (null == parentLocalName)
////                    parentLocalName = parent.getNodeName();
////                QName parentQn = new QName(parentNsUri, parentLocalName);
//
//                QName parentQn = DomHelper.getQName(parent);
//
//                // TODO: remove this cast, make getElementType return SchemaProperty
//
//                List<SchemaProperty> children = Configuration.getInstance().getXsd().getChildElements(parentQn);
//                SchemaProperty childSchema = children.stream().filter(child -> child.getName().equals(childQn)).findFirst()
//                        .get();
//                //if (parentCanAddElement(parent, childSchema)) {
//                if (canAddElement((Element)parent, childSchema.getName(), children.size())) {
//                    JMenuItem childItem = new JMenuItem(childQn.getLocalPart());
//                    childItem.addActionListener(e -> {
//                        Document doc = selectedNode.getOwnerDocument();
//                        Node complexNode = NodeCreator.createElementNodeWithPreconfiguredChildren(doc, childSchema);
//                        parent.insertBefore(complexNode, selectedNode);
//                        // outline.fireTableDataChanged();
//                        // TODO: fire table data changed or something, then expand
//                        // until selected node is reached
//                        // outline.getModel().
//                        ((GenericTreeTableXmlEditor.MyOutline)outline).internalDrityUpdate(complexNode);
//                    });
//                    addBefore.add(childItem);
//                }
//            }
//        }
//
//        addBefore.setEnabled(addBefore.getItemCount() > 0);
//        return addBefore;
//    }

    private boolean canAddElement(Element parent, QName elemQName) {
        int currentChildElementCount = DomHelper.getElementChildCountExcludingAnyTextNode(parent);

        XsdIntrospection xsd = Configuration.getInstance().getXsd();

        SchemaParticle container = xsd.getContainerInfoForElement(elemQName);
        if (null != container) {
            if(container.getParticleType() == SchemaParticle.CHOICE) {
                BigInteger choiceMax = container.getMaxOccurs();
                if(null != choiceMax && choiceMax.compareTo(BigInteger.valueOf(currentChildElementCount)) <= 0) {
                    // if the choice's maxOccurs does not permit any more children,
                    // we do not need to proceed any further
                    return false;
                }
            }
        }

        BigInteger max = xsd.getMaxOccurs(elemQName);

        if(null != max) {
            int currCountOfThisElementType = 0;
            NodeList list = parent.getChildNodes();
            for (int i = 0; i < list.getLength(); i++) {
                Node item = list.item(i);
                if(item instanceof Element) {
                    QName siblingQn = DomHelper.getQName(item);
                    if (DomHelper.qNamesAreEqual(elemQName, siblingQn))
                        currCountOfThisElementType++;
                }
            }

            return currCountOfThisElementType < max.intValue();
        }

        return true;
    }

//    /**
//     * @param selectedNode
//     * @param prop
//     * @return
//     */
//    private boolean parentCanAddElement(Node parentNode, SchemaProperty child) {
//        System.out.println("checking if child" + child.getName().getLocalPart() + " can be added to parent "
//                + parentNode.getNodeName());
//        if (parentNode instanceof Element) {
//            Element elem = (Element) parentNode;
//            if (null != child.getMaxOccurs()) {
//                int max = child.getMaxOccurs().intValue();
//
//                int count = 0;
//                Node childNode = elem.getFirstChild();
//                while (null != childNode) {
//                    if (childNode instanceof Element) {
//                        QName childNodeQn = new QName(childNode.getNamespaceURI(), childNode.getLocalName());
////                        System.out.println("checking add: " + childNodeQn);
////                        System.out.println("cmparing add: " + child.getName());
//                        // Beware of comparing xerces.QName with xmlbeans.QName
//                        // using equals() - it always fails
//                        if (childNodeQn.getNamespaceURI().equals(child.getName().getNamespaceURI())
//                                && childNodeQn.getLocalPart().equals(child.getName().getLocalPart()))
//                            count++;
//                    }
//                    childNode = childNode.getNextSibling();
//                }
//                if (count < max)
//                    return true;
//            } else
//                return true;
//        }
//        return false;
//    }

    String findJavaType(SchemaType sType) {
        while (sType.getFullJavaName() == null)
            sType = sType.getBaseType();

        return sType.getFullJavaName();
    }
}
