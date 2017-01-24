package com.github.ianrae.dnalparse.impoter;

import org.dval.ErrorMessage;
import org.dval.ErrorType;

import com.github.ianrae.dnalparse.impl.CompilerContext;
import com.github.ianrae.dnalparse.impl.SourceCompiler;

public class DefaultImportLoader implements ImportLoader {

    @Override
    public void importPackage(String pkg, CompilerContext context) {
        context.runawayCounter++;
        if (context.runawayCounter > 100) {
            ErrorMessage err = new ErrorMessage(ErrorType.PARSINGERROR, "Recursive imports detected!. Halting");
            context.errL.add(err);
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
