package de.hsh.taskxmleditor.presentation.taskxml_v1_1_gui;

import de.hsh.taskxmleditor.application.CreateTaskxmlDefaults;
import de.hsh.taskxmleditor.presentation.*;
import de.hsh.taskxmleditor.presentation.document_filters.InputMustNotBeEmptyHighlighter;
import de.hsh.taskxmleditor.presentation.document_filters.MinMaxDoubleFilter;
import de.hsh.taskxmleditor.presentation.document_filters.PositiveIntegerFilter;
import de.hsh.taskxmleditor.taskxml_v1_1.*;
import de.hsh.taskxmleditor.utils.Str;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;

public class TestPanel extends JPanel implements InputValidator {
    public final static Logger log = LoggerFactory.getLogger(TestPanel.class);
    private TitledBorder titledBorder;
    private Test model;
//    private TestConfigPanel testConfig;
    private JTextField title;
    private JTextField testType;
    private JTextField id;
    private JTextField validity;
    private JTextField timeout;
    private ExternalResourceReferencesPanel extRefs;
    private FileReferencesPanel fileRefs;
    private TestMetaData testMetaData;

    public TestPanel(Task task, Test model) {
        this.model = model;

        // for new tests, create some defaults
        if (null == model.getTestConfiguration())
            model.setTestConfiguration(CreateTaskxmlDefaults.createTestConfiguration());
        if(null == model.getTestConfiguration().getFilerefs())
            model.getTestConfiguration().setFilerefs(new Filerefs());
        if(null == model.getTestConfiguration().getExternalresourcerefs())
            model.getTestConfiguration().setExternalresourcerefs(new Externalresourcerefs());

        titledBorder = BorderFactory.createTitledBorder(null, "Title", TitledBorder.LEADING, TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 24), Color.BLACK);

        title = Gui.createTextField();
        title.getDocument().addDocumentListener(new DocumentListenerAdapter() {
            @Override
            public void contentChanged() {
                titledBorder.setTitle(title.getText());
                revalidate();
                repaint();
            }
        });

        testType = Gui.createTextField();
        id = Gui.createTextField();
        validity = Gui.createTextField();
        timeout = Gui.createTextField();

        testMetaData = model.getTestConfiguration().getTestMetaData();
        if(null == testMetaData)
            testMetaData = new TestMetaData();
        OtherNamespaceElementsPanel testMetaDataNamespaces = new OtherNamespaceElementsPanel(task, testMetaData.getAny(),
                Str.get("Test.MetaDataName"));
        testMetaDataNamespaces.setItemCountChangedListener(itemCount -> {
            if(itemCount > 0) {
                model.getTestConfiguration().setTestMetaData(testMetaData);
                System.out.println("add tmd");
            }
            else {
                model.getTestConfiguration().setTestMetaData(null);
                System.out.println("rem tmd");
            }
        });

        OtherNamespaceElementsPanel others = new OtherNamespaceElementsPanel(task, model.getTestConfiguration().getAny());

        GridBagLayout layout = new GridBagLayout();
        // FlowLayout layout = new FlowLayout();
        JPanel textFieldsPanel = new JPanel(layout);
        // textFieldsPanel.setBackground(Color.yellow);
        // layout.rowWeights = new double[] { 0.0, 0.0, 0.0, 1.0 };
        // controlsPanel.setBorder(Gui.emptyBorder);
        // setBorder(Gui.emptyBorder);
        JLabel title = new JLabel(Str.get("Test.Title"));
        title.setPreferredSize(new Dimension(70, title.getPreferredSize().height));
        textFieldsPanel.add(title, Gui.createGbc(0, 0, GridBagConstraints.VERTICAL));
        textFieldsPanel.add(Gui.combineWithHelp(this.title, Str.get("Test.Title.Help")),
                Gui.createGbc(1, 0, GridBagConstraints.VERTICAL));
        textFieldsPanel.add(new JLabel(Str.get("Test.ID")), Gui.createGbc(0, 1, GridBagConstraints.VERTICAL));
        textFieldsPanel.add(Gui.combineWithHelp(id, Str.get("Test.ID.Help")), Gui.createGbc(1, 1, GridBagConstraints.VERTICAL));
        textFieldsPanel.add(new JLabel(Str.get("Test.TestType")), Gui.createGbc(0, 2, GridBagConstraints.VERTICAL));
        textFieldsPanel.add(Gui.combineWithHelp(testType, Str.get("Test.TestType.Help")),
                Gui.createGbc(1, 2, GridBagConstraints.VERTICAL));
        textFieldsPanel.add(new JLabel(Str.get("Test.Validity")), Gui.createGbc(0, 3, GridBagConstraints.VERTICAL));
        textFieldsPanel.add(Gui.combineWithHelp(validity, Str.get("Test.Validity.Help")),
                Gui.createGbc(1, 3, GridBagConstraints.VERTICAL));
        // "Timeout" is part of test-configuration, actually
        // I put it here due to "limited" panel space
        // (it looks better this way)
        textFieldsPanel.add(new JLabel(Str.get("Test.Timeout")), Gui.createGbc(0, 4, GridBagConstraints.VERTICAL));
        textFieldsPanel.add(Gui.combineWithHelp(timeout, Str.get("Test.Timeout.Help")),
                Gui.createGbc(1, 4, GridBagConstraints.VERTICAL));

//        // textFieldsPanel.add(new JLabel(Str.get("GUI.Test.TestConfig")),
//        // Gui.createGbc(0, 4, GridBagConstraints.VERTICAL));
//        GridBagConstraints gbc = Gui.createGbc(3, 0, GridBagConstraints.VERTICAL);
//        gbc.gridheight = 5;
//        textFieldsPanel.add(testConfig, gbc);
//
//        JPanel testConfigGroup = new JPanel(new BorderLayout());
//        // testConfigGroup.setBorder(BorderFactory.createTitledBorder("TestConfig"));
//        testConfigGroup.add(testConfig, BorderLayout.CENTER);

