package com.github.ianrae.world;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dval.DStructHelper;
import org.dval.DStructType;
import org.dval.DType;
import org.dval.DValue;
import org.dval.DValueImpl;
import org.dval.Shape;
import org.dval.builder.StructBuilder;
import org.dval.nrule.NRule;
import org.dval.util.StringTrail;
import org.junit.Test;

import com.github.ianrae.dnalparse.DNALCompiler;
import com.github.ianrae.dnalparse.DataSet;
import com.github.ianrae.dnalparse.Transaction;
import com.github.ianrae.dnalparse.impl.DataSetImpl;
import com.github.ianrae.dnalparse.nrule.IsaRule;

/*
 * TODO
 * -change dval,myworld,and repository so we can start using NewWorld and GCache
 * -add injection so can select between using MyWorld and NewWorld
 * 
 * -the default gcache would be an empty cache with no db.
 * -so NewWorld's map of top-level values is what defines the corral
 * -then add a Generator that generates dnal. can load in next run using compiler
 *  
 * -then implement a hsql with tbl-per-type 
 */

public class PersistenceTests extends BaseWorldTest {

    public static class DBRecord {
        public long id;  //autoinc id by db
        public DValue dval;
        public Map<String, Object> carrierMap = new HashMap<>(); //ids of sub-objs. !!do later
    }

    public static class PersistException extends Exception {

        /**
         * 
         */
        private static final long serialVersionUID = 1L;

        public PersistException(long id, String msg) {
            super(String.format("%d: %s", id, msg));
        }
    }
    
    /**
     * Via and Isa require that we can search by fields.
     * Usually the naturalKey of a type but might be another field.
     * !!later add api for Long values
     * @author ian
     *
     */
    public interface DBQuery {
        List<DBRecord> findAllByField(String fieldName, String value) throws PersistException;
        List<DBRecord> findAllByField(String fieldName, List<String> valueList) throws PersistException;
        int countByField(String fieldName, String value) throws PersistException;
        boolean existsByField(String fieldName, String value) throws PersistException;
    }
    
    /**
     * Index, used for isa rules.
     * Note 'via' is a transient thing only used during ast-to-dnal
     *
     */
    public interface DBIndex {
        void insert(DValue dval) throws PersistException;
        void update(DValue dval) throws PersistException;
        void delete(Long dval) throws PersistException;
        boolean isIndexed(String fieldName);
        List<Long> findByNaturalKey(DValue naturalKeyValue) throws PersistException;
    }

    /**
     * One table per dval type.
     * Every top-level type has a table, and every type in a via or isa (both ends)
     * Normally only struct dvals have tables,
     * but can have scalar and list dvals.
     *
     */
    public static class DBTable implements DBQuery {
        public Map<Long,DBRecord> records = new HashMap<>();
        private long nextId = 1;
        private DBIndex indexHandler = null;
        
        public String getDBAsTrail() {
            StringTrail trail = new StringTrail();
            for(Long key: records.keySet()) {
                DBRecord rec = records.get(key);
                doXX(trail, rec.dval);
            }
            return trail.toString();
        }
        
        //!!doesn't handle runaway
        private void doXX(StringTrail trail, DValue dval) {
            String ss = "ss";
            Long id = (Long) dval.getPersistenceId();
            DType dtype = dval.getType();
            if (dtype.isScalarShape()) {
                ss = dval.asString();
                String s = String.format("%s", ss);
                trail.add(s);
            } else if (dtype.isStructShape()) {
                String s = String.format("%d:struct", id);
                trail.add(s);
                for(String kk: dval.asMap().keySet()) {
                    DValue inner = dval.asMap().get(kk);
                    doXX(trail, inner);
                }
            } else {
                String s = String.format("%d:list", id);
                trail.add(s);
            }
        }

