
package com.osi.re.ws.stub;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for CreateIncidentRequestType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CreateIncidentRequestType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element name="asset_id" type="{http://www.w3.org/2001/XMLSchema}int" form="unqualified"/>
 *         &lt;element name="checkpoint_id" type="{http://www.w3.org/2001/XMLSchema}int" form="unqualified"/>
 *         &lt;element name="tkt_title" type="{http://www.w3.org/2001/XMLSchema}string" form="unqualified"/>
 *         &lt;element name="tkt_desc" type="{http://www.w3.org/2001/XMLSchema}string" form="unqualified"/>
 *         &lt;element name="tkt_last_chk_updated_date" type="{http://www.w3.org/2001/XMLSchema}dateTime" form="unqualified"/>
 *       &lt;/all>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CreateIncidentRequestType", propOrder = {

})
public class CreateIncidentRequestType {

    @XmlElement(name = "asset_id")
    protected int assetId;
    @XmlElement(name = "checkpoint_id")
    protected int checkpointId;
    @XmlElement(name = "tkt_title", required = true)
    protected String tktTitle;
    @XmlElement(name = "tkt_desc", required = true)
    protected String tktDesc;
    @XmlElement(name = "tkt_last_chk_updated_date", required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar tktLastChkUpdatedDate;
    @XmlElement(name = "tkt_dev_checkpoint_id")
    protected int tkt_dev_checkpoint_id;

	/**
     * Gets the value of the assetId property.
     * 
     */
    public int getAssetId() {
        return assetId;
    }

    /**
     * Sets the value of the assetId property.
     * 
     */
    public void setAssetId(int value) {
        this.assetId = value;
    }

    /**
     * Gets the value of the checkpointId property.
     * 
     */
    public int getCheckpointId() {
        return checkpointId;
    }

    /**
     * Sets the value of the checkpointId property.
     * 
     */
    public void setCheckpointId(int value) {
        this.checkpointId = value;
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
     * Gets the value of the tktLastChkUpdatedDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getTktLastChkUpdatedDate() {
        return tktLastChkUpdatedDate;
    }

    /**
     * Sets the value of the tktLastChkUpdatedDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *    
     *     
     */
    public void setTktLastChkUpdatedDate(XMLGregorianCalendar value) {
        this.tktLastChkUpdatedDate = value;
    }
    
    
    /**
     * Gets the value of the tkt_dev_checkpoint_id property.
     * 
     */
    public int getTkt_dev_checkpoint_id() {
		return tkt_dev_checkpoint_id;
	}
    /**
     * Sets the value of the tkt_dev_checkpoint_id property.
     * 
     */
	public void setTkt_dev_checkpoint_id(int tkt_dev_checkpoint_id) {
		this.tkt_dev_checkpoint_id = tkt_dev_checkpoint_id;
	}
}
