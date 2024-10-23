package com.vegaflare;

import com.vegaflare.utils.Logger;

import java.sql.*;
import java.time.LocalDate;

import static java.lang.System.exit;


public class DBItg {
    private Connection connection;
    public Connection getConnection() {return connection;}

    public DBItg(String user, String pass, String url) {

        try {
            if (url.startsWith("jdbc:oracle")){
                Class.forName("oracle.jdbc.driver.OracleDriver");
            }else {
                Class.forName("org.postgresql.Driver");
            }
        } catch (ClassNotFoundException e) {
            Logger.logError(e.getMessage());
            exit(16);
        }

        try {
            this.connection = DriverManager.getConnection(url, user, pass);
            if(this.connection.isValid(20)){
                Logger.logInfo("Connected to DB");
            }
        } catch (SQLException e) {
            Logger.logError(e.getMessage());
            exit(17);
            connection = null; // this will never be reached
        }

    }


    public ResultSet runStatement(String query) {
        Statement statement = null;
        try {
            statement = this.connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        } catch (SQLException e) {
            Logger.logError(e.getMessage());
            exit(18);
        }
        ResultSet rs = null;
        try {
            rs = statement.executeQuery(query);
        } catch (SQLException e) {
            Logger.logError(e.getMessage());
            exit(19);
        }
        return rs;
    }

    // returns an arrays with the matching runids
    public static int[] getRunIDs(ResultSet rs) throws SQLException {
        rs.last();
        int size = rs.getRow();
        int[] runs = new int[size];
        rs.beforeFirst();
        int i = 0;
        while(rs.next()){
            runs[i] =rs.getInt(2);
            ++i;
        }

        return runs;
    }



    // Function to generate and return query on the go
    public static String getQuery(int days, String archiveKeyValue, String status, String dbType, int client, boolean ignore) {
        LocalDate lastDay = LocalDate.now().minusDays(days);
        String ignoreComments = ignore? "" : "AND (acmt.acmt_content IS NULL OR acmt.acmt_content <> 'IGNORE')";

            if(archiveKeyValue.equals("*")) {
                if(status.equals("BLOCKED"))
                return "select eh_name, eh_ah_idnr"
                        + "\nfrom eh"
                        + "\nLEFT OUTER JOIN acmt ON eh.eh_ah_idnr = acmt.acmt_ah_idnr"
                        + "\nwhere eh_status " + getStatusCode(status)
                        + "\nand eh_client = " + client
                        + "\nand to_char(eh_starttime, 'YYYY-MM-DD') <= '" + lastDay
                        + "'\nand eh_otype = 'JOBP'"
                        + "\n" + ignoreComments
                        + "\norder by"
                        + "\ncase"
                        + "\n    when eh_starttype = '<JSCH>' then '001'"
                        + "\n    when eh_starttype = '<ONCE>' then '002'"
                        + "\n    when eh_starttype is null then '003'"
                        + "\n    else eh_starttype"
                        + "\n       end";
                else return "select eh_name, eh_ah_idnr"
                        + "\nfrom eh"
                        + "\nLEFT OUTER JOIN acmt ON eh.eh_ah_idnr = acmt.acmt_ah_idnr"
                        + "\nwhere eh_status " + getStatusCode(status)
                        + "\nand eh_client = " + client
                        + "\nand (eh_endtime IS NULL OR to_char(eh_endtime, 'YYYY-MM-DD') <= '" + lastDay
                        + "')\nand eh_otype in ('JOBS','JOBP','JOBF','SCRI')"
                        + "\n" + ignoreComments
                        + "\norder by"
                        + "\ncase"
                        + "\n    when eh_starttype = '<JSCH>' then '001'"
                        + "\n    when eh_starttype = '<ONCE>' then '002'"
                        + "\n    when eh_starttype is null then '003'"
                        + "\n    else eh_starttype"
                        + "\n       end";
            }

            else {
                if(status.equals("BLOCKED"))
                    return "select eh_name, eh_ah_idnr"
                            + "\nfrom eh"
                            + "\nLEFT OUTER JOIN acmt ON eh.eh_ah_idnr = acmt.acmt_ah_idnr"
                            + "\nwhere eh_status " + getStatusCode(status)
                            + "\nand eh_client = " + client
                            + "\nand eh_archive1 = '" + archiveKeyValue
                            + "'\nand to_char(eh_starttime, 'YYYY-MM-DD') <= '" + lastDay
                            + "'\nand eh_otype = 'JOBP'"
                            + "\n" + ignoreComments
                            + "\norder by"
                            + "\ncase"
                            + "\n    when eh_starttype = '<JSCH>' then '001'"
                            + "\n    when eh_starttype = '<ONCE>' then '002'"
                            + "\n    when eh_starttype is null then '003'"
                            + "\n    else eh_starttype"
                            + "\n       end";
                else
                    return "select eh_name, eh_ah_idnr"
                            + "\nfrom eh"
                            + "\nLEFT OUTER JOIN acmt ON eh.eh_ah_idnr = acmt.acmt_ah_idnr"
                            + "\nwhere eh_status " + getStatusCode(status)
                            + "\nand eh_client = " + client
                            + "\nand eh_archive1 = '" + archiveKeyValue
                            + "'\nand (eh_endtime IS NULL OR to_char(eh_endtime, 'YYYY-MM-DD') <= '" + lastDay
                            + "')\nand eh_otype in ('JOBS','JOBP','JOBF','SCRI')"
                            + "\n" + ignoreComments
                            + "\norder by"
                            + "\ncase"
                            + "\n    when eh_starttype = '<JSCH>' then '001'"
                            + "\n    when eh_starttype = '<ONCE>' then '002'"
                            + "\n    when eh_starttype is null then '003'"
                            + "\n    else eh_starttype"
                            + "\n       end";

            }
    }


    public static String getStatusCode(String status)  {
        switch (status) {
            case "ENDED_OK": return "= 1900";
            case "ANY_OK" : return "between 1900 and 1999";
            case "ENDED_NOT_OK": return "= 1800";
            case "ANY_ABEND": return  "between 1800 and 1899";
            case "BLOCKED": return "= 1560";
            default : Logger.logError( "Status type '" + status + "' is not supported/valid"); exit(12);
            return ""; // this line will never be reached
        }
    }

}
