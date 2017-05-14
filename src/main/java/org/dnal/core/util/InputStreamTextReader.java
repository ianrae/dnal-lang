package org.dnal.core.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

/**
 * @author irae
 *
 */
public class InputStreamTextReader {
	
	

	/**
	 * Read string and return contents as single string 
	 * 
	 */
	public String readEntireStream(InputStream stream) {
		String result = null;
		
        try {
			Reader reader = new InputStreamReader(stream);
			BufferedReader r = new BufferedReader(reader);

			StringBuilder sb = new StringBuilder();
			String line = r.readLine();
			while (line != null) {
				sb.append(line);
				sb.append(System.lineSeparator());
			    line = r.readLine();
			}
			result = sb.toString();
		} catch (IOException e) {
			System.out.println("InputStreamTextReader failed: " + e.getMessage());
		}
        return result;
	}
	
}