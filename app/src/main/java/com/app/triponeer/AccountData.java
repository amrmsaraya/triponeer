package com.app.triponeer;

public class AccountData {
    private String accFaceBookName;
    private String accFaceBookEmail;
    private String accFaceBookUrlPhoto;
    private String accName;
    private String accEmail;
    private String accUrlPhoto;
    private String accPassword;

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

    public String getAccUrlPhoto() {
        return accUrlPhoto;
    }

    public void setAccUrlPhoto(String accUrlPhoto) {
        this.accUrlPhoto = accUrlPhoto;
    }

    public AccountData()
    {
        accFaceBookName ="";
        accFaceBookEmail ="";
        accFaceBookUrlPhoto ="";
        accPassword="";
    }

    public AccountData(String accEmail, String accPassword) {
        this.accEmail = accEmail;
        this.accPassword = accPassword;
    }

    public AccountData(String accName, String accEmail, String accPassword) {
        this.accName = accName;
        this.accEmail = accEmail;
        this.accPassword = accPassword;
    }

    public String getAccFaceBookName() {
        return accFaceBookName;
    }

    public void setAccFaceBookName(String accFaceBookName) {
        this.accFaceBookName = accFaceBookName;
    }

    public String getAccFaceBookEmail() {
        return accFaceBookEmail;
    }

    public void setAccFaceBookEmail(String accFaceBookEmail) {
        this.accFaceBookEmail = accFaceBookEmail;
    }

    public String getAccFaceBookUrlPhoto() {
        return accFaceBookUrlPhoto;
    }

    public void setAccFaceBookUrlPhoto(String accFaceBookUrlPhoto) {
        this.accFaceBookUrlPhoto = accFaceBookUrlPhoto;
    }

    public String getAccPassword() {
        return accPassword;
    }

    public void setAccPassword(String accPassword) {
        this.accPassword = accPassword;
    }
}
