//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2017.02.21 at 09:25:16 PM CET 
//

package de.hsh.taskxmleditor.taskxml_v1_1;

import org.w3c.dom.Element;

import javax.xml.bind.annotation.*;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * Java class for test-configuration complex type.
 * <p>
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * <p>
 * <pre>
 * &lt;complexType name="test-configuration">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="filerefs" type="{urn:proforma:task:v1.1}filerefs" minOccurs="0"/>
 *         &lt;element name="timeout" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}positiveInteger">
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="externalresourcerefs" type="{urn:proforma:task:v1.1}externalresourcerefs" minOccurs="0"/>
 *         &lt;any processContents='lax' namespace='##other' maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="test-meta-data" type="{urn:proforma:task:v1.1}test-meta-data" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "test-configuration", propOrder = {"filerefs", "timeout", "externalresourcerefs", "any",
        "testMetaData"})
public class TestConfiguration {

    protected Filerefs filerefs;
    protected BigInteger timeout;
    protected Externalresourcerefs externalresourcerefs;
    @XmlAnyElement(lax = true)
    protected List<Object> any;
    @XmlElement(name = "test-meta-data")
    protected TestMetaData testMetaData;

    /**
     * Gets the value of the filerefs property.
     *
     * @return possible object is {@link Filerefs }
     */
    public Filerefs getFilerefs() {
        return filerefs;
    }

    /**
     * Sets the value of the filerefs property.
     *
     * @param value allowed object is {@link Filerefs }
     */
    public void setFilerefs(Filerefs value) {
        this.filerefs = value;
    }

    /**
     * Gets the value of the timeout property.
     *
     * @return possible object is {@link BigInteger }
     */
    public BigInteger getTimeout() {
        return timeout;
    }

    /**
     * Sets the value of the timeout property.
     *
     * @param value allowed object is {@link BigInteger }
     */
    public void setTimeout(BigInteger value) {
        this.timeout = value;
    }

    /**
     * Gets the value of the externalresourcerefs property.
     *
     * @return possible object is {@link Externalresourcerefs }
     */
    public Externalresourcerefs getExternalresourcerefs() {
        return externalresourcerefs;
    }

    /**
     * Sets the value of the externalresourcerefs property.
     *
     * @param value allowed object is {@link Externalresourcerefs }
     */
    public void setExternalresourcerefs(Externalresourcerefs value) {
        this.externalresourcerefs = value;
    }

    /**
     * Gets the value of the any property.
     * <p>
     * <p>
     * This accessor method returns a reference to the live list, not a
     * snapshot. Therefore any modification you make to the returned list will
     * be present inside the JAXB object. This is why there is not a
     * <CODE>set</CODE> method for the any property.
     * <p>
     * <p>
     * For example, to add a new item, do as follows:
     * <p>
     * <pre>
     * getAny().add(newItem);
     * </pre>
     * <p>
     * <p>
     * <p>
     * Objects of the following type(s) are allowed in the list {@link Element }
     * {@link Object }
     */
    public List<Object> getAny() {
        if (any == null) {
            any = new ArrayList<Object>();
        }
        return this.any;
    }

    /**
     * Gets the value of the testMetaData property.
     *
     * @return possible object is {@link TestMetaData }
     */
    public TestMetaData getTestMetaData() {
        return testMetaData;
    }

    /**
     * Sets the value of the testMetaData property.
     *
     * @param value allowed object is {@link TestMetaData }
     */
    public void setTestMetaData(TestMetaData value) {
        this.testMetaData = value;
    }

    public static TestConfiguration create() {
        TestConfiguration t = new TestConfiguration();
        t.externalresourcerefs = new Externalresourcerefs();
        t.externalresourcerefs.externalresourceref = new ArrayList<Externalresourceref>();
        t.any = new ArrayList<Object>();
        return t;
    }

}
