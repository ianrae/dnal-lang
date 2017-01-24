package org.dnal.core.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Reads a text file and returns the contents as a list of lines of text.
 * Generally only used in junit tests;
 *
 * @author irae
 *
 */
public class TextFileReader {
	
	

	/**
	 * Read file and return contents as single string 
	 * 
	 */
	public String readFileAsSingleString(String path) {
		List<String> lines = readFile(path);
		
		String lf = System.getProperty("line.separator");
		StringBuilder sb = new StringBuilder();
		for(String s : lines) {
			sb.append(s);
			sb.append(lf);
		}
		return sb.toString();
	}
	
    /**
     *
     * @param path
     * @return list of all the lines of text in the file
     */
    @SuppressWarnings("PMD.AvoidPrintStackTrace")
    public List<String> readFile(final String path) {
        try {
            return doReadFile(path);
        } catch (final IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     *
     * @param path
     * @return list of all the lines of text in the file
     * @throws IOException
     * @throws Exception
     */
    private List<String> doReadFile(final String path) throws IOException {
        final List<String> linesL = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            final StringBuilder builder = new StringBuilder();
            String line = reader.readLine();

            while (line != null) {
                builder.append(line);
                builder.append(System.lineSeparator());
                linesL.add(line);
                line = reader.readLine();
            }
        }
        return linesL;
    }
}