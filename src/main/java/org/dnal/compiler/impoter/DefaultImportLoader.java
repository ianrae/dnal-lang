package org.dnal.compiler.impoter;

import org.dnal.api.impl.CompilerContext;
import org.dnal.api.impl.SourceCompiler;
import org.dnal.core.ErrorType;

public class DefaultImportLoader implements ImportLoader {

    @Override
    public void importPackage(String pkg, CompilerContext context) {
        context.runawayCounter++;
        if (context.runawayCounter > 100) {
        	context.addOldErrorMsg(ErrorType.PARSINGERROR, "Recursive imports detected!. Halting");
            return;
        }
        
        String tmp = pkg.replace('.', '/');
        String path = String.format("%s/%s.dnal", context.sourceDir, tmp);
        SourceCompiler compiler = new SourceCompiler(context.world, context.registry, context.crf, context.et, context);
        
        String prevPkg = context.packageName;
        compiler.compile(path);
        context.packageName = prevPkg; //reset
    }

}
