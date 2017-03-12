package org.dnal.compiler.core;

import java.util.Calendar;
import java.util.Date;

import org.dnal.compiler.dnalgenerate.DateFormatParser;
import org.dnal.core.nrule.virtual.VirtualDate;
import org.junit.Test;

public class ValidationDateTests extends BaseValidationTests {
    private long same = 1481482266089L;
    private long before = same - 10;
    private long after = same + 10;

    @Test
    public void testDateStr() {
        Date dt = new Date(same);
        log(dt.toString());
        String s = DateFormatParser.format(dt);
        log(s);
        String strDate = "== '2016-12-11T13:51:06.089-0500'";
        chkCustomIntRule(strDate, same, true);
        strDate = "== '2016-12-11T13:51:06.089'";
        chkCustomIntRule(strDate, same, false);
        strDate = "== '2016-12-11T13:51:06'";
        chkCustomIntRule(strDate, same, false);
        strDate = "== '2016-12-11T13:51'";
        chkCustomIntRule(strDate, same, false);
        strDate = "== '2016-12-11T13'";
        chkCustomIntRule(strDate, same, false);
        strDate = "== '2016-12-11'";
        chkCustomIntRule(strDate, same, false);
        strDate = "== '2016-12'";
        chkCustomIntRule(strDate, same, false);
        strDate = "== '2016'";
        chkCustomIntRule(strDate, same, false);
        
    }
    
	@Test
	public void test1() {
		chkRule(">", before, false);
		chkRule(">", same, false);
		chkRule(">", after, true);
		
		chkRule(">=", before, false);
		chkRule(">=", same, true);
		chkRule(">=", after, true);
		
		chkRule("<", before, true);
		chkRule("<", same, false);
		chkRule("<", after, false);
		
		chkRule("<=", before, true);
		chkRule("<=", same, true);
		chkRule("<=", after, false);
	}
	
	@Test
	public void test2() {
		chkRule("==", before, false);
		chkRule("==", same, true);
		
		chkRule("!=", before, true);
		chkRule("!=", same, false);
	}
	
	@Test
	public void testOr() {
	    String rule = String.format("< %d or > %d", after, before);
		chkOrRule(rule, same, true);
	}
	@Test
	public void testOrFail() {
        String rule = String.format("< %d or > %d", before, after);
		chkOrRule(rule, same, false);
	}
	@Test
	public void testAnd() {
        String rule = String.format("< %d and > %d", after, before);
		chkOrRule(rule, same, true);
	}
	@Test
	public void testAndFail() {
	    String rule = String.format("< %d and > %d", before, after);
		chkOrRule(rule, same, false);
	}
	@Test
	public void testCustom() {
		VirtualDate vs = new VirtualDate();
//		MyCustomDateRule rule = new MyCustomDateRule("mydaterule", vs);
//		CustomRuleRegistry.addRule(rule);
		crf.addFactory(new MyCustomDateRule.Factory());
		chkCustomIntRule("mydaterule(4)", same, true);
		chkCustomIntRule("!mydaterule(4)", same, false);
		
		Date dt = getDate(2014, 12, 25);
		log(dt.toString());
		chkCustomIntRule("mydaterule(4)", dt.getTime(), false);
	}
	
	private Date getDate(int year, int mon, int day) {
	    Calendar cal = Calendar.getInstance();
	    cal.set(Calendar.YEAR, year);
	    cal.set(Calendar.MONTH, mon - 1);
	    cal.set(Calendar.DATE, day);
	    Date dt = cal.getTime();
	    return dt;
	}

	private void chkCustomIntRule(String text, long n, boolean ok) {
		String s = String.format("type Foo date %s end let x Foo = %d", text, n);
		parseAndValidate(s, ok, "DATE_SHAPE");
	}
	private void chkOrRule(String text, long n, boolean ok) {
		String s = String.format("type Foo date %s end let x Foo = %d", text, n);
		parseAndValidate(s, ok);
	}
	private void chkRule(String op, long n, boolean ok) {
		String s = String.format("type Foo date %s 1481482266089 end let x Foo = %d", op, n);
		parseAndValidate(s, ok);
	}
	private void parseAndValidate(String input, boolean expected) {
		parseAndValidate(input, expected, "DATE_SHAPE");
	}

}
