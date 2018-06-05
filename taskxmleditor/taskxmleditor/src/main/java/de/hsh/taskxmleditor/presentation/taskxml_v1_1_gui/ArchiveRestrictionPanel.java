package de.hsh.taskxmleditor.presentation.taskxml_v1_1_gui;

import de.hsh.taskxmleditor.presentation.InputValidator;
import de.hsh.taskxmleditor.presentation.*;
import de.hsh.taskxmleditor.presentation.document_filters.InputMustNotBeEmptyHighlighter;
import de.hsh.taskxmleditor.presentation.document_filters.PositiveIntegerFilter;
import de.hsh.taskxmleditor.taskxml_v1_1.ArchiveRestrType;
import de.hsh.taskxmleditor.taskxml_v1_1.Task;
import de.hsh.taskxmleditor.thirdparty_libs.tips4java.DisabledPanel;
import de.hsh.taskxmleditor.utils.Str;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.math.BigInteger;
import java.util.ArrayList;

public class ArchiveRestrictionPanel extends JPanel implements InputValidator {
    public final static Logger log = LoggerFactory.getLogger(ArchiveRestrictionPanel.class);
    public static final String KEY_NAME = "Archive Restriction";
    private ArchiveRestrType model;
    private String unpackFilesFromArchiveRegexpType = "";
    private ArchiveRestrType.FileRestrictions fileRestr = new ArchiveRestrType.FileRestrictions();
    private JTextField maxSize;
    private JTextField mimeType;
    private JTextField allowedArchFileName;
    private AddRemoveItemsPanel archiveRestrTypeItems;
    private JTextField unpackFilesFromArchRegexp;
    private JRadioButton radioUnpackFilesFromArchRegexp;
    private JRadioButton radioFileRestrictions;

    public ArchiveRestrType getModel() {
        return model;
    }

