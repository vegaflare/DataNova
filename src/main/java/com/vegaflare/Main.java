package com.vegaflare;

import com.vegaflare.utils.Initializer;
import com.vegaflare.utils.Logger;

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

        if(tasks.length != 0) {
            // check if conditions valid for cancel, deactivation is a must either way
            if (init.getOperation().equals("C") && init.getStatus().equals("BLOCKED"))
                init.getAutomicInt().cancelTasks(tasks);
            init.getAutomicInt().deactivateTasks(tasks);
        }else{
            Logger.logInfo("Query returned zero results");
        }
        init.close();
    }
}