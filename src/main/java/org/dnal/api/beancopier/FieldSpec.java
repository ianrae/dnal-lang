package org.dnal.api.beancopier;

public class FieldSpec {
	public FieldSpec(String srcField, String destField) {
		super();
		this.srcField = srcField;
		this.destField = destField;
	}
	public String srcField;
	public String destField;
	public String formatOptions;
}