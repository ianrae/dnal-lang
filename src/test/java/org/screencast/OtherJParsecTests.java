package org.screencast;

import static org.junit.Assert.*;

import java.util.List;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.codehaus.jparsec.Parser;
import org.codehaus.jparsec.Parsers;
import org.codehaus.jparsec.Scanners;
import org.codehaus.jparsec.functors.Pair;
import org.junit.Test;

public class OtherJParsecTests {
	/*
	 * very cool. JParsec lets you build parsers in java.
	 */
	
	public interface Expression {}
	
	private Parser whitespaceInteger() {
		return Scanners.WHITESPACES.next(Scanners.INTEGER).
				map(new org.codehaus.jparsec.functors.Map<String, Integer>() {

					@Override
					public Integer map(String arg0) {
						return Integer.parseInt(arg0);
					}

				});
	}

	@Test
	public void test() {
		assertEquals(35, whitespaceInteger().parse(" 35"));
	}
	
	private Parser whitespaceEquals() {
		return Scanners.WHITESPACES.next(Scanners.isChar('='));
	}
	

	private static class RotateExpression implements Expression {
		private Integer val;
		public RotateExpression(Integer val) {
			this.val = val;
		}
		
		@Override
		public boolean equals(Object o) {

			if (o == this) return true;
			if (!(o instanceof RotateExpression)) {
				return false;
			}

			RotateExpression expr = (RotateExpression) o;

			return new EqualsBuilder()
					.append(val, expr.val)
					.isEquals();
		}

		@Override
		public int hashCode() {
			return new HashCodeBuilder(17, 37)
					.append(val)
					.toHashCode();
		}

		@Override
		public String toString() {
			return "RotateExpression [" + val + "]";
		}	
	}
	private static class EqualsSignExpression implements Expression {
		public EqualsSignExpression() {
		}
		
		@Override
		public boolean equals(Object o) {
			return true;
		}

		@Override
		public int hashCode() {
			return new HashCodeBuilder(17, 37)
					.toHashCode();
		}

		@Override
		public String toString() {
			return "EqualsSignExpression [" + "" + "]";
		}	
	}
	private static class VarnameExpression implements Expression {
		private String val;
		public VarnameExpression(String val) {
			this.val = val;
		}
		
		@Override
		public boolean equals(Object o) {

			if (o == this) return true;
			if (!(o instanceof RotateExpression)) {
				return false;
			}

			RotateExpression expr = (RotateExpression) o;

			return new EqualsBuilder()
					.append(val, expr.val)
					.isEquals();
		}

		@Override
		public int hashCode() {
			return new HashCodeBuilder(17, 37)
					.append(val)
					.toHashCode();
		}

		@Override
		public String toString() {
			return "VarnameExpression [" + val + "]";
		}	
	}
	
//	private static class TranslateExpression implements Expression {
//		private Integer val;
//		private Integer val2;
//		public TranslateExpression(Integer val, Integer val2) {
//			this.val = val;
//			this.val2 = val2;
//		}
//		@Override
//		public boolean equals(Object o) {
//
//			if (o == this) return true;
//			if (!(o instanceof TranslateExpression)) {
//				return false;
//			}
//
//			TranslateExpression expr = (TranslateExpression) o;
//
//			return new EqualsBuilder()
//					.append(val, expr.val)
//					.append(val2, expr.val2)
//					.isEquals();
//		}
//
//		@Override
//		public int hashCode() {
//			return new HashCodeBuilder(17, 37)
//					.append(val)
//					.append(val2)
//					.toHashCode();
//		}	
//		@Override
//		public String toString() {
//			return "TranslateExpression [" + val + "," + val2 + "]";
//		}	
//	}
//
	private Parser<RotateExpression> rotateExpression() {
		return Parsers.sequence(varnameExpression() , whitespaceEquals()).next(whitespaceInteger())
				.map(new org.codehaus.jparsec.functors.Map<Integer, RotateExpression>() {
					@Override
					public RotateExpression map(Integer arg0) {
						return new RotateExpression(arg0);
					}
				});
	}

	@Test
	public void test2() {
		RotateExpression expr = new RotateExpression(15);
		assertEquals(expr.val, rotateExpression().parse("able  = 15").val);
	}
	
