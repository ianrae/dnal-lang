package org.dnal.api.impl;

import java.util.ArrayList;
import java.util.List;

import org.dnal.api.GeneratorEx;
import org.dnal.compiler.parser.error.LineLocator;
import org.dnal.core.DTypeRegistry;
import org.dnal.core.NewErrorMessage;
import org.dnal.core.repository.World;
import org.dnal.outputex.DNALGeneratePhaseEx;
import org.dnal.outputex.OutputGeneratorEx;
import org.dnal.outputex.OutputOptions;

public class GeneratorImplEx implements GeneratorEx {
    private List<NewErrorMessage> errL = new ArrayList<>();
    protected World world;
    protected DTypeRegistry registry;
    protected CompilerContext context;
    private LineLocator lineLocator;
    
    public GeneratorImplEx(DTypeRegistry registry, World world, CompilerContext context, LineLocator lineLocator) {
        this.registry = registry;
        this.world = world;
        if (context.errL != null) {
            this.errL = context.errL;
        }
        this.context = context;
        this.lineLocator = lineLocator;
    }
    
    @Override
    public boolean generate(OutputGeneratorEx visitor) {
        DNALGeneratePhaseEx phase = new DNALGeneratePhaseEx(context.et, registry, world, lineLocator);
        boolean b = phase.generate(visitor, OutputOptions.ALL);
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