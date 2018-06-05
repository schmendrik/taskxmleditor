package de.hsh.taskxmleditor.presentation.taskxml_v1_1_gui;

import de.hsh.taskxmleditor.application.CreateTaskxmlDefaults;
import de.hsh.taskxmleditor.presentation.InputValidator;
import de.hsh.taskxmleditor.presentation.Gui;
import de.hsh.taskxmleditor.presentation.InvalidInput;
import de.hsh.taskxmleditor.taskxml_v1_1.*;
import de.hsh.taskxmleditor.utils.Str;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class SubmRestrictionsPanel extends JPanel implements InputValidator {
    public final static Logger log = LoggerFactory.getLogger(SubmRestrictionsPanel.class);
    private static final long serialVersionUID = 1L;
    private SubmissionRestrictions model;
    private JComboBox<String> chooseRestType;
    private ArchiveRestrictionPanel archiveRestr;
    private FileRestrictionPanel filesRestr;
    private FileRegexpRestrictionPanel regexpRestr;

    public SubmRestrictionsPanel(Task task, SubmissionRestrictions model) {
        this.model = model;

        CardLayout layout = new CardLayout();
        JPanel cards = new JPanel(layout);

        JPanel comboBoxPane = new JPanel();
        comboBoxPane.setAlignmentX(Component.LEFT_ALIGNMENT);
        String comboBoxItems[] = {ArchiveRestrictionPanel.KEY_NAME, FileRestrictionPanel.KEY_NAME,
                FileRegexpRestrictionPanel.KEY_NAME};
        chooseRestType = new JComboBox<>(comboBoxItems);
        chooseRestType.setEditable(false);
        chooseRestType.addItemListener(e -> {
            CardLayout cl = (CardLayout) (cards.getLayout());
            cl.show(cards, (String) e.getItem());
            // binding to model data happens in another item listener further below
        });
        comboBoxPane.add(chooseRestType);

        String showCard = ArchiveRestrictionPanel.KEY_NAME; // some default value, doesn't really matter which one
        if (null != model.getRestrictionObject() && model.getRestrictionObject() instanceof ArchiveRestrType) {
            archiveRestr = new ArchiveRestrictionPanel(task, (ArchiveRestrType) model.getRestrictionObject());
            showCard = ArchiveRestrictionPanel.KEY_NAME;
        } else
            archiveRestr = new ArchiveRestrictionPanel(task, CreateTaskxmlDefaults.createArchiveRestrType());

        if (null != model.getRestrictionObject() && model.getRestrictionObject() instanceof FileRestrType) {
            filesRestr = new FileRestrictionPanel(task, (FileRestrType) model.getRestrictionObject());
            showCard = FileRestrictionPanel.KEY_NAME;
        } else
            filesRestr = new FileRestrictionPanel(task, new FileRestrType());

        if (null != model.getRestrictionObject() && model.getRestrictionObject() instanceof FileRegexpRestrType) {
            regexpRestr = new FileRegexpRestrictionPanel(task, (FileRegexpRestrType) model.getRestrictionObject());
            chooseRestType.setSelectedItem(FileRegexpRestrictionPanel.KEY_NAME);
            showCard = FileRegexpRestrictionPanel.KEY_NAME;
        } else
            regexpRestr = new FileRegexpRestrictionPanel(task, new FileRegexpRestrType());

        cards.add(filesRestr, FileRestrictionPanel.KEY_NAME);
        cards.add(archiveRestr, ArchiveRestrictionPanel.KEY_NAME);
        cards.add(regexpRestr, FileRegexpRestrictionPanel.KEY_NAME);

        // this needs to happen after the cards.add(*) calls have been
        // done, so that show() "overwrites" the panel displayed by the
        // last add() call
        chooseRestType.setSelectedItem(showCard);
        layout.show(cards, showCard);

        FlowLayout layout1 = new FlowLayout(FlowLayout.LEFT);
        layout.setVgap(0);
        layout.setHgap(0);
        JPanel besideOneAnother = new JPanel(layout1);
        besideOneAnother.add(new JLabel(Str.get("Restr.ChooseRestrType")));
        besideOneAnother.add(Box.createRigidArea(new Dimension(3, 0)));
        besideOneAnother.add(Gui.combineWithHelp(comboBoxPane, Str.get("Restr.ChooseRestrType.Help")));

        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBorder(Gui.emptyBorder);
        p.add(besideOneAnother);
        p.add(cards);

        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder(Str.get("Restr.TabName")));
        add(p, BorderLayout.CENTER);

        chooseRestType.addItemListener(e -> {
            if (e.getItem().toString().equals(ArchiveRestrictionPanel.KEY_NAME)) {
                model.setRestrictionObject(archiveRestr.getModel());
            } else if (e.getItem().toString().equals(FileRestrictionPanel.KEY_NAME)) {
                model.setRestrictionObject(filesRestr.getModel());
            } else if (e.getItem().toString().equals(FileRegexpRestrictionPanel.KEY_NAME)) {
                model.setRestrictionObject(regexpRestr.getModel());
            }
        });
    }

    @Override
    public java.util.List<InvalidInput> validateInput() {
        ArrayList<InvalidInput> a = new ArrayList<>();

        if (this.chooseRestType.getSelectedItem().toString().equals(ArchiveRestrictionPanel.KEY_NAME)) {
            a.addAll(archiveRestr.validateInput());
        } else if (this.chooseRestType.getSelectedItem().toString().equals(FileRestrictionPanel.KEY_NAME)) {
            a.addAll(filesRestr.validateInput());
        } else if (this.chooseRestType.getSelectedItem().toString().equals(FileRegexpRestrictionPanel.KEY_NAME)) {
            a.addAll(regexpRestr.validateInput());
        }

        return a;
    }
}
