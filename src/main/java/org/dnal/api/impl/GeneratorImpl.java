package org.dnal.api.impl;

import java.util.ArrayList;
import java.util.List;

import org.dnal.api.Generator;
import org.dnal.compiler.generate.DNALGeneratePhase;
import org.dnal.compiler.generate.TypeGenerator;
import org.dnal.compiler.generate.ValueGenerator;
import org.dnal.compiler.parser.error.LineLocator;
import org.dnal.core.DTypeRegistry;
import org.dnal.core.DValue;
import org.dnal.core.NewErrorMessage;
import org.dnal.core.repository.World;

public class GeneratorImpl implements Generator {
    private List<NewErrorMessage> errL = new ArrayList<>();
    protected World world;
    protected DTypeRegistry registry;
    protected CompilerContext context;
    private LineLocator lineLocator;
    
    public GeneratorImpl(DTypeRegistry registry, World world, CompilerContext context, LineLocator lineLocator) {
        this.registry = registry;
        this.world = world;
        if (context.errL != null) {
            this.errL = context.errL;
        }
        this.context = context;
        this.lineLocator = lineLocator;
    }
    
    @Override
    public List<NewErrorMessage> getErrors() {
        return errL;
    }

	@Override
	public boolean generateTypes(TypeGenerator visitor) {
        DNALGeneratePhase phase = new DNALGeneratePhase(context.et, registry, world, lineLocator);
        boolean b = phase.generateTypes(visitor);
        if (! b) {
            return false;
        }
        return true;
	}

	@Override
	public boolean generateValues(ValueGenerator visitor) {
        DNALGeneratePhase phase = new DNALGeneratePhase(context.et, registry, world, lineLocator);
        boolean b = phase.generateValues(visitor);
        if (! b) {
            return false;
        }
        return true;
	}

	@Override
	public boolean generateValue(ValueGenerator visitor, DValue dval, String valueName) {
        DNALGeneratePhase phase = new DNALGeneratePhase(context.et, registry, world, lineLocator);
        boolean b = phase.generateValue(visitor, dval, valueName);
        if (! b) {
            return false;
        }
        return true;
	}
	
}