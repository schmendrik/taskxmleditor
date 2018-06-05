package de.hsh.taskxmleditor.presentation.taskxml_v1_1_gui;

import de.hsh.taskxmleditor.presentation.InputValidator;
import de.hsh.taskxmleditor.presentation.Gui;
import de.hsh.taskxmleditor.presentation.InvalidInput;
import de.hsh.taskxmleditor.presentation.OtherNamespaceElementsPanel;
import de.hsh.taskxmleditor.taskxml_v1_1.GradingHints;
import de.hsh.taskxmleditor.taskxml_v1_1.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class GradingHintsPanel extends JPanel implements InputValidator {
    public final static Logger log = LoggerFactory.getLogger(GradingHintsPanel.class);
    private static final long serialVersionUID = 1L;

    public GradingHintsPanel(Task task, GradingHints model) {
        OtherNamespaceElementsPanel other = new OtherNamespaceElementsPanel(task, model.getAny());
        JScrollPane scroller = Gui.createScrollPane(other);

        setLayout(new BorderLayout());
        add(scroller, BorderLayout.CENTER);
    }

    @Override
    public List<InvalidInput> validateInput() {
        return new ArrayList<>();
    }
}
