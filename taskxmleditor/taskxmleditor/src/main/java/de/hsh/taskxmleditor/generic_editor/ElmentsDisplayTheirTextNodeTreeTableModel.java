package de.hsh.taskxmleditor.generic_editor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.*;

import javax.swing.tree.TreePath;
import java.util.ArrayList;
import java.util.Collections;

/**
 * This TreeTableModel makes it so "Text" nodes, which represent the text content
 * of an element (their parent), does not appear in the GenericEditor's tree view.
 * The text node's value is shown directly beside the parent element in the 'value'
 * This makes it way more user friendly.
 *
 * TODO: Though there needs to be some sort of indicator to make the user know that
 * elements (whose text node does not have a value yet) can have their (text node) value
 * edited by clicking on the element's row in the value column. Because it's not
 * apparent right now.
 */
public class ElmentsDisplayTheirTextNodeTreeTableModel extends ExtendedTreeTableModel {
    final static Logger log = LoggerFactory.getLogger(ElmentsDisplayTheirTextNodeTreeTableModel.class);
    private Node root;

    public ElmentsDisplayTheirTextNodeTreeTableModel(Node root) {
        this.root = root;
    }

    @Override
    public int getColumnCount() {
        return 3;
    }

    @Override
    public String getColumnName(int column) {
        switch (column) {
            case 0:
                return "Node";
            case 1:
                return "";
            case 2:
                return "Value";
            default:
                return "Unknown";
        }
    }

    @Override
    public Object getValueAt(Object node, int column) {
        Node n = (Node) node;
        switch (column) {
            case 0:
                return node;
            case 1:
                return "";
            case 2:
                return getNodeTextContent(n);
            default:
                return "Unknown";
        }
    }

    // Return the specified child of a parent node.
    @Override
    public Object getChild(Object parent, int index) {
        Node p = (Node) parent;

        for (int i = 0; i < p.getChildNodes().getLength(); i++) {
            Node c = p.getChildNodes().item(i);
            if (c instanceof Text)
                continue;
            if (index-- == 0)
                return c;
        }
        for (int i = 0; i < p.getAttributes().getLength(); i++) {
            Node n = p.getAttributes().item(i);
            if (index-- == 0)
                return n;
        }
        return null;
    }

    // How many children does this node have?
    @Override
    public int getChildCount(Object node) {
        Node n = (Node) node;
        int count = 0;
        if (n.hasAttributes())
            count = n.getAttributes().getLength();
        for (int i = 0; i < n.getChildNodes().getLength(); i++) {
            Node child = n.getChildNodes().item(i);
            if (!(child instanceof Text))
                count++;
        }
        return count;
    }

    // Return the index of the child node in the parent node
    @Override
    public int getIndexOfChild(Object parent, Object child) {
        Node p = (Node) parent;
        int index = 0;
        for (int i = 0; i < p.getChildNodes().getLength(); i++) {
            Node c = p.getChildNodes().item(i);
            if (c instanceof Text)
                continue;
            if (p.getChildNodes().item(i).equals(child))
                return index;
            index++;
        }
        for (int i = 0; i < p.getAttributes().getLength(); i++) {
            if (p.getAttributes().item(i).equals(child))
                return index;
            index++;
        }
        return index;
    }

    // Is this node a leaf? (Leaf nodes are displayed differently by JTree)
    @Override
    public boolean isLeaf(Object node) {
        Node n = (Node) node;
        if (n.hasAttributes())
            return false;
        if (n.hasChildNodes()) {
            if (n.getChildNodes().getLength() == 1)
                if (n.getChildNodes().item(0) instanceof Text)
                    return true;
            return false;
        }
        return true;
    }

    @Override
    public Object getRoot() {
        return root;
    }


    @Override
    public void insertChild(Node parent, Node child, int preferredPosition) {
        if(parent.getChildNodes().getLength() < preferredPosition) {
            preferredPosition = parent.getChildNodes().getLength();
        }

        if (parent instanceof Element) {
            if (!parent.hasChildNodes()) {
                parent.appendChild(child);
            } else {
                Node beforeNode = parent.getChildNodes().item(preferredPosition);
                parent.insertBefore(child, beforeNode);
            }

            modelSupport.fireNewRoot();
        } else
            log.error("parent is not an element");
    }

    @Override
    public void setAttribute(Element parent, Attr attribute) {
        parent.setAttributeNode(attribute);
        modelSupport.fireNewRoot();
    }

    @Override
    public void removeChild(Node parent, Node child) {
        if(child instanceof Element)
            ((Element) parent).removeChild((Element) child);
        else
            ((Element) parent).removeAttributeNode((Attr) child);
        modelSupport.fireNewRoot();
    }

    public static TreePath getTreePath(Node n, boolean includeTopLevelDocument) {
        ArrayList<Node> a = new ArrayList<>();

        do {
            if(n instanceof Document && !includeTopLevelDocument)
                continue;
            a.add(n);
        } while(null != (n = n.getParentNode()));

        Collections.reverse(a);
        return new TreePath(a.toArray(new Object[0]));
    }

    @Override
    public String getNodeTextContent(Node n) {
        return staticGetNodeTextContent(n);
    }

    public static String staticGetNodeTextContent(Node n) {
        if (n instanceof Element) {
            for (int i = 0; i < n.getChildNodes().getLength(); i++) {
                Node c = n.getChildNodes().item(i);
                if (c instanceof Text)
                    return c.getTextContent();
            }
        } else if (n instanceof Attr)
            return ((Attr) n).getValue();
        return null;
    }

    @Override
    public void setNodeTextContent(Node n, String content) {
        staticSetNodeTextContent(n, content);
    }

    public static void staticSetNodeTextContent(Node n, String content) {
        if (n instanceof Element) {
            Element e = (Element)n;
//            n.setTextContent(content);
            Text text = null;
            for (int i = 0; i < n.getChildNodes().getLength(); i++) {
                Node c = n.getChildNodes().item(i);
                if (c instanceof Text)
                    text = (Text)c;
            }
            if(null == text) {
                text = e.getOwnerDocument().createTextNode(content);
                e.appendChild(text);
            }
            else {
                text.setTextContent(content);
            }
        } else if (n instanceof Attr)
            n.setTextContent(content);
    }
}
