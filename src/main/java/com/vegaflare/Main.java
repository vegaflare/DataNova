package com.vegaflare;

import com.vegaflare.utils.Initializer;
import com.vegaflare.utils.Logger;


public class Main {
    public static void main(String[] args) throws Exception {

        Initializer init = new Initializer(args);
        String query = DBItg.getQuery(init.getKeepDays(),
                init.getArchiveKey(), init.getStatus(),
                "doesn't matter yet",
                init.getUc4Client(), init.getIgnoreExempted());
        int[] tasks = DBItg.getRunIDs(init.getDBItg().runStatement(query));
        init.getDBItg().getConnection().close();

        if(tasks.length != 0) {
            // check if conditions valid for cancel, deactivation is a must either way
            if (init.getOperation().equals("C")) {init.getAutomicInt().cancelTasks(tasks);}
            init.getAutomicInt().deactivateTasks(tasks);
        }else{
            Logger.logInfo("Query returned zero results, nothing to be done");
        }
        init.close();
    }
}