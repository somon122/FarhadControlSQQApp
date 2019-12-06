package com.farhad.questionpanel.AdminPanel;

public class BlockUserClass {


    String uId;
    String userName;
    String userEmail;

    public BlockUserClass() {
    }

    public BlockUserClass(String uId, String userName, String userEmail) {
        this.uId = uId;
        this.userName = userName;
        this.userEmail = userEmail;
    }

    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }
}
