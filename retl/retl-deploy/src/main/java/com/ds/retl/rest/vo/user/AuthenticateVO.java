package com.ds.retl.rest.vo.user;

/**
 * Created by john on 2017/10/6.
 */
public class AuthenticateVO {
    String user, password;

    public void setUser(String user) {
        this.user = user;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }
}
