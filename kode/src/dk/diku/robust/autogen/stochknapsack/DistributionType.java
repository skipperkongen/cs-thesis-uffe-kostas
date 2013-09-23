//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.3 in JDK 1.6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2008.10.22 at 02:00:41 PM CEST 
//


package dk.diku.robust.autogen.stochknapsack;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for distributionType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="distributionType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice>
 *         &lt;element name="normal" type="{http://www.diku.dk/robust/autogen/stochknapsack}normalDistribution"/>
 *         &lt;element name="uniform" type="{http://www.diku.dk/robust/autogen/stochknapsack}uniformDistribution"/>
 *         &lt;element name="gamma" type="{http://www.diku.dk/robust/autogen/stochknapsack}gammaDistribution"/>
 *         &lt;element name="polynomial" type="{http://www.diku.dk/robust/autogen/stochknapsack}polynomialDistribution"/>
 *       &lt;/choice>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "distributionType", propOrder = {
    "normal",
    "uniform",
    "gamma",
    "polynomial"
})
public class DistributionType {

    protected NormalDistribution normal;
    protected UniformDistribution uniform;
    protected GammaDistribution gamma;
    protected PolynomialDistribution polynomial;

    /**
     * Gets the value of the normal property.
     * 
     * @return
     *     possible object is
     *     {@link NormalDistribution }
     *     
     */
    public NormalDistribution getNormal() {
        return normal;
    }

    /**
     * Sets the value of the normal property.
     * 
     * @param value
     *     allowed object is
     *     {@link NormalDistribution }
     *     
     */
    public void setNormal(NormalDistribution value) {
        this.normal = value;
    }

    /**
     * Gets the value of the uniform property.
     * 
     * @return
     *     possible object is
     *     {@link UniformDistribution }
     *     
     */
    public UniformDistribution getUniform() {
        return uniform;
    }

    /**
     * Sets the value of the uniform property.
     * 
     * @param value
     *     allowed object is
     *     {@link UniformDistribution }
     *     
     */
    public void setUniform(UniformDistribution value) {
        this.uniform = value;
    }

    /**
     * Gets the value of the gamma property.
     * 
     * @return
     *     possible object is
     *     {@link GammaDistribution }
     *     
     */
    public GammaDistribution getGamma() {
        return gamma;
    }

    /**
     * Sets the value of the gamma property.
     * 
     * @param value
     *     allowed object is
     *     {@link GammaDistribution }
     *     
     */
    public void setGamma(GammaDistribution value) {
        this.gamma = value;
    }

    /**
     * Gets the value of the polynomial property.
     * 
     * @return
     *     possible object is
     *     {@link PolynomialDistribution }
     *     
     */
    public PolynomialDistribution getPolynomial() {
        return polynomial;
    }

    /**
     * Sets the value of the polynomial property.
     * 
     * @param value
     *     allowed object is
     *     {@link PolynomialDistribution }
     *     
     */
    public void setPolynomial(PolynomialDistribution value) {
        this.polynomial = value;
    }

}