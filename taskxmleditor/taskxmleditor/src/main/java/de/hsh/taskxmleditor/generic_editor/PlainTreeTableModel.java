package de.hsh.taskxmleditor.generic_editor;

import org.w3c.dom.*;

import javax.swing.tree.TreePath;
import java.util.ArrayList;
import java.util.Collections;

/**
 * This class' code is outdated. It is not used anyway
 */
public class PlainTreeTableModel extends ExtendedTreeTableModel {
    private Node root;

    public PlainTreeTableModel(Node root) {
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
        if(parent instanceof Element)
            return ((Element) parent).getChildNodes().item(index);
        return null;
    }

    // How many children does this node have?
    @Override
    public int getChildCount(Object node) {
        if(node instanceof Element)
            return ((Element) node).getChildNodes().getLength();
        System.out.println("node " + ((Node)node).getLocalName() + " has no element, no children");
        return 0;
    }

    // Return the index of the child node in the parent node
    @Override
    public int getIndexOfChild(Object parent, Object child) {
        if(parent instanceof Element) {
            Element e = (Element) parent;
            NodeList list = e.getChildNodes();
            for(int i=0; i<list.getLength(); i++)
                if(child.equals(list.item(i)))
                    return i;
        }
        System.out.println("This shouldn't happen");
        return 0;
    }

    // Is this node a leaf? (Leaf nodes are displayed differently by JTree)
    @Override
    public boolean isLeaf(Object node) {
        return !(node instanceof Element);
    }

    @Override
    public Object getRoot() {
        return root;
    }

    @Override
    public void insertChild(Node parent, Node child, int position) {
        parent.appendChild(child);

        modelSupport.fireNewRoot();
    }

    @Override
    public void setAttribute(Element parent, Attr attribute) {
        System.out.println("NOT IMPLEMENTED");
    }

    @Override
    public void removeChild(Node parent, Node child) {
        parent.removeChild(child);
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

    public void removeNode(Node n) {
        // outline.removePath or somethding
    }

    @Override
    public String getNodeTextContent(Node n) {
        return n.getNodeValue();
    }

    @Override
    public void setNodeTextContent(Node n, String content) {
        n.setNodeValue(content);
    }
}
