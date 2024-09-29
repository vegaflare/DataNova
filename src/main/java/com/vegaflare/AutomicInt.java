package com.vegaflare;

import com.uc4.api.SearchResultItem;
import com.uc4.api.UC4ObjectName;
import com.uc4.api.objects.CustomAttribute;
import com.uc4.api.objects.Job;
import com.uc4.api.objects.UC4Object;
import com.uc4.communication.Connection;
import com.uc4.communication.ConnectionAttributes;
import com.uc4.communication.requests.*;
import com.vegaflare.utils.Logger;

import java.io.IOException;
import java.util.Iterator;
import java.util.Objects;

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








    public boolean saveObject(UC4Object obj) throws IOException {
        SaveObject save = new SaveObject(obj);
        uc4.sendRequestAndWait(save);
        //Test if save was successful
        if (save.getMessageBox() != null) {
            Logger.logError(save.getMessageBox().getText());
            failCounter++;
            return false;
        } else {
            return true;
        }
    }

}

