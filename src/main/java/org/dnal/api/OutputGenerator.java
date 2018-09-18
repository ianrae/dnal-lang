package org.dnal.api;

import org.dnal.compiler.generate.TypeGeneratorEx;
import org.dnal.compiler.generate.ValueGeneratorEx;

/**
 * a pair of generators, one for types and one for values.
 * Either, or both, can be null.
 * 
 * @author ian
 *
 */
public class OutputGenerator {
	public TypeGeneratorEx typeGenerator;
	public ValueGeneratorEx valueGenerator;
}
