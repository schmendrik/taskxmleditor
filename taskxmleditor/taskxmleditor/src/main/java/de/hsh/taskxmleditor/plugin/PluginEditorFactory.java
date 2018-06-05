package de.hsh.taskxmleditor.plugin;

@FunctionalInterface
public interface PluginEditorFactory {
    PluginEditor factory(PluginEditorModel inputData) throws Exception;
}
