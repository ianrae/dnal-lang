package com.mycompany.app;

import static org.junit.Assert.*;

import java.lang.reflect.InvocationTargetException;

import org.junit.Test;

public class Java8MethodRefTests {
	
	public static enum Species {
		INSECT,
		MAMMAL
	}

	private static class Pet {
		private String name;
		private Integer age;
		private int size;
		private Species species;

		public Pet(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public Integer getAge() {
			return age;
		}

		public void setAge(Integer age) {
			this.age = age;
		}

		public int getSize() {
			return size;
		}

		public void setSize(int size) {
			this.size = size;
		}

		public Species getSpecies() {
			return species;
		}

		public void setSpecies(Species species) {
			this.species = species;
		}
	}

	private static class Zoo {
		private String val1;
		private String val2;
		private Pet pet;

		public Zoo(String val1, String val2) {
			super();
			this.val1 = val1;
			this.val2 = val2;
		}

		public String getVal1() {
			return val1;
		}
		public void setVal1(String val1) {
			this.val1 = val1;
		}
		public String getVal2() {
			return val2;
		}
		public void setVal2(String val2) {
			this.val2 = val2;
		}

		public Pet getPet() {
			return pet;
		}

		public void setPet(Pet pet) {
			this.pet = pet;
		}
	}

	public interface KKStr {
		String invokeGetter();
	}
	public interface KKStr3 {
		String invokeGetter(Zoo obj);
	}
	public static class MyKKStr3Impl implements KKStr3 {
		@Override
		public String invokeGetter(Zoo obj) {
			return obj.getVal2();
		}

	}

	public static class MyKK {
		public void foo(KKStr fn) {
			String val = fn.invokeGetter();
			log("foo: " + val);
		}
		public String foo2(Object obj, String methName) {
			Object res = null;
			try {
				res = obj.getClass().getMethod(methName).invoke(obj);
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return (String) res;
		}

		public void foo3(Zoo obj, KKStr3 fn) {
			if (obj == null) {
				return;
			}
			String val = fn.invokeGetter(obj);
			log("foo3: " + val);
		}
		private void log(String string) {
			System.out.println(string);
		}
	}

	public interface KKStr4<T> {
		String invokeGetter(T obj);
	}
	public interface KKInt4<T> {
		Integer invokeGetter(T obj);
	}
	public static class MyKK4<T> {
		private T obj;

		public MyKK4(T obj) {
			this.obj = obj;
		}

		public void foo4(KKStr4<T> fn) {
			if (obj == null) {
				return;
			}
			String val = fn.invokeGetter(obj);
			log("foo4: " + val);
		}
		private void log(String string) {
			System.out.println(string);
		}
	}

	public interface KKObj5<T,P> {
		P invokeGetter(T obj);
	}

	public static class MM<T> {
		private T tval;

		public MM(T tt) {
			tval = tt;
		}
		public String asString() {
			if (tval instanceof String) {
				return (String) tval;
			} else {
				return tval.toString();
			}
		}
		public Integer asInteger() {
			if (tval instanceof Integer) {
				return (Integer) tval;
			} else {
				throw new IllegalArgumentException("wrongtype!");
			}
		}
		public T value() {
			//			String val = fn2.invokeGetter(tval);
			//			log("foo5: " + val);
			return tval;
		}
	}

	public static class MyKK5<T,P> {
		private T obj;

		public MyKK5(T obj) {
			this.obj = obj;
		}

		public void foo5(KKObj5<T,P> fn, KKStr4<P> fn2) {
			if (obj == null) {
				return;
			}
			P objx = fn.invokeGetter(obj);
			if (objx == null) {
				return;
			}

			String val = fn2.invokeGetter(objx);
			log("foo5: " + val);
		}

		public <T2> MM<T2> foo5a(KKObj5<T,T2> fn, KKStr4<T2> fn2) {
			if (obj == null) {
				return null;
			}
			T2 objx = fn.invokeGetter(obj);
			if (objx == null) {
				return null;
			}
			String val = fn2.invokeGetter(objx);
			log("foo4: " + val);

			return new MM<T2>(objx);
		}

		public <T2> MM<T2> foo6(KKObj5<T,T2> fn) {
			if (obj == null) {
				return null;
			}
			T2 objx = fn.invokeGetter(obj);
			if (objx == null) {
				return null;
			}

			return new MM<T2>(objx);
		}

		private void log(String string) {
			System.out.println(string);
		}
	}

	public interface MM6 {
		String asString();
		Integer asInteger();
		<T2> T2 value();
		<T2> T2 value(T2 defaultVal);
	}
	public static class MM6String implements MM6 {
		private String tval;

		public MM6String(String tt) {
			tval = tt;
		}
		public String asString() {
			return (String) tval;
		}
		public Integer asInteger() {
			throw new IllegalArgumentException("wrongtype!");
		}
		@SuppressWarnings("unchecked")
		@Override
		public String value() {
			return tval;
		}
		@Override
		public <T2> T2 value(T2 defaultVal) {
			if (tval == null) {
				return defaultVal;
			}
			return (T2) tval;
		}
	}
	public static class MM6Integer implements MM6 {
		private Integer tval;

