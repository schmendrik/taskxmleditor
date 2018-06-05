package de.hsh.taskxmleditor.generic_editor;

import org.netbeans.swing.outline.RenderDataProvider;
import org.w3c.dom.Node;

import java.awt.*;

public class EditorDataProvider implements RenderDataProvider {
    @Override
    public Color getBackground(Object o) {
        return null;
    }

    @Override
    public String getDisplayName(Object o) {
        Node d = (Node) o;
        String nodeName = d.getLocalName() != null ? d.getLocalName() : d.getNodeName();

        // this added too much clutter
//        if(d instanceof Element) {
//            String summary = getElementContentSummary((Element)d);
//            return nodeName + " " + summary;
//        }

        return nodeName;
    }

//    private String getElementContentSummary(Element element) {
//        StringBuilder sb = new StringBuilder();
//        NodeList l = element.getChildNodes();
//        for(int i=0; i<l.getLength(); i++) {
//            Node c = l.item(i);
//            if (c instanceof Element)
//                sb.append(c.getNodeName()+", ");
//        }
//        if(sb.length() > 0)
//            return "(" + sb.toString() + ")";
//        return null;
//    }

    @Override
    public Color getForeground(Object o) {
        return null;
    }

    @Override
    public javax.swing.Icon getIcon(Object o) {
        return null;
    }

    @Override
    public String getTooltipText(Object o) {
        return null;
    }

    @Override
    public boolean isHtmlDisplayName(Object o) {
        return false;
    }
}