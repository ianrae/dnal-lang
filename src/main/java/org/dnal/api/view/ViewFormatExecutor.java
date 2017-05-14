package org.dnal.api.view;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.dnal.api.DataSet;
import org.dnal.api.Transaction;
import org.dnal.compiler.et.XErrorTracker;
import org.dnal.compiler.parser.ast.ViewFormatExp;
import org.dnal.core.DType;
import org.dnal.core.DValue;
import org.dnal.core.NewErrorMessage;
import org.dnal.core.Shape;

public class ViewFormatExecutor {

	private XErrorTracker et;
	private DataSet ds;

	public ViewFormatExecutor(XErrorTracker et, DataSet ds) {
		this.et = et;
		this.ds = ds;
	}

	public DValue execute(DValue sourceVal, DType destType, Transaction trans, DValue inner, ViewFormatExp vfe) {
		DValue result = null;
		
		switch(vfe.fnName) {
		case "format":
			result = execFormat(sourceVal, destType, trans, inner, vfe);
			break;
		default:
			addError(String.format("%s: unknown view function name: %s", destType.getName(), vfe.fnName));
		}
		
		return result;
	}
	
	private DValue execFormat(DValue sourceVal, DType destType, Transaction trans, DValue inner, ViewFormatExp vfe) {
		if (vfe.argL.isEmpty()) {
			addError(String.format("%s: format() requires one param, such as 'yyyy'", destType.getName()));
		}
		
		if (sourceVal.getType().isShape(Shape.DATE)) {
			if (destType.isShape(Shape.STRING)) {
				String arg = vfe.argL.get(0).strValue();
			    DateFormat df1 = new SimpleDateFormat(arg);
				Date dt = sourceVal.asDate();
				String s = df1.format(dt);
				return trans.createStringBuilder().buildFromString(s);
			}
		}
		
		addError(String.format("%s: format() not supported for this type: %s", destType.getName(), sourceVal.getType().getName()));
		return null;
	}

	protected void addError(String message) {
		NewErrorMessage nem = new NewErrorMessage();
		nem.setErrorType(NewErrorMessage.Type.API_ERROR);
		nem.setMessage(message);
		et.addError(nem);
	}


}
