package com.github.ianrae.dnalc;

import static org.junit.Assert.*;

import org.dnal.dnalc.ConfigFileOptions;
import org.dnal.dnalc.cmdline.CmdLineArgParser;
import org.dnal.dnalc.cmdline.Command;
import org.dnal.dnalc.cmdline.GenerateCommand;
import org.junit.Test;

public class CmdLineArgParserTests {
	
	@Test
	public void testNone() {
		CmdLineArgParser parser = createParser();
		String[] args = new String[] { };
		Command cmd = parser.parse(args);
		assertNull(cmd);
	}

	@Test
	public void testFileOnly() {
		useOptions = false;
		String[] args = new String[] { "abc.dnal" };
		GenerateCommand cmd = (GenerateCommand) chkParse(args, "generate");
		assertEquals("none", cmd.outputType);
	}

	@Test
	public void test() {
		String[] args = new String[] { "--version"  };
		Command cmd = chkParse(args, "version");
	}
	
	@Test
	public void testConfig() {
		String[] args = new String[] { "v", "--config=z.dnal", "abc.dnal" };
		Command cmd = chkParse(args, "validate");
		assertEquals("z.dnal", cmd.configPath);
		assertEquals("abc.dnal", cmd.srcPath);
	}
	
	@Test
	public void testGen() {
		String[] args = new String[] { "generate", "--output-path=mytypes", "--output=java/dnal", "abc.dnal" };
		GenerateCommand cmd = (GenerateCommand) chkParse(args, "generate");
		assertEquals(null, cmd.configPath);
		assertEquals("abc.dnal", cmd.srcPath);
		assertEquals("mytypes", cmd.outputDir);
		assertEquals("java/dnal", cmd.outputType);
	}
	
	@Test
	public void testGen2() {
		String[] args = new String[] { "g", "-d", "-o=mytypes", "-t=java/dnal", "abc.dnal" };
		GenerateCommand cmd = (GenerateCommand) chkParse(args, "generate");
		assertEquals(null, cmd.configPath);
		assertEquals("abc.dnal", cmd.srcPath);
		assertEquals("mytypes", cmd.outputDir);
		assertEquals("java/dnal", cmd.outputType);
		assertEquals(true, cmd.debug);
	}
	
	@Test
	public void testUseConfig() {
		String[] args = new String[] { "g", "-c=config.dnal", "abc.dnal" };
		GenerateCommand cmd = (GenerateCommand) chkParse(args, "generate");
		assertEquals("config.dnal", cmd.configPath);
		assertEquals("abc.dnal", cmd.srcPath);
		assertEquals("mytypes", cmd.outputDir);
		assertEquals("java/dnal", cmd.outputType);
		assertEquals(false, cmd.debug);
	}
	
	//--
	private boolean useOptions = true;
	private CmdLineArgParser createParser() {
		MockConfigLoader loader = new MockConfigLoader();
		loader.options = new ConfigFileOptions();
		loader.options.outputPath = "mytypes";
		loader.options.outputType = "java/dnal";
		
		if (!useOptions) {
			loader.options = null;
		}
		
		CmdLineArgParser parser = new CmdLineArgParser(loader);
		return parser;
	}
	
	private Command chkParse(String[] args, String expected) {
		CmdLineArgParser parser = createParser();
		Command cmd = parser.parse(args);
		assertEquals(0, parser.getErrorCount());
		assertEquals(expected, cmd.name);
		return cmd;
	}
}
