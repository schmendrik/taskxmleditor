package de.hsh.taskxmleditor.presentation.taskxml_v1_1_gui;

import de.hsh.taskxmleditor.presentation.InputValidator;
import de.hsh.taskxmleditor.presentation.DocumentListenerAdapter;
import de.hsh.taskxmleditor.presentation.Gui;
import de.hsh.taskxmleditor.presentation.InvalidInput;
import de.hsh.taskxmleditor.presentation.TextInputWithHtmlPreviewPanel;
import de.hsh.taskxmleditor.presentation.document_filters.InputMustNotBeEmptyHighlighter;
import de.hsh.taskxmleditor.taskxml_v1_1.Task;
import de.hsh.taskxmleditor.utils.Str;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class TaskPanel extends JPanel implements InputValidator {
    private Task task;
    private TextInputWithHtmlPreviewPanel htmlEditor;
    private final int VPADDING = 20;
    private final JTextField uuid;
    private final JComboBox<String> lang;
    private final ProgLangPanel progLangPanel;

    public TaskPanel(Task task) {
        this.task = task;

        JPanel descPanel = new JPanel();
        descPanel.setLayout(new BoxLayout(descPanel, BoxLayout.Y_AXIS));
        descPanel.setBorder(BorderFactory.createTitledBorder(Str.get("Task.TaskDescription")));
        htmlEditor = new TextInputWithHtmlPreviewPanel();
        htmlEditor.setText(task.getDescription());
        htmlEditor.addChangeListener(new DocumentListenerAdapter() {
            @Override
            public void contentChanged() {
                task.setDescription(htmlEditor.getText());
            }
        });
        descPanel.add(htmlEditor);

        JPanel langPanel = new JPanel(new GridLayout(1, 1));
        langPanel.setBorder(BorderFactory.createTitledBorder(Str.get("Task.ProgLangName")));
        progLangPanel = new ProgLangPanel(task.getProglang());
        langPanel.add(progLangPanel);

        // uuid, parent-uuid (opt.), lang
        uuid = Gui.createTextField();
        JTextField parentUuid = Gui.createTextField();
        // TODO: The content of the “lang” attribute must comply with the IETF BCP 47, RFC 4647 and ISO 639-1:2002 standards.
        // https://www.ietf.org/rfc/rfc4647.txt
//        JTextField lang = Gui.createTextField();
        String langList = Str.get("NaturalLangList");
        lang = new JComboBox<>(Arrays.asList(langList.split("\\s*,\\s*")).toArray(new String[0]));
        lang.setEditable(true);


        JPanel taskAttributesPanel = new JPanel();
        GridBagLayout layout = new GridBagLayout();
        taskAttributesPanel.setLayout(layout);
        taskAttributesPanel.setBorder(BorderFactory.createTitledBorder(Str.get("Task.TaskAttributes")));
        taskAttributesPanel.add(new JLabel(Str.get("Task.UUID")), Gui.createGbc(0, 0, GridBagConstraints.VERTICAL));
        taskAttributesPanel.add(Gui.combineWithHelp(uuid, Str.get("Task.UUID.Help")), Gui.createGbc(1, 0, GridBagConstraints.VERTICAL));
        taskAttributesPanel.add(new JLabel(Str.get("Task.ParentUUID")), Gui.createGbc(0, 1, GridBagConstraints.VERTICAL));
        taskAttributesPanel.add(Gui.combineWithHelp(parentUuid, Str.get("Task.ParentUUID.Help")), Gui.createGbc(1, 1, GridBagConstraints.VERTICAL));
        taskAttributesPanel.add(new JLabel(Str.get("Task.Lang")), Gui.createGbc(0, 2, GridBagConstraints.VERTICAL));
        taskAttributesPanel.add(Gui.combineWithHelp(lang, Str.get("Task.Lang.Help")), Gui.createGbc(1, 2, GridBagConstraints.VERTICAL));

        JPanel footPanel = new JPanel();
        footPanel.setLayout(new BoxLayout(footPanel, BoxLayout.Y_AXIS));
        footPanel.add(Box.createRigidArea(new Dimension(0, VPADDING)));
        footPanel.add(langPanel);
        footPanel.add(Box.createRigidArea(new Dimension(0, VPADDING)));
        footPanel.add(taskAttributesPanel);

        setLayout(new BorderLayout());
        add(taskAttributesPanel, BorderLayout.NORTH);
        add(htmlEditor, BorderLayout.CENTER);
        add(langPanel, BorderLayout.SOUTH);

        uuid.setText(task.getUuid());
        parentUuid.setText(task.getParentUuid());
        lang.setSelectedItem(task.getLang());

        uuid.getDocument().addDocumentListener(new DocumentListenerAdapter() {
            @Override
            public void contentChanged() {
                task.setUuid(uuid.getText());
            }
        });
        new InputMustNotBeEmptyHighlighter(uuid);

        parentUuid.getDocument().addDocumentListener(new DocumentListenerAdapter() {
            @Override
            public void contentChanged() {
                if(!parentUuid.getText().isEmpty())
                    task.setParentUuid(parentUuid.getText());
                else
                    task.setParentUuid(null);
            }
        });

        lang.addItemListener(e -> task.setLang(e.getItem().toString()));
        new InputMustNotBeEmptyHighlighter(((JTextField) lang.getEditor().getEditorComponent()));

//        lang.getDocument().addDocumentListener(new DocumentListenerAdapter() {
//            @Override
//            public void contentChanged() {
//                task.setLang(lang.getText());
//            }
//        });
//        new InputMustNotBeEmptyHighlighter(lang);
    }

    @Override
    public List<InvalidInput> validateInput() {
        ArrayList<InvalidInput> a = new ArrayList<>();

        if(uuid.getText().trim().isEmpty())
            a.add(new InvalidInput("UUID", uuid, Str.get("ValueIsMissing")));
        if(htmlEditor.getText().trim().isEmpty())
            //a.add(new InvalidInput("TaskDescription", htmlEditor.getTextArea() /* Balloon is not popping up */, Str.get("ValueIsMissing")));
            a.add(new InvalidInput("TaskDescription", htmlEditor, Str.get("ValueIsMissing")));
        if(null == lang.getSelectedItem() || lang.getSelectedItem().toString().isEmpty())
            a.add(new InvalidInput("Language", lang, Str.get("ValueIsMissing")));

        a.addAll(progLangPanel.validateInput());

        return a;
    }
}
