//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2017.02.21 at 09:25:16 PM CET 
//


package de.hsh.taskxmleditor.taskxml_v1_1;

import javax.xml.bind.annotation.*;


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
 *         &lt;element name="description" type="{urn:proforma:task:v1.1}description"/>
 *         &lt;element name="proglang" type="{urn:proforma:task:v1.1}proglang"/>
 *         &lt;element name="submission-restrictions" type="{urn:proforma:task:v1.1}submission-restrictions"/>
 *         &lt;element name="files" type="{urn:proforma:task:v1.1}files"/>
 *         &lt;element name="external-resources" type="{urn:proforma:task:v1.1}external-resources" minOccurs="0"/>
 *         &lt;element name="model-solutions" type="{urn:proforma:task:v1.1}model-solutions"/>
 *         &lt;element name="tests" type="{urn:proforma:task:v1.1}tests"/>
 *         &lt;element name="grading-hints" type="{urn:proforma:task:v1.1}grading-hints" minOccurs="0"/>
 *         &lt;element name="meta-data" type="{urn:proforma:task:v1.1}meta-data"/>
 *       &lt;/sequence>
 *       &lt;attribute name="uuid" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="parent-uuid" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="lang" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "description",
        "proglang",
        "submissionRestrictions",
        "files",
        "externalResources",
        "modelSolutions",
        "tests",
        "gradingHints",
        "metaData"
})
@XmlRootElement(name = "task")
public class Task {

    @XmlElement(required = true)
    protected String description;
    @XmlElement(required = true)
    protected Proglang proglang;
    @XmlElement(name = "submission-restrictions", required = true)
    protected SubmissionRestrictions submissionRestrictions;
    @XmlElement(required = true)
    protected Files files;
    @XmlElement(name = "external-resources")
    protected ExternalResources externalResources;
    @XmlElement(name = "model-solutions", required = true)
    protected ModelSolutions modelSolutions;
    @XmlElement(required = true)
    protected Tests tests;
    @XmlElement(name = "grading-hints")
    protected GradingHints gradingHints;
    @XmlElement(name = "meta-data", required = true)
    protected MetaData metaData;
    @XmlAttribute(name = "uuid", required = true)
    protected String uuid;
    @XmlAttribute(name = "parent-uuid")
    protected String parentUuid;
    @XmlAttribute(name = "lang", required = true)
    protected String lang;

    /**
     * Gets the value of the description property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the value of the description property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setDescription(String value) {
        this.description = value;
    }

    /**
     * Gets the value of the proglang property.
     *
     * @return possible object is
     * {@link Proglang }
     */
    public Proglang getProglang() {
        return proglang;
    }

    /**
     * Sets the value of the proglang property.
     *
     * @param value allowed object is
     *              {@link Proglang }
     */
    public void setProglang(Proglang value) {
        this.proglang = value;
    }

    /**
     * Gets the value of the submissionRestrictions property.
     *
     * @return possible object is
     * {@link SubmissionRestrictions }
     */
    public SubmissionRestrictions getSubmissionRestrictions() {
        return submissionRestrictions;
    }

    /**
     * Sets the value of the submissionRestrictions property.
     *
     * @param value allowed object is
     *              {@link SubmissionRestrictions }
     */
    public void setSubmissionRestrictions(SubmissionRestrictions value) {
        this.submissionRestrictions = value;
    }

    /**
     * Gets the value of the files property.
     *
     * @return possible object is
     * {@link Files }
     */
    public Files getFiles() {
        return files;
    }

    /**
     * Sets the value of the files property.
     *
     * @param value allowed object is
     *              {@link Files }
     */
    public void setFiles(Files value) {
        this.files = value;
    }

    /**
     * Gets the value of the externalResources property.
     *
     * @return possible object is
     * {@link ExternalResources }
     */
    public ExternalResources getExternalResources() {
        return externalResources;
    }

    /**
     * Sets the value of the externalResources property.
     *
     * @param value allowed object is
     *              {@link ExternalResources }
     */
    public void setExternalResources(ExternalResources value) {
        this.externalResources = value;
    }

    /**
     * Gets the value of the modelSolutions property.
     *
     * @return possible object is
     * {@link ModelSolutions }
     */
    public ModelSolutions getModelSolutions() {
        return modelSolutions;
    }

    /**
     * Sets the value of the modelSolutions property.
     *
     * @param value allowed object is
     *              {@link ModelSolutions }
     */
    public void setModelSolutions(ModelSolutions value) {
        this.modelSolutions = value;
    }

    /**
     * Gets the value of the tests property.
     *
     * @return possible object is
     * {@link Tests }
     */
    public Tests getTests() {
        return tests;
    }

    /**
     * Sets the value of the tests property.
     *
     * @param value allowed object is
     *              {@link Tests }
     */
    public void setTests(Tests value) {
        this.tests = value;
    }

    /**
     * Gets the value of the gradingHints property.
     *
     * @return possible object is
     * {@link GradingHints }
     */
    public GradingHints getGradingHints() {
        return gradingHints;
    }

    /**
     * Sets the value of the gradingHints property.
     *
     * @param value allowed object is
     *              {@link GradingHints }
     */
    public void setGradingHints(GradingHints value) {
        this.gradingHints = value;
    }

    /**
     * Gets the value of the metaData property.
     *
     * @return possible object is
     * {@link MetaData }
     */
    public MetaData getMetaData() {
        return metaData;
    }

    /**
     * Sets the value of the metaData property.
     *
     * @param value allowed object is
     *              {@link MetaData }
     */
    public void setMetaData(MetaData value) {
        this.metaData = value;
    }

    /**
     * Gets the value of the uuid property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * Sets the value of the uuid property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setUuid(String value) {
        this.uuid = value;
    }

    /**
     * Gets the value of the parentUuid property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getParentUuid() {
        return parentUuid;
    }

    /**
     * Sets the value of the parentUuid property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setParentUuid(String value) {
        this.parentUuid = value;
    }

    /**
     * Gets the value of the lang property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getLang() {
        return lang;
    }

    /**
     * Sets the value of the lang property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setLang(String value) {
        this.lang = value;
    }

}
