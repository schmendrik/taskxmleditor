package de.hsh.taskxmleditor.generic_editor;

import org.jdesktop.swingx.treetable.AbstractTreeTableModel;
import org.w3c.dom.Element;
import org.w3c.dom.Attr;
import org.w3c.dom.Node;

public abstract class ExtendedTreeTableModel extends AbstractTreeTableModel {
    public abstract String getNodeTextContent(Node n);
    public abstract void setNodeTextContent(Node n, String content);
    public abstract void insertChild(Node parent, Node child, int position);
    public abstract void setAttribute(Element parent, Attr attribute);
    public abstract void removeChild(Node parent, Node child);
}
