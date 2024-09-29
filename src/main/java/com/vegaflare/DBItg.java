package com.vegaflare;

import com.vegaflare.exceptions.InvalidParameterException;
import com.vegaflare.utils.Logger;

import java.sql.*;
import java.time.LocalDate;
import java.time.temporal.TemporalAmount;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class DBItg {
    private final Connection connection;

    public Connection getConnection() {return connection;}

    public DBItg(String user, String pass, String url) throws ClassNotFoundException, SQLException, InvalidParameterException {
        Class.forName("org.postgresql.Driver");


        this.connection = DriverManager.getConnection(url, user, pass);
        if(this.connection.isValid(20)){
            Logger.logInfo("Connected to DB");
        }
        //String query = getQuery(0, "*", "ANY_ABEND", "postgres", 7);
        //System.out.println(query);
        //ResultSet rs = runStatement(query);
        //int[] runNumbers = getRunIDs(rs);
    }
    public ResultSet runStatement(String query) throws SQLException {
        Statement statement = this.connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        //        while (resultSet.next()) {
//            System.out.println(resultSet.getArray(1)+" : "+ resultSet.getArray(2));
//        }

        return statement.executeQuery(query);
    }

    // returns an arrays with the matching runids
    public static int[] getRunIDs(ResultSet rs) throws SQLException {
        rs.last();
        int size = rs.getRow();
        int runs[] = new int[size];
        rs.beforeFirst();
        int i = 0;
        while(rs.next()){
            runs[i] =rs.getInt(2);
            ++i;
        }

        System.out.println("Number of iterations: "+i);
        System.out.println("First: "+runs[0]);
        System.out.println("Last: "+runs[size-1]);
        return runs;
    }



    // Function to generate and return query on the go
    public static String getQuery(int days, String archiveKeyValue, String status, String dbType, int client) throws InvalidParameterException {
        LocalDate lastday = LocalDate.now().minusDays(days);
            if(archiveKeyValue.equals("*")) {
                if(status.equals("BLOCKED"))
                return "select eh_name, eh_ah_idnr"
                        + "\nfrom eh"
                        + "\nwhere eh_status " + getStatusCode(status)
                        + "\nand eh_client = " + client
                        + "\nand to_char(eh_starttime, 'YYYY-MM-DD') <= '" + lastday
                        + "'\nand eh_otype = 'JOBP'"
                        + "\norder by"
                        + "\ncase"
                        + "\n    when eh_starttype = '<JSCH>' then '001'"
                        + "\n    when eh_starttype = '<ONCE>' then '002'"
                        + "\n    when eh_starttype is null then '003'"
                        + "\n    else eh_starttype"
                        + "\n       end;";
                else return "select eh_name, eh_ah_idnr"
                        + "\nfrom eh"
                        + "\nwhere eh_status " + getStatusCode(status)
                        + "\nand eh_client = " + client
                        + "\nand to_char(eh_endtime, 'YYYY-MM-DD') <= '" + lastday
                        + "'\nand eh_otype in ('JOBS','JOBP','JOBF','SCRI')"
                        + "\norder by"
                        + "\ncase"
                        + "\n    when eh_starttype = '<JSCH>' then '001'"
                        + "\n    when eh_starttype = '<ONCE>' then '002'"
                        + "\n    when eh_starttype is null then '003'"
                        + "\n    else eh_starttype"
                        + "\n       end;";
            }

            else {
                if(status.equals("BLOCKED"))
                    return "select eh_name, eh_ah_idnr"
                            + "\nfrom eh"
                            + "\nwhere eh_status " + getStatusCode(status)
                            + "\nand eh_client = " + client
                            + "\nand eh_archive1 = '" + archiveKeyValue
                            + "'\nand to_char(eh_starttime, 'YYYY-MM-DD') <= '" + lastday
                            + "'\nand eh_otype = 'JOBP"
                            + "\norder by"
                            + "\ncase"
                            + "\n    when eh_starttype = '<JSCH>' then '001'"
                            + "\n    when eh_starttype = '<ONCE>' then '002'"
                            + "\n    when eh_starttype is null then '003'"
                            + "\n    else eh_starttype"
                            + "\n       end;";
                else
                    return "select eh_name, eh_ah_idnr"
                            + "\nfrom eh"
                            + "\nwhere eh_status " + getStatusCode(status)
                            + "\nand eh_client = " + client
                            + "\nand eh_archive1 = '" + archiveKeyValue
                            + "'\nand to_char(eh_endtime, 'YYYY-MM-DD') <= '" + lastday
                            + "'\nand eh_otype in ('JOBS','JOBP','JOBF','SCRI')"
                            + "\norder by"
                            + "\ncase"
                            + "\n    when eh_starttype = '<JSCH>' then '001'"
                            + "\n    when eh_starttype = '<ONCE>' then '002'"
                            + "\n    when eh_starttype is null then '003'"
                            + "\n    else eh_starttype"
                            + "\n       end;";

            }
    }

    public static String getStatusCode(String status) throws InvalidParameterException {
        return switch (status) {
            case "ENDED_OK" -> "= 1900";
            case "ANY_OK" -> "between 1900 and 1999";
            case "ENDED_NOT_OK" -> "= 1800";
            case "ANY_ABEND" -> "between 1800 and 1899";
            case "BLOCKED" -> "= 1560";
            default -> throw new InvalidParameterException("Status type '" + status + "' is not supported/valid");
        };
    }

}