        public long insert(DValue dval) throws PersistException {
            if (dval.getPersistenceId() != null) {
                Long id = (Long) dval.getPersistenceId();
                throw new PersistException(id, "already exists in insert");
            }
            DBRecord rec = new DBRecord();
            rec.id = nextId++;
            rec.dval = dval;
            DValueImpl impl = (DValueImpl) dval;
            impl.setPersistenceId(Long.valueOf(rec.id));
            records.put(rec.id, rec);
            
            if (indexHandler != null) {
                indexHandler.insert(dval);
            }
            
            return rec.id;
        }
        //only used with circ deps. create a row with id but no data
        //creates a slight bug in that any other node may read the empty insert
        //but not the subsequent update!!
        public long insertIdOnly() throws PersistException {
            DBRecord rec = new DBRecord();
            rec.id = nextId++;
            rec.dval = null;
            records.put(rec.id, rec);
            return rec.id;
        }
        public void update(DValue dval) throws PersistException {
            Long id = (Long) dval.getPersistenceId();
            if (id == null) {
                throw new PersistException(0L, "no id in update dval");
            }
            if (! records.containsKey(id)) {
                throw new PersistException(id, "id not found in update");
            }
            DBRecord rec = records.get(id);
            rec.dval = dval;
            records.put(rec.id, rec);
            
            if (indexHandler != null) {
                indexHandler.update(dval);
            }
        }
        public List<DBRecord> getAll() throws PersistException {
            List<DBRecord> list = new ArrayList<>();
            for(Long id: records.keySet()) {
                DBRecord rec = records.get(id);
                list.add(rec);
            }
            return list;
        }
        public DBRecord getById(Long id) throws PersistException {
            if (id == null) {
                throw new PersistException(0L, "no id in get dval");
            }
            if (! records.containsKey(id)) {
                return null;
            }
            DBRecord rec = records.get(id);
            return rec;
        }
        public List<Long> findAllByNaturalKey(DValue natkeyValue) throws PersistException {
            if (indexHandler == null) {
                throw new PersistException(0L, "no indexHandler in findAllByNaturalKey");
            }
            return indexHandler.findByNaturalKey(natkeyValue);
        }

        public void delete(long id) throws PersistException {
            if (! records.containsKey(id)) {
                throw new PersistException(id, "already exists in insert");
            }
            records.remove(id);
            
            if (indexHandler != null) {
                indexHandler.delete(id);
            }
        }

        public int size() {
            return records.size();
        }

        public void setIndexHandler(MyDBIndex dbindex) {
            this.indexHandler = dbindex;
        }

        @Override
        public List<DBRecord> findAllByField(String fieldName, String value) throws PersistException {
            List<DBRecord> resultL = new ArrayList<>();
            if (canUseIndex(fieldName)) {
                DValue dval = new DValueImpl(null, value);
                List<Long> idL = indexHandler.findByNaturalKey(dval);
                for(Long id: idL) {
                    DBRecord rec = records.get(id);
                    resultL.add(rec);
                }
            } else {
                for(Long id: records.keySet()) {
                    DBRecord rec = records.get(id);
                    if (rec.dval.getType().isStructShape()) {
                        DStructHelper helper = new DStructHelper(rec.dval);
                        DValue inner = helper.getField(fieldName);
                        if (inner != null) {
                            String ss = inner.asString();
                            if (ss.equals(value)) {
                                resultL.add(rec);
                            }
                        }
                    }
                }
            }
            return resultL;
        }
        
        private boolean canUseIndex(String fieldName) {
            if (indexHandler != null && indexHandler.isIndexed(fieldName)) {
                return true;
            } else {
                return false;
            }
                
        }

        @Override
        public List<DBRecord> findAllByField(String fieldName, List<String> valueList) throws PersistException {
            List<DBRecord> resultL = new ArrayList<>();
            for(String sval: valueList) {
                resultL.addAll(this.findAllByField(fieldName, sval));
            }
            return resultL;
        }

        @Override
        public int countByField(String fieldName, String value) throws PersistException {
            List<DBRecord> list = this.findAllByField(fieldName, value);
            return list.size();
        }

