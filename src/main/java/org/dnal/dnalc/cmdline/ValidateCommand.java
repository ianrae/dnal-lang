package org.dnal.dnalc.cmdline;

import java.util.List;

public class ValidateCommand extends Command {
    public List<String> customRulePackages;
    
	 public ValidateCommand() {
		 name = "validate";
	 }
}