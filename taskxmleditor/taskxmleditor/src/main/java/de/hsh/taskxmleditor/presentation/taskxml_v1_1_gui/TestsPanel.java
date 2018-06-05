package de.hsh.taskxmleditor.presentation.taskxml_v1_1_gui;

import de.hsh.taskxmleditor.application.CreateTaskxmlDefaults;
import de.hsh.taskxmleditor.presentation.InputValidator;
import de.hsh.taskxmleditor.presentation.AddRemoveItemsPanel;
import de.hsh.taskxmleditor.presentation.Gui;
import de.hsh.taskxmleditor.presentation.InvalidInput;
import de.hsh.taskxmleditor.taskxml_v1_1.Task;
import de.hsh.taskxmleditor.taskxml_v1_1.Test;
import de.hsh.taskxmleditor.taskxml_v1_1.Tests;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class TestsPanel extends JPanel implements InputValidator {
    public final static Logger log = LoggerFactory.getLogger(TestsPanel.class);
    private Tests model;
    private JPanel panelsDisplay;

    private AddRemoveItemsPanel addRemItems;

    public TestsPanel(Task task, Tests model) {
        this.model = model;

        addRemItems = new AddRemoveItemsPanel(() -> {
            Test newTest = CreateTaskxmlDefaults.createTest(model.getTest());
            TestPanel p = new TestPanel(task, newTest);
            return p;
        });

        java.util.List<Test> shallowCopy = model.getTest().subList(0, model.getTest().size());
        //Collections.reverse(shallowCopy);
        shallowCopy.forEach(t -> {
            TestPanel testPanel = new TestPanel(task, t);
            addRemItems.addItem(testPanel);
        });

        addRemItems.setItemAdded(item -> {
            TestPanel tp = (TestPanel) item;
            Test t = tp.getModel();
            model.getTest().add(t);
        });

        addRemItems.setItemRemoved(item -> {
            TestPanel tp = (TestPanel) item;
            Test t = tp.getModel();
            model.getTest().remove(t);
        });

        setLayout(new BorderLayout());
        add(Gui.createScrollPane(addRemItems), BorderLayout.CENTER);
    }

    @Override
    public java.util.List<InvalidInput> validateInput() {
        ArrayList<InvalidInput> a = new ArrayList<>();

        // check uniqueness of ids
        ArrayList<String> idsInUse = new ArrayList<>();

        for (Component item : addRemItems.getItems()) {
            TestPanel p = (TestPanel) item;
            a.addAll((p.validateInput()));

            if(idsInUse.stream().anyMatch(id -> id.equals(p.getTestId().getText())))
                a.add(new InvalidInput("Test", p.getTestId(), "Test ID is already in use"));
            idsInUse.add(p.getTestId().getText());
        }

        return a;
    }
}
