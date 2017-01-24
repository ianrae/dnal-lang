package com.github.ianrae.dnalc;

import org.dnal.core.logger.Log;

import com.github.ianrae.dnalparse.generate.SimpleMinimumFormatVisitor;


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
