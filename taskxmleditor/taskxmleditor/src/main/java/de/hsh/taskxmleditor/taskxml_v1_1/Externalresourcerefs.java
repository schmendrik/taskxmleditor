//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2017.02.21 at 09:25:16 PM CET 
//


package de.hsh.taskxmleditor.taskxml_v1_1;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>Java class for externalresourcerefs complex type.
 * <p>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;complexType name="externalresourcerefs">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence maxOccurs="unbounded" minOccurs="0">
 *         &lt;element name="externalresourceref" type="{urn:proforma:task:v1.1}externalresourceref"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "externalresourcerefs", propOrder = {
        "externalresourceref"
})
public class Externalresourcerefs {

    protected List<Externalresourceref> externalresourceref;

    /**
     * Gets the value of the externalresourceref property.
     * <p>
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the externalresourceref property.
     * <p>
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getExternalresourceref().add(newItem);
     * </pre>
     * <p>
     * <p>
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Externalresourceref }
     */
    public List<Externalresourceref> getExternalresourceref() {
        if (externalresourceref == null) {
            externalresourceref = new ArrayList<Externalresourceref>();
        }
        return this.externalresourceref;
    }

}
