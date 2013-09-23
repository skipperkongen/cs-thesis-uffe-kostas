//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.3 in JDK 1.6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2008.11.06 at 01:26:13 PM CET 
//


package dk.diku.robust.autogen.sclsp_experiment;

import java.math.BigInteger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ExperimentParameters complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ExperimentParameters">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="num_simulations" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" />
 *       &lt;attribute name="num_iterations" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ExperimentParameters")
public class ExperimentParameters {

    @XmlAttribute(name = "num_simulations")
    @XmlSchemaType(name = "nonNegativeInteger")
    protected BigInteger numSimulations;
    @XmlAttribute(name = "num_iterations")
    @XmlSchemaType(name = "nonNegativeInteger")
    protected BigInteger numIterations;

    /**
     * Gets the value of the numSimulations property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getNumSimulations() {
        return numSimulations;
    }

    /**
     * Sets the value of the numSimulations property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setNumSimulations(BigInteger value) {
        this.numSimulations = value;
    }

    /**
     * Gets the value of the numIterations property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getNumIterations() {
        return numIterations;
    }

    /**
     * Sets the value of the numIterations property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setNumIterations(BigInteger value) {
        this.numIterations = value;
    }

}
