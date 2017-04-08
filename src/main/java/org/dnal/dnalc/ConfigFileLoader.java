package org.dnal.dnalc;

public interface ConfigFileLoader {
	boolean existsConfigFile(String path);
	ConfigFileOptions load(String path);
}