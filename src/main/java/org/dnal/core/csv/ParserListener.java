package org.dnal.core.csv;

import java.util.List;

import org.dnal.core.NewErrorMessage;
import org.dnal.core.oldbuilder.XStructValueBuilder;

public interface ParserListener {
	void lineProcessed(int lineNum, XStructValueBuilder builder, List<NewErrorMessage> list);
}
