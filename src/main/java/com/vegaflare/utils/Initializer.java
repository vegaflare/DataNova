package com.vegaflare.utils;

import com.vegaflare.AutomicInt;
import com.vegaflare.DBItg;
import org.ini4j.Ini;
import org.ini4j.InvalidFileFormatException;

import java.io.File;
import java.io.IOException;
import com.vegaflare.exceptions.InvalidParameterException;

import java.sql.SQLException;
import java.util.Arrays;

import static java.lang.System.exit;

public class Initializer {

    private final String uc4Host;
    private final int uc4Port;
    private final String uc4User;
    private final String uc4Pwd;
    private final String uc4Dept;
    private final int uc4Client;
    private final String JDBCUrl;
    private String operation = "D";
    private AutomicInt automicInt;
    private String status = "ENDED_OK";
    private int keepDays;
    private String DBPwd;
    private String DBUser;
    private DBItg db;
    private String archiveKey;

    public String getArchiveKey(){return archiveKey;}

    public DBItg getDBItg() {
        return db;
    }
    public String getStatus() {
        return status;
    }

    public int getKeepDays() {
        return keepDays;
    }

    public String getOperation() {
        return operation;
    }

    public String getUc4Host() {
        return uc4Host;
    }

    public int getUc4Port() {
        return uc4Port;
    }

    public String getUc4User() {
        return uc4User;
    }

    public String getUc4Pwd() {
        return uc4Pwd;
    }

    public String getUc4Dept() {
        return uc4Dept;
    }

    public int getUc4Client() {
        return uc4Client;
    }

    public AutomicInt getAutomicInt() {
        return automicInt;
    }

    public String getDBPwd() {return DBPwd;}

    public String getDBUser() {return DBUser;}

    public String getJDBCUrl() {return JDBCUrl;}




    public Initializer(String[] args) throws Exception {


        try {
            resolveParams(args);
        } catch (InvalidParameterException e) {
            Logger.logException(e);
            exit(1);
        }


        // Open ini file

        File file = new File("config-properties.ini");
        Ini ini = new Ini(file);

        // Initialize all required parameters

        uc4Host = ini.get("automic", "host");
        uc4Port = Integer.parseInt((ini.get("automic", "port")));
        uc4Dept = ini.get("automic", "dept");
        uc4User = ini.get("automic", "user");
        uc4Pwd = ini.get("automic", "pass");
        uc4Client = Integer.parseInt(ini.get("automic", "client"));
        JDBCUrl = ini.get("JDBC", "connString");


//         Initialize connections to UC4
        automicInt = new AutomicInt(uc4Host, uc4Port, uc4User, uc4Dept, uc4Pwd, uc4Client);
        db  = new DBItg(DBUser, DBPwd, JDBCUrl);

    }

    public void close() {
        try {
            db.getConnection().close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        try {
            automicInt.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }



    protected void resolveParams(String[] args) throws InvalidParameterException {

        validateParams(args);
        for (int i = 0; i < args.length; i += 2) {
            switch (args[i]) {
                case "-operation": if(Arrays.asList(new String[]{"C","D"}).contains(args[i+1])){
                    this.operation = args[i + 1];
                }else{printParamDef();
                    throw new InvalidParameterException("Enter a valid mode.");}
                    break;
                case "-DBUser": this.DBUser = args[i+1];
                    break;
                case "-DBPwd" : this.DBPwd = args[i+1];
                    break;
                case "-status" : this.status = args[i+1];
                    break;
                case "-d" : this.keepDays = Integer.parseInt(args[i+1]);
                    break;
                case "-key" : archiveKey =  args[i+1].equals("%")? "*": args[i+1];
                    break;
            }
        }
    }

    // validate the cline params
    protected void validateParams(String[] args) throws InvalidParameterException {
        if (args.length == 8 || args.length == 12){
            String[] available = (args.length == 8) ? new String[]{"-key","-d","-DBUser", "-DBPwd"} : new String[]{"-key","-d","-DBUser", "-DBPwd","-status","-operation"};
            for (String s : available) {
                if (!Arrays.asList(args).contains(s)) {
                    printParamDef();
                    throw new InvalidParameterException("Invalid arguments provided.");
                }
            }
        }else{
            printParamDef();
            for( int i=0; i < args.length; i++)
                    System.out.println("\n"+args[i]);
            throw new InvalidParameterException("Invalid number of arguments provided."+ args.length);



        }
    }

    // Prints expected parameters
    private static void printParamDef(){
        System.out.println("\nExpected arguments:\n");
        System.out.println("   -DBUser <user>");
        System.out.println("   -DBPwd <pwd>");
        System.out.println("   -d <number of days>");
        System.out.println("   -key <ArchiveKey1 value to look for>");
        System.out.println("   *status: can be 'ENDED_OK'(default), 'ENDED_NOT_OK', 'ANY_OK', 'ANY_ABEND', 'BLOCKED'");
        System.out.println("   *operation: can be 'D' - deactivate(default), 'C' - cancel");
    }

}
