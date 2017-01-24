package com.github.ianrae.dnalc;

import org.dnal.dnalc.ConfigFileLoader;
import org.dnal.dnalc.ConfigFileOptions;

class MockConfigLoader implements ConfigFileLoader {
	public ConfigFileOptions options;
	@Override
	public ConfigFileOptions load(String path) {
		return options;
	}
	
}