package com.github.ianrae.dnalparse.parser.ast;

import java.util.List;

public class IsaRuleExp extends RuleExp {
    
    public String fieldName;
    public String val;
    
    public IsaRuleExp(IdentExp exp, List<List<IdentExp>> arg) {
        this.fieldName = (exp == null) ? null : exp.val;
        
        StringBuilder sb = new StringBuilder();
        boolean flag = false;
        for(List<IdentExp> sublist : arg) {
            if (! sublist.isEmpty()) {
                if (flag) {
                    sb.append('.');
                }
                sb.append(sublist.get(0).val);
                flag = true;
            }
        }
        this.val = sb.toString();
    }

    @Override
    public String strValue() {
        return val;
    }

}
