package org.dnal.api.impl;

import org.dnal.core.DTypeRegistry;
import org.dnal.core.repository.World;

public class Internals {
    private DTypeRegistry registry;
    private World world;
    
    public Internals(DTypeRegistry registry, World world) {
        this.world = world;
        this.registry = registry;
    }

    public DTypeRegistry getRegistry() {
        return registry;
    }

    public World getWorld() {
        return world;
    }
    
}
