package org.dnal.core.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dnal.core.DStructType;
import org.dnal.core.DType;
import org.dnal.core.DValue;
import org.dnal.core.Shape;
import org.dnal.core.nrule.ValidationScorer;

public class MyWorld implements WorldListener {
	private Map<DType, Repository> repoMap = new HashMap<>();
	private RepositoryFactory factory;
	public static boolean debug = false;
    private Map<String,DValue> valueMap = new HashMap<>();
    private List<String> orderedList = new ArrayList<>();
	
	private boolean goesInRepo(DType type) {
		boolean shouldAdd = false;
		if (type.isScalarShape() || type.isShape(Shape.STRUCT) || type.isShape(Shape.LIST)) {
			shouldAdd = true;
		}
		return shouldAdd;
	}

	@Override
	public void typeRegistered(DType type) {
		if (goesInRepo(type)) {
			logDebug(String.format("W: reg type '%s'", type.getCompleteName()));
			Repository repo = factory.createFor(type);
			repoMap.put(type, repo);
		}
	}
	
	private void log(String s) {
		System.out.println(s);
	}
	private void logDebug(String s) {
		if (debug) {
			System.out.println(s);
		}
	}
	
    public void addTopLevelValue(String name, DValue dval) {
        valueMap.put(name, dval);
        orderedList.add(name);
        valueAdded(dval);
    }
    public DValue findTopLevelValue(String varName) {
        return valueMap.get(varName);
    }

	@Override
	public void valueAdded(DValue dval) {
		DType type = dval.getType();
		if (goesInRepo(type)) {
			Repository repo = repoMap.get(type);
			//!! check for null
			if (! repo.inRepo(dval)) {
			    repo.add(dval);
			}
		}
	}
	
	@Override
	public boolean inRepo(DValue dval) {
		DType type = dval.getType();
		Repository repo = repoMap.get(type);
		if (repo != null) {
			return repo.inRepo(dval);
		}
		return false;
	}

	@Override
	public void setRepositoryFactory(RepositoryFactory factory) {
		this.factory = factory;
	}
	
	public RepositoryFactory getRepositoryFactory() {
	    return factory;
	}
	
	public Map<DType, Repository> getRepoMap() {
		return repoMap;
	}
	
	public void dump() {
		log("--dump---");
		
		for(DType type : repoMap.keySet()) {
			Repository repo = repoMap.get(type);
			log(String.format("repo %s: %d ", type.getCompleteName(), repo.size()));
			for(DValue dval : repo.getAll()) {
//				if (! dval.getValState().equals(ValidationState.UNKNOWN)) {
					String str = "";
					if (dval.getType().isScalarShape()) {
						str = String.format(": '%s'", dval.getObject().toString());
					}
					log(String.format("  %s - %s%s", repo.getType().getCompleteName(), dval.getValState().name(), str));
//				}
			}
		}
		log("--dump end.---");
	}
	
	public void scoreWorld(ValidationScorer scorer) {
		for(DType type : repoMap.keySet()) {
			Repository repo = repoMap.get(type);
			if (type.isShape(Shape.STRUCT)) {
				doScoreRepository(repo, scorer);
			}
		}
	}
    private void doScoreRepository(Repository repo, ValidationScorer scorer) {
        for(DValue dval : repo.getAll()) {
            switch(dval.getType().getShape()) {
            case LIST:
                scoreList(scorer, dval);
                break;
            case STRUCT:
                scoreStruct(scorer, dval);
                break;
            default:
                scoreScalar(scorer, dval);
                break;
            }
        }
    }
    private void scoreScalar(ValidationScorer scorer, DValue dval) {
        scorer.score(dval);
    }

    private void scoreStruct(ValidationScorer scorer, DValue dval) {
        Map<String,DValue> map = dval.asMap();
        for(String fieldName : map.keySet()) {
            DValue inner = map.get(fieldName);
            scorer.score(inner);
        }
        scorer.score(dval);
    }

    private void scoreList(ValidationScorer scorer, DValue dval) {
        List<DValue> list = dval.asList();
        for(DValue inner : list) {
            scorer.score(dval);
        }
        scorer.score(dval);
    }

	public void dumpType(DType type) {
		String baseTypeName = (type.getBaseType() == null) ? "" : ": " + type.getBaseType().getCompleteName();
		String shapeName = type.getShape().name();
		log(String.format("--%s %s [%s]---", type.getCompleteName(), baseTypeName, shapeName));
		
		if (type instanceof DStructType) {
			DStructType structType = (DStructType) type;
			for(String fieldName : structType.getFields().keySet()) {
				DType field = structType.getFields().get(fieldName);
				String base = (field.getBaseType() == null) ? "" : ": " + field.getBaseType().getCompleteName();
				String sh = field.getShape().name();
				log(String.format(" %s %s %s [%s]", fieldName, field.getName(), base, sh));
			}
		}
		
		log("--end.---");
	}

	@Override
	public boolean hasRepo(DType dtype) {
		return repoMap.containsKey(dtype);
	}
	
	@Override
	public Repository getRepoFor(DType dtype) {
		return repoMap.get(dtype);
	}

    public Map<String, DValue> getValueMap() {
        return valueMap;
    }

    public List<String> getOrderedList() {
        return orderedList;
    }
}