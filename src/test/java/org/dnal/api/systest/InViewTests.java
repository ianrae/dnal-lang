package org.dnal.api.systest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.dnal.api.Transaction;
import org.dnal.api.view.ViewLoader;
import org.dnal.core.DStructType;
import org.dnal.core.DType;
import org.dnal.core.DValue;
import org.dnal.core.DViewType;
import org.dnal.core.builder.StructBuilder;
import org.junit.Test;

public class InViewTests extends SysTestBase {
	
	
	@Test
	public void test() throws Exception {
		addSrc("type Address struct { street string, city string} end ");
		addSrc("inview Address <- AddressDTO { ");
		addSrc(" city <- town string   street <- lane string } end");
		String src = addSrc(" let x Address = { 'elm', 'ottawa' }");
		chkValue("x", src, 1, 1);

		DType type = dataSetLoaded.getType("AddressDTO");
		assertEquals(null, type);
		DViewType viewType = registry.getViewType("AddressDTO");
		assertEquals("AddressDTO", viewType.getName());

		Transaction trans = dataSetLoaded.createTransaction();
		StructBuilder builder = trans.createStructBuilder(viewType);
		DValue inner = trans.createStringBuilder().buildFromString("oxford");
		builder.addField("town", inner);
		inner = trans.createStringBuilder().buildFromString("main");
		builder.addField("lane", inner);
		DValue viewVal = builder.finish();
		
		DType targetType = dataSetLoaded.getType("Address");
		ViewLoader loader = new ViewLoader(dataSetLoaded);
		DValue dval = loader.load(viewVal, (DStructType) targetType);

		assertEquals("Address", dval.getType().getName());
		assertEquals("main", dval.asStruct().getField("street").asString());
		assertEquals("oxford", dval.asStruct().getField("city").asString());
	}


	@Test
	public void testPerson() throws Exception {
		String path = SOURCE_DIR + "person1.dnal";
		loadFile = true;
		chkValue("x", path, 2, 1);

		DViewType viewType = registry.getViewType("PersonInputBad");
		assertEquals("PersonInputBad", viewType.getName());
		
		Transaction trans = dataSetLoaded.createTransaction();
		StructBuilder builder = trans.createStructBuilder(viewType);
		DValue inner = trans.createStringBuilder().buildFromString("sue");
		builder.addField("fname", inner);
		DValue dval = builder.finish();
		assertEquals(false, builder.wasSuccessful());
		assertNotNull(dval);
	}
	

	//-----------------------
	private static final String SOURCE_DIR = "./src/main/resources/test/view/";
	
	private StringBuilder sb = new StringBuilder();

	private String addSrc(String s) {
		sb.append(s);
		return sb.toString();
	}

}
