package com.kakaobank.transactiongenerator.global.utils;

import com.kakaobank.transactiongenerator.global.WithLogger;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class SimulatorUtils implements WithLogger {
    private SimulatorUtils() {
        throw new IllegalStateException("Utility class");
    }

    private static final GregorianCalendar gc = new GregorianCalendar();
    public static final String CUSTOMER_NAME = "김OO";
    public static final String BANK_NAME = "OO은행";

    private static String getRandomBirth() {
        int year = randBetween(1924, 1980);
        gc.set(Calendar.YEAR, year);
        int dayOfYear = randBetween(1, gc.getActualMaximum(Calendar.DAY_OF_YEAR));
        gc.set(Calendar.DAY_OF_YEAR, dayOfYear);
        int month = (gc.get(Calendar.MONTH) + 1);
        String sMonth = month < 10 ? "0" + month : "" + month;
        int day = gc.get(Calendar.DAY_OF_MONTH);
        String sDay = day < 10 ? "0" + day : "" + day;

        return "" + gc.get(Calendar.YEAR) + sMonth + sDay;
    }

    private static int randBetween(int start, int end) {
        return start + (int)Math.round(Math.random() * (end - start));
    }

    public static String stringGen() {
        return UUID.randomUUID().toString();
    }

    public static String accountGen() {
        return String.valueOf(ThreadLocalRandom.current().nextLong(10000000000000L, 100000000000000L));
    }

    public static long amountGen() {
        return ThreadLocalRandom.current().nextLong(300000L, 600000L);
    }

    public static long withdrawAmountGen() {
        return ThreadLocalRandom.current().nextLong(100000L, 500000L);
    }

    public static long transferAmountGen() {
        return ThreadLocalRandom.current().nextLong(100000L, 400000L);
    }

    public static String timeGen() { return String.valueOf(System.currentTimeMillis()); }

    public static String birthGen() { return getRandomBirth(); }
}