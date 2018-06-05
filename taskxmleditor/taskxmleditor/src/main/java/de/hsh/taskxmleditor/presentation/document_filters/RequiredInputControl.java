//package de.hsh.taskxmleditor.presentation.document_filters;
//
//import javax.swing.*;
//import javax.swing.border.Border;
//import javax.swing.event.DocumentEvent;
//import javax.swing.event.DocumentListener;
//import javax.swing.text.JTextComponent;
//import java.awt.*;
//import java.util.Observable;
//import java.util.Observer;
//
///**
// * Source: http://blog.cyberborean.org/2006/05/07/tips-tricks-required-text-fields-in-swing
// * Modified by Paul Reiser.
// */
//public class RequiredInputControl implements Observer {
//    Component comp = null;
//    Border defaultBorder = BorderFactory.createEmptyBorder();
//    Border highlightBorder = BorderFactory.createLineBorder(Color.RED, 3);
//
//    public RequiredInputControl(Component comp) {
//        this.comp = comp;
//
////        if (!(comp instanceof Observable)) {
////
////        }
////
////
//////        if(comp instanceof JComponent)
//////        defaultBorder = comp.getBorder();
////        comp.addObserver(this);
////        this.maybeHighlight();
////
////        comp.addPropertyChangeListener("enabled", e -> {
////            this.maybeHighlight();
////        });
//    }
//
//    private void maybeHighlight() {
//        if (!comp.isEnabled())
//            comp.setBorder(defaultBorder);
//        else if (comp.getText().trim().length() > 0)
//            comp.setBorder(defaultBorder);
//        else
//            comp.setBorder(highlightBorder);
//    }
//
//    @Override
//    public void update(Observable o, Object arg) {
//        maybeHighlight();
//    }
//}