package com.exchanger.exchange_api.util;

import java.util.Calendar;
import java.util.Date;

public class DateUtils {
    public static Date getTomorrow(Date date){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.HOUR, 24);
        return calendar.getTime();
    }
}
