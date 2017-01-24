package org.jparsec;

import java.util.List;


public class DNType extends Function {

	public DNType(Identifier name) {
		super(name);
	}

	  public DNType(Identifier name, Expr argument, Expr... moreArguments) {
		  super(name, argument, moreArguments);
	  }

	  public DNType(Identifier name, List<Expr> arguments) {
		  super(name, arguments);
	  }

}
