package de.hsh.taskxmleditor.plugin;

import de.hsh.taskxmleditor.presentation.InputValidator;
import de.hsh.taskxmleditor.presentation.InvalidInput;

import javax.swing.*;
import java.util.List;

public abstract class PluginEditor extends JPanel implements InputValidator {

    /**
     * This method is called by the editor application that is hosting a plugin editor
     * @throws Exception
     */
    public abstract void saveInput() throws Exception;

    /**
     * Creates a list of invalid user input
     * @return
     */
    public abstract List<InvalidInput> validateInput();
}
