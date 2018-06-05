package de.hsh.taskxmleditor.presentation;

import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

public abstract class PopupMenuListenerAdapter implements PopupMenuListener {
    public abstract void popupMenuDropDown(PopupMenuEvent e);

    @Override
    public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
        popupMenuDropDown(e);
    }

    @Override
    public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
    }

    @Override
    public void popupMenuCanceled(PopupMenuEvent e) {
    }
}
