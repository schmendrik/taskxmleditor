//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2017.02.22 at 09:18:42 AM CET 
//


package de.hsh.taskxmleditor.grappa_aspect_hierarchy_v0_0_1;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>Java class for overall-testconf-type complex type.
 * <p>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;complexType name="overall-testconf-type">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence maxOccurs="unbounded">
 *         &lt;element name="file-roles">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="file-role" maxOccurs="unbounded">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;attribute name="file-id" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                           &lt;attribute name="role" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="estimated-grading-seconds" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "overall-testconf-type", propOrder = {
        "fileRolesAndEstimatedGradingSeconds"
})
public class OverallTestconfType {

    @XmlElements({
            @XmlElement(name = "file-roles", required = true, type = OverallTestconfType.FileRoles.class),
            @XmlElement(name = "estimated-grading-seconds", required = true, type = Integer.class)
    })
    protected List<Object> fileRolesAndEstimatedGradingSeconds;

    /**
     * Gets the value of the fileRolesAndEstimatedGradingSeconds property.
     * <p>
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the fileRolesAndEstimatedGradingSeconds property.
     * <p>
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getFileRolesAndEstimatedGradingSeconds().add(newItem);
     * </pre>
     * <p>
     * <p>
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link OverallTestconfType.FileRoles }
     * {@link Integer }
     */
    public List<Object> getFileRolesAndEstimatedGradingSeconds() {
        if (fileRolesAndEstimatedGradingSeconds == null) {
            fileRolesAndEstimatedGradingSeconds = new ArrayList<Object>();
        }
        return this.fileRolesAndEstimatedGradingSeconds;
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
     *       &lt;sequence>
     *         &lt;element name="file-role" maxOccurs="unbounded">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;attribute name="file-id" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
     *                 &lt;attribute name="role" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
            "fileRole"
    })
    public static class FileRoles {

        @XmlElement(name = "file-role", required = true)
        protected List<FileRole> fileRole;

        /**
         * Gets the value of the fileRole property.
         * <p>
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the fileRole property.
         * <p>
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getFileRole().add(newItem);
         * </pre>
         * <p>
         * <p>
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link OverallTestconfType.FileRoles.FileRole }
         */
        public List<FileRole> getFileRole() {
            if (fileRole == null) {
                fileRole = new ArrayList<FileRole>();
            }
            return this.fileRole;
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
         *       &lt;attribute name="file-id" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
         *       &lt;attribute name="role" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
         *     &lt;/restriction>
         *   &lt;/complexContent>
         * &lt;/complexType>
         * </pre>
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "")
        public static class FileRole {

            @XmlAttribute(name = "file-id", required = true)
            protected String fileId;
            @XmlAttribute(name = "role", required = true)
            protected String role;

            /**
             * Gets the value of the fileId property.
             *
             * @return possible object is
             * {@link String }
             */
            public String getFileId() {
                return fileId;
            }

            /**
             * Sets the value of the fileId property.
             *
             * @param value allowed object is
             *              {@link String }
             */
            public void setFileId(String value) {
                this.fileId = value;
            }

            /**
             * Gets the value of the role property.
             *
             * @return possible object is
             * {@link String }
             */
            public String getRole() {
                return role;
            }

            /**
             * Sets the value of the role property.
             *
             * @param value allowed object is
             *              {@link String }
             */
            public void setRole(String value) {
                this.role = value;
            }

        }

    }

}