package de.hsh.taskxmleditor.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

/**
 * This class implements the Swing TreeModel interface so that the DOM tree
 * returned by a TreeWalker can be displayed in a JTree component.
 */
public class XmlDomTreeModel implements TreeModel {
    final static Logger logger = LoggerFactory.getLogger(XmlDomTreeModel.class);
    private Node root;

    /**
     * Create a TreeModel for a TreeWalker that returns the specified element
     * and all of its descendant nodes.
     */
    public XmlDomTreeModel(Node root) {
        this.root = root;
    }

    // Return the root of the tree
    @Override
    public Object getRoot() {
        return root;
    }

    /**
     * @param n
     * @return
     */
    public static String getNodeTextContent(Node n) {
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

    public static void setNodeTextContent(Node n, String content) {
        if (n instanceof Element) {
            for (int i = 0; i < n.getChildNodes().getLength(); i++) {
                Node c = n.getChildNodes().item(i);
                if (c instanceof Text)
                    c.setTextContent(content);
            }
        } else if (n instanceof Attr)
            n.setTextContent(content);
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
        }
        return false;
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

    // Only required for editable trees; unimplemented here.
    @Override
    public void valueForPathChanged(TreePath path, Object newvalue) {
        logger.debug("valueForPathChanged({}, {})", path, newvalue);
    }

    @Override
    public void addTreeModelListener(TreeModelListener l) {
    }

    @Override
    public void removeTreeModelListener(TreeModelListener l) {
    }

    public static XmlDomTreeModel create(Node root) throws Exception {
//        logger.debug("Creating TreeModel with root: " + root);
        return new XmlDomTreeModel(root);
    }
}