package com.vegaflare.utils;

import com.vegaflare.AutomicInt;
import com.vegaflare.DBItg;
import org.apache.commons.cli.*;

import java.io.IOException;

import java.sql.SQLException;

import static java.lang.System.exit;

public class Initializer {

    private String uc4Host;
    private int uc4Port;
    private String uc4User;
    private String uc4Pwd;
    private String uc4Dept;
    private int uc4Client;
    private String JDBCUrl;
    private String operation = "D";
    private final AutomicInt automicInt;
    private String status = "ENDED_OK";
    private int keepDays = 30;
    private String DBPwd;
    private String DBUser;
    private final DBItg db;
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
        Logger.logInfo("Database connection closed");
        try {
            automicInt.close();
        } catch (IOException e) {
            Logger.logError(e.getMessage());
            exit(14);
        }
        Logger.logInfo("UC4 connection closed");
    }


    protected void resolveParams(String[] args){


        Options options = getOptions();

        CommandLineParser parser = new DefaultParser();
        HelpFormatter helpFormatter = new HelpFormatter();

        //Parse the options from commandline inputs
        CommandLine cmd;
        try {
            cmd = parser.parse(options, args);

            this.archiveKey = cmd.getOptionValue("key");
            if (this.archiveKey.startsWith("-")) {
                throw new ParseException("Missing or invalid value for option -key / --archiveKey");
            } else if (archiveKey.equals("%")){
                this.archiveKey = "*";
            }

            this.uc4Host = cmd.getOptionValue("h");
            this.uc4Port = Integer.parseInt(cmd.getOptionValue("p"));
            this.uc4Client = Integer.parseInt(cmd.getOptionValue("c"));
            this.uc4User = cmd.getOptionValue("u");
            this.uc4Dept = cmd.getOptionValue("dept");
            this.uc4Pwd = cmd.getOptionValue("uc4pwd");


            this.JDBCUrl = cmd.getOptionValue("jdbc");
            this.DBUser = cmd.getOptionValue("DBUser");
            this.DBPwd = cmd.getOptionValue("DBPwd");

            if (cmd.hasOption("n")) {
                try {
                    this.keepDays = Integer.parseInt(cmd.getOptionValue("n"));
                } catch (NumberFormatException e) {
                    Logger.logError("Integer value expected for option 'n'.");
                    helpFormatter.printHelp("DataNova.jar", options);
                    exit(1);
                }
            }
            if (cmd.hasOption("status")) {
                this.status = cmd.getOptionValue("status");
                if (status.equals("BLOCKED")) {
                    if(!cmd.hasOption("CANCEL")) {
                        throw new ParseException("BLOCKED tasks cannot be deactivated, only cancel is possible. ");
                    } else {
                        this.operation = "C";
                    }
                }
            }

            if (cmd.hasOption("CANCEL")) {
                if(!this.operation.equals("C")) {
                    throw new ParseException("Only blocked tasks can be canceled, please try again without 'CANCEL' for deactivation.");
                }
            }
            if(cmd.hasOption("IGNORE")){
                this.ignoreExempted = true;
            }
        } catch (ParseException e) {
            Logger.logError(e.getMessage());
            helpFormatter.printHelp("DataNova.jar", options);
            exit(11);
        }

    }


    ///Definition of all Options
    Options getOptions(){
        Options options = new Options();
//      Define the options

        options.addOption("IGNORE", false, "Overrides commented ignores");
        options.addOption("CANCEL", false, "To cancel objects, only use if status selection is blocked - deactivate, if this flag is not mentioned");
        options.addOption("status", true, "Can be 'ENDED_OK'(default), 'ENDED_NOT_OK', 'ANY_OK', 'ANY_ABEND', 'BLOCKED'");

        Option uc4U = Option.builder("u")
                .longOpt("uc4User")
                .desc("User name of the UC4 user")
                .hasArg(true)
                .argName("USERNAME")
                .required(true)
                .build();
        Option uc4pwd = Option.builder("uc4pwd")
                .desc("Password for the UC4")
                .hasArg(true)
                .argName("PASSWORD")
                .required(true)  // Mark as required
                .build();
        Option uc4Dpt = Option.builder("dept")
                .longOpt("uc4Department")
                .desc("Department of UC4 user")
                .hasArg(true)
                .argName("DPT")
                .required(true)
                .build();
        Option host = Option.builder("h")
                .longOpt("host")
                .desc("Hostname or IP of the AE server")
                .hasArg(true)
                .argName("HOSTNAME")
                .required(true)
                .build();
        Option port = Option.builder("p")
                .longOpt("port")
                .desc("Port for UC4 CP")
                .hasArg(true)
                .argName("NUMBER")
                .required(true)
                .build();
        Option client = Option.builder("c")
                .longOpt("client")
                .desc("UC4 client number")
                .hasArg(true)
                .argName("NUMBER")
                .required(true)
                .build();
        Option dbUser = Option.builder("DBUser")
                .desc("User name for the database")
                .hasArg(true)
                .argName("USERNAME")
                .required(true)
                .build();

        Option dbPwd = Option.builder("DBPwd")
                .desc("Password for the database user")
                .hasArg(true)
                .argName("PASSWORD")
                .required(true)
                .build();
        Option arKey = Option.builder("key")
                .longOpt("archiveKey")
                .desc("Archive key for the tasks to be considered '%' for all")
                .hasArg(true)
                .argName("ARCHIVE_KEY")
                .required(true)
                .build();
        Option jdbc = Option.builder("jdbc")
                .desc("JDBC URL")
                .hasArg(true)
                .argName("URL")
                .required(true)
                .build();
        Option days = Option.builder("n")
                .desc("Number of days to be excluded from today,default is 30")
                .hasArg(true)
                .argName("NUMBER")
                .required(false)
                .build();
        options.addOption(dbUser);
        options.addOption(dbPwd);
        options.addOption(arKey);
        options.addOption(days);
        options.addOption(uc4U);
        options.addOption(uc4pwd);
        options.addOption(client);
        options.addOption(uc4Dpt);
        options.addOption(host);
        options.addOption(port);
        options.addOption(jdbc);

        return options;
    }


}


