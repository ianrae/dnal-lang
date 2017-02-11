package com.github.ianrae.dnalparse.compiler;

import java.util.Calendar;
import java.util.Date;

import org.dnal.compiler.dnalgenerate.DateFormatParser;
import org.dnal.core.nrule.virtual.VirtualDate;
import org.junit.Test;

public class ValidateRangeRuleTests extends BaseValidationTests {
    private long same = 1481482266089L;
    private long before = same - 10;
    private long after = same + 10;

	
	private Date getDate(int year, int mon, int day) {
	    Calendar cal = Calendar.getInstance();
	    cal.set(Calendar.YEAR, year);
	    cal.set(Calendar.MONTH, mon - 1);
	    cal.set(Calendar.DATE, day);
	    Date dt = cal.getTime();
	    return dt;
	}


	@Test
	public void testRangeRuleComma() {
		String s1 = DateFormatParser.format(new Date(before));
		String s2 = DateFormatParser.format(new Date(after));
		String str = String.format("range('%s','%s')", s1, s2);
		chkRuleDate(str, same, true);
		
        str = String.format("range(%d,%d)", before, after);
        chkRuleDate(str, same, true);
        chkRuleDate(str, same - 9, true);
        chkRuleDate(str, same - 10, true);
        chkRuleDate(str, same - 11, false);
        chkRuleDate(str, same + 9, true);
        chkRuleDate(str, same + 10, false);
	}
    @Test
    public void testRangeRuleInt() {
        String str = "range(15,20)";
//        chkRuleInt(str, 15, true);
        
        str = String.format("range(%d..%d)", 15, 20);
//        chkRuleInt(str, 14, false);
        chkRuleInt(str, 15, true);
        chkRuleInt(str, 19, true);
        chkRuleInt(str, 20, false);
    }
    
	
	private void chkRuleDate(String text, long n, boolean ok) {
		String s = String.format("type Foo date %s end let x Foo = %d", text, n);
		parseAndValidate(s, ok, "DATE_SHAPE");
	}
    private void chkRuleInt(String text, long n, boolean ok) {
        String s = String.format("type Foo int %s end let x Foo = %d", text, n);
        parseAndValidate(s, ok, "INTEGER_SHAPE");
    }

}
