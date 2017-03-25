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
		addSrc("type Address struct { street string city string} end ");
		addSrc("outview Address -> AddressDTO { ");
		addSrc(" city -> town string   street -> lane string } end");
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
		addSrc("type Address struct { street string code string} end ");
		addSrc("outview Address -> AddressDTO { ");
		addSrc(" street -> lane string    code -> townId int } end");
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
		addSrc("type Address struct { street string city string} end ");
		addSrc("outview AddressDTO -> Address { ");
		String src = addSrc(" street -> street string    city -> city string } end");
		chkFail(src, 2, "has already been defined");
	}
	@Test
	public void testFail2() {
		addSrc("type Address struct { street string city string} end ");
		addSrc("outview ZZZ -> AddressDTO { ");
		String src = addSrc(" street -> street string    city -> city string } end");
		chkFail(src, 1, "has unknown type");
	}
	@Test
	public void testFail3() {
		addSrc("type Address struct { street string city string} end ");
		addSrc("outview ZZZ -> AddressDTO { ");
		String src = addSrc(" street <- street string    city -> city string } end");
		chkFail(src, 2, "cannot mix");
	}
	@Test
	public void testFail4() {
		addSrc("type Address struct { street string city string} end ");
		addSrc("outview Address -> AddressDTO { ");
		String src = addSrc(" city -> town zzz   street -> lane string } end");
		chkFail(src, 1, "has unknown type 'zzz'");
	}


	//-----------------------
	private StringBuilder sb = new StringBuilder();

	private String addSrc(String s) {
		sb.append(s);
		return sb.toString();
	}

	protected void chkView(String varName, String source, int expectedTypes, int expectedVals) {
		chk(source, expectedTypes, expectedVals);
	}    

}
