package org.dnal.core.oldvalidation;

public class CompositeRule extends VRule {
	private String op;
	private VRule left;
	private VRule right;

	public CompositeRule(String op, VRule left, VRule right) {
		super("composite!!!");
		this.op = op;
		this.left = left;
		this.right = right;
	}

	public String getOp() {
		return op;
	}

	public VRule getLeft() {
		return left;
	}

	public VRule getRight() {
		return right;
	}
	
	@Override
	public String getRule() {
		return String.format("%s{%s,%s}", op, left.getRule(), right.getRule());
	}

}
