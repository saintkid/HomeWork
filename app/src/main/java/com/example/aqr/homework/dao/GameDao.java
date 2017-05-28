package com.example.aqr.homework.dao;

import android.graphics.Point;

import com.example.aqr.homework.tool.GameBroad;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by Aqr on 2017/5/28.
 */

public class GameDao {
    public GameDao(){

    }
    //User manager
    public boolean selectUser(String name, String password){
        return false;
    }
    public boolean saveUser(String name, String password){
        return false;
    }
    //GameBroad manager
    public ArrayList<GameBroad> selectGameBroad(){
        return  null;
    }
    public void saveGameBroad(String name, int time, int numbers){

    }
    //SaveGame manager
    public ArrayList<Point> selectSaveGame(String name, int type){
        return null;
    }
    public void saveGame(String name,int type, ArrayList<Point> points){

    }


}
