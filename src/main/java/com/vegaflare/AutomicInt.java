package com.vegaflare;

import com.uc4.api.objects.CustomAttribute;
import com.uc4.api.objects.UC4Object;
import com.uc4.communication.Connection;
import com.uc4.communication.ConnectionAttributes;
import com.uc4.communication.requests.*;
import com.vegaflare.utils.Logger;

import java.io.IOException;

public class AutomicInt {

    private Connection uc4;
    private boolean isLoginSuccessful;
    private int failCounter;
    private CustomAttribute refCA;

    public boolean isLoginSuccessful() {
        return isLoginSuccessful;
    }

    public void close() throws IOException {
        uc4.close();
    }

    public AutomicInt(String host, int port, String user, String dept, String pass, int client) {


        try {
            uc4 = Connection.open(host, port);
            CreateSession login = uc4.login(client, user, dept, pass, 'E');
            ConnectionAttributes conAttr = uc4.getSessionInfo();
            String srvName = conAttr.getServerName();

            //Test if the login was successful
            isLoginSuccessful = login.isLoginSuccessful();
            if (!isLoginSuccessful) {
                Logger.logError(login.getMessageBox().getText());
                uc4.close();
                //exit(5);
            } else {
                Logger.logInfo("Connected to AE at :'" + client + "->" + srvName + "' successfully.");
            }
        } catch (IOException e) {
            Logger.logError(e.getMessage());
            isLoginSuccessful = false;
        }


    }

public void deactivateTasks(int[] tasks) throws IOException {
       DeactivateTask dt = new DeactivateTask(tasks,true);
       Logger.logInfo("Deactivating "+tasks.length +" tasks.");
       uc4.sendRequestAndWait(dt);

       if(dt.getMessageBox()!= null){
           Logger.logError(dt.getMessageBox().getText());
           failCounter++;
       } else {
           Logger.logInfo("Tasks deactivated successfully.");
       }
}

    public void cancelTasks(int[] tasks) throws IOException {
        CancelTask cancel= new CancelTask(tasks,true);
        Logger.logInfo("Cancelling "+tasks.length +" tasks.");
        uc4.sendRequestAndWait(cancel);

        if(cancel.getMessageBox()!= null){
            Logger.logError(cancel.getMessageBox().getText());
            failCounter++;
        } else {
            Logger.logInfo("Tasks cancelled successfully.");
        }
    }


}

