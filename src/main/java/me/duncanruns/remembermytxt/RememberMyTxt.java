package me.duncanruns.remembermytxt;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RememberMyTxt {

    public static Logger LOGGER = LogManager.getLogger();

    public static final String MOD_ID = "remember-my-txt";
    public static final String MOD_NAME = "Remember My Txt";

    public static void log(Level level, String message) {
        LOGGER.log(level, "[" + MOD_NAME + "] " + message);
    }

}