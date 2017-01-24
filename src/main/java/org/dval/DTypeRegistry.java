package org.dval;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.dval.logger.Log;
import org.dval.nrule.NRule;


public class DTypeRegistry {
	private Map<String,DType> map = new TreeMap<>();
	private List<DType> orderedList = new ArrayList<>();
	private int nextBitIndex; //!! atomic thing later for thread safety
	private DTypeHierarchy th;

	public synchronized void add(String name, DType type) {
	    type.setBitIndex(nextBitIndex++);
	    orderedList.add(type);
		map.put(name, type);
		
		th = null; //clear
	}
	
	/**
	 * Create lazily. must be thread-safe
	 * @return
	 */
	public synchronized DTypeHierarchy getHierarchy() {
	    if (th == null) {
	        th = new DTypeHierarchy();
	        th.build(map);
	    }
	    return th;
	}

	public Set<String> getAll() {
		return map.keySet();
	}

	public int size() {
		return map.size();
	}
	
	public boolean existsType(String name) {
	    return getType(name) != null;
	}

	public DType getType(String name) {
		return map.get(name);
	}
	public DType getType(BuiltInTypes builtInType) {
		return map.get(builtInType.name());
	}
	
    public List<DType> getChildTypes(DType type) {
        DTypeHierarchy th = this.getHierarchy();
        return th.findChildTypes(this.map, type);
    }
    public List<DType> getParentTypes(DType type) {
        DTypeHierarchy th = this.getHierarchy();
        return th.findParentTypes(this.map, type);
    }
	

	public void dump() {
		Log.log("--regdump---");
		
		for(String typeName: map.keySet()) {
			DType type = map.get(typeName);
			String hasRepo = "";
//			if (listener.hasRepo(type)) {
//				hasRepo = "*";
//			}
			String base = (type.getBaseType() == null) ? "" : ": " + type.getBaseType().getName();
			String sh = type.getShape().name();
			Log.log(String.format(" %s %s [%s] %s", type.getCompleteName(), base, sh, hasRepo));
			String s = "";
			for(NRule rule : type.getRules()) {
				if (s.isEmpty()) {
					s += rule.getName();
				} else {
					s += ";" + rule.getName();
				}
			}
			if (! s.isEmpty()) {
				Log.log(String.format("   %s", s));
			}
			
		}
		Log.log("--regdump end.---");
	}

    public List<DType> getOrderedList() {
        return orderedList;
    }
	
}