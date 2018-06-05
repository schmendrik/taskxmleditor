package de.hsh.taskxmleditor.presentation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.util.ArrayList;

public class ChoiceDialog extends JPanel {
    public final static Logger log = LoggerFactory.getLogger(ChoiceDialog.class);


    private String[][] choices;

    private JTextArea info;

    private ArrayList<JRadioButton> buttons = new ArrayList<>();

    private ButtonGroup bg = new ButtonGroup();

    /**
     * @param choices a N*2 array with [N][0] being the choice's name,
     *                and [N][1] being the info displayed when hovering
     *                over a choice with the mouse cursor.
     */
    public ChoiceDialog(String[][] choices) {
        this.choices = choices;

        //Color bgColor =  getBackground();

        info = new JTextArea();
        info.setEditable(false);
        info.setLineWrap(true);
        info.setWrapStyleWord(true);
        Color color = new Color(222, 225, 229);
        info.setBackground(color);

        JPanel choicesPanel = new JPanel();
        choicesPanel.setBorder(new MatteBorder(0, 0, 0, 5, getBackground()));
        choicesPanel.setLayout(new BoxLayout(choicesPanel, BoxLayout.Y_AXIS));
        for (int i = 0; i < choices.length; i++) {
            JRadioButton b = new JRadioButton();
            b.setText(choices[i][0]);
            bg.add(b);
            choicesPanel.add(b);
            buttons.add(b);
            b.addMouseListener(new DisplayInfo(choices[i][1]));
        }

        JPanel infoPane = new JPanel(new BorderLayout());
        infoPane.add(info, BorderLayout.CENTER);

        JSplitPane split = new JSplitPane();
        split.setLeftComponent(choicesPanel);
        split.setRightComponent(infoPane);
        setLayout(new BorderLayout());
        add(split, BorderLayout.CENTER);
    }

    public String getSelectedChoice() {
        for (JRadioButton b : buttons) {
            if (b.isSelected())
                return b.getText();
        }
        return null;
    }

    private class DisplayInfo extends java.awt.event.MouseAdapter {
        private String helpText;

        public DisplayInfo(String helpText) {
            this.helpText = helpText;
        }

        public void mouseEntered(java.awt.event.MouseEvent evt) {
            info.setText(helpText);
        }

        public void mouseExited(java.awt.event.MouseEvent evt) {
            info.setText("");
        }
    }
}
