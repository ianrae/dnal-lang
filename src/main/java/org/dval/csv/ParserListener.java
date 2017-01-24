package org.dval.csv;

import java.util.List;

import org.dval.ErrorMessage;
import org.dval.oldbuilder.XStructValueBuilder;

public interface ParserListener {
	void lineProcessed(int lineNum, XStructValueBuilder builder, List<ErrorMessage> list);
}
