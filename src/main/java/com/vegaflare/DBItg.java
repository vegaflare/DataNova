package com.vegaflare;

import com.vegaflare.utils.Logger;

import java.sql.*;
import java.time.LocalDate;
import java.time.temporal.TemporalAmount;


public class DBItg {
    private Connection connection;

    public Connection getConnection() {return connection;}

    public DBItg(String user, String pass, String url) throws ClassNotFoundException, SQLException {
        Class.forName("org.postgresql.Driver");


        connection = DriverManager.getConnection(url, user, pass);
        if(connection.isValid(20)){
            Logger.logInfo("Connected to DB");
        }
        runStatement();



    }
    public void runStatement() throws SQLException {
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("Select eh_ah_idnr from EH;");
        while (resultSet.next()) {
            System.out.println("Value:" + resultSet.getArray(1));
        }

    }

    // Function to generate and return query on the go
    public String getQuery(String opc, int days, String archiveKeyValue, String status, String dbType, int client){
        LocalDate lastday = LocalDate.now().minusDays(days);
            // if opcode is deactivate
            return "select eh_name, eh_ah_idnr"
            +"from eh"
            +"where eh_status in " + getStatusCode(status)
            +" and eh_client = "+client
            +"and to_char(eh_endtime, 'YYYYMMDD') < " + lastday
            +"and eh_otype in ('JOBS','JOBP','JOBF','SCRI')"
            +"order by"
            +"case"
            +"    when eh_starttype = '<JSCH>' then '001'"
            +"    when eh_starttype = '<ONCE>' then '002'"
            +"    when eh_starttype is null then '003'"
            +"else eh_starttype"
            +"        end;";

    }

    public String getStatusCode(String status){
        switch (status){
            case "ENDED_OK": return "= 1900";
            case "ANY_OK"  : return "between 1900 and 1999";
            case "ENDED_NOT_OK": return "= 1800";
            case "ANY_ABEND"  : return "between 1800 and 1899";
        }
        return status;
    }

}
