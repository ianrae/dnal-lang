package org.dnal.api.systest;

import static org.junit.Assert.assertEquals;

import org.dnal.api.view.ViewRenderer;
import org.dnal.core.DType;
import org.dnal.core.DValue;
import org.dnal.core.DViewType;
import org.junit.Test;

public class OutViewTests extends SysTestBase {

	@Test
	public void test() throws Exception {
		addSrc("type Address struct { street string, city string} end ");
		addSrc("outview Address -> AddressDTO { ");
		addSrc(" city -> town string,   street -> lane string } end");
		String src = addSrc(" let x Address = { 'elm', 'ottawa' }");
		chkValue("x", src, 1, 1);

		DType type = dataSetLoaded.getType("AddressDTO");
		assertEquals(null, type);
		DViewType viewType = registry.getViewType("AddressDTO");
		assertEquals("AddressDTO", viewType.getName());

		ViewRenderer renderer = new ViewRenderer(dataSetLoaded);
		DValue source = dataSetLoaded.getValue("x");
		DValue viewval = renderer.render(viewType, source);

		assertEquals("elm", viewval.asStruct().getField("lane").asString());
		assertEquals("ottawa", viewval.asStruct().getField("town").asString());
	}

	@Test
	public void test2() throws Exception {
		addSrc("type Address struct { street string, code string} end ");
		addSrc("outview Address -> AddressDTO { ");
		addSrc(" street -> lane string,    code -> townId int } end");
		String src = addSrc(" let x Address = { 'elm', '405' }");
		chkValue("x", src, 1, 1);

		DType type = dataSetLoaded.getType("AddressDTO");
		assertEquals(null, type);
		DViewType viewType = registry.getViewType("AddressDTO");
		assertEquals("AddressDTO", viewType.getName());

		ViewRenderer renderer = new ViewRenderer(dataSetLoaded);
		DValue source = dataSetLoaded.getValue("x");
		DValue viewval = renderer.render(viewType, source);

		assertEquals("elm", viewval.asStruct().getField("lane").asString());
		assertEquals(405, viewval.asStruct().getField("townId").asInt());
	}

	@Test
	public void testFail1() {
		addSrc("type Address struct { street string, city string} end ");
		addSrc("outview AddressDTO -> Address { ");
		String src = addSrc(" street -> street string,    city -> city string } end");
		chkFail(src, 2, "has already been defined");
	}
	@Test
	public void testFail2() {
		addSrc("type Address struct { street string, city string} end ");
		addSrc("outview ZZZ -> AddressDTO { ");
		String src = addSrc(" street -> street string,    city -> city string } end");
		chkFail(src, 1, "has unknown type");
	}
	@Test
	public void testFail3() {
		addSrc("type Address struct { street string, city string} end ");
		addSrc("outview ZZZ -> AddressDTO { ");
		String src = addSrc(" street <- street string,    city -> city string } end");
		chkFail(src, 2, "cannot mix");
	}
	@Test
	public void testFail4() {
		addSrc("type Address struct { street string, city string} end ");
		addSrc("outview Address -> AddressDTO { ");
		String src = addSrc(" city -> town zzz,   street -> lane string } end");
		chkFail(src, 1, "has unknown type 'zzz'");
	}
	@Test
	public void testFail5() {
		addSrc("type Address struct { street string, city string} end ");
		addSrc("inview Address <- AddressDTO { ");
		String src = addSrc(" city <- town string,   street <- lane string } end");
		chkValue("x", src, 1, 0);
		DViewType viewType = registry.getViewType("AddressDTO");
		assertEquals("AddressDTO", viewType.getName());

		ViewRenderer renderer = new ViewRenderer(dataSetLoaded);
		DValue source = dataSetLoaded.getValue("x");
		boolean ok = false;
		try {
			DValue viewval = renderer.render(viewType, source);
			ok = true;
		} catch (Exception e) {
			assertEquals(true, e instanceof IllegalArgumentException);
			log(e.getMessage());
		}
		assertEquals(false, ok);
	}
	
	@Test
	public void testFile() throws Exception {
		String path = SOURCE_DIR + "address1.dnal";
		loadFile = true;
		
		chkValue("x", path, 1, 1);

		DType type = dataSetLoaded.getType("AddressDTO");
		assertEquals(null, type);
		DViewType viewType = registry.getViewType("AddressDTO");
		assertEquals("AddressDTO", viewType.getName());

		ViewRenderer renderer = new ViewRenderer(dataSetLoaded);
		DValue source = dataSetLoaded.getValue("x");
		DValue viewval = renderer.render(viewType, source);

		assertEquals("elm", viewval.asStruct().getField("lane").asString());
		assertEquals("ottawa", viewval.asStruct().getField("town").asString());
		assertEquals(true, viewval.asStruct().getField("enabled").asBoolean());
		assertEquals("true", viewval.asStruct().getField("flag2").asString());
		assertEquals(142, viewval.asStruct().getField("inum").asInt());
	}

	@Test
	public void testPerson() throws Exception {
		String path = SOURCE_DIR + "person1.dnal";
		loadFile = true;
		
		chkValue("x", path, 2, 1);

		DViewType viewType = registry.getViewType("PersonDTO");
		assertEquals("PersonDTO", viewType.getName());

		ViewRenderer renderer = new ViewRenderer(dataSetLoaded);
		DValue source = dataSetLoaded.getValue("x");
		DValue viewval = renderer.render(viewType, source);

		assertEquals("bob", viewval.asStruct().getField("fname").asString());
		assertEquals("ottawa", viewval.asStruct().getField("town").asString());
	}
	@Test
	public void testPersonBad() throws Exception {
		String path = SOURCE_DIR + "person1.dnal";
		loadFile = true;
		
		chkValue("x", path, 2, 1);

		DViewType viewType = registry.getViewType("PersonBadDTO");
		assertEquals("PersonBadDTO", viewType.getName());

		ViewRenderer renderer = new ViewRenderer(dataSetLoaded);
		DValue source = dataSetLoaded.getValue("x");
		DValue viewval = renderer.render(viewType, source);
		assertEquals(null, viewval);
	}

	@Test
	public void testFormat() throws Exception {
		String path = SOURCE_DIR + "address2.dnal";
		loadFile = true;
		
		chkValue("x", path, 1, 1);

		DViewType viewType = registry.getViewType("AddressDTO");
		ViewRenderer renderer = new ViewRenderer(dataSetLoaded);
		DValue source = dataSetLoaded.getValue("x");
		DValue viewval = renderer.render(viewType, source);

		assertEquals("elm", viewval.asStruct().getField("lane").asString());
		assertEquals("ottawa", viewval.asStruct().getField("town").asString());
		assertEquals("2016", viewval.asStruct().getField("year").asString());
	}

	//-----------------------
	private static final String SOURCE_DIR = "./src/main/resources/test/view/";
	
	private StringBuilder sb = new StringBuilder();

	private String addSrc(String s) {
		sb.append(s);
		return sb.toString();
	}

}
