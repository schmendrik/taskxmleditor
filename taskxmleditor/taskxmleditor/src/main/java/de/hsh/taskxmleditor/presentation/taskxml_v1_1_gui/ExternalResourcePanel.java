package de.hsh.taskxmleditor.presentation.taskxml_v1_1_gui;

import de.hsh.taskxmleditor.presentation.InputValidator;
import de.hsh.taskxmleditor.presentation.DocumentListenerAdapter;
import de.hsh.taskxmleditor.presentation.Gui;
import de.hsh.taskxmleditor.presentation.InvalidInput;
import de.hsh.taskxmleditor.presentation.OtherNamespaceElementsPanel;
import de.hsh.taskxmleditor.presentation.document_filters.InputMustNotBeEmptyHighlighter;
import de.hsh.taskxmleditor.taskxml_v1_1.ExternalResource;
import de.hsh.taskxmleditor.taskxml_v1_1.Task;
import de.hsh.taskxmleditor.utils.Str;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.ArrayList;

public class ExternalResourcePanel extends JPanel implements InputValidator {
    public final static Logger log = LoggerFactory.getLogger(ExternalResourcePanel.class);
    private static final long serialVersionUID = 1L;
    private TitledBorder titledBorder;
    private ExternalResource model;
    private Task task;
    //private JTextField desc;
    private JTextField id;
    private JTextField ref;

    public ExternalResourcePanel(Task task, ExternalResource model) {
        this.task = task;
        this.model = model;

        JTextArea desc = Gui.createTextArea();//.createTextField();
        id = Gui.createTextField();
        ref = Gui.createTextField();

        titledBorder = BorderFactory.createTitledBorder(null, "Title", TitledBorder.LEADING, TitledBorder.TOP,
                Gui.titledBorderFont, Color.BLACK);

        GridBagLayout layout = new GridBagLayout();
        JPanel textFieldsPanel = new JPanel(layout);

        JLabel refLabel = new JLabel(Str.get("ExtRes.Reference"));
        refLabel.setPreferredSize(new Dimension(100, refLabel.getPreferredSize().height));
        textFieldsPanel.add(refLabel, Gui.createGbc(0, 0, GridBagConstraints.VERTICAL));
        textFieldsPanel.add(Gui.combineWithHelp(ref, Str.get("ExtRes.Reference.Help")),
                Gui.createGbc(1, 0, GridBagConstraints.VERTICAL));

        JLabel idLabel = new JLabel(Str.get("ExtRes.ID"));
        idLabel.setPreferredSize(new Dimension(100, idLabel.getPreferredSize().height));
        textFieldsPanel.add(idLabel, Gui.createGbc(0, 1, GridBagConstraints.VERTICAL));
        textFieldsPanel.add(Gui.combineWithHelp(id, Str.get("ExtRes.ID.Help")),
                Gui.createGbc(1, 1, GridBagConstraints.VERTICAL));

        JLabel descLabel = new JLabel(Str.get("ExtRes.Description"));
        descLabel.setPreferredSize(new Dimension(100, descLabel.getPreferredSize().height));
        textFieldsPanel.add(descLabel, Gui.createGbc(0, 2, GridBagConstraints.VERTICAL));
        JScrollPane s = new JScrollPane(desc);
        s.setPreferredSize(new Dimension(400, 70));
        textFieldsPanel.add(Gui.combineWithHelp(s, Str.get("ExtRes.Description.Help")),
                Gui.createGbc(1, 2, GridBagConstraints.VERTICAL));

        OtherNamespaceElementsPanel anyElems = new OtherNamespaceElementsPanel(task, model.getAny());
        GridBagConstraints gbc = Gui.createGbc(0, 3);
        gbc.gridwidth = 2;
        textFieldsPanel.add(anyElems, gbc);

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        setBorder(titledBorder);
        add(textFieldsPanel);

        id.setText(model.getId());
        titledBorder.setTitle(model.getId());
        desc.setText(model.getDescription());
        ref.setText(model.getReference());

        id.getDocument().addDocumentListener(new DocumentListenerAdapter() {
            @Override
            public void contentChanged() {
                model.setId(id.getText());

            }
        });
        new InputMustNotBeEmptyHighlighter(id);

        desc.getDocument().addDocumentListener(new DocumentListenerAdapter() {
            @Override
            public void contentChanged() {
                model.setDescription(desc.getText());
                titledBorder.setTitle(desc.getText());
            }
        });

        ref.getDocument().addDocumentListener(new DocumentListenerAdapter() {
            @Override
            public void contentChanged() {
                model.setReference(ref.getText());
            }
        });
    }

    public JTextField getIdField() {
        return id;
    }

    public ExternalResource getModel() {
        return model;
    }

    @Override
    public java.util.List<InvalidInput> validateInput() {
        ArrayList<InvalidInput> a = new ArrayList<>();

        if (id.getText().isEmpty())
            a.add(new InvalidInput("ID", id, Str.get("ValueIsMissing")));

        return a;
    }
}
