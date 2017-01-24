package com.github.ianrae.dnalparse.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

import org.dval.logger.Log;


public class TextComparer {
	private int numErrors;
	private int lineNum;

	public boolean compare(String s1, String s2) {
		if (! s1.equals(s2)) {
			return false;
		}
		return true;
	}

	public boolean compareText(String text1, String text2) {
		numErrors = 0;
		lineNum = 1;
		try {
			boolean b = doCompareText(text1, text2);
			return b;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	private boolean doCompareText(String text1, String text2) throws IOException {
		BufferedReader in1 = new BufferedReader(new StringReader(text1));
		BufferedReader in2 = new BufferedReader(new StringReader(text2));

		String line;
		while ((line = in1.readLine()) != null) {
			//System.out.println(line);
			String line2 = in2.readLine();
			
			if (line2 == null) {
				addError(String.format("(text1 is longer): \n %s\n %s", line, line2));
			} else if (! compare(line, line2)) {
				addError(String.format(": \n %s\n %s", line, line2));
			}
			lineNum++;
		}
		
		String extra2 = in2.readLine();
		if (extra2 != null) {
			addError(String.format("(text2 is longer): \n %s\n %s", null, extra2));
		}

		in1.close();
		in2.close();

		return numErrors == 0;
	}
	
	private void addError(String msg) {
		String ss = String.format("Error at line %d ", lineNum);
		Log.log(ss + msg);
		numErrors++;
	}
}