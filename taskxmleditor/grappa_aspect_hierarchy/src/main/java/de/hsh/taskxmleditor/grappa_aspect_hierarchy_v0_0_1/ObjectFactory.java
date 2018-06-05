//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2017.02.22 at 09:18:42 AM CET 
//


package de.hsh.taskxmleditor.grappa_aspect_hierarchy_v0_0_1;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each
 * Java content interface and Java element interface
 * generated in the de.hsh.taskxmleditor.plugin.grappa_plugin.grappa_aspect_hierarchy_v0_0_1 package.
 * <p>An ObjectFactory allows you to programatically
 * construct new instances of the Java representation
 * for XML content. The Java representation of XML
 * content can consist of schema derived interfaces
 * and classes representing the binding of schema
 * type definitions, element declarations and model
 * groups.  Factory methods for each of these are
 * provided in this class.
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _AcceptedRdKinds_QNAME = new QName("urn:grappa:tests:hierarchical:v0.0.1", "accepted-rd-kinds");
    private final static QName _ComputingResources_QNAME = new QName("urn:grappa:tests:hierarchical:v0.0.1", "computing-resources");
    private final static QName _OverallTestConfiguration_QNAME = new QName("urn:grappa:tests:hierarchical:v0.0.1", "overall-test-configuration");
    private final static QName _TestGroup_QNAME = new QName("urn:grappa:tests:hierarchical:v0.0.1", "test-group");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: de.hsh.taskxmleditor.plugin.grappa_plugin.grappa_aspect_hierarchy_v0_0_1
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link OverallTestconfType }
     */
    public OverallTestconfType createOverallTestconfType() {
        return new OverallTestconfType();
    }

    /**
     * Create an instance of {@link OverallTestconfType.FileRoles }
     */
    public OverallTestconfType.FileRoles createOverallTestconfTypeFileRoles() {
        return new OverallTestconfType.FileRoles();
    }

    /**
     * Create an instance of {@link ComputingResourcesType }
     */
    public ComputingResourcesType createComputingResourcesType() {
        return new ComputingResourcesType();
    }

    /**
     * Create an instance of {@link RdkindListType }
     */
    public RdkindListType createRdkindListType() {
        return new RdkindListType();
    }

    /**
     * Create an instance of {@link TestGroupType }
     */
    public TestGroupType createTestGroupType() {
        return new TestGroupType();
    }

    /**
     * Create an instance of {@link ReprType }
     */
    public ReprType createReprType() {
        return new ReprType();
    }

    /**
     * Create an instance of {@link RdkindType }
     */
    public RdkindType createRdkindType() {
        return new RdkindType();
    }

    /**
     * Create an instance of {@link ReprHtmlType }
     */
    public ReprHtmlType createReprHtmlType() {
        return new ReprHtmlType();
    }

    /**
     * Create an instance of {@link ReprPlainTextType }
     */
    public ReprPlainTextType createReprPlainTextType() {
        return new ReprPlainTextType();
    }

    /**
     * Create an instance of {@link TestElementType }
     */
    public TestElementType createTestElementType() {
        return new TestElementType();
    }

    /**
     * Create an instance of {@link ReprPdfType }
     */
    public ReprPdfType createReprPdfType() {
        return new ReprPdfType();
    }

    /**
     * Create an instance of {@link TestGroupMembersType }
     */
    public TestGroupMembersType createTestGroupMembersType() {
        return new TestGroupMembersType();
    }

    /**
     * Create an instance of {@link OverallTestconfType.FileRoles.FileRole }
     */
    public OverallTestconfType.FileRoles.FileRole createOverallTestconfTypeFileRolesFileRole() {
        return new OverallTestconfType.FileRoles.FileRole();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RdkindListType }{@code >}}
     */
    @XmlElementDecl(namespace = "urn:grappa:tests:hierarchical:v0.0.1", name = "accepted-rd-kinds")
    public JAXBElement<RdkindListType> createAcceptedRdKinds(RdkindListType value) {
        return new JAXBElement<RdkindListType>(_AcceptedRdKinds_QNAME, RdkindListType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ComputingResourcesType }{@code >}}
     */
    @XmlElementDecl(namespace = "urn:grappa:tests:hierarchical:v0.0.1", name = "computing-resources")
    public JAXBElement<ComputingResourcesType> createComputingResources(ComputingResourcesType value) {
        return new JAXBElement<ComputingResourcesType>(_ComputingResources_QNAME, ComputingResourcesType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link OverallTestconfType }{@code >}}
     */
    @XmlElementDecl(namespace = "urn:grappa:tests:hierarchical:v0.0.1", name = "overall-test-configuration")
    public JAXBElement<OverallTestconfType> createOverallTestConfiguration(OverallTestconfType value) {
        return new JAXBElement<OverallTestconfType>(_OverallTestConfiguration_QNAME, OverallTestconfType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TestGroupType }{@code >}}
     */
    @XmlElementDecl(namespace = "urn:grappa:tests:hierarchical:v0.0.1", name = "test-group")
    public JAXBElement<TestGroupType> createTestGroup(TestGroupType value) {
        return new JAXBElement<TestGroupType>(_TestGroup_QNAME, TestGroupType.class, null, value);
    }

}
