package org.dnal.compiler.dnalgenerate;

import org.dnal.compiler.parser.ast.Exp;

public interface ValueVisitor {
	void visitValue(Exp exp);
}