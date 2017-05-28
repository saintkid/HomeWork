package com.example.aqr.homework.domain;

import cn.bmob.v3.BmobObject;

/**
 * Created by Aqr on 2017/5/28.
 */

public class User extends BmobObject {
    private String name;
    private String password;
    public User(){

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
