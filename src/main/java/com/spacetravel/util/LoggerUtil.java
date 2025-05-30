package com.spacetravel.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class LoggerUtil {

    static {
        String logLevel = System.getProperty("log.level", "info");

        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", logLevel);
        System.setProperty("org.slf4j.simpleLogger.showDateTime", "true");
        System.setProperty("org.slf4j.simpleLogger.dateTimeFormat", "yyyy-MM-dd HH:mm:ss");
        System.setProperty("org.slf4j.simpleLogger.showThreadName", "false");
        System.setProperty("org.slf4j.simpleLogger.logFile", "System.out");
    }

    private LoggerUtil() {
        // Utility class — no instantiation
    }

    public static Logger getLogger(Class<?> clazz) {
        return LoggerFactory.getLogger(clazz);
    }
}
