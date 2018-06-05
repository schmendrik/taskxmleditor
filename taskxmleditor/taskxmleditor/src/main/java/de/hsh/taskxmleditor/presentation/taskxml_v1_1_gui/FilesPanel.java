package de.hsh.taskxmleditor.presentation.taskxml_v1_1_gui;

import de.hsh.taskxmleditor.application.CreateTaskxmlDefaults;
import de.hsh.taskxmleditor.presentation.InputValidator;
import de.hsh.taskxmleditor.presentation.AddRemoveItemsPanel;
import de.hsh.taskxmleditor.presentation.Gui;
import de.hsh.taskxmleditor.presentation.InvalidInput;
import de.hsh.taskxmleditor.taskxml_v1_1.File;
import de.hsh.taskxmleditor.taskxml_v1_1.Files;
import de.hsh.taskxmleditor.taskxml_v1_1.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FilesPanel extends JPanel implements InputValidator {
    public final static Logger log = LoggerFactory.getLogger(FilesPanel.class);
    private Files model;

    private static AddRemoveItemsPanel addRemItems;
    private final JScrollPane scrollPane;

    public FilesPanel(Task task, Files model) {
        this.model = model;

        addRemItems = new AddRemoveItemsPanel(() -> {
            File newFile = CreateTaskxmlDefaults.createFile(model.getFile());
            FilePanel p = new FilePanel(newFile);
            return p;
        });

        // display them in reverse order (which will be the natural order)
        List<File> shallowCopy = model.getFile().subList(0, model.getFile().size());
        Collections.reverse(shallowCopy);
        shallowCopy.forEach(f -> {
            FilePanel filePanel = new FilePanel(f);
            addRemItems.addItem(filePanel);
        });

        addRemItems.setItemAdded(item -> {
            FilePanel fp = (FilePanel) item;
            File f = fp.getModel();
            model.getFile().add(f);
            // JOptionPane.showMessageDialog(null, "added " + f.getId());
        });

        addRemItems.setItemRemoved(item -> {
            FilePanel fp = (FilePanel) item;
            File f = fp.getModel();
            model.getFile().remove(f);
            // JOptionPane.showMessageDialog(null, "removed " + f.getId());
        });

        setLayout(new BorderLayout());
        scrollPane = Gui.createScrollPane(addRemItems);
        add(scrollPane, BorderLayout.CENTER);
    }

    @Override
    public java.util.List<InvalidInput> validateInput() {
        ArrayList<InvalidInput> a = new ArrayList<>();

        // check uniqueness of ids
        ArrayList<String> idsInUse = new ArrayList<>();

        for (Component item : addRemItems.getItems()) {
            FilePanel fp = (FilePanel) item;
            a.addAll((fp.validateInput()));

            if(idsInUse.stream().anyMatch(id -> id.equals(fp.getFileId())))
                a.add(new InvalidInput("File", fp.getFileIdField(), "File ID is already in use"));
            idsInUse.add(fp.getFileId());
        }

        return a;
    }


}
