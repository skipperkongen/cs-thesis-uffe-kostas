//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.3 in JDK 1.6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2008.10.22 at 02:00:41 PM CEST 
//


package dk.diku.robust.autogen.stochknapsack;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the dk.diku.robust.autogen.stochknapsack package. 
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

    private final static QName _Knapsack_QNAME = new QName("http://www.diku.dk/robust/autogen/stochknapsack", "knapsack");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: dk.diku.robust.autogen.stochknapsack
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link UniformDistribution }
     * 
     */
    public UniformDistribution createUniformDistribution() {
        return new UniformDistribution();
    }

    /**
     * Create an instance of {@link DistributionType }
     * 
     */
    public DistributionType createDistributionType() {
        return new DistributionType();
    }

    /**
     * Create an instance of {@link IncidentType }
     * 
     */
    public IncidentType createIncidentType() {
        return new IncidentType();
    }

    /**
     * Create an instance of {@link ItemType }
     * 
     */
    public ItemType createItemType() {
        return new ItemType();
    }

    /**
     * Create an instance of {@link NormalDistribution }
     * 
     */
    public NormalDistribution createNormalDistribution() {
        return new NormalDistribution();
    }

    /**
     * Create an instance of {@link ItemList }
     * 
     */
    public ItemList createItemList() {
        return new ItemList();
    }

    /**
     * Create an instance of {@link GammaDistribution }
     * 
     */
    public GammaDistribution createGammaDistribution() {
        return new GammaDistribution();
    }

    /**
     * Create an instance of {@link StochasticKnapsackProblem }
     * 
     */
    public StochasticKnapsackProblem createStochasticKnapsackProblem() {
        return new StochasticKnapsackProblem();
    }

    /**
     * Create an instance of {@link PolynomialDistribution }
     * 
     */
    public PolynomialDistribution createPolynomialDistribution() {
        return new PolynomialDistribution();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link StochasticKnapsackProblem }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.diku.dk/robust/autogen/stochknapsack", name = "knapsack")
    public JAXBElement<StochasticKnapsackProblem> createKnapsack(StochasticKnapsackProblem value) {
        return new JAXBElement<StochasticKnapsackProblem>(_Knapsack_QNAME, StochasticKnapsackProblem.class, null, value);
    }

}
