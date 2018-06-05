package de.hsh.taskxmleditor.plugin;

import de.hsh.taskxmleditor.taskxml_v1_1.Task;
import org.w3c.dom.Node;

import javax.xml.namespace.QName;

public interface PluginEditorModel {
    /***
     * Retrieves the node that contains the data which is displayed
     * and edited by plugin editors
     *
     * @return the node to be edited
     */
    Node getNode();

    /**
     * Any changes that are made to a node will eventually have to be saved
     * @param node
     */
    void saveNode(Node node);

    /**
     * Retrieves the qname that identifies the node (see getNode())
     * @return
     */
    QName getQName();

    //TaskInfo getTaskInfo();

    /**
     * This is the original, mutable Task... for now.
     * @return
     */
    Task getTask();
}
