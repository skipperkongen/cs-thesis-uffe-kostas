//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.3 in JDK 1.6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2008.11.06 at 10:10:37 AM CET 
//


package dk.diku.robust.autogen.sclsp_solution;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for CLSPSolution complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CLSPSolution">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="global_parameters" type="{http://www.diku.dk/robust/autogen/sclsp_solution}GlobalParameters"/>
 *         &lt;element name="on_stock" type="{http://www.diku.dk/robust/autogen/sclsp_solution}StockTimePeriods"/>
 *         &lt;element name="being_produced" type="{http://www.diku.dk/robust/autogen/sclsp_solution}ProductionTimePeriods"/>
 *         &lt;element name="is_setup" type="{http://www.diku.dk/robust/autogen/sclsp_solution}IsSetupTimePeriods"/>
 *         &lt;element name="being_setup" type="{http://www.diku.dk/robust/autogen/sclsp_solution}BeingSetupTimePeriods"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CLSPSolution", propOrder = {
    "globalParameters",
    "onStock",
    "beingProduced",
    "isSetup",
    "beingSetup"
})
public class CLSPSolution {

    @XmlElement(name = "global_parameters", required = true)
    protected GlobalParameters globalParameters;
    @XmlElement(name = "on_stock", required = true)
    protected StockTimePeriods onStock;
    @XmlElement(name = "being_produced", required = true)
    protected ProductionTimePeriods beingProduced;
    @XmlElement(name = "is_setup", required = true)
    protected IsSetupTimePeriods isSetup;
    @XmlElement(name = "being_setup", required = true)
    protected BeingSetupTimePeriods beingSetup;

    /**
     * Gets the value of the globalParameters property.
     * 
     * @return
     *     possible object is
     *     {@link GlobalParameters }
     *     
     */
    public GlobalParameters getGlobalParameters() {
        return globalParameters;
    }

    /**
     * Sets the value of the globalParameters property.
     * 
     * @param value
     *     allowed object is
     *     {@link GlobalParameters }
     *     
     */
    public void setGlobalParameters(GlobalParameters value) {
        this.globalParameters = value;
    }

    /**
     * Gets the value of the onStock property.
     * 
     * @return
     *     possible object is
     *     {@link StockTimePeriods }
     *     
     */
    public StockTimePeriods getOnStock() {
        return onStock;
    }

    /**
     * Sets the value of the onStock property.
     * 
     * @param value
     *     allowed object is
     *     {@link StockTimePeriods }
     *     
     */
    public void setOnStock(StockTimePeriods value) {
        this.onStock = value;
    }

    /**
     * Gets the value of the beingProduced property.
     * 
     * @return
     *     possible object is
     *     {@link ProductionTimePeriods }
     *     
     */
    public ProductionTimePeriods getBeingProduced() {
        return beingProduced;
    }

    /**
     * Sets the value of the beingProduced property.
     * 
     * @param value
     *     allowed object is
     *     {@link ProductionTimePeriods }
     *     
     */
    public void setBeingProduced(ProductionTimePeriods value) {
        this.beingProduced = value;
    }

    /**
     * Gets the value of the isSetup property.
     * 
     * @return
     *     possible object is
     *     {@link IsSetupTimePeriods }
     *     
     */
    public IsSetupTimePeriods getIsSetup() {
        return isSetup;
    }

    /**
     * Sets the value of the isSetup property.
     * 
     * @param value
     *     allowed object is
     *     {@link IsSetupTimePeriods }
     *     
     */
    public void setIsSetup(IsSetupTimePeriods value) {
        this.isSetup = value;
    }

    /**
     * Gets the value of the beingSetup property.
     * 
     * @return
     *     possible object is
     *     {@link BeingSetupTimePeriods }
     *     
     */
    public BeingSetupTimePeriods getBeingSetup() {
        return beingSetup;
    }

    /**
     * Sets the value of the beingSetup property.
     * 
     * @param value
     *     allowed object is
     *     {@link BeingSetupTimePeriods }
     *     
     */
    public void setBeingSetup(BeingSetupTimePeriods value) {
        this.beingSetup = value;
    }

}
