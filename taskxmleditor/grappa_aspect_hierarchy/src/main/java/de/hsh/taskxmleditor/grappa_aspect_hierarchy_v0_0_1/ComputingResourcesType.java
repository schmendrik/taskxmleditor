//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2017.02.22 at 09:18:42 AM CET 
//


package de.hsh.taskxmleditor.grappa_aspect_hierarchy_v0_0_1;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for computing-resources-type complex type.
 * <p>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;complexType name="computing-resources-type">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element name="max-runtime-seconds-wallclock-time" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="max-disc-quota-kib" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="max-mem-mib" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *       &lt;/all>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "computing-resources-type", propOrder = {

})
public class ComputingResourcesType {

    @XmlElement(name = "max-runtime-seconds-wallclock-time", required = true, type = Integer.class, nillable = true)
    protected Integer maxRuntimeSecondsWallclockTime;
    @XmlElement(name = "max-disc-quota-kib", required = true, type = Integer.class, nillable = true)
    protected Integer maxDiscQuotaKib;
    @XmlElement(name = "max-mem-mib", required = true, type = Integer.class, nillable = true)
    protected Integer maxMemMib;

    /**
     * Gets the value of the maxRuntimeSecondsWallclockTime property.
     *
     * @return possible object is
     * {@link Integer }
     */
    public Integer getMaxRuntimeSecondsWallclockTime() {
        return maxRuntimeSecondsWallclockTime;
    }

    /**
     * Sets the value of the maxRuntimeSecondsWallclockTime property.
     *
     * @param value allowed object is
     *              {@link Integer }
     */
    public void setMaxRuntimeSecondsWallclockTime(Integer value) {
        this.maxRuntimeSecondsWallclockTime = value;
    }

    /**
     * Gets the value of the maxDiscQuotaKib property.
     *
     * @return possible object is
     * {@link Integer }
     */
    public Integer getMaxDiscQuotaKib() {
        return maxDiscQuotaKib;
    }

    /**
     * Sets the value of the maxDiscQuotaKib property.
     *
     * @param value allowed object is
     *              {@link Integer }
     */
    public void setMaxDiscQuotaKib(Integer value) {
        this.maxDiscQuotaKib = value;
    }

    /**
     * Gets the value of the maxMemMib property.
     *
     * @return possible object is
     * {@link Integer }
     */
    public Integer getMaxMemMib() {
        return maxMemMib;
    }

    /**
     * Sets the value of the maxMemMib property.
     *
     * @param value allowed object is
     *              {@link Integer }
     */
    public void setMaxMemMib(Integer value) {
        this.maxMemMib = value;
    }

}
