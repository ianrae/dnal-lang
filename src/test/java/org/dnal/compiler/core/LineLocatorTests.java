package org.dnal.compiler.core;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class LineLocatorTests {
	
	public static class LineLocator {
		private String src;
		private String[] ar;
		private boolean containsCR;
		
		public LineLocator(String src) {
			this.src = src;
		}
		
		public int findLineNumForPos(int pos) {
			if (ar == null) {
				ar = src.split("\n");
				containsCR = src.contains("\r");
			}
			
			if (pos <= 0) {
				return 0; //unknown
			}
			
			int lineNum = 1;
			int currPos = 0;
			int EOLSize = (containsCR) ? 1 : 1;
			for(String line: ar) {
				int len = line.length();
				int endLinePos = currPos + len + EOLSize; 
				if (pos >= currPos && pos < endLinePos) {
					return lineNum;
				}
				currPos = endLinePos;
				lineNum++;
			}
			return lineNum;
		}

	}

	@Test
	public void test1() {
		String src = "";
		LineLocator locator = new LineLocator(src);
		assertEquals(0, locator.findLineNumForPos(-1));
		assertEquals(0, locator.findLineNumForPos(0));
		assertEquals(2, locator.findLineNumForPos(1));
		
		chkLocation(src, 0, -1);
		chkLocation(src, 0, 0);
		chkLocation(src, 2, 1);
	}
	
	@Test
	public void test2() {
		String src = "abc";
		chkLocation(src, 1, 1);
		chkLocation(src, 1, 2);
		chkLocation(src, 1, 3);
		chkLocation(src, 2, 4);
	}
	
	@Test
	public void testEOL() {
		String src = "abc\n";
		chkLocation(src, 1, 1);
		chkLocation(src, 1, 2);
		chkLocation(src, 1, 3);
		chkLocation(src, 2, 4);
		
		src = "abc\r\n";
		chkLocation(src, 1, 1);
		chkLocation(src, 1, 2);
		chkLocation(src, 1, 3);
		chkLocation(src, 2, 4);
	}
	
	@Test
	public void testEOL2() {
		String src = "abc\ndef";
		chkLocation(src, 1, 1);
		chkLocation(src, 1, 2);
		chkLocation(src, 1, 3);
		chkLocation(src, 2, 4);
		chkLocation(src, 2, 5);
		chkLocation(src, 2, 6);
		chkLocation(src, 2, 7);
		chkLocation(src, 3, 8);
		
		src = "abc\r\ndef";
		chkLocation(src, 1, 1);
		chkLocation(src, 1, 2);
		chkLocation(src, 1, 3);
		chkLocation(src, 1, 4);
		chkLocation(src, 2, 5);
		chkLocation(src, 2, 6);
		chkLocation(src, 2, 7);
		chkLocation(src, 2, 8);
		chkLocation(src, 3, 9);
	}
	
	//--
	private void chkLocation(String src, int expectedPos, int pos) {
		LineLocator locator = new LineLocator(src);
		assertEquals(expectedPos, locator.findLineNumForPos(pos));
	}
}
