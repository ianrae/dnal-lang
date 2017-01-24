package com.github.ianrae.dnalparse.dnalgenerate;

import java.util.ArrayList;
import java.util.List;

import org.dval.BuiltInTypes;
import org.dval.DListType;
import org.dval.DStructType;
import org.dval.DType;
import org.dval.DTypeRegistry;
import org.dval.DValue;
import org.dval.Shape;
import org.dval.fluent.type.TypeBuilder;
import org.dval.fluent.type.TypeBuilder.Inner;
import org.dval.logger.Log;
import org.dval.nrule.NRule;
import org.dval.repository.MyWorld;

import com.github.ianrae.dnalparse.et.XErrorTracker;
import com.github.ianrae.dnalparse.impl.CompilerContext;
import com.github.ianrae.dnalparse.nrule.UniqueRule;
import com.github.ianrae.dnalparse.parser.DNALDocument;
import com.github.ianrae.dnalparse.parser.ast.CustomRule;
import com.github.ianrae.dnalparse.parser.ast.EnumMemberExp;
import com.github.ianrae.dnalparse.parser.ast.Exp;
import com.github.ianrae.dnalparse.parser.ast.FullAssignmentExp;
import com.github.ianrae.dnalparse.parser.ast.FullEnumTypeExp;
import com.github.ianrae.dnalparse.parser.ast.FullListTypeExp;
import com.github.ianrae.dnalparse.parser.ast.FullStructTypeExp;
import com.github.ianrae.dnalparse.parser.ast.FullTypeExp;
import com.github.ianrae.dnalparse.parser.ast.IdentExp;
import com.github.ianrae.dnalparse.parser.ast.ImportExp;
import com.github.ianrae.dnalparse.parser.ast.RuleDeclExp;
import com.github.ianrae.dnalparse.parser.ast.RuleExp;
import com.github.ianrae.dnalparse.parser.ast.StructMemberExp;
import com.github.ianrae.dnalparse.parser.error.ErrorTrackingBase;
import com.github.ianrae.dnalparse.parser.error.TypeInfo;

public class ASTToDNALGenerator extends ErrorTrackingBase implements TypeVisitor, ValueVisitor {
    protected MyWorld world;
    protected DTypeRegistry registry;
    private ASTToDNALValueGenerator valueGenerator;
    private CustomRuleFactory crf;
    private List<RuleDeclaration> ruleDeclL;
    private PackageHelper packageHelper;
    private CompilerContext context;

    public ASTToDNALGenerator(MyWorld world, DTypeRegistry registry, XErrorTracker et, 
            CustomRuleFactory crf, CompilerContext context) {
        super(et);
        this.world = world;
        this.registry = registry;
        this.crf = crf;
        this.context = context;
        this.packageHelper = new PackageHelper(registry, context.packageName);
    }

    public boolean generate(List<Exp> nodeL) {
        this.doc = new DNALDocument(nodeL);

        context.perf.startTimer("ast-to-dval:imports");
        processImports();
        context.perf.endTimer("ast-to-dval:imports");

        context.perf.startTimer("ast-to-dval:types");
        buildRuleDeclL();

        for(Exp exp: doc.getTypes()) {
            try {
                visitType(exp);
            } catch (Exception e) {
                this.addError2s("ASTtype '%s': %s", exp.strValue(), e.getMessage());
            }
        }
        context.perf.endTimer("ast-to-dval:types");

        context.perf.startTimer("ast-to-dval:values");
        String packageName = packageHelper.getPackageName();
        valueGenerator = new ASTToDNALValueGenerator(world, context, doc, registry, packageHelper);
        for(Exp exp: doc.getValues()) {
            try {
                visitValue(exp);
            } catch (Exception e) {
                e.printStackTrace();
                this.addError2s("ASTvalue '%s': %s", exp.strValue(), e.getMessage());
            }
        }
        context.perf.endTimer("ast-to-dval:values");

        return areNoErrors();
    }

    private void buildRuleDeclL() {
        ruleDeclL = new ArrayList<>();
        for(RuleDeclExp exp: doc.getRuleDeclarations()) {
            Shape shape = TypeInfo.stringToShape(exp.ruleType);
            RuleDeclaration decl = new RuleDeclaration(exp.ruleName, shape);
            this.ruleDeclL.add(decl);
        }

        crf.addRuleDelcarations(ruleDeclL);
    }

    @Override
    public void visitValue(Exp exp) {
        FullAssignmentExp typeExp = (FullAssignmentExp) exp;
        if (typeExp.isListVar()) {
            buildExplicitListType(typeExp);
        }

        DValue dval = valueGenerator.buildTopLevelValue(typeExp);
        if (dval != null) {
            String completeName = packageHelper.buildCompleteName(typeExp.var.name());
            world.addTopLevelValue(completeName, dval);
        }
    }

