package de.hsh.taskxmleditor;

import de.hsh.taskxmleditor.plugin.NamespacePlugin;
import de.hsh.taskxmleditor.plugin.PluginEditor;
import de.hsh.taskxmleditor.plugin.PluginEditorFactory;
import de.hsh.taskxmleditor.plugin.PluginEditorModel;
import de.hsh.taskxmleditor.presentation.ComputingResourcesEditor;
import de.hsh.taskxmleditor.presentation.TestGroupEditor;

import javax.xml.namespace.QName;
import java.util.HashMap;
import java.util.Properties;

public class Plugin implements NamespacePlugin {
    private HashMap<QName, PluginEditorFactory> map = new HashMap<>();

    @Override
    public void init(Properties props) {
        map.put(new QName("urn:grappa:tests:hierarchical:v0.0.1", "computing-resources"),
                inputData -> new ComputingResourcesEditor(inputData));
        map.put(new QName("urn:grappa:tests:hierarchical:v0.0.1", "test-group"),
                inputData -> new TestGroupEditor(inputData));
//        map.put(new QName("urn:grappa:tests:hierarchical:v0.0.1", "accepted-rd-kinds"),
//                inputData -> new TestGroupEditor(inputData));
    }

    @Override
    public QName[] getSupportedQNames() {
        return map.keySet().stream().toArray(size -> new QName[size]);
    }


    @Override
    public PluginEditor createEditorInstance(QName qName, PluginEditorModel inputData) throws Exception {
        return map.get(qName).factory(inputData);
    }
}
