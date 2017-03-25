package org.dnal.api.view;

import java.util.Date;

import org.dnal.api.DataSet;
import org.dnal.api.Transaction;
import org.dnal.api.impl.DataSetImpl;
import org.dnal.compiler.et.XErrorTracker;
import org.dnal.core.DType;
import org.dnal.core.DTypeRegistry;
import org.dnal.core.DValue;
import org.dnal.core.NewErrorMessage;

public class ViewLoaderRendererBase {
		protected DataSet ds;
		protected DTypeRegistry registry;
		protected XErrorTracker et;

		public ViewLoaderRendererBase(DataSet ds) {
			this.ds = ds;
			DataSetImpl dsi = (DataSetImpl) ds;
			et = dsi.getCompilerContext().et;
			this.registry = dsi.getInternals().getRegistry();
		}
		
		protected DValue convert(DValue sourceVal, DType destType, Transaction trans) throws Exception {
			DType dtype = sourceVal.getType();
			if (dtype.getShape().equals(destType.getShape())) {
				//not strictly true (NEString vs string) but all we care about is the value
				return sourceVal;
			} else {
				return buildValue(trans, destType, sourceVal);
			}
		}

		protected DValue buildValue(Transaction trans, DType destType, DValue sourceVal) throws Exception {
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
		
		
		protected void addError(String message) {
			NewErrorMessage nem = new NewErrorMessage();
			nem.setErrorType(NewErrorMessage.Type.API_ERROR);
			nem.setMessage(message);
			et.addError(nem);
		}

	}