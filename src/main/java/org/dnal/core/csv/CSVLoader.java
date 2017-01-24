package org.dnal.core.csv;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import au.com.bytecode.opencsv.CSVReader;

public class CSVLoader {
	private String path;
	private CSVReader reader;
	private String[] hdr;
	private int lineCount;
    private char delim;

    public CSVLoader(String path) {
        this(path, ';');
    }
	public CSVLoader(String path, char delim) {
		this.path = path;
		this.delim = delim;
	}

	public boolean open() {
		boolean ok = false;
		try {
			reader = new CSVReader(new FileReader(path), delim);
			ok = true;
		} catch (FileNotFoundException e) {
		}
		return ok;
	}

	public String[] readLine() {
		String [] nextLine = null;
		try {
			nextLine = reader.readNext();
			if (nextLine == null) {
				return null;
			} else if (isEmpty(nextLine)) {
				return null;
			}
			
			//first non-empty line
			if (hdr == null) {
				hdr = nextLine;
			}
			
		} catch (IOException e) {
			System.out.println("CSVLoader error: " + e.getMessage());
		}
		
		lineCount++;
		return nextLine;
	}

	private boolean isEmpty(String[] nextLine) {
		if (nextLine.length == 0) {
			return true;
		}

		if (nextLine.length == 1 && nextLine[0].isEmpty()) {
			return true;
		}
		return false;
	}

	public String[] getHdr() {
		return hdr;
	}

	public Object getLineCount() {
		return lineCount;
	}
}