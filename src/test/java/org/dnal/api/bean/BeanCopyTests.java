package org.dnal.api.bean;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

import org.dnal.api.DataSet;
import org.dnal.api.Transaction;
import org.dnal.api.bean.ReflectionBeanLoaderTest.ClassA;
import org.dnal.api.view.ViewLoader;
import org.dnal.core.DStructType;
import org.dnal.core.DValue;
import org.junit.Test;


public class BeanCopyTests {
	
	public static class BeanMethodInvoker {
		public BeanMethodCache getAllGetters(Class<?> beanClass) {
			return doGetAllGetters(beanClass, null);
		}
		public BeanMethodCache getGetters(Class<?> beanClass, List<String> filter) {
			return doGetAllGetters(beanClass, filter);
		}
		
		private BeanMethodCache doGetAllGetters(Class<?> beanClass, List<String> filter) {
			BeanMethodCache methodCache = new BeanMethodCache();
			Method[] ar = beanClass.getMethods();
			for(Method method: ar) {
				String mname = method.getName();
				if (method.getParameterCount() == 0) {
					if (mname.equals("getClass")) {
						continue;
					}

					if (mname.startsWith("get")) {
						String name = mname.substring(3); //remove get
						name = lowify(name);
						if (wantMethod(name, filter)) {
							methodCache.add(name, method);
						}
					} else if (mname.startsWith("is")) {
						String name = mname.substring(2); //remove is
						name = lowify(name);
						if (wantMethod(name, filter)) {
							methodCache.add(name, method);
						}
					}
				}
			}
			return methodCache;
		}
		
		public Object invokeGetter(BeanMethodCache methodCache, Object bean, String fieldName) throws Exception {
			Method meth = methodCache.getMethod(fieldName);
			if (meth == null) {
				throw new IllegalArgumentException(String.format("missing method for: '%s'", fieldName));
			}

			Object res = null;
			res = meth.invoke(bean);
			return res;
		}
		
		//--setters
		public BeanMethodCache getAllSetters(Class<?> beanClass) {
			return doGetAllSetters(beanClass, null);
		}
		public BeanMethodCache getSetters(Class<?> beanClass, List<String> filter) {
			return doGetAllSetters(beanClass, filter);
		}
		
		private BeanMethodCache doGetAllSetters(Class<?> beanClass, List<String> filter) {
			BeanMethodCache methodCache = new BeanMethodCache();
			Method[] ar = beanClass.getMethods();
			for(Method method: ar) {
				String mname = method.getName();
				if (method.getParameterCount() == 1) {

					if (mname.startsWith("set")) {
						String name = mname.substring(3); //remove set
						name = lowify(name);
						if (wantMethod(name, filter)) {
							methodCache.add(name, method);
						}
					}
				}
			}
			return methodCache;
		}
		
		public void invokeSetter(BeanMethodCache methodCache, Object bean, String fieldName, Object value) throws Exception {
			Method meth = methodCache.getMethod(fieldName);
			if (meth == null) {
				throw new IllegalArgumentException(String.format("missing method for: '%s'", fieldName));
			}

			meth.invoke(bean, value);
		}
		

		private boolean wantMethod(String name, List<String> filter) {
			if (filter == null) {
				return true;
			}
			return filter.contains(name);
		}

		private String lowify(String s) {
			if (s.isEmpty()) {
				return s;
			} else {
				String s1 = s.substring(0, 1).toLowerCase() + s.substring(1);
				return s1;
			}
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
	
//	public static class MyInt extends Integer {
//		
//	}
	
	@Test
	public void testCopy() throws Exception {
		BeanMethodInvoker finder = new BeanMethodInvoker();
		BeanMethodCache methodCacheX = finder.getAllSetters(ClassX.class);
		BeanMethodCache methodCacheDTO = finder.getAllSetters(ClassXDTO.class);
		
		ClassXDTO dto = new ClassXDTO();
		dto.ss1 = "abc";
		dto.ss2 = "abc2";
		
		ClassX x = new ClassX();
		DNALLoader loader = new DNALLoader();
		String dnal = "type X struct { s1 string optional s2 string optional  } end";
		String dnal2 = " type XDTO struct { ss1 string optional ss2 string optional } end";
		String dnal3 = " inview X <- XDTOView { s1 <- ss1 string   s2 <- ss2 string } end";		
		
//        XErrorTracker.logErrors = true;
//        Log.debugLogging = true;
		boolean b = loader.loadTypeDefinitionFromString(dnal + dnal2 + dnal3);
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
	}	
	private void log(String s) {
		System.out.println(s);;
	}

}
