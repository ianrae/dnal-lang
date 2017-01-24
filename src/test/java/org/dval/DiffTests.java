package org.dval;

import static org.junit.Assert.assertEquals;

import org.dnal.core.BuiltInTypes;
import org.dnal.core.DListType;
import org.dnal.core.DType;
import org.dnal.core.DValue;
import org.dnal.core.DValueImpl;
import org.dnal.core.Shape;
import org.dnal.core.compare.DValueComparer;
import org.dnal.core.oldbuilder.XListValueBuilder;
import org.junit.Test;

public class DiffTests extends BaseDValTest {
	
	@Test
	public void testDValueInt() {
		DValue val = buildInt(33L);
		DValue val2 = buildInt(33L);

		DValueComparer differ = new DValueComparer();
		assertEquals(true, differ.compare(val, val));
		assertEquals(true, differ.compare(val, val2));

		assertEquals(false, differ.compare(null, null));
		assertEquals(false, differ.compare(val, null));
		assertEquals(false, differ.compare(null, val2));

		DValue val3 = buildInt(223L);
		assertEquals(false, differ.compare(val, val3));
	}
	@Test
	public void testDValueStr() {
		DValue val = buildStr("abc");
		DValue val2 = buildStr("abc");

		DValueComparer differ = new DValueComparer();
		assertEquals(true, differ.compare(val, val));
		assertEquals(true, differ.compare(val, val2));

		assertEquals(false, differ.compare(null, null));
		assertEquals(false, differ.compare(val, null));
		assertEquals(false, differ.compare(null, val2));

		DValue val3 = buildStr("ABC");
		assertEquals(false, differ.compare(val, val3)); //case-sensitive

		DValue val4 = buildInt(223L);
		assertEquals(false, differ.compare(val, val4));
	}
	@Test
	public void testList() {
		DValue val = buildList("abc", "def");
		DValue val2 = buildList("abc", "def");

		DValueComparer differ = new DValueComparer();
		assertEquals(true, differ.compare(val, val));
		assertEquals(true, differ.compare(val, val2));

		DValue val3 = buildList("def", "abc");
		DValue val4 = buildList("def", "abc", "h");
		assertEquals(false, differ.compare(val, val3));
		assertEquals(false, differ.compare(val3, val4));
	}
	
	//-----
	private DValue buildInt(long n) {
		DType type = registry.getType(BuiltInTypes.STRING_SHAPE);

		DValue val = new DValueImpl(type, Long.valueOf(n));
		return val;
	}
	private DValue buildStr(String s) {
		DType type = registry.getType(BuiltInTypes.STRING_SHAPE);

		DValue val = new DValueImpl(type, s);
		return val;
	}

	private DValue buildList(String s1, String s2) {
		return buildList(s1, s2, null);
	}
	private DValue buildList(String s1, String s2, String s3) {
		DType eltype = registry.getType(BuiltInTypes.STRING_SHAPE);
		if (registry.getType("mylist") == null) {
			DListType type = new DListType(Shape.LIST, "mylist", null, eltype);
			registerType("mylist", type);
		}
		DListType type = (DListType) registry.getType("mylist");
		XListValueBuilder builder = new XListValueBuilder(type);

		DValue sval = buildStringVal(registry, s1);
		builder.addValue(sval);
		sval = buildStringVal(registry, s2);
		builder.addValue(sval);
		
		if (s3 != null) {
			sval = buildStringVal(registry, s3);
			builder.addValue(sval);
		}
		
		builder.finish();
		assertEquals(true, builder.wasSuccessful());
		return builder.getDValue();
	}
}
