package de.hsh.taskxmleditor.presentation;

import de.hsh.taskxmleditor.taskxml_v1_1.Test;

import javax.swing.*;
import javax.swing.event.PopupMenuEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.function.Consumer;

/**
 * Automatically refreshes its selection list with actually
 * existing (and thus valid) test ids from the task's test list.
 * this makes user changes to tests automatically available to
 * this combo box and removes dead items
 */
public class AutoRefreshingComboBoxForTests extends JComboBox<Test> {
    private DefaultComboBoxModel<Test> testRefModel = new DefaultComboBoxModel<>();

    private void reloadTests(java.util.List<Test> tests) {
        // Bugfix: dead items
        // Do NOT remove the currently selected (dead) item. It's a reference
        // to a test object that has been set for this particular test group/element.
        // If the test that the reference points to has been deleted, removing
        // the dead test item would cause another (valid) test item to automatically
        // take its place without us noticing, and we can't have that.

        // temporarily remove action listeners so they don't act on our refreshing
        // the selection list
        ActionListener[] listeners = getActionListeners();
        Arrays.stream(listeners).forEach(l -> removeActionListener(l));

        Test theChosenOne = (Test)testRefModel.getSelectedItem();
        testRefModel.removeAllElements();
        tests.stream().forEach(test -> testRefModel.addElement(test));
        testRefModel.setSelectedItem(theChosenOne);

        // reapply action listeners
        Arrays.stream(listeners).forEach(l -> addActionListener(l));
    }

    public AutoRefreshingComboBoxForTests(java.util.List<Test> tests, Consumer<String> setId) {
        setModel(testRefModel);

        reloadTests(tests);

        // refresh the combo box's selection list every time the user clicks on
        // the drop down arrow, so we don't get to select dead items unknowningly
        addPopupMenuListener(new PopupMenuListenerAdapter() {
            @Override
            public void popupMenuDropDown(PopupMenuEvent e) {
                reloadTests(tests);
            }
        });

        addActionListener(l -> {
            callSetId(setId);
        });

        // For newly constructed combo boxes that don't get an initial
        // value from the model, we use the combo boxes 'natural default value'
        // as the intial value for the model
        callSetId(setId);
    }

    private void callSetId(Consumer<String> setId) {
        if (null != getSelectedItem()) {
            Test selectedItem = (Test) getSelectedItem();
            setId.accept(selectedItem.getId());
        }
    }
}
