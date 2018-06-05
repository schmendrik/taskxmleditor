package de.hsh.taskxmleditor.presentation.document_filters;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;
import java.awt.*;

/**
 * Source: http://blog.cyberborean.org/2006/05/07/tips-tricks-required-text-fields-in-swing
 * Modified by Paul Reiser.
 */
public class InputMustNotBeEmptyHighlighter implements DocumentListener {
    JTextComponent comp = null;
    Border defaultBorder = null;
    Border highlightBorder = BorderFactory.createLineBorder(Color.ORANGE, 2);

    public InputMustNotBeEmptyHighlighter(JTextComponent jtc) {
        comp = jtc;
        defaultBorder = comp.getBorder();
        comp.getDocument().addDocumentListener(this);
        this.maybeHighlight();

        comp.addPropertyChangeListener("enabled", e -> {
            this.maybeHighlight();
        });
    }

    public void insertUpdate(DocumentEvent e) {
        maybeHighlight();
    }

    public void removeUpdate(DocumentEvent e) {
        maybeHighlight();
    }

    public void changedUpdate(DocumentEvent e) {
        maybeHighlight();
    }

    private void maybeHighlight() {
        if (!comp.isEnabled())
            comp.setBorder(defaultBorder);
        else if (comp.getText().trim().length() > 0)
            comp.setBorder(defaultBorder);
        else
            comp.setBorder(highlightBorder);
    }
}