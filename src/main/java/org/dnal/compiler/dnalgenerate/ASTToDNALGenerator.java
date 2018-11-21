package org.dnal.compiler.dnalgenerate;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.dnal.api.impl.AddObserver;
import org.dnal.api.impl.CompilerContext;
import org.dnal.compiler.et.XErrorTracker;
import org.dnal.compiler.nrule.UniqueRule;
import org.dnal.compiler.parser.DNALDocument;
import org.dnal.compiler.parser.ast.CustomRule;
import org.dnal.compiler.parser.ast.EnumMemberExp;
import org.dnal.compiler.parser.ast.Exp;
import org.dnal.compiler.parser.ast.FullAssignmentExp;
import org.dnal.compiler.parser.ast.FullEnumTypeExp;
import org.dnal.compiler.parser.ast.FullListTypeExp;
import org.dnal.compiler.parser.ast.FullMapTypeExp;
import org.dnal.compiler.parser.ast.FullStructTypeExp;
import org.dnal.compiler.parser.ast.FullTypeExp;
import org.dnal.compiler.parser.ast.IdentExp;
import org.dnal.compiler.parser.ast.ImportExp;
import org.dnal.compiler.parser.ast.RuleDeclExp;
import org.dnal.compiler.parser.ast.RuleExp;
import org.dnal.compiler.parser.ast.StructMemberExp;
import org.dnal.compiler.parser.error.ErrorTrackingBase;
import org.dnal.compiler.parser.error.LineLocator;
import org.dnal.compiler.parser.error.TypeInfo;
import org.dnal.core.BuiltInTypes;
import org.dnal.core.DListType;
import org.dnal.core.DMapType;
import org.dnal.core.DStructType;
import org.dnal.core.DType;
import org.dnal.core.DTypeRegistry;
import org.dnal.core.DValue;
import org.dnal.core.Shape;
import org.dnal.core.fluent.type.TypeBuilder;
import org.dnal.core.fluent.type.TypeBuilder.Inner;
import org.dnal.core.logger.Log;
import org.dnal.core.nrule.NRule;
import org.dnal.core.repository.World;

public class ASTToDNALGenerator extends ErrorTrackingBase implements TypeVisitor, ValueVisitor {
	protected World world;
	protected DTypeRegistry registry;
	private ASTToDNALValueGenerator valueGenerator;
	private CustomRuleFactory crf;
	private List<RuleDeclaration> ruleDeclL;
	private PackageHelper packageHelper;
	private CompilerContext context;
	private int nextInnerSuffix = 1000;

	public ASTToDNALGenerator(World world, DTypeRegistry registry, XErrorTracker et, 
			CustomRuleFactory crf, CompilerContext context, LineLocator locator) {
		super(et, locator);
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
				e.printStackTrace();
				this.addError2s("ASTtype '%s': %s", exp.strValue(), e.getMessage());
			}
		}
		context.perf.endTimer("ast-to-dval:types");