        JPanel referencesPanel = new JPanel(new FlowLayout());
        fileRefs = new FileReferencesPanel(task, model.getTestConfiguration().getFilerefs(), Str.get("Test.FileRefs"), Str.get("Test.FileRefs.Help"), false);
        extRefs = new ExternalResourceReferencesPanel(task, model.getTestConfiguration().getExternalresourcerefs(), Str.get("Test.ExtResRefs"), Str.get("Test.ExtResRefs.Help"));

        Dimension prefSize = fileRefs.getPreferredSize();
        prefSize.height = 240;
        fileRefs.setPreferredSize(prefSize);
        extRefs.setPreferredSize(prefSize);
        //fileRefs.setPreferredSize(new Dimension(200, 200));
        //Gui.resizeJTableColumnWidth(fileRefs.getTable());
        //Gui.resizeJTableColumnWidth(extRefs);
        referencesPanel.add(fileRefs);
        referencesPanel.add(extRefs);

        JPanel besideOneAnother = new JPanel(new FlowLayout());
        besideOneAnother.add(textFieldsPanel);
        besideOneAnother.add(referencesPanel);

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(titledBorder);
        add(besideOneAnother);
        add(testMetaDataNamespaces);
        add(others);

        this.title.setText(model.getTitle());
        titledBorder.setTitle(model.getTitle());
        testType.setText(model.getTestType());
        id.setText(model.getId());
        if (null != model.getTestConfiguration().getTimeout())
            timeout.setText(model.getTestConfiguration().getTimeout().toString());
        if(null != model.getValidity()) {
            if(model.getValidity().compareTo(new BigDecimal(1.0)) == 0)
                // dont want a "1". people need to know they can
                // put decimals in right away. also, keep it consistent with the
                // rest of the loaded "1.0"s
                validity.setText("1.0");
            else
                validity.setText(model.getValidity().toString());
        }
        if(fileRefs.getFileReferenceCount() == 0)
            model.getTestConfiguration().setFilerefs(null);

        this.title.getDocument().addDocumentListener(new DocumentListenerAdapter() {
            @Override
            public void contentChanged() {
                model.setTitle(TestPanel.this.title.getText());
                titledBorder.setTitle(TestPanel.this.title.getText());
                revalidate();
                repaint();
            }
        });
        new InputMustNotBeEmptyHighlighter(this.title);

        testType.getDocument().addDocumentListener(new DocumentListenerAdapter() {
            @Override
            public void contentChanged() {
                model.setTestType(testType.getText());
            }
        });
        new InputMustNotBeEmptyHighlighter(testType);

        id.getDocument().addDocumentListener(new DocumentListenerAdapter() {
            @Override
            public void contentChanged() {
                model.setId(id.getText());
            }
        });
        new InputMustNotBeEmptyHighlighter(id);

        validity.getDocument().addDocumentListener(new DocumentListenerAdapter() {
            @Override
            public void contentChanged() {
                    if(!validity.getText().isEmpty())
                        model.setValidity(BigDecimal.valueOf(Double.parseDouble(validity.getText())));
                    else
                        model.setValidity(null);
            }
        });
        new MinMaxDoubleFilter(validity, true, 0.0, 1.0);

        timeout.getDocument().addDocumentListener(new DocumentListenerAdapter() {
            @Override
            public void contentChanged() {
                    if(!timeout.getText().isEmpty())
                        model.getTestConfiguration().setTimeout(BigInteger.valueOf(Integer.parseInt(timeout.getText())));
                    else
                        model.getTestConfiguration().setTimeout(null);
            }
        });
        new PositiveIntegerFilter(timeout, true);

        fileRefs.setFileRefCountChanged(refCount -> {
            if(refCount > 0)
                model.getTestConfiguration().setFilerefs(fileRefs.getModel());
            else
                model.getTestConfiguration().setFilerefs(null);
        });
    }

    public Test getModel() {
        return model;
    }

    public JTextField getTestId() {
        return id;
    }

    @Override
    public java.util.List<InvalidInput> validateInput() {
        ArrayList<InvalidInput> a = new ArrayList<>();

        if(id.getText().isEmpty())
            a.add(new InvalidInput("ID", id, Str.get("ValueIsMissing")));

        if(title.getText().isEmpty())
            a.add(new InvalidInput("Title", title, Str.get("ValueIsMissing")));

        if(testType.getText().isEmpty())
            a.add(new InvalidInput("TestType", testType, Str.get("ValueIsMissing")));

        a.addAll(fileRefs.validateInput());
        a.addAll(extRefs.validateInput());

        return a;
    }
}
