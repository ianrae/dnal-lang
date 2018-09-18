package org.dnal.dnalc;

public class ConfigFileOptions {
	public String outputPath;
	public String outputType;
	public String javaPackage;
	public String customRulePackages; //comma separated list
	public boolean writeOutputFilesEnabled = true; //for unit tests
	//
	//				var javaOptions JavaOptions = {
	//				  package = 'a.c.b'
	//				  beanOptions = {
	//				    generateInterface = true
	//				    generateImmutableBean = true
	//				    generateBean = true
	//				    generateLoader = true
	//				  }
	//				}
}