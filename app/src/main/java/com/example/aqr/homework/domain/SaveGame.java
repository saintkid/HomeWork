package com.example.aqr.homework.domain;

import cn.bmob.v3.BmobObject;

/**
 * Created by Aqr on 2017/5/28.
 */

public class SaveGame extends BmobObject {
    private int xPoint;
    private int yPoint;
    private int type;
    private String name;

    public SaveGame(){

    }

    public int getxPoint() {
        return xPoint;
    }

    public void setxPoint(int xPoint) {
        this.xPoint = xPoint;
    }

    public int getyPoint() {
        return yPoint;
    }

    public void setyPoint(int yPoint) {
        this.yPoint = yPoint;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
