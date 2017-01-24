package org.dval;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

public class TransformerTests {
	
	public static interface Transformer {
		String transform(String input);
	}
	public static class CSVTransformer {
		private Map<String,List<Transformer>> map = new HashMap<>();
		
		public void add(String fieldName, Transformer transformer) {
			List<Transformer> list = map.get(fieldName);
			if (list == null) {
				list = new ArrayList<>();
			}
			list.add(transformer);
			map.put(fieldName, list);
		}
		
		public String apply(String fieldName, String input) {
			List<Transformer> list = map.get(fieldName);
			if (list == null || input == null) {
				return input;
			}
			
			String res = input;
			for(Transformer transformer : list) {
				res = transformer.transform(res);
			}
			
			return res;
		}
	}
	
	public static class TruncateTransformer implements Transformer {
		private int maxLen;
		
		public TruncateTransformer(int maxLen) {
			this.maxLen = maxLen;
		}

		@Override
		public String transform(String input) {
			if (input.length() > maxLen) {
				input = input.substring(0, maxLen);
			}
			
			return input;
		}
	}
	public static class UppercaseTransformer implements Transformer {

		@Override
		public String transform(String input) {
			input = input.toUpperCase();
			
			return input;
		}
	}

	@Test
	public void test() {
		assertEquals("abc", new TruncateTransformer(4).transform("abc"));
		assertEquals("abcd", new TruncateTransformer(4).transform("abcd"));
		assertEquals("abcd", new TruncateTransformer(4).transform("abcde"));
		assertEquals("", new TruncateTransformer(4).transform(""));
		//null will throw exception
	}

	@Test
	public void testCSV() {
		Transformer transformer = new TruncateTransformer(4);
		CSVTransformer csvt = new CSVTransformer();
		csvt.add("name", transformer);
		
		
		assertEquals("abcd", csvt.apply("name", "abcd"));
		assertEquals("abcd", csvt.apply("name", "abcde"));
		assertEquals(null, csvt.apply("name", null));
		assertEquals("abcde", csvt.apply("city", "abcde"));
	}

	@Test
	public void testCSVList() {
		Transformer transformer = new TruncateTransformer(4);
		CSVTransformer csvt = new CSVTransformer();
		csvt.add("name", transformer);
		csvt.add("name", new UppercaseTransformer());
		
		assertEquals("ABCD", csvt.apply("name", "abcd"));
		assertEquals("ABCD", csvt.apply("name", "abcde"));
		assertEquals(null, csvt.apply("name", null));
		assertEquals("abcde", csvt.apply("city", "abcde"));
	}
}
