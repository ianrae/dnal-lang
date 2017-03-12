package org.dnal.other;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.StringReader;

import org.dnal.core.util.TextComparer;
import org.junit.Test;

public class StringReaderTests {

	@Test
	public void test() throws Exception {
		String s = buildLines("line1", "line2", "line3");
		BufferedReader in = new BufferedReader(new StringReader(s));

		TextComparer cmp = new TextComparer();
		String line;
		while ((line = in.readLine()) != null) {
			System.out.println(line);
		}
	}

	@Test
	public void testText() {
		String target = buildLines("line1", "line2", "line3");
		chkCompare(true, target, "line1", "line2", "line3");
		chkCompare(false, target, "line1", "line2", "line3ss");
		
		chkCompare(true, target, "line1", "line2", "line3");
		chkCompare(false, target, "line1", "line2", "line3ss");
	}
	
	@Test
	public void testText2() {
		String target = buildLines("line1", null, null);
		
		chkCompare(true, target, "line1", null, null);
		chkCompare(false, target, "line1xx", null, null);
		
		chkCompare(false, target, "line1", "line2", null);
		
		target = buildLines("line1", "line2", null);
		chkCompare(false, target, "line1", null, null);
		
		target = buildLines("", " ", null);
		chkCompare(true, target, "", " ", null);
	}
	
	private String target;
	private void chkCompare(boolean expected, String target, String line1, String line2, String line3) {
		String s2 = buildLines(line1, line2, line3);

		TextComparer comparer = new TextComparer();
		assertEquals(expected, comparer.compareText(target, s2));
	}

	private String buildLines(String line1, String line2, String line3) {
		String         ls = System.getProperty("line.separator");
		StringBuilder sb = new StringBuilder();
		if (line1 != null) {
			sb.append(line1);
			sb.append(ls);
		}
		if (line2 != null) {
			sb.append(line2);
			sb.append(ls);
		}
		if (line3 != null) {
			sb.append(line3);
			sb.append(ls);
		}
		return sb.toString();
	}

}
