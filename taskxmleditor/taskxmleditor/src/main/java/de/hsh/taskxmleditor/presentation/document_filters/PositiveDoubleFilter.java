package de.hsh.taskxmleditor.presentation.document_filters;

import net.java.balloontip.BalloonTip;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import javax.swing.text.PlainDocument;

public class PositiveDoubleFilter extends DocumentFilterBase {
    private JComponent c;
    private boolean nullable = false;

    public PositiveDoubleFilter(JTextComponent c) {
        this(c, false);
    }

    public PositiveDoubleFilter(JTextComponent c, boolean nullable) {
        this.c = c;
        this.nullable = nullable;
        ((PlainDocument) c.getDocument()).setDocumentFilter(this);
    }

    @Override
    protected boolean test(String input) {
        try {
            if (nullable && (null == input || input.isEmpty()))
                return true;
            Double d = Double.parseDouble(input);
            return d >= 0.0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    @Override
    protected BalloonTip getError() {
        StringBuilder sb = new StringBuilder("Value must be a positive decimal.");
        if(nullable)
            sb.append(" Value can be empty.");
        else
            sb.append(" Value cannot be empty.");
        return new BalloonTip(c, sb.toString());
    }
}
