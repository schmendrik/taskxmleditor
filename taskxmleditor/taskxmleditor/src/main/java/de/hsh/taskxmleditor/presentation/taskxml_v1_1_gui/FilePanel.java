package de.hsh.taskxmleditor.presentation.taskxml_v1_1_gui;

import de.hsh.taskxmleditor.application.Configuration;
import de.hsh.taskxmleditor.presentation.InputValidator;
import de.hsh.taskxmleditor.presentation.DocumentListenerAdapter;
import de.hsh.taskxmleditor.presentation.Gui;
import de.hsh.taskxmleditor.presentation.InvalidInput;
import de.hsh.taskxmleditor.presentation.document_filters.InputMustNotBeEmptyHighlighter;
import de.hsh.taskxmleditor.taskxml_v1_1.File;
import de.hsh.taskxmleditor.utils.FileOperations;
import de.hsh.taskxmleditor.utils.Str;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.xmlbeans.SchemaProperty;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlAnySimpleType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileView;
import javax.xml.namespace.QName;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.io.FileNotFoundException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;

public class FilePanel extends JPanel implements InputValidator {
    public final static Logger log = LoggerFactory.getLogger(FilePanel.class);
    private java.io.File file;
    private TitledBorder titledBorder;
    private File model;
    private JTextField filePath;
    private JTextField id;
    private JTextField fileName;
    private JTextArea comment;
    private JComboBox<FileType> type;
    private JComboBox<String> clazz;
    private JTextArea fileContent;
    private JScrollPane fileContentScrollPane;

    private JLabel pathLabel = new JLabel(Str.get("File.FilePath"));
    private JLabel fileNameLabel = new JLabel(Str.get("File.FileName"));

    private JPanel pathPanel = new JPanel();
    private JPanel fileNamePanel = new JPanel();

    private JButton openInEditorBtn;
    private JButton reloadFileContentBtn;

    private JPanel cannotDisplayFileContentsPanel;

    public enum FileType {
        file, embedded
    }

    public File getModel() {
        return model;
    }