		public MM6Integer(Integer tt) {
			tval = tt;
		}
		public String asString() {
			return tval.toString();
		}
		public Integer asInteger() {
			return tval;
		}
		@SuppressWarnings("unchecked")
		@Override
		public Integer value() {
			return tval;
		}
		@Override
		public <T2> T2 value(T2 defaultVal) {
			if (tval == null) {
				return defaultVal;
			}
			return (T2) tval;
		}
	}
	public static class MM6Object implements MM6 {
		private Object tval;

		public MM6Object(Object tt) {
			tval = tt;
		}
		public String asString() {
			return tval.toString();
		}
		public Integer asInteger() {
			throw new IllegalArgumentException("wrongobj");
		}
		@SuppressWarnings("unchecked")
		@Override
		public Object value() {
			return tval;
		}
		@Override
		public <T2> T2 value(T2 defaultVal) {
			if (tval == null) {
				return defaultVal;
			}
			return (T2) tval;
		}
	}
	public static class MyKK6<T> {
		private T obj;

		public MyKK6(T obj) {
			this.obj = obj;
		}

		public <T2> MM6 get(KKObj5<T,T2> fn) {
			if (obj == null) {
				return null;
			}
			T2 objx = fn.invokeGetter(obj);
//			if (objx == null) {
//				return null;
//			}

			if (objx instanceof String) {
				return new MM6String((String) objx);
			} else if (objx instanceof Integer) {
				return new MM6Integer((Integer) objx);
			} else {
				return new MM6Object(objx);
//				throw new IllegalArgumentException("Wrongtype");
			}
		}

		private void log(String string) {
			System.out.println(string);
		}
	}

	@Test
	public void test() {
		Zoo zoo = new Zoo("a", "b");
		assertEquals("a", zoo.getVal1());

		MyKK mykk = new MyKK();
		mykk.foo(zoo::getVal1);

		assertEquals("b", mykk.foo2(zoo, "getVal2"));

		log("2..");
		mykk.foo(() -> zoo.getVal1());

		log("3..");
		Zoo zoo2 = zoo;
		mykk.foo3(zoo2, new MyKKStr3Impl());
		mykk.foo3(zoo2, (Zoo z) -> z.getVal1());
		mykk.foo3(zoo2, z -> z.getVal1());
		log("3null..");
		zoo2 = null;
		mykk.foo3(zoo2, new MyKKStr3Impl());
		mykk.foo3(zoo2, (Zoo z) -> z.getVal1());
	}
	@Test
	public void test4() {
		Zoo zoo = new Zoo("a", "b");

		MyKK4<Zoo> mykk = new MyKK4<>(zoo);
		mykk.foo4((Zoo z) -> z.getVal1());
		mykk.foo4(z -> z.getVal2());
		log("4null..");
		mykk = new MyKK4<>(null);
		mykk.foo4((z)-> z.getVal1());
	}
	@Test
	public void test5() {
		Zoo zoo = new Zoo("a", "b");
		Pet pet = new Pet("andy");
		zoo.setPet(pet);

		MyKK5<Zoo,Pet> mykk = new MyKK5<>(zoo);
		mykk.foo5(z -> z.getPet(), p -> p.getName());
		//		log("4null..");
		//		mykk = new MyKK4<>(null);
		//		mykk.foo4((z)-> z.getVal1());
		log("t2");
		mykk.foo5a(z -> z.getPet(), p -> p.getName());
	}
	@Test
	public void test6() {
		Pet pet = new Pet("andy");
		pet.setAge(100);
		pet.setSize(500);
		pet.setSpecies(Species.MAMMAL);
		MyKK6<Pet> gh = new MyKK6<>(pet);

		String s = gh.get(p -> p.getName()).asString();
		assertEquals("andy", s);

		Integer n = gh.get(p -> p.getAge()).value();
		assertEquals(100, n.intValue());
		n = gh.get(p -> p.getAge()).asInteger();
		assertEquals(100, n.intValue());
		
		//int
		n = gh.get(p -> p.getSize()).asInteger();
		assertEquals(500, n.intValue());
		n = gh.get(p -> p.getSize()).value();
		assertEquals(500, n.intValue());
		
		//species
		Species species = gh.get(p -> p.getSpecies()).value();
		assertEquals(Species.MAMMAL, species);

		pet.setSpecies(null);
		species = gh.get(p -> p.getSpecies()).value(Species.INSECT);
//		species = mykk.get(p -> p.getSpecies()).value();
		assertEquals(Species.INSECT, species);
		
		
//throws		n = mykk.foo6(p -> p.getName()).asInteger();

	}

	private void log(String string) {
		System.out.println(string);
	}
}
