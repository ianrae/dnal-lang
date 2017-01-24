package org.dnal.core;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.dnal.core.BuiltInTypes;
import org.dnal.core.DType;
import org.dnal.core.DTypeRegistry;
import org.dnal.core.DTypeRegistryBuilder;
import org.dnal.core.DValue;
import org.dnal.core.ErrorMessage;
import org.dnal.core.ValidationState;
import org.dnal.core.logger.Log;
import org.dnal.core.nrule.SimpleNRuleRunner;
import org.dnal.core.nrule.ValidationScorer;
import org.dnal.core.oldbuilder.XBooleanValueBuilder;
import org.dnal.core.oldbuilder.XDValueBuilder;
import org.dnal.core.oldbuilder.XDateValueBuilder;
import org.dnal.core.oldbuilder.XEnumValueBuilder;
import org.dnal.core.oldbuilder.XIntegerValueBuilder;
import org.dnal.core.oldbuilder.XLongValueBuilder;
import org.dnal.core.oldbuilder.XNumberValueBuilder;
import org.dnal.core.oldbuilder.XStringValueBuilder;
import org.dnal.core.repository.MockRepositoryFactory;
import org.dnal.core.repository.MyWorld;
import org.junit.Before;

public class BaseDValTest {
	protected MyWorld world = new MyWorld();
	
	//---------
	protected DTypeRegistry registry;

	@Before
	public void init() {
		registry = initRegistry();
	}

	//-----
	protected DValue buildStringVal(DTypeRegistry registry, String input) {
		DType type = registry.getType(BuiltInTypes.STRING_SHAPE);
		XStringValueBuilder builder = new XStringValueBuilder(type);
		builder.buildFromString(input);
		assertEquals(true, builder.finish());
		return builder.getDValue();
	}
	protected DValue buildEnumVal(DTypeRegistry registry, String input) {
		DType type = registry.getType(BuiltInTypes.ENUM_SHAPE);
		XEnumValueBuilder builder = new XEnumValueBuilder(type);
		builder.buildFromString(input);
		assertEquals(true, builder.finish());
		return builder.getDValue();
	}
	protected DValue buildIntVal(DTypeRegistry registry, int n) {
		DType type = registry.getType(BuiltInTypes.INTEGER_SHAPE);
		XIntegerValueBuilder builder = new XIntegerValueBuilder(type);
		builder.buildFrom(n);
		assertEquals(true, builder.finish());
		return builder.getDValue();
	}
    protected DValue buildLongVal(DTypeRegistry registry, long n) {
        DType type = registry.getType(BuiltInTypes.LONG_SHAPE);
        XLongValueBuilder builder = new XLongValueBuilder(type);
        builder.buildFrom(Long.valueOf(n));
        assertEquals(true, builder.finish());
        return builder.getDValue();
    }
    protected DValue buildNumberVal(DTypeRegistry registry, double d) {
        DType type = registry.getType(BuiltInTypes.NUMBER_SHAPE);
        XNumberValueBuilder builder = new XNumberValueBuilder(type);
        builder.buildFrom(Double.valueOf(d));
        assertEquals(true, builder.finish());
        return builder.getDValue();
    }
	protected DValue buildBooleanVal(DTypeRegistry registry, boolean b) {
		DType type = registry.getType(BuiltInTypes.BOOLEAN_SHAPE);
		XBooleanValueBuilder builder = new XBooleanValueBuilder(type);
		builder.buildFrom(Boolean.valueOf(b));
		assertEquals(true, builder.finish());
		return builder.getDValue();
	}
	protected DValue buildDateVal(DTypeRegistry registry, String input) {
		DType type = registry.getType(BuiltInTypes.DATE_SHAPE);
		XDateValueBuilder builder = new XDateValueBuilder(type);
		builder.buildFromString(input);
		assertEquals(true, builder.finish());
		return builder.getDValue();
	}
	protected void dumpErrors(XDValueBuilder builder) {
		for(ErrorMessage err: builder.getValidationErrors()) {
			log(err.getMessage());
		}
	}
	protected void chkErrors(XDValueBuilder builder, int expected) {
		dumpErrors(builder);
		assertEquals(expected, builder.getValidationErrors().size());
	}
	protected DTypeRegistry initRegistry() {
		world.setRepositoryFactory(new MockRepositoryFactory());
		
		DTypeRegistryBuilder regBuilder = new DTypeRegistryBuilder();
		regBuilder.init(world);
		DTypeRegistry registry = regBuilder.getRegistry();
		return registry;
	}
	protected void log(String s) {
		Log.log(s);
	}
	
//	protected void dumpValErrors(BaseVRuleRunner runner) {
//		dumpValErrors(runner.getValidationErrors());
//	}
	protected void dumpValErrors(List<ErrorMessage> errorL) {
		for(ErrorMessage err: errorL) {
			log("rule failed: " + err.getMessage());
		}
	}
//	protected void chkValErrors(BaseVRuleRunner runner, int expected) {
//		dumpValErrors(runner);
//		assertEquals(expected, runner.getValidationErrors().size());
//	}
	
	protected void chkInvalid(DValue dval) {
		assertEquals(ValidationState.INVALID, dval.getValState());
	}
	protected void chkValid(DValue dval) {
		assertEquals(ValidationState.VALID, dval.getValState());
	}
	protected void chkUnknown(DValue dval) {
		assertEquals(ValidationState.UNKNOWN, dval.getValState());
	}
    protected void registerType(String typeName, DType dtype) {
        registry.add(typeName, dtype);
        world.typeRegistered(dtype);
    }
	
	
	
	protected void chkScorer(ValidationScorer scorer, boolean allValid, boolean someInvalid, boolean someUnknown) {
		assertEquals(allValid, scorer.allValid());
		assertEquals(someInvalid, scorer.someInvalid());
		assertEquals(someUnknown, scorer.someUnknown());
	}
	protected void chkScorerCounts(ValidationScorer scorer, int allValid, int someInvalid, int someUnknown) {
		assertEquals(allValid, scorer.getValidCount());
		assertEquals(someInvalid, scorer.getInvalidCount());
		assertEquals(someUnknown, scorer.getUnknownCount());
	}
	
	
	
	protected void xchkValErrors(SimpleNRuleRunner runner, int expected) {
		xdumpValErrors(runner);
		assertEquals(expected, runner.getValidationErrors().size());
	}
	protected void xdumpValErrors(SimpleNRuleRunner runner) {
		dumpValErrors(runner.getValidationErrors());
	}
	
}