        //used for unique fields. can use more efficient search
        //For example. can search gcache first. if find a match no need to search db.
        @Override
        public boolean existsByField(String fieldName, String value)
                throws PersistException {
            List<DBRecord> list = this.findAllByField(fieldName, value);
            return list.size() == 1;
        }
    }

    /**
     * Render into json. do diffing
     * dval can be null if optional
     */
    public interface DBRenderer {
        String render(DValue dval);
        String diffAndRender(DValue dvalOld, DValue dval);
    }

    /**
     * a set of tables and indexes that represent a dval 'world'
     */
    public static class DBTableSet {
        private Map<String,DBTable> tables = new HashMap<>();

        public DBTableSet() {
        }
        
        public DBQuery getQueryAPI(String tableName) {
            DBTable tbl = getTbl(tableName);
            return tbl;
        }
        
        public String getAsTrail(String tableName) {
            DBTable tbl = getTbl(tableName);
            return tbl.getDBAsTrail();
        }

        public DBRecord getById(String type, long id) throws PersistException {
            DBTable tbl = getTbl(type);
            return tbl.getById(id);
        }
        public long insert(String type, DValue dval) throws PersistException {
            DBTable tbl = getTbl(type);
            return tbl.insert(dval);
        }
        private long insertIdOnly(String type) throws PersistException {
            DBTable tbl = getTbl(type);
            return tbl.insertIdOnly();
        }
        public void update(String type, DValue oldVal, DValue dval) throws PersistException {
            DBTable tbl = getTbl(type);
            tbl.update(dval);
        }
        public void delete(String type, DValue dval) throws PersistException {
            DBTable tbl = getTbl(type);
            Long id = (Long) dval.getPersistenceId();
            tbl.delete(id);
        }
        public int getTableSize(String type) {
            DBTable tbl = getTbl(type);
            return tbl.size();
        }

        private DBTable getTbl(String type) {
            return tables.get(type);
        }

        public boolean hasTable(String type) {
            return tables.containsKey(type);
        }
    }
    
    /**
     * gcache uses type.id for cache key.
     * -also used by dbindex
     *
     */
    public static class KeyHelper {
        public Long getId(DValue dval) {
            Long id = (Long) dval.getPersistenceId();
            return id;
        }
        public String makeKey(String type, DValue dval) {
            return makeKey(type, getId(dval));
        }
        public String makeKey(String type, Long id) {
            String key = String.format("%s.%d", type, id);
            return key;
        }
        public Long extractId(String key) {
            String[] ar = key.split("\\.");
            String s = ar[1];
            return Long.parseLong(s);
        }
    }
    
    /**
     * A cache of db objects.  One cache for whole tablset
     * Only objects that exist in db go into cache.
     * Cache can be tuned for performance and time-to-live
     * If db is eventlog then cache represents a readmodel -- the current projections
     * of objects. reloadValue would freshen the projection.
     * 
     * To implement lazy loading, this cache would get back DBRecord with carrierIds
     * which are the actual persId of sub-objects.
     * It would create DValueProxy for each sub-object passing in its type and persId.
     * The proxy would load the object on demand by calling back into gcache.
     * 
     */
    public static class GCache {
        private Map<String,DBRecord> cache = new HashMap<>(); //type+id
        private DBTableSet tableset;
        private KeyHelper keyHelper = new KeyHelper();

        public GCache(DBTableSet tables) {
            this.tableset = tables;
        }

        public DBQuery getQueryAPI(String tableName) {
            return tableset.getQueryAPI(tableName);
        }
        
        public String getAsTrail(String tableName) {
            String type = tableName;
            return tableset.getAsTrail(type);
        }
        
        public void clearCache() {
            cache.clear();
        }

