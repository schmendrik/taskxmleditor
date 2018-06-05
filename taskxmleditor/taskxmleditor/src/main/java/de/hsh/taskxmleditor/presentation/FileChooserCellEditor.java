package de.hsh.taskxmleditor.presentation;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.io.File;

public class FileChooserCellEditor extends DefaultCellEditor implements TableCellEditor {
    private static final int CLICK_COUNT_TO_START = 2;
    private JButton button;
    private JFileChooser fileChooser;
    private String file = "";

    public FileChooserCellEditor() {
        super(new JTextField());
        setClickCountToStart(CLICK_COUNT_TO_START);

        button = new JButton();
        button.setBackground(Color.white);
        button.setFont(button.getFont().deriveFont(Font.PLAIN));
        button.setBorder(null);

        fileChooser = new JFileChooser();
    }

    @Override
    public Object getCellEditorValue() {
        return file;
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        file = value.toString();
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                fileChooser.setSelectedFile(new File(file));
                if (fileChooser.showOpenDialog(button) == JFileChooser.APPROVE_OPTION) {
                    file = fileChooser.getSelectedFile().getAbsolutePath();
                }
                fireEditingStopped();
            }
        });
        button.setText(file);
        return button;
    }
}