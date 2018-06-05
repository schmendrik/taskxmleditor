package de.hsh.taskxmleditor.presentation;

import javax.swing.*;

public class FatalErrorWindow extends JFrame  {
    public FatalErrorWindow(JPanel error) {
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setContentPane(error);
    }

    public void display() {
        SwingUtilities.invokeLater(() -> {
            setLocationRelativeTo(null);
            setVisible(true);
        });
    }
}
