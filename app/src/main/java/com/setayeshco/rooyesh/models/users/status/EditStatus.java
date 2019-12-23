package com.setayeshco.rooyesh.models.users.status;

/**
 * Created by abderrahimelimame on 6/9/16.
 * Email : abderrahim.elimame@gmail.com
 */

public class EditStatus {
    private String newStatus;
    private String password;
    private String std_number;
    private int statusID;
    private int statusisProf;



    public int isStatusisProf() {
        return statusisProf;
    }

    public void setStatusisProf(int statusisProf) {
        this.statusisProf = statusisProf;
    }



    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getStdNumber() {
        return std_number;
    }

    public void setStdNumber(String std_number) {
        this.std_number = std_number;
    }



    public String getNewStatus() {
        return newStatus;
    }

    public void setNewStatus(String newStatus) {
        this.newStatus = newStatus;
    }

    public int getStatusID() {
        return statusID;
    }

    public void setStatusID(int statusID) {
        this.statusID = statusID;
    }
}
