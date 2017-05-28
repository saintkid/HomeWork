package com.example.aqr.homework.domain;

import cn.bmob.v3.BmobObject;

/**
 * Created by Aqr on 2017/5/28.
 */

public class GameBroad extends BmobObject {
    private String name;
    private int time;
    private int moveNumbers;
    public GameBroad(){

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public int getMoveNumbers() {
        return moveNumbers;
    }

    public void setMoveNumbers(int moveNumbers) {
        this.moveNumbers = moveNumbers;
    }
}
