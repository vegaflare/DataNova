package com.vegaflare.utils;

import com.vegaflare.AutomicInt;
import com.vegaflare.DBItg;
import org.apache.commons.cli.*;
import org.ini4j.Ini;

import java.io.File;
import java.io.IOException;
import com.vegaflare.exceptions.InvalidParameterException;

import java.sql.SQLException;

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
    private int keepDays = 30;
    private String DBPwd;
    private String DBUser;
    private DBItg db;
    private String archiveKey;
    private Boolean ignoreExempted = false;

    public String getArchiveKey() {
        return archiveKey;
    }

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


    public int getUc4Client() {
        return uc4Client;
    }

    public AutomicInt getAutomicInt() {
        return automicInt;
    }


    /**
     * @return True if all objects with comment 'IGNORE' needs to be considered.
     */
    public Boolean getIgnoreExempted() {
        return ignoreExempted;
    }


    public Initializer(String[] args) {

            resolveParams(args);

        // Open ini file

        File file = new File("config-properties.ini");
        Ini ini = null;
        try {
            ini = new Ini(file);
        } catch (IOException e) {
            Logger.logError(e.getMessage());
            exit(20);
        }

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
            db = new DBItg(DBUser, DBPwd, JDBCUrl);


    }

    public void close() {
        try {
            db.getConnection().close();
        } catch (SQLException e) {
            Logger.logError(e.getMessage());
            exit(13);
        }
        try {
            automicInt.close();
        } catch (IOException e) {
            Logger.logError(e.getMessage());
            exit(14);
        }
    }


    protected void resolveParams(String[] args){


        Options options = new Options();
//      Define the options

        options.addOption("IGNORE", false, "Overrides commented ignores");
        options.addOption("CANCEL", false, "To cancel objects, only use if status selection is blocked - deactivate, if this flag is not mentioned");
        options.addOption("status", true, "Can be 'ENDED_OK'(default), 'ENDED_NOT_OK', 'ANY_OK', 'ANY_ABEND', 'BLOCKED'");

        Option dbUser = Option.builder("DBUser")
                .desc("User name for the database")
                .hasArg(true)
                .argName("USERNAME")
                .required(true)  // Mark as required
                .build();
        Option dbPwd = Option.builder("DBPwd")
                .desc("Password for the database user")
                .hasArg(true)
                .argName("PASSWORD")
                .required(true)  // Mark as required
                .build();
        Option arKey = Option.builder("key")
                .longOpt("archiveKey")
                .desc("Archive key for the tasks to be considered '%' for all")
                .hasArg(true)
                .argName("ARCHIVE_KEY")
                .required(true)  // Mark as required
                .build();
        Option days = Option.builder("n")
                .desc("Number of days to be excluded from today,default is 30")
                .hasArg(true)
                .argName("NUMBER")
                .required(false)  // Mark as required
                .build();
        options.addOption(dbUser);
        options.addOption(dbPwd);
        options.addOption(arKey);
        options.addOption(days);

        CommandLineParser parser = new DefaultParser();
        HelpFormatter helpFormatter = new HelpFormatter();

        //Parse the options from commandline inputs
        CommandLine cmd = null;
        try {
            cmd = parser.parse(options, args);

            this.archiveKey = cmd.getOptionValue("key");
            if (this.archiveKey.startsWith("-")) {
                throw new ParseException("Missing or invalid value for option -key / --archiveKey");
            } else if (archiveKey.equals("%")){
                this.archiveKey = "*";
            }
            this.DBUser = cmd.getOptionValue("DBUser");
            if (this.DBUser.startsWith("-")) {
                throw new ParseException("Missing or invalid value for option -DBUser");
            }
            this.DBPwd = cmd.getOptionValue("DBPwd");
            if (this.DBPwd.startsWith("-")) {
                throw new ParseException("Missing or invalid value for option -DBPwd");
            }
            if (cmd.hasOption("n")) {
                try {
                    this.keepDays = Integer.parseInt(cmd.getOptionValue("n"));
                } catch (NumberFormatException e) {
                    Logger.logError("Integer value expected for option 'n'.");
                    helpFormatter.printHelp("Vegaflare - DataNova", options);
                    exit(1);
                }
            }
            if (cmd.hasOption("status")) {
                this.status = cmd.getOptionValue("status");
                if (this.status.startsWith("-")) {
                    throw new ParseException("Missing or invalid value for option -status");
                }
                if (!cmd.hasOption("CANCEL") && status.equals("BLOCKED")) {
                    throw new ParseException("BLOCKED tasks cannot be deactivated, only cancel is possible. ");
                } else {
                    this.operation = "C";
                }
            }

            if (cmd.hasOption("CANCEL")) {
                throw new ParseException("Only blocked tasks can be canceled, please try again without 'CANCEL' for deactivation.");
            }
            if(cmd.hasOption("IGNORE")){
                this.ignoreExempted = true;
            }
        } catch (ParseException e) {
            Logger.logError(e.getMessage());
            helpFormatter.printHelp("Vegaflare - DataNova", options);
            exit(11);
        }

    }


}


