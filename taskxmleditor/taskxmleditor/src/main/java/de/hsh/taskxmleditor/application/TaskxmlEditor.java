package de.hsh.taskxmleditor.application;

import de.hsh.taskxmleditor.plugin.PluginManager;
import de.hsh.taskxmleditor.presentation.ApplicationWindow;
import de.hsh.taskxmleditor.presentation.ErrorPanel;
import de.hsh.taskxmleditor.presentation.FatalErrorWindow;
import de.hsh.taskxmleditor.xsd.XsdIntrospection;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.xmlbeans.XmlObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.UIManager.LookAndFeelInfo;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;

public class TaskxmlEditor {
    public final static Logger log = LoggerFactory.getLogger(TaskxmlEditor.class);

    public static void main(String[] args) {
        try {
            for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }

            ToolTipManager.sharedInstance().setInitialDelay(0);
            ToolTipManager.sharedInstance().setDismissDelay(Integer.MAX_VALUE);

            Configuration.getInstance().init();

            PluginManager.init();
            loadSchemas();

            ApplicationWindow appWindow = new ApplicationWindow();
            appWindow.display();
        } catch (Exception oops) {
            log.error(ExceptionUtils.getStackTrace(oops));
            new FatalErrorWindow(new ErrorPanel(ExceptionUtils.getMessage(oops))).display();
        }
    }

    private static void loadSchemas() throws Exception {
        Configuration config = Configuration.getInstance();

        if (Configuration.SCHEMA_DIRECTORY.exists()) {
            File[] listOfFiles = Configuration.SCHEMA_DIRECTORY.listFiles();//(pathname -> pathname.getName().toLowerCase().endsWith(".xsd"));

            ArrayList<XmlObject> objs = new ArrayList<>();
            for (File f : listOfFiles) {
                try (InputStream is = new FileInputStream(f)) {
                    XmlObject obj = XmlObject.Factory.parse(is);
                    objs.add(obj);

                    try {
                        String tarNs = XsdIntrospection.extractTargetNamespace(obj);
                        config.getTargetNsToFileMapping().put(tarNs, f);
                        log.info("Schema file '{}' has been found and loaded", f);
                    } catch (Exception e) {
                        log.error("Failed to extract targetNamespace for file: " + f);
                        log.error(ExceptionUtils.getStackTrace(e));
                    }
                }
            }

            XsdIntrospection xsd = new XsdIntrospection(objs.toArray(new XmlObject[0]));
            config.setXsd(xsd);

            File taskXsdFile = config.getTargetNsToFileMapping().get(Configuration.TASK_XSD_TARGET_NAMESPACE);
            if (null == taskXsdFile)
                throw new Exception(String.format("No ProFormA schema file for namespace uri '%s' found in schema directory ('%s').\n\nCannot proceed.",
                        Configuration.TASK_XSD_TARGET_NAMESPACE, Configuration.SCHEMA_DIRECTORY.getAbsolutePath()));
            config.setTaskXsdFile(taskXsdFile);
        } else
            throw new Exception("Schema directory does not exist: " + Configuration.SCHEMA_DIRECTORY.getAbsolutePath());
    }
}
