package de.hsh.taskxmleditor.presentation.document_filters;

import de.hsh.taskxmleditor.presentation.TimedBalloon;
import net.java.balloontip.BalloonTip;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.DocumentFilter;

public abstract class DocumentFilterBase extends DocumentFilter {
    //private BalloonTip lastBalloon = null;
    private TimedBalloon lastBalloon = null;

    protected abstract boolean test(String input);

    protected abstract BalloonTip getError();

    private void displayError() {
        if (null != lastBalloon)
            lastBalloon.closeBalloon();
        lastBalloon = new TimedBalloon(getError()); // will display the new error message just by calling the ctor
    }

    @Override
    public void insertString(FilterBypass fb, int offset, String string,
                             AttributeSet attr) throws BadLocationException {

        Document doc = fb.getDocument();
        StringBuilder sb = new StringBuilder();
        sb.append(doc.getText(0, doc.getLength()));
        sb.insert(offset, string);

        if (test(sb.toString()))
            super.insertString(fb, offset, string, attr);
        else
            displayError();
    }

    @Override
    public void replace(FilterBypass fb, int offset, int length, String text,
                        AttributeSet attrs) throws BadLocationException {

        Document doc = fb.getDocument();
        StringBuilder sb = new StringBuilder();
        sb.append(doc.getText(0, doc.getLength()));
        sb.replace(offset, offset + length, text);

        if (test(sb.toString()))
            super.replace(fb, offset, length, text, attrs);
        else
            displayError();
    }

    @Override
    public void remove(FilterBypass fb, int offset, int length)
            throws BadLocationException {
        Document doc = fb.getDocument();
        StringBuilder sb = new StringBuilder();
        sb.append(doc.getText(0, doc.getLength()));
        sb.delete(offset, offset + length);

        if (test(sb.toString())) {
            super.remove(fb, offset, length);
        }
        else {
            displayError();
        }
    }
}
