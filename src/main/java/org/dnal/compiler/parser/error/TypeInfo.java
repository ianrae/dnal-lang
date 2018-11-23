package org.dnal.compiler.parser.error;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dnal.compiler.parser.ast.IdentExp;
import org.dnal.core.BuiltInTypes;
import org.dnal.core.DType;
import org.dnal.core.Shape;

public class TypeInfo {
	
	public enum Type {
		INT,
		LONG,
		NUMBER,
		DATE,
		BOOLEAN,
		STRING,
		LIST,
		STRUCT,
		MAP,
		ENUM,
		ANY,
		
		UNKNOWN_TYPE
	}
	
	private static final String[] arPrimitiveType = new String[] { "int", "long", "number", "date", "boolean", "string", "list", "struct", "map", "enum", "any" };
	private static final List<String> listPrimitiveTypes = Arrays.asList(arPrimitiveType);
	
	private static final String[] arScalarType = new String[] { "int", "long", "number", "date", "boolean", "string", "enum" };
	private static final List<String> listScalarType = Arrays.asList(arScalarType);
	
	
	public static Type typeOf(IdentExp ident) {
		return typeOf(ident.val);
	}
	public static Type typeOf(String typeName) {
		switch(typeName) {
		case "int": return Type.INT;
		case "long": return Type.LONG;
		case "number": return Type.NUMBER;
		case "date": return Type.DATE;
		case "boolean": return Type.BOOLEAN;
		case "string": return Type.STRING;
		case "list": return Type.LIST;
		case "struct": return Type.STRUCT;
		case "map": return Type.MAP;
		case "enum": return Type.ENUM;
		case "any": return Type.ANY;
		default: return Type.UNKNOWN_TYPE;
		}
	}

	public static boolean isPrimitiveType(IdentExp ident) {
//		String[] ar = new String[] { "int", "long", "number", "date", "boolean", "string", "list", "struct", "map", "enum", "any" };
//		List<String> list = Arrays.asList(ar);

		return (listPrimitiveTypes.contains(ident.val));
	}
	public static boolean isScalarType(IdentExp ident) {
//		String[] ar = new String[] { "int", "long", "number", "date", "boolean", "string", "enum" };
//		List<String> list = Arrays.asList(ar);

		return (listScalarType.contains(ident.val));
	}


	public static String toShapeType(String primitiveType) {
	    initIfNeeded();
	    TypeDetails details = map1.get(primitiveType);
	    if (details == null) {
	        return "?????";
	    } else {
	        return details.builtinType.name();
	    }
	    
//	    
//		BuiltInTypes[] bit = { BuiltInTypes.INTEGER_SHAPE, BuiltInTypes.LONG_SHAPE, BuiltInTypes.NUMBER_SHAPE, 
//		        BuiltInTypes.DATE_SHAPE,
//		        BuiltInTypes.BOOLEAN_SHAPE, BuiltInTypes.STRING_SHAPE };
//		String[] ar = { "int", "long", "number", "date", "boolean", "string" };
//		
//		for(int i = 0; i < ar.length; i++) {
//			String primName = ar[i];
//			if (primName.equals(primitiveType)) {
//				return bit[i].name();
//			}
//		}
//		
//		return "?????";
	}
	
	public static boolean isBuiltIntype(String typeName) {
	    return typeName.contains("_SHAPE");
	}
    public static String getBaseTypeName(DType dtype) {
        return getBaseTypeName(dtype, false);
    }
    public static String getBaseTypeName(DType dtype, boolean withPkg) {
        String baseTypeName = "??";
        if (dtype.getBaseType() == null) {
            if (dtype.isShape(Shape.ENUM)) {
                baseTypeName = "enum";
            } else if (dtype.isStructShape()) {
                baseTypeName = "struct";
            } else if (dtype.isMapShape()) {
            	baseTypeName = "map";
            } else if (dtype.isAnyShape()) {
            	baseTypeName = "any";
            }
        } else {
            DType baseType = dtype.getBaseType();
            baseTypeName = (withPkg) ? baseType.getCompleteName() : baseType.getName();
            if (TypeInfo.isBuiltIntype(baseTypeName)) {
                baseTypeName = TypeInfo.parserTypeOf(baseTypeName);
            }
        }
        return baseTypeName;
    }
	
	
	public static String parserTypeOf(String dnalTypeName) {
        initIfNeeded();
        TypeDetails details = map2.get(dnalTypeName);
        if (details == null) {
            return dnalTypeName;
        } else {
            return details.dnalName;
        }
	    
//	    
//		BuiltInTypes[] bit = { BuiltInTypes.INTEGER_SHAPE, BuiltInTypes.LONG_SHAPE, BuiltInTypes.NUMBER_SHAPE, 
//		        BuiltInTypes.DATE_SHAPE,
//		        BuiltInTypes.BOOLEAN_SHAPE, BuiltInTypes.STRING_SHAPE };
//		String[] ar = { "int", "long", "number", "date", "boolean", "string" };
//		
//		for(int i = 0; i < bit.length; i++) {
//			String bitName = bit[i].name();
//			if (bitName.equals(dnalTypeName)) {
//				return ar[i];
//			}
//		}
//		
//		return dnalTypeName;
	}
	
