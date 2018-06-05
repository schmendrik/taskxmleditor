package de.hsh.taskxmleditor.presentation.taskxml_v1_1_gui;

import de.hsh.taskxmleditor.presentation.InputValidator;
import de.hsh.taskxmleditor.presentation.DocumentListenerAdapter;
import de.hsh.taskxmleditor.presentation.Gui;
import de.hsh.taskxmleditor.presentation.InvalidInput;
import de.hsh.taskxmleditor.presentation.document_filters.InputMustNotBeEmptyHighlighter;
import de.hsh.taskxmleditor.presentation.document_filters.PositiveIntegerFilter;
import de.hsh.taskxmleditor.taskxml_v1_1.FileRegexpRestrType;
import de.hsh.taskxmleditor.taskxml_v1_1.Task;
import de.hsh.taskxmleditor.utils.Str;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.math.BigInteger;
import java.util.ArrayList;

public class FileRegexpRestrictionPanel extends JPanel implements InputValidator {
    public final static Logger log = LoggerFactory.getLogger(FileRegexpRestrictionPanel.class);
    private static final long serialVersionUID = 1L;
    public static final String KEY_NAME = "Regular Expression Restriction";
    private FileRegexpRestrType model;
    private JTextField maxSize;
    private JTextField mimeType;
    private JTextField textContent;

    public FileRegexpRestrType getModel() {
        return model;
    }

    public FileRegexpRestrictionPanel(Task task, FileRegexpRestrType model) {
        this.model = model;
        maxSize = Gui.createTextField();
        mimeType = Gui.createTextField();
        textContent = Gui.createTextField();

        GridBagLayout layout = new GridBagLayout();
        // get rid of vertical gaps by setting the every row weight to 0.0
        // except the very last one
        setLayout(layout);
        add(new JLabel(Str.get("Restr.MaxSize")), Gui.createGbc(0, 0));
        add(Gui.combineWithHelp(maxSize, Str.get("Restr.MaxSize.Help")), Gui.createGbc(1, 0));
        add(new JLabel(Str.get("Restr.MimeTypeRegexp")), Gui.createGbc(0, 1));
        add(Gui.combineWithHelp(mimeType, Str.get("Restr.MimeTypeRegexp.Help")), Gui.createGbc(1, 1));
        add(new JLabel(Str.get("Restr.Regexp.FileRegexRestrTypeTextContent")), Gui.createGbc(0, 2));
        add(Gui.combineWithHelp(textContent, Str.get("Restr.Regexp.FileRegexRestrTypeTextContent.Help")), Gui.createGbc(1, 2));
        JPanel dummy = new JPanel();
        GridBagConstraints gbc = Gui.createGbc(0, 3);
        gbc.weighty = 1.0; // fill the rest of the space so my components remain at the top
        add(dummy, gbc);

        if (null != model.getMaxSize())
            maxSize.setText(model.getMaxSize().toString());
        mimeType.setText(model.getMimeTypeRegexp());
        textContent.setText(model.getValue());

        maxSize.getDocument().addDocumentListener(new DocumentListenerAdapter() {
            @Override
            public void contentChanged() {
                if(!maxSize.getText().isEmpty())
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

        textContent.getDocument().addDocumentListener(new DocumentListenerAdapter() {
            @Override
            public void contentChanged() {
                model.setValue(textContent.getText());
            }
        });
        new InputMustNotBeEmptyHighlighter(textContent);
    }

    @Override
    public java.util.List<InvalidInput> validateInput() {
        ArrayList<InvalidInput> a = new ArrayList<>();

        if(textContent.getText().isEmpty())
            a.add(new InvalidInput("Regexp", textContent, Str.get("ValueIsMissing")));

        return a;
    }
}