    private void buildExplicitListType(FullAssignmentExp typeExp) {
        //		    IdentExp elementType = typeExp.getListSubType();
        //            String typeName = elementType.name();
        //            String elType =  typeExp.getListSubType();
        IdentExp elexp =  typeExp.getListSubType();
        String elType = elexp.name();
        if (TypeInfo.isPrimitiveType(elexp)) {
            elType = TypeInfo.toShapeType(elType);
        }

        DType eltype = registry.getType(elType);
        if (eltype == null) {
            this.addError2s("let '%s': unknown list element type '%s'", typeExp.var.name(), elType);
        }

        String typeName = typeExp.type.name();
        if (registry.getType(typeName) == null) {
            DListType dtype = new DListType(Shape.LIST, typeName, null, eltype);
            registerType(typeName, dtype);
        }
    }

    private void registerType(String typeName, DType dtype) {
        packageHelper.registerType(typeName, dtype);
        world.typeRegistered(dtype);
    }

    @Override
    public void visitType(Exp exp) {
        FullTypeExp typeExp = (FullTypeExp) exp;
        String typeName = typeExp.var.name();

        Log.debugLog("type " + typeName);
        if (typeExp instanceof FullStructTypeExp) {
            buildStructType((FullStructTypeExp)exp);
        } else if (typeExp instanceof FullEnumTypeExp) {
            buildEnumType((FullEnumTypeExp)exp);
        } else if (typeExp instanceof FullListTypeExp) {
            buildListType((FullListTypeExp)exp);
        } else {
            DType baseDType;
            DType dtype = null;

            switch(TypeInfo.typeOf(typeExp.type)) {
            case INT:
                baseDType = registry.getType(BuiltInTypes.INTEGER_SHAPE);
                dtype = new DType(Shape.INTEGER, typeName, baseDType);
                registerType(typeName, dtype);
                break;
            case LONG:
                baseDType = registry.getType(BuiltInTypes.LONG_SHAPE);
                dtype = new DType(Shape.LONG, typeName, baseDType);
                registerType(typeName, dtype);
                break;
            case NUMBER:
                baseDType = registry.getType(BuiltInTypes.NUMBER_SHAPE);
                dtype = new DType(Shape.NUMBER, typeName, baseDType);
                registerType(typeName, dtype);
                break;
            case DATE:
                baseDType = registry.getType(BuiltInTypes.DATE_SHAPE);
                dtype = new DType(Shape.DATE, typeName, baseDType);
                registerType(typeName, dtype);
                break;
            case BOOLEAN:
                baseDType = registry.getType(BuiltInTypes.BOOLEAN_SHAPE);
                dtype = new DType(Shape.BOOLEAN, typeName, baseDType);
                registerType(typeName, dtype);
                break;
            case STRING:
                baseDType = registry.getType(BuiltInTypes.STRING_SHAPE);
                dtype = new DType(Shape.STRING, typeName, baseDType);
                registerType(typeName, dtype);
                break;
                //				case LIST:
                //					baseType = registry.getType(BuiltInTypes.LIST_SHAPE);
                //					type = new DType(Shape.STRING, name, baseType);
                //					registry.add(name, type);
                //					break;

            default:
                baseDType = packageHelper.findRegisteredType(typeExp.type.name());
                if (baseDType != null) {
                    dtype = new DType(baseDType.getShape(), typeName, baseDType);
                    registerType(typeName, dtype);
                } else {
                    addError2s("type '%s' - unknown %s", typeName, typeExp.type.name());
                }
                break;
            }

            if (dtype != null) {
                addValidationRules(typeExp, dtype);
            }
        }
    }

    private void addValidationRules(FullTypeExp typeExp, DType type) {
        RuleConverter converter = new RuleConverter(crf, ruleDeclL, getET());

        List<RuleExp> adjustedRuleList = adjustRules(type, typeExp.ruleList);

        for(Exp exp: adjustedRuleList) {
            NRule vrule = converter.convert(type, exp, context);
            if (vrule == null) {
                this.addError2s("type %s: unknown rule %s", typeExp.type.name(), exp.strValue());
            } else {
                this.log(vrule.getName());
                type.getRawRules().add(vrule);
            }
        }


        //unique is not a rule but evaluate it like a rule
        if (type instanceof DStructType) {
            DStructType structType = (DStructType) type;
            for(String fieldName: structType.getFields().keySet()) {
                if (structType.fieldIsUnique(fieldName)) {
                    UniqueRule rule = new UniqueRule("unique", fieldName, structType, this.context);
                    type.getRawRules().add(rule);
                }
            }
        }        

    }

    private List<RuleExp> adjustRules(DType type, List<RuleExp> ruleList) {
        RuleAdjuster adjuster = new RuleAdjuster();

        for (RuleExp exp: ruleList) {
            adjuster.adjust(type, exp);
        }

        if (ruleList.size() == 2) {
            Exp exp = ruleList.get(0);
            if (exp instanceof CustomRule) {
                List<RuleExp> L = new ArrayList<>();
                CustomRule rule = (CustomRule) exp;
                if (rule.ruleName.equals("len")) {
                    rule.hackExtra = ruleList.get(1);
                    L.add(rule);
                    return L;
                }
            }
        }
        return ruleList;
    }

