package org.dnal.core;

import java.util.ArrayList;
import java.util.List;

import org.dnal.core.nrule.NRule;
import org.dnal.core.util.NameUtils;

public class DType {
	private Shape shape;
	private String name;
	private String packageName;
	private String completeName;
	private DType baseType; //can be null
	private List<NRule> rules = new ArrayList<>();
	private int bitIndex;

	public DType(Shape shape, String name, DType baseType) {
		this.shape = shape;
		this.name = name;
		this.completeName = name;
		this.baseType = baseType;
	}

	public boolean isShape(Shape target) {
		return (target != null && target.equals(shape));
	}
	public boolean isScalarShape() {
		switch(shape) {
		case LIST:
		case STRUCT:
			return false;
		default:
			return true;
		}
	}
	public boolean isStructShape() {
	    return shape == Shape.STRUCT;
	}

	public Shape getShape() {
		return shape;
	}

	public String getName() {
		return name;
	}

	public DType getBaseType() {
		return baseType;
	}
	
	/**
	 * Can type2 be used where this is expected.
	 * @param type2  derived class
	 * @return
	 */
	public boolean isAssignmentCompatible(DType type2) {
		if (this == type2) {
			return true;
		}
		DType current = type2.getBaseType();
		
		//!!add runaway check
		while(current != null) {
			if (current == this) {
				return true;
			}
			current = current.getBaseType();
		}
		return false;
	}
	
	public List<NRule> getRules() {
		List<NRule> copy = new ArrayList<>(rules);
		return copy;
	}
	public List<NRule> getRawRules() {
		return rules;
	}
	public boolean hasRules() {
		return rules.size() > 0;
	}

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
        this.completeName = NameUtils.completeName(packageName, name);
    }
    
    public String getCompleteName() {
        return completeName;
    }

    public int getBitIndex() {
        return bitIndex;
    }

    public void setBitIndex(int bitIndex) {
        this.bitIndex = bitIndex;
    }
	
}