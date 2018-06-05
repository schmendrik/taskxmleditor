//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2017.02.21 at 09:25:16 PM CET 
//


package de.hsh.taskxmleditor.taskxml_v1_1;

import javax.xml.bind.annotation.*;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>Java class for file-restr-type complex type.
 * <p>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;complexType name="file-restr-type">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice maxOccurs="unbounded">
 *         &lt;element name="required">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attGroup ref="{urn:proforma:task:v1.1}mimetype-attr"/>
 *                 &lt;attGroup ref="{urn:proforma:task:v1.1}maxsize-attr"/>
 *                 &lt;attribute name="filename" use="required" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="optional">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attGroup ref="{urn:proforma:task:v1.1}mimetype-attr"/>
 *                 &lt;attGroup ref="{urn:proforma:task:v1.1}maxsize-attr"/>
 *                 &lt;attribute name="filename" use="required" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/choice>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "file-restr-type", propOrder = {
        "requiredOrOptional"
})
public class FileRestrType {

    @XmlElements({
            @XmlElement(name = "required", type = FileRestrType.Required.class),
            @XmlElement(name = "optional", type = FileRestrType.Optional.class)
    })
    protected List<Object> requiredOrOptional;

    /**
     * Gets the value of the requiredOrOptional property.
     * <p>
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the requiredOrOptional property.
     * <p>
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRequiredOrOptional().add(newItem);
     * </pre>
     * <p>
     * <p>
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link FileRestrType.Required }
     * {@link FileRestrType.Optional }
     */
    public List<Object> getRequiredOrOptional() {
        if (requiredOrOptional == null) {
            requiredOrOptional = new ArrayList<Object>();
        }
        return this.requiredOrOptional;
    }


    /**
     * <p>Java class for anonymous complex type.
     * <p>
     * <p>The following schema fragment specifies the expected content contained within this class.
     * <p>
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;attGroup ref="{urn:proforma:task:v1.1}mimetype-attr"/>
     *       &lt;attGroup ref="{urn:proforma:task:v1.1}maxsize-attr"/>
     *       &lt;attribute name="filename" use="required" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class Optional {

        @XmlAttribute(name = "filename", required = true)
        @XmlSchemaType(name = "anySimpleType")
        protected String filename;
        @XmlAttribute(name = "mime-type-regexp")
        protected String mimeTypeRegexp;
        @XmlAttribute(name = "max-size")
        @XmlSchemaType(name = "positiveInteger")
        protected BigInteger maxSize;

        /**
         * Gets the value of the filename property.
         *
         * @return possible object is
         * {@link String }
         */
        public String getFilename() {
            return filename;
        }

        /**
         * Sets the value of the filename property.
         *
         * @param value allowed object is
         *              {@link String }
         */
        public void setFilename(String value) {
            this.filename = value;
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

    }


    /**
     * <p>Java class for anonymous complex type.
     * <p>
     * <p>The following schema fragment specifies the expected content contained within this class.
     * <p>
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;attGroup ref="{urn:proforma:task:v1.1}mimetype-attr"/>
     *       &lt;attGroup ref="{urn:proforma:task:v1.1}maxsize-attr"/>
     *       &lt;attribute name="filename" use="required" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class Required {

        @XmlAttribute(name = "filename", required = true)
        @XmlSchemaType(name = "anySimpleType")
        protected String filename;
        @XmlAttribute(name = "mime-type-regexp")
        protected String mimeTypeRegexp;
        @XmlAttribute(name = "max-size")
        @XmlSchemaType(name = "positiveInteger")
        protected BigInteger maxSize;

        /**
         * Gets the value of the filename property.
         *
         * @return possible object is
         * {@link String }
         */
        public String getFilename() {
            return filename;
        }

        /**
         * Sets the value of the filename property.
         *
         * @param value allowed object is
         *              {@link String }
         */
        public void setFilename(String value) {
            this.filename = value;
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

    }

}