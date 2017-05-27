package com.example.aqr.homework.tool;


import android.graphics.Point;

import java.util.ArrayList;

/**
 * Created by Aqr on 2017/5/24.
 */

public class GameOverManager {
    private final int MAX_STONE = 5;

    public GameOverManager() {
    }

    public boolean isFiveInRow(ArrayList<Point> points) {
        for (Point p : points) {
            int x = p.x;
            int y = p.y;
         if(checkHorizantol(x, y, points)||checkVertical(x, y, points)||checkLeftDiagonal(x, y, points)||checkRightDiagonal(x, y, points)){
             return  true;
         }
        }

        return false;
    }

    private boolean checkHorizantol(int x, int y, ArrayList<Point> points){
        Point point = new Point(x, y);
        int count = 1;
        point.offset(1,0);
        while (points.contains(point)&&count<MAX_STONE){
            count++;
            point.offset(1,0);
        }
        point.set(x, y);
        point.offset(-1, 0);
        while (points.contains(point)&&count <MAX_STONE){
            count++;
            point.offset(-1,0);
        }
        if(count == MAX_STONE){
            return  true;
        }

        return false;
    }

    private boolean checkVertical(int x, int y, ArrayList<Point> points){
        Point point = new Point(x, y);
        int count = 1;
        point.offset(0,1);
        while (points.contains(point)&&count<MAX_STONE){
            count++;
            point.offset(0,1);
        }
        point.set(x, y);
        point.offset(0, -1);
        while (points.contains(point)&&count <MAX_STONE){
            count++;
            point.offset(0,-1);
        }
        if(count == MAX_STONE){
            return  true;
        }

        return false;
    }

    private boolean checkLeftDiagonal(int x, int y, ArrayList<Point> points){
        Point point = new Point(x, y);
        int count = 1;
        point.offset(1,-1);
        while (points.contains(point)&&count<MAX_STONE){
            count++;
            point.offset(1,-1);
        }
        point.set(x, y);
        point.offset(-1, 1);
        while (points.contains(point)&&count <MAX_STONE){
            count++;
            point.offset(-1,1);
        }
        if(count == MAX_STONE){
            return  true;
        }

        return false;
    }

    private boolean checkRightDiagonal(int x, int y, ArrayList<Point> points){
        Point point = new Point(x, y);
        int count = 1;
        point.offset(1,1);
        while (points.contains(point)&&count<5){
            count++;
            point.offset(1,1);
        }
        point.set(x, y);
        point.offset(-1, -1);
        while (points.contains(point)&&count <5){
            count++;
            point.offset(-1,-1);
        }
        if(count == 5){
            return  true;
        }

        return false;
    }




}
