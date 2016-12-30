
package com.osi.agent.webservicestub;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.osi.agent.webservicestub package. 
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

    private final static QName _ExecuteSnmpTable_QNAME = new QName("http://webservice.agent.osius.com/", "executeSnmpTable");
    private final static QName _ExecuteSnmpTableResponse_QNAME = new QName("http://webservice.agent.osius.com/", "executeSnmpTableResponse");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.osi.agent.webservicestub
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link ExecuteSnmpTable }
     * 
     */
    public ExecuteSnmpTable createExecuteSnmpTable() {
        return new ExecuteSnmpTable();
    }

    /**
     * Create an instance of {@link ExecuteSnmpTableResponse }
     * 
     */
    public ExecuteSnmpTableResponse createExecuteSnmpTableResponse() {
        return new ExecuteSnmpTableResponse();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ExecuteSnmpTable }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.agent.osius.com/", name = "executeSnmpTable")
    public JAXBElement<ExecuteSnmpTable> createExecuteSnmpTable(ExecuteSnmpTable value) {
        return new JAXBElement<ExecuteSnmpTable>(_ExecuteSnmpTable_QNAME, ExecuteSnmpTable.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ExecuteSnmpTableResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.agent.osius.com/", name = "executeSnmpTableResponse")
    public JAXBElement<ExecuteSnmpTableResponse> createExecuteSnmpTableResponse(ExecuteSnmpTableResponse value) {
        return new JAXBElement<ExecuteSnmpTableResponse>(_ExecuteSnmpTableResponse_QNAME, ExecuteSnmpTableResponse.class, null, value);
    }

}
