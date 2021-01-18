package com.app.triponeer;

public class User {
    public String accName;
    public String accEmail;

    public String getAccName() {
        return accName;
    }

    public void setAccName(String accName) {
        this.accName = accName;
    }

    public String getAccEmail() {
        return accEmail;
    }

    public void setAccEmail(String accEmail) {
        this.accEmail = accEmail;
    }

    public User()
    {
        accName="";
        accEmail="";
    }
    public User(String accName, String accEmail) {
        this.accName = accName;
        this.accEmail = accEmail;
    }

}