    public ArchiveRestrictionPanel(Task task, ArchiveRestrType model) {
        this.model = model;

        JPanel archiveRestrTypeItemsWrapper = new JPanel(new BorderLayout());
        archiveRestrTypeItemsWrapper.setBorder(BorderFactory.createTitledBorder(Str.get("Restr.File.Name")));
        DisabledPanel archiveRestrTypeItemsPanel = new DisabledPanel(archiveRestrTypeItemsWrapper);

        this.unpackFilesFromArchRegexp = Gui.createTextField();
        this.radioUnpackFilesFromArchRegexp = new JRadioButton(Str.get("Restr.Archive.UnpackFilesFromArchiveRegexp"));
        this.radioUnpackFilesFromArchRegexp.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                this.model.setUnpackFilesFromArchiveRegexpOrFileRestrictions(unpackFilesFromArchiveRegexpType);
                this.unpackFilesFromArchRegexp.setEnabled(true);
                archiveRestrTypeItemsPanel.setEnabled(false);
            } else if (e.getStateChange() == ItemEvent.DESELECTED) {
                this.unpackFilesFromArchRegexp.setEnabled(false);
            }
        });

        this.radioFileRestrictions = new JRadioButton(Str.get("Restr.Archive.FileRestrictions"));
        this.radioFileRestrictions.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                this.model.setUnpackFilesFromArchiveRegexpOrFileRestrictions(fileRestr);
                archiveRestrTypeItemsPanel.setEnabled(true);
                this.unpackFilesFromArchRegexp.setEnabled(false);
            } else if (e.getStateChange() == ItemEvent.DESELECTED) {
                archiveRestrTypeItemsPanel.setEnabled(false);
            }
        });

        ButtonGroup group = new ButtonGroup();
        group.add(this.radioUnpackFilesFromArchRegexp);
        group.add(this.radioFileRestrictions);

        archiveRestrTypeItems = new AddRemoveItemsPanel(() -> {
            final String REQUIRED = "Required";
            final String OPTIONAL = "Optional";

            String[][] choices = {
                    {REQUIRED, Str.get("Restr.Archive.RequiredInfo")},
                    {OPTIONAL, Str.get("Restr.Archive.OptionalInfo")}
            };
            ChoiceDialog choicesDialog = new ChoiceDialog(choices);
            choicesDialog.setPreferredSize(new Dimension(400, 80));

            int reply = JOptionPane.showConfirmDialog(null,
                    choicesDialog,
                    Str.get("Restr.Archive.AddNewRestr"),
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE);

            if (reply == JOptionPane.OK_OPTION) {
                ArchiveRestrTypeReqOptPanel a;
                String choice = choicesDialog.getSelectedChoice();
                if (choice.equals(REQUIRED)) {
                    ArchiveRestrType.FileRestrictions.Required r = new ArchiveRestrType.FileRestrictions.Required();
                    a = new ArchiveRestrTypeReqOptPanel(r);
                } else if (choice.equals(OPTIONAL)) {
                    ArchiveRestrType.FileRestrictions.Optional o = new ArchiveRestrType.FileRestrictions.Optional();
                    a = new ArchiveRestrTypeReqOptPanel(o);
                } else throw new Exception("Unknown choice " + choice);
                return a;
            }
            return null;
        }, true);

        maxSize = Gui.createTextField();
        if (null != model.getMaxSize())
            maxSize.setText(model.getMaxSize().toString());

        mimeType = Gui.createTextField();
        if (null != model.getMimeTypeRegexp())
            mimeType.setText(model.getMimeTypeRegexp());

        allowedArchFileName = Gui.createTextField();
        if (null != model.getAllowedArchiveFilenameRegexp())
            allowedArchFileName.setText(model.getAllowedArchiveFilenameRegexp());

        // Read initial task.xml data
        Object obj = model.getUnpackFilesFromArchiveRegexpOrFileRestrictions();
        if (null != obj) {
            if (obj instanceof String) {
                this.unpackFilesFromArchRegexp.setText((String) obj);
                // is UnpackFilesFromArchiveRegexp
                this.radioUnpackFilesFromArchRegexp.setSelected(true);
                this.radioUnpackFilesFromArchRegexp.doClick();
            } else if (obj instanceof ArchiveRestrType.FileRestrictions) {
                fileRestr = (ArchiveRestrType.FileRestrictions) obj;
                this.radioFileRestrictions.setSelected(true);
                this.radioFileRestrictions.doClick();

                java.util.List<Object> shallowCopy = new ArrayList<>(fileRestr.getRequiredOrOptional());
                //Collections.reverse(shallowCopy);
                shallowCopy.forEach(o -> {
                    if (o instanceof ArchiveRestrType.FileRestrictions.Optional) {
                        ArchiveRestrType.FileRestrictions.Optional opt = (ArchiveRestrType.FileRestrictions.Optional) o;
                        archiveRestrTypeItems.addItem(new ArchiveRestrTypeReqOptPanel(opt));
                    } else if (o instanceof ArchiveRestrType.FileRestrictions.Required) {
                        ArchiveRestrType.FileRestrictions.Required req = (ArchiveRestrType.FileRestrictions.Required) o;
                        archiveRestrTypeItems.addItem(new ArchiveRestrTypeReqOptPanel(req));
                    }
                });
            } else {
                log.error("Unknown archive-restr-type: " + obj);
            }
        } else {
            // Set up some defaults
            this.radioFileRestrictions.setSelected(false);

            // these 2 statements go together
            this.radioUnpackFilesFromArchRegexp.setSelected(true);
            this.radioUnpackFilesFromArchRegexp.doClick();
        }

        archiveRestrTypeItemsWrapper.add(archiveRestrTypeItems, BorderLayout.CENTER);

        archiveRestrTypeItems.setItemAdded(item -> {
            ArchiveRestrTypeReqOptPanel a = (ArchiveRestrTypeReqOptPanel) item;
            fileRestr.getRequiredOrOptional().add(a.getFileRestrictionObject());
        });

        archiveRestrTypeItems.setItemRemoved(item -> {
            ArchiveRestrTypeReqOptPanel a = (ArchiveRestrTypeReqOptPanel) item;
            fileRestr.getRequiredOrOptional().remove(a.getFileRestrictionObject());
        });

        archiveRestrTypeItems.setCanRemoveItem(item -> {
            if (archiveRestrTypeItems.getItemCount() == 1) {
                JOptionPane.showMessageDialog(null, "There must be at least 1 restriction available. Please add another restriction before removing this one.");
                return false;
            }
            return true;
        });

        GridBagLayout layout = new GridBagLayout();
        // get rid of vertical gaps by setting the every row w eight to 0.0
        // except the very last one
        layout.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 1.0};
        setLayout(layout);
        add(new JLabel(Str.get("Restr.MaxSize")), Gui.createGbc(0, 0, GridBagConstraints.VERTICAL));
        add(Gui.combineWithHelp(maxSize, Str.get("Restr.MaxSize.Help")), Gui.createGbc(1, 0, GridBagConstraints.VERTICAL));
        add(new JLabel(Str.get("Restr.MimeTypeRegexp")), Gui.createGbc(0, 1, GridBagConstraints.VERTICAL));
        add(Gui.combineWithHelp(mimeType, Str.get("Restr.MimeTypeRegexp.Help")), Gui.createGbc(1, 1, GridBagConstraints.VERTICAL));
        add(new JLabel(Str.get("Restr.Archive.ArchiveNameRegexp")), Gui.createGbc(0, 2, GridBagConstraints.VERTICAL));
        add(Gui.combineWithHelp(allowedArchFileName, Str.get("Restr.Archive.ArchiveNameRegexp.Help")), Gui.createGbc(1, 2, GridBagConstraints.VERTICAL));
        add(this.radioUnpackFilesFromArchRegexp, Gui.createGbc(0, 3, GridBagConstraints.VERTICAL));
        add(Gui.combineWithHelp(this.unpackFilesFromArchRegexp, Str.get("Restr.Archive.UnpackFilesFromArchiveRegexp.Help")), Gui.createGbc(1, 3, GridBagConstraints.VERTICAL));
        add(Gui.combineWithHelp(this.radioFileRestrictions, Str.get("Restr.Archive.FileRestrictions.Help")), Gui.createGbc(0, 4, GridBagConstraints.NONE));
        JScrollPane wrapper = Gui.createScrollPane(archiveRestrTypeItemsPanel);
        wrapper.setBorder(null);
        add(wrapper, Gui.createGbc(1, 4, GridBagConstraints.BOTH));

        this.unpackFilesFromArchRegexp.getDocument().addDocumentListener(new DocumentListenerAdapter() {
            @Override
            public void contentChanged() {
                // Since the string in model.setUnpackFilesFromArchiveRegexp[...] gets
                // overwritten with every radio button change, we need to keep a seperate
                // string (unpackFilesFromArchiveRegexpType) updated, so that it can be
                // written to the model upon radio button change
                unpackFilesFromArchiveRegexpType = unpackFilesFromArchRegexp.getText();
                model.setUnpackFilesFromArchiveRegexpOrFileRestrictions(unpackFilesFromArchiveRegexpType);
            }
        });
        new InputMustNotBeEmptyHighlighter(this.unpackFilesFromArchRegexp);

        maxSize.getDocument().addDocumentListener(new DocumentListenerAdapter() {
            @Override
            public void contentChanged() {
                if (!maxSize.getText().isEmpty())
                    model.setMaxSize(BigInteger.valueOf(Integer.parseInt(maxSize.getText())));
                else
                    model.setMaxSize(null);
            }
        });
        new PositiveIntegerFilter(maxSize);

        mimeType.getDocument().addDocumentListener(new DocumentListenerAdapter() {
            @Override
            public void contentChanged() {
                model.setMimeTypeRegexp(mimeType.getText());
            }
        });

        allowedArchFileName.getDocument().addDocumentListener(new DocumentListenerAdapter() {
            @Override
            public void contentChanged() {
                model.setAllowedArchiveFilenameRegexp(allowedArchFileName.getText());
            }
        });
    }

    @Override
    public java.util.List<InvalidInput> validateInput() {
        ArrayList<InvalidInput> a = new ArrayList<>();

        if (radioUnpackFilesFromArchRegexp.isSelected()) {
            if(unpackFilesFromArchRegexp.getText().isEmpty())
            a.add(new InvalidInput("UnpackFilesRegexp", unpackFilesFromArchRegexp,
                    Str.get("ValueIsMissing")));
        }
        else {
            boolean hasOneRequiredThingie = false;
            for (Component item : archiveRestrTypeItems.getItems()) {
                ArchiveRestrTypeReqOptPanel p = (ArchiveRestrTypeReqOptPanel) item;

                if(!hasOneRequiredThingie && p.getFileRestrictionObject()
                        instanceof ArchiveRestrType.FileRestrictions.Required)
                    hasOneRequiredThingie = true;

                a.addAll(p.validateInput());
            }

            if(!hasOneRequiredThingie)
                a.add(new InvalidInput("RequiredItem", archiveRestrTypeItems,
                        "At least one item of type 'Required' is missing."));
        }

        return a;
    }

    public class ArchiveRestrTypeReqOptPanel extends JPanel implements InputValidator {
        private Object fileRestrObj;
        private JTextField path;

        public Object getFileRestrictionObject() {
            return fileRestrObj;
        }

        public ArchiveRestrTypeReqOptPanel(ArchiveRestrType.FileRestrictions.Required model) {
            fileRestrObj = model;

            path = Gui.createTextField();
            JTextField mime = Gui.createTextField();

            GridBagLayout layout = new GridBagLayout();
            setLayout(layout);
            setBorder(BorderFactory.createTitledBorder("Required"));
            add(new JLabel(Str.get("Restr.Archive.Path")), Gui.createGbc(0, 0, GridBagConstraints.VERTICAL));
            add(Gui.combineWithHelp(path, Str.get("Restr.Archive.Path.Help")), Gui.createGbc(1, 0, GridBagConstraints.VERTICAL));
            add(new JLabel(Str.get("Restr.MimeTypeRegexp")), Gui.createGbc(0, 1, GridBagConstraints.VERTICAL));
            add(Gui.combineWithHelp(mime, Str.get("Restr.MimeTypeRegexp.Help")), Gui.createGbc(1, 1, GridBagConstraints.VERTICAL));

            path.setText(model.getPath());
            mime.setText(model.getMimeTypeRegexp());

            path.getDocument().addDocumentListener(new DocumentListenerAdapter() {
                @Override
                public void contentChanged() {
                    model.setPath(path.getText());
                }
            });
            new InputMustNotBeEmptyHighlighter(path);

            mime.getDocument().addDocumentListener(new DocumentListenerAdapter() {
                @Override
                public void contentChanged() {
                    model.setMimeTypeRegexp(mime.getText());
                }
            });
        }

        public ArchiveRestrTypeReqOptPanel(ArchiveRestrType.FileRestrictions.Optional model) {
            fileRestrObj = model;

            path = Gui.createTextField();
            JTextField mime = Gui.createTextField();

            GridBagLayout layout = new GridBagLayout();
            setLayout(layout);
            setBorder(BorderFactory.createTitledBorder("Optional"));
            add(new JLabel(Str.get("Restr.Archive.Path")), Gui.createGbc(0, 0, GridBagConstraints.VERTICAL));
            add(Gui.combineWithHelp(path, Str.get("Restr.Archive.Path.Help")), Gui.createGbc(1, 0, GridBagConstraints.VERTICAL));
            add(new JLabel(Str.get("Restr.MimeTypeRegexp")), Gui.createGbc(0, 1, GridBagConstraints.VERTICAL));
            add(Gui.combineWithHelp(mime, Str.get("Restr.MimeTypeRegexp.Help")), Gui.createGbc(1, 1, GridBagConstraints.VERTICAL));

            path.setText(model.getPath());
            mime.setText(model.getMimeTypeRegexp());

            path.getDocument().addDocumentListener(new DocumentListenerAdapter() {
                @Override
                public void contentChanged() {
                    model.setPath(path.getText());
                }
            });
            new InputMustNotBeEmptyHighlighter(path);

            mime.getDocument().addDocumentListener(new DocumentListenerAdapter() {
                @Override
                public void contentChanged() {
                    model.setMimeTypeRegexp(mime.getText());
                }
            });
        }

        @Override
        public java.util.List<InvalidInput> validateInput() {
            ArrayList<InvalidInput> a = new ArrayList<>();

            if (path.getText().isEmpty())
                a.add(new InvalidInput("Path", path, Str.get("ValueIsMissing")));

            return a;
        }
    }
}
