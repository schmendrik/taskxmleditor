package de.hsh.taskxmleditor.presentation.taskxml_v1_1_gui;

import de.hsh.taskxmleditor.presentation.InputValidator;
import de.hsh.taskxmleditor.presentation.*;
import de.hsh.taskxmleditor.presentation.document_filters.InputMustNotBeEmptyHighlighter;
import de.hsh.taskxmleditor.presentation.document_filters.PositiveIntegerFilter;
import de.hsh.taskxmleditor.taskxml_v1_1.FileRestrType;
import de.hsh.taskxmleditor.taskxml_v1_1.Task;
import de.hsh.taskxmleditor.utils.Str;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.math.BigInteger;
import java.util.ArrayList;

public class FileRestrictionPanel extends JPanel implements InputValidator {
    public final static Logger log = LoggerFactory.getLogger(ArchiveRestrictionPanel.class);
    public static final String KEY_NAME = "File Restriction";
    private FileRestrType model;
    private AddRemoveItemsPanel items;

    public FileRestrType getModel() {
        return model;
    }

    public FileRestrictionPanel(Task task, FileRestrType model) {
        this.model = model;


        items = new AddRemoveItemsPanel(() -> {
            final String REQUIRED = "Required";
            final String OPTIONAL = "Optional";

            String[][] choices = {
                    {REQUIRED, Str.get("Restr.File.RequiredInfo")},
                    {OPTIONAL, Str.get("Restr.File.OptionalInfo")}
            };
            ChoiceDialog choicesDialog = new ChoiceDialog(choices);
            choicesDialog.setPreferredSize(new Dimension(400, 80));

            int reply = JOptionPane.showConfirmDialog(items,
                    choicesDialog,
                    Str.get("Restr.File.AddNewRestr"),
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE);

            if (reply == JOptionPane.OK_OPTION) {
                ReqOptPanel a = null;
                String choice = choicesDialog.getSelectedChoice();
                if (choice.equals(REQUIRED)) {
                    FileRestrType.Required r = new FileRestrType.Required();
                    a = new ReqOptPanel(r);
                } else if (choice.equals(OPTIONAL)) {
                    FileRestrType.Optional o = new FileRestrType.Optional();
                    a = new ReqOptPanel(o);
                } else throw new Exception("Unknown choice " + choice);
                return a;
            }
            return null;
        }, true);

        items.setItemAdded(item -> {
            ReqOptPanel a = (ReqOptPanel) item;
            model.getRequiredOrOptional().add(a.getRestrictionObject());
        });

        items.setItemRemoved(item -> {
            ReqOptPanel a = (ReqOptPanel) item;
            model.getRequiredOrOptional().remove(a.getRestrictionObject());
        });

        setLayout(new BorderLayout());
        JScrollPane scroller = Gui.createScrollPane(items);
        scroller.setBorder(null);
        add(scroller, BorderLayout.CENTER);
    }

    @Override
    public java.util.List<InvalidInput> validateInput() {
        ArrayList<InvalidInput> a = new ArrayList<>();

        if (items.getItems().size() < 1)
            a.add(new InvalidInput("FileRestrType", items, "At least one item is required"));

        for (Component item : items.getItems()) {
            ReqOptPanel p = (ReqOptPanel) item;
            a.addAll(p.validateInput());
        }

        return a;
    }

    public class ReqOptPanel extends JPanel implements InputValidator {
        private Object model;
        private JTextField fileName;

        public Object getRestrictionObject() {
            return model;
        }

