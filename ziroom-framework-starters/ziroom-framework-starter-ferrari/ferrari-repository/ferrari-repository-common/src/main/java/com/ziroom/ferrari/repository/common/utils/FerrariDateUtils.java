package com.ziroom.ferrari.repository.common.utils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;

/**
 * @Author: J.T.
 * @Date: 2021/9/1 17:17
 * @Version 1.0
 */
public class FerrariDateUtils {

    public static Date plus(Date date, long amountToAdd, TimeUnit timeUnit) {
        LocalDateTime localDateTime = LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
        localDateTime = localDateTime.plus(amountToAdd, timeUnit.unit);
        ZonedDateTime zonedDateTime = localDateTime.atZone(ZoneId.systemDefault());
        return Date.from(zonedDateTime.toInstant());
    }

    public static Date minus(Date date, long amountToSubtract, TimeUnit timeUnit) {
        LocalDateTime localDateTime = LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
        localDateTime = localDateTime.minus(amountToSubtract, timeUnit.unit);
        ZonedDateTime zonedDateTime = localDateTime.atZone(ZoneId.systemDefault());
        return Date.from(zonedDateTime.toInstant());
    }

    public enum TimeUnit {

        SECONDS("Seconds", ChronoUnit.SECONDS),
        MINUTES("Minutes", ChronoUnit.MINUTES),
        HOURS("Hours", ChronoUnit.HOURS),
        DAYS("Days", ChronoUnit.DAYS),
        WEEKS("Weeks", ChronoUnit.WEEKS),
        MONTHS("Months", ChronoUnit.MONTHS),
        YEARS("Years", ChronoUnit.YEARS);

        private final String name;
        private final ChronoUnit unit;

        private TimeUnit(String name, ChronoUnit unit) {
            this.name = name;
            this.unit = unit;
        }

    }
}
