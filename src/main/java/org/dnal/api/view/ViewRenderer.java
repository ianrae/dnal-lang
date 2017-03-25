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

public class ViewRenderer extends ViewLoaderRendererBase {

		public ViewRenderer(DataSet ds) {
			super(ds);
		}
		public DValue render(DViewType viewType, DValue source) throws Exception {
			DType sourceType = registry.getType(viewType.getRelatedTypeName());
			if (!(sourceType instanceof DStructType)) {
				throw new IllegalArgumentException(String.format("can't find type: %s", viewType.getRelatedTypeName()));
			}

			if (! viewType.getDirection().equals(ViewDirection.OUTBOUND)) {
				throw new IllegalArgumentException(String.format("cannot render an inview: %s", viewType.getName()));
			}

			//source can be circle and sourceType can be shape
			if (! sourceType.isAssignmentCompatible(source.getType())) {
				throw new IllegalArgumentException(String.format("type mismatch. view expects %s but got %s", viewType.getRelatedTypeName(), source.getType().getName()));
			}

	
			Transaction trans = ds.createTransaction();
			Map<String,DValue> resultMap = new HashMap<>();
			for(String viewFieldName: viewType.getFields().keySet()) {
				DType destType = viewType.getFields().get(viewFieldName);
				String srcField = viewType.getNamingMap().get(viewFieldName);
				DValue sourceVal = source.asStruct().getField(srcField);

				DValue inner = convert(sourceVal, destType, trans); 
				resultMap.put(viewFieldName, inner);
			}

			DValueImpl dval = new DValueImpl(viewType, resultMap); 
			return dval;
		}

	}