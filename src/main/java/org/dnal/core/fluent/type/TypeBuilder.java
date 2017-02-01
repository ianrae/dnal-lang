package org.dnal.core.fluent.type;

import java.util.ArrayList;
import java.util.List;

import org.dnal.core.BuiltInTypes;
import org.dnal.core.DListType;
import org.dnal.core.DStructType;
import org.dnal.core.DType;
import org.dnal.core.DTypeRegistry;
import org.dnal.core.DTypeRegistryBuilder;
import org.dnal.core.OrderedMap;
import org.dnal.core.Shape;
import org.dnal.core.nrule.CompareRule;
import org.dnal.core.nrule.NRule;
import org.dnal.core.nrule.virtual.VirtualInt;
import org.dnal.core.nrule.virtual.VirtualIntMember;
import org.dnal.core.nrule.virtual.VirtualLong;
import org.dnal.core.nrule.virtual.VirtualLongMember;
import org.dnal.core.nrule.virtual.VirtualNumber;
import org.dnal.core.nrule.virtual.VirtualNumberMember;
import org.dnal.core.nrule.virtual.VirtualPseudoLen;
import org.dnal.core.nrule.virtual.VirtualPseudoLenMember;
import org.dnal.core.repository.MockRepositoryFactory;
import org.dnal.core.repository.World;
import org.dnal.core.repository.WorldListener;
import org.dnal.core.util.NameUtils;

public class TypeBuilder {
	public class Inner {
		protected DType currentField = null;
		protected String currentFieldName;
		protected boolean optional = false;
		protected boolean unique = false;
		protected List<NRule> rules = new ArrayList<>();

		public InnerString other(String fieldName, DType eltype) {
			validateFieldName(fieldName);
			doAddField(fieldName, eltype);
			return new InnerString(currentField, rules, fieldName); //not really correct!!
		}
		public Inner optional() {
		    this.optional = true;
		    return this;
		}
        public Inner unique() {
            this.unique = true;
            return this;
        }
        public InnerString string(String fieldName) {
            validateFieldName(fieldName);
            DType eltype = registry.getType(BuiltInTypes.STRING_SHAPE);
            doAddField(fieldName, eltype);
            return new InnerString(currentField, rules, fieldName);
        }
		public InnerEnum enumeration(String fieldName) {
			validateFieldName(fieldName);
			DType eltype = registry.getType(BuiltInTypes.ENUM_SHAPE);
            doAddField(fieldName, eltype);
			return new InnerEnum(currentField, rules, fieldName);
		}
		public InnerInteger integer(String fieldName) {
			validateFieldName(fieldName);
			DType eltype = registry.getType(BuiltInTypes.INTEGER_SHAPE);
            doAddField(fieldName, eltype);
			return new InnerInteger(currentField, rules, fieldName);
		}
        public InnerLong longInteger(String fieldName) {
            validateFieldName(fieldName);
            DType eltype = registry.getType(BuiltInTypes.LONG_SHAPE);
            doAddField(fieldName, eltype);
            return new InnerLong(currentField, rules, fieldName);
        }
        public InnerNumber number(String fieldName) {
            validateFieldName(fieldName);
            DType eltype = registry.getType(BuiltInTypes.NUMBER_SHAPE);
            doAddField(fieldName, eltype);
            return new InnerNumber(currentField, rules, fieldName);
        }
		public Inner bool(String fieldName) {
			validateFieldName(fieldName);
			DType eltype = registry.getType(BuiltInTypes.BOOLEAN_SHAPE);
            doAddField(fieldName, eltype);
			return this;
		}
		public Inner date(String fieldName) {
			validateFieldName(fieldName);
			DType eltype = registry.getType(BuiltInTypes.DATE_SHAPE);
            doAddField(fieldName, eltype);
			return this;
		}
		public Inner stringList(String fieldName) {
			validateFieldName(fieldName);
			DType eltype = registry.getType(BuiltInTypes.STRING_SHAPE);
			String listTypeName = String.format("%s<%s>", "LIST", eltype.getName());
			DListType type = (DListType) registry.getType(listTypeName);
			if (type == null) {
				type = new DListType(Shape.LIST, listTypeName, null, eltype);
				registerType(listTypeName, type);
			}
            doAddField(fieldName, type);
			return this;
		}

		public void end() {
			Shape shape = (amBuildingEnum) ? Shape.ENUM : Shape.STRUCT;
			generatedType = new DStructType(shape, typeName, baseType, fieldMap);
			addRules(generatedType);
			registerType(typeName, generatedType);
		}
		
		private void doAddField(String fieldName, DType eltype) {
            if (fieldMap.containsKey(fieldName)) {
                throw new IllegalArgumentException("field name already used: " + fieldName);
            }
            fieldMap.add(fieldName, eltype, optional, unique);
            currentField = eltype;
            currentFieldName = fieldName;
		    
		}
		private void addRules(DStructType generatedType) {
			if (currentField != null && rules.size() > 0) {
				for(NRule rule : rules) {
					generatedType.getRawRules().add(rule);
				}
			}
		}
		private void validateFieldName(String fieldName) {
			if (! isValidFieldName(fieldName)) {
				throw new IllegalArgumentException("invalid field name: " + fieldName);
			}
			
		}
		private boolean isValidFieldName(String fieldName) {
			
			for(int i = 0; i < fieldName.length(); i++) {
				char ch = fieldName.charAt(i);
				if (Character.isWhitespace(ch)) {
					return false;
				} else if (Character.isISOControl(ch)) {
					return false;
				} else if (! Character.isJavaIdentifierPart(ch)) {
					return false;
				}
			}
			return true;
		}
	}
	
