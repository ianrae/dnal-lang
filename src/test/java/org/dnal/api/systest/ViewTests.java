package org.dnal.api.systest;

import org.junit.Test;

public class ViewTests extends SysTestBase {

    @Test
    public void test() {
        String src1 = "type Address struct { street string city string} end ";
        String src2 = "view AddressDTO <- Address { ";
        String src3 = " city string <- city street string <- street } end";
        chkValue("x", src1 + src2 + src3, 1, 0);
    }
    
    @Test
    public void testFail1() {
        String src1 = "type Address struct { street string city string} end ";
        String src2 = "view Address <- Address { ";
        String src3 = " city string <- city street string <- street } end";
        chkFail(src1 + src2 + src3, 1, "has already been defined");
    }
    @Test
    public void testFail2() {
        String src1 = "type Address struct { street string city string} end ";
        String src2 = "view AddressDTO <- ZZZs { ";
        String src3 = " city string <- city  street string <- street } end";
        chkFail(src1 + src2 + src3, 1, "has unknown type");
    }
    @Test
    public void testFail3() {
        String src1 = "type Address struct { street string city string} end ";
        String src2 = "view AddressDTO <- Address { ";
        String src3 = " city string <- city street string -> street } end";
        chkFail(src1 + src2 + src3, 1, "cannot mix");
    }
    @Test
    public void testFail4() {
        String src1 = "type Address struct { street string city string} end ";
        String src2 = "view AddressDTO <- Address { ";
        String src3 = " city zzz <- city street string <- street } end";
        chkFail(src1 + src2 + src3, 1, "has unknown type 'zzz'");
    }
    

    //-----------------------
    private StringBuilder sb;

    private void o(String s) {
        sb.append(s);
    }
    
    protected void chkView(String varName, String source, int expectedTypes, int expectedVals) {
        chk(source, expectedTypes, expectedVals);
    }    
    
}
