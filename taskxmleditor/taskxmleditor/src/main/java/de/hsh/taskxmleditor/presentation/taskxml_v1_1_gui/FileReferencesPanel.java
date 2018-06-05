package de.hsh.taskxmleditor.presentation.taskxml_v1_1_gui;

import de.hsh.taskxmleditor.presentation.InputValidator;
import de.hsh.taskxmleditor.presentation.Gui;
import de.hsh.taskxmleditor.presentation.InvalidInput;
import de.hsh.taskxmleditor.taskxml_v1_1.File;
import de.hsh.taskxmleditor.taskxml_v1_1.Fileref;
import de.hsh.taskxmleditor.taskxml_v1_1.Filerefs;
import de.hsh.taskxmleditor.taskxml_v1_1.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Consumer;


/**
 * This can be used for displaying and editing file references for both
 * normal file references and external references.
 */
public class FileReferencesPanel extends JPanel implements InputValidator {
    final static Logger log = LoggerFactory.getLogger(FileReferencesPanel.class);
    private DefaultTableModel model;
    private JTable table;
    private Task task;
    private TitledBorder titledBorder;
    private TitledBorder highLightBorder;
    //    private Filerefs refs;
    private Filerefs refs;
    private boolean atLeastOneItemRequired;

    private static final int ID_COLUMN_INDEX = 0;

    private Consumer<Integer> fileRefCountChanged;

    public FileReferencesPanel(Task task, Filerefs refs, String title, String helpText, boolean atLeastOneItemRequried) {
        this.task = task;
        this.refs = refs;
        this.atLeastOneItemRequired = atLeastOneItemRequried;
        //this.allRefs = allRefs;

        model = new DefaultTableModel(new Object[]{title, "Info"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(model);
        table.setTableHeader(null);
        table.setShowGrid(false);
        table.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);

        JScrollPane scrollTable = Gui.createScrollPane(table);
        //scrollTable.setPreferredSize(new Dimension(700, 200));

        // scrollTable.setColumnHeader(null);
        // scrollTable.setMinimumSize(new Dimension(100, 80));

        // JLabel caption = new JLabel("Table-Name:");
        // caption.setHorizontalAlignment(SwingConstants.LEFT);

        JButton add = new JButton("Add");
        add.addActionListener(l -> addReferences());
        JButton remove = new JButton("Remove");
        remove.addActionListener(l -> removeSelectedRefsFromTableAndModel());
        JPanel buttons = new JPanel();
        buttons.setLayout(new BoxLayout(buttons, BoxLayout.X_AXIS));
        buttons.add(add);
        buttons.add(remove);
        buttons.add(Box.createHorizontalGlue());
        JLabel help = new JLabel();
        help.setIcon(new ImageIcon(Gui.helpIcon2424));
        help.setToolTipText(helpText);
        buttons.add(help);

        setLayout(new BorderLayout());
        titledBorder = BorderFactory.createTitledBorder(title);
        highLightBorder = BorderFactory.createTitledBorder(title);
        highLightBorder.setTitleColor(Color.RED);
        setBorder(titledBorder);
        add(scrollTable, BorderLayout.CENTER);
        add(buttons, BorderLayout.SOUTH);

        if (null != refs) {
            int before = model.getRowCount();
            for (Fileref r : refs.getFileref()) {
                File f = findFile(r.getRefid());
                if(null != f) {
                    model.addRow(new Object[]{r.getRefid(), getFileInfo(f)});
                }
                else {
                    model.addRow(new Object[]{r.getRefid(), "This ID does not exist"});
                    log.warn("A file reference (id '{}') does not actually exist.", r.getRefid());
                }
            }

            if(before != model.getRowCount())
                callRefCountConsumer();
        }

        maybeHighlight();
    }

    public Filerefs getModel() {
        return refs;
    }

    public void setFileRefCountChanged(Consumer<Integer> consumer) {
        fileRefCountChanged = consumer;
    }

    private void callRefCountConsumer() {
        if(null != fileRefCountChanged)
            fileRefCountChanged.accept(model.getRowCount());
    }

    /**
     * need to put this code into RequiredInputControl or something
     */
    public void maybeHighlight() {
        if(atLeastOneItemRequired) {
            if (!isEnabled())
                setBorder(titledBorder);
            else if (table.getModel().getRowCount() > 0)
                setBorder(titledBorder);
            else
                setBorder(highLightBorder);

            repaint();
        }
    }

    public JTable getTable() {
        return table;
    }

    private void addReferences() {
        Object[] columnNames = {"Select", "File ID", "Info"};

        ArrayList<Object[]> data = new ArrayList<>();
        for (File file : task.getFiles().getFile()) {
            boolean selectable = true;
            for (int i = 0; i < model.getRowCount(); i++) {
                if (model.getValueAt(i, 0).toString().equals(file.getId())) {
                    selectable = false;
                    break;
                }
            }
            if (selectable) {
                String info;
                if (file.getType().equals("embedded"))
                    info = file.getFilename();
                else
                    info = file.getValue();
                data.add(new Object[]{false, file.getId(), info});
            }
        }

        Object[][] rowValues = new Object[data.size()][];
        for (int i = 0; i < data.size(); i++) {
            rowValues[i] = data.get(i);
        }

        DefaultTableModel model = new DefaultTableModel(rowValues, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 0; // only first column (checkbox) is editable
            }
        };

        JTable addRefsTable = new JTable(model) {
            /*@Override
            public Class getColumnClass(int column) {
            return getValueAt(0, column).getClass();
            }*/

            @Override
            public Class getColumnClass(int column) {
                switch (column) {
                    case 0:
                        return Boolean.class;
                    case 1:
                        return String.class;
                    case 2:
                        return String.class;
                }
                return String.class;
            }
        };
        addRefsTable.setPreferredScrollableViewportSize(addRefsTable.getPreferredSize());
        addRefsTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        Gui.resizeJTableColumnWidth(addRefsTable);
        JScrollPane addRefsTablePane = new JScrollPane(addRefsTable);
        addRefsTablePane.setPreferredSize(new Dimension(400, 400));

        addRefsTable.setTableHeader(null);

        JComponent display;
        if (addRefsTable.getRowCount() > 0)
            display = addRefsTablePane;
        else
            display = new JLabel("There are no more references left to add.");

        int reply = JOptionPane.showConfirmDialog(null, display,
                "Add references", JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);

        if (reply == JOptionPane.OK_OPTION) {
            for (int i = 0; i < addRefsTable.getRowCount(); i++) {
                boolean isChecked = (boolean) addRefsTable.getValueAt(i, 0);
                String id = (String) addRefsTable.getValueAt(i, 1);
                if (isChecked) {
                    File file = findFile(id);
                    addRef(file);
                }
            }

            revalidate();
            repaint();
        }
    }

//    private void addRef(String id, String info) {
//        model.addRef(new Object[]{id, info});
//    }