//		registry.dump();

		context.perf.startTimer("ast-to-dval:values");
		String packageName = packageHelper.getPackageName();
		valueGenerator = new ASTToDNALValueGenerator(world, context, doc, registry, packageHelper, this.getLineLocator());
		for(Exp exp: doc.getValues()) {
			try {
				visitValue(exp);
			} catch (Exception e) {
				e.printStackTrace();
				this.addError2s(exp, "ASTvalue '%s': %s", exp.strValue(), e.getMessage());
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
			
	    	//add all sub-vals (needed for vias and unique)
	    	AddObserver observer = new AddObserver(world);
	    	observer.observe(dval);
			
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
			this.addError2s(typeExp, "let '%s': unknown list element type '%s'", typeExp.var.name(), elType);
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

		Log.debugLog("type %s", typeName);
		if (typeExp instanceof FullStructTypeExp) {
			buildStructType((FullStructTypeExp)exp);
		} else if (typeExp instanceof FullEnumTypeExp) {
			buildEnumType((FullEnumTypeExp)exp);
		} else if (typeExp instanceof FullListTypeExp) {
			buildListType((FullListTypeExp)exp);
		} else if (typeExp instanceof FullMapTypeExp) {
			buildMapType((FullMapTypeExp)exp);
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
						addError2s(typeExp, "type '%s' - unknown %s", typeName, typeExp.type.name());
					}
					break;
			}

			if (dtype != null) {
				addValidationRules(typeExp, dtype);
			}
		}
	}

	private void addValidationRules(FullTypeExp typeExp, DType type) {
		RuleConverter converter = new RuleConverter(crf, ruleDeclL, getET(), this.getLineLocator());

		List<RuleExp> adjustedRuleList = adjustRules(type, typeExp.ruleList);

		for(Exp exp: adjustedRuleList) {
			NRule vrule = converter.convert(type, exp, context);
			if (vrule == null) {
				this.addError2s(typeExp, "type %s: unknown rule %s", typeExp.type.name(), exp.strValue());
			} else {
//				this.log(vrule.getName());
				type.getRawRules().add(vrule);
			}
		}


		//unique is not a rule but evaluate it like a rule
		if (type instanceof DStructType) {
			DStructType structType = (DStructType) type;
			for(String fieldName: structType.getFields().keySet()) {
				if (structType.fieldIsUnique(fieldName)) {
					UniqueRule rule = new UniqueRule("unique", fieldName, structType, this.context);
					rule.setRuleText(fieldName);
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

		if (ruleList.size() >= 2) {
			List<RuleExp> L = new ArrayList<>();
			int n = ruleList.size();
			for(int i = 0; i < n; i++) {
				RuleExp exp = ruleList.get(i);
				if (exp instanceof CustomRule && i < (n-1)) {
					CustomRule rule = (CustomRule) exp;
					if (rule.ruleName.equals("len")) {
						rule.hackExtra = ruleList.get(i+1);
						L.add(rule);
						i++;
					}
				} else {
					L.add(exp);
				}
			}
			return L;
		}
		return ruleList;
	}

	private void buildStructType(FullStructTypeExp typeExp) {
		TypeBuilder tb = new TypeBuilder(registry, world);
		tb.setPackageName(packageHelper.getPackageName());
		if (! typeExp.type.name().equals("struct")) {
			DType baseType = packageHelper.findRegisteredType(typeExp.type.name());
			if (baseType == null) {
				this.addError(typeExp, "unknown struct base type", typeExp);
			} else if (baseType instanceof DStructType) {
				tb.setBaseType((DStructType) baseType);
			} else {
				this.addError(typeExp, "struct base type '%s' is not a struct type", typeExp);
			}
		}

		Inner inner = tb.start(typeExp.var.name());

		for(StructMemberExp membExp : typeExp.members.list) {
//			log(" m " + membExp.strValue());
			inner.resetForNextField();
			if (membExp.optional) {
				inner.optional();
			}
			if (membExp.isUnique) {
				inner.unique();
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
					addError2s(membExp, "struct type '%s' - unknown %s", typeExp.var.name(), membExp.type.name());
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
		FullListTypeExp listExp = new FullListTypeExp(0, varname, typename, elementType, ruleList);
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
//			log(" m " + membExp.strValue());
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
				addError2s(typeExp, "struct type '%s' - unknown %s", typeExp.var.name(), membExp.type.name());
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
		
		DType dtype = doListInner(exp, typeName, elType);
		this.addValidationRules(exp, dtype);
	}
	private DType doListInner(FullListTypeExp exp, String typeName, String elType) {
		DType eltype = null;
		String target = "list<";
		if (elType.startsWith(target)) {
			elType = StringUtils.substringAfter(elType, target);
			elType = elType.substring(0, elType.length() - 1);
			String innerTypeName = String.format("%s%d", typeName, nextInnerSuffix++);
			eltype = doListInner(exp, innerTypeName, elType); //recursion!
		} else {
			IdentExp elexp = new IdentExp(elType);
			if (TypeInfo.isPrimitiveType(elexp)) {
				elType = TypeInfo.toShapeType(elType);
			}
			eltype = packageHelper.findRegisteredType(elType);
		}
		
		if (eltype == null) {
			this.addError2s(exp, "type '%s': unknown list element type '%s'", typeName, elType);
		}

		DListType dtype = new DListType(Shape.LIST, typeName, null, eltype);
		registerType(typeName, dtype);
		return dtype;
	}
	
	private void buildMapType(FullMapTypeExp exp) {
		String typeName = exp.var.name();
		String elType = exp.getListElementType();
		
		DType dtype = doMapInner(exp, typeName, elType);
		this.addValidationRules(exp, dtype);
	}
	private DType doMapInner(FullMapTypeExp exp, String typeName, String elType) {
		DType eltype = null;
		String target = "map<";
		if (elType.startsWith(target)) {
			elType = StringUtils.substringAfter(elType, target);
			elType = elType.substring(0, elType.length() - 1);
			String innerTypeName = String.format("%s%d", typeName, nextInnerSuffix++);
			eltype = doMapInner(exp, innerTypeName, elType); //recursion!
		} else {
			IdentExp elexp = new IdentExp(elType);
			if (TypeInfo.isPrimitiveType(elexp)) {
				elType = TypeInfo.toShapeType(elType);
			}
			eltype = packageHelper.findRegisteredType(elType);
		}
		
		if (eltype == null) {
			this.addError2s("type '%s': unknown map element type '%s'", typeName, elType);
		}

		DMapType dtype = new DMapType(Shape.MAP, typeName, null, eltype);
		registerType(typeName, dtype);
		return dtype;
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
				this.addError2s(exp, "import '%s': %s", exp.strValue(), e.getMessage());
			}
		}

	}


	private void log(String s) {
		Log.log(s);
	}

}