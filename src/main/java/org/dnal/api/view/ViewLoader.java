package org.dnal.api.view;

import java.util.HashMap;
import java.util.Map;

import org.dnal.api.DataSet;
import org.dnal.api.Transaction;
import org.dnal.compiler.parser.ast.ViewDirection;
import org.dnal.core.DStructType;
import org.dnal.core.DType;
import org.dnal.core.DValue;
import org.dnal.core.DValueImpl;
import org.dnal.core.DViewType;

public class ViewLoader extends ViewLoaderRendererBase {

		public ViewLoader(DataSet ds) {
			super(ds);
		}
		public DValue load(DValue source, DStructType targetType) throws Exception {
			if (!(source.getType() instanceof DViewType)) {
				throw new IllegalArgumentException(String.format("source must be a DViewType: %s", source.getType().getName()));
			}
			DViewType viewType = (DViewType) source.getType();
			
			DType tmp = registry.getType(viewType.getRelatedTypeName());
			if (!(tmp instanceof DStructType)) {
				throw new IllegalArgumentException(String.format("can't find type: %s", viewType.getRelatedTypeName()));
			}
			DStructType intoType = (DStructType) tmp;

			if (! viewType.getDirection().equals(ViewDirection.INBOUND)) {
				throw new IllegalArgumentException(String.format("cannot render an inview: %s", viewType.getName()));
			}

			//source can be circle and sourceType can be shape
			//clazzShape.isAssignableFrom(clazzCir);
			//so targetType can be a circ and sourceType can be a shape
			if (! intoType.isAssignmentCompatible(targetType)) {
				throw new IllegalArgumentException(String.format("type mismatch. view expects %s but got %s", viewType.getRelatedTypeName(), source.getType().getName()));
			}

			Transaction trans = ds.createTransaction();
			Map<String,DValue> resultMap = new HashMap<>();
			for(String targetFieldName: intoType.getFields().keySet()) {
				DType destType = intoType.getFields().get(targetFieldName);
				String srcField = viewType.getNamingMap().get(targetFieldName);
				DValue sourceVal = source.asStruct().getField(srcField);

				DValue inner = convert(sourceVal, destType, trans); 
				resultMap.put(targetFieldName, inner);
			}

			DValueImpl dval = new DValueImpl(targetType, resultMap); 
			return dval;
		}

	}