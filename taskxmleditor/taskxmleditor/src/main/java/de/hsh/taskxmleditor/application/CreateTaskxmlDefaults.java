package de.hsh.taskxmleditor.application;

import de.hsh.taskxmleditor.taskxml_v1_1.*;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.List;

public class CreateTaskxmlDefaults {
    private static int fileId = 0;
    private static int extResId = 0;
    private static int modSolId = 0;
    private static int testId = 0;

    private static String createIdString(int id) {
        return "id" + StringUtils.leftPad(Integer.toString(id), 3, "0");
    }

    private static boolean fileIdExists(String id, List<File> existingFiles) {
        for (File f : existingFiles)
            if (null != f.getId() && f.getId().equals(id))
                return true;
        return false;
    }

    private static boolean extResIdExists(String id, List<ExternalResource> existingRes) {
        for (ExternalResource r : existingRes)
            if (null != r.getId() && r.getId().equals(id))
                return true;
        return false;
    }

    public static String createFileId(List<File> existingFiles) {
        String id = createIdString(fileId);
        while (fileIdExists(id, existingFiles))
            id = createIdString(++fileId);
        return id;
    }

    private static String createExternalResourceId(List<ExternalResource> existingRes) {
        String id = createIdString(extResId);
        while (extResIdExists(id, existingRes))
            id = createIdString(++extResId);
        return id;
    }

    public static File createFile(List<File> existingFiles) {
        File f = new File();
        f.setId(createFileId(existingFiles));
        f.setClazz("internal");
        // f.setComment(""); // not necessary
        f.setFilename("filename");
        f.setType("embedded");
        return f;
    }

    public static ExternalResource createExternalResource(List<ExternalResource> existingRes) {
        ExternalResource res = new ExternalResource();
        res.setId(createExternalResourceId(existingRes));
        return res;
    }

    public static ArchiveRestrType createArchiveRestrType() {
        ArchiveRestrType a = new ArchiveRestrType();
        a.setUnpackFilesFromArchiveRegexpOrFileRestrictions("");
        return a;
    }

    public static ModelSolution createModelSolution(List<ModelSolution> existingSolutions) {
        ModelSolution m = new ModelSolution();
        m.setId(createModelSolutionId(existingSolutions));
        return m;
    }

    private static String createModelSolutionId(List<ModelSolution> existingSolutions) {
        String id = createIdString(modSolId);
        while (modSolIdExists(id, existingSolutions))
            id = createIdString(++modSolId);
        return id;
    }

    private static boolean modSolIdExists(String id, List<ModelSolution> existingSolutions) {
        for (ModelSolution r : existingSolutions)
            if (null != r.getId() && r.getId().equals(id))
                return true;
        return false;
    }

    public static Test createTest(List<Test> existingTests) {
        Test m = new Test();
        m.setId(createTestId(existingTests));
        m.setValidity(new BigDecimal(1.0));
        return m;
    }

    private static String createTestId(List<Test> existingTests) {
        String id = createIdString(testId);
        while (testIdExists(id, existingTests))
            id = createIdString(++testId);
        return id;
    }

    private static boolean testIdExists(String id, List<Test> existingTests) {
        for (Test r : existingTests)
            if (null != r.getId() && r.getId().equals(id))
                return true;
        return false;
    }

    public static TestConfiguration createTestConfiguration() {
        TestConfiguration c = new TestConfiguration();
        return c;
    }
}
