package foo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

public class OutTest {

	/**
	 * @param args
	 * @throws FileNotFoundException 
	 */
	public static void main(String[] args) throws FileNotFoundException {
		// TODO Auto-generated method stub
		File file  = new File("log/test2.log");
		System.out.println(file.exists());
	    PrintStream printStream = new PrintStream(new FileOutputStream(file));

	    System.out.println("1");

	    System.setOut(printStream);


	    System.out.println("2");
	}

}
