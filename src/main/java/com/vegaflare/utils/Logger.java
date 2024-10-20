package com.vegaflare.utils;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class Logger {
    public static void logInfo(String text){
        String now  = DateTimeFormatter.ofPattern("ddMMyyyy/hhmmss.SSS").format(ZonedDateTime.now());
        System.out.println(now+" [INFO] "+ text);
    }

    public static void logError(String text){
        String now  = DateTimeFormatter.ofPattern("ddMMyyyy/hhmmss.SSS").format(ZonedDateTime.now());
        System.err.println(now+" [ERROR] "+ text);
    }

}
