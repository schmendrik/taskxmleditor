package de.hsh.taskxmleditor.presentation.document_filters;

import net.java.balloontip.BalloonTip;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import javax.swing.text.PlainDocument;

public class MinMaxDoubleFilter extends DocumentFilterBase {
    private JComponent c;
    private boolean nullable = false;
    private double min, max;

    public MinMaxDoubleFilter(JTextComponent c, boolean nullable, double min, double max) {
        this.c = c;
        this.nullable = nullable;
        this.min = min;
        this.max = max;
        ((PlainDocument) c.getDocument()).setDocumentFilter(this);
    }

    @Override
    protected boolean test(String input) {
        try {
            if (nullable && (null == input || input.isEmpty()))
                return true;

            // TODO: vor punkt und nach punkt zahlenstellen limitieren

            Double d = Double.parseDouble(input);
            return d >= min && d <= max;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    @Override
    protected BalloonTip getError() {
        StringBuilder sb = new StringBuilder(String.format("Value range for this property must be from %s to %s.", min, max));
        if(nullable)
            sb.append(" Value can be empty.");
        else
            sb.append(" Value cannot be empty.");
        return new BalloonTip(c, sb.toString());
    }
}
