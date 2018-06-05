package de.hsh.taskxmleditor.presentation;

import de.hsh.taskxmleditor.application.Configuration;
import de.hsh.taskxmleditor.utils.Str;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class AddRemoveItemsPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    final int ADD_NEW_ITEM_POS;
    final int GAP_BETWEEN_VERTICAL_LINECONTENT = 10;//30;

    private JButton addItemAbove;

    private final Color alternateRowColor = new Color(240, 240, 240);

    private Callable<Component> itemFactory;

    private JPanel itemsListPanel;

    private List<Component> items = new ArrayList<>();

    private Consumer<Component> itemAdded = null;
    private Consumer<Component> itemRemoved = null;
    private Predicate<Component> canRemoveItem = null;

    private Dimension buttonSize;
    private Font font;

    public void setItemAdded(Consumer<Component> itemAdded) {
        this.itemAdded = itemAdded;
    }

    public void setItemRemoved(Consumer<Component> itemRemoved) {
        this.itemRemoved = itemRemoved;
    }

    public void setCanRemoveItem(Predicate<Component> canRemoveItem) {
        this.canRemoveItem = canRemoveItem;
    }

    /**
     * Like clicking on the 'add' button.
     */
    public void triggerItemAddedEvent() {
        if(null != itemFactory)
            addItemAbove.doClick();
    }

    private JButton createButton(String name, boolean visible, String tooltip, ActionListener l) {
        JButton btn = new JButton(name); // new RoundButton(plusIcon);//
        btn.setFont(font);
        btn.setPreferredSize(buttonSize);
        btn.addActionListener(l);
        btn.setToolTipText(tooltip);
        return btn;
    }


    /**
     * Creates a panel that can be used to add and display items. No
     * button to create new items is provided, though.
     */
    public AddRemoveItemsPanel() {
        this(null, true);
    }

    public AddRemoveItemsPanel(Callable<Component> itemFactory) {
        this(itemFactory, true);
    }

    /**
     * Creates a panel that can add and remove items using button clicks
     *
     * @param itemFactory the factory method that creates components when you click on
     *                    an 'add new row' button
     */
    public AddRemoveItemsPanel(Callable<Component> itemFactory, boolean bigButtons) {
        if (bigButtons) {
            font = new Font("Arial", Font.BOLD, 20);
            this.buttonSize = new Dimension(40, 40);
        } else {
            font = new Font("Arial", Font.PLAIN, 10);
            this.buttonSize = new Dimension(35, 35);
        }


        this.itemFactory = itemFactory;
        ADD_NEW_ITEM_POS = null != itemFactory ? 1 /* right below the button */ : 0;


        // There is a problem that when you use BoxLayout (y axis), it'll
        // stretch the inner components to no end.. very annoying.
        // A workaround is to use FlowLayout as the container for That
        // BoxLayout.

        // setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        setLayout(new FlowLayout(FlowLayout.LEFT));
        itemsListPanel = new JPanel();
        itemsListPanel.setLayout(new BoxLayout(itemsListPanel, BoxLayout.Y_AXIS));

        add(itemsListPanel);

        if (null != itemFactory)
            initAddButton();

    }

    private void initAddButton() {

        JPanel aboveButtonPanel = new JPanel(new BorderLayout());
        addItemAbove = createButton("+", true, "Add New Item", l -> {
            Component item = null;
            try {
                item = itemFactory.call();
                if (null == item)
                    return;
            } catch (Exception e) {
                e.printStackTrace();
                item = new JLabel(e.getMessage());
            }

            JPanel line = createItemLine(item);
            itemsListPanel.add(line, ADD_NEW_ITEM_POS);
            items.add(item);

            try {
                if (null != itemAdded)
                    itemAdded.accept(item);
            } catch (Exception e) {
                e.printStackTrace();
            }

            revalidate();
            repaint();
        });

        JPanel addGapBelowBtnPanel = new JPanel(new BorderLayout());
        addGapBelowBtnPanel.add(addItemAbove, BorderLayout.NORTH);
        addGapBelowBtnPanel.add(Box.createRigidArea(new Dimension(0, GAP_BETWEEN_VERTICAL_LINECONTENT)),
                BorderLayout.SOUTH);

        aboveButtonPanel.add(addGapBelowBtnPanel, BorderLayout.LINE_START);

        itemsListPanel.add(aboveButtonPanel);

    }

    private JPanel createItemLine(Component item) {
        JPanel p = new JPanel(new BorderLayout());

        JButton remBtn = createButton("x", true, "Remove This Item", l -> {
            if(null != canRemoveItem && !canRemoveItem.test(item))
                return;

            if(Boolean.parseBoolean(Configuration.getPropertiesKey
                    ("AddRemoveItemsPanel.confirmRemoval", "true"))) {
                int reply = JOptionPane.showConfirmDialog(null,
                        Str.get("AddRemoveItemsPanel.ConfirmRemovalQuestion"), "Remove item", JOptionPane.YES_NO_OPTION);
                if (reply != JOptionPane.YES_OPTION)
                    return;
            }


            itemsListPanel.remove(p);
            items.remove(item);

            try {
                if (null != itemRemoved)
                    itemRemoved.accept(item);
            } catch (Exception e) {
                e.printStackTrace();
            }


            revalidate();
            repaint();
        });


        // this will keep the button from stretching inside the border layout
        JPanel wrapperRemBtn = new JPanel(new BorderLayout());
        wrapperRemBtn.add(Box.createGlue(), BorderLayout.CENTER);
        wrapperRemBtn.add(remBtn, BorderLayout.NORTH);

        p.add(wrapperRemBtn, BorderLayout.LINE_START);
        p.add(item, BorderLayout.CENTER);
        p.add(Box.createRigidArea(new Dimension(0, GAP_BETWEEN_VERTICAL_LINECONTENT)), BorderLayout.SOUTH);

        return p;
    }

    public int getItemCount() {
        return items.size();//itemCount;
    }

    public List<Component> getItems() {
        return items;//Collections.unmodifiableList(items);
    }

    public void addItem(Component item) {
        JPanel line = createItemLine(item);
        items.add(item);
        itemsListPanel.add(line, ADD_NEW_ITEM_POS);

        try {
            if (null != itemAdded)
                itemAdded.accept(item);
        } catch (Exception e) {
            e.printStackTrace();
        }

        revalidate();
        repaint();
    }
}
