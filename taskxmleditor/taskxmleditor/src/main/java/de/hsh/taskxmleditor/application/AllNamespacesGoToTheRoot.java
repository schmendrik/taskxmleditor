package de.hsh.taskxmleditor.application;

import com.sun.xml.bind.marshaller.NamespacePrefixMapper;

import java.util.ArrayList;

public class AllNamespacesGoToTheRoot extends NamespacePrefixMapper {
    ArrayList<String> nsList = new ArrayList<>();

    public AllNamespacesGoToTheRoot() {
//        Configuration config = Configuration.getInstance();
//        config.getAllExtensionXsdFilesThatTheTaskActuallyUses().forEach((ns, file) -> {
//
//        });
    }


    @Override
    public String getPreferredPrefix(String arg0, String arg1, boolean arg2) {
        return null;
    }

    @Override
    public String[] getPreDeclaredNamespaceUris2() {
        return new String[]{
                "grappa", "urn:grappa:tests:hierarchical:v0.0.1",
//                "", "urn:grappa:tests:hierarchical:v0.0.1"
        };
    }
}
