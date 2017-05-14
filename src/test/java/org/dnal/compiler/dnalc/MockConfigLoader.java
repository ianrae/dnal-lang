package org.dnal.compiler.dnalc;

import org.dnal.dnalc.ConfigFileLoader;
import org.dnal.dnalc.ConfigFileOptions;

class MockConfigLoader implements ConfigFileLoader {
	public ConfigFileOptions options;
	@Override
	public ConfigFileOptions load(String path) {
		return options;
	}
	@Override
	public boolean existsConfigFile(String path) {
		return true;
	}
	
}