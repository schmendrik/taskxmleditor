package de.hsh.taskxmleditor.presentation;

import de.hsh.taskxmleditor.grappa_aspect_hierarchy_v0_0_1.TestGroupType;
import de.hsh.taskxmleditor.plugin.PluginEditorModel;
import de.hsh.taskxmleditor.plugin.PluginEditor;
import de.hsh.taskxmleditor.plugin.PluginHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;

import javax.xml.bind.Binder;
import java.awt.*;

public class TestGroupEditor extends PluginEditor {
    public final static Logger log = LoggerFactory.getLogger(TestGroupEditor.class);
    private PluginEditorModel model;
    private TestGroupType data;

    private Binder<Node> binder;
    private TestGroupPanel testGroup;

    public TestGroupEditor(PluginEditorModel model) throws Exception {
        this.model = model;

        data = PluginHelper.unmarshal(TestGroupType.class, model.getNode());

        testGroup = new TestGroupPanel(model.getTask(), data);
        setLayout(new BorderLayout());
        add(testGroup, BorderLayout.CENTER);
    }

    @Override
    public void saveInput() throws Exception {
        //Node node = new PluginHelper<TestGroupType>().update(TestGroupType.class, data, model.getNode());
        Node node = PluginHelper.marshal(TestGroupType.class, model.getQName(), data);
        model.saveNode(node);
    }

    @Override
    public java.util.List<InvalidInput> validateInput() {
        return testGroup.validateInput();
    }
}
