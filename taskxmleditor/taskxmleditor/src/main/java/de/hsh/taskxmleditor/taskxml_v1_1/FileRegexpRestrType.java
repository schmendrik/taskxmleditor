//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2017.02.21 at 09:25:16 PM CET 
//


package de.hsh.taskxmleditor.taskxml_v1_1;

import javax.xml.bind.annotation.*;
import java.math.BigInteger;


/**
 * <p>Java class for file-regexp-restr-type complex type.
 * <p>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;complexType name="file-regexp-restr-type">
 *   &lt;simpleContent>
 *     &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
 *       &lt;attGroup ref="{urn:proforma:task:v1.1}maxsize-attr"/>
 *       &lt;attGroup ref="{urn:proforma:task:v1.1}mimetype-attr"/>
 *     &lt;/extension>
 *   &lt;/simpleContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "file-regexp-restr-type", propOrder = {
        "value"
})
public class FileRegexpRestrType {

    @XmlValue
    protected String value;
    @XmlAttribute(name = "max-size")
    @XmlSchemaType(name = "positiveInteger")
    protected BigInteger maxSize;
    @XmlAttribute(name = "mime-type-regexp")
    protected String mimeTypeRegexp;

    /**
     * Gets the value of the value property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getValue() {
        return value;
    }

    /**
     * Sets the value of the value property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Gets the value of the maxSize property.
     *
     * @return possible object is
     * {@link BigInteger }
     */
    public BigInteger getMaxSize() {
        return maxSize;
    }

    /**
     * Sets the value of the maxSize property.
     *
     * @param value allowed object is
     *              {@link BigInteger }
     */
    public void setMaxSize(BigInteger value) {
        this.maxSize = value;
    }

    /**
     * Gets the value of the mimeTypeRegexp property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getMimeTypeRegexp() {
        return mimeTypeRegexp;
    }

    /**
     * Sets the value of the mimeTypeRegexp property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setMimeTypeRegexp(String value) {
        this.mimeTypeRegexp = value;
    }

}
