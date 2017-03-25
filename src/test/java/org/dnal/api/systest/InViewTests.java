package org.dnal.api.systest;

import static org.junit.Assert.assertEquals;

import org.dnal.api.view.ViewRenderer;
import org.dnal.core.DType;
import org.dnal.core.DValue;
import org.dnal.core.DViewType;
import org.junit.Test;

public class InViewTests extends SysTestBase {

	@Test
	public void test() throws Exception {
		addSrc("type Address struct { street string city string} end ");
		addSrc("inview Address <- AddressDTO { ");
		addSrc(" city <- town string   street <- lane string } end");
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