    public FilePanel(File model) {
        this.model = model;

        titledBorder = BorderFactory.createTitledBorder(null, "", TitledBorder.LEADING, TitledBorder.TOP,
                Gui.titledBorderFont, Color.BLACK);

        filePath = Gui.createTextField();
        filePath.setEnabled(false);

        JButton openFileBtn = new JButton(Str.get("File.SelectFile"));
        openFileBtn.addActionListener(l -> {
//            JFileChooser fileChooser = ApplicationWindow.fileChooser;

            final java.io.File lockDir = Configuration.getInstance().getTaskXmlFile().getParentFile();
            JFileChooser fc = new JFileChooser(lockDir);
            fc.setFileView(new FileView() {
                @Override
                public Boolean isTraversable(java.io.File f) {
                    return f.toPath().startsWith(lockDir.toPath());
                }
            });
            if (fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                file = fc.getSelectedFile();
                filePath.setText(FileOperations.getFilePathRelativeToTaskxml(file));
                updateGui();
            }


        });

        id = Gui.createTextField();

        fileName = Gui.createTextField();

        comment = Gui.createTextArea();

        // TODO: pull from xsd
        clazz = createComboBoxForTheClassAttribute();

        type = new JComboBox<>(FileType.values());

        filePath = Gui.createTextField();
        filePath.setEnabled(false);

        fileName = Gui.createTextField();

        GridBagLayout layout = new GridBagLayout();
        JPanel textFieldsPanel = new JPanel(layout);

        JLabel typeLabel = new JLabel(Str.get("File.FileType"));
        typeLabel.setPreferredSize(new Dimension(100, typeLabel.getPreferredSize().height));
        textFieldsPanel.add(typeLabel, Gui.createGbc(0, 0, GridBagConstraints.VERTICAL));
        textFieldsPanel.add(Gui.combineWithHelp(type, Str.get("File.FileType.Help")),
                Gui.createGbc(1, 0, GridBagConstraints.VERTICAL));
        textFieldsPanel.add(pathLabel, Gui.createGbc(0, 1, GridBagConstraints.VERTICAL));

        FlowLayout flow1 = new FlowLayout();
        flow1.setVgap(0);
        flow1.setHgap(0);
        pathPanel.setLayout(flow1);
        pathPanel.add(filePath);
        pathPanel.add(Box.createRigidArea(new Dimension(3, 0)));
        pathPanel.add(Gui.combineWithHelp(openFileBtn, Str.get("File.FilePath.Help")));
        textFieldsPanel.add(pathPanel, Gui.createGbc(1, 1, GridBagConstraints.VERTICAL));

        fileNamePanel.add(Gui.combineWithHelp(fileName, Str.get("File.FileName.Help")));
        textFieldsPanel.add(fileNameLabel, Gui.createGbc(0, 1, GridBagConstraints.VERTICAL));
        textFieldsPanel.add(fileNamePanel, Gui.createGbc(1, 1, GridBagConstraints.VERTICAL));

        textFieldsPanel.add(new JLabel(Str.get("File.ID")), Gui.createGbc(0, 2, GridBagConstraints.VERTICAL));
        textFieldsPanel.add(Gui.combineWithHelp(id, Str.get("File.ID.Help")), Gui.createGbc(1, 2, GridBagConstraints.VERTICAL));

        textFieldsPanel.add(new JLabel(Str.get("File.Comment")), Gui.createGbc(0, 3, GridBagConstraints.VERTICAL));
        JScrollPane s = new JScrollPane(comment);
        s.setPreferredSize(new Dimension(400, 70));
        textFieldsPanel.add(Gui.combineWithHelp(s, Str.get("File.Comment.Help")),
                Gui.createGbc(1, 3, GridBagConstraints.VERTICAL));

        textFieldsPanel.add(new JLabel(Str.get("File.Class")), Gui.createGbc(0, 4, GridBagConstraints.VERTICAL));
        textFieldsPanel.add(Gui.combineWithHelp(clazz, Str.get("File.Class.Help")),
                Gui.createGbc(1, 4, GridBagConstraints.VERTICAL));

        fileContent = Gui.createTextArea(); // new JTextArea();
        fileContent.setLineWrap(true);
        fileContent.setWrapStyleWord(true);

        fileContent.setFont(new Font("monospaced", Font.PLAIN, 12));
        fileContentScrollPane = Gui.createScrollPane(fileContent);
        Dimension d = new Dimension(800, fileContent.getPreferredSize().height);
        fileContentScrollPane.setPreferredSize(d);
        GridBagConstraints gbc = Gui.createGbc(2, 0, GridBagConstraints.BOTH);
        // TODO: if you re-introduce editin buttons, put them below fileContent and
        // make the fileContent's gridheight the current height minus 1.
        gbc.gridheight = 4;
        gbc.weightx = gbc.weighty = 1.0;
        textFieldsPanel.add(fileContentScrollPane, gbc);
        cannotDisplayFileContentsPanel = new JPanel(new BorderLayout());
        cannotDisplayFileContentsPanel.setPreferredSize(d);
        cannotDisplayFileContentsPanel.setVisible(false);
        // cannotDisplayFileContentsPanel.setBackground(Color.green);
        JLabel cannotLabel = new JLabel(Str.get("File.CannotDisplayContent"));
        cannotLabel.setHorizontalAlignment(SwingConstants.CENTER);
        cannotLabel.setVerticalAlignment(SwingConstants.CENTER);
        cannotDisplayFileContentsPanel.add(cannotLabel, BorderLayout.CENTER);
        textFieldsPanel.add(cannotDisplayFileContentsPanel, gbc);

        // TODO: disable these for now, no editing of external files in v1.0
        openInEditorBtn = new JButton("Edit");
        openInEditorBtn.addActionListener(l -> {
            try {
                java.awt.Desktop.getDesktop().edit(file);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, e.getMessage());
            }
        });
        reloadFileContentBtn = new JButton("Reload");
        reloadFileContentBtn.addActionListener(l -> {
            try {
                fileContent.setText(readFileContents());
                fileContent.setCaretPosition(0);
            } catch (Exception e) {
                log.error(ExceptionUtils.getStackTrace(e));
                JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        JPanel besideOneAnother = new JPanel(new FlowLayout());
        besideOneAnother.add(reloadFileContentBtn);
        besideOneAnother.add(openInEditorBtn);
        gbc = Gui.createGbc(2, 4);
        gbc.anchor = GridBagConstraints.NORTHEAST;
        textFieldsPanel.add(besideOneAnother, gbc);

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(titledBorder);
        add(textFieldsPanel);

        initialLoadTaskData();

        type.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                onFileTypeChanged((FileType) e.getItem());
                log.debug("selected: " + e.getItem());
                // type.setSelectedItem(FileType.embedded);
            } else if (e.getStateChange() == ItemEvent.DESELECTED) {
                // test((FileType) e.getItem(), (FileType)
                // type.getSelectedItem());
                // log.debug("deselected: " + e.getItem());
            }
        });

