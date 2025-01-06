package com.ewyboy.blockexchange;

import com.ewyboy.blockexchange.platform.Services;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ModLogger {

    private static final boolean IS_DEV_ENV = Services.PLATFORM.isDevelopmentEnvironment();
    private static final Logger LOGGER = LoggerFactory.getLogger(Constants.MOD_NAME);

    public static void info(String message, Object... args) {
        LOGGER.info(message, args);
    }

    public static void warn(String message, Object... args) {
        LOGGER.warn(message, args);
    }

    public static void error(String message, Object... args) {
        LOGGER.error(message, args);
    }

    public static void debug(String message, Object... args) {
        LOGGER.debug(message, args);
    }

    public static void trace(String message, Object... args) {
        LOGGER.trace(message, args);
    }

    public static boolean isDevelopment() {
        return IS_DEV_ENV;
    }

}
