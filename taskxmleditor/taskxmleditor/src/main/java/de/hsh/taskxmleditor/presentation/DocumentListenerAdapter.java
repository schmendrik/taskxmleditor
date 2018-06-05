package de.hsh.taskxmleditor.presentation;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public abstract class DocumentListenerAdapter implements DocumentListener {
    @Override
    public void changedUpdate(DocumentEvent e) {
        contentChanged();
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        contentChanged();
    }

    @Override
    public void insertUpdate(DocumentEvent e) {
        contentChanged();
    }

    public abstract void contentChanged();
}