        // this needs to happen AFTER the initial loading of task data
        // type.addItemListener(e -> test((FileType) e.getItem(), (FileType)
        // type.getSelectedItem()));

        filePath.getDocument().addDocumentListener(new DocumentListenerAdapter() {
            @Override
            public void contentChanged() {
                model.setValue(filePath.getText());
            }
        });

        id.getDocument().addDocumentListener(new DocumentListenerAdapter() {
            @Override
            public void contentChanged() {
                model.setId(id.getText());
            }
        });
        new InputMustNotBeEmptyHighlighter(id);

        fileName.getDocument().addDocumentListener(new DocumentListenerAdapter() {
            @Override
            public void contentChanged() {
                model.setFilename(fileName.getText());
                titledBorder.setTitle(fileName.getText());
                revalidate();
                repaint();
            }
        });

        comment.getDocument().addDocumentListener(new DocumentListenerAdapter() {
            @Override
            public void contentChanged() {
                model.setComment(comment.getText());
            }
        });

        fileContent.getDocument().addDocumentListener(new DocumentListenerAdapter() {
            @Override
            public void contentChanged() {
                fileContentEdited();
            }
        });

        clazz.addActionListener(l -> model.setClazz(clazz.getSelectedItem().toString()));

        type.addActionListener(l -> model.setType(type.getSelectedItem().toString()));

        updateGui();
    }

