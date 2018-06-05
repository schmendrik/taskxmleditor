package de.hsh.taskxmleditor.presentation;

import de.hsh.taskxmleditor.application.Configuration;

import javax.swing.*;
import java.awt.*;

public class AboutDialog extends JPanel {
    public AboutDialog() {
        JLabel appName = new JLabel("Taskxml Editor");
        JLabel version = new JLabel("Version 1.0.2");
        JLabel proforma = new JLabel("Supported ProFormA Namespace:");
        JLabel pfVersions = new JLabel(Configuration.TASK_XSD_TARGET_NAMESPACE);


        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        add(appName);
        add(version);
        add(Box.createRigidArea(new Dimension(0, 20)));
        add(proforma);
        add(pfVersions);
    }
}
