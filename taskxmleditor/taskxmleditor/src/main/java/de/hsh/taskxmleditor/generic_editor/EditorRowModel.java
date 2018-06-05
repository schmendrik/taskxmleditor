package de.hsh.taskxmleditor.generic_editor;

import org.netbeans.swing.outline.RowModel;
import org.w3c.dom.Node;

public class EditorRowModel implements RowModel {
    @Override
    public Class<?> getColumnClass(int column) {
        switch (column) {
            case 0:
                return String.class;
            case 1:
                return String.class;
            default:
                assert false;
        }
        return null;
    }

    @Override
    public int getColumnCount() {
        return 2;
    }

    @Override
    public String getColumnName(int column) {
        switch (column) {
            case 0:
                return "    "; // Info column, don't show header
            case 1:
                return "Value";
            default:
                assert false;
        }
        return null;
    }

    @Override
    public Object getValueFor(Object node, int column) {
        Node n = (Node) node;
        switch (column) {
            case 0:
                return "   ";
            case 1:
                return ElmentsDisplayTheirTextNodeTreeTableModel.staticGetNodeTextContent(n);
            default:
                assert false;
        }
        return null;
    }

    @Override
    public boolean isCellEditable(Object node, int column) {
        if (column == 0) // no editing in info column
            return false;
        return true;
    }

    @Override
    public void setValueFor(Object node, int column, Object value) {
        Node n = (Node) node;
        ElmentsDisplayTheirTextNodeTreeTableModel.staticSetNodeTextContent(n, value.toString());
    }
}

