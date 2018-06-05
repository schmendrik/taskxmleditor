package de.hsh.taskxmleditor.plugin;

//import de.hsh.taskxmleditor.plugin.grappa_plugin.GrappaPlugin;

import de.hsh.taskxmleditor.application.Configuration;
import de.hsh.taskxmleditor.generic_editor.GenericEditor;
import de.hsh.taskxmleditor.presentation.InvalidInput;
import de.hsh.taskxmleditor.dom.DomHelper;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.namespace.QName;
import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.List;

public class PluginManager {
    public final static Logger log = LoggerFactory.getLogger(PluginManager.class);
    // pair.left = plugin, pair.right = the plugin's config
    private static java.util.List<Pair<NamespacePlugin, Properties>> plugins;
    private static ArrayList<Pair<QName, PluginEditor>> circulatingEditors = new ArrayList<>();

    public static void init() throws Exception {
        plugins = loadPlugins();
        for (Pair<NamespacePlugin, Properties> p : plugins) {
            try {
                p.getLeft().init(p.getRight());
            } catch(Exception e) {
                log.error(ExceptionUtils.getStackTrace(e));
            }
        }
    }

    public static void clearCirculatingEditors() {
        circulatingEditors.clear();
    }

    public static void saveAllEditorChanges() {
        for (Pair<QName, PluginEditor> editor : circulatingEditors) {
            try {
                log.info("Saving changes for editor: " + editor.getKey());
                editor.getValue().saveInput();
            } catch (Exception e) {
                log.error("Could not save changes.");
                log.error(ExceptionUtils.getStackTrace(e));
            }
        }
    }

    private static List<Pair<NamespacePlugin, Properties>> loadPlugins() throws Exception {
        final String PLUGIN_CLASS_NAME = "de.hsh.taskxmleditor.Plugin";

        ArrayList<Pair<NamespacePlugin, Properties>> arr = new ArrayList<>();
        Configuration config = Configuration.getInstance();
        if (Configuration.PLUGIN_DIRECTORY.exists()) {
            File[] jarFiles = Configuration.PLUGIN_DIRECTORY.listFiles((pathname -> pathname.getName().toLowerCase().endsWith(".jar")));
            File[] propsFiles = Configuration.PLUGIN_DIRECTORY.listFiles((pathname -> pathname.getName().toLowerCase().endsWith(".properties")));


            for (File jarFile : jarFiles) {
                Object obj = null;

                try {
                    URLClassLoader classLoader = URLClassLoader.newInstance(new URL[]{jarFile.toURI().toURL()});
                    Class<?> clazz = Class.forName(PLUGIN_CLASS_NAME, true, classLoader);
                    obj = clazz.getConstructor().newInstance();
                } catch (Exception oops) {
                    log.error(org.apache.tika.utils.ExceptionUtils.getStackTrace(oops));
                }

                if (obj instanceof NamespacePlugin) {
                    NamespacePlugin np = (NamespacePlugin) obj;
                    log.info("Plugin successfully loaded from file: " + jarFile);
                    //arr.add(np);

                    Properties props = new Properties();
                    String pluginName = FilenameUtils.removeExtension(jarFile.getName());
                    for (File propsFile : propsFiles) {
                        String propsName = FilenameUtils.removeExtension(propsFile.getName());
                        if(propsName.equals(pluginName)) {
                            try(InputStream is = new FileInputStream(propsFile)) {
                                props.load(is);
                                log.info("Properties file found and loaded: " + propsFile);
                            } catch(Exception e) {
                                log.error(ExceptionUtils.getStackTrace(e));
                            }
                        }
                    }
                    arr.add(new ImmutablePair<>(np, props));
                } else {
                    log.error("Could not find class {} in file: {}", PLUGIN_CLASS_NAME, jarFile);
                }
            }
        } else
            throw new Exception("Plugin directory does not exist: " + Configuration.PLUGIN_DIRECTORY.getAbsolutePath());

        return arr;
    }

    public static int getCirculatingEditorCount() {
        return circulatingEditors.size();
    }

    public static void removeEditorFromCirculation(PluginEditor editor) {
        boolean remove = circulatingEditors.removeIf(p -> p.getValue().equals(editor));
        if (remove) {
            log.info("Editor removed from circulation.");
            log.debug("Remaining editor count: " + circulatingEditors.size());
        } else
            log.error("Couldn't remove editor from circulation: it's not there, where did it go?! " + editor);
    }

    public static Collection<Pair<QName, PluginEditor>> getCriculatingEditorList() {
        return Collections.unmodifiableList(circulatingEditors);
    }

    public static PluginEditor createEditor(QName qName, PluginEditorModel editorModel) {
        PluginEditor pluginEditor = null;
        for (Pair<NamespacePlugin, Properties> p : plugins) {
            NamespacePlugin plugin = p.getLeft();
            QName[] qnames = plugin.getSupportedQNames();
            if (Arrays.stream(qnames).anyMatch(qn -> DomHelper.qNamesAreEqual(qn, qName))) {
                try {
                    pluginEditor = plugin.createEditorInstance(qName, editorModel);

                    if(null == pluginEditor)
                        throw new Exception("Something went wrong in the plugin's createEditorInstance(). A null value was returned where a valid reference was expected.");

                    break;
                } catch (Exception e) {
                    log.error("Could not create plugin editor for " + qName);
                    log.error(ExceptionUtils.getStackTrace(e));
                }
            }
        }

        if (null == pluginEditor) {
            // No editor found in plugins, fall back to using a generic one
            pluginEditor = new GenericEditor(editorModel);
            pluginEditor.setPreferredSize(new Dimension(pluginEditor.getPreferredSize().width, 200));
        }

        circulatingEditors.add(new ImmutablePair<>(qName, pluginEditor));

        log.info("For QName " + qName + ", this editor was created: " + pluginEditor.getClass());
        return pluginEditor;
    }

    public static List<InvalidInput> doPluginValidation(boolean stopOnFirstError) {
        ArrayList<InvalidInput> a = new ArrayList<>();

        for (Pair<QName, PluginEditor> p : circulatingEditors) {
            try {
                List<InvalidInput> list = p.getRight().validateInput();

                if(null == list) // the plugin does not support validation
                    continue;

                // if, for some reason, the plugin editor developers neglect to
                // set faulty component, set the component at least to the plugin editor
                list.forEach(c -> {
                    if(null == c.getComponent())
                        c.setComponent(p.getRight());
                });
                a.addAll(list);
            } catch (Exception oops) {
                log.error(String.format("Plugin didn't properly catch exceptions... skipping this one (%s)", p.getKey()));
                // Cannot treat this exception itself as a validation error, because the user needs to be
                // able to fix his input errors. If he can't fix exceptions, the validation is forever gonna be
                // stuck with this exception
            }
        }

        return a;
    }
}
