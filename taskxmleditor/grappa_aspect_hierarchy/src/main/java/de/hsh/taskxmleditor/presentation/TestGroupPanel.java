package de.hsh.taskxmleditor.presentation;


import de.hsh.taskxmleditor.Str;
import de.hsh.taskxmleditor.grappa_aspect_hierarchy_v0_0_1.TestElementType;
import de.hsh.taskxmleditor.grappa_aspect_hierarchy_v0_0_1.TestGroupMembersType;
import de.hsh.taskxmleditor.grappa_aspect_hierarchy_v0_0_1.TestGroupType;
import de.hsh.taskxmleditor.presentation.document_filters.InputMustNotBeEmptyHighlighter;
import de.hsh.taskxmleditor.presentation.document_filters.PositiveDoubleFilter;
import de.hsh.taskxmleditor.taskxml_v1_1.Task;
import de.hsh.taskxmleditor.taskxml_v1_1.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.ArrayList;

public class TestGroupPanel extends JPanel implements InputValidator {
    public final static Logger log = LoggerFactory.getLogger(TestGroupPanel.class);
    private AddRemoveItemsPanel items;
    private Task task;
    private TestGroupType testGroupData;
    private AutoRefreshingComboBoxForTests testRefId;

    public TestGroupPanel(Task task, TestGroupType data) throws Exception {
        this.task = task;
        this.testGroupData = data;

        JTextField scoreMax = Gui.createTextField();
        Dimension d = scoreMax.getPreferredSize();
        d.width = 300;
        scoreMax.setPreferredSize(d);

        testRefId = new AutoRefreshingComboBoxForTests(task.getTests().getTest(), id -> data.setTestrefId(id));
        d = testRefId.getPreferredSize();
        d.width = 300;
        testRefId.setPreferredSize(d);

        items = new AddRemoveItemsPanel(() -> {
            final String TEST_GROUP = "Test Group";
            final String TEST_ELEMENT = "Test Element";

            String[][] choices = {
                    {TEST_GROUP, "Test Group: Adding a Test Group will cause this dialog to pop up until a Test Element is chosen."},
                    {TEST_ELEMENT, "Test Element Help Info"}
            };
            ChoiceDialog choicesDialog = new ChoiceDialog(choices);
            choicesDialog.setPreferredSize(new Dimension(400, 80));

            int reply = JOptionPane.showConfirmDialog(null,
                    choicesDialog,
                    "Add test type",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE);

            if (reply == JOptionPane.OK_OPTION) {
                String choice = choicesDialog.getSelectedChoice();
                if (choice.equals(TEST_GROUP)) {
                    TestGroupType defaultVal = new TestGroupType();
                    defaultVal.setTestGroupMembers(new TestGroupMembersType());
                    TestGroupPanel p = new TestGroupPanel(task, defaultVal);
                    return p;
                } else if (choice.equals(TEST_ELEMENT)) {
                    TestElementType defaultVal = new TestElementType();
                    TestElementPanel p = new TestElementPanel(task, defaultVal);
                    return p;
                } else throw new Exception("Unknown choice " + choice);
            }
            return null;
        }, false);

        JPanel textFields = new JPanel(new GridBagLayout());
        textFields.add(new JLabel(Str.get("TG.ScoreMax")), Gui.createGbc(0, 0, GridBagConstraints.VERTICAL));
        textFields.add(Gui.combineWithHelp(scoreMax, Str.get("TG.ScoreMax.Help")), Gui.createGbc(1, 0, GridBagConstraints.VERTICAL));
        textFields.add(new JLabel(Str.get("TG.TestRefId")), Gui.createGbc(0, 1, GridBagConstraints.VERTICAL));
        textFields.add(Gui.combineWithHelp(testRefId, Str.get("TG.TestRefId.Help")), Gui.createGbc(1, 1, GridBagConstraints.VERTICAL));

        TitledBorder titledBorder = BorderFactory.createTitledBorder(Str.get("TG.Title"));

        if (null != data.getScoreMax())
            scoreMax.setText(data.getScoreMax().toString());
        if (null != data.getTestrefId()) {
            Test t = task.getTests().getTest().stream().filter(test -> test.getId().equals(data.getTestrefId())).findFirst().orElse(null);
            if (null != t) {
                testRefId.setSelectedItem(t);
                if(null != t.getTitle() && !t.getTitle().isEmpty())
                    titledBorder.setTitle(t.getTitle());
            }
            else {
                // this should cause a InputValidator.validateInput() error:
                testRefId.setSelectedItem(null);
            }
        }


        loadModelData(data);

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(titledBorder);
        add(textFields);
        add(items);

        items.setItemAdded(item -> {
            if (item instanceof TestGroupPanel) {
                data.getTestGroupMembers().getTestElementOrTestGroup().add(((TestGroupPanel) item).testGroupData);
            } else if (item instanceof TestElementPanel) {
                data.getTestGroupMembers().getTestElementOrTestGroup().add(((TestElementPanel) item).testElementData);
            } else throw new RuntimeException("Unknown item " + item);
        });

        items.setItemRemoved(item -> {
            if (item instanceof TestGroupPanel) {
                data.getTestGroupMembers().getTestElementOrTestGroup().remove(((TestGroupPanel) item).testGroupData);
            } else if (item instanceof TestElementPanel) {
                data.getTestGroupMembers().getTestElementOrTestGroup().remove(((TestElementPanel) item).testElementData);
            } else throw new RuntimeException("Unknown item " + item);
        });

//        items.setCanRemoveItem(item -> items.getItemCount() > 1);

        items.setCanRemoveItem(item -> {
            if (items.getItemCount() == 1) {
                JOptionPane.showMessageDialog(null, "The Test Group element must have at least one item of type Test Group or Test Element.\nPlease add another Test Group or Test Element item before removing this one.");
                return false;
            }
            return true;
        });

        scoreMax.getDocument().addDocumentListener(new DocumentListenerAdapter() {
            @Override
            public void contentChanged() {
                if (null != scoreMax.getText() && !scoreMax.getText().isEmpty())
                    data.setScoreMax(Double.parseDouble(scoreMax.getText()));
                else
                    data.setScoreMax(null);
            }
        });
        new PositiveDoubleFilter(scoreMax, true);

        testRefId.addActionListener(l -> {
            Test t = (Test) testRefId.getSelectedItem();
            data.setTestrefId(t.getId());
            if(null != t.getTitle() && !t.getTitle().isEmpty())
            titledBorder.setTitle(t.getTitle());
            revalidate();
            repaint();
        });

        new InputMustNotBeEmptyHighlighter(((JTextField) testRefId.getEditor().getEditorComponent()));

        // This is ok, but confusing to get one 'add dialog' after another when
        // you add a TestGroup
        // TODO: solve this by a special popup dialog explaining the special
        // circumstances of TestGroup or something.
        // if it's a fresh TestGroup (no pre-existing items), we
        // need force the user to add at least one item.
        if (items.getItemCount() == 0)
            items.triggerItemAddedEvent();
    }

