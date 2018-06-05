package de.hsh.taskxmleditor.generic_editor;


import de.hsh.taskxmleditor.application.Configuration;
import de.hsh.taskxmleditor.dom.DomHelper;
import de.hsh.taskxmleditor.presentation.Gui;
import org.netbeans.swing.outline.DefaultOutlineCellRenderer;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.xml.namespace.QName;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class HelpIconCellRenderer extends DefaultOutlineCellRenderer {

    @Override
    public Component getTableCellRendererComponent(JTable table,
                                                   Object value,
                                                   boolean isSelected,
                                                   boolean hasFocus,
                                                   int row,
                                                   int column) {

        Component cell = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        if(column == GenericEditor.HELP_COLUMN_INDEX) {

                if (cell instanceof JLabel) {

                    // the actual info string is not used, it's more of an
                    // indicator whether there's an annotation available for
                    // the node in htis row.
                    // the actual tooltip setting is handled elsewhere
                    String info = getInfo(table, value, row, column);

                    if (null != info && !info.trim().isEmpty()) {
                        JLabel label = (JLabel) cell;

                        BufferedImage read = null;
                        try {
                            read = ImageIO.read(Gui.class.getResourceAsStream("/helpicon1616.png"));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        label.setIcon(new ImageIcon(read));
                        label.setText("");
                        return label;
                    }
            }
        }

        return cell;
    }

    public class ImagePanel extends JPanel{

        private BufferedImage image;

        public ImagePanel() {
            setOpaque(false);
            //setBackground(Color.green);
            try {
                image = ImageIO.read(Gui.class.getResourceAsStream("/helpicon2424.png"));
//                setPreferredSize(new Dimension(5, image.getHeight()));
            } catch (IOException ex) {
                // handle exception...
            }

            xOffset = getWidth() - image.getWidth();
            xOffset = xOffset / 2;
        }

        private int xOffset = 0;

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            g.drawImage(image, -xOffset, 0, this); // see javadoc for more info on the parameters
        }

    }

    private String getInfo(final JTable table,
                         final Object value,
                         final int row,
                         final int column) {
        if (null != value) {
            Node node = (Node) table.getModel().getValueAt(row, 0);

            if (node instanceof Attr) {
                Element owner = DomHelper.getOwnerElement(node);
                QName ownerQn = DomHelper.getQName(owner);
                return Configuration.getInstance().getXsd().getAttributeAnnotation(ownerQn, node.getLocalName());
            } else if (node instanceof Element) {
                QName qName = DomHelper.getQName(node);
                return Configuration.getInstance().getXsd().getElementAnnotation(qName);
            }
        }

        return null;
    }
}