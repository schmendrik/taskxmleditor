//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2017.02.22 at 09:18:42 AM CET 
//


package de.hsh.taskxmleditor.grappa_aspect_hierarchy_v0_0_1;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for test-element-type complex type.
 * <p>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;complexType name="test-element-type">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attGroup ref="{urn:grappa:tests:hierarchical:v0.0.1}test-element-attr-type"/>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "test-element-type")
public class TestElementType {

    @XmlAttribute(name = "testref-id", required = true)
    protected String testrefId;
    @XmlAttribute(name = "score-max")
    protected Double scoreMax;

    /**
     * Gets the value of the testrefId property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getTestrefId() {
        return testrefId;
    }

    /**
     * Sets the value of the testrefId property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setTestrefId(String value) {
        this.testrefId = value;
    }

    /**
     * Gets the value of the scoreMax property.
     *
     * @return possible object is
     * {@link Double }
     */
    public Double getScoreMax() {
        return scoreMax;
    }

    /**
     * Sets the value of the scoreMax property.
     *
     * @param value allowed object is
     *              {@link Double }
     */
    public void setScoreMax(Double value) {
        this.scoreMax = value;
    }

}