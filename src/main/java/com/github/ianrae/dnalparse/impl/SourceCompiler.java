package com.github.ianrae.dnalparse.impl;

import java.io.File;
import java.util.List;

import org.codehaus.jparsec.error.ParserException;
import org.dval.DTypeRegistry;
import org.dval.ErrorMessage;
import org.dval.logger.Log;
import org.dval.repository.MyWorld;

import com.github.ianrae.dnalparse.DataSet;
import com.github.ianrae.dnalparse.Generator;
import com.github.ianrae.dnalparse.dnalgenerate.ASTToDNALGenerator;
import com.github.ianrae.dnalparse.dnalgenerate.CustomRuleFactory;
import com.github.ianrae.dnalparse.et.XErrorTracker;
import com.github.ianrae.dnalparse.generate.GenerateVisitor;
import com.github.ianrae.dnalparse.parser.DNALDocument;
import com.github.ianrae.dnalparse.parser.FullParser;
import com.github.ianrae.dnalparse.parser.ast.Exp;
import com.github.ianrae.dnalparse.parser.ast.PackageExp;
import com.github.ianrae.dnalparse.parser.error.ErrorScope;
import com.github.ianrae.dnalparse.parser.error.ErrorTrackingBase;
import com.github.ianrae.dnalparse.parser.error.ParseErrorChecker;
import com.github.ianrae.dnalparse.utils.TextFileReader;
import com.github.ianrae.dnalparse.validate.ValidationPhase;

public class SourceCompiler extends ErrorTrackingBase {
    protected MyWorld world;
    protected DTypeRegistry registry;
    private CustomRuleFactory crf;
    private CompilerContext context;
    
    public SourceCompiler(MyWorld world, DTypeRegistry registry, CustomRuleFactory crf, 
            XErrorTracker et, CompilerContext context) {
        super(et);
        this.crf = crf;
        this.world = world;
        this.registry = registry;
        this.context = context;
    }
    
    public Internals getInternals() {
        return new Internals(registry, world);
    }
    
    public DataSet compile(String path) {
        return compile(path, null);
    }
    public DataSet compile(String path, GenerateVisitor visitor) {
        if (! fileExists(path)) {
            return null;
        }

        this.pushScope(new ErrorScope(path, "", ""));
        boolean b = loadAndParse(path);
        if (! b) {
            return null;
        }
        
        return doCompile(visitor);
    }
    
    private DataSet doCompile(GenerateVisitor visitor) {
        //now validate the DVALs
        boolean b = validatePhase();
        
        if (b && visitor != null) {
            b = generator(visitor);
        }
        
        popScope();
        return (b) ? new DataSetImpl(registry, world, context) : null;
    }
    public DataSet compileString(String input) {
        return compileString(input, null);
    }
    public DataSet compileString(String input, GenerateVisitor visitor) {
        this.pushScope(new ErrorScope("string-source", "", ""));
        boolean b = parseIntoDVals(input);
        if (! b) {
            return null;
        }
        
        return doCompile(visitor);
    }
    
    private boolean loadAndParse(String path) {
        if (! fileExists(path)) {
            return false;
        }

        context.perf.startTimer("io");
        TextFileReader reader = new TextFileReader();
        String input = reader.readFileAsSingleString(path);
        context.perf.endTimer("io");

        boolean b = parseIntoDVals(input);
        return b;
    }

    private boolean fileExists(String path) {
        File f = new File(path);
        if (! f.exists()) {
            ErrorMessage error = new ErrorMessage(0, "can't find file: " + path);
            this.addErrorObj(error);
        }
        return f.exists();
    }
    private boolean parseIntoDVals(String input) {
        parseAndGenDVals(input);
        if (areSomeErrors()) {
            return false;
        }
        return true;
    }

    private ASTToDNALGenerator parseAndGenDVals(String input) {
        Log.debugLog("parsing: " + input);
        
        context.perf.startTimer("jparsec");
        DNALDocument doc = null;     
        boolean ok = false;
        try {
            List<Exp> list = FullParser.fullParse(input);
            doc = new DNALDocument(list);
            ok = true;
        } catch (ParserException e) {
            //e.printStackTrace();
            int lineNum = e.getLocation().line;
            ErrorMessage err = new ErrorMessage(lineNum, e.getMessage());
            addErrorObj(err);
        }
        context.perf.endTimer("jparsec");
        
        if (! ok) {
            return null;
        }

        if (! pass2(doc.getStatementList())) {
            return null;
        }

        context.perf.startTimer("ast-to-dnal");
        PackageExp pkgExp = doc.getPackage();
        context.packageName = (pkgExp == null) ? null : pkgExp.val;
        ASTToDNALGenerator dnalGenerator = new ASTToDNALGenerator(world, registry, getET(), 
                this.crf, context);
        boolean b = dnalGenerator.generate(doc.getStatementList());
        context.perf.endTimer("ast-to-dnal");
        return (b) ? dnalGenerator : null;
    }

    private boolean pass2(List<Exp> list) {
        context.perf.startTimer("check");
        ParseErrorChecker errorChecker = new ParseErrorChecker(list, getET());
        boolean b = errorChecker.checkForErrors();
        context.perf.endTimer("check");
        if (! b) {
//            for(ErrorMessage perr : errorChecker.getErrors()) {
//                int lineNum = (perr.getLineNum() > 0) ? perr.getLineNum() : -1;
//                String s = String.format("line %d: %s", lineNum, perr.getMessage());
//                //              log(s);
//                this.errL.add(perr);
//            }
        }
        return b;
    }

    private boolean validatePhase() {
        context.perf.startTimer("validate");
        ValidationPhase validator = new ValidationPhase(this.world, context.et);
        boolean b = validator.validate();
        context.perf.endTimer("validate");
        if (! b) {
            validator.dumpErrors();
        }
        return b;
    }
    
    private boolean generator(GenerateVisitor visitor) {
        Generator generator = new GeneratorImpl(registry, world, context);
        context.perf.startTimer("generate");
        boolean b = generator.generate(visitor);
        context.perf.endTimer("generate");
        return b;
    }

    public List<ErrorMessage> getErrors() {
        return getET().getErrL();
    }

}