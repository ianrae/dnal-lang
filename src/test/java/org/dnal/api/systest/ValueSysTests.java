package org.dnal.api.systest;

import static org.junit.Assert.assertEquals;

import org.dnal.core.DValue;
import org.junit.Test;

public class ValueSysTests extends SysTestBase {

    @Test
    public void testT200() {
        chkFail("type Foo int end let x Foo = 14 let x int = 44", 1, "value name 'x' has already been defined");
    }
    
    @Test
    public void testT201() {
        chkFail("let x int = 5 let y int = zzz", 1, "cannot resolve reference to 'zzz'");
    }
    
}