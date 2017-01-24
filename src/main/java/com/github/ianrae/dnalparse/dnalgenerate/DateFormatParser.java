package com.github.ianrae.dnalparse.dnalgenerate;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

//http://stackoverflow.com/questions/2201925/converting-iso-8601-compliant-string-to-java-util-date
public class DateFormatParser {
    private static final DateFormat df1 = new SimpleDateFormat("yyyy");
    private static final DateFormat df2 = new SimpleDateFormat("yyyy-MM");
    private static final DateFormat df3 = new SimpleDateFormat("yyyy-MM-dd");
    private static final DateFormat df4 = new SimpleDateFormat("yyyy-MM-dd'T'HH");
    private static final DateFormat df5 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
    private static final DateFormat df6 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    private static final DateFormat df7 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
    private static final DateFormat dfFull = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

    private static DateFormat getDateFormat(String input) {
        int len = input.length();
        switch(len) {
        case 4:
            return df1;
        case 7:
            return df2;
        case 10:
            return df3;
        case 13:
            return df4;
        case 16:
            return df5;
        case 19:
            return df6;
        case 23:
            return df6;
        default:
            return dfFull;
        }
    }
    
    //support all forms of iso 8601!!
    public static Date parse(String input) {
        //        String string1 = "2001-07-04T12:08:56.235-0700";
        Date dt = null;
        try {
            DateFormat df = getDateFormat(input);
            dt = df.parse(input);
        } catch (ParseException e) {
        }  
        return dt;
    }

    public static String format(Date dt) {
        return dfFull.format(dt);
    }
}
