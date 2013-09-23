//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.3 in JDK 1.6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2008.10.30 at 03:32:41 PM CET 
//


package dk.diku.robust.autogen.sclsp_scenario;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for CostOnResource complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CostOnResource">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="cost_at_time" type="{http://www.diku.dk/robust/autogen/sclsp_scenario}CostAtTime" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *       &lt;attribute name="resource_id" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CostOnResource", propOrder = {
    "costAtTime"
})
public class CostOnResource {

    @XmlElement(name = "cost_at_time", required = true)
    protected List<CostAtTime> costAtTime;
    @XmlAttribute(name = "resource_id", required = true)
    protected int resourceId;

    /**
     * Gets the value of the costAtTime property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the costAtTime property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCostAtTime().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link CostAtTime }
     * 
     * 
     */
    public List<CostAtTime> getCostAtTime() {
        if (costAtTime == null) {
            costAtTime = new ArrayList<CostAtTime>();
        }
        return this.costAtTime;
    }

    /**
     * Gets the value of the resourceId property.
     * 
     */
    public int getResourceId() {
        return resourceId;
    }

    /**
     * Sets the value of the resourceId property.
     * 
     */
    public void setResourceId(int value) {
        this.resourceId = value;
    }

}
