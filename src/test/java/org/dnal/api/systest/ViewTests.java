package org.dnal.api.systest;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.dnal.api.DataSet;
import org.dnal.api.Transaction;
import org.dnal.api.impl.DataSetImpl;
import org.dnal.core.DStructType;
import org.dnal.core.DType;
import org.dnal.core.DTypeRegistry;
import org.dnal.core.DValue;
import org.dnal.core.DValueImpl;
import org.dnal.core.DViewType;
import org.junit.Test;

public class ViewTests extends SysTestBase {
	
	public static class ViewRenderer {
		private DataSet ds;
		private DTypeRegistry registry;
		
		public ViewRenderer(DataSet ds) {
			this.ds = ds;
			DataSetImpl dsi = (DataSetImpl) ds;
			this.registry = dsi.getInternals().getRegistry();
		}
		public DValue render(DViewType viewType, DValue source) {
			DType sourceType = registry.getType(viewType.getRelatedTypeName());
	        if (!(sourceType instanceof DStructType)) {
	        	throw new IllegalArgumentException(String.format("can't find type: %s", viewType.getRelatedTypeName()));
	        }
			
	        if (! sourceType.isAssignmentCompatible(source.getType())) {
	        	throw new IllegalArgumentException(String.format("type mismatch. view expects %s but got %s", viewType.getRelatedTypeName(), source.getType().getName()));
	        }
	        
	        Transaction trans = ds.createTransaction();
//	        DStructType structType = (DStructType) sourceType;
	        Map<String,DValue> resultMap = new HashMap<>();
	        for(String viewFieldName: viewType.getFields().keySet()) {
	        	String srcField = viewType.getNamingMap().get(viewFieldName);
	        	DValue inner = source.asStruct().getField(srcField);
	        	resultMap.put(viewFieldName, inner);
	        }
			
	        DValueImpl dval = new DValueImpl(viewType, resultMap); 
			return dval;
		}
	}

    @Test
    public void test() {
        String src1 = "type Address struct { street string city string} end ";
        String src2 = "view AddressDTO <- Address { ";
        String src3 = " city string <- city street string <- street } end";
        String src4 = " let x Address = { 'elm', 'ottawa' }";
        chkValue("x", src1 + src2 + src3 + src4, 1, 1);
        
        DType type = dataSetLoaded.getType("AddressDTO");
        assertEquals(null, type);
        DViewType viewType = registry.getViewType("AddressDTO");
        assertEquals("AddressDTO", viewType.getName());
        
        ViewRenderer renderer = new ViewRenderer(dataSetLoaded);
        DValue source = dataSetLoaded.getValue("x");
        DValue viewval = renderer.render(viewType, source);
        
        assertEquals("elm", viewval.asStruct().getField("street").asString());
        assertEquals("ottawa", viewval.asStruct().getField("city").asString());
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
