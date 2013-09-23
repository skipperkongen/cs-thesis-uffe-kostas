package foo.marshall;

import java.io.FileInputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import dk.diku.robust.autogen.test.Numbers;
import dk.diku.robust.autogen.test.ObjectFactory;
import dk.diku.robust.autogen.test.Test;

public class MarshallTest {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		
		JAXBContext jc = JAXBContext.newInstance( "dk.diku.robust.autogen.test" );
		Unmarshaller u = jc.createUnmarshaller();
		
		Object obj = ((JAXBElement)u.unmarshal( new FileInputStream( "xml/test.xml" ) )).getValue();
		Test test = (Test) obj ;
		ObjectFactory fact = new ObjectFactory();
		test.getNumbers().get(0).setA(2);
		test.getNumbers().get(0).setB(3);
		
		Numbers nums = fact.createNumbers();
		nums.setA(42);
		nums.setB(43);
		test.getNumbers().add(nums);
		
		Marshaller m = jc.createMarshaller();
		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		m.marshal( test, System.out );
	}

}
