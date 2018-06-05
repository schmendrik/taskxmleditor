package de.hsh.taskxmleditor.presentation;

import de.hsh.taskxmleditor.Str;
import de.hsh.taskxmleditor.grappa_aspect_hierarchy_v0_0_1.TestElementType;
import de.hsh.taskxmleditor.presentation.document_filters.InputMustNotBeEmptyHighlighter;
import de.hsh.taskxmleditor.presentation.document_filters.PositiveDoubleFilter;
import de.hsh.taskxmleditor.taskxml_v1_1.Task;
import de.hsh.taskxmleditor.taskxml_v1_1.Test;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.ArrayList;

    public class TestElementPanel extends JPanel implements InputValidator {
        public TestElementType testElementData;
        private AutoRefreshingComboBoxForTests testRefId;
        private Task task;

        public TestElementPanel(Task task, TestElementType data) throws Exception {
            this.task = task;
            this.testElementData = data;

            JTextField scoreMax = Gui.createTextField();
            Dimension d = scoreMax.getPreferredSize();
            d.width = 300;
            scoreMax.setPreferredSize(d);

            testRefId = new AutoRefreshingComboBoxForTests(task.getTests().getTest(), id -> data.setTestrefId(id));
            d = testRefId.getPreferredSize();
            d.width = 300;
            testRefId.setPreferredSize(d);

            setLayout(new GridBagLayout());
            add(new JLabel(Str.get("TE.ScoreMax")), Gui.createGbc(0, 0, GridBagConstraints.VERTICAL));
            add(Gui.combineWithHelp(scoreMax, Str.get("TE.ScoreMax.Help")), Gui.createGbc(1, 0, GridBagConstraints.VERTICAL));
            add(new JLabel(Str.get("TE.TestRefId")), Gui.createGbc(0, 1, GridBagConstraints.VERTICAL));
            add(Gui.combineWithHelp(testRefId, Str.get("TE.TestRefId.Help")), Gui.createGbc(1, 1, GridBagConstraints.VERTICAL));

            TitledBorder titledBorder = BorderFactory.createTitledBorder(Str.get("TE.Title"));
            setBorder(titledBorder);

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
        }

        @Override
        public java.util.List<InvalidInput> validateInput() {
            ArrayList<InvalidInput> a = new ArrayList<>();

            if(null == testRefId.getSelectedItem() || testRefId.getSelectedItem().equals(""))
                a.add(new InvalidInput("TestElement", testRefId, "Value is missing."));
            else {
                String id = ((Test) testRefId.getSelectedItem()).getId();
                if (!task.getTests().getTest().stream().anyMatch(t -> t.getId().equals(id)))
                    a.add(new InvalidInput("TestElement", testRefId, String.format("Reference ID '%s' does not exist (refer to the Tests tab)",
                            id)));
            }
            return a;
        }
    }


//public class TestElementPanel extends JPanel implements InputValidator {
//    public final static Logger log = LoggerFactory.getLogger(TestElementPanel.class);
//    private JTextField testRefId;
//    private Task task;
//
//    public TestElementPanel(Task task, TestGroupType data) throws Exception {
//        this.task = task;
//        JTextField scoreMax = Gui.createTextField();
//        testRefId = Gui.createTextField();
//
//        AddRemoveItemsPanel items = new AddRemoveItemsPanel(() -> {
//            return null;
//        }, false);
//
//        setBorder(BorderFactory.createTitledBorder("Test Group"));
//        setLayout(new GridBagLayout());
//        add(new JLabel("Score Max:"), Gui.createGbc(0, 1, GridBagConstraints.VERTICAL));
//        add(Gui.combineWithHelp(scoreMax, ""), Gui.createGbc(1, 1, GridBagConstraints.VERTICAL));
//        add(new JLabel("Test-Ref ID:"), Gui.createGbc(0, 2, GridBagConstraints.VERTICAL));
//        add(Gui.combineWithHelp(testRefId, ""), Gui.createGbc(1, 2, GridBagConstraints.VERTICAL));
//        GridBagConstraints gbc = Gui.createGbc(0, 3, GridBagConstraints.BOTH);
//        gbc.gridwidth = 2;
//        add(items, gbc);
//
//        if (null != data.getScoreMax())
//            scoreMax.setText(data.getScoreMax().toString());
//        if (null != data.getTestrefId())
//            testRefId.setText(data.getTestrefId());
//
//        scoreMax.getDocument().addDocumentListener(new DocumentListenerAdapter() {
//            @Override
//            public void contentChanged() {
//                if (null != scoreMax.getText() && !scoreMax.getText().isEmpty())
//                    data.setScoreMax(Double.parseDouble(scoreMax.getText()));
//                else
//                    data.setScoreMax(null);
//            }
//        });
//        new PositiveDoubleFilter(scoreMax, true);
//
//        testRefId.getDocument().addDocumentListener(new DocumentListenerAdapter() {
//            @Override
//            public void contentChanged() {
//                data.setTestrefId((testRefId.getText()));
//            }
//        });
//        new InputMustNotBeEmptyHighlighter(testRefId);
//    }
//}
