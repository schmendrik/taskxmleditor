package de.hsh.taskxmleditor.presentation.taskxml_v1_1_gui;

import de.hsh.taskxmleditor.application.CreateTaskxmlDefaults;
import de.hsh.taskxmleditor.presentation.InputValidator;
import de.hsh.taskxmleditor.presentation.AddRemoveItemsPanel;
import de.hsh.taskxmleditor.presentation.Gui;
import de.hsh.taskxmleditor.presentation.InvalidInput;
import de.hsh.taskxmleditor.taskxml_v1_1.ExternalResource;
import de.hsh.taskxmleditor.taskxml_v1_1.ExternalResources;
import de.hsh.taskxmleditor.taskxml_v1_1.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class ExternalResourcesPanel extends JPanel implements InputValidator {
    public final static Logger log = LoggerFactory.getLogger(ExternalResourcesPanel.class);
    private static final long serialVersionUID = 1L;
    private ExternalResources model;
    private Task task;
    private static AddRemoveItemsPanel addRemItems;

    public ExternalResourcesPanel(Task task, ExternalResources model) {
        this.task = task;
        this.model = model;

        addRemItems = new AddRemoveItemsPanel(() -> {
            ExternalResource newRes = CreateTaskxmlDefaults.createExternalResource(model.getExternalResource());
            ExternalResourcePanel p = new ExternalResourcePanel(task, newRes);
            return p;
        });

        java.util.List<ExternalResource> shallowCopy = model.getExternalResource().subList(0, model.getExternalResource().size());
        //Collections.reverse(shallowCopy);
        shallowCopy.forEach(r -> {
            ExternalResourcePanel resPanel = new ExternalResourcePanel(task, r);
            addRemItems.addItem(resPanel);
        });

        addRemItems.setItemAdded(item -> {
            ExternalResourcePanel rp = (ExternalResourcePanel) item;
            ExternalResource r = rp.getModel();
            model.getExternalResource().add(r);
        });

        addRemItems.setItemRemoved(item -> {
            ExternalResourcePanel rp = (ExternalResourcePanel) item;
            ExternalResource r = rp.getModel();
            model.getExternalResource().remove(r);
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
            ExternalResourcePanel p = (ExternalResourcePanel) item;
            a.addAll((p.validateInput()));

            if(idsInUse.stream().anyMatch(id -> id.equals(p.getIdField().getText())))
                a.add(new InvalidInput("Resource", p.getIdField(), "Resource ID is already in use"));
            idsInUse.add(p.getIdField().getText());
        }

        return a;
    }
}
