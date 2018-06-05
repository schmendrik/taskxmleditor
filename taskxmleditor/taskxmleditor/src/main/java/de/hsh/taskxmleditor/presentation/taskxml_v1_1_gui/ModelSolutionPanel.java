package de.hsh.taskxmleditor.presentation.taskxml_v1_1_gui;

import de.hsh.taskxmleditor.presentation.InputValidator;
import de.hsh.taskxmleditor.presentation.DocumentListenerAdapter;
import de.hsh.taskxmleditor.presentation.Gui;
import de.hsh.taskxmleditor.presentation.InvalidInput;
import de.hsh.taskxmleditor.presentation.document_filters.InputMustNotBeEmptyHighlighter;
import de.hsh.taskxmleditor.taskxml_v1_1.Filerefs;
import de.hsh.taskxmleditor.taskxml_v1_1.ModelSolution;
import de.hsh.taskxmleditor.taskxml_v1_1.Task;
import de.hsh.taskxmleditor.utils.Str;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.ArrayList;

public class ModelSolutionPanel extends JPanel implements InputValidator {
    private ModelSolution model;
    private TitledBorder titledBorder;
    private JTextField id;
    private FileReferencesPanel refs;

    public ModelSolutionPanel(Task task, ModelSolution modelSolution) {
        this.model = modelSolution;

        if(null == modelSolution.getFilerefs())
            modelSolution.setFilerefs(new Filerefs());

        id = Gui.createTextField();
        //JTextField comment = Gui.createTextField();
        JTextArea comment = Gui.createTextArea();


        refs = new FileReferencesPanel(task, modelSolution.getFilerefs(), "File References", Str.get("ModSol.FileRefs.Help"), true);
        Dimension prefSize = refs.getPreferredSize();
        prefSize.height = 240;
        refs.setPreferredSize(prefSize);

        titledBorder = BorderFactory.createTitledBorder(null, Str.get("ModSol.Name"), TitledBorder.LEADING, TitledBorder.TOP,
                Gui.titledBorderFont, Color.BLACK);
        setBorder(titledBorder);
        setLayout(new GridBagLayout());
        JLabel idLbl = new JLabel(Str.get("ModSol.ID"));
        idLbl.setPreferredSize(new Dimension(100, idLbl.getPreferredSize().height));
        add(idLbl, Gui.createGbc(0, 0, GridBagConstraints.VERTICAL));
        add(Gui.combineWithHelp(id, Str.get("ModSol.ID.Help")), Gui.createGbc(1, 0, GridBagConstraints.VERTICAL));
        add(new JLabel(Str.get("ModSol.Comment")), Gui.createGbc(0, 1, GridBagConstraints.VERTICAL));
        JScrollPane s = new JScrollPane(comment);
        s.setPreferredSize(new Dimension(400, 70));
        add(Gui.combineWithHelp(s, Str.get("ModSol.Comment.Help")), Gui.createGbc(1, 1, GridBagConstraints.VERTICAL));
        add(new JLabel(Str.get("ModSol.FileRefs")), Gui.createGbc(0, 2, GridBagConstraints.NONE));
        add(refs, Gui.createGbc(1, 2, GridBagConstraints.BOTH));

        id.setText(model.getId());
        if(null != model.getId())
            titledBorder.setTitle(model.getId());
        comment.setText(model.getComment());

        id.getDocument().addDocumentListener(new DocumentListenerAdapter() {
            @Override
            public void contentChanged() {
                model.setId(id.getText());
                titledBorder.setTitle(id.getText());
                repaint();
            }
        });
        new InputMustNotBeEmptyHighlighter(id);

        comment.getDocument().addDocumentListener(new DocumentListenerAdapter() {
            @Override
            public void contentChanged() {
                model.setComment(comment.getText());
            }
        });
    }

    public ModelSolution getModelSolution() {
        return model;
    }

    @Override
    public java.util.List<InvalidInput> validateInput() {
        ArrayList<InvalidInput> a = new ArrayList<>();

        if(id.getText().isEmpty())
            a.add(new InvalidInput("ID", id, Str.get("ValueIsMissing")));

        a.addAll(refs.validateInput());

        return a;
    }
}
