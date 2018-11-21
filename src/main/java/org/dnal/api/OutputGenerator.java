package org.dnal.api;

import org.dnal.compiler.generate.TypeGenerator;
import org.dnal.compiler.generate.ValueGenerator;

/**
 * a pair of generators, one for types and one for values.
 * Either, or both, can be null.
 * 
 * @author ian
 *
 */
public class OutputGenerator {
	public TypeGenerator typeGenerator;
	public ValueGenerator valueGenerator;
}
