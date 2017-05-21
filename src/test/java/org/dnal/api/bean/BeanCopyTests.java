package org.dnal.api.bean;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.dnal.api.DataSet;
import org.dnal.api.Transaction;
import org.dnal.api.bean.ReflectionBeanLoaderTest.ClassA;
import org.dnal.api.view.ViewLoader;
import org.dnal.compiler.et.XErrorTracker;
import org.dnal.compiler.performance.PerfContinuingTimer;
import org.dnal.compiler.performance.PerfTimer;
import org.dnal.core.DStructType;
import org.dnal.core.DValue;
import org.dnal.core.NewErrorMessage;
import org.dnal.core.logger.Log;
import org.junit.Test;


public class BeanCopyTests {

	public static class FieldSpec {
		public FieldSpec(String srcField, String destField) {
			super();
			this.srcField = srcField;
			this.destField = destField;
		}
		public String srcField;
		public String destField;
		public String formatOptions;
	}

	public static class BeanCopierContext {
		private DNALLoader loader;
		public PerfContinuingTimer pctA = new PerfContinuingTimer();
		public PerfContinuingTimer pctB = new PerfContinuingTimer();
		public PerfContinuingTimer pctC = new PerfContinuingTimer();
		public PerfContinuingTimer pctD = new PerfContinuingTimer();
		public PerfContinuingTimer pctE = new PerfContinuingTimer();

		private BeanMethodCache methodCacheX;
		private List<String> allDestFields;
		private List<String> allSourceFields;
		private BeanMethodCache bmx;
		private BeanMethodCache bmdto;


		public BeanCopierContext() {
			loader = new DNALLoader();
			loader.initCompiler();
		}

		public boolean prepare(Object dto, Object x, List<FieldSpec> fieldL) {
			try {
				if (! doPrepare(dto, x, fieldL)) {
					return false;
				}
			} catch (Exception e) {
				addError("Exception:" + e.getMessage());
			}
			return ! areErrors();
		}

		private boolean doPrepare(Object dto, Object x, List<FieldSpec> fieldL) throws Exception {
			pctE.start();
			BeanMethodInvoker finder = new BeanMethodInvoker();
			methodCacheX = finder.getAllSetters(x.getClass());

			allDestFields = finder.getAllFields(x.getClass());
			allSourceFields = finder.getAllFields(dto.getClass());
			List<String> xlist = new ArrayList<>();
			List<String> dtolist = new ArrayList<>();
			for(FieldSpec field: fieldL) {
				if (allSourceFields.contains(field.srcField)) {
					dtolist.add(field.srcField);
				} else {
					addError(String.format("src can't find field '%s'", field.srcField));
				}

				if (allDestFields.contains(field.destField)) {
					xlist.add(field.destField);
				} else {
					addError(String.format("dest can't find field '%s'", field.destField));
				}
			}

			if (areErrors()) {
				return false;
			}

			BeanToDTypeBuilder builder = new BeanToDTypeBuilder();
			bmx = finder.getGetters(x.getClass(), xlist);
			bmdto = finder.getGetters(dto.getClass(), dtolist);

			String xName = x.getClass().getSimpleName();
			String dtoName = dto.getClass().getSimpleName();
			String viewName =  dtoName + "View";
			String dnal = builder.buildDnalType(xName, bmx, xlist);
			String dnal2 = builder.buildDnalType(dtoName, bmdto, dtolist);
			String dnal3 = builder.buildDnalView(xName, viewName, bmx, bmdto, xlist, dtolist);

			//			XErrorTracker.logErrors = true;
			//			Log.debugLogging = true;
			boolean b = loader.loadTypeDefinitionFromString(String.format("%s %s %s", dnal, dnal2, dnal3));
			if (! b) {
				return false;
			}
			pctE.end();

			return true;
		}

		private boolean areErrors() {
			return loader.getErrorTracker().areErrors();
		}

		private void addError(String message) {
			NewErrorMessage nem = new NewErrorMessage();
			nem.setErrorType(NewErrorMessage.Type.PARSING_ERROR);
			nem.setMessage(message);
			loader.getErrorTracker().addError(nem);
		}
	}

	public static class BeanCopier {
		BeanCopierContext bctx;
		private boolean isPrepared;


		public BeanCopier() {
			bctx = new BeanCopierContext();
		}

		public boolean copy(Object dto, Object x, List<FieldSpec> fieldL) {
			boolean ok = false;
			try {
				if (! isPrepared) {
					if (! prepare(dto, x, fieldL)) {
						return false;
					}
				} 
				ok = doCopy(dto, x, fieldL);
			} catch (Exception e) {
				addError("Exception:" + e.getMessage());
			}
			return ok;
		}