    private void buildStructType(FullStructTypeExp typeExp) {
        TypeBuilder tb = new TypeBuilder(registry, world);
        tb.setPackageName(packageHelper.getPackageName());
        if (! typeExp.type.name().equals("struct")) {
            DType baseType = packageHelper.findRegisteredType(typeExp.type.name());
            if (baseType == null) {
                this.addError("unknown struct base type", typeExp);
            } else if (baseType instanceof DStructType) {
                tb.setBaseType((DStructType) baseType);
            } else {
                this.addError("struct base type '%s' is not a struct type", typeExp);
            }
        }

        Inner inner = tb.start(typeExp.var.name());

        for(StructMemberExp membExp : typeExp.members.list) {
            log(" m " + membExp.strValue());
            if (membExp.optional) {
                inner.optional();
            }
            switch(TypeInfo.typeOf(membExp.type)) {
            case INT:
                inner = inner.integer(membExp.var.strValue());
                break;
            case LONG:
                inner = inner.longInteger(membExp.var.strValue());
                break;
            case NUMBER:
                inner = inner.number(membExp.var.strValue());
                break;
            case DATE:
                inner = inner.date(membExp.var.strValue());
                break;
            case BOOLEAN:
                inner = inner.bool(membExp.var.strValue());
                break;
            case STRING:
                inner = inner.string(membExp.var.strValue());
                break;

            default:
                if (! buildKnownType(inner, membExp)) {
                    addError2s("struct type '%s' - unknown %s", typeExp.var.name(), membExp.type.name());
                }
                break;
            }
        }

        inner.end();
        DStructType structType = tb.getType();
        packageHelper.addPackage(structType);
        addValidationRules(typeExp, structType);
    }

    private boolean buildKnownType(Inner inner, StructMemberExp membExp) {
        if (membExp.isListVar()) {
            return structMemberExplicitList(inner, membExp);
        }

        if (packageHelper.findRegisteredType(membExp.type.name()) == null) {
            return false;
        }
        DType eltype = packageHelper.findRegisteredType(membExp.type.name());
        String fieldName = membExp.var.name();

        inner.other(fieldName, eltype);
        return true;
    }

    private boolean structMemberExplicitList(Inner inner, StructMemberExp membExp) {
        if (packageHelper.existsRegisteredType(membExp.type.name())) {
            return true;
        }

        IdentExp varname = new IdentExp(membExp.type.name());
        IdentExp typename = new IdentExp(membExp.type.name());
        //        IdentExp elementType = membExp.getListSubType();
        IdentExp elementType = new IdentExp(membExp.type.name());
        List<RuleExp> ruleList = new ArrayList<>();
        FullListTypeExp listExp = new FullListTypeExp(varname, typename, elementType, ruleList);
        visitType(listExp);

        DType eltype = packageHelper.findRegisteredType(typename.name());
        inner.other(membExp.var.name(), eltype);

        return true;
    }

    private void buildEnumType(FullEnumTypeExp typeExp) {
        TypeBuilder tb = new TypeBuilder(registry, world);
        tb.setPackageName(packageHelper.getPackageName());
        tb.setAmBuildingEnum(true);
        Inner inner = tb.start(typeExp.var.name());

        for(EnumMemberExp membExp : typeExp.members.list) {
            log(" m " + membExp.strValue());
            switch(TypeInfo.typeOf(membExp.type)) {
            //				case INT:
            //					inner = inner.integer(membExp.var.strValue());
            //					break;
            //				case BOOLEAN:
            //					inner = inner.bool(membExp.var.strValue());
            //					break;
            case STRING:
                inner = inner.string(membExp.var.strValue());
                break;

            default:
                addError2s("struct type '%s' - unknown %s", typeExp.var.name(), membExp.type.name());
                break;
            }
        }

        inner.end();
        DStructType structType = tb.getType();
        packageHelper.addPackage(structType);
        addValidationRules(typeExp, tb.getType());
    }

    private void buildListType(FullListTypeExp exp) {
        String typeName = exp.var.name();
        String elType = exp.getListElementType();
        IdentExp elexp = new IdentExp(elType);
        if (TypeInfo.isPrimitiveType(elexp)) {
            elType = TypeInfo.toShapeType(elType);
        }

        DType eltype = packageHelper.findRegisteredType(elType);
        if (eltype == null) {
            this.addError2s("type '%s': unknown list element type '%s'", typeName, elType);
        }

        DListType dtype = new DListType(Shape.LIST, typeName, null, eltype);
        registerType(typeName, dtype);
    }

    private void processImports() {
        //        context.errL = errL;
        context.world = world;
        context.registry = registry;
        context.crf = crf;

        for(ImportExp exp: doc.getImports()) {
            try {
                log("!!!!!!!!!!!!!!!! "+ exp.val);

                context.loader.importPackage(exp.val, context);
            } catch (Exception e) {
                this.addError2s("import '%s': %s", exp.strValue(), e.getMessage());
            }
        }

    }

    private void log(String s) {
        Log.log(s);
    }

}