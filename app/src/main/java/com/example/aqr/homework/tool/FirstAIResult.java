package com.example.aqr.homework.tool;

import android.graphics.Point;

/**
 * Created by Aqr on 2017/5/25.
 */

public class FirstAIResult {
    int count;
    //点位
    Point point;
    //方向
    int direction;
    //状态
    int aliveState;
    public FirstAIResult(int count, Point point, int direction) {
        this(count, point, direction, FiveInRowAI.ALIVE);
    }

    public FirstAIResult(int count, Point point, int direction,int aliveState) {
        this.count = count;
        this.point = point;
        this.direction = direction;
        this.aliveState = aliveState;
    }



    public FirstAIResult init(Point point,int direction,int aliveState){
        this.count = 1;
        this.point = point;
        this.direction = direction;
        this.aliveState = aliveState;
        return this;
    }

    public FirstAIResult cloneResult(){
        return new FirstAIResult(count, point, direction,aliveState);
    }
}
