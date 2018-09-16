package org.dnal.core.repository;

import java.util.ArrayList;
import java.util.List;

import org.dnal.core.DType;
import org.dnal.core.DValue;
import org.dnal.core.logger.Log;


public class MockRepository implements Repository {
	private List<DValue> list = new ArrayList<>();
	private DType type;
	
	public MockRepository(DType type) {
		this.type = type;
	}

	@Override
	public DType getType() {
		return type;
	}

	@Override
	public List<DValue> getAll() {
		return list;
	}

	@Override
	public void add(DValue dval) {
		Log.debugLog("MR:add %s", dval.getType().getName());
		list.add(dval);
	}

	@Override
	public long size() {
		return list.size();
	}

	@Override
	public boolean inRepo(DValue dval) {
		return list.contains(dval);
	}

//	@Override
//	public DValue findByField(String fieldName, Object value) {
//
//		for(DValue dval : list) {
//			Map<String, DValue> fields = dval.asMap();
//			DValue fieldVal = fields.get(fieldName);
//			if (fieldVal == null) {
//				Log.log(String.format("error: can't find field: %s", fieldName));
//				return null;
//			} else {
//				Object obj = fieldVal.getObject();
//				if (value.equals(obj)) {
//					return dval;
//				}
//			}
//		}
//		return null;
//	}


}