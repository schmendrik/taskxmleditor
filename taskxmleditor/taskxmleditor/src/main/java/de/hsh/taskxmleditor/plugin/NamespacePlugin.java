package de.hsh.taskxmleditor.plugin;

import javax.xml.namespace.QName;
import java.util.Properties;

public interface NamespacePlugin {
    void init(Properties props);

    /***
     * Returns all supported qnames that this plugin has plugin editors for.
     *
     * @return
     */
    QName[] getSupportedQNames();

    /***
     * Creates a new plugin editor instance for a particular qname
     * @param qName
     * @return an instance of a plugin editor, or null if the the qname is not supported
     *         is not supported
     */
    // Editor createEditor(QName qName);

    PluginEditor createEditorInstance(QName qName, PluginEditorModel inputData) throws Exception;

}
