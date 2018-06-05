package de.hsh.taskxmleditor.presentation;

import de.hsh.taskxmleditor.application.Configuration;
import de.hsh.taskxmleditor.plugin.PluginManager;
import de.hsh.taskxmleditor.presentation.taskxml_v1_1_gui.TaskxmlPanel;
import de.hsh.taskxmleditor.taskxml_v1_1.Task;
import de.hsh.taskxmleditor.utils.FileOperations;
import de.hsh.taskxmleditor.utils.Zip;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.xml.bind.UnmarshalException;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class ApplicationWindow extends JFrame implements Observer {
  private static final long serialVersionUID = 1L;
  final static Logger log = LoggerFactory.getLogger(ApplicationWindow.class);
  private static final String TITLE = "Taskxml Editor";
  public static final JFileChooser fileChooser = new JFileChooser();
  private TaskxmlPanel taskxmlPanel;

  public static FileNameExtensionFilter xml = new FileNameExtensionFilter("XML Files", "xml");
  public static FileNameExtensionFilter zip = new FileNameExtensionFilter("ZIP Files", "zip");

  private JMenuItem menuItemSave;
  private JMenuItem menuItemSaveAs;
  private JMenuItem menuItemSaveAsZip;
  private JMenuItem menuItemXsdValidate;
  private JMenuItem menuItemSmartValidation;

  public ApplicationWindow() {
    setTitle(TITLE);
    setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    setExtendedState(JFrame.MAXIMIZED_BOTH);
    setJMenuBar(createMenu());

    fileChooser.setFileFilter(xml);
    fileChooser.setPreferredSize(new Dimension(600, 600));

    Configuration.getInstance().addObserver(this);
  }

  public void display() {
    SwingUtilities.invokeLater(() -> {
      setLocationRelativeTo(null);
      setVisible(true);
    });
  }

  private void loadTask(File file) {
    setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    boolean loadedSuccessfully = false;
    PluginManager.clearCirculatingEditors();
    try {
      String mimeType = Files.probeContentType(Paths.get(file.toURI()));
      log.debug("mime type: " + mimeType);
      String ext = FilenameUtils.getExtension(file.getAbsolutePath());
      log.debug("extension: " + ext);
      if (ext.equalsIgnoreCase("zip")) {
        log.debug("Cannot load zip files yet");
        JOptionPane.showMessageDialog(this, "Loading Zip files is not supported yet.");
        return;
      } else if (ext.equalsIgnoreCase("xml")) {
        Configuration.getInstance().loadTaskXml(file);
        log.debug("Task file loaded: {}", file);
        loadedSuccessfully = true;
      } else
        throw new Exception("mime type " + mimeType + " is not supported");
    } catch (UnmarshalException e) {
      log.error("Could not load task file: " + file);
      log.error(ExceptionUtils.getStackTrace(e));
      StringBuffer sb = new StringBuffer();
      sb.append(String.format("Could not load task file '%s'.", file));
      sb.append(String.format("\n\nPlease note that this editor only supports the ProFormA namespace '%s'.",
              Configuration.TASK_XSD_TARGET_NAMESPACE));
//            sb.append(String.format("\n\nIf this is actually the case, you could try tricking the editor into" +
//                    " loading the task file by changing the task file's incompatible" +
//                    " XML namespace into the ProFormA verison mentioned above." +
//                    " This does not guarantee that the editor will be able to handle the outdated task." +
//                    " Depending on how old the task's version is, the editor might be able to handle" +
//                    " the task."));
      sb.append("\n\nOriginal error message:\n\n" + ExceptionUtils.getRootCauseMessage(e));
      setContent(new ErrorPanel(sb.toString()));
    } catch (Exception e) {
      log.error("Could not load task file: " + file);
      log.error(ExceptionUtils.getStackTrace(e));
      setContent(new ErrorPanel(e.getMessage()));
    } finally {
      setCursor(Cursor.getDefaultCursor());
    }
    menuItemSave.setEnabled(loadedSuccessfully);
    menuItemSaveAs.setEnabled(loadedSuccessfully);
    menuItemSaveAsZip.setEnabled(loadedSuccessfully);
    menuItemXsdValidate.setEnabled(loadedSuccessfully);
    menuItemSmartValidation.setEnabled(loadedSuccessfully);
  }

  /**
   * @param filter
   * @param saveDialog true, if you want to save, or false if you want to open something
   * @return
   */
  private File displayFileChooserDialog(FileNameExtensionFilter filter, boolean saveDialog) {
    // Allow only the one filter to be used and remove the others
    Arrays.stream(fileChooser.getChoosableFileFilters()).forEach(f -> fileChooser.removeChoosableFileFilter(f));
    fileChooser.setFileFilter(filter);
    int result;
    if (saveDialog)
      result = fileChooser.showSaveDialog(this);
    else
      result = fileChooser.showOpenDialog(this);
    if (result == JFileChooser.APPROVE_OPTION) {
      File file = fileChooser.getSelectedFile();
      log.debug("File chooser: " + file.getName());
      return file;
    }
    return null;
  }

  private JMenuBar createMenu() {
    JMenuItem menuItemCreateNewTask = new JMenuItem("New");
    menuItemCreateNewTask.addActionListener(l -> {
      // there is no support for adding blank tasks yet
      // createNewTask();
    });

    JMenuItem openZipItem = new JMenuItem("Open Zip...", KeyEvent.VK_O);
    openZipItem.setEnabled(false);
    openZipItem.addActionListener(e -> {
      // no support for opening task zips either
    });

    menuItemSave = new JMenuItem("Save", KeyEvent.VK_S);
    menuItemSave.setEnabled(false);
    menuItemSave.addActionListener(e -> save());

    menuItemSaveAs = new JMenuItem("Save As...", KeyEvent.VK_A);
    menuItemSaveAs.setEnabled(false);
    menuItemSaveAs.addActionListener(e -> {
      File file = displayFileChooserDialog(xml, true);
      if (null != file) {
        if (!file.getAbsolutePath().endsWith(".xml"))
          file = new File(file.getAbsolutePath() + ".xml");
        saveAs(file, true);
      }
    });

    menuItemSaveAsZip = new JMenuItem("Save As ZIP");
    menuItemSaveAsZip.setEnabled(false);
    menuItemSaveAsZip.addActionListener(l -> {
      File file = displayFileChooserDialog(zip, true);
      if (null != file) {
        if (!file.getAbsolutePath().endsWith(".zip"))
          file = new File(file.getAbsolutePath() + ".zip");

        boolean includeXsd = Boolean.parseBoolean(Configuration.getPropertiesKey("includeXSDFilesIntoZipArchive", "true"));
        saveAsZip(file, includeXsd);
      }
    });

    JMenuItem menuItemOpen = new JMenuItem("Open...", KeyEvent.VK_O);
    menuItemOpen.addActionListener(e -> {
      File file = displayFileChooserDialog(xml, false);
      if (null != file) {
        loadTask(file);
      }
    });

    JMenuItem exit = new JMenuItem("Exit");
    exit.addActionListener(e -> System.exit(0));

    JMenuItem about = new JMenuItem("About");
    about.addActionListener(e -> {
      JOptionPane.showMessageDialog(this, new AboutDialog());
    });

    menuItemXsdValidate = new JMenuItem("XML Validation");
    menuItemXsdValidate.addActionListener(e -> {
      try {
        Configuration.getInstance().validate();
        JOptionPane.showMessageDialog(this, String.format("No errors.", "XML Validation", JOptionPane.INFORMATION_MESSAGE));
      } catch (SAXException oops) {
        Throwable error = ExceptionUtils.getRootCause(oops);//oops.getLinkedException();
        //log.warn(ExceptionUtils.getStackTrace(oops));
        JOptionPane.showMessageDialog(this, String.format("XML validation failed with the following error:\n\n%s", error.getMessage()), "XML Validation Error", JOptionPane.ERROR_MESSAGE);
      } catch (Exception oops) {
        log.error(ExceptionUtils.getStackTrace(oops));
        JOptionPane.showMessageDialog(this, oops.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
      }
    });

    menuItemSmartValidation = new JMenuItem("Value Validation");
    menuItemSmartValidation.addActionListener(e -> {
      performSmartValidation();
    });

    JMenu fileMenu = new JMenu("File");
    JMenu helpMenu = new JMenu("Help");

    fileMenu.setMnemonic(KeyEvent.VK_F);
    fileMenu.add(menuItemOpen);
//        fileMenu.add(menuItemCreateNewTask);
    fileMenu.add(new JSeparator());
    fileMenu.add(menuItemSave);
    fileMenu.add(menuItemSaveAs);
    fileMenu.add(menuItemSaveAsZip);
    fileMenu.add(new JSeparator());
    fileMenu.add(exit);

    helpMenu.add(menuItemXsdValidate);
    helpMenu.add(menuItemSmartValidation);
    helpMenu.add(new JSeparator());
    helpMenu.add(about);

    JMenuBar menuBar = new JMenuBar();
    menuBar.add(fileMenu);
    menuBar.add(helpMenu);

    // Initially, these are disabled until a task file has been loaded,
    // at which point they are enabled
    menuItemSave.setEnabled(false);
    menuItemSaveAs.setEnabled(false);
    menuItemSaveAsZip.setEnabled(false);
    menuItemXsdValidate.setEnabled(false);
    menuItemSmartValidation.setEnabled(false);

    return menuBar;
  }

  private void performSmartValidation() {
    ArrayList<InvalidInput> errors = new ArrayList<>();
    errors.addAll(taskxmlPanel.validateInput());
    errors.addAll(PluginManager.doPluginValidation(true));
    log.debug("Input Validation: {} errors/warnings found", errors.size());
    if (errors.size() > 0) {
      InvalidInput error = errors.get(0);
      Gui.focus(error.getComponent());
      new TimedBalloon((JComponent) error.getComponent(), error.getError());
    } else
      JOptionPane.showMessageDialog(this, String.format("No errors.", "Validation", JOptionPane.INFORMATION_MESSAGE));
  }

//  private void createNewTask() {
//    try {
//      File file = displayFileChooserDialog(xml, true);
//      if (null != file) {
//        Task task = new Task();
//        Configuration.getInstance().saveTaskXml(task, file);
//        loadTask(file);
//      }
//    } catch (Exception oops) {
//      log.error(ExceptionUtils.getStackTrace(oops));
//      JOptionPane.showMessageDialog(this, String.format("Could not save to file. Error:\n\n%s", oops.getMessage()), "Create New Task", JOptionPane.ERROR_MESSAGE);
//    }
//  }

  private boolean save() {
    return saveAs(Configuration.getInstance().getTaskXmlFile(), true);
  }

  private boolean saveAs(File destination, boolean reloadWithDestinationFile) {
    if (null != Configuration.getInstance().getTask()) {
      setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

      try {
        boolean definitelySave = true;
        try {
          if (Boolean.parseBoolean(Configuration.getPropertiesKey
                  ("performXSDValidationBeforeSaving", "true")))
            Configuration.getInstance().validate();
        } catch (SAXException | Configuration.CombiningSchemaException oops) {
          log.warn("Validation failed");
          log.warn(ExceptionUtils.getStackTrace(oops));
          Throwable error = ExceptionUtils.getRootCause(oops);
          error = null != error ? error : oops;
          JOptionPane.showMessageDialog(this, String.format("XML validation failed with the following error:\n\n%s", error.getMessage()), "XML Validation Error", JOptionPane.ERROR_MESSAGE);

          // TODO: offer a "don't bother me anymore" checkbox:
          int reply = JOptionPane.showConfirmDialog(this, "Do you still want to save?", "Saving", JOptionPane.YES_NO_OPTION);
          definitelySave = reply == JOptionPane.YES_OPTION;
        } catch (Exception oops) {
          log.error(ExceptionUtils.getStackTrace(oops));
          JOptionPane.showMessageDialog(this, oops.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        if (definitelySave)
          actuallySaveTask(destination, reloadWithDestinationFile);
        return true;
      } catch (Exception e) {
        log.error(ExceptionUtils.getStackTrace(e));
        JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
      } finally {
        setCursor(Cursor.getDefaultCursor());
      }
    }
    return false;
  }

  private void actuallySaveTask(File destination, boolean reloadWithDestinationFile) throws Exception {
    log.info("Saving to: " + destination);
    Configuration config = Configuration.getInstance();
    try (FileOutputStream fos = new FileOutputStream(destination)) {
      config.saveTaskXml(config.getTask(), fos);
    }
    File currTaskxml = config.getTaskXmlFile();
    if (reloadWithDestinationFile && !currTaskxml.getAbsolutePath().equals(destination.getAbsolutePath()))
      loadTask(destination);
  }

  private void saveAsZip(File destination, boolean includeXsdFiles) {
    try {
      Task task = Configuration.getInstance().getTask();
      File taskxmlDir = FileOperations.getTaskXmlDirectoryPath();
      ArrayList<Pair<File, String>> filesToZip = new ArrayList<>(task.getFiles().getFile().size());

      File editedTaskXml = File.createTempFile("task", ".xml");

      if (!saveAs(editedTaskXml, false))
        return;

      Pair<File, String> rootTaskxml = new ImmutablePair<>(editedTaskXml, "task.xml");
      filesToZip.add(rootTaskxml);

      for (de.hsh.taskxmleditor.taskxml_v1_1.File file : task.getFiles().getFile()) {
        if (file.getType().equals("file")) {
          String relativeFilePath = file.getValue();
          File fullFilePath = Paths.get(taskxmlDir.getAbsolutePath(), relativeFilePath).toFile();
          String relPath = FileOperations.getFilePathRelativeToTaskxml(fullFilePath);
          filesToZip.add(new ImmutablePair<>(fullFilePath, relPath));
        }
      }

      if (includeXsdFiles) {
        File taskXsdFile = Configuration.getInstance().getTaskXsdFile();
        filesToZip.add(new ImmutablePair<>(taskXsdFile, taskXsdFile.getName()));
        HashMap<String, File> inUse = Configuration.getInstance().getAllExtensionXsdFilesThatTheTaskActuallyUses();
        for (Map.Entry<String, File> e : inUse.entrySet()) {
          ImmutablePair<File, String> p = new ImmutablePair<>(e.getValue().getAbsoluteFile(), e.getValue().getName());
          filesToZip.add(p);
        }
      }

      Zip.createZip(destination, filesToZip);
      editedTaskXml.deleteOnExit();
    } catch (Exception e) {
      log.error(ExceptionUtils.getStackTrace(e));
    }
  }

  private void setContent(JComponent content) {
    setContentPane(content);
    revalidate();
    repaint();
  }

  @Override
  public void update(Observable o, Object arg) {
    if (o.equals(Configuration.getInstance())) {
      setTitle(TITLE + " - " + Configuration.getInstance().getTaskXmlPath());
      taskxmlPanel = new TaskxmlPanel(Configuration.getInstance().getTask());
      setContent(taskxmlPanel);
    }
  }
}