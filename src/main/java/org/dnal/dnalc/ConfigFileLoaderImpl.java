package org.dnal.dnalc;

import java.io.File;
import java.util.Properties;

public class ConfigFileLoaderImpl implements ConfigFileLoader {
	private Properties props;
	
	@Override
	public ConfigFileOptions load(String path) {
		
		PropertyFileReader reader = new PropertyFileReader(path);
		props = reader.read();
		ConfigFileOptions options = new ConfigFileOptions();
		
		options.customRulePackages = getString("dnalc.custom.rule.packages", null);
		options.javaPackage = getString("dnalc.output.java.package", null);
		options.outputPath = getString("dnal.output.path", ".");
		options.outputType = getString("dnal.output.type", null);
		return options;
	}

	private String getString(String key, String defaultVal) {
		String value = props.getProperty(key);
		return (value == null) ? defaultVal : value;
	}

	@Override
	public boolean existsConfigFile(String path) {
		if (path == null) {
			return false;
		}
		File f = new File(path);
		if (f.exists()) {
			return true;
		}
		return false;
	}


}
