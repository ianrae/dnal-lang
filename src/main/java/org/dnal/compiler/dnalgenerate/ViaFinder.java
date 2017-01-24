package org.dnal.compiler.dnalgenerate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dnal.compiler.et.XErrorTracker;
import org.dnal.compiler.parser.ast.ViaExp;
import org.dnal.compiler.parser.error.ErrorTrackingBase;
import org.dnal.core.DStructHelper;
import org.dnal.core.DStructType;
import org.dnal.core.DType;
import org.dnal.core.DTypeRegistry;
import org.dnal.core.DValue;
import org.dnal.core.repository.MyWorld;
import org.dnal.core.repository.Repository;

public class ViaFinder extends ErrorTrackingBase {

    private MyWorld world;
    private DTypeRegistry registry;

    public ViaFinder(MyWorld world, DTypeRegistry registry, XErrorTracker et) {
        super(et);
        this.world = world;
        this.registry = registry;
        // TODO Auto-generated constructor stub
    }

    public List<DValue> findMatches(ViaExp via) {
        DType dtype = registry.getType(via.typeExp.name());
        if (dtype == null) {
            addError2s("via '%s' - unknown type '%s'", via.fieldExp.name(), via.typeExp.name());
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
        return matchL;
    }

    public boolean calculateUnique(DStructType structType, String fieldName) {
        List<Repository> repoList = buildRepoList(structType);

        Map<String,Integer> map = new HashMap<>();
        for(Repository repo: repoList) {
            for(DValue tmp: repo.getAll()) {
                DValue inner = tmp.asStruct().getField(fieldName);
                if (inner != null) {
                    String str = inner.asString();
                    if (map.containsKey(str)) {
                        return false;
                    } else {
                        map.put(str, 0);
                    }
                }
            }
        }
        return true;
    }

    private boolean isMatch(DValue dval, ViaExp via) {
        if (via == null || via.valueExp == null) {
            addError2s("via '%s' - null", via.fieldExp.name(), "");
            return false;
        }

        //ONLY structs for now!!
        DStructHelper helper = new DStructHelper(dval);
        DValue tmp = helper.getField(via.fieldExp.name());
        if (tmp == null) {
            addError2s("via '%s' - unknown match", via.fieldExp.name(), "");
            return false;
        }

        //LATER use better match
        String s1 = via.valueExp.strValue();
        String s2 = tmp.asString();
        return (s1.equals(s2));
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