    private void loadModelData(TestGroupType data) throws Exception {
        if (null == data.getTestGroupMembers())
            return;

        java.util.List<Object> tests = new ArrayList<>(data.getTestGroupMembers().getTestElementOrTestGroup());
        //Collections.reverse(tests);
        for (Object obj : tests) {
            if (obj instanceof TestElementType) {
                TestElementType testElement = (TestElementType) obj;
                items.addItem(new TestElementPanel(task, testElement));
            } else if (obj instanceof TestGroupType) {
                TestGroupType testGroup = (TestGroupType) obj;
                items.addItem(new TestGroupPanel(task, testGroup));
            } else log.error("unknown type " + obj);
        }
    }

    @Override
    public java.util.List<InvalidInput> validateInput() {
        ArrayList<InvalidInput> a = new ArrayList<>();

        if (null == testRefId.getSelectedItem() || testRefId.getSelectedItem().equals(""))
            a.add(new InvalidInput("TestGroup", testRefId, "Value is missing."));
        else {
            String id = ((Test) testRefId.getSelectedItem()).getId();

            if (!task.getTests().getTest().stream().anyMatch(t -> t.getId().equals(id)))
                a.add(new InvalidInput("TestGroup", testRefId, String.format("Reference ID '%s' does not exist (refer to the Tests tab)",
                        id)));

            for (Component item : items.getItems()) {
                if (item instanceof TestGroupPanel)
                    a.addAll(((TestGroupPanel) item).validateInput());
                else if (item instanceof TestElementPanel)
                    a.addAll(((TestElementPanel) item).validateInput());


            }
        }

        return a;
    }


}
