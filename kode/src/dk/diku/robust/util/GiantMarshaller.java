package dk.diku.robust.util;

import java.io.FileOutputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;

import dk.diku.robust.autogen.sclsp_instance.SCLSPInstance;
import dk.diku.robust.autogen.sclsp_scenario.LotSizingScenario;

public class GiantMarshaller {
	
	private final static String PATH_LSP_SCENARIO = "dk.diku.robust.autogen.sclsp_scenario";
	private final static String PATH_LSP_INSTANCE = "dk.diku.robust.autogen.sclsp_instance";
	
	public static void marshallCLSPScenario(JAXBElement<LotSizingScenario> scn, String path) throws Exception {
		JAXBContext jc = JAXBContext.newInstance( PATH_LSP_SCENARIO );
		Marshaller m = jc.createMarshaller();
		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		FileOutputStream fos = new FileOutputStream(path);
		m.marshal( scn, fos );
		System.out.println("scenario-xml writen to " + path);
		fos.close();
	}

	public static void marshallSCLSPInstance(JAXBElement<SCLSPInstance> instance, String path) throws Exception {
		JAXBContext jc = JAXBContext.newInstance( PATH_LSP_INSTANCE );
		Marshaller m = jc.createMarshaller();
		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		FileOutputStream fos = new FileOutputStream(path);
		m.marshal( instance, fos );
		System.out.println("instance-xml writen to " + path);
		fos.close();
	}
	
}
