package com.github.ianrae.dnalc;

class MockConfigLoader implements ConfigFileLoader {
	public ConfigFileOptions options;
	@Override
	public ConfigFileOptions load(String path) {
		return options;
	}
	
}