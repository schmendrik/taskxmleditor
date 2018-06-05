package de.hsh.taskxmleditor.presentation.document_filters;

import net.java.balloontip.BalloonTip;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import javax.swing.text.PlainDocument;

public class PositiveIntegerFilter extends DocumentFilterBase {
    private JComponent c;
    private boolean nullable;

    public PositiveIntegerFilter(JTextComponent c) {
        this(c, false);
    }

    public PositiveIntegerFilter(JTextComponent c, boolean nullable) {
        this.c = c;
        this.nullable = nullable;
        ((PlainDocument) c.getDocument()).setDocumentFilter(this);
    }

    @Override
    protected boolean test(String input) {
        try {
            if (nullable && (null == input || input.isEmpty()))
                return true;
            Integer i = Integer.parseInt(input);
            return i >= 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    @Override
    protected BalloonTip getError() {
        StringBuilder sb = new StringBuilder("Value must be a positive integer.");
        if(nullable)
            sb.append(" Value can be empty.");
        else
            sb.append(" Value cannot be empty.");
        return new BalloonTip(c, sb.toString());
    }
}