	public class InnerString extends Inner {
		public InnerString(DType field, List<NRule> rules, String fieldName) {
			this.currentField = field;
			this.rules = rules;
			this.currentFieldName = fieldName;
		}
		public InnerString notEmpty() {
			addSizeRule(">", 0);
//			VRule rule = new VRule(String.format("!empty(%s)", currentFieldName));
//			rules.add(rule);
			return this;
		}
		public InnerString minSize(int size) {
			addSizeRule(">=", size);
			return this;
		}
		
		private void addSizeRule(String op, int size) {
		    VirtualPseudoLenMember vs = new VirtualPseudoLenMember();
		    vs.fieldName = currentFieldName;
			NRule rule = new CompareRule<VirtualPseudoLen, Integer>(op, op, vs, size);
//			WrapperRule<VirtualInt> wrapper = new WrapperRule<VirtualInt>("wrap", rule, vs);
//			StructWrapperRule swr = new StructWrapperRule("swr", wrapper, currentFieldName);
//			rules.add(swr);
			rules.add(rule);
		}
		
		public InnerString maxSize(int size) {
			addSizeRule("<", size);
			return this;
		}
	}
	public class InnerEnum extends Inner {
		public InnerEnum(DType field, List<NRule> rules, String fieldName) {
			this.currentField = field;
			this.rules = rules;
			this.currentFieldName = fieldName;
		}
	}
	public class InnerInteger extends Inner {
		public InnerInteger(DType field, List<NRule> rules, String fieldName) {
			this.currentField = field;
			this.rules = rules;
			this.currentFieldName = fieldName;
		}
		public InnerInteger min(int minimum) {
			addCompRule(">", minimum);
			return this;
		}
		public InnerInteger max(int maximum) {
			addCompRule("<", maximum);
			return this;
		}
		private void addCompRule(String op, int size) {
            VirtualIntMember vs = new VirtualIntMember();
            vs.fieldName = currentFieldName;
			NRule rule = new CompareRule<VirtualInt, Integer>(op, op, vs, size);
//			WrapperRule<VirtualInt> wrapper = new WrapperRule<VirtualInt>("wrap", rule, vs);
//			StructWrapperRule swr = new StructWrapperRule("swr", wrapper, currentFieldName);
			rules.add(rule);
		}
	}
    public class InnerLong extends Inner {
        public InnerLong(DType field, List<NRule> rules, String fieldName) {
            this.currentField = field;
            this.rules = rules;
            this.currentFieldName = fieldName;
        }
        public InnerLong min(int minimum) {
            addCompRule(">", minimum);
            return this;
        }
        public InnerLong max(int maximum) {
            addCompRule("<", maximum);
            return this;
        }
        private void addCompRule(String op, int size) {
            VirtualLongMember vs = new VirtualLongMember();
            vs.fieldName = currentFieldName;
            NRule rule = new CompareRule<VirtualLong, Long>(op, op, vs, (long)size);
//          WrapperRule<VirtualInt> wrapper = new WrapperRule<VirtualInt>("wrap", rule, vs);
//          StructWrapperRule swr = new StructWrapperRule("swr", wrapper, currentFieldName);
            rules.add(rule);
        }
    }
    public class InnerNumber extends Inner {
        public InnerNumber(DType field, List<NRule> rules, String fieldName) {
            this.currentField = field;
            this.rules = rules;
            this.currentFieldName = fieldName;
        }
        public InnerNumber min(double minimum) {
            addCompRule(">", minimum);
            return this;
        }
        public InnerNumber max(double maximum) {
            addCompRule("<", maximum);
            return this;
        }
        private void addCompRule(String op, Double size) {
            VirtualNumberMember vs = new VirtualNumberMember();
            vs.fieldName = currentFieldName;
            NRule rule = new CompareRule<VirtualNumber, Double>(op, op, vs, size);
//          WrapperRule<VirtualInt> wrapper = new WrapperRule<VirtualInt>("wrap", rule, vs);
//          StructWrapperRule swr = new StructWrapperRule("swr", wrapper, currentFieldName);
            rules.add(rule);
        }
        
    }
	

	private Inner xx = new Inner();
	private DTypeRegistry registry;
//	private Map<String,DType> fieldMap = new TreeMap<>();
	private OrderedMap fieldMap = new OrderedMap();
	private String typeName;
	private DStructType generatedType;
	private WorldListener world;
	private boolean amBuildingEnum;
	private DStructType baseType = null;
	private String packageName;

	public TypeBuilder(DTypeRegistry registry, WorldListener listener) {
		this.registry = registry;
		this.world = listener;
	}
	public TypeBuilder() {
		initRegistry();
	}
	public void setBaseType(DStructType type) {
	    baseType = type;
	}
    private void registerType(String typeName, DType dtype) {
        String completeName = NameUtils.completeName(packageName, typeName);
        registry.add(completeName, dtype);
        world.typeRegistered(dtype);
    }
 
	private void initRegistry() {
		world = new World();
		world.setRepositoryFactory(new MockRepositoryFactory());

		DTypeRegistryBuilder regBuilder = new DTypeRegistryBuilder();
		regBuilder.init(world);
		registry = regBuilder.getRegistry();
	}

	public Inner start(String typeName) {
		this.typeName = typeName;
		return xx;
	}

	public DStructType getType() {
		return generatedType;
	}
	public void setAmBuildingEnum(boolean amBuildingEnum) {
		this.amBuildingEnum = amBuildingEnum;
	}
    public String getPackageName() {
        return packageName;
    }
    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }
	
}