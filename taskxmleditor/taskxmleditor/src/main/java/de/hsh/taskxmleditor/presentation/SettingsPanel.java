package de.hsh.taskxmleditor.presentation;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class SettingsPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    public SettingsPanel() {
        JTabbedPane tabs = new JTabbedPane();
        tabs.setBorder(new EmptyBorder(5, 5, 5, 5));
        tabs.addTab("General", new JPanel());
        tabs.addTab("XSD Files", new XsdFilesSettingsPanel());

        setLayout(new BorderLayout());
        add(tabs, BorderLayout.CENTER);
    }
}
