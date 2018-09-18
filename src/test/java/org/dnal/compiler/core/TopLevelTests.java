package org.dnal.compiler.core;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.dnal.api.DNALCompiler;
import org.dnal.api.DataSet;
import org.dnal.api.impl.CompilerImpl;
import org.dnal.compiler.et.XErrorTracker;
import org.dnal.compiler.generate.old.SimpleFormatOutputGenerator;
import org.dnal.core.util.TextComparer;
import org.dnal.core.util.TextFileReader;
import org.dnal.core.util.TextFileWriter;
import org.junit.Test;


/*
 * optional with via or isa means a match of 0 or 1 are both valid.
 * 
 * TO DO LIST
 * DONE-tests
 * DONE-rules,
 * DONE  customrules,
 *  DON add in with multiple args
 *    add regex and other custom rules
 *    should nrule implement wrapper idea directly?
 * DONE  or
 * DONE-var assign. sh = c
 * DONE-dnal lang
 * DONE-enum
 * DONE-list
 * DONE-dnalc
 * DONE-number
 * DONE-java codegen
 * DONE date
 * DONE optional
 * DONE long
 * DONE via and isa
 * 
 * -fix range 10..20
 * -unique
 * -proxydval. then can implement update and delete
 * -newworld
 */

/* 
 * http://stackoverflow.com/questions/520328/can-you-find-all-classes-in-a-package-using-reflection
 * -use Guava or Reflections to find custom rules
 * 
 * rules -a series of AND statements
   > 0   means the value is > 0
 -rules must be type-compatible
 >,<,>=,<=,==,!=     
 == null or != null
 or and ( ) !
 in(val1,val2,val3) !in
 empty !empty minLen(100) maxLen(149) isLen(10) upToLen(150)
    or len > 0 --nicer but len needs to be a reserved word
      -we can be lazy about it. only when we eval a len rule do we check
       that the struct doesn't have a member named len
 regex contains startsWith endsWith
 custom ... (see below)

 -the above are all single value rules
 -a rule can also specify multiple values (in a struct)
    x > 0 and y < 10
*/

public class TopLevelTests {

	public static class MyVisitor extends SimpleFormatOutputGenerator {
	    private String lf = System.getProperty("line.separator");

		public boolean compare(String input) {
			TextComparer cmp = new TextComparer();
			String flat = flatten(outputL);
			return cmp.compareText(input.trim(), flat.trim());  //remove any blank lines at end
		}
		
		public boolean generateFile(String outPath) {
			TextFileWriter writer = new TextFileWriter();
			return writer.writeFile(outPath, outputL);
		}
		
		private String flatten(List<String> L) {
			StringBuffer sb = new StringBuffer();
			for(String s: L) {
				sb.append(s);
				sb.append(lf);
			}
			return sb.toString();
		}
	}

	private static final String GENERATE_DIR = "./src/main/resources/test/generate/";
	
	@Test
	public void test1() {
		List<String> list = getFiles();
		for(String name : list) {
			doFile(name);
		}
		log(String.format("%d files done.", list.size()));
	}
	
	@Test
	public void test2() {
		XErrorTracker.logErrors = true;
//		doFile("via3.dnal");
		doFile("map1.dnal");
//		doFile("list3.dnal");
	}
	
	private List<String> getFiles() {
		File dir = new File(GENERATE_DIR);
		List<String> list = Arrays.asList(dir.list());
		List<String> list2 = new ArrayList<>();
		for(String name : list) {
			if (name.endsWith(".dnal")) {
				list2.add(name);
			}
		}
				
		return list2;
	}
	
	private void doFile(String dnalFile) {
		log(String.format("--%s--", dnalFile));
        DNALCompiler compiler = new CompilerImpl();
//        compiler.getCompilerOptions().useMockImportLoader(true); //!!
		
		MyVisitor visitor = new MyVisitor();
//		log(getCurrentDir());
		String dir = GENERATE_DIR;
		String path = dir + dnalFile;
//		log(path);
		DataSet dataSet = compiler.compile(path, visitor);
		boolean b = (dataSet != null);
		assertEquals(true, b);
		
		TextFileReader reader = new TextFileReader();
		String refText = reader.readFileAsSingleString(dir + FilenameUtils.getBaseName(path) + ".txt");
		
		b = visitor.compare(refText);
		if (! b) {
			String outPath = String.format("%sjnk33-%s.txt", dir, FilenameUtils.getBaseName(path));
			log("writing " + outPath);
			boolean b2 = visitor.generateFile(outPath);
			assertEquals(true, b2);
		}
		assertEquals(true, b);
	}

	private void log(String s) {
		System.out.println(s);
	}
	
    private static String getCurrentDir() {
        final File file = new File(".");
        String path = file.getAbsolutePath();
        if (path.endsWith(".")) {
            path = path.substring(0, path.length() - 1); // strip off .
        }
        if (path.endsWith(File.separator)) {
            path = path.substring(0, path.length() - 1); // strip off / or \
        }
        return path;
    }
}
