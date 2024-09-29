package com.vegaflare;

import com.vegaflare.utils.Logger;

import java.sql.*;


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

}
