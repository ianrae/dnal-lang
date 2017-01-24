import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.dnal.compiler.dnalgenerate.RuleDeclaration;
import org.dnal.compiler.dnalgenerate.RuleFactory;
import org.junit.Test;
import org.reflections.Reflections;
import org.reflections.scanners.ResourcesScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;

import com.github.ianrae.dnalc.RuleFactoryFinder;
import com.google.common.reflect.ClassPath;

//https://github.com/ronmamo/reflections
public class ReflectionTests {
    
    @Test
    public void testGuava() throws Exception {
        final ClassLoader loader = Thread.currentThread().getContextClassLoader();
        
        String packageName = "com.github.ianrae.world";
        ClassPath classpath = ClassPath.from(loader); // scans the class path used by classloader
        for (ClassPath.ClassInfo classInfo : classpath.getTopLevelClasses(packageName)) {
          System.out.println("aa: " + classInfo.getName());
          Class<?> clazz = classInfo.getClass();
//          System.out.println("bb: " + clazz.getName() );
        }
    }
    
    @Test
    public void testReflections() throws Exception {
        List<ClassLoader> classLoadersList = new LinkedList<ClassLoader>();
        classLoadersList.add(ClasspathHelper.contextClassLoader());
        classLoadersList.add(ClasspathHelper.staticClassLoader());

        String packageName = "com.github.ianrae.world";
        Reflections reflections = new Reflections(new ConfigurationBuilder()
            .setScanners(new SubTypesScanner(false /* don't exclude Object.class */), new ResourcesScanner())
            .setUrls(ClasspathHelper.forClassLoader(classLoadersList.toArray(new ClassLoader[0])))
            .filterInputsBy(new FilterBuilder().include(FilterBuilder.prefix(packageName))));      
        
        Set<String> types = reflections.getAllTypes();
        for(String s : types) {
            System.out.println(s);
        }

        Set<Class<? extends RuleFactory>> subTypes = reflections.getSubTypesOf(RuleFactory.class);        
        for(Class<? extends RuleFactory> clazz : subTypes) {
            System.out.println("sub: " + clazz.getName());
            RuleFactory factory = clazz.newInstance();
            RuleDeclaration decl = factory.getDeclaration();
            System.out.println("d: " + decl.ruleName);
        }
        
    }
    
    @Test
    public void testReflections3() {
        String[] ar = new String[] { "com.github.ianrae.world", "com.github.ianrae.dnalparse.compiler" };
        RuleFactoryFinder rff = new RuleFactoryFinder();
        List<RuleFactory> list = rff.findFactories(Arrays.asList(ar));
        
        for(RuleFactory factory : list) {
            System.out.println(factory.getDeclaration().ruleName);
        }
    }
}
