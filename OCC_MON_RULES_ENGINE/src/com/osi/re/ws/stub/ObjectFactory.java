
package com.osi.re.ws.stub;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the server package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _CreateRequestResponse_QNAME = new QName("urn:server", "CreateRequestResponse");
    private final static QName _CreateIncident_QNAME = new QName("urn:server", "CreateIncident");
    private final static QName _CreateIncidentResponse_QNAME = new QName("urn:server", "CreateIncidentResponse");
    private final static QName _CreateRequest_QNAME = new QName("urn:server", "CreateRequest");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: server
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link CreateRequestRequestType }
     * 
     */
    public CreateRequestRequestType createCreateRequestRequestType() {
        return new CreateRequestRequestType();
    }

    /**
     * Create an instance of {@link CreateIncidentResponseType }
     * 
     */
    public CreateIncidentResponseType createCreateIncidentResponseType() {
        return new CreateIncidentResponseType();
    }

    /**
     * Create an instance of {@link CreateRequestResponseType }
     * 
     */
    public CreateRequestResponseType createCreateRequestResponseType() {
        return new CreateRequestResponseType();
    }

    /**
     * Create an instance of {@link CreateIncidentRequestType }
     * 
     */
    public CreateIncidentRequestType createCreateIncidentRequestType() {
        return new CreateIncidentRequestType();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CreateRequestResponseType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:server", name = "CreateRequestResponse")
    public JAXBElement<CreateRequestResponseType> createCreateRequestResponse(CreateRequestResponseType value) {
        return new JAXBElement<CreateRequestResponseType>(_CreateRequestResponse_QNAME, CreateRequestResponseType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CreateIncidentRequestType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:server", name = "CreateIncident")
    public JAXBElement<CreateIncidentRequestType> createCreateIncident(CreateIncidentRequestType value) {
        return new JAXBElement<CreateIncidentRequestType>(_CreateIncident_QNAME, CreateIncidentRequestType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CreateIncidentResponseType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:server", name = "CreateIncidentResponse")
    public JAXBElement<CreateIncidentResponseType> createCreateIncidentResponse(CreateIncidentResponseType value) {
        return new JAXBElement<CreateIncidentResponseType>(_CreateIncidentResponse_QNAME, CreateIncidentResponseType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CreateRequestRequestType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:server", name = "CreateRequest")
    public JAXBElement<CreateRequestRequestType> createCreateRequest(CreateRequestRequestType value) {
        return new JAXBElement<CreateRequestRequestType>(_CreateRequest_QNAME, CreateRequestRequestType.class, null, value);
    }

}
