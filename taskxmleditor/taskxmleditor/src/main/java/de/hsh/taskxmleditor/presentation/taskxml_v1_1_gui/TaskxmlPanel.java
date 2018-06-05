package de.hsh.taskxmleditor.presentation.taskxml_v1_1_gui;

import de.hsh.taskxmleditor.presentation.InputValidator;
import de.hsh.taskxmleditor.presentation.InvalidInput;
import de.hsh.taskxmleditor.taskxml_v1_1.ExternalResources;
import de.hsh.taskxmleditor.taskxml_v1_1.GradingHints;
import de.hsh.taskxmleditor.taskxml_v1_1.Task;
import de.hsh.taskxmleditor.utils.Str;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;

public class TaskxmlPanel extends JPanel implements InputValidator {
    public final static Logger log = LoggerFactory.getLogger(TaskxmlPanel.class);

    private TaskPanel taskPanel;
    private ProgLangPanel progLangPanel;
    private SubmRestrictionsPanel restrPanel;
    private TestsPanel testsPanel;
    private FilesPanel filesPanel;
    private ExternalResourcesPanel extResPanel;
    private GradingHintsPanel gradingHintsPanel;
    private MetaDataPanel metaDataPanel;
    private ModelSolutionsPanel modelSolutions;
    private Task task;

    public TaskxmlPanel(Task task) {
        this.task = task;

        if(null == task.getExternalResources())
            task.setExternalResources(new ExternalResources());
        if(null == task.getGradingHints())
            task.setGradingHints(new GradingHints());

        taskPanel = new TaskPanel(task);
        restrPanel = new SubmRestrictionsPanel(task, task.getSubmissionRestrictions());
        testsPanel = new TestsPanel(task, task.getTests());
        filesPanel = new FilesPanel(task, task.getFiles());
        modelSolutions = new ModelSolutionsPanel(task, task.getModelSolutions());
        gradingHintsPanel = new GradingHintsPanel(task, task.getGradingHints());
        metaDataPanel = new MetaDataPanel(task, task.getMetaData());
        extResPanel = new ExternalResourcesPanel(task, task.getExternalResources());

        JTabbedPane tabs = new JTabbedPane();
        tabs.setBorder(new EmptyBorder(5, 5, 5, 5));

        tabs.addTab(Str.get("Task.TabName"), taskPanel);
        tabs.addTab(Str.get("Restr.TabName"), restrPanel);
        tabs.addTab(Str.get("File.TabName"), filesPanel);
        tabs.addTab(Str.get("ExtRes.TabName"), extResPanel);
        tabs.addTab(Str.get("ModSol.TabName"), modelSolutions);
        tabs.addTab(Str.get("Test.TabName"), testsPanel);
        tabs.addTab(Str.get("GradHint.TabName"), gradingHintsPanel);
        tabs.addTab(Str.get("MetaData.TabName"), metaDataPanel);

        setLayout(new BorderLayout());
        add(tabs, BorderLayout.CENTER);
    }

    @Override
    public java.util.List<InvalidInput> validateInput() {
        ArrayList<InvalidInput> a = new ArrayList<>();
        a.addAll(taskPanel.validateInput());
        a.addAll(restrPanel.validateInput());
        a.addAll(filesPanel.validateInput());
        a.addAll(modelSolutions.validateInput());
        a.addAll(extResPanel.validateInput());
        a.addAll(testsPanel.validateInput());
        a.addAll(gradingHintsPanel.validateInput());
        a.addAll(metaDataPanel.validateInput());
        return a;
    }
}