//    private void resetBorder() {
//        // didnt get this to work quite yet, its unimportant anyway
//        LineBorder red = new LineBorder(Color.RED);
//        titledBorder = BorderFactory.createTitledBorder(red, titledBorder.getTitle(),
//                TitledBorder.LEADING, TitledBorder.TOP,
//                Gui.titledBorderFont, Color.RED);
//        setBorder(titledBorder);
//        repaint();
//    }

    private boolean reverting = false;

    private void onFileTypeChanged(FileType newType) {
        if (reverting) {
            reverting = false;
            return;
        }

        // change from embedded to file:
        if (newType.equals(FileType.file)) {
            // if it has actual embedded content, save it to the new file
            // don't trim the content
            if (null != model.getValue() && !model.getValue().isEmpty()) {
                int reply = JOptionPane.showConfirmDialog(null,
                        Str.get("File.SaveEmbeddedToAnotherFileMsg"), Str.get("File.SaveEmbeddedToAnotherFileTitle"),
                        JOptionPane.YES_NO_OPTION);
                if (reply == JOptionPane.YES_OPTION) {

                    final java.io.File lockDir = Configuration.getInstance().getTaskXmlFile().getParentFile();
                    JFileChooser fc = new JFileChooser(lockDir);
                    fc.setFileView(new FileView() {
                        @Override
                        public Boolean isTraversable(java.io.File f) {
                            return f.toPath().startsWith(lockDir.toPath());
                        }
                    });

                    if (null != model.getFilename() && !model.getFilename().isEmpty())
                        fc.setSelectedFile(Paths.get(lockDir.getAbsolutePath(), model.getFilename()).toFile());

                    if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                        java.io.File saveFile = fc.getSelectedFile();
                        log.info("Saving to file: " + saveFile.getAbsolutePath());

                        String encoding = "utf-8";
                        try {
                            encoding = Configuration.getInstance().getTaskXmlFileEncoding();
                        } catch (Exception e1) {
                            log.error(ExceptionUtils.getStackTrace(e1));
                        }

                        try {
                            FileOperations.saveToFile(model.getValue(), saveFile, encoding);

                            model.setFilename(FileOperations.getFilePathRelativeToTaskxml(saveFile));
                            model.setValue(model.getFilename());
                            model.setType(FileType.file.toString());

                            //setFile(saveFile);
                            file = saveFile;
                            updateGui();
                            updateTitledBorder();
                            filePath.setText(FileOperations.getFilePathRelativeToTaskxml(saveFile));
                        } catch (Exception e1) {
                            log.error(ExceptionUtils.getStackTrace(e1));
                        }
                    }
                }
                // set it back to embedded without any changes
                else {
                    reverting = true;
                    type.setSelectedItem(FileType.embedded);
                }
            }
        }
        // change from file to embedded
        else if (newType.equals(FileType.embedded)) {
            log.debug("changing file type from file to embedded");
            int reply = JOptionPane.showConfirmDialog(null,
                    Str.get("File.EmbedFileMsg"), Str.get("File.EmbedFileTitle"),
                    JOptionPane.YES_NO_OPTION);
            if (reply == JOptionPane.YES_OPTION) {
                String encoding = "UTF-8";
                try {
                    encoding = FileOperations.detectEncoding(file);
                } catch (Exception e) {
                }

                try {
                    String content = FileOperations.readFromFile(file, encoding);
                    log.debug("read file content: " + content);
                    model.setValue(content);
                    model.setFilename(file.getName());
                    fileName.setText(model.getFilename());
                    file = null;
                } catch (Exception e) {
                    log.error(ExceptionUtils.getStackTrace(e));
                    JOptionPane.showMessageDialog(null, "File cannot be embedded. Error: " + ExceptionUtils.getMessage(e), "Error", JOptionPane.ERROR_MESSAGE);
                    reverting = true;
                    type.setSelectedItem(FileType.file); // revert back
                }
            } else {
                reverting = true;
                type.setSelectedItem(FileType.file); // revert back
            }
        }

        updateGui();
    }

    private void updateTitledBorder() {
        if (type.getSelectedItem().equals(FileType.embedded))
            titledBorder.setTitle(fileName.getText());
        else
            titledBorder.setTitle(filePath.getText());

    }

    private void updateGui() {
        boolean isEmbedded = type.getSelectedItem().equals(FileType.embedded);

        if (isEmbedded) {
            pathLabel.setVisible(false);
            pathPanel.setVisible(false);
            fileNameLabel.setVisible(true);
            fileNamePanel.setVisible(true);
            fileContent.setEnabled(true);
            fileContent.setText(model.getValue());
            fileContent.setCaretPosition(0);
            openInEditorBtn.setVisible(false);
            reloadFileContentBtn.setVisible(false);
        } else { // is external file
            pathLabel.setVisible(true);
            pathPanel.setVisible(true);
            fileNameLabel.setVisible(false);
            fileNamePanel.setVisible(false);
            fileContent.setEnabled(false);

            // load file content
            String extFileContent = "";
            try {
                extFileContent = readFileContents();
            } catch (FileNotFoundException e) {
                log.warn(ExceptionUtils.getStackTrace(e));
                // highlight error
//                resetBorder();
            } catch (Exception e) {
                log.error(ExceptionUtils.getStackTrace(e));
            }

            if (null != extFileContent) {
                fileContent.setText(extFileContent);
                fileContent.setCaretPosition(0);
                fileContentScrollPane.setVisible(true);
                openInEditorBtn.setVisible(true);
                reloadFileContentBtn.setVisible(true);
                cannotDisplayFileContentsPanel.setVisible(false);
            } else {
                fileContent.setText("");
                fileContentScrollPane.setVisible(false);
                cannotDisplayFileContentsPanel.setVisible(true);
                openInEditorBtn.setVisible(false);
                reloadFileContentBtn.setVisible(false);
            }
        }

        updateTitledBorder();

        revalidate();
        repaint();
    }

    /**
     * load initial task.xml data
     */
    private void initialLoadTaskData() {
        id.setText(model.getId());
        filePath.setText(model.getValue());
        comment.setText(model.getComment());
        clazz.setSelectedItem(model.getClazz());

        // file type is embedded by default if not specified otherwise in the
        // task.xml
        FileType fileType = FileType.embedded;
        if (null != model.getType())
            fileType = FileType.valueOf(model.getType());
        type.setSelectedItem(fileType);

        if (null != model.getFilename())
            fileName.setText(model.getFilename());

        if (null != model.getValue() && !model.getValue().isEmpty()) {
            java.io.File file = new java.io.File(FileOperations.getTaskXmlDirectoryPath(), model.getValue());
            this.file = file;
            updateGui();
        }
    }

    /**
     * (I need the component for validation purposes)
     *
     * @return
     */
    public JTextField getFileIdField() {
        return id;
    }


    private void fileContentEdited() {
        if (type.getSelectedItem().equals(FileType.embedded)) {
            model.setValue(fileContent.getText());
        } else if (type.getSelectedItem().equals(FileType.file)) {
            // write to file or something
        }
    }

