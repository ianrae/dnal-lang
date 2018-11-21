package org.dnal.compiler.dnalgenerate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jparsec.functors.Pair;
import org.dnal.compiler.et.XErrorTracker;
import org.dnal.compiler.parser.ast.ViaExp;
import org.dnal.compiler.parser.error.ErrorTrackingBase;
import org.dnal.compiler.parser.error.LineLocator;
import org.dnal.core.DStructHelper;
import org.dnal.core.DStructType;
import org.dnal.core.DType;
import org.dnal.core.DTypeRegistry;
import org.dnal.core.DValue;
import org.dnal.core.repository.Repository;
import org.dnal.core.repository.World;

public class ViaFinder extends ErrorTrackingBase {

	private World world;
	private DTypeRegistry registry;
	private List<Pair<String, DValue>> pendingL; //transaction items. not yet added to repo but need validation of uniqueness

	public ViaFinder(World world, DTypeRegistry registry, XErrorTracker et, LineLocator locator) {
		super(et, locator);
		this.world = world;
		this.registry = registry;
		// TODO Auto-generated constructor stub
	}

	public List<DValue> findMatches(ViaExp via) {
		DType dtype = registry.getType(via.typeExp.name());
		if (dtype == null) {
			addError2s(via, "via '%s' - unknown type '%s'", via.fieldExp.name(), via.typeExp.name());
			return null;
		}

		List<Repository> repoList = buildRepoList(dtype);

		List<DValue> matchL = new ArrayList<>();
		for(Repository repo: repoList) {
			for(DValue tmp: repo.getAll()) {
				if (isMatch(tmp, via)) {
					matchL.add(tmp);
				}
			}
		}

		repoList = buildRepoListForListTypes(dtype);
		for(Repository repo: repoList) {
			for(DValue tmp: repo.getAll()) {
				List<DValue> list = tmp.asList();
				for(DValue element: list) {
					if (isMatch(element, via)) {
						matchL.add(element);
					}
				}
			}
		}


		return matchL;
	}

	public boolean calculateUnique(DStructType structType, String fieldName) {
		Map<String,Integer> map = new HashMap<>();
		Map<DValue,DValue> alreadyHandledMap = new HashMap<>();
		
		/* validating a pending transaction add means we need to check the uniquess
		 * of a value in pendingL but not yet in repo.
		 */
		if (pendingL != null) {
			for(Pair<String,DValue> pair: pendingL) {
				DValue dval = pair.b;
				if (dval.getType() == structType || isChildTypeOf(dval, structType)) {
					DValue inner = dval.asStruct().getField(fieldName);
					if (inner != null) {
						if (alreadyHandledMap.containsKey(inner)) {
							continue;
						}
						
						String str = inner.asString();
						if (map.containsKey(str)) {
							return false;
						} else {
							alreadyHandledMap.put(inner, inner);
							map.put(str, 0);
						}
					}
				}
			}
		}

		List<Repository> repoList = buildRepoList(structType);
		for(Repository repo: repoList) {
			for(DValue tmp: repo.getAll()) {
				DValue inner = tmp.asStruct().getField(fieldName);
				if (inner != null) {
					if (alreadyHandledMap.containsKey(inner)) {
						continue;
					}
					
					String str = inner.asString();
					if (map.containsKey(str)) {
						return false;
					} else {
						alreadyHandledMap.put(inner, inner);
						map.put(str, 0);
					}
				}
			}
		}

		//and check lists
		repoList = buildRepoListForListTypes(structType);
		for(Repository repo: repoList) {
			for(DValue tmp: repo.getAll()) {
				List<DValue> list = tmp.asList();
				for(DValue element: list) {
					DValue inner = element.asStruct().getField(fieldName);
					if (inner != null) {
						if (alreadyHandledMap.containsKey(inner)) {
							continue;
						}

						String str = inner.asString();
						if (map.containsKey(str)) {
							return false;
						} else {
							alreadyHandledMap.put(inner, inner);
							map.put(str, 0);
						}
					}
				}
			}
		}

		return true;
	}

	private boolean isChildTypeOf(DValue dval, DStructType structType) {
		List<DType> childL = registry.getChildTypes(structType);
		for(DType type: childL) {
			if (dval.getType() == type) {
				return true;
			}
		}
		return false;
	}

	private boolean isMatch(DValue dval, ViaExp via) {
		if (via == null || via.valueExp == null) {
			addError2s(via, "via '%s' - null", via.fieldExp.name(), "");
			return false;
		}

		//ONLY structs for now!!
		DStructHelper helper = new DStructHelper(dval);
		DValue tmp = helper.getField(via.fieldExp.name());
		if (tmp == null) {
			addError2s(via, "via '%s' - unknown match", via.fieldExp.name(), "");
			return false;
		}

		ViaValueMatcher matcher = new ViaValueMatcher();
		boolean b = matcher.match(via.valueExp.strValue(), tmp);
		if (matcher.getErrMsg() != null) {
			addError2s(via, "via '%s' - match failed: %s", via.fieldExp.name(), matcher.getErrMsg());
			return false;
		}
		return b;
	}

	private List<Repository> buildRepoList(DType dtype) {
		List<Repository> repoList = new ArrayList<>();
		Repository repo = world.getRepoFor(dtype);
		if (repo != null) {
			repoList.add(repo);
		}
		
		List<DType> childL = registry.getChildTypes(dtype);
		for(DType tmp: childL) {
			repo = world.getRepoFor(tmp);
			if (repo != null) {
				repoList.add(repo);
			}
		}
		return repoList;
	}
	private List<Repository> buildRepoListForListTypes(DType elementType) {
		List<Repository> repoList = new ArrayList<>();
		Repository repo = world.getRepoForListType(elementType);
		if (repo != null) {
			repoList.add(repo);
		}
		List<DType> childL = registry.getChildTypes(elementType);
		for(DType tmp: childL) {
			repo = world.getRepoForListType(tmp);
			if (repo != null) {
				repoList.add(repo);
			}
		}
		return repoList;
	}

	public void setPendingL(List<Pair<String, DValue>> pendingL) {
		this.pendingL = pendingL;
	}

	//    public Exp convertToExp(DValue dval) {
	//        Shape shape = dval.getType().getShape();
	//        switch(shape) {
	//        case INTEGER:
	//        {
	//            Integer n = dval.asInt();
	//            return new IntegerExp(n);
	//        }
	//        case NUMBER:
	//        {
	//            double d = dval.asNumber();
	//            return new NumberExp(d);
	//        }
	//        case DATE:
	//        {
	//            Date dt = dval.asDate();
	//            return new LongExp(dt.getTime());
	//        }
	//        case BOOLEAN:
	//        {
	//            boolean b = dval.asBoolean();
	//            return new BooleanExp(b);
	//        }
	//        case STRING:
	//        {
	//            String s = dval.asString();
	//            return new StringExp(s);
	//        }
	//        case ENUM:
	//        {
	//            //handle later!!
	//        }
	//        break;
	//        //              case LIST:
	//        //                  break;
	//
	//        default:
	//            addError2s("var '%s' - unknown shape '%s'", "?", shape.name());
	//            break;
	//        }
	//
	//        return null;
	//    }

}
