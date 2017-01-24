package org.dval;

import static org.junit.Assert.*;

import java.io.File;

import org.dval.csv.CSVLoader;
import org.dval.util.StringTrail;
import org.junit.Test;

public class OpenCsvTests {

	@Test
	public void testFiles() {
		String path = "C:\\Users\\ian\\Documents\\GitHub\\dval\\src\\main\\resources\\test\\products1.csv";
		File f = new File(path);
		assertEquals(true, f.exists());

		//current dir is dval
		path = "src/main/resources/test/products1.csv";
		f = new File(path);
		assertEquals(true, f.exists());
	}

	@Test
	public void test2()  {
		String path = "src/main/resources/test/products1.csv";

		CSVLoader loader = new CSVLoader(path);
		assertEquals(true, loader.open());

		String [] nextLine;
		while ((nextLine = loader.readLine()) != null) {
			// nextLine[] is an array of values from the line
			System.out.println(nextLine[0] + nextLine[1] + "etc...");
		}		
		assertEquals(4, loader.getLineCount());
		
		StringTrail trail = new StringTrail(loader.getHdr());
		assertEquals("code;desc;age", trail.toString());
	}


}