    private File findFile(String fileId) {
        for (File file : task.getFiles().getFile()) {
            if(file.getId().equals(fileId))
                return file;
        }
        return null;
    }

    private String getFileInfo(File file) {
        if (file.getType().equals("embedded"))
            return file.getFilename();
        else
            return file.getValue();
    }

    /**
     * Adds a new file reference to both the Jtable and the Task model.
     *
     * @param file
     */
    private void addRef(File file) {
        model.addRow(new Object[]{file.getId(), getFileInfo(file)});
        Fileref r = new Fileref();
        r.setRefid(file.getId());
        java.util.List<Fileref> fileref = refs.getFileref();
        fileref.add(r);

        callRefCountConsumer();
        maybeHighlight();
    }

    private void removeSelectedRefsFromTableAndModel() {
        ArrayList<String> idsToRemove = new ArrayList<>();
        Arrays.stream(table.getSelectedRows()).forEach(row -> {
            String id = (String) table.getValueAt(row, ID_COLUMN_INDEX);
            idsToRemove.add(id);
        });

        for(String idToRemove : idsToRemove) {
            for(int i=0; i<model.getRowCount(); i++) {
                String id = (String) table.getValueAt(i, ID_COLUMN_INDEX);
                if(id.equals(idToRemove)) {
                    model.removeRow(i);
                    // remove from task model too
                    for (Fileref ref : refs.getFileref()) {
                        if (ref.getRefid().equals(id)) {
                            refs.getFileref().remove(ref);
                            break;
                        }
                    }
                    break;
                }
            }
        }

        callRefCountConsumer();
        maybeHighlight();
    }

    public int getFileReferenceCount() {
        return model.getRowCount();
    }

    public String[] getFileReferenceStrings() {
        int rows = model.getRowCount();
        ArrayList<String> list = new ArrayList<>(rows);
        for (int i = 0; i < rows; i++)
            list.add(model.getValueAt(i, 0).toString());
        return list.toArray(new String[rows]);
    }

    @Override
    public java.util.List<InvalidInput> validateInput() {
        ArrayList<InvalidInput> a = new ArrayList<>();

        String[] ids = getFileReferenceStrings();

        // using the table as the component doesn't make the balloon pop up for some reason,
        // so i'll use the jpanel itself


        if(atLeastOneItemRequired && ids.length == 0)
            a.add(new InvalidInput("FileReference", this, "At least one file reference is required"));

        for (String id : ids) {
            if(!task.getFiles().getFile().stream().anyMatch(f -> f.getId().equals(id)))
                a.add(new InvalidInput("FileReference", this, String.format("File ID '%s' does not exist (refer to the Files tab)", id)));
        }

        return a;
    }
}


