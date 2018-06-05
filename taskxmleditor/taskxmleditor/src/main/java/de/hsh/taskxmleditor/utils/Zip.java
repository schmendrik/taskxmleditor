package de.hsh.taskxmleditor.utils;

import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Zip {
    private final static Logger log = LoggerFactory.getLogger(Zip.class);

    /**
     *
     * @param destinationZipFile
     * @param files a pair with the left being the source file and the right being the
     *              relative file path to the task.xml within the zip (e.g. attachment/something.txt)
     * @throws Exception
     */
    public static void createZip(File destinationZipFile, List<Pair<File, String>> files) throws Exception {
        byte[] buffer = new byte[1024];

        try (FileOutputStream fos = new FileOutputStream(destinationZipFile)) {
            try (ZipOutputStream zos = new ZipOutputStream(fos)) {
                for (Pair<File, String> file : files) {
                    log.debug("Zipping file: " + file.getLeft().getName());

                    log.debug("\tWill appear in the zip with this path: " + file.getRight());
                    ZipEntry ze = new ZipEntry(file.getRight());
                    zos.putNextEntry(ze);
                    try (FileInputStream in = new FileInputStream(file.getLeft().getAbsolutePath())) {
                        int len;
                        while ((len = in.read(buffer)) > 0)
                            zos.write(buffer, 0, len);
                    }
                    zos.closeEntry();
                }
                log.debug("Zipping's done");
            }
        }
    }
}
