package com.vegaflare.utils;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class Logger {
    public static void logInfo(String text){
        ZoneId z = ZoneId.of( "Asia/Kolkata");
        String now  = DateTimeFormatter.ofPattern("ddMMyyyy/hhmmss").format(ZonedDateTime.now(z));
        System.out.println("[INFO] "+now+" : "+ text);
    }

    public static void logError(String text){
        ZoneId z = ZoneId.of( "Asia/Kolkata");
        String now  = DateTimeFormatter.ofPattern("ddMMyyyy/hhmmss").format(ZonedDateTime.now(z));
        System.err.println("[ERROR] "+now+" : "+ text);
    }

    public static void logException(Exception e){
        ZoneId z = ZoneId.of( "Asia/Kolkata");
        String now  = DateTimeFormatter.ofPattern("ddMMyyyy/hhmmss").format(ZonedDateTime.now(z));
        System.err.println("[ERROR] "+now+" : "+e.getClass()+e.getMessage());
        //throw new RuntimeException();
    }
}
