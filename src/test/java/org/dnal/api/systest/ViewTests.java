package org.dnal.api.systest;

import org.junit.Test;

public class ViewTests extends SysTestBase {

    @Test
    public void test() {
        String src1 = "type Address struct { street string city string} end ";
        String src2 = "view AddressDTO <- Address { ";
        String src3 = " city <- city street <- street } end";
        chkValue("x", src1 + src2 + src3, 1, 0);
    }
    
    @Test
    public void testFail1() {
        String src1 = "type Address struct { street string city string} end ";
        String src2 = "view Address <- Address { ";
        String src3 = " city <- city street <- street } end";
        chkFail(src1 + src2 + src3, 1, "has already been defined");
    }
    @Test
    public void testFail2() {
        String src1 = "type Address struct { street string city string} end ";
        String src2 = "view AddressDTO <- ZZZs { ";
        String src3 = " city <- city street <- street } end";
        chkFail(src1 + src2 + src3, 1, "has already been defined");
    }

//    @Test
//    public void test514fail4() {
//        String src = buildBothMulti();
//        src = src.replace("[ via personCode code ]", "[ via personCode xxxcode ]"); //invalid field name
//        chkFail(src, 5, "Acan't resolve via: personCode");
//    }
    

    //-----------------------
    private StringBuilder sb;

    private void o(String s) {
        sb.append(s);
    }
    
    protected void chkView(String varName, String source, int expectedTypes, int expectedVals) {
        chk(source, expectedTypes, expectedVals);
    }    
    
}
