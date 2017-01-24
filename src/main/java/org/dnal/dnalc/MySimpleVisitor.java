package org.dnal.dnalc;

import org.dnal.compiler.generate.SimpleMinimumFormatVisitor;
import org.dnal.core.logger.Log;


public class MySimpleVisitor extends SimpleMinimumFormatVisitor {

    @Override
	public void finish() {
		Log.log(" ");
		Log.log("output: ");
		for(String line: this.outputL) {
			Log.log(" " + line);
		}
	}

}
