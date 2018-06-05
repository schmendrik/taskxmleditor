package de.hsh.taskxmleditor.presentation;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class AddRemoveReadOnlyStrings extends JPanel {
    private static final long serialVersionUID = -5465438914484655325L;
    private DefaultTableModel model;
    private JTable table;
    private Callable<List<String>> getValuesToSelect;

    public AddRemoveReadOnlyStrings(String title, Callable<List<String>> getValuesToSelect) {
        this.getValuesToSelect = getValuesToSelect;
        model = new DefaultTableModel(new Object[]{title}, 0);
        table = new JTable(model);
        table.setTableHeader(null);
        table.setShowGrid(false);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollTable = Gui.createScrollPane(table);

        JButton add = new JButton("Add");
        // TODO: Show a list of file ids that can be selected...

        JButton remove = new JButton("Remove");
        remove.addActionListener(l -> removeSelectedString());
        JButton edit = new JButton("Edit");
        edit.addActionListener(
                l -> JOptionPane.showMessageDialog(this, "TODO...\nJust double-click an item to edit ..."));

        JPanel buttons = new JPanel();
        buttons.setLayout(new BoxLayout(buttons, BoxLayout.X_AXIS));
        buttons.add(add);
        buttons.add(remove);
        buttons.add(edit);

        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder(title));

        add(scrollTable, BorderLayout.CENTER);
        add(buttons, BorderLayout.SOUTH);

        Dimension prefSize = getPreferredSize();
        prefSize.height = 300;
        setPreferredSize(prefSize);
    }

    public void addString(String str) {
        model.addRow(new Object[]{str});
    }

    public void removeSelectedString() {
        if (-1 != table.getSelectedRow())
            model.removeRow(table.getSelectedRow());
    }

    public String[] getStrings() {
        int rows = model.getRowCount();
        ArrayList<String> list = new ArrayList<>(rows);
        for (int i = 0; i < rows; i++)
            list.add(model.getValueAt(i, 0).toString());
        return list.toArray(new String[rows]);
    }
}
