package com.example.aqr.homework.tool;

import android.graphics.Point;

/**
 * Created by Aqr on 2017/5/26.
 */

public class SecondAIResult implements Comparable<SecondAIResult> {

    //活4
    int alive4 = 0;
    //活3数量
    int alive3 = 0;
    //半活4，一头封的
    int halfAlive4 = 0;
    //半活3，一头封的
    int halfAlive3 = 0;
    //活2数量
    int alive2 = 0;
    //点位
    Point point;

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((point == null) ? 0 : point.hashCode());
        return result;
    }


    @Override
    public boolean equals(Object obj) {
        SecondAIResult other = (SecondAIResult) obj;
        if (point == null) {
            if (other.point != null)
                return false;
        } else if (!point.equals(other.point))
            return false;
        return true;
    }

    public SecondAIResult(Point point) {
        this.point = point;
    }

    @Override
    public int compareTo(SecondAIResult another) {
        return compareTowResult(this, another);
    }

    public int compareTowResult(SecondAIResult oneResult, SecondAIResult another) {
        if (oneResult.alive4 > another.alive4) {
            return -1;
        }
        if (oneResult.alive4 < another.alive4) {
            return 1;
        }
        if (oneResult.halfAlive4 > another.halfAlive4) {
            return -1;
        }
        if (oneResult.halfAlive4 < another.halfAlive4) {
            return 1;
        }
        if (oneResult.alive3 > another.alive3) {
            return -1;
        }
        if (oneResult.alive3 < another.alive3) {
            return 1;
        }
        if (oneResult.alive2 > another.alive2) {
            return -1;
        }
        if (oneResult.alive2 < another.alive2) {
            return 1;
        }
        if (oneResult.halfAlive3 > another.halfAlive3) {
            return -1;
        }
        if (oneResult.halfAlive3 > another.halfAlive3) {
            return 1;
        }
        return 0;
    }

}