        //oldval can be null if new
        public void putValue(DValue oldVal, DValue dval, boolean fullSave) throws PersistException {
            throwIfNull(dval);
            boolean exists = (dval.getPersistenceId() != null);
            prepare(dval);
            //already added to cache
            //!!do fullsave later

            String type = getTableName(dval);
            if (exists) {
                tableset.update(type, oldVal, dval);
            } else {
                //already inserted
            }
        }
        private void throwIfNull(DValue dval) throws PersistException {
            if (dval == null) {
                throw new PersistException(0, "null passed to gcache!!");
//            } else if (! dval.getType().isStructShape()) {
//                throw new PersistException(0, "non-struct passed to gcache!! - " + dval.getType().getName());
            }
        }

        public DValue getValue(DValue dval) throws PersistException {
            throwIfNull(dval);
            String key = makeKey(dval);
            DBRecord rec = cache.get(key);
            if (rec != null) {
                return rec.dval;
            }

            String type = getTableName(dval);
            long id = (long) dval.getPersistenceId();
            rec = this.tableset.getById(type, id);
            if (rec == null) {
                return null;
            }
            cache.put(key, rec);
            return rec.dval;
        }
        public DValue getValue(String typeName, long id) throws PersistException {
            String key = makeKey(typeName, id);
            DBRecord rec = cache.get(key);
            if (rec != null) {
                return rec.dval;
            }

            String type = typeName;
            rec = this.tableset.getById(type, id);
            if (rec == null) {
                return null;
            }
            cache.put(key, rec);
            return rec.dval;
        }
        public DValue getCachedValue(DValue dval) throws PersistException {
            throwIfNull(dval);
            String key = makeKey(dval);
            DBRecord rec = cache.get(key);
            if (rec != null) {
                return rec.dval;
            } else {
                return null;
            }
        }
        public DValue reloadValue(DValue dval) throws PersistException {
            throwIfNull(dval);
            String key = makeKey(dval);

            String type = getTableName(dval);
            long id = (long) dval.getPersistenceId();
            DBRecord rec = this.tableset.getById(type, id);
            cache.put(key, rec);
            return rec.dval;
        }
        public void removeValue(DValue dval) throws PersistException {
            throwIfNull(dval);
            String type = getTableName(dval);
            tableset.delete(type, dval);
        }

        //dval is a struct
        private void prepare(DValue dval) throws PersistException {
            Map<DValue, String> runawayMap = new HashMap<>();
            doPrepare(dval, runawayMap, true);
        }

        /**
         * @param dval is a struct or a list
         * @param runawayMap
         */
        private void doPrepare(DValue dval, Map<DValue, String> runawayMap, boolean isTopLevel) throws PersistException {
            boolean isStruct = dval.getType().isStructShape();
            if (runawayMap.containsKey(dval)) {
                if (isStruct && dval.getPersistenceId() == null) {
                    long id = tableset.insertIdOnly(getTableName(dval));
                    DValueImpl impl = (DValueImpl) dval;
                    impl.setPersistenceId(Long.valueOf(id));
                }
                return;
            }

            boolean needsId;
            if (dval.getType().isScalarShape()) {
                needsId = isTopLevel;
            } else if (dval.getType().isShape(Shape.LIST)) {
                List<DValue> L = dval.asList();
                for(DValue inner: L) {
                    if (! inner.getType().isScalarShape()) {
                        doPrepare(inner, runawayMap, false); //**recursion**
                    } 
                }
                needsId = isTopLevel;
            } else if (isStruct){
                Map<String,DValue> map = dval.asMap();
                for(String field: map.keySet()) {
                    DValue inner = map.get(field);
                    if (inner != null && !inner.getType().isScalarShape()) {
                        doPrepare(inner, runawayMap, false); //**recursion**
                    }                    
                }
                needsId = true;
            } else {
                Long id = (Long) dval.getPersistenceId();
                throw new PersistException(id, "uknown type in prepare: " + dval.getType().getName());
            }

            //for non-sttucts, only top-level need id.
            //all structs need id
            if (! needsId) {
                return;
            }
            
            if (dval.getPersistenceId() == null) {
                long id = tableset.insert(getTableName(dval), dval);
                DValueImpl impl = (DValueImpl) dval;
                impl.setPersistenceId(Long.valueOf(id));
            }
            
            runawayMap.put(dval, "");
            addToCache(dval);
        }

