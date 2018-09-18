package org.dnal.api.systest;

import static org.junit.Assert.*;

import java.util.List;

import org.dnal.api.DataSet;
import org.dnal.api.impl.DataSetImpl;
import org.dnal.compiler.generate.old.OldDNALGeneratePhase;
import org.dnal.compiler.generate.DNALGeneratePhaseEx;
import org.dnal.compiler.generate.DNALValueGeneratorEx;
import org.dnal.compiler.generate.JSONValueGeneratorEx;
import org.dnal.compiler.generate.ValueGeneratorEx;
import org.dnal.compiler.generate.old.DNALValueVisitor;
import org.dnal.compiler.generate.old.JSONValueVisitor;
import org.dnal.compiler.generate.old.ValueGeneratorVisitor;
import org.dnal.core.DTypeRegistry;
import org.dnal.core.DValue;
import org.dnal.core.repository.World;
import org.junit.Test;

public class ValueGeneratorTests extends SysTestBase {
	
	public static class ValueRenderer {
		
		public String render(DataSet ds, String varName, ValueGeneratorEx visitor) {
			DataSetImpl dsimpl = (DataSetImpl) ds;
			World world = dsimpl.getInternals().getWorld();
	        DTypeRegistry registry = dsimpl.getCompilerContext().registry;
			DNALGeneratePhaseEx phase = new DNALGeneratePhaseEx(dsimpl.getCompilerContext().et, registry, world, null);
			DValue dval = ds.getValue(varName);
			boolean b = phase.generateValue(visitor, dval, varName);
			assertEquals(true, b);
			String output = null;
			if (visitor instanceof DNALValueGeneratorEx) {
				DNALValueGeneratorEx dnalVisitor = (DNALValueGeneratorEx) visitor;
				output = flatten(dnalVisitor.outputL);
			} else if (visitor instanceof JSONValueGeneratorEx) {
				JSONValueGeneratorEx dnalVisitor = (JSONValueGeneratorEx) visitor;
				output = flatten(dnalVisitor.outputL);
			}
			return output;
		}
		
		private String flatten(List<String> L) {
			StringBuffer sb = new StringBuffer();
			for(String s: L) {
				sb.append(s);
			}
			return sb.toString();
		}
	}

    @Test
    public void test() {
    	DataSet ds = load("type Foo int end let x Foo = 14", true);
    	DValue dval = ds.getValue("x");
    	assertEquals(14, dval.asInt());
    	ValueRenderer renderer = new ValueRenderer();
		DNALValueGeneratorEx visitor = new DNALValueGeneratorEx();
    	String s = renderer.render(ds, "x", visitor);
    	assertEquals("let x Foo = 14", s);
    }
    
    @Test
    public void testJSON() {
    	DataSet ds = load("type Foo int end let x Foo = 14", true);
    	DValue dval = ds.getValue("x");
    	assertEquals(14, dval.asInt());
    	ValueRenderer renderer = new ValueRenderer();
		JSONValueGeneratorEx visitor = new JSONValueGeneratorEx();
    	String s = renderer.render(ds, "x", visitor);
    	log(s);
    	assertEquals("{\"x\": 14}", s);
    }
    
}