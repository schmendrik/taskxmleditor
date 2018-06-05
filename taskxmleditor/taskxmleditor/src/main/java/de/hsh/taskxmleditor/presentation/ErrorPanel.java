package de.hsh.taskxmleditor.presentation;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;

public class ErrorPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    public ErrorPanel(String error) {
        setBorder(new EtchedBorder());
        JTextArea a = new JTextArea();
        a.setEditable(false);
        a.setText(error);
        JScrollPane p = Gui.createScrollPane(a);
        setLayout(new BorderLayout());
        add(p, BorderLayout.CENTER);
    }
}
