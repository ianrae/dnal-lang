package org.dnal.compiler.parser.ast;

import java.util.ArrayList;
import java.util.List;

public class CustomRule extends RuleExp {
	public String ruleName;
	public String fieldName; //can be null
	public List<Exp> argL = new ArrayList<>();
	public boolean polarity;
	public Exp hackExtra;

	public CustomRule(RuleWithFieldExp ruleExp,  List<List<Exp>> args, String not) {
		this.ruleName = ruleExp.ruleName;
		this.fieldName = ruleExp.fieldName;
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
    public CustomRule(RuleWithFieldExp ruleExp, RangeExp range, String not) {
        this.ruleName = ruleExp.ruleName;
        this.fieldName = ruleExp.fieldName;
        this.polarity = (not == null);
        argL.add(range);
    }
	
	@Override
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