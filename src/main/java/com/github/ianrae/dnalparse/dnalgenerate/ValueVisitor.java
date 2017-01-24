package com.github.ianrae.dnalparse.dnalgenerate;

import com.github.ianrae.dnalparse.parser.ast.Exp;

public interface ValueVisitor {
	void visitValue(Exp exp);
}