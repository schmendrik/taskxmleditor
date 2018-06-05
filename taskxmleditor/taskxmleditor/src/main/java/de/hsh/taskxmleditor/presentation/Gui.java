package de.hsh.taskxmleditor.presentation;

import de.hsh.taskxmleditor.application.CopySelectedTextActionListener;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Arrays;
import java.util.Enumeration;

public class Gui {
    public final static Logger log = LoggerFactory.getLogger(Gui.class);

    public static Font titledBorderFont = new Font("Verdana", Font.BOLD, 22);


    public static BufferedImage helpIcon2424;
    public static BufferedImage helpIcon1616;

    static {
        try {
            helpIcon2424 = ImageIO.read(Gui.class.getResourceAsStream("/helpicon2424.png"));
            helpIcon1616 = ImageIO.read(Gui.class.getResourceAsStream("/helpicon1616.png"));
        } catch (IOException e) {
            log.error(ExceptionUtils.getStackTrace(e));
        }
    }

    public static void fixPanelWithGridBagLayout(JPanel p) {
        if (p.getLayout() instanceof GridBagLayout) {
            GridBagLayout layout = (GridBagLayout) p.getLayout();
            int[][] dim = layout.getLayoutDimensions();
            // int cols = dim[0].length;
            int rows = dim[1].length;
            double[] rowWeights = new double[rows];
            Arrays.fill(rowWeights, 0.0);
            log.debug("rows: " + rows);
            rowWeights[rows - 1] = 1.0;
            layout.rowWeights = rowWeights;
        }

    }

    public static GridBagConstraints createGbc(int x, int y) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;

        gbc.insets = new Insets(10, 0, 0, 0);
        // gbc.anchor = (x == 0) ? GridBagConstraints.WEST :
        // GridBagConstraints.EAST;
        // gbc.fill = (x == 0) ? GridBagConstraints.BOTH :
        // GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        // gbc.fill = fill;

