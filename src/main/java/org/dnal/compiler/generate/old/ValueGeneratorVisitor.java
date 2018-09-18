package org.dnal.compiler.generate.old;

import java.util.ArrayList;
import java.util.List;

import org.dnal.core.DListType;
import org.dnal.core.DMapType;
import org.dnal.core.DStructType;
import org.dnal.core.DType;
import org.dnal.core.nrule.NRule;
import org.dnal.dnalc.ConfigFileOptions;

public abstract class ValueGeneratorVisitor implements OldOutputGenerator {
    public List<String> outputL = new ArrayList<>();

	@Override
	public void setOptions(ConfigFileOptions configFileOptions) {
	}

	@Override
	public void startStructType(String name, DStructType dtype) throws Exception {
	}

	@Override
	public void startEnumType(String name, DStructType dtype) throws Exception {
	}

	@Override
	public void startType(String name, DType dtype) throws Exception {
	}

	@Override
	public void startListType(String name, DListType type) throws Exception {
	}
	@Override
	public void startMapType(String name, DMapType type) throws Exception {
	}


	@Override
	public void structMember(String name, DType type) throws Exception {
	}

	@Override
	public void rule(int index, String ruleText, NRule rule) throws Exception {
	}

	@Override
	public void enumMember(String name, DType memberType) throws Exception {
	}

	@Override
	public void endType(String name, DType type) throws Exception {
	}

	@Override
	public void finish() throws Exception {
	}
}