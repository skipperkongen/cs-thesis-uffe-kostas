//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.3 in JDK 1.6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2008.10.31 at 11:37:18 AM CET 
//


package dk.diku.robust.autogen.test;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the dk.diku.robust.autogen.test package. 
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

    private final static QName _SomeNumbers_QNAME = new QName("http://www.diku.dk/robust/autogen/test", "some_numbers");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: dk.diku.robust.autogen.test
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link Numbers }
     * 
     */
    public Numbers createNumbers() {
        return new Numbers();
    }

    /**
     * Create an instance of {@link Test }
     * 
     */
    public Test createTest() {
        return new Test();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Test }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.diku.dk/robust/autogen/test", name = "some_numbers")
    public JAXBElement<Test> createSomeNumbers(Test value) {
        return new JAXBElement<Test>(_SomeNumbers_QNAME, Test.class, null, value);
    }

}