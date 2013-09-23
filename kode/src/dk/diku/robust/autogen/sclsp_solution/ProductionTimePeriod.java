//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.3 in JDK 1.6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2008.11.06 at 10:10:37 AM CET 
//


package dk.diku.robust.autogen.sclsp_solution;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ProductionTimePeriod complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ProductionTimePeriod">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence maxOccurs="unbounded">
 *         &lt;element name="item_production" type="{http://www.diku.dk/robust/autogen/sclsp_solution}ItemProduction"/>
 *       &lt;/sequence>
 *       &lt;attribute name="time_period" use="required" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ProductionTimePeriod", propOrder = {
    "itemProduction"
})
public class ProductionTimePeriod {

    @XmlElement(name = "item_production", required = true)
    protected List<ItemProduction> itemProduction;
    @XmlAttribute(name = "time_period", required = true)
    @XmlSchemaType(name = "nonNegativeInteger")
    protected BigInteger timePeriod;

    /**
     * Gets the value of the itemProduction property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the itemProduction property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getItemProduction().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ItemProduction }
     * 
     * 
     */
    public List<ItemProduction> getItemProduction() {
        if (itemProduction == null) {
            itemProduction = new ArrayList<ItemProduction>();
        }
        return this.itemProduction;
    }

    /**
     * Gets the value of the timePeriod property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getTimePeriod() {
        return timePeriod;
    }

    /**
     * Sets the value of the timePeriod property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setTimePeriod(BigInteger value) {
        this.timePeriod = value;
    }

}
