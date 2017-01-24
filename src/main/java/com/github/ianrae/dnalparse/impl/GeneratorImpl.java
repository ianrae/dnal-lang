package com.github.ianrae.dnalparse.impl;

import java.util.ArrayList;
import java.util.List;

import org.dnal.core.DTypeRegistry;
import org.dnal.core.ErrorMessage;
import org.dnal.core.repository.MyWorld;

import com.github.ianrae.dnalparse.Generator;
import com.github.ianrae.dnalparse.generate.DNALGeneratePhase;
import com.github.ianrae.dnalparse.generate.GenerateVisitor;

public class GeneratorImpl implements Generator {
    private List<ErrorMessage> errL = new ArrayList<>();
    protected MyWorld world;
    protected DTypeRegistry registry;
    protected CompilerContext context;
    
//    public Generator(DTypeRegistry registry, MyWorld world) {
//        this(registry, world, null);
//    }
    public GeneratorImpl(DTypeRegistry registry, MyWorld world, CompilerContext context) {
        this.registry = registry;
        this.world = world;
        if (context.errL != null) {
            this.errL = context.errL;
        }
        this.context = context;
    }
    
    public boolean generate(GenerateVisitor visitor) {
        DNALGeneratePhase phase = new DNALGeneratePhase(context.et, registry, world);
        boolean b = phase.generate(visitor);
        if (! b) {
            return false;
        }
        return true;
    }

    public List<ErrorMessage> getErrors() {
        return errL;
    }

}