package com.mengcraft.prefixbox;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public enum DatePrintHelper {

    INSTANCE;

    private final Map<String, SimpleDateFormat> all = new HashMap<>();

    public static String format(Date date, String syntax) {
        SimpleDateFormat format = INSTANCE.all.get(syntax);
        if (format == null) {
            INSTANCE.all.put(syntax, format = new SimpleDateFormat(syntax));
        }
        return format.format(date);
    }

}
