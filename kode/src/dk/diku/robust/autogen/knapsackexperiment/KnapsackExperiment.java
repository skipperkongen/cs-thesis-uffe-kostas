//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.3 in JDK 1.6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2008.10.23 at 02:01:50 PM CEST 
//


package dk.diku.robust.autogen.knapsackexperiment;

import java.math.BigInteger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for KnapsackExperiment complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="KnapsackExperiment">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;choice>
 *           &lt;element name="genetic_algorithm" type="{http://www.diku.dk/robust/autogen/knapsackexperiment}GeneticAlgorithmSetup"/>
 *           &lt;element name="local_search" type="{http://www.diku.dk/robust/autogen/knapsackexperiment}LocalSearchSetup"/>
 *         &lt;/choice>
 *         &lt;element name="num_iterations" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger"/>
 *         &lt;element name="simulation" type="{http://www.diku.dk/robust/autogen/knapsackexperiment}Simulation"/>
 *         &lt;element name="visualization" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="desired_robustness" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *       &lt;/sequence>
 *       &lt;attribute name="path_to_instance" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="path_to_log" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "KnapsackExperiment", propOrder = {
    "geneticAlgorithm",
    "localSearch",
    "numIterations",
    "simulation",
    "visualization",
    "desiredRobustness"
})
public class KnapsackExperiment {

    @XmlElement(name = "genetic_algorithm")
    protected GeneticAlgorithmSetup geneticAlgorithm;
    @XmlElement(name = "local_search")
    protected LocalSearchSetup localSearch;
    @XmlElement(name = "num_iterations", required = true)
    @XmlSchemaType(name = "nonNegativeInteger")
    protected BigInteger numIterations;
    @XmlElement(required = true)
    protected Simulation simulation;
    protected boolean visualization;
    @XmlElement(name = "desired_robustness")
    protected double desiredRobustness;
    @XmlAttribute(name = "path_to_instance")
    protected String pathToInstance;
    @XmlAttribute(name = "path_to_log")
    protected String pathToLog;

    /**
     * Gets the value of the geneticAlgorithm property.
     * 
     * @return
     *     possible object is
     *     {@link GeneticAlgorithmSetup }
     *     
     */
    public GeneticAlgorithmSetup getGeneticAlgorithm() {
        return geneticAlgorithm;
    }

    /**
     * Sets the value of the geneticAlgorithm property.
     * 
     * @param value
     *     allowed object is
     *     {@link GeneticAlgorithmSetup }
     *     
     */
    public void setGeneticAlgorithm(GeneticAlgorithmSetup value) {
        this.geneticAlgorithm = value;
    }

    /**
     * Gets the value of the localSearch property.
     * 
     * @return
     *     possible object is
     *     {@link LocalSearchSetup }
     *     
     */
    public LocalSearchSetup getLocalSearch() {
        return localSearch;
    }

    /**
     * Sets the value of the localSearch property.
     * 
     * @param value
     *     allowed object is
     *     {@link LocalSearchSetup }
     *     
     */
    public void setLocalSearch(LocalSearchSetup value) {
        this.localSearch = value;
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

    /**
     * Gets the value of the simulation property.
     * 
     * @return
     *     possible object is
     *     {@link Simulation }
     *     
     */
    public Simulation getSimulation() {
        return simulation;
    }

    /**
     * Sets the value of the simulation property.
     * 
     * @param value
     *     allowed object is
     *     {@link Simulation }
     *     
     */
    public void setSimulation(Simulation value) {
        this.simulation = value;
    }

    /**
     * Gets the value of the visualization property.
     * 
     */
    public boolean isVisualization() {
        return visualization;
    }

    /**
     * Sets the value of the visualization property.
     * 
     */
    public void setVisualization(boolean value) {
        this.visualization = value;
    }

    /**
     * Gets the value of the desiredRobustness property.
     * 
     */
    public double getDesiredRobustness() {
        return desiredRobustness;
    }

    /**
     * Sets the value of the desiredRobustness property.
     * 
     */
    public void setDesiredRobustness(double value) {
        this.desiredRobustness = value;
    }

    /**
     * Gets the value of the pathToInstance property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPathToInstance() {
        return pathToInstance;
    }

    /**
     * Sets the value of the pathToInstance property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPathToInstance(String value) {
        this.pathToInstance = value;
    }

    /**
     * Gets the value of the pathToLog property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPathToLog() {
        return pathToLog;
    }

    /**
     * Sets the value of the pathToLog property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPathToLog(String value) {
        this.pathToLog = value;
    }

}
