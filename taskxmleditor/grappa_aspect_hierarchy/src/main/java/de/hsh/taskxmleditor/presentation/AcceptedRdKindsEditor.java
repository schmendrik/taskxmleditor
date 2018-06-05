//package de.hsh.taskxmleditor.presentation;
//
//import de.hsh.taskxmleditor.Str;
//import de.hsh.taskxmleditor.grappa_aspect_hierarchy_v0_0_1.ComputingResourcesType;
//import de.hsh.taskxmleditor.grappa_aspect_hierarchy_v0_0_1.RdkindListType;
//import de.hsh.taskxmleditor.plugin.PluginEditorModel;
//import de.hsh.taskxmleditor.plugin.Marshaller;
//import de.hsh.taskxmleditor.plugin.PluginEditor;
//import de.hsh.taskxmleditor.presentation.document_filters.PositiveIntegerFilter;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.w3c.dom.Node;
//
//import javax.swing.*;
//import java.awt.*;
//
//public class AcceptedRdKindsEditor extends PluginEditor {
//    public final static Logger log = LoggerFactory.getLogger(AcceptedRdKindsEditor.class);
//    private PluginEditorModel model;
//    private RdkindListType data;
//    private AddRemoveItemsPanel items;
//
//    public AcceptedRdKindsEditor(PluginEditorModel model) throws Exception {
//        this.model = model;
//
//
//        JTextField maxRuntimeField = Gui.createTextField();
//        JTextField maxDiscField = Gui.createTextField();
//        JTextField maxMemField = Gui.createTextField();
//
//        setBorder(BorderFactory.createTitledBorder("Computing Resources"));
//        setLayout(new GridBagLayout());
//        add(new JLabel(Str.get("Grappa.MaxRuntime")), Gui.createGbc(0, 0, GridBagConstraints.VERTICAL));
//        add(Gui.combineWithHelp(maxRuntimeField, Str.get("Grappa.MaxRuntime.Help")), Gui.createGbc(1, 0, GridBagConstraints.VERTICAL));
//        add(new JLabel(Str.get("Grappa.MaxDiscQuota")), Gui.createGbc(0, 1, GridBagConstraints.VERTICAL));
//        add(Gui.combineWithHelp(maxDiscField, Str.get("Grappa.MaxDiscQuota.Help")), Gui.createGbc(1, 1, GridBagConstraints.VERTICAL));
//        add(new JLabel(Str.get("Grappa.MaxMemory")), Gui.createGbc(0, 2, GridBagConstraints.VERTICAL));
//        add(Gui.combineWithHelp(maxMemField, Str.get("Grappa.MaxMemory.Help")), Gui.createGbc(1, 2, GridBagConstraints.VERTICAL));
//
//        data = Marshaller.unmarshal(ComputingResourcesType.class, model.getNode());
//
//        if (null != data.getMaxDiscQuotaKib())
//            maxDiscField.setText(data.getMaxDiscQuotaKib().toString());
//        if (null != data.getMaxMemMib())
//            maxMemField.setText(data.getMaxMemMib().toString());
//        if (null != data.getMaxRuntimeSecondsWallclockTime())
//            maxRuntimeField.setText(data.getMaxRuntimeSecondsWallclockTime().toString());
//
//        maxDiscField.getDocument().addDocumentListener(new DocumentListenerAdapter() {
//            @Override
//            public void contentChanged() {
//                if (!maxDiscField.getText().isEmpty())
//                    data.setMaxDiscQuotaKib(Integer.parseInt(maxDiscField.getText()));
//                else
//                    data.setMaxDiscQuotaKib(null);
//            }
//        });
//        new PositiveIntegerFilter(maxDiscField, true);
//
//        maxMemField.getDocument().addDocumentListener(new DocumentListenerAdapter() {
//            @Override
//            public void contentChanged() {
//                if (!maxMemField.getText().isEmpty())
//                    data.setMaxMemMib(Integer.parseInt(maxMemField.getText()));
//                else
//                    data.setMaxMemMib(null);
//            }
//        });
//        new PositiveIntegerFilter(maxMemField, true);
//
//        maxRuntimeField.getDocument().addDocumentListener(new DocumentListenerAdapter() {
//            @Override
//            public void contentChanged() {
//                if (!maxRuntimeField.getText().isEmpty())
//                    data.setMaxRuntimeSecondsWallclockTime(Integer.parseInt(maxRuntimeField.getText()));
//                else
//                    data.setMaxRuntimeSecondsWallclockTime(null);
//            }
//        });
//        new PositiveIntegerFilter(maxRuntimeField, true);
//    }
//
//    @Override
//    public void saveInput() throws Exception {
//        Node n = Marshaller.marshal(ComputingResourcesType.class, model.getQName(), data);
//        model.saveNode(n);
//    }
//}
