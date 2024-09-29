package com.vegaflare;

import com.vegaflare.utils.Initializer;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) throws Exception {

        Initializer init = new Initializer(args);
        String query = DBItg.getQuery(init.getKeepDays(),
                init.getArchiveKey(), init.getStatus(),
                "doesn't matter yet",
                init.getUc4Client());
        int[] tasks = DBItg.getRunIDs(init.getDBItg().runStatement(query));

        // check if conditions valid for cancel or deactivation
        if(init.getOperation().equals("C") && init.getStatus().equals("BLOCKED"))
            init.getAutomicInt().cancelTasks(tasks);
        else
            init.getAutomicInt().deactivateTasks(tasks);
        init.close();
    }
}