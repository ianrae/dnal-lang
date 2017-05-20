package org.dnal.other;

import static org.junit.Assert.*;

import org.junit.Test;

public class OtherTests {
	
	static class Shape {
		public int x;
		public int y;
	}
	
	static class Circle extends Shape {
		public int radius;
	}

	@Test
	public void test() {
		Class<?> clazzShape = Shape.class;
		Class<?> clazzCir = Circle.class;
		
		boolean b = clazzShape.isAssignableFrom(clazzCir);
		assertEquals(true, b);
		b = clazzCir.isAssignableFrom(clazzShape);
		assertEquals(false, b);
	}
}
