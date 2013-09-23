//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.3 in JDK 1.6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2008.10.22 at 02:00:41 PM CEST 
//


package dk.diku.robust.autogen.stochknapsack;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for stochasticKnapsackProblem complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="stochasticKnapsackProblem">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="items" type="{http://www.diku.dk/robust/autogen/stochknapsack}itemList"/>
 *         &lt;choice>
 *           &lt;element name="stochasticCapacity" type="{http://www.diku.dk/robust/autogen/stochknapsack}distributionType"/>
 *           &lt;element name="capacity" type="{http://www.w3.org/2001/XMLSchema}float"/>
 *         &lt;/choice>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "stochasticKnapsackProblem", propOrder = {
    "items",
    "stochasticCapacity",
    "capacity"
})
public class StochasticKnapsackProblem {

    @XmlElement(required = true)
    protected ItemList items;
    protected DistributionType stochasticCapacity;
    protected Float capacity;

    /**
     * Gets the value of the items property.
     * 
     * @return
     *     possible object is
     *     {@link ItemList }
     *     
     */
    public ItemList getItems() {
        return items;
    }

    /**
     * Sets the value of the items property.
     * 
     * @param value
     *     allowed object is
     *     {@link ItemList }
     *     
     */
    public void setItems(ItemList value) {
        this.items = value;
    }

    /**
     * Gets the value of the stochasticCapacity property.
     * 
     * @return
     *     possible object is
     *     {@link DistributionType }
     *     
     */
    public DistributionType getStochasticCapacity() {
        return stochasticCapacity;
    }

    /**
     * Sets the value of the stochasticCapacity property.
     * 
     * @param value
     *     allowed object is
     *     {@link DistributionType }
     *     
     */
    public void setStochasticCapacity(DistributionType value) {
        this.stochasticCapacity = value;
    }

    /**
     * Gets the value of the capacity property.
     * 
     * @return
     *     possible object is
     *     {@link Float }
     *     
     */
    public Float getCapacity() {
        return capacity;
    }

    /**
     * Sets the value of the capacity property.
     * 
     * @param value
     *     allowed object is
     *     {@link Float }
     *     
     */
    public void setCapacity(Float value) {
        this.capacity = value;
    }

}