//    private void setFile(java.io.File f) {
//        file = f;
//
//        try {
//            readFileContents();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }


    private String readFileContents() throws Exception {
        if (null == file)
            throw new Exception("file has not been initialized");

        String fileContentStr = null;
        if (type.getSelectedItem().equals(FileType.embedded)) {
            fileContentStr = model.getValue();
        } else if (type.getSelectedItem().equals(FileType.file)) {
            boolean isPlainText = false;
            try {
                isPlainText = FileOperations.isPlainText(file);
            } catch (Exception e) {
                log.warn(ExceptionUtils.getStackTrace(e));
            }

            if (isPlainText) {
                String encoding = "utf-8";
                try {
                    encoding = FileOperations.detectEncoding(file);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                fileContentStr = FileOperations.readFromFile(file, encoding);
            }
        }

        return fileContentStr;
    }

    public String getFileId() {
        return id.getText();
    }

    public String getFileName() {
        return fileName.getName();
    }

    private JComboBox<String> createComboBoxForTheClassAttribute() {
        try {
            Configuration config = Configuration.getInstance();
            QName fileQn = new QName(Configuration.TASK_XSD_TARGET_NAMESPACE, "file");
            SchemaProperty classProp = config.getXsd().getAttributeInfo(fileQn, "class");
            SchemaType typeInfo = classProp.getType();
            XmlAnySimpleType[] enumVals = typeInfo.getEnumerationValues();

            if (null == enumVals)
                throw new Exception("typeInfo.getEnumerationValues() returned null for the class attribute");

            JComboBox<String> combo = new JComboBox<>();
            Arrays.stream(enumVals).forEach(enumVal -> combo.addItem(enumVal.getStringValue()));
            //combo.setSelectedItem(which one?);
            return combo;
        } catch (Exception e) {
            log.error(ExceptionUtils.getStackTrace(e));
        }

        // fill statically...
        JComboBox<String> combo = new JComboBox<>();
        combo.addItem("template");
        combo.addItem("library");
        combo.addItem("inputdata");
        combo.addItem("instruction");
        combo.addItem("internal-library");
        combo.addItem("internal");
        return combo;
    }

    @Override
    public java.util.List<InvalidInput> validateInput() {
        ArrayList<InvalidInput> a = new ArrayList<>();

        if (id.getText().isEmpty())
            a.add(new InvalidInput("File", id, Str.get("ValueIsMissing")));

        // class and and type are restricted to the combo box's enum list, no need to check that

        if (type.getSelectedItem().equals(FileType.file)) {
            if (null == file)
                a.add(new InvalidInput("File", filePath, "File is not set", InvalidInput.ErrorType.Warning));
            else if (!file.exists())
                a.add(new InvalidInput("File", filePath, "File path does not exist", InvalidInput.ErrorType.Warning));
        }

        return a;
    }
}
