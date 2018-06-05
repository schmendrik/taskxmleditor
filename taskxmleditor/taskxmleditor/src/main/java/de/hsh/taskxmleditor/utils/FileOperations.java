package de.hsh.taskxmleditor.utils;

import com.ibm.icu.text.CharsetDetector;
import com.ibm.icu.text.CharsetMatch;
import de.hsh.taskxmleditor.application.Configuration;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.tika.Tika;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;

public class FileOperations {
    public final static Logger log = LoggerFactory.getLogger(FileOperations.class);

    private static final Tika tika = new Tika();

    public static void addJarToClasspath(File file) throws Exception {
        Class<?>[] parameters = new Class[]{URL.class};
        ClassLoader contextClassLoader = Thread.currentThread()
                .getContextClassLoader();
        URLClassLoader classLoader = (URLClassLoader) contextClassLoader;
        Class<?> sysclass = URLClassLoader.class;
        URL jarUrl = file.toURI().toURL();
        Method method = sysclass.getDeclaredMethod("addURL", parameters);
        method.setAccessible(true);
        method.invoke(classLoader, new Object[]{jarUrl});
    }

    public static java.io.File getTaskXmlDirectoryPath() {
        java.io.File taskXml = Configuration.getInstance().getTaskXmlFile();
        java.io.File taskXmlDirectory = new java.io.File(
                FilenameUtils.getFullPathNoEndSeparator(taskXml.getAbsolutePath()));
        return taskXmlDirectory;
    }

    public static String getFilePathRelativeToTaskxml(java.io.File f) {
        java.nio.file.Path base = java.nio.file.Paths.get(getTaskXmlDirectoryPath().getAbsolutePath());
        java.nio.file.Path absolute = java.nio.file.Paths.get(f.getAbsolutePath());
        java.nio.file.Path relative = base.relativize(absolute);
        return relative.toString().replace("\\", "/");
    }

    public static String detectEncoding(File f) throws Exception {
        try (InputStream is = new BufferedInputStream(new FileInputStream(f))) {
            CharsetDetector detector = new CharsetDetector();
            detector.setText(is);
            CharsetMatch match = detector.detect();

            return match.getName();// .toUpperCase(Locale.ENGLISH);
        } catch (Exception e) {
            log.warn("Could not detect encoding for file " + f.getAbsolutePath());
            throw e;
        }
    }

    public static boolean isPlainText(File f) throws IOException {
        // Unfortunately, Files.probeContentType() is broken (see:
        // http://stackoverflow.com/questions/19711956/alternative-to-files-probecontenttype
        // (and google in general))
        // String mimeType = Files.probeContentType(Paths.get(f.toURI()));

        String mimeType = tika.detect(f);
        if (null == mimeType)
            throw new IllegalArgumentException("Cannot determine mime type for file " + f.getAbsolutePath());
        if (mimeType.contains("application")) {
            if (!mimeType.contains("xml"))
                return false;
        }
        return true;
    }

    public static void saveToFile(String str, File destFile, String encoding) throws Exception {
        if (null == str)
            str = "";
        try (Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(destFile), encoding))) {
            out.write(str);
        } catch (Exception oops) {
            log.error(ExceptionUtils.getStackTrace(oops));
            throw oops;
        }
    }

    public static String readFromFile(File sourceFile, String encoding) throws Exception {
        return new String(Files.readAllBytes(sourceFile.toPath()));
    }

    public static void createFileFromResource(String resource, File file) throws Exception {
        try (InputStream input = FileOperations.class.getResourceAsStream(resource)) {

            try (OutputStream out = new FileOutputStream(file)) {
                int read;
                byte[] bytes = new byte[1024];
                while ((read = input.read(bytes)) != -1) {
                    out.write(bytes, 0, read);
                }
            }

        } catch (Exception e) {
            log.error(ExceptionUtils.getStackTrace(e));
            throw e;
        }
    }

    /**
     * this is a workaround for methods like SchemaFactory.newSchema(). That
     * method takes in a File argument, but doesn't read it as a stream, which
     * is necessary for resources packed in a jar.
     * <p>
     * So we create a temp file from a resource file and pass that as the
     * argument.
     * <p>
     * See http://stackoverflow.com/a/14612564/4765331
     */
    public static void createTempFileFromResource(String resource) throws Exception {
        File file = File.createTempFile("tempfile", ".tmp");
        file.deleteOnExit();
        createFileFromResource(resource, file);
    }

    public static File createTempFileFromResource(InputStream input, String extension) throws Exception {
        File file = File.createTempFile("tempfile", extension);
        try (OutputStream out = new FileOutputStream(file)) {
            int read;
            byte[] bytes = new byte[1024];
            while ((read = input.read(bytes)) != -1) {
                out.write(bytes, 0, read);
            }
        }
        file.deleteOnExit();
        return file;
    }
}