        // gbc.insets = (x == 0) ? WEST_INSETS : EAST_INSETS;
        gbc.weightx = (x == 0) ? 0.01 : 0.5;
        // gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.VERTICAL;
        return gbc;
    }

    public static GridBagConstraints createGbc(int x, int y, int fill) {
        GridBagConstraints gbc = createGbc(x, y);
        gbc.fill = fill;
        return gbc;
    }

    public static JTextField createTextField() {
        return createTextField(400);
    }

    public static JTextArea createTextArea() {
        JTextArea textArea = new JTextArea();
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);

        JMenuItem copyMenu = new JMenuItem();
        copyMenu.setText("Copy");
        copyMenu.addActionListener(new CopySelectedTextActionListener(textArea));

        JMenuItem pasteMenu = new JMenuItem();
        pasteMenu.setText("Paste");
        pasteMenu.addActionListener(l -> {
            try {
                String data = (String) Toolkit.getDefaultToolkit()
                        .getSystemClipboard().getData(DataFlavor.stringFlavor);
                textArea.insert(data, textArea.getCaretPosition());
            } catch (Exception e) {
                System.out.println(e);
            }
        });

        JPopupMenu pMenu = new JPopupMenu();
        pMenu.add(copyMenu);
        pMenu.add(pasteMenu);

        textArea.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger() && e.getComponent() instanceof JTextComponent) {
                    if (textArea.getSelectedText() != null)
                        copyMenu.setEnabled(true);
                    else
                        copyMenu.setEnabled(false);
                    pMenu.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });


        return textArea;
    }

    public static JTextField createTextField(int prefWidth) {
        JTextField f = new JTextField();
        Dimension d = f.getPreferredSize();
        d.width = prefWidth;
        f.setPreferredSize(d);
        Dimension min = f.getMinimumSize();
        min.width = 200;
        f.setMinimumSize(min);
        return f;
    }

    public static JTextArea createTextArea(int prefWidth) {
        JTextArea f = new JTextArea();
        f.setLineWrap(true);
        f.setWrapStyleWord(true);
        Dimension d = f.getPreferredSize();
        d.width = prefWidth;
        f.setPreferredSize(d);
        Dimension min = f.getMinimumSize();
        min.width = 200;
        f.setMinimumSize(min);
        // f.setRows(3);
        return f;
    }

    public static JComponent combineWithHelp(JComponent c, String helpTextKey) {
        if (null == helpTextKey || helpTextKey.isEmpty())
            return c;

        FlowLayout layout = new FlowLayout();
        layout.setVgap(0);
        layout.setHgap(0);
        JPanel p = new JPanel(layout);
        p.add(c);
        JLabel imgLabel = new JLabel(new ImageIcon(helpIcon2424));
        helpTextKey = String.format("<html><body>%s</body></html>", helpTextKey);
        setLabelToolTipDisplayingForever(imgLabel, helpTextKey);
        p.add(Box.createRigidArea(new Dimension(3, 0)));
        p.add(imgLabel);
        return p;
    }

    public static void setLabelToolTipDisplayingForever(JLabel lbl, String tooltip) {
        lbl.setToolTipText(tooltip);
    }

    private static final int SCROLL_PANE_SCROLL_SPEED = 20;

    /**
     * Creates a JScrollPane with increased scrolling speed.
     */

    public static JScrollPane createScrollPane(Component innerComponent) {
        JScrollPane p = new JScrollPane();
        if (null != innerComponent)
            p.setViewportView(innerComponent);
        p.getVerticalScrollBar().setUnitIncrement(SCROLL_PANE_SCROLL_SPEED);
        return p;
    }

    public static JPanel createGroupBoxWrapperForAnyObject(Component component, String title) {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.add(component, BorderLayout.CENTER);
        wrapper.setBorder(BorderFactory.createTitledBorder(title));
        return wrapper;
    }

    public static AbstractButton getSelectedButton(ButtonGroup buttonGroup) {
        for (Enumeration<AbstractButton> buttons = buttonGroup.getElements(); buttons.hasMoreElements(); ) {
            AbstractButton button = buttons.nextElement();
            if (button.isSelected())
                return button;
        }
        return null;
    }

    /**
     * Creates a JScrollPane with increased scrolling speed.
     */

    public static JScrollPane createScrollPane() {
        return createScrollPane(null);
    }

    public static final EmptyBorder emptyBorder = new EmptyBorder(10, 10, 10, 10);

    /**
     * Don't forget to do this: table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
     * Source: http://stackoverflow.com/a/17627497
     *
     * @param table
     */
    public static void resizeJTableColumnWidth(JTable table) {
        final TableColumnModel columnModel = table.getColumnModel();
        for (int column = 0; column < table.getColumnCount(); column++) {
            int width = 15; // Min width
            for (int row = 0; row < table.getRowCount(); row++) {
                TableCellRenderer renderer = table.getCellRenderer(row, column);
                Component comp = table.prepareRenderer(renderer, row, column);
                width = Math.max(comp.getPreferredSize().width + 1, width);
            }
            if (width > 300)
                width = 300;
            columnModel.getColumn(column).setPreferredWidth(width);
        }
    }

    public static void focus(Component c) {
        focus(c, c);
    }

    private static void focus(Component c, Component originialComponent){
        if(c instanceof JFrame)
            return;

        Component p = c.getParent();

        focus(p, originialComponent);

        if (p instanceof JTabbedPane) {
            ((JTabbedPane) p).setSelectedComponent(c);
            focus(p, originialComponent);
        } else if (p instanceof JScrollPane) {
            if(originialComponent instanceof JComponent) {
                getContainer(originialComponent).scrollRectToVisible(originialComponent.getBounds());
                //((JComponent) originialComponent).scrollRectToVisible(originialComponent.getBounds());

            }
            //((JScrollPane) p).getViewport().setViewPosition(new Point(0, yOffset));
        }
    }

    public static JPanel getContainer(Component c) {
        if(c instanceof JPanel)
            return (JPanel)c;
        return getContainer(c.getParent());
    }

    private static void focus(Component c, int yOffset) {
        if(c instanceof JFrame)
            return;

        Component p = c.getParent();

        System.out.println("offset: " + yOffset);
        focus(p, p.getY() + yOffset);

        if (p instanceof JTabbedPane) {
            ((JTabbedPane) p).setSelectedComponent(c);
            focus(p, p.getY());
        } else if (p instanceof JScrollPane) {
            System.out.println("bounds: " + c.getBounds());
            ((JScrollPane) p).getViewport().setViewPosition(new Point(0, yOffset));
        }
    }

    private Component findNearestContainerToScrollpane(Component c)    {
        Container parent = c.getParent();
        if(parent instanceof JScrollPane)
            return c;
        return findNearestContainerToScrollpane(c.getParent());
    }
}