    public static String shapeForRuleDecl(Shape shape) {
        String s = "???";
        switch(shape) {
        case BOOLEAN:
            s = "boolean";
            break;
        case DATE:
            s = "date";
            break;
        case ENUM:
            s = "enum";
            break;
        case INTEGER:
            s = "int";
            break;
        case LONG:
            s = "long";
            break;
        case LIST:
            s = "list";
            break;
        case NUMBER:
            s = "number";
            break;
        case STRING:
            s = "string";
            break;
        case STRUCT:
            s = "struct";
            break;
        case MAP:
        	s = "map";
        	break;
        case ANY:
        	s = "any";
        	break;
        default:
            break;
        }
        
        return s;
    }
    
    public static boolean isListAny(String typename) {
    	return "list<any>".equals(typename);
    }
    public static boolean isMapAny(String typename) {
    	return "map<any>".equals(typename);
    }
    
    public static Shape stringToShape(String shapeName) {
        Shape s = null;
        switch(shapeName) {
        case "boolean":
            s = Shape.BOOLEAN;
            break;
        case "date":
            s = Shape.DATE;
            break;
        case "enum":
            s = Shape.ENUM;
            break;
        case "int":
            s = Shape.INTEGER;
            break;
        case "long":
            s = Shape.LONG;
            break;
        case "list":
            s = Shape.LIST;
            break;
        case "number":
            s = Shape.NUMBER;
            break;
        case "string":
            s = Shape.STRING;
            break;
        case "struct":
            s = Shape.STRUCT;
            break;
        case "map":
            s = Shape.MAP;
            break;
        case "any":
            s = Shape.ANY;
            break;
        default:
            break;
        }
        
        return s;
    }
    
    private static class TypeDetails {
        public String dnalName;
        public BuiltInTypes builtinType;
        public boolean isPrimitive;
        public boolean isScalar;
    }
    private static Map<String,TypeDetails> map1 = new HashMap<>();
    private static Map<String,TypeDetails> map2 = new HashMap<>();
    private static boolean initYet = false;
    
    private static void initIfNeeded() {
        if (initYet) {
            return; //!!make thread safe
        }
        initYet = true;
        
        addTD("int", BuiltInTypes.INTEGER_SHAPE, true, true);
        addTD("long", BuiltInTypes.LONG_SHAPE, true, true);
        addTD("number", BuiltInTypes.NUMBER_SHAPE, true, true);
        addTD("date", BuiltInTypes.DATE_SHAPE, true, true);
        addTD("boolean", BuiltInTypes.BOOLEAN_SHAPE, true, true);
        addTD("string", BuiltInTypes.STRING_SHAPE, true, true);
//!!        addTD("list", BuiltInTypes.LIST_SHAPE, true);
//        addTD("struct", BuiltInTypes.STRUCT_SHAPE, true, false);
        addTD("enum", BuiltInTypes.ENUM_SHAPE, true, true);
        addTD("any", BuiltInTypes.ANY_SHAPE, true, true);
        
    }
    private static void addTD(String dnalName, BuiltInTypes bitType,
            boolean b, boolean bScalar) {
        TypeDetails td = new TypeDetails();
        td.dnalName = dnalName;
        td.builtinType = bitType;
        td.isPrimitive = b;
        td.isScalar = bScalar;
        
        map1.put(dnalName, td); //"int"
        map2.put(bitType.name(), td); //shape
    }
}
