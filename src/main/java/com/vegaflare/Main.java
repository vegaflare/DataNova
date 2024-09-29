package com.vegaflare;

import com.vegaflare.utils.Initializer;

import java.io.IOException;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) throws IOException, SQLException, ClassNotFoundException {

        Initializer init = new Initializer(args);
        init.close();
    }
}