package de.hsh.taskxmleditor.presentation.taskxml_v1_1_gui;

import de.hsh.taskxmleditor.presentation.InputValidator;
import de.hsh.taskxmleditor.presentation.Gui;
import de.hsh.taskxmleditor.presentation.InvalidInput;
import de.hsh.taskxmleditor.taskxml_v1_1.ExternalResource;
import de.hsh.taskxmleditor.taskxml_v1_1.Externalresourceref;
import de.hsh.taskxmleditor.taskxml_v1_1.Externalresourcerefs;
import de.hsh.taskxmleditor.taskxml_v1_1.Task;
import de.hsh.taskxmleditor.utils.Str;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;


/**
 * This can be used for displaying and editing file references for both
 * normal file references and external references.
 */
public class ExternalResourceReferencesPanel extends JPanel implements InputValidator {
    final static Logger log = LoggerFactory.getLogger(ExternalResourceReferencesPanel.class);
    private DefaultTableModel model;
    private JTable table;
    private Task task;
    private Externalresourcerefs refs;

    private static final int ID_COLUMN_INDEX = 0;

    public ExternalResourceReferencesPanel(Task task, Externalresourcerefs refs, String title, String helpText) {
        this.task = task;
        this.refs = refs;
        //this.allRefs = allRefs;

        model = new DefaultTableModel(new Object[]{title, "Info"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(model);
        table.setTableHeader(null);
        table.setShowGrid(false);
        table.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);

        JScrollPane scrollTable = Gui.createScrollPane(table);

        JButton add = new JButton(Str.get("ExtRes.Ref.Add"));
        add.addActionListener(l -> addReferences());
        JButton remove = new JButton(Str.get("ExtRes.Ref.Remove"));
        remove.addActionListener(l -> removeRefFromModel());
        JPanel buttons = new JPanel();
        buttons.setLayout(new BoxLayout(buttons, BoxLayout.X_AXIS));
        buttons.add(add);
        buttons.add(remove);
        buttons.add(Box.createHorizontalGlue());
        JLabel help = new JLabel();
        help.setIcon(new ImageIcon(Gui.helpIcon2424));
        help.setToolTipText(helpText);
        buttons.add(help);

        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder(title));
        add(scrollTable, BorderLayout.CENTER);
        add(buttons, BorderLayout.SOUTH);

        if (null != refs) {
            for (Externalresourceref r : refs.getExternalresourceref()) {
                //File f = findFile(r.getRefid());
                ExternalResource res = findExtResource(r.getRefid());
                if(null != res) {
                    model.addRow(new Object[]{r.getRefid(), res.getReference()});
                }
                else {
                    model.addRow(new Object[]{r.getRefid(), "This ID does not exist"});
                    log.warn("An external resource reference (id '{}') does not actually exist.", r.getRefid());
                }
            }
        }
    }

    public JTable getTable() {
        return table;
    }

    private void addReferences() {
        Object[] columnNames = {Str.get("ExtRes.Ref.Select"), Str.get("ExtRes.Ref.HeaderName"), "Info"};

        ArrayList<Object[]> data = new ArrayList<>();
        for (ExternalResource ext : task.getExternalResources().getExternalResource()) {
            boolean selectable = true;
            for (int i = 0; i < model.getRowCount(); i++) {
                if (model.getValueAt(i, 0).toString().equals(ext.getId())) {
                    selectable = false;
                    break;
                }
            }
            if (selectable) {
                String info = ext.getReference();
                data.add(new Object[]{false, ext.getId(), info});
            }
        }

        Object[][] rowValues = new Object[data.size()][];
        for (int i = 0; i < data.size(); i++) {
            rowValues[i] = data.get(i);
        }

        DefaultTableModel model = new DefaultTableModel(rowValues, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 0; // only first column (checkbox) is editable
            }
        };

        JTable addRefsTable = new JTable(model) {
            @Override
            public Class getColumnClass(int column) {
                switch (column) {
                    case 0:
                        return Boolean.class;
                    case 1:
                        return String.class;
                    case 2:
                        return String.class;
                }
                return String.class;
            }
        };
        addRefsTable.setPreferredScrollableViewportSize(addRefsTable.getPreferredSize());
        addRefsTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        Gui.resizeJTableColumnWidth(addRefsTable);
        JScrollPane addRefsTablePane = new JScrollPane(addRefsTable);
        addRefsTablePane.setPreferredSize(new Dimension(400, 400));
        addRefsTable.setTableHeader(null);

        JComponent display;
        if (addRefsTable.getRowCount() > 0)
            display = addRefsTablePane;
        else
            display = new JLabel(Str.get("ExtRes.Ref.NoMoreLeft"));

        int reply = JOptionPane.showConfirmDialog(null, display,
                Str.get("ExtRes.Ref.AddTitle"), JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);

        if (reply == JOptionPane.OK_OPTION) {
            for (int i = 0; i < addRefsTable.getRowCount(); i++) {
                boolean isChecked = (boolean) addRefsTable.getValueAt(i, 0);
                String id = (String) addRefsTable.getValueAt(i, 1);
                if (isChecked) {
                    ExternalResource res = findExtResource(id);
                    addRefToModel(res);
                }
            }

            revalidate();
            repaint();
        }
    }

    private ExternalResource findExtResource(String id) {
        for (ExternalResource externalResource : task.getExternalResources().getExternalResource()) {
            if(externalResource.getId().equals(id))
                return externalResource;
        }
        return null;
    }

    /**
     * Adds a new file reference to both the Jtable and the Task model.
     *
     * @param file
     */
    private void addRefToModel(ExternalResource res) {
        model.addRow(new Object[]{res.getId(), res.getReference()});
        Externalresourceref r = new Externalresourceref();
        r.setRefid(res.getId());
        refs.getExternalresourceref().add(r);
    }

    private void removeRefFromModel() {
        ArrayList<String> idsToRemove = new ArrayList<>();
        Arrays.stream(table.getSelectedRows()).forEach(row -> {
            String id = (String) table.getValueAt(row, ID_COLUMN_INDEX);
            idsToRemove.add(id);
        });

        for(String idToRemove : idsToRemove) {
            for(int i=0; i<model.getRowCount(); i++) {
                String id = (String) table.getValueAt(i, ID_COLUMN_INDEX);
                if(id.equals(idToRemove)) {
                    model.removeRow(i);
                    // remove from task model too
                    for (Externalresourceref ref : refs.getExternalresourceref()) {
                        if (ref.getRefid().equals(id)) {
                            refs.getExternalresourceref().remove(ref);
                            break;
                        }
                    }
                    break;
                }
            }
        }
    }

    public String[] getExtReferenceStrings() {
        int rows = model.getRowCount();
        ArrayList<String> list = new ArrayList<>(rows);
        for (int i = 0; i < rows; i++)
            list.add(model.getValueAt(i, 0).toString());
        return list.toArray(new String[rows]);
    }

    @Override
    public java.util.List<InvalidInput> validateInput() {
        ArrayList<InvalidInput> a = new ArrayList<>();

        String[] ids = this.getExtReferenceStrings();

        // using the table as the component doesn't make the balloon pop up for some reason,
        // so i'll use the jpanel itself

        for (String id : ids) {
            if(!task.getExternalResources().getExternalResource().stream().anyMatch(f -> f.getId().equals(id)))
                a.add(new InvalidInput("ExternalReference", this, String.format("Resource ID '%s' does not exist (refer to the External Resources tab)", id)));
        }

        return a;
    }
}


