package org.dnal.core.world;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.dnal.api.DNALCompiler;
import org.dnal.api.BeanLoader;
import org.dnal.api.DataSet;
import org.dnal.api.Transaction;
import org.dnal.api.WorldException;
import org.dnal.api.impl.CompilerContext;
import org.dnal.api.impl.DataSetImpl;
import org.dnal.core.DType;
import org.dnal.core.DTypeRegistry;
import org.dnal.core.DValue;
import org.dnal.core.builder.IntBuilder;
import org.dnal.core.repository.World;
import org.junit.Test;

public class BeanTests extends BaseWorldTest {
    
    public interface ScalarValue {
    }
    
    public interface Foo extends ScalarValue {
        int value();
    }
    
    public static class FooValue implements Foo {
        private DValue dval;
        
        public FooValue(DValue dval) {
            this.dval = dval;
        }
        
        @Override
        public int value() {
            return dval.asInt();
        }
    }
    
    //FooBean would have getters/setters
    //needs own loader FooBeanLoader
    
    public static class FooLoader implements BeanLoader<FooValue> {
        private DTypeRegistry registry;
        private World world;
        private CompilerContext context;
        
        @Override
        public Class<?> willLoad() {
            return FooValue.class;
        }

        @Override
        public FooValue create(DValue dval) {
            FooValue bean = new FooValue(dval);
            return bean;
        }

        @Override
        public DValue createDValue(Object beanParam) {
            FooValue bean = (FooValue)beanParam;
            
            String name = beanParam.getClass().getSimpleName();
            DType type = registry.getType(name);
            DataSet dataSet = new DataSetImpl(registry, world, context);
            Transaction trans = dataSet.createTransaction();
            IntBuilder builder = trans.createIntBuilder();
            
            Integer val = new Integer(bean.value());
            DValue dval = builder.buildFrom(val);
            return dval;
        }

        @Override
        public void attach(DTypeRegistry registry, World world, CompilerContext context) {
            this.registry = registry;
            this.world = world;
            this.context = context;
        }
    }

    private static final String GENERATE_DIR = "./src/main/resources/test/generate/";
    
    @Test
    public void test() {
        DNALCompiler compiler = createCompiler();
        String path = GENERATE_DIR + "struct1.dnal";
        DataSet dataSet = compiler.compile(path, null);
        assertNotNull(dataSet);
    }
    
    @Test
    public void testStr() {
        String s = String.format("type Foo int end let x Foo = 10 let y Foo = x");
        DNALCompiler compiler = createCompiler();
        String path = GENERATE_DIR + "struct1.dnal";
        DataSet dataSet = compiler.compileString(s, null);
        assertNotNull(dataSet);
    }
    
    @Test
    public void testGet() {
        DataSet dataSet = createWorld();
        DValue dval = dataSet.getValue("no such value");
        assertNull(dval);
        dval = dataSet.getValue("x");
        assertEquals(10, dval.asInt());

        dval = dataSet.getValue("y");
        assertEquals(10, dval.asInt());
    }
    
    @Test
    public void testBean() {
        DataSet dataSet = createWorld();
        dataSet.registerBeanLoader(new FooLoader());
        
        Foo x = dataSet.getAsBean("nosuchname", FooValue.class);
        assertNull(x);
        x = dataSet.getAsBean("x", FooValue.class);
        assertNotNull(x);
        assertEquals(10, x.value());
    }
    
    @Test
    public void testLoadFromBean() {
        DataSet dataSet = createWorld();
        dataSet.registerBeanLoader(new FooLoader());
        
        Foo x = dataSet.getAsBean("nosuchname", FooValue.class);
        assertNull(x);
        x = dataSet.getAsBean("x", FooValue.class);
        assertNotNull(x);
        assertEquals(10, x.value());
        
        boolean b = false;
        int n = dataSet.size();
        Transaction trans = dataSet.createTransaction();
        
        DValue dval = null;
        try {
            dval = trans.createFromBean(x);
        } catch (WorldException e) {
            e.printStackTrace();
        }
        assertNotNull(dval);
        assertEquals(10, dval.asInt());
        
        
        trans.add("newVal", dval);
        b = trans.commit();
        
        assertEquals(true, b);
        assertEquals(0, trans.getValErrorList().size());
        assertEquals(n+1, dataSet.size());
    }

    //--
    private DataSet createWorld() {
        String s = String.format("type Foo int end let x Foo = 10 let y Foo = x");
        DNALCompiler compiler = createCompiler();
        String path = GENERATE_DIR + "struct1.dnal";
        DataSet dataSet = compiler.compileString(s, null);
        return dataSet;
    }
    
}
