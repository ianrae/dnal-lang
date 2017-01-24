package org.dnal.core.oldvalidation;


public class ExpresssionParser {

	public String parseFunctionArg(String ruleText, String fnName) {
		String s = ruleText.trim();
		String target = fnName + "(";
		int pos1 = s.indexOf(target);
		int pos2 = s.indexOf(")");
		if (pos1 >= 0 && pos2 > pos1) {
			String sval = s.substring(pos1 + target.length(), pos2);
			return sval;
		}
		return null;
	}
	public Long parseFunctionArgLong(String ruleText, String fnName) {
		String sval = parseFunctionArg(ruleText, fnName);
		if (sval != null) {
			Long lval = Long.parseLong(sval);
			return lval;
		}
		return null;
	}
	
	public String parseFunctionName(String ruleText) {
		String s = ruleText.trim();
		String target = "(";
		int pos1 = s.indexOf(target);
		if (pos1 > 0) {
			String sval = s.substring(0, pos1);
			return sval;
		}
		return null;
	}
	public String parseFunctionFirstArg(String ruleText, String fnName) {
		String s = ruleText.trim();
		String target = fnName + "(";
		int pos1 = s.indexOf(target);
		int pos2 = s.indexOf(",");
		if (pos1 >= 0 && pos2 > pos1) {
			String sval = s.substring(pos1 + target.length(), pos2);
			return sval;
		}
		return null;
	}
	public String parseFunctionSecondArg(String ruleText, String fnName) {
		String s = ruleText.trim();
		String target = fnName + "(";
		int pos1 = s.indexOf(target);
		int pos2 = s.indexOf(",");
		if (pos1 >= 0 && pos2 > pos1) {
			int pos3 = s.indexOf(")", pos2 + 1);
			String sval = s.substring(pos2 + 1, pos3);
			return sval;
		}
		return null;
	}
}
