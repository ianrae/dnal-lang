package org.dnal.api.systest;

import static org.junit.Assert.assertEquals;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.dnal.api.DataSet;
import org.dnal.api.Transaction;
import org.dnal.api.impl.DataSetImpl;
import org.dnal.compiler.et.XErrorTracker;
import org.dnal.compiler.parser.ast.ViewDirection;
import org.dnal.core.DStructType;
import org.dnal.core.DType;
import org.dnal.core.DTypeRegistry;
import org.dnal.core.DValue;
import org.dnal.core.DValueImpl;
import org.dnal.core.DViewType;
import org.dnal.core.NewErrorMessage;
import org.dnal.core.builder.StructBuilder;
import org.junit.Test;

public class InViewTests extends SysTestBase {
	
	
	public class ViewLoader {
		private DataSet ds;
		private DTypeRegistry registry;
		private XErrorTracker et;

		public ViewLoader(DataSet ds) {
			this.ds = ds;
			DataSetImpl dsi = (DataSetImpl) ds;
			et = dsi.getCompilerContext().et;
			this.registry = dsi.getInternals().getRegistry();
		}
		public DValue load(DValue source, DStructType structType) throws Exception {
			if (!(source.getType() instanceof DViewType)) {
				throw new IllegalArgumentException(String.format("source must be a DViewType: %s", source.getType().getName()));
			}
			DViewType viewType = (DViewType) source.getType();
			DType sourceType = registry.getType(viewType.getRelatedTypeName());
			
			if (!(sourceType instanceof DStructType)) {
				throw new IllegalArgumentException(String.format("can't find type: %s", viewType.getRelatedTypeName()));
			}

			if (! viewType.getDirection().equals(ViewDirection.INBOUND)) {
				throw new IllegalArgumentException(String.format("cannot render an inview: %s", viewType.getName()));
			}

			//source can be circle and sourceType can be shape
			if (! sourceType.isAssignmentCompatible(sourceType)) {
				throw new IllegalArgumentException(String.format("type mismatch. view expects %s but got %s", viewType.getRelatedTypeName(), source.getType().getName()));
			}

			Transaction trans = ds.createTransaction();
			Map<String,DValue> resultMap = new HashMap<>();
			for(String targetFieldName: structType.getFields().keySet()) {
				DType destType = structType.getFields().get(targetFieldName);
				String srcField = viewType.getNamingMap().get(targetFieldName);
				DValue sourceVal = source.asStruct().getField(srcField);

				DValue inner = convert(sourceVal, destType, trans); 
				resultMap.put(targetFieldName, inner);
			}

			DValueImpl dval = new DValueImpl(structType, resultMap); 
			return dval;
		}
		private DValue convert(DValue sourceVal, DType destType, Transaction trans) throws Exception {
			DType dtype = sourceVal.getType();
			if (dtype.getShape().equals(destType.getShape())) {
				//not strictly true (NEString vs string) but all we care about is the value
				return sourceVal;
			} else {
				return buildValue(trans, destType, sourceVal);
			}
		}

		private DValue buildValue(Transaction trans, DType destType, DValue sourceVal) throws Exception {
			DValue dval = null;
			String ss;
			int nn;
			long nnLong;
			double dd;
			boolean bb;
			Date dt;
			Object obj = sourceVal.getObject();
			switch(destType.getShape()) {
			case STRING:
				ss = sourceVal.asString();
				dval = trans.createStringBuilder(destType).buildFromString(ss);
				break;
			case INTEGER:
			{
				if (obj instanceof Number) {
					Number num = (Number) obj;
					nn = num.intValue();
				} else {
					nn = Integer.parseInt(obj.toString());
				}
				dval = trans.createIntBuilder(destType).buildFrom(nn);
			}
			break;
			case LONG:
			{
				if (obj instanceof Number) {
					Number num = (Number) obj;
					nnLong = num.longValue();
				} else {
					nnLong = Long.parseLong(obj.toString());
				}
				dval = trans.createLongBuilder(destType).buildFrom(nnLong);
			}
			break;
			case NUMBER:
			{
				if (obj instanceof Number) {
					Number num = (Number) obj;
					dd = num.doubleValue();
				} else {
					dd = Double.parseDouble(obj.toString());
				}
				dval = trans.createNumberBuilder(destType).buildFrom(dd);
			}
			break;
			case BOOLEAN:
			{
				bb = Boolean.parseBoolean(obj.toString());
				dval = trans.createBooleanBuilder(destType).buildFrom(bb);
			}
			break;
			case DATE:
			{
				if (obj instanceof Date) {
					dt = (Date) obj;
					dval = trans.createDateBuilder(destType).buildFrom(dt);
				} else {
					dval = trans.createDateBuilder(destType).buildFromString(obj.toString());
				}
			}
			break;
			case ENUM:
			{
				dval = trans.createEnumBuilder(destType).buildFromString(obj.toString());
			}
			break;
			
//			LIST,
//			//		MAP,
//			STRUCT,
			
			default:
				addError("unsupported shape: " + destType.getShape().name());
				break;
			}
			return dval;
		}
		
		
		private void addError(String message) {
			NewErrorMessage nem = new NewErrorMessage();
			nem.setErrorType(NewErrorMessage.Type.API_ERROR);
			nem.setMessage(message);
			et.addError(nem);
		}

	}
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
