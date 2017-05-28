package com.example.aqr.homework.tool;

import android.graphics.Point;

import java.util.Random;

/**
 * Created by Aqr on 2017/5/26.
 */

public class PointGrade implements Comparable<PointGrade> {
    int maxPointGrade;
    Point bestPoint;

    public PointGrade() {

    }


    @Override
    public int compareTo(PointGrade another) {
        if (this.maxPointGrade > another.maxPointGrade) {
            return -1;
        }
        if (this.maxPointGrade < another.maxPointGrade) {
            return 1;
        }
        if(this.maxPointGrade == another.maxPointGrade){
            int radon = new Random().nextInt(10);
            if(radon<=4){
                return -1;
            }
            else{
                return 1;
            }
        }
        return 0;
    }


}
