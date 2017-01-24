package com.github.ianrae.dnalparse.dnalgenerate;

import com.github.ianrae.dnalparse.parser.ast.Exp;

public interface TypeVisitor {
	void visitType(Exp exp);
}