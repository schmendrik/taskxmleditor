package de.hsh.taskxmleditor.presentation.taskxml_v1_1_gui;

import de.hsh.taskxmleditor.presentation.InputValidator;
import de.hsh.taskxmleditor.presentation.DocumentListenerAdapter;
import de.hsh.taskxmleditor.presentation.Gui;
import de.hsh.taskxmleditor.presentation.InvalidInput;
import de.hsh.taskxmleditor.presentation.document_filters.InputMustNotBeEmptyHighlighter;
import de.hsh.taskxmleditor.taskxml_v1_1.Proglang;
import de.hsh.taskxmleditor.utils.Str;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

public class ProgLangPanel extends JPanel implements InputValidator {
    public final static Logger log = LoggerFactory.getLogger(ProgLangPanel.class);
    private JComboBox<String> progLangName;
    private JTextField progLangVersion;
    private Proglang proglang;

    public ProgLangPanel(Proglang proglang) {
        this.proglang = proglang;

        String langList = Str.get("ProgLangList");
        progLangName = new JComboBox<>(Arrays.asList(langList.split("\\s*,\\s*")).toArray(new String[0]));
        progLangName.setEditable(true);

        GridBagLayout layout = new GridBagLayout();
        // layout.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 1.0 };
        setLayout(layout);
        setBorder(Gui.emptyBorder);
        add(new JLabel(Str.get("Task.ProgLangName")), Gui.createGbc(0, 0, GridBagConstraints.VERTICAL));
        add(Gui.combineWithHelp(progLangName, Str.get("Task.ProgLangName.Help")), Gui.createGbc(1, 0, GridBagConstraints.VERTICAL));
        add(new JLabel(Str.get("Task.ProgLangVersion")), Gui.createGbc(0, 1, GridBagConstraints.VERTICAL));
        progLangVersion = Gui.createTextField(100);
        add(Gui.combineWithHelp(progLangVersion, Str.get("Task.ProgLangVersion.Help")),
                Gui.createGbc(1, 1, GridBagConstraints.VERTICAL));

        progLangVersion.setText(proglang.getVersion());
        progLangName.setToolTipText(proglang.getValue());

        progLangVersion.getDocument().addDocumentListener(new DocumentListenerAdapter() {
            @Override
            public void contentChanged() {
                proglang.setVersion(progLangVersion.getText());
            }
        });
        new InputMustNotBeEmptyHighlighter(progLangVersion);

        progLangName.addActionListener(l -> proglang.setValue(progLangName.getSelectedItem().toString()));
        new InputMustNotBeEmptyHighlighter((JTextField) progLangName.getEditor().getEditorComponent());
    }

    @Override
    public java.util.List<InvalidInput> validateInput() {
        ArrayList<InvalidInput> a = new ArrayList<>();

        if(progLangVersion.getText().isEmpty())
            a.add(new InvalidInput("ProgLang Version", progLangVersion, Str.get("ValueIsMissing")));
        if(null == progLangName.getSelectedItem() || progLangName.getSelectedItem().toString().isEmpty())
            a.add(new InvalidInput("ProgLang Name", progLangName, Str.get("ValueIsMissing")));

        return a;
    }
}
