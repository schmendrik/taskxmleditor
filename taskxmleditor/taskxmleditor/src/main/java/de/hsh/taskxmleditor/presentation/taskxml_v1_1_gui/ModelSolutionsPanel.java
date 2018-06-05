package de.hsh.taskxmleditor.presentation.taskxml_v1_1_gui;

import de.hsh.taskxmleditor.application.CreateTaskxmlDefaults;
import de.hsh.taskxmleditor.presentation.InputValidator;
import de.hsh.taskxmleditor.presentation.AddRemoveItemsPanel;
import de.hsh.taskxmleditor.presentation.Gui;
import de.hsh.taskxmleditor.presentation.InvalidInput;
import de.hsh.taskxmleditor.taskxml_v1_1.ModelSolution;
import de.hsh.taskxmleditor.taskxml_v1_1.ModelSolutions;
import de.hsh.taskxmleditor.taskxml_v1_1.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class ModelSolutionsPanel extends JPanel implements InputValidator {
    public final static Logger log = LoggerFactory.getLogger(ModelSolutionsPanel.class);
    private ModelSolutions model;
    private AddRemoveItemsPanel items;

    public ModelSolutionsPanel(Task task, ModelSolutions model) {
        this.model = model;

        items = new AddRemoveItemsPanel(() -> {
            ModelSolution ms = CreateTaskxmlDefaults.createModelSolution(model.getModelSolution());
            ModelSolutionPanel p = new ModelSolutionPanel(task, ms);
            return p;
        }, true);

        model.getModelSolution().forEach(ms -> items.addItem(new ModelSolutionPanel(task, ms)));

        items.setItemAdded(item -> {
            ModelSolutionPanel a = (ModelSolutionPanel) item;
            model.getModelSolution().add(a.getModelSolution());
        });

        items.setItemRemoved(item -> {
            ModelSolutionPanel a = (ModelSolutionPanel) item;
            model.getModelSolution().remove(a.getModelSolution());
        });

        setLayout(new BorderLayout());
        JScrollPane scroller = Gui.createScrollPane(items);
        scroller.setBorder(null);
        add(scroller, BorderLayout.CENTER);
    }

    @Override
    public java.util.List<InvalidInput> validateInput() {
        ArrayList<InvalidInput> a = new ArrayList<>();

        for (Component item : items.getItems()) {
            ModelSolutionPanel p = (ModelSolutionPanel) item;
            a.addAll((p.validateInput()));
        }

        return a;
    }
}

