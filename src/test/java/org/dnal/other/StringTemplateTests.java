package org.dnal.other;

import org.junit.Test;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupFile;

public class StringTemplateTests {
    private static final String CODEGEN_DIR = "./src/main/resources/test/codegen/";

    @Test
    public void test() {
        ST hello = new ST("Hello, <name>");
        hello.add("name", "World");
        System.out.println(hello.render());    
    }
    
    @Test
    public void test2() {
     // Load the file
        final STGroup stGroup = new STGroupFile(CODEGEN_DIR + "file1.stg");

        // Pick the correct template
        final ST templateExample = stGroup.getInstanceOf("templateExample");

        // Pass on values to use when rendering
        templateExample.add("param", "Hello World");

        Integer k = 99;
        Object[] ar = new Object[] { "Ter", "Parr", k };
        templateExample.addAggr("items.{ firstName ,lastName, id }", ar); // add() uses varargs
        templateExample.addAggr("items.{firstName, lastName ,id}", ar);
        
        // Render
        final String render = templateExample.render();

        // Print
        System.out.println(render);
    }

}