	private Parser<VarnameExpression> varnameExpression() {
		return Scanners.IDENTIFIER
				.map(new org.codehaus.jparsec.functors.Map<String, VarnameExpression>() {
					@Override
					public VarnameExpression map(String arg0) {
						return new VarnameExpression(arg0);
					}
				});
	}
	
	@Test
	public void test2a() {
		VarnameExpression expr = new VarnameExpression("Able7");
		assertEquals(expr.val, varnameExpression().parse("Able7").val);
	}
	

//	@Test
//	public void test3() {
//		assertEquals(new TranslateExpression(15, 20), translateExpression().parse("translate 15 20"));
//	}
//
//	private Parser<TranslateExpression> translateExpression() {
//		return Scanners.string("translate").next(Parsers.tuple(whitespaceInteger(), whitespaceInteger()))
//				.map(new org.codehaus.jparsec.functors.Map<Pair<Integer, Integer>, TranslateExpression>() {
//					@Override
//					public TranslateExpression map(Pair<Integer, Integer> arg0) {
//						return new TranslateExpression(arg0.a, arg0.b);
//					}
//				});
//	}
//	
//	
//	private static class RectExpression implements Expression {
//		private Integer val;
//		private Integer val2;
//		private Integer val3;
//		private Integer val4;
//		public RectExpression(Integer val, Integer val2, Integer val3, Integer val4) {
//			this.val = val;
//			this.val2 = val2;
//			this.val3 = val3;
//			this.val4 = val4;
//		}
//		@Override
//		public boolean equals(Object o) {
//
//			if (o == this) return true;
//			if (!(o instanceof RectExpression)) {
//				return false;
//			}
//
//			RectExpression expr = (RectExpression) o;
//
//			return new EqualsBuilder()
//					.append(val, expr.val)
//					.append(val2, expr.val2)
//					.append(val3, expr.val3)
//					.append(val4, expr.val4)
//					.isEquals();
//		}
//
//		@Override
//		public int hashCode() {
//			return new HashCodeBuilder(17, 37)
//					.append(val)
//					.append(val2)
//					.append(val3)
//					.append(val4)
//					.toHashCode();
//		}
//		@Override
//		public String toString() {
//			return "RectExpression [" + val + ", " + val2 + ", " + val3 + ", " + val4 + "]";
//		}	
//	}
//
//	@Test
//	public void test4() {
//		assertEquals(new RectExpression(15, 20, 25, 30), rectExpression().parse("rect 15 20 25 30"));
//	}
//
//	private Parser<RectExpression> rectExpression() {
//		return Scanners.string("rect").next(whitespaceInteger().times(4))
//				.map(new org.codehaus.jparsec.functors.Map<List<Integer>, RectExpression>() {
//					@Override
//					public RectExpression map(List<Integer> arg0) {
//						return new RectExpression(arg0.get(0), arg0.get(1), arg0.get(2), arg0.get(3));
//					}
//				});
//	}
//	
//	private Parser<Expression> expression() {
//		return Parsers.or(rectExpression(), rotateExpression(), translateExpression());
//	}
//	
//	@Test
//	public void test5() {
//		assertEquals(new RotateExpression(30), expression().parse("rotate 30"));
//		assertEquals(new RectExpression(15, 20, 25, 30), expression().parse("rect 15 20 25 30"));
//	}
//	
//	private Parser<List<Expression>> block() {
//		return Parsers.between(Scanners.string("do").next(Scanners.WHITESPACES),
//				expression().sepBy(Scanners.WHITESPACES),
//				Scanners.WHITESPACES.next(Scanners.string("end"))
//				);
//	}
//
//	@Test
//	public void test6() {
//		assertEquals("[RectExpression [15, 20, 25, 30]]", block().parse("do rect 15 20 25 30 end").toString());
//	}
//	
//	private final static String example = "do \n" +
//					" translate 30 31 \n" + 
//					" translate 40 41 \n" +
//					"end \n" +
//					"do \n" + 
//					" rotate 30\n" +
//					"end";
//
//	private final static String example2 = "do \n" +
//			" translate 30 31 \n" + 
//			"end";
//
//	
//	private Parser<List<List<Expression>>> geoGrammar() {
//		return block().sepBy(Scanners.WHITESPACES);
//	}
//	
//	@Test
//	public void test7() {
//		System.out.println(example);
//		String s = "[[TranslateExpression [30,31], TranslateExpression [40,41]], [RotateExpression [30]]]";
//		assertEquals(s, geoGrammar().parse(example).toString());
//	}
//	
}