		private boolean prepare(Object dto, Object x, List<FieldSpec> fieldL) throws Exception {
			boolean ok = bctx.prepare(dto, x, fieldL);
			if (ok) {
				isPrepared = true;
			}
			return ok;
		}

		private boolean doCopy(Object dto, Object x, List<FieldSpec> fieldL) throws Exception {
			BeanMethodInvoker finder = new BeanMethodInvoker();

			bctx.pctA.start();
			List<String> xlist = new ArrayList<>();
			List<String> dtolist = new ArrayList<>();
			for(FieldSpec field: fieldL) {
				if (bctx.allSourceFields.contains(field.srcField)) {
					dtolist.add(field.srcField);
				} else {
					addError(String.format("src can't find field '%s'", field.srcField));
				}

				if (bctx.allDestFields.contains(field.destField)) {
					xlist.add(field.destField);
				} else {
					addError(String.format("dest can't find field '%s'", field.destField));
				}
			}
			bctx.pctA.end();

			if (areErrors()) {
				return false;
			}

			String xName = x.getClass().getSimpleName();
			String dtoName = dto.getClass().getSimpleName();
			String viewName =  dtoName + "View";

			bctx.pctB.start();
			DValue dvalDTO = bctx.loader.createFromBean(viewName, dto);
			if (dvalDTO == null) {
				return false;
			}
			bctx.pctB.end();

			bctx.pctC.start();
			DataSet ds = bctx.loader.getDataSet();
			ViewLoader viewLoader = new ViewLoader(ds);
			DValue dval = viewLoader.load(dvalDTO, (DStructType) ds.getType(xName));
			if (dval == null) {
				return false;
			}
			bctx.pctC.end();

			//now convert dval into x
			bctx.pctD.start();
			ScalarConvertUtil util = new ScalarConvertUtil();
			for(String fieldName: xlist) {
				Method meth = bctx.methodCacheX.getMethod(fieldName);

				Class<?> paramClass = meth.getParameterTypes()[0];
				DValue inner = dval.asStruct().getField(fieldName);
				if (inner != null) {
					Object obj = util.toObject(inner, paramClass);
					if (obj == null) {
						return false;
					}
					finder.invokeSetter(bctx.methodCacheX, x, fieldName, obj);
				}
			}
			bctx.pctD.end();
			return true;
		}

		private boolean areErrors() {
			return bctx.loader.getErrorTracker().areErrors();
		}

		private void addError(String message) {
			NewErrorMessage nem = new NewErrorMessage();
			nem.setErrorType(NewErrorMessage.Type.PARSING_ERROR);
			nem.setMessage(message);
			bctx.loader.getErrorTracker().addError(nem);
		}
	}


	@Test
	public void test() {
		BeanMethodInvoker finder = new BeanMethodInvoker();
		BeanMethodCache methodCache = finder.getAllGetters(ReflectionBeanLoaderTest.ClassA.class);
		assertEquals(6, methodCache.size());
		assertNotNull(methodCache.getMethod("nval"));
	}
	@Test
	public void testFilter() {
		BeanMethodInvoker finder = new BeanMethodInvoker();
		List<String> filter = Collections.singletonList("nval");
		BeanMethodCache methodCache = finder.getGetters(ReflectionBeanLoaderTest.ClassA.class, filter);
		assertEquals(1, methodCache.size());
		assertNotNull(methodCache.getMethod("nval"));
	}

	@Test
	public void testInvokeGetter() throws Exception {
		BeanMethodInvoker finder = new BeanMethodInvoker();
		List<String> filter = Collections.singletonList("nval");
		BeanMethodCache methodCache = finder.getGetters(ReflectionBeanLoaderTest.ClassA.class, filter);

		ReflectionBeanLoaderTest.ClassA beanA = new ClassA();
		beanA.setNval(44);
		Object obj = finder.invokeGetter(methodCache, beanA, "nval");
		Integer nval = (Integer) obj;
		assertEquals(44, nval.intValue());
	}

	@Test
	public void testInvokeSetter() throws Exception {
		BeanMethodInvoker finder = new BeanMethodInvoker();
		List<String> filter = Collections.singletonList("nval");
		BeanMethodCache methodCache = finder.getSetters(ReflectionBeanLoaderTest.ClassA.class, filter);

		ReflectionBeanLoaderTest.ClassA beanA = new ClassA();
		beanA.setNval(44);
		finder.invokeSetter(methodCache, beanA, "nval", Integer.valueOf(55));
		assertEquals(55, beanA.getNval());
	}

