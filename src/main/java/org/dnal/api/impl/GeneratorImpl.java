package org.dnal.api.impl;

import java.util.ArrayList;
import java.util.List;

import org.dnal.api.Generator;
import org.dnal.compiler.generate.DNALGeneratePhase;
import org.dnal.compiler.generate.OutputGenerator;
import org.dnal.compiler.parser.error.LineLocator;
import org.dnal.core.DTypeRegistry;
import org.dnal.core.NewErrorMessage;
import org.dnal.core.repository.World;

public class GeneratorImpl implements Generator {
    private List<NewErrorMessage> errL = new ArrayList<>();
    protected World world;
    protected DTypeRegistry registry;
    protected CompilerContext context;
    private LineLocator lineLocator;
    
//    public Generator(DTypeRegistry registry, MyWorld world) {
//        this(registry, world, null);
//    }
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
    public boolean generate(OutputGenerator visitor) {
        DNALGeneratePhase phase = new DNALGeneratePhase(context.et, registry, world, lineLocator);
        boolean b = phase.generate(visitor);
        if (! b) {
            return false;
        }
        return true;
    }

    @Override
    public List<NewErrorMessage> getErrors() {
        return errL;
    }

}