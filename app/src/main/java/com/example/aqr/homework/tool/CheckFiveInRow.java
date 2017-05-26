package com.example.aqr.homework.tool;


import android.graphics.Point;

import java.util.ArrayList;

/**
 * Created by Aqr on 2017/5/24.
 */

public class CheckFiveInRow {
    private final  int MAX_STONE = 5;
    public CheckFiveInRow() {
    }

    public boolean isFiveInRow(ArrayList<Point> points) {
        for (Point p : points) {
            int x = p.x;
            int y = p.y;
            if (checkPoints(x, y, points)) {
                return true;
            }
        }

        return false;
    }

    private boolean checkPoints(int x, int y, ArrayList<Point> points) {
        int count = 1;

        if(count>=5){
            return true;
        }
        for (int i = 1; i < MAX_STONE; i++) {
            if (points.contains(new Point(x - i, y)) || points.contains(new Point(x + i, y)) ||
                    points.contains(new Point(x, y - i)) || points.contains(new Point(x, y + i)) ||
                    points.contains(new Point(x - i, y - i)) || points.contains(new Point(x + i, y + i)) ||
                    points.contains(new Point(x + i, y - i)) || points.contains(new Point(x - i, y + i))) {
                count++;
            } else {
                break;
            }
        }
        if (count == MAX_STONE) {
            return true;
        }

        return false;

    }


}
