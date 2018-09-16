package org.dnal.compiler.parser.error;

public class LineLocator {
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