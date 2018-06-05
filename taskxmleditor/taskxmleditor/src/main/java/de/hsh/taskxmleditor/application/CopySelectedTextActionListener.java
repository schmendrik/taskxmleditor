package de.hsh.taskxmleditor.application;

import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CopySelectedTextActionListener implements ActionListener {
    private JTextComponent c;

    public CopySelectedTextActionListener(JTextComponent c) {
        this.c = c;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        StringSelection entry = new StringSelection(c.getSelectedText());
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(entry, entry);
    }
}
