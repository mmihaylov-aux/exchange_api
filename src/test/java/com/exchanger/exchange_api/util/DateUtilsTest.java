package com.exchanger.exchange_api.util;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Calendar;
import java.util.Date;

@RunWith(SpringRunner.class)
public class DateUtilsTest {
    @Test
    public void when_get_tomorrow_with_today_should_return_correct_date() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        Date now = calendar.getTime();

        Date tomorrow = DateUtils.getTomorrow(now);
        Calendar tomorrowCalendar = Calendar.getInstance();
        tomorrowCalendar.setTime(tomorrow);

        Assert.assertEquals(calendar.get(Calendar.HOUR_OF_DAY), tomorrowCalendar.get(Calendar.HOUR_OF_DAY));
        Assert.assertNotEquals(calendar.get(Calendar.DAY_OF_MONTH), tomorrowCalendar.get(Calendar.HOUR_OF_DAY));
    }

}
