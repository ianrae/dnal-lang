package org.dnal.compiler.dnalgenerate;

import org.dnal.compiler.parser.ast.Exp;

public interface TypeVisitor {
	void visitType(Exp exp);
}