//package de.hsh.taskxmleditor.presentation.taskxml_v1_1_gui;
//
//import de.hsh.taskxmleditor.presentation.Gui;
//import de.hsh.taskxmleditor.taskxml_v1_1.Task;
//import org.apache.commons.lang3.tuple.Pair;
//
//import javax.swing.*;
//import javax.swing.table.DefaultTableModel;
//import java.awt.*;
//import java.util.ArrayList;
//import java.util.concurrent.Callable;
//
//
///**
// * This can be used for displaying and editing file references for both
// * normal file references and external references.
// */
//public class FileReferencesPanel extends JPanel {
//    private DefaultTableModel model;
//    private JTable table;
//    private Task task;
//    private Callable<java.util.List<Pair<String, String>> getAllAvailableReferenceStrings;
//
//
//    /**
//     *
//     * @param title
//     * @param initialRefs a list of pairs with the left value being the refId and the
//     *                    right value being the fileName/filePath
//     * @param getAllAvailableReferenceStrings
//     */
//    public FileReferencesPanel(String title, java.util.List<org.apache.commons.lang3.tuple.Pair<String, String>> initialRefs,
//                           Callable<java.util.List<Pair<String, String>>> getAllAvailableReferenceStrings) {
//        this.getAllAvailableReferenceStrings = getAllAvailableReferenceStrings;
//
//        model = new DefaultTableModel(new Object[]{title}, 0);
//        table = new JTable(model);
//        table.setTableHeader(null);
//        table.setShowGrid(true);
//        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
//
//        JScrollPane scrollTable = Gui.createScrollPane(table);
//
//        // scrollTable.setColumnHeader(null);
//        // scrollTable.setMinimumSize(new Dimension(100, 80));
//
//        // JLabel caption = new JLabel("Table-Name:");
//        // caption.setHorizontalAlignment(SwingConstants.LEFT);
//
//        JButton add = new JButton("Add");
//        add.addActionListener(l -> addReferences());
//        JButton remove = new JButton("Remove");
//        remove.addActionListener(l -> removeSelectedString());
//        JButton edit = new JButton("Edit");
//        edit.addActionListener(
//                l -> JOptionPane.showMessageDialog(this, "TODO...\nJust double-click an item to edit ..."));
//        JPanel buttons = new JPanel();
//        buttons.setLayout(new BoxLayout(buttons, BoxLayout.X_AXIS));
//        buttons.add(add);
//        buttons.add(remove);
//        buttons.add(edit);
//
//        setLayout(new BorderLayout());
//        setBorder(BorderFactory.createTitledBorder(title));
//        add(scrollTable, BorderLayout.CENTER);
//        add(buttons, BorderLayout.SOUTH);
//        Dimension prefSize = getPreferredSize();
//        prefSize.height = 300;
//
//        setPreferredSize(prefSize);
//        // setPreferredSize(getPreferredSize());
//        // setMaximumSize(new Dimension(100, 200));
//        // Dimension d = getPreferredSize();
//        // d.width = 50;
//        // setMaximumSize(d);
//    }
//
//    private void addReferences() {
//        DefaultTableModel notYetAddedRefs = new DefaultTableModel(new Object[]{"References"}, 0);
//        JTable addRefsTable = new JTable(model);
//        addRefsTable.setTableHeader(null);
//        addRefsTable.setShowGrid(true);
//        addRefsTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
//
//        // find references that haven't been added by the user yet and
//        // display in the 'add more references' dialog.
//        java.util.List<Pair<String, String>> refs = null;
//        try {
//            refs = getAllAvailableReferenceStrings.call();
//        } catch (Exception e) {
//            System.out.println(e.getStackTrace());
//        }
//        for (Pair<String, String> ref : refs) {
//            boolean show = true;
//            for (int i = 0; i < model.getRowCount(); i++) {
//                if (model.getValueAt(i, 0).toString().equals(ref.getLeft())) {
//                    show = false;
//                    break;
//                }
//            }
//            if (show)
//                notYetAddedRefs.addRow(new Object[]{ref});
//        }
//
//        int reply = JOptionPane.showConfirmDialog(this, addRefsTable,
//                "Add references", JOptionPane.OK_CANCEL_OPTION,
//                JOptionPane.PLAIN_MESSAGE);
//
//        if (reply == JOptionPane.OK_OPTION) {
//            for (int i = 0; i < addRefsTable.getRowCount(); i++) {
//                model.addRow(new Object[]{addRefsTable.getValueAt(i, 0)});
//            }
//        }
//    }
//
//    //    public void addString(String str) {
////        model.addRow(new Object[]{str});
////    }
////
//    private void removeSelectedString() {
//        if (-1 != table.getSelectedRow())
//            model.removeRow(table.getSelectedRow());
//    }
//
//    public String[] getFileReferenceStrings() {
//        int rows = model.getRowCount();
//        ArrayList<String> list = new ArrayList<>(rows);
//        for (int i = 0; i < rows; i++)
//            list.add(model.getValueAt(i, 0).toString());
//        return list.toArray(new String[rows]);
//    }
//}
