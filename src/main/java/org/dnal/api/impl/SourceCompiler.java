package org.dnal.api.impl;

import java.io.File;
import java.util.List;

import org.codehaus.jparsec.error.ParserException;
import org.dnal.api.DataSet;
import org.dnal.api.Generator;
import org.dnal.compiler.dnalgenerate.ASTToDNALGenerator;
import org.dnal.compiler.dnalgenerate.CustomRuleFactory;
import org.dnal.compiler.et.XErrorTracker;
import org.dnal.compiler.generate.GenerateVisitor;
import org.dnal.compiler.parser.DNALDocument;
import org.dnal.compiler.parser.FullParser;
import org.dnal.compiler.parser.ast.Exp;
import org.dnal.compiler.parser.ast.PackageExp;
import org.dnal.compiler.parser.error.ErrorScope;
import org.dnal.compiler.parser.error.ErrorTrackingBase;
import org.dnal.compiler.parser.error.ParseErrorChecker;
import org.dnal.compiler.validate.ValidationPhase;
import org.dnal.core.DTypeRegistry;
import org.dnal.core.ErrorMessage;
import org.dnal.core.NewErrorMessage;
import org.dnal.core.logger.Log;
import org.dnal.core.repository.World;
import org.dnal.core.util.TextFileReader;

public class SourceCompiler extends ErrorTrackingBase {
    protected World world;
    protected DTypeRegistry registry;
    private CustomRuleFactory crf;
    private CompilerContext context;
    
    public SourceCompiler(World world, DTypeRegistry registry, CustomRuleFactory crf, 
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
    		NewErrorMessage error = new NewErrorMessage();
            error.setMessage("can't find file: " + path);
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
    		NewErrorMessage err = new NewErrorMessage();
    		err.setLineNum(lineNum);
    		err.setMessage(e.getMessage());
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

    public List<NewErrorMessage> getErrors() {
        return getET().getErrL();
    }

}