	//================================
	public static class ClassX {
		private String s1;
		private String s2;
		public String getS1() {
			return s1;
		}
		public void setS1(String s1) {
			this.s1 = s1;
		}
		public String getS2() {
			return s2;
		}
		public void setS2(String s2) {
			this.s2 = s2;
		}
	}
	public static class ClassXDTO {
		private String ss1;
		private String ss2;
		public String getSs1() {
			return ss1;
		}
		public void setSs1(String ss1) {
			this.ss1 = ss1;
		}
		public String getSs2() {
			return ss2;
		}
		public void setSs2(String ss2) {
			this.ss2 = ss2;
		}
	}
	public static class ClassY{
		private int yy1;
		private short yy2;
		private List<Integer> yylist1;

		public int getYy1() {
			return yy1;
		}

		public void setYy1(int yy1) {
			this.yy1 = yy1;
		}

		public short getYy2() {
			return yy2;
		}

		public void setYy2(short yy2) {
			this.yy2 = yy2;
		}

		public List<Integer> getYylist1() {
			return yylist1;
		}

		public void setYylist1(List<Integer> yylist1) {
			this.yylist1 = yylist1;
		}
	}

	//	public static class MyInt extends Integer {  not allowed
	//		
	//	}

	@Test
	public void testCopy() throws Exception {
		BeanMethodInvoker finder = new BeanMethodInvoker();
		BeanMethodCache methodCacheX = finder.getAllSetters(ClassX.class);

		ClassXDTO dto = new ClassXDTO();
		dto.ss1 = "abc";
		dto.ss2 = "abc2";

		ClassX x = new ClassX();
		DNALLoader loader = new DNALLoader();
		//		String dnal = "type X struct { s1 string optional s2 string optional  } end";
		//		String dnal2 = " type XDTO struct { ss1 string optional ss2 string optional } end";
		//		String dnal3 = " inview X <- XDTOView { s1 <- ss1 string   s2 <- ss2 string } end";		

		//		List<String> xlist = Arrays.asList("s1", "s2");
		//		List<String> dtolist = Arrays.asList("ss1", "ss2");
		List<String> xlist = finder.getAllFields(ClassX.class);
		List<String> dtolist = finder.getAllFields(ClassXDTO.class);

		BeanToDTypeBuilder builder = new BeanToDTypeBuilder();
		BeanMethodCache bmx = finder.getGetters(ClassX.class, xlist);
		BeanMethodCache bmdto = finder.getGetters(ClassXDTO.class, dtolist);

		String dnal = builder.buildDnalType("X", bmx, xlist);
		String dnal2 = builder.buildDnalType("XDTO", bmdto, dtolist);
		String dnal3 = builder.buildDnalView("X", "XDTOView", bmx, bmdto, xlist, dtolist);


		XErrorTracker.logErrors = true;
		Log.debugLogging = true;
		boolean b = loader.loadTypeDefinitionFromString(String.format("%s %s %s", dnal, dnal2, dnal3));
		assertEquals(true, b);

		//		ReflectionViewLoader vvv = new ReflectionViewLoader("XDTOView", ds, et, fieldConverter)
		DValue dvalDTO = loader.createFromBean("XDTOView", dto);
		assertEquals("abc", dvalDTO.asStruct().getField("ss1").asString());
		assertEquals("abc2", dvalDTO.asStruct().getField("ss2").asString());

		DataSet ds = loader.getDataSet();
		ViewLoader viewLoader = new ViewLoader(ds);
		DValue dval = viewLoader.load(dvalDTO, (DStructType) ds.getType("X"));

		assertEquals("X", dval.getType().getName());
		assertEquals("abc", dval.asStruct().getField("s1").asString());
		assertEquals("abc2", dval.asStruct().getField("s2").asString());

		//now convert dval into x
		Method meth = methodCacheX.getMethod("s1");
		//		finder.invokeSetter(methodCacheX, x, "s1", dval.asStruct().getField("s1").asString());
		finder.invokeSetter(methodCacheX, x, "s1", dval.asStruct().getField("s1").getObject());

		ScalarConvertUtil util = new ScalarConvertUtil();
		Class<?> paramClass = meth.getParameterTypes()[0];
		//		finder.invokeSetter(methodCacheX, x, "s2", dval.asStruct().getField("s2").asString());
		finder.invokeSetter(methodCacheX, x, "s2", util.toObject(dval.asStruct().getField("s2"), paramClass));
		assertEquals("abc", x.getS1());
		assertEquals("abc2", x.getS2());
	}