        public ReqOptPanel(Object model) {
            this.model = model;

            JTextField mime = Gui.createTextField();
            JTextField maxSize = Gui.createTextField();
            fileName = Gui.createTextField();

            GridBagLayout layout = new GridBagLayout();
            setLayout(layout);
            setBorder(BorderFactory.createTitledBorder("Required"));
            add(new JLabel(Str.get("Restr.MimeTypeRegexp")), Gui.createGbc(0, 0, GridBagConstraints.VERTICAL));
            add(Gui.combineWithHelp(mime, Str.get("Restr.MimeTypeRegexp.Help")), Gui.createGbc(1, 0, GridBagConstraints.VERTICAL));
            add(new JLabel(Str.get("Restr.MaxSize")), Gui.createGbc(0, 1, GridBagConstraints.VERTICAL));
            add(Gui.combineWithHelp(maxSize, Str.get("Restr.MaxSize.Help")), Gui.createGbc(1, 1, GridBagConstraints.VERTICAL));
            add(new JLabel(Str.get("Restr.File.FileName")), Gui.createGbc(0, 2, GridBagConstraints.VERTICAL));
            add(Gui.combineWithHelp(fileName, Str.get("Restr.File.FileName.Help")), Gui.createGbc(1, 2, GridBagConstraints.VERTICAL));

            String mimeText;
            if (model instanceof FileRestrType.Required) {
                FileRestrType.Required mod = (FileRestrType.Required) model;
                mime.setText(mod.getMimeTypeRegexp());
                if (null != mod.getMaxSize())
                    maxSize.setText(mod.getMaxSize().toString());
                fileName.setText(mod.getFilename());
            } else if (model instanceof FileRestrType.Optional) {
                FileRestrType.Optional mod = (FileRestrType.Optional) model;
                mime.setText(mod.getMimeTypeRegexp());
                if (null != mod.getMaxSize())
                    maxSize.setText(mod.getMaxSize().toString());
                fileName.setText(mod.getFilename());
            }

            mime.getDocument().addDocumentListener(new DocumentListenerAdapter() {
                @Override
                public void contentChanged() {
                    if (model instanceof FileRestrType.Required) {
                        FileRestrType.Required mod = (FileRestrType.Required) model;
                        mod.setMimeTypeRegexp(mime.getText());
                    } else if (model instanceof FileRestrType.Optional) {
                        FileRestrType.Optional mod = (FileRestrType.Optional) model;
                        mod.setMimeTypeRegexp(mime.getText());
                    }
                }
            });

            maxSize.getDocument().addDocumentListener(new DocumentListenerAdapter() {
                @Override
                public void contentChanged() {
                    if (model instanceof FileRestrType.Required) {
                        FileRestrType.Required mod = (FileRestrType.Required) model;
                        if (!maxSize.getText().isEmpty())
                            mod.setMaxSize(BigInteger.valueOf(Integer.parseInt(maxSize.getText())));
                        else
                            mod.setMaxSize(null);
                    } else if (model instanceof FileRestrType.Optional) {
                        FileRestrType.Optional mod = (FileRestrType.Optional) model;
                        if (!maxSize.getText().isEmpty())
                            mod.setMaxSize(BigInteger.valueOf(Integer.parseInt(maxSize.getText())));
                        else
                            mod.setMaxSize(null);
                    }
                }
            });
            new PositiveIntegerFilter(maxSize);

            fileName.getDocument().addDocumentListener(new DocumentListenerAdapter() {
                @Override
                public void contentChanged() {
                    if (model instanceof FileRestrType.Required) {
                        FileRestrType.Required mod = (FileRestrType.Required) model;
                        mod.setFilename(fileName.getText());
                    } else if (model instanceof FileRestrType.Optional) {
                        FileRestrType.Optional mod = (FileRestrType.Optional) model;
                        mod.setFilename(fileName.getText());
                    }
                }
            });
            new InputMustNotBeEmptyHighlighter(fileName);
        }

        @Override
        public java.util.List<InvalidInput> validateInput() {
            ArrayList<InvalidInput> a = new ArrayList<>();

            if (fileName.getText().isEmpty())
                a.add(new InvalidInput("File Name", fileName, Str.get("ValueIsMissing")));

            return a;
        }
    }
}
