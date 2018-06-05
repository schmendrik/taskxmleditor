package de.hsh.taskxmleditor.presentation.taskxml_v1_1_gui;

import de.hsh.taskxmleditor.presentation.InputValidator;
import de.hsh.taskxmleditor.presentation.DocumentListenerAdapter;
import de.hsh.taskxmleditor.presentation.Gui;
import de.hsh.taskxmleditor.presentation.InvalidInput;
import de.hsh.taskxmleditor.presentation.OtherNamespaceElementsPanel;
import de.hsh.taskxmleditor.presentation.document_filters.InputMustNotBeEmptyHighlighter;
import de.hsh.taskxmleditor.taskxml_v1_1.MetaData;
import de.hsh.taskxmleditor.taskxml_v1_1.Task;
import de.hsh.taskxmleditor.utils.Str;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class MetaDataPanel extends JPanel implements InputValidator {
    public final static Logger log = LoggerFactory.getLogger(MetaDataPanel.class);
    private JTextField title;

    public MetaDataPanel(Task task, MetaData model) {
        OtherNamespaceElementsPanel other = new OtherNamespaceElementsPanel(task, model.getAny());
        title = Gui.createTextField();
        JPanel alignBesideOneAnother = new JPanel(new FlowLayout(FlowLayout.LEFT));
        alignBesideOneAnother.add(new JLabel(Str.get("MetaData.Title")));
        alignBesideOneAnother.add(Gui.combineWithHelp(title, Str.get("MetaData.Title.Help")));
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder(Str.get("MetaData.Name")));
        add(alignBesideOneAnother, BorderLayout.NORTH);
        JScrollPane scroller = Gui.createScrollPane(other);
        add(scroller, BorderLayout.CENTER);

        title.setText(model.getTitle());

        title.getDocument().addDocumentListener(new DocumentListenerAdapter() {
            @Override
            public void contentChanged() {
                model.setTitle(title.getText());
            }
        });
        new InputMustNotBeEmptyHighlighter(title);
    }

    @Override
    public java.util.List<InvalidInput> validateInput() {
        ArrayList<InvalidInput> a = new ArrayList<>();

        if(title.getText().isEmpty())
            a.add(new InvalidInput("MetaData Title", title, Str.get("ValueIsMissing")));

        return a;
    }
}
