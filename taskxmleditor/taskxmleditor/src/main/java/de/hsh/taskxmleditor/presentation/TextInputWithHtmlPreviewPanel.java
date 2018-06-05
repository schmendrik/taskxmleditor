package de.hsh.taskxmleditor.presentation;

import de.hsh.taskxmleditor.presentation.document_filters.InputMustNotBeEmptyHighlighter;
import de.hsh.taskxmleditor.utils.Str;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;
import java.awt.*;
import java.awt.event.ActionListener;

public class TextInputWithHtmlPreviewPanel extends JPanel {
    public final static Logger log = LoggerFactory.getLogger(TextInputWithHtmlPreviewPanel.class);
    private JEditorPane preview;
    private JTextArea textArea;

    public JTextArea getTextArea() {
        return textArea;
    }

    public TextInputWithHtmlPreviewPanel() {
        createLivePreview();
        textArea = createTextArea();
        new InputMustNotBeEmptyHighlighter(textArea);
        preview = createLivePreview();

        JSplitPane split = new JSplitPane();
        split.setOrientation(JSplitPane.VERTICAL_SPLIT);
        split.setLeftComponent(Gui.createScrollPane(textArea));
        split.setRightComponent(Gui.createScrollPane(preview));
        split.setResizeWeight(0.6);
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder(Str.get("Task.TaskDescription")));
        add(createFormatButtons(textArea), BorderLayout.NORTH);
        add(split, BorderLayout.CENTER);
    }

    public void setText(String desc) {
        textArea.setText(desc);
    }

    public String getText() {
        return textArea.getText();
    }

    public void addChangeListener(DocumentListener l) {
        textArea.getDocument().addDocumentListener(l);
    }

    private void insertTags(String startTag, String endTag, JTextArea a) {
        String selText = a.getSelectedText();
        if (null != selText && !selText.isEmpty()) {
            selText = startTag + selText + endTag;
            a.replaceSelection(selText);
        } else {
            a.insert(startTag + endTag, a.getCaretPosition());
            a.setCaretPosition(a.getCaretPosition() - endTag.length());
        }
        a.requestFocus();
    }

    private JButton createButton(String name, String tooltip, ActionListener listener) {
        JButton b = new JButton(name);
        b.addActionListener(listener);
        if (null != tooltip && !tooltip.isEmpty())
            b.setToolTipText(tooltip);
        return b;
    }

    private JButton createButton(String name, ActionListener e) {
        return createButton(name, null, e);
    }

    private JPanel createFormatButtons(JTextArea inputBox) {
        /* from the proforma whitepaper, these elements are allowed:
         * , a, b, blockquote, br, p, sup, sub, center, div, dl, dd,
		 * dt, em, font, h1, h2, h3, h4, h5, h6, hr, img, li, ol, strong, pre,
		 * span, table, tbody, td, tr, th, tt and ul.
		 * 
		 * However, for div, b, br, dd, dt, dl, blockquote, p, sup, sub, h1,
		 * ...h6, li, ol, hr, strong, pre, span, table, tbody, td, tr, th, tt
		 * and ul there are no attributes allowed in order to avoid problems
		 * with different layouts.
		 */

        // setButtonImage(bold, "bold.png"); // it works
        JButton italic = new JButton("Italic");
        italic.setToolTipText("Italic");
        italic.addActionListener(e -> insertTags("<i>", "</i>", inputBox));
        JButton link = new JButton("Link");
        link.setToolTipText("Link");

        JButton code = new JButton("Code");
        code.setToolTipText("Code");
        code.addActionListener(e -> insertTags("<pre><code>", "</code></pre>", inputBox));
        JButton orderList = new JButton("OrLi");
        JButton unordList = new JButton("UnLi");
        // setButtonImage(italic, "bold.png");

        JPanel btnPnl = new JPanel(new FlowLayout());
        // TODO: add escape chars like <>& etc
        // http://www.theukwebdesigncompany.com/articles/entity-escape-characters.php
        // btnPnl.setBackground(Color.RED);
        btnPnl.add(createButton("<br>", "Line Break", e -> insertTags("<br>", "", inputBox)));
        btnPnl.add(createButton("<h1>", "Header 1", e -> insertTags("<h1>", "</h1>", inputBox)));
        btnPnl.add(createButton("<h2>", "Header 2", e -> insertTags("<h2>", "</h2>", inputBox)));
        btnPnl.add(createButton("<h3>", "Header 3", e -> insertTags("<h3>", "</h3>", inputBox)));
        btnPnl.add(createButton("<p>", "Paragraph", e -> insertTags("<p>", "</p>", inputBox)));
        btnPnl.add(createButton("<b>", "Bold", e -> insertTags("<b>", "</b>", inputBox)));
        btnPnl.add(italic);
        btnPnl.add(createButton("Link", "Link", e -> insertTags("<a href=\"url\">", "</a>", inputBox)));
        btnPnl.add(code);

//        JLabel imgLabel = new JLabel(new ImageIcon(Gui.helpIcon2424));
//        imgLabel.setToolTipText(Str.get("Task.HTMLAllowed.Help"));
//        btnPnl.add(imgLabel);

        return btnPnl;
    }

    private JTextArea createTextArea() {
        JTextArea textArea = Gui.createTextArea(); //new JTextArea();
        textArea.setMinimumSize(new Dimension(300, 300));
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        textArea.setRows(10);
        Font font = textArea.getFont();
        textArea.setFont(new Font("monospaced", Font.PLAIN, font.getSize()+4));

        textArea.getDocument().addDocumentListener(new DocumentListener() {

            @Override
            public void removeUpdate(DocumentEvent e) {
                try {
                    preview.setText(e.getDocument().getText(0, e.getDocument().getLength()));
                } catch (BadLocationException e1) {
                    e1.printStackTrace();
                }
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                try {
                    preview.setText(e.getDocument().getText(0, e.getDocument().getLength()));
                } catch (BadLocationException e1) {
                    e1.printStackTrace();
                }
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
            }
        });

        return textArea;
    }

    private void setButtonImage(JButton btn, String imagePath) {
        try {
            Image img = ImageIO.read(ClassLoader.getSystemResource(imagePath));
            btn.setIcon(new ImageIcon(img));
        } catch (Exception ex) {
            System.out.println(ex);
        }
    }

    private JEditorPane createLivePreview() {
        JEditorPane preview = new JEditorPane();
        preview.setEditable(false);
        preview.setBackground(new Color(238, 238, 238));
        HTMLEditorKit kit = new HTMLEditorKit();
        preview.setEditorKit(kit);

        StyleSheet styleSheet = kit.getStyleSheet();
        styleSheet.addRule("body {color:#000; font-family:times; margin: 4px;}");
        styleSheet.addRule("h1 {color: blue;}");
        styleSheet.addRule("h2 {color: #ff0000;}");
        styleSheet.addRule("pre {font : 20px monaco; color : black; background-color : #fafafa; }");
        styleSheet.addRule("table, th, td { border: 1px solid black; }");

        Document doc = kit.createDefaultDocument();
        preview.setDocument(doc);
        return preview;
    }
}
