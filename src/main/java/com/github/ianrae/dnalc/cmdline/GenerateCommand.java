package com.github.ianrae.dnalc.cmdline;

import java.util.List;

public class GenerateCommand extends Command {
	public String outputDir;
	public String outputType;
	public String javaPackage;
    public List<String> customRulePackages;
	
	 public GenerateCommand() {
		 name = "generate";
	 }
}