package org.dnal.compiler.parser.ast;

import java.util.ArrayList;
import java.util.List;


public class ViewFormatExp extends RuleExp {
	public String fnName;
	public List<Exp> argL = new ArrayList<>();
	public Exp hackExtra;

	public ViewFormatExp(IdentExp ruleExp,  List<List<Exp>> args) {
		this.fnName = ruleExp.val;
		
		if (args != null) {
			List<Exp> list = new ArrayList<>();
			if (! args.isEmpty()) {
				for(List<Exp> sublist : args) {
					if (! sublist.isEmpty()) {
						list.add(sublist.get(0));
					}
				}
			}
			argL = list;
		}
	}
	
	public String strValue() {
		String ss = String.format("%s(", fnName);
		int i = 0;
		for(Exp exp : argL) {
			if (i > 0) {
				ss += "," + exp.strValue();
			} else {
				ss += exp.strValue();
			}
			i++;
		}
		ss = String.format("%s)", ss);
		return ss;
	}
}