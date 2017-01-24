package org.dnal.core;

import static org.junit.Assert.assertEquals;

import java.util.HashSet;
import java.util.Set;

import org.dnal.core.DStructHelper;
import org.dnal.core.DStructType;
import org.dnal.core.DTypeRegistry;
import org.dnal.core.DValue;
import org.dnal.core.DValueImpl;
import org.dnal.core.compare.DStructComparer;
import org.dnal.core.fluent.type.TypeBuilder;
import org.dnal.core.oldbuilder.XStructValueBuilder;
import org.junit.Test;

public class DiffPartialTests extends BaseDValTest {
	
	@Test
	public void testPartial() {
		DStructType cityType = this.buildCityType(registry);
		DValue val = this.buildCity(registry, cityType, "boston");
		DValue val2 = this.buildCity(registry, cityType, "boston");

		DStructComparer differ = new DStructComparer();
		Set<String> fieldNames = new HashSet<>();
		assertEquals(true, differ.compare(val, val, fieldNames));
		assertEquals(true, differ.compare(val, val2, fieldNames));
		
		fieldNames.add("name");
		assertEquals(true, differ.compare(val, val2, fieldNames));
		
		DValue val3 = this.buildCity(registry, cityType, "toronto");
		assertEquals(false, differ.compare(val, val3, fieldNames));
		
		val2.asMap().put("flag2", buildBooleanVal(registry, true));
		assertEquals(true, differ.compare(val, val2, fieldNames));
		fieldNames.add("flag2");
		assertEquals(false, differ.compare(val, val2, fieldNames));
	}
	
	@Test
	public void testPartialWithHelper() {
		DStructType cityType = this.buildCityType(registry);
		DValue val = this.buildCity(registry, cityType, "boston");
		DValue val2 = this.buildCity(registry, cityType, "boston");

		DStructComparer differ = new DStructComparer();
		Set<String> fieldNames = new HashSet<>();
		fieldNames.add("name");
		fieldNames.add("flag2");
		assertEquals(true, differ.compare(val, val2, fieldNames));
		
		DStructHelper helper = val2.asStruct();
		DValue inner = helper.getField("flag2");
        DValueImpl implInner = (DValueImpl) inner;
		implInner.forceObject(Boolean.TRUE);
		assertEquals(false, differ.compare(val, val2, fieldNames));
	}

	//-----
	private DStructType buildCityType(DTypeRegistry registry) {
		TypeBuilder tb = new TypeBuilder(registry, world);
		tb.start("City")
		.string("name").minSize(2)
		.bool("flag2")
		.end();

		DStructType type = tb.getType();
		return type;
	}
	
	public DValue buildCity(DTypeRegistry registry, DStructType cityType, String cityName) {
		XStructValueBuilder builder = new XStructValueBuilder(cityType);
		builder.addField("name", buildStringVal(registry, cityName));
		builder.addField("flag2", buildBooleanVal(registry, false));
		builder.finish();
		chkErrors(builder, 0);
		DValue addr = builder.getDValue();
		return addr;
	}
	

}
