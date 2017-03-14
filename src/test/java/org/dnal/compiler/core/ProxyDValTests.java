package org.dnal.compiler.core;

import org.junit.Test;

public class ProxyDValTests  extends BaseValidationTests  {
    
    @Test
    public void test1() {
        chkRule(">", 99, false);
    }


    private void chkRule(String op, int n, boolean ok) {
        String s = String.format("type Foo int %s 100 end let x Foo = %d", op, n);
        parseAndValidate(s, ok);
    }  

    private void parseAndValidate(String input, boolean expected) {
        parseAndValidate(input, expected, "INTEGER_SHAPE");
    }

}
