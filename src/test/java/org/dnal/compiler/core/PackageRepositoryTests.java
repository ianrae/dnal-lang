package org.dnal.compiler.core;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dnal.compiler.impoter.PackageRepository;
import org.dnal.compiler.parser.ast.ImportExp;
import org.junit.Test;

public class PackageRepositoryTests {
    
    public interface MyImportLoader {
        
        List<ImportExp> loadPackage(String pkg);
    }
    public class MockImportLoader implements MyImportLoader {
        public Map<String, List<ImportExp>> importMap = new HashMap<>();

        @Override
        public List<ImportExp> loadPackage(String pkg) {
            return importMap.get(pkg);
        }
        
    }
    
    public static class ImportProcessor {
        private PackageRepository prepo;
        private MyImportLoader loader;
        public int counter;
        
        public ImportProcessor(PackageRepository prepo, MyImportLoader loader) {
            this.prepo = prepo;
            this.loader = loader;
        }
        
        public void process(List<ImportExp> list) {
            doProcess(list, 0);
        }
        private void doProcess(List<ImportExp> list, int runawayCounter) {
            if (runawayCounter > 100) {
                System.out.println("RUNAWAY!!!");
                return;
            }
            for(ImportExp exp : list) {
                String pkg = exp.val;
                if (! pkg.isEmpty()) {
                    doImport(pkg, runawayCounter);
                }
            }
        }

        private void doImport(String pkg, int runawayCounter) {
            
            if (!prepo.exists(pkg)) {
                List<ImportExp> inner = loader.loadPackage(pkg);
                if (inner == null) {
                    System.out.println(pkg + " MISSING!");
                    return;
                }
                System.out.println(pkg);
                prepo.addPackage(pkg, "xx");
                
                doProcess(inner, runawayCounter + 1); //**recursion**
                counter++;
            }
        }
    }

    @Test
    public void test() {
        PackageRepository prepo = new PackageRepository();
        String pkg = "a.b.X";
        assertEquals(false, prepo.exists(pkg));
        prepo.addPackage(pkg, "");
        assertEquals(true, prepo.exists("a.b.X"));
    }
    
    @Test
    public void test2() {
        String[] arx = { "", ""};
        String[] arz = { "", ""};
        
        String[] ar = { "", ""};
        chkLoad(ar, 0, arx, arz);
        String[] ar2 = { "a.b" };
        chkLoad(ar2, 1, arx, arz);
        String[] ar3 = { "a.b", "a.b" };
        chkLoad(ar3, 1, arx, arz);
        String[] ar4 = { "a.b", "a.c" };
        chkLoad(ar4, 2, arx, arz);
    }

    @Test
    public void test3() {
        String[] arx = { "a.d", ""};
        String[] arz = { "a.d", ""};
        
        String[] ar = { "a.b", "a.c", "a.d" };
        chkLoad(ar, 3, arx, arz);
    }
    
    @Test
    public void test4() {
        String[] arx = { "a.c", ""};
        String[] arz = { "a.b", ""};
        
        String[] ar = { "a.b",  };
        chkLoad(ar, 2, arx, arz);
    }
    
    private void chkLoad(String[] ar, int expected, String[] arx, String[] arz) {
        PackageRepository prepo = new PackageRepository();
        MockImportLoader loader = new MockImportLoader();
        loader.importMap.put("a.b", createImpList(arx));
        loader.importMap.put("a.c", createImpList(arz));
        String[] ard = { "", ""};
        loader.importMap.put("a.d", createImpList(ard));
        
        ImportProcessor processor = new ImportProcessor(prepo, loader);
        List<ImportExp> importList = createImpList(ar);
        processor.process(importList);
        assertEquals(expected, processor.counter);
    }

    private List<ImportExp> createImpList(String[] ar) {
        List<ImportExp> importList = new ArrayList<>();
        for(String s : ar) {
            importList.add(new ImportExp(s));
        }
        return importList;
    }

}
