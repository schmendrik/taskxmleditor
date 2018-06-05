package de.hsh.taskxmleditor.plugin;

import de.hsh.taskxmleditor.taskxml_v1_1.Task;
import org.w3c.dom.Node;

import javax.xml.namespace.QName;
import java.util.List;

public class PluginEditorModelImpl implements PluginEditorModel {
    private Node node;
    private Task task;
    private QName qn;
    private List<Object> anyContainer;

    public PluginEditorModelImpl(List<Object> anyContainer, QName qname, Node node, Task task) { //TaskInfoImpl taskInfo) {
        this.node = node;
        this.task = task;
        this.qn = qname;
        this.anyContainer = anyContainer;
    }

    @Override
    public Node getNode() {
        return node;
    }

    @Override
    public void saveNode(Node newNode) {
        if (null != node) {
            int i = anyContainer.indexOf(node);
            if (i != -1) {
                // replace old node with new one
                anyContainer.set(i, newNode);
            } else {
                anyContainer.add(newNode);
            }
        } else {
            anyContainer.add(newNode);
        }
        node = newNode;
    }

    @Override
    public QName getQName() {
        return qn;
    }

    @Override
    public Task getTask() {
        return task;
    }


}
