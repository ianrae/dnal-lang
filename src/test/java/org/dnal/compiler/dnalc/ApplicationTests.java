package org.dnal.compiler.dnalc;

import static org.junit.Assert.*;

import org.dnal.dnalc.Application;
import org.dnal.dnalc.ConfigFileLoaderImpl;
import org.junit.Test;

public class ApplicationTests {

	@Test
	public void test() {
		Application app = new Application(new ConfigFileLoaderImpl());
		
		String path = String.format("-c=%s", "src/main/resources/test/config/dnalc.properties");
		String[] args = { "g",  "--debug", "-t=text/simple", path, "src/main/resources/test/generate/int1.dnal"};
		app.run(args);
	}

}
