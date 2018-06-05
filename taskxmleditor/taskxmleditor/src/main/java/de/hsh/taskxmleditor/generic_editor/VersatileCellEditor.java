package de.hsh.taskxmleditor.generic_editor;

import de.hsh.taskxmleditor.application.Configuration;
import de.hsh.taskxmleditor.dom.DomHelper;
import de.hsh.taskxmleditor.xsd.XsdIntrospection;
import org.apache.xmlbeans.SchemaProperty;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlAnySimpleType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import javax.xml.namespace.QName;
import java.awt.*;
import java.util.Arrays;

/**
 * Depending on a node's SchemaType (XMLBeans), this cell editor shows
 * the proper editor component (i.e. text fields for strings, combo boxes for enumerations, ...)
 */
public class VersatileCellEditor extends AbstractCellEditor implements TableCellEditor {
    final static Logger log = LoggerFactory.getLogger(VersatileCellEditor.class);
    private static final long serialVersionUID = 1L;
    private TableCellEditor editor;

    @Override
    public Object getCellEditorValue() {
        if (editor != null) {
            return editor.getCellEditorValue();
        }

        return null;
    }

    /**
     * @return a default cell editor for xml types which we don't know the
     * definiton (due to a missing xsd file, probably)
     */
    private TableCellEditor createSimpleTextFieldCellEditor(boolean editable) {
        JTextField textField = new JTextField();
        textField.setEnabled(editable);
        return new DefaultCellEditor(textField);
    }

    private TableCellEditor createElementCellEditor(SchemaType elem) {
        if (null != elem) {
            if (elem.isSimpleType()) {
                // TODO: add facet support

                if ("XMLBOOLEAN".equals(elem.getPrimitiveType().getShortJavaName().toUpperCase())) {
                    // TODO: this code is redundant (see createAttributeCellEditor())
                    JComboBox<String> combo = new JComboBox<>();
                    combo.setEditable(false);
                    combo.addItem("true");
                    combo.addItem("false");
                    return new DefaultCellEditor(combo);
                } else {
                    JTextField textField = new JTextField();
                    return new DefaultCellEditor(textField);
                }
            } else if("XMLSTRING".equals(elem.getBaseType().getShortJavaName().toUpperCase())) {
                JTextField textField = new JTextField();
                return new DefaultCellEditor(textField);
            }
        }

        return null;
    }

    // TODO: add facet support
    private TableCellEditor createAttributeCellEditor(SchemaProperty attr) {

        // TODO: check restrictions imposed on the simple type using
        // attr.getType().getFacet(SchemaType.BTC_POSITIVE_INTEGER) etc

        // TODO (somewhere): match regex pattern using
        // SchemaType.matchPatternFacet(str)

        if (null == attr) {
            // So this happens when the attribute's namespace is unknown (that
            // is, there is no xsd file for it)
            // log.debug("createAttributeCellEditor({}): attribute is null",
            // attr.getName());

            // Note: this also happens when you call an attribute on the <task>
            // element that has another namespace prefix

            // Return a simple editable text field for now.
            return createSimpleTextFieldCellEditor(true);
        }

        SchemaType typeInfo = attr.getType();

        if (typeInfo.isSimpleType()) {
            // is a simple, atomic value; display in text field
            // atomic values include enumeration restrictions
            if (SchemaType.ATOMIC == typeInfo.getSimpleVariety()) {
                // Case 1: attribute has enumeration restriction - make it a combo box
                if (null != typeInfo.getEnumerationValues()) {
                    XmlAnySimpleType[] enumVals = typeInfo.getEnumerationValues();
                    JComboBox<String> combo = new JComboBox<>();
                    Arrays.stream(enumVals).forEach(enumVal -> combo.addItem(enumVal.getStringValue()));
                    String defVal = XsdIntrospection.getAttributeDefaultValue(attr);
                    combo.setSelectedItem(defVal);
                    return new DefaultCellEditor(combo);
                }
                // Case 2: attribute is boolean
                else if ("XMLBOOLEAN".equals(typeInfo.getPrimitiveType().getShortJavaName().toUpperCase())) {
                    // For some reason, using a JCheckBox fails with a CastException:
                    // java.lang.ClassCastException: javax.swing.JCheckBox cannot be cast to org.netbeans.swing.outline.DefaultOutlineCellRenderer
                    // return new DefaultCellEditor(new JCheckBox());
                    // Odly enough, JComboBoxes work without problems, so lets make use of them
                    JComboBox<String> combo = new JComboBox<>();
                    combo.setEditable(false);
                    combo.addItem("true");
                    combo.addItem("false");
                    return new DefaultCellEditor(combo);
                }
                // Case 3: Treat everything else as a string for now
                else {
                    JTextField textField = new JTextField();
                    String defVal = XsdIntrospection.getAttributeDefaultValue(attr);
                    textField.setText(defVal);
                    return new DefaultCellEditor(textField);
                }
            }
        }

        // allow everything to be editable for now
        JTextField textField = new JTextField();
        String defVal = XsdIntrospection.getAttributeDefaultValue(attr);
        textField.setText(defVal);
        return new DefaultCellEditor(textField);
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            Node node = (Node) table.getModel().getValueAt(row, 0);

            if (node instanceof Attr) {
                Element owner = DomHelper.getOwnerElement(node);
                QName ownerQn = DomHelper.getQName(owner);
                SchemaProperty attrInfo = Configuration.getInstance().getXsd().getAttributeInfo(ownerQn, node.getLocalName());
                editor = createAttributeCellEditor(attrInfo);
            /*} else if (node instanceof Text) {
                editor = createSimpleTextFieldCellEditor(true);*/
            } else {
                if (node instanceof Element) {
                    QName qName = DomHelper.getQName(node);
                    SchemaType elemInfo = Configuration.getInstance().getXsd().getType(qName);//.getElementType(qName);
                    if (null != elemInfo)
                        editor = createElementCellEditor(elemInfo);
                    else {
                        editor = createSimpleTextFieldCellEditor(true);
                    }
                } else
                    return null;
            }

            if (null != editor)
                return editor.getTableCellEditorComponent(table, value, isSelected, row, column);

        return null;
    }
}
