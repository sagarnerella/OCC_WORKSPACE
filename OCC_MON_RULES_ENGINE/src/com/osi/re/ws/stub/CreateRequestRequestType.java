
package com.osi.re.ws.stub;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for CreateRequestRequestType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CreateRequestRequestType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element name="cust_name" type="{http://www.w3.org/2001/XMLSchema}string" form="unqualified"/>
 *         &lt;element name="tkt_title" type="{http://www.w3.org/2001/XMLSchema}string" form="unqualified"/>
 *         &lt;element name="tkt_desc" type="{http://www.w3.org/2001/XMLSchema}string" form="unqualified"/>
 *         &lt;element name="service_catlog" type="{http://www.w3.org/2001/XMLSchema}string" form="unqualified"/>
 *         &lt;element name="assets" type="{http://www.w3.org/2001/XMLSchema}string" form="unqualified"/>
 *         &lt;element name="proj" type="{http://www.w3.org/2001/XMLSchema}string" form="unqualified"/>
 *       &lt;/all>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CreateRequestRequestType", propOrder = {

})
public class CreateRequestRequestType {

    @XmlElement(name = "cust_name", required = true)
    protected String custName;
    @XmlElement(name = "tkt_title", required = true)
    protected String tktTitle;
    @XmlElement(name = "tkt_desc", required = true)
    protected String tktDesc;
    @XmlElement(name = "service_catlog", required = true)
    protected String serviceCatlog;
    @XmlElement(required = true)
    protected String assets;
    @XmlElement(required = true)
    protected String proj;

    /**
     * Gets the value of the custName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCustName() {
        return custName;
    }

    /**
     * Sets the value of the custName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCustName(String value) {
        this.custName = value;
    }

    /**
     * Gets the value of the tktTitle property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTktTitle() {
        return tktTitle;
    }

    /**
     * Sets the value of the tktTitle property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTktTitle(String value) {
        this.tktTitle = value;
    }

    /**
     * Gets the value of the tktDesc property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTktDesc() {
        return tktDesc;
    }

    /**
     * Sets the value of the tktDesc property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTktDesc(String value) {
        this.tktDesc = value;
    }

    /**
     * Gets the value of the serviceCatlog property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getServiceCatlog() {
        return serviceCatlog;
    }

    /**
     * Sets the value of the serviceCatlog property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setServiceCatlog(String value) {
        this.serviceCatlog = value;
    }

    /**
     * Gets the value of the assets property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAssets() {
        return assets;
    }

    /**
     * Sets the value of the assets property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAssets(String value) {
        this.assets = value;
    }

    /**
     * Gets the value of the proj property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProj() {
        return proj;
    }

    /**
     * Sets the value of the proj property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProj(String value) {
        this.proj = value;
    }

}
