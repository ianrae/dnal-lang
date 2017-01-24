package com.github.ianrae.dnalparse.parser.ast;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jparsec.Token;

public class CustomRule extends RuleExp {
	public String ruleName;
	public List<Exp> argL = new ArrayList<>();
	public boolean polarity;
	public Exp hackExtra;

	public CustomRule(String ruleName,  List<List<Exp>> args, String not) {
		this.ruleName = ruleName;
		this.polarity = (not == null);
		
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
	public CustomRule(String name, RangeExp range, String not) {
		this.ruleName = name;
        this.polarity = (not == null);
		argL.add(range);
	}
	
	public String strValue() {
		String ss = String.format("%s(", ruleName);
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