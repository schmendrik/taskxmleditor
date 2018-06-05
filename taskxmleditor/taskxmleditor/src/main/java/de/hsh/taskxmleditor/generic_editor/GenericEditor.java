package de.hsh.taskxmleditor.generic_editor;

import de.hsh.taskxmleditor.application.Configuration;
import de.hsh.taskxmleditor.dom.DomHelper;
import de.hsh.taskxmleditor.plugin.PluginEditorModel;
import de.hsh.taskxmleditor.plugin.PluginHelper;
import de.hsh.taskxmleditor.plugin.PluginEditor;
import de.hsh.taskxmleditor.presentation.Gui;
import de.hsh.taskxmleditor.presentation.InvalidInput;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.netbeans.swing.outline.DefaultOutlineModel;
import org.netbeans.swing.outline.Outline;
import org.netbeans.swing.outline.OutlineModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.xml.namespace.QName;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class GenericEditor extends PluginEditor {
    final static Logger log = LoggerFactory.getLogger(GenericEditor.class);

    public static final int NODE_COLUMN_INDEX = 0;
    public static final int HELP_COLUMN_INDEX = 1;
    public static final int VALUE_COLUMN_INDEX = 2;

    private ExtendedOutline outline;
    private PluginEditorModel editorModel;

    private JScrollPane scrollPane;

    public GenericEditor(PluginEditorModel editorModel) {
        this.editorModel = editorModel;
        setLayout(new BorderLayout());

        ExtendedTreeTableModel treeModel = new ElmentsDisplayTheirTextNodeTreeTableModel(editorModel.getNode());
//        ExtendedTreeTableModel treeModel = new PlainTreeTableModel(editorModel.getNode());
        OutlineModel outlineModel = DefaultOutlineModel.createOutlineModel(treeModel,
                new EditorRowModel(), false);
        outline = new ExtendedOutline();
        outline.setRenderDataProvider(new EditorDataProvider());
        outline.setRootVisible(true); // so we can right click the parent and add child nodes
        outline.setModel(outlineModel);
        //outline.expandPath(new TreePath(treeModel.getRoot()));
        outline.setAutoCreateColumnsFromModel(true);
        outline.setColumnHidingAllowed(false);

        outline.getColumnModel().getColumn(HELP_COLUMN_INDEX).setCellRenderer(new HelpIconCellRenderer());
        outline.getColumnModel().getColumn(VALUE_COLUMN_INDEX).setCellEditor(new VersatileCellEditor());
        outline.getColumnModel().getColumn(NODE_COLUMN_INDEX).setPreferredWidth(130);
        outline.getColumnModel().getColumn(HELP_COLUMN_INDEX).setMaxWidth(30);

        outline.addMouseListener(new CreateChildNodesContextMenu(outline, treeModel));

        scrollPane = Gui.createScrollPane(outline);
        add(scrollPane, BorderLayout.CENTER);
    }

    public void setPreferredSize(Dimension dim) {
        scrollPane.setPreferredSize(dim);
    }

    public void resizeTableHeight(int maxHeight) {
        int height = (1 + outline.getRowCount()) * outline.getRowHeight();
        if(height > maxHeight)
            height = maxHeight;
        Dimension d = scrollPane.getPreferredSize();
        d.height = height;
    }

    // https://tips4java.wordpress.com/2008/11/10/table-column-adjuster/
    private void resizeTable() {
        outline.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        for (int column = 0; column < outline.getColumnCount(); column++) {
            TableColumn tableColumn = outline.getColumnModel().getColumn(column);
            int preferredWidth = tableColumn.getMinWidth();
            int maxWidth = tableColumn.getMaxWidth();

            for (int row = 0; row < outline.getRowCount(); row++) {
                TableCellRenderer cellRenderer = outline.getCellRenderer(row,
                        column);
                Component c = outline.prepareRenderer(cellRenderer, row, column);
                int width = c.getPreferredSize().width +
                        outline.getIntercellSpacing().width;
                preferredWidth = Math.max(preferredWidth, width);
                if (preferredWidth >= maxWidth) {
                    preferredWidth = maxWidth;
                    break;
                }
            }
            tableColumn.setPreferredWidth(preferredWidth);
        }
    }

    @Override
    public void saveInput() throws Exception {
        this.editorModel.saveNode(this.editorModel.getNode());
    }

    @Override
    public java.util.List<InvalidInput> validateInput() {
        ArrayList<InvalidInput> a = new ArrayList<>();

        HashMap<String, File> map = Configuration.getInstance().getTargetNsToFileMapping();
        File xsd = map.get(editorModel.getQName().getNamespaceURI());
        if (null != xsd) {
            try {
                PluginHelper.validateNode(editorModel.getNode(), xsd);
            } catch (Exception e) {
                a.add(new InvalidInput(editorModel.getQName().getLocalPart(), this, e.getMessage()));
            }
        }
        return a;
    }

    private class ExtendedOutline extends Outline {
        @Override
        public String getToolTipText(MouseEvent e) {
            String tip = null;
            java.awt.Point p = e.getPoint();
            int rowIndex = rowAtPoint(p);
            int colIndex = columnAtPoint(p);
            // rowIndex = getRowSorter().convertRowIndexToModel(rowIndex);
            if (colIndex == HELP_COLUMN_INDEX) {
                try {
                    tip = getInfo(rowIndex);
                } catch (RuntimeException e1) {
                    log.error(ExceptionUtils.getStackTrace(e1));
                }
            }
            return tip;
        }

        private String getInfo(int row) {
            Node node = (Node) getValueAt(row, 0);
            if (node instanceof Attr) {
                Element owner = DomHelper.getOwnerElement(node);
                QName ownerQn = DomHelper.getQName(owner);
                return Configuration.getInstance().getXsd().getAttributeAnnotation(ownerQn, node.getLocalName());
            } else if (node instanceof Element) {
                QName qName = DomHelper.getQName(node);
                return Configuration.getInstance().getXsd().getElementAnnotation(qName);
            }
            return null;
        }
    }
}