	@Test
	public void testBeanCopier() {
		BeanCopier copier = new BeanCopier();
		ClassXDTO dto = new ClassXDTO();
		dto.ss1 = "abc";
		dto.ss2 = "abc2";
		ClassX x = new ClassX();

		List<FieldSpec> fields = new ArrayList<>();
		fields.add(new FieldSpec("ss1", "s1"));
		fields.add(new FieldSpec("ss2", "s2"));

		boolean b = copier.copy(dto, x, fields);
		assertEquals(true, b);
		assertEquals("abc", x.getS1());
		assertEquals("abc2", x.getS2());
	}

	@Test
	public void testBeanCopierPerf() {
		BeanCopier copier = new BeanCopier();
		ClassXDTO dto = new ClassXDTO();
		dto.ss1 = "abc";
		dto.ss2 = "abc2";
		List<FieldSpec> fields = new ArrayList<>();
		fields.add(new FieldSpec("ss1", "s1"));
		fields.add(new FieldSpec("ss2", "s2"));

		PerfTimer perf = new PerfTimer();
		perf.startTimer("a");
		int n = 10; //1000; //7899
		//with perf 800. so 0.8 msec per run
		for(int i = 0; i < n; i++) {
			ClassX x = new ClassX();
			boolean b = copier.copy(dto, x, fields);
			assertEquals(true, b);
			assertEquals("abc", x.getS1());
			assertEquals("abc2", x.getS2());
		}
		perf.endTimer("a");
		perf.dump();
		log(String.format("pctA %d", copier.bctx.pctA.getDuration()));
		log(String.format("pctB %d", copier.bctx.pctB.getDuration()));
		log(String.format("pctC %d", copier.bctx.pctC.getDuration()));
		log(String.format("pctD %d", copier.bctx.pctD.getDuration()));
		log(String.format("pctE %d", copier.bctx.pctE.getDuration()));

	}

	@Test
	public void testY() throws Exception {
		BeanMethodInvoker finder = new BeanMethodInvoker();
		BeanMethodCache methodCacheDTO = finder.getAllSetters(ClassY.class);

		ClassY y = new ClassY();
		finder.invokeSetter(methodCacheDTO, y, "yy1", 15);
		assertEquals(15, y.getYy1());
		short sn = 23;
		finder.invokeSetter(methodCacheDTO, y, "yy1", sn);
		assertEquals(23, y.getYy1());

		//		finder.invokeSetter(methodCacheDTO, y, "yy2", 15);
		//		assertEquals(15, y.getYy2());
		sn = 23;
		finder.invokeSetter(methodCacheDTO, y, "yy2", sn);
		assertEquals(23, y.getYy2());

		List<Integer> list1 = Collections.singletonList(100);
		finder.invokeSetter(methodCacheDTO, y, "yylist1", list1);
		assertEquals(100, y.getYylist1().get(0).intValue());
	}

	@Test
	public void testConvert() throws Exception {
		ClassX x = new ClassX();
		DNALLoader loader = new DNALLoader();
		String dnal = "type X struct { n1 int } end";
		boolean b = loader.loadTypeDefinitionFromString(dnal);
		assertEquals(true, b);

		DataSet ds = loader.getDataSet();
		Transaction trans = ds.createTransaction();
		DValue dval = trans.createIntBuilder().buildFrom(100);
		assertEquals(100, dval.asInt());
		ScalarConvertUtil util = new ScalarConvertUtil();
		Object obj = util.toObject(dval);
		Integer n = (Integer) obj;
		assertEquals(100, n.intValue());

		obj = util.toObject(dval, Integer.class);
		n = (Integer) obj;
		assertEquals(100, n.intValue());

		obj = util.toObject(dval, Short.class);
		Short sn = (Short) obj;
		assertEquals(100, sn.intValue());
	}

	@Test
	public void testConvertI()  {
		assertEquals(false, Long.class.isAssignableFrom(Integer.class));
		assertEquals(false, Integer.class.isAssignableFrom(Long.class));

		List<String> list1 = Arrays.asList("a", "bbb", "ccc");
		List<String> list2 = Arrays.asList("a", "bb", "ccc");
		assertEquals(false, list1.equals(list2));
		list1 = Arrays.asList("a", "bbb", "ccc");
		list2 = Arrays.asList("a", "bbb", "ccc");
		assertEquals(true, list1.equals(list2));
	}	

	@Test
	public void testBeanToDTypeBuilder() {
		BeanMethodInvoker finder = new BeanMethodInvoker();
		BeanMethodCache methodCache = finder.getAllGetters(ClassX.class);
		BeanToDTypeBuilder builder = new BeanToDTypeBuilder();
		List<String> xlist = Arrays.asList("s1", "s2");
		String dnal = builder.buildDnalType("X", methodCache, xlist);
		assertEquals("type X struct {  s1 string optional s2 string optional } end", dnal);
	}


	private void log(String s) {
		System.out.println(s);;
	}

}