        private void addToCache(DValue dval) {
            String key = makeKey(dval);
            DBRecord rec = new DBRecord();
            Long id = (Long) dval.getPersistenceId();
            rec.id = id;
            rec.dval = dval;
            //no carriers!!
            this.cache.put(key, rec);
        }
        private String makeKey(DValue dval) {
            String type = getTableName(dval);
            return keyHelper.makeKey(type, dval);
        }
        private String makeKey(String type, Long id) {
            return keyHelper.makeKey(type, id);
        }

        private String getTableName(DValue dval) {
            return dval.getType().getName();
        }
    }
    
    /**
     * World aka corral.  All the top-level objects in a dataset.
     * Is backed by a gcache and a db
     */
    public static class NewWorld {
        private GCache gcache;
        private Map<String,DValue> topLevelMap = new HashMap<>();
        
        public NewWorld(GCache cache) {
            this.gcache = cache;
        }
        
        public DBQuery getQueryAPI(String tableName) {
            return gcache.getQueryAPI(tableName);
        }
        
        public void insertValue(String name, DValue dval) throws PersistException {
            if (topLevelMap.containsKey(name)) {
                throw new PersistException(0, "NewWorld.insertValue already has name: " + name);
            }
            gcache.putValue(null, dval, false);
            topLevelMap.put(name, dval);
        }
        public void updateValue(String name, DValue dval) throws PersistException {
            DValue oldVal = topLevelMap.get(name);
            if (oldVal == null) {
                throw new PersistException(0, "NewWorld.updateValue not found name: " + name);
            }
            
            gcache.putValue(oldVal, dval, false);
            topLevelMap.put(name, dval);
        }
        public void removeValue(String name) throws PersistException {
            DValue oldVal = topLevelMap.get(name);
            if (oldVal == null) {
                throw new PersistException(0, "NewWorld.removeValue not found name: " + name);
            }
            
            gcache.removeValue(oldVal);
            topLevelMap.remove(name);
        }
        public DValue getValue(String name) throws PersistException {
            DValue dval = topLevelMap.get(name);
            if (dval != null) {
                return dval;
            }

            String typeName = parseName(name);
            long id = parseNameForId(name);
            dval = gcache.getValue(typeName, id);
            if (dval == null) {
                return null;
            }
            topLevelMap.put(name, dval);
            return dval;
        }
        public DValue reloadValue(DValue dval) throws PersistException {
            dval = gcache.reloadValue(dval);
            return dval;
        }

        //!!NameToIdStrategy
        private long parseNameForId(String name) {
            String[] ar = name.split("\\.");
            String s = ar[1];
            return Long.parseLong(s);
        }

        private String parseName(String name) {
            String[] ar = name.split("\\.");
            return ar[0];
        }
    }
    
    /**
     * Index for natural keys. Each isa rule will
     * have an index.
     * -for now, a type can have at most one natural key.
     *
     */
    public static class MyDBIndex implements DBIndex {
        private String type;
        private String natkeyFieldName;
        private Map<String,String> records = new HashMap<>();
        private KeyHelper keyHelper = new KeyHelper();

        public MyDBIndex(String type, String natkeyFieldName) {
            this.type = type;
            this.natkeyFieldName = natkeyFieldName;
        }
        @Override
        public void insert(DValue dval) throws PersistException {
            String key = keyHelper.makeKey(type, dval);
            String keyvalue = makeKeyVal(dval);
            if (keyvalue == null) { //optional natkey value
                return;
            }
            
            if (records.containsKey(key)) {
                throw new PersistException(keyHelper.getId(dval), "already in index. insert");
            }
            
            records.put(key, keyvalue);
        }
        private String makeKeyVal(DValue dval) {
            DStructHelper helper = new DStructHelper(dval);
            DValue inner = helper.getField(natkeyFieldName);
            if (inner == null) {
                return null;
            }
            String keyvalue = inner.asString(); //long or string only for now!!
            return keyvalue;
        }
        @Override
        public void update(DValue dval) {
            String key = keyHelper.makeKey(type, dval);
            String keyvalue = makeKeyVal(dval);
            if (keyvalue == null) { //optional natkey value
                return;
            }

            records.put(key, keyvalue);
        }

        @Override
        public void delete(Long id) {
            String key = keyHelper.makeKey(type, id);
            records.remove(key);
        }

        @Override
        public List<Long> findByNaturalKey(DValue naturalKeyValue) {
            List<Long> resultL = new ArrayList<>();
            for(String key: records.keySet()) {
                String keyvalue = records.get(key);
                if (keyvalue.equals(naturalKeyValue)) {
                    Long id = keyHelper.extractId(key);
                    resultL.add(id);
                }
                
            }
            return resultL;
        }
        @Override
        public boolean isIndexed(String fieldName) {
            return natkeyFieldName.equals(fieldName);
        }
    }
    
    public static class DBTablesetBuilder {
        
        public DBTableSet buildFrom(DataSet ds) {
            return buildFrom(ds, null);
        }
        public DBTableSet buildFrom(DataSet ds, List<String> additionalTableNamesList) {
            DataSetImpl dataSet = (DataSetImpl) ds;
            DBTableSet tableset = new DBTableSet();
            
            if (additionalTableNamesList != null) {
                for(String typeName: additionalTableNamesList) {
                    DValue dval = ds.getValue(typeName);
                    DType dtype = dval.getType();
                    addTableIfNotYetAdded(tableset, dtype);
                }
            }
            
            //need tbl for each top-level type
            for(String topLevelName: dataSet.getInternals().getWorld().getOrderedList()) {
                DValue dval = ds.getValue(topLevelName);
                DType dtype = dval.getType();
                addTableIfNotYetAdded(tableset, dtype);
            }
            //then each end of all isa-relations need a table
            for(String type: dataSet.getInternals().getRegistry().getAll()) {
                DType dtype = dataSet.getInternals().getRegistry().getType(type);
                for(NRule rule: dtype.getRawRules()) {
                    if (rule instanceof IsaRule) {
                        addTableIfNotYetAdded(tableset, dtype);
                        IsaRule isa = (IsaRule) rule;
                        String targetType = isa.getViaFieldTypeName();
                        DType target = dataSet.getInternals().getRegistry().getType(targetType);
                        addTableIfNotYetAdded(tableset, target);
                        DBTable tbl = tableset.getTbl(targetType);
                        
                        //add an index Address.code isa Person.email
                        //add index on Person tbl of email
                        MyDBIndex dbindex = new MyDBIndex(targetType, isa.getViaFieldName());
                        tbl.setIndexHandler(dbindex);
                    }
                }
            }
            
            return tableset;
        }
        
        private void addTableIfNotYetAdded(DBTableSet tableset, DType dtype) {
            String type = dtype.getName();
            if (! tableset.hasTable(type)) {
                DBTable tbl = new DBTable();
                tableset.tables.put(type, tbl);
            }
        }
    }

    @Test
    public void test() throws PersistException {
        DBTable tbl = new DBTable();
        assertEquals(0, tbl.size());

        DataSet ds = createDataSet();
        DValue dval = createDValueLong(ds, "x1", 21L, 1);
        tbl.insert(dval);
        assertEquals(1, tbl.size());
        chkPersistId(dval, 1L);

        dval = createDValueLong(ds, "x2", 22L, 2);
        assertEquals(2, ds.size());
        tbl.insert(dval);
        assertEquals(2, tbl.size());
        chkPersistId(dval, 2L);
        
        String trail = tbl.getDBAsTrail();
        assertEquals("21;22", trail);
        
        //api can't be use with scalar values
        List<DBRecord> xlist = tbl.findAllByField("aaaa", Long.valueOf(21L).toString());
        assertEquals(0, xlist.size());

        tbl.delete(1L);
        tbl.delete(2L);
        assertEquals(0, tbl.size());
    }
    @Test
    public void test2() throws PersistException {
        DBTable tbl = new DBTable();
        assertEquals(0, tbl.size());

        DataSet ds = createDataSet();
        DValue dval = createDValueLong(ds, "x1", 22L, 1);
        tbl.insert(dval);
        assertEquals(1, tbl.size());
        chkPersistId(dval, 1L);
        Long id = (Long) dval.getPersistenceId();

        dval = createDValueLong(ds, "x2", 23L, 2);
        DValueImpl impl = (DValueImpl) dval;
        impl.setPersistenceId(id); //hack
        assertEquals(2, ds.size());
        tbl.update(dval);
        assertEquals(1, tbl.size());
        chkPersistId(dval, 1L);

        DBRecord rec = tbl.getById(1L);
        assertEquals(23L, rec.dval.asLong());
        rec = tbl.getById(991L);
        assertEquals(null, rec);

        tbl.delete(1L);
        assertEquals(0, tbl.size());
    }

    //------DBTableSet
    @Test
    public void test3() throws PersistException {
        DataSet ds = createDataSet();
        DValue dval = createDValueLong(ds, "x1", 22L, 1);
        DValue first = dval;
        String type = dval.getType().getName();
        DBTableSet tblset = buldDBTableSetFromDataSet(ds);
        long id = tblset.insert(type, dval);
        assertEquals(1L, id);
        chkPersistId(dval, 1L);

        dval = createDValueLong(ds, "x2", 23L, 2);
        //        dval.setPersistenceId(id); //hack
        assertEquals(2, ds.size());
        id = tblset.insert(type, dval);
        assertEquals(2L, id);
        chkPersistId(dval, 2L);

        assertEquals(2, tblset.getTableSize(type));
        tblset.delete(type, dval);
        tblset.delete(type, first);
        assertEquals(0, tblset.getTableSize(type));
    }
    @Test
    public void test3a() throws PersistException {
        DataSet ds = createDataSet();
        DValue dval = createDValueLong(ds, "x1", 22L, 1);
        String type = dval.getType().getName();
        DBTableSet tblset = buldDBTableSetFromDataSet(ds);
        long id = tblset.insert(type, dval);
        assertEquals(1L, id);
        chkPersistId(dval, 1L);

        DValue dval2 = createDValueLong(ds, "x2", 23L, 2);
        DValueImpl impl2 = (DValueImpl) dval2;
        impl2.setPersistenceId(id); //hack
        assertEquals(2, ds.size());
        tblset.update(type, dval, dval2);
        assertEquals(1L, id);
        chkPersistId(dval2, 1L);
    }

    //---GCache
    @Test
    public void test20() throws PersistException {
        DataSet ds = createDataSet();
        DValue dval = createPerson(ds, "x1", "abe", 1);
        DBTableSet tblset = buldDBTableSetFromDataSet(ds);
        GCache cache = new GCache(tblset);
        
        cache.putValue(null, dval, false);
        chkPersistId(dval, 1L);
        Long id = (Long) dval.getPersistenceId();
        
        DValue dval2 = createPerson(ds, "x1", "def", 1);
        DValueImpl impl2 = (DValueImpl) dval2;
        impl2.setPersistenceId(id); //hack
        assertEquals(1, ds.size());
        cache.putValue(dval, dval2, false);
        chkPersistId(dval2, 1L);
        
        String trail = cache.getAsTrail("Person");
        assertEquals("1:struct;def", trail);
        
        //api can be used with struct values
        String type = "Person";
        DBQuery api = cache.getQueryAPI(type);
        List<DBRecord> xlist = api.findAllByField("name", "bob");
        assertEquals(0, xlist.size());
        xlist = api.findAllByField("zzzname", "bob");
        assertEquals(0, xlist.size());
        xlist = api.findAllByField("name", "def");
        assertEquals(1, xlist.size());
        int n = api.countByField("name", "def");
        assertEquals(1, n);
        xlist = api.findAllByField("name", Collections.singletonList("def"));
        
        DValue dval3 = cache.getValue(dval2);
    }
    
    //---NewWorld
    @Test
    public void test30() throws PersistException {
        DataSet ds = createDataSet();
        DValue dval = createPerson(ds, "Person.1", "abe", 1);
        DBTableSet tblset = buldDBTableSetFromDataSet(ds);
        GCache cache = new GCache(tblset);
        NewWorld world = new NewWorld(cache);
        
        world.insertValue("Person.1", dval);
        chkPersistId(dval, 1L);
        Long id = (Long) dval.getPersistenceId();

        DValue dval2 = world.getValue("Person.1");
        assertSame(dval2, dval);
    }
    
    @Test
    public void test31() throws PersistException {
        DataSet ds = createDataSet();
        DValue dval = createPerson(ds, "Person.1", "abe", 1);
        DBTableSet tblset = buldDBTableSetFromDataSet(ds);
        GCache cache = new GCache(tblset);
        NewWorld world = new NewWorld(cache);
        
        cache.putValue(null, dval, false);
        chkPersistId(dval, 1L);

        DValue dval2 = world.getValue("Person.1");
        assertSame(dval2, dval);
    }
    @Test
    public void test31a() throws PersistException {
        DataSet ds = createDataSet();
        DValue dval = createPerson(ds, "Person.1", "abe", 1);
        DBTableSet tblset = buldDBTableSetFromDataSet(ds);
        GCache cache = new GCache(tblset);
        NewWorld world = new NewWorld(cache);
        
        cache.putValue(null, dval, false);
        chkPersistId(dval, 1L);
        cache.clearCache();
        
        DValue dval2 = world.getValue("Person.1");
        assertSame(dval2, dval);
    }


    //----
    private DNALCompiler compiler;
    private DStructType personType;
    
    
    protected DBTableSet buldDBTableSet() {
        DBTableSet tableset = new DBTableSet();
        DBTable tbl = new DBTable();
        tableset.tables.put("Person", tbl);
        tbl = new DBTable();
        tableset.tables.put("Address", tbl);
        return tableset;
    }
    protected DBTableSet buldDBTableSetFromDataSet(DataSet ds) {
        DBTablesetBuilder builder = new DBTablesetBuilder();
        return builder.buildFrom(ds);
    }
    
    protected DataSet createDataSet() {
        compiler = super.createCompiler();
        DataSet ds = compiler.compileString("type Person struct { name string } end");
        assertEquals(0, ds.size());
        return ds;
    }

    private DValue createDValueLong(DataSet ds, String name, long n, int expected) {
        Transaction trans = ds.createTransaction();
        DValue dval = trans.createLongBuilder().buildFrom(n);
        trans.add(name, dval);
        boolean b = trans.commit();
        assertEquals(true, b);
        assertEquals(expected, ds.size());

        return dval;
    }
    
    private DValue createPerson(DataSet ds, String name, String s, int expected) {
        Transaction trans = ds.createTransaction();
        StructBuilder sb = trans.createStructBuilder("Person");
        sb.addField("name", trans.createStringBuilder().buildFromString(s));
        DValue dval = sb.finish();
        trans.add(name, dval);
        boolean b = trans.commit();
        assertEquals(true, b);
        assertEquals(expected, ds.size());

        return dval;
    }

    private void chkPersistId(DValue dval, long expected) {
        Long id = (Long) dval.getPersistenceId();
        assertEquals(expected, id.longValue());
    }
}
