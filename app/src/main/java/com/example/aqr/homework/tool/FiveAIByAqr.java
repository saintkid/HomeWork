package com.example.aqr.homework.tool;

import android.graphics.Point;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * Created by Aqr on 2017/5/26.
 */

public class FiveAIByAqr {
    private ArrayList<Point> computerPoints = new ArrayList<>();
    private ArrayList<Point> humanPoints = new ArrayList<>();
    private ArrayList<Point> freePoints = new ArrayList<>();
    // private int freePointNumbers = 0;

    private ArrayList<PointGrade> finalBestPoints = new ArrayList<>();

    private ArrayList<PointGrade> humanBestPoints = new ArrayList<>();
    private ArrayList<PointGrade> computerBestPoints = new ArrayList<>();
    private PointGrade human = new PointGrade();
    private PointGrade computer = new PointGrade();

    private static final int MAX_POINT = 15;
    private static final int RANGE_STEP = 1;

    private boolean isSideFree = false;
    private boolean isOtherSideFree = false;

    private class AIRange {
        int xStart, yStart, xStop, yStop;

        private AIRange(int xStart, int yStart, int xStop, int yStop) {
            this.xStart = xStart;
            this.yStart = yStart;
            this.xStop = xStop;
            this.yStop = yStop;
        }
    }

    AIRange currentRange = new AIRange(0, 0, 0, 0);


    private void initRange(ArrayList<Point> computerPoints, ArrayList<Point> humanPoints) {
        currentRange.xStart = humanPoints.get(0).x - RANGE_STEP;
        currentRange.yStart = humanPoints.get(0).y - RANGE_STEP;
        currentRange.xStop = humanPoints.get(0).x + RANGE_STEP;
        currentRange.yStop = humanPoints.get(0).y + RANGE_STEP;
        for (Point point : humanPoints) {
            if (point.x - RANGE_STEP < currentRange.xStart) {
                currentRange.xStart = point.x - RANGE_STEP;
            } else if (point.x + RANGE_STEP > currentRange.xStop) {
                currentRange.xStop = point.x + RANGE_STEP;
            }
            if (point.y - RANGE_STEP < currentRange.yStart) {
                currentRange.yStart = point.y - RANGE_STEP;
            } else if (point.y + RANGE_STEP > currentRange.yStop) {
                currentRange.yStop = point.y + RANGE_STEP;
            }
        }
        for (Point point : computerPoints) {
            if (point.x - RANGE_STEP < currentRange.xStart) {
                currentRange.xStart = point.x - RANGE_STEP;
            } else if (point.x + RANGE_STEP > currentRange.xStop) {
                currentRange.xStop = point.x + RANGE_STEP;
            }
            if (point.y - RANGE_STEP < currentRange.yStart) {
                currentRange.yStart = point.y - RANGE_STEP;
            } else if (point.y + RANGE_STEP > currentRange.yStop) {
                currentRange.yStop = point.y + RANGE_STEP;
            }
        }

        //如果范围扩大后超过了棋盘，则等于棋盘
        currentRange.xStart = currentRange.xStart < 0 ? 0 : currentRange.xStart;
        currentRange.yStart = currentRange.yStart < 0 ? 0 : currentRange.yStart;
        currentRange.xStop = currentRange.xStop >= MAX_POINT ? MAX_POINT - 1 : currentRange.xStop;
        currentRange.yStop = currentRange.yStop >= MAX_POINT ? MAX_POINT - 1 : currentRange.yStop;
    }

    public FiveAIByAqr() {

    }

    public void setParams(ArrayList<Point> computerPoints, ArrayList<Point> humanPoints, ArrayList<Point> freePoints) {
        //refresh();
        this.computerPoints = computerPoints;
        this.humanPoints = humanPoints;
        this.freePoints = freePoints;
        //Log.d("Test","humanPoint: "+humanPoints.get(humanPoints.size()-1));
        Log.d("Test", "humanPointSize: " + humanPoints.size());
        initRange(computerPoints, humanPoints);
    }

    private void refresh() {


        humanBestPoints.clear();
        computerBestPoints.clear();
        finalBestPoints.clear();
        Log.d("Test", "refresh successful");

    }

    public Point doFirstAI() {
        int hGrade, vGrade, rDGrade, lDGrade;

        for (Point point : freePoints) {
            if (point.x < currentRange.xStart || point.x > currentRange.xStop || point.y < currentRange.yStart || point.y > currentRange.yStop) {
                continue;
            }

            hGrade = getHorizantolMaxGrade(point, computerPoints, freePoints);
            if (hGrade > 9) {
                return point;
            }
            vGrade = getVerticalMaxGrade(point, computerPoints, freePoints);
            if (vGrade > 9) {
                return point;
            }
            rDGrade = getRightDiagonalMaxGrade(point, computerPoints, freePoints);
            if (rDGrade > 9) {
                return point;
            }
            lDGrade = getLeftDiagonalMaxGrade(point, computerPoints, freePoints);
            if (lDGrade > 9) {
                return point;
            }
            Log.d("Test", "AI:" + "\n" + "HGrade: " + hGrade + "\t" + "VGrade: " + vGrade + "\t" + "RDGrade: " + rDGrade + "\t" + "LDGrade: " + lDGrade + "\n");
            computer.maxPointGrade = compareGrade(hGrade, vGrade, rDGrade, lDGrade);
            computer.bestPoint = point;
            if (!computerBestPoints.contains(computer)) {
                computerBestPoints.add(computer);
                computer = new PointGrade();
            }


            Log.d("Test", "AIGrade: " + computer.maxPointGrade + "\t" + "AIPoint: " + computer.bestPoint);

            hGrade = getHorizantolMaxGrade(point, humanPoints, freePoints);
            vGrade = getVerticalMaxGrade(point, humanPoints, freePoints);
            rDGrade = getRightDiagonalMaxGrade(point, humanPoints, freePoints);
            lDGrade = getLeftDiagonalMaxGrade(point, humanPoints, freePoints);
            Log.d("Test", "HUMAN:" + "\n" + "HGrade: " + hGrade + "\t" + "VGrade: " + vGrade + "\t" + "RDGrade: " + rDGrade + "\t" + "LDGrade: " + lDGrade + "\n");
            human.maxPointGrade = compareGrade(hGrade, vGrade, rDGrade, lDGrade);
            human.bestPoint = point;
            if (!humanBestPoints.contains(human)) {
                humanBestPoints.add(human);
                human = new PointGrade();
            }


        }
        return null;
    }

    private Point doSecondAI() {
        for (int i = 0; i < humanBestPoints.size(); i++) {
            Log.d("TestBest", "BestHumanGrade: " + humanBestPoints.get(i).maxPointGrade + "\t" + "HumanPoint: " + humanBestPoints.get(i).bestPoint);
            Log.d("TestBest", "BestAIGrade: " + computerBestPoints.get(i).maxPointGrade + "\t" + "AIPoint: " + computerBestPoints.get(i).bestPoint);
        }

        for (int i = 0; i < humanBestPoints.size(); i++) {
            human = humanBestPoints.get(i);
            computer = computerBestPoints.get(i);
            PointGrade temp = (human.maxPointGrade > computer.maxPointGrade) ? human : computer;
            Log.d("TestfinalBest", "BestPoint: \t" + temp.bestPoint + "BestGrade: \t" + temp.maxPointGrade);
            if (!finalBestPoints.contains(temp)) {
                finalBestPoints.add(temp);
            }

        }
        for (int i = 0; i < finalBestPoints.size(); i++)
            Log.d("TestfinalBest", "BestGrade: " + finalBestPoints.get(i).maxPointGrade + "\t" + "AIPoint: " + finalBestPoints.get(i).bestPoint);

        Collections.sort(finalBestPoints);
        for (int i = 0; i < finalBestPoints.size(); i++)
            Log.d("TestfinalBest", "BestGrade: " + finalBestPoints.get(i).maxPointGrade + "\t" + "AIPoint: " + finalBestPoints.get(i).bestPoint);
        return finalBestPoints.get(0).bestPoint;


    }


    public Point getBestPoint() {
        Point bestPoint;
        bestPoint = doFirstAI();
        if (bestPoint != null) {
            return bestPoint;
        }

        bestPoint = doSecondAI();
        refresh();

        return bestPoint;
        // doFirstAI();

    }

    public int compareGrade(final int hGrade, final int vGrade, final int rDGrade, final int lDGrade) {
        ArrayList<Integer> tempInt = new ArrayList<Integer>() {
            {
                add(hGrade);
                add(vGrade);
                add(rDGrade);
                add(lDGrade);
            }
        };

        // int[] tempInt = new int[]{hGrade, vGrade, rDGrade, lDGrade};
        //int[] tempIntTest = new int[]{6, 5, 5, 6};
        //String tempTest = tempIntTest.toString();
        // String temp = tempInt.toString();


        //Log.d("GRADESTRIGN", "Temp: " + tempIntTest.toString() + "/n" + "Indexof: 6" + tempIntTest.contains(6));

        int max = hGrade;
        if (vGrade > max) max = vGrade;
        if (rDGrade > max) max = rDGrade;
        if (lDGrade > max) max = lDGrade;

        if (isRepeat(tempInt, 6)) {
            if (max < 9) max = 9;
        }
        if (tempInt.contains(6) && tempInt.contains(5)) {

            if (max < 9) max = 9;
        }
        if (isRepeat(tempInt, 5)) {
            if (max < 8) max = 8;

        }
        if (tempInt.contains(5) && tempInt.contains(3)) {

            if (max < 7) max = 7;
        }
        if (isRepeat(tempInt, 2)) {

            if (max < 4) max = 4;
        }
        return max;

    }

    public boolean isRepeat(ArrayList<Integer> integers, Integer integer) {
        int repeatNumber = 0;
        for (int i = 0; i < integers.size(); i++) {
            if (integers.get(i) == integer) {
                repeatNumber++;
            }
        }
        if (repeatNumber >= 2) {
            return true;
        }
        return false;
    }

    public int markGrade(int count) {

        if (count == 5) {
            return 10;
        }
        if (isSideFree && isOtherSideFree && count == 4) {
            return 9;
        }
        if ((isOtherSideFree || isSideFree) && count == 4) {
            return 6;
        }
        if (isOtherSideFree && isSideFree && count == 3) {
            return 5;
        }
        if ((isOtherSideFree || isSideFree) && count == 3) {
            return 3;
        }
        if (isOtherSideFree && isSideFree && count == 2) {
            return 2;
        }
        if ((isOtherSideFree || isSideFree) && count == 2) {
            return 1;
        }
        isOtherSideFree = false;
        isSideFree = false;
        return 0;
    }

    public int getHorizantolMaxGrade(Point p, ArrayList<Point> playPoints, ArrayList<Point> freePoints) {
        int count = 1;
        int x = p.x;
        int y = p.y;
        Log.d("Test", "getGrade: \t" + p);
        p.offset(1, 0);

        while (playPoints.contains(p) && count < 5 && p.x < MAX_POINT) {
            Log.d("Test", "isContains: \t" + playPoints.contains(p));
            Log.d("Test", "getGradeoffX(1): \t" + p);
            count++;
            p.offset(1, 0);
        }

        isSideFree = freePoints.contains(p);
        Log.d("Test", "SideFree: \t" + isSideFree);
        p.set(x, y);
        Log.d("Test", "getGrade: \t" + p);
        p.offset(-1, 0);
        while (playPoints.contains(p) && count < 5 && p.x >= 0) {
            Log.d("Test", "isContains: \t" + playPoints.contains(p));
            Log.d("Test", "getGradeoffX(-1): \t" + p);
            count++;
            p.offset(-1, 0);
        }
        isOtherSideFree = freePoints.contains(p);
        Log.d("Test", "OtherSideFree: \t" + isOtherSideFree);
        p.set(x, y);
        return markGrade(count);
    }

    public int getVerticalMaxGrade(Point p, ArrayList<Point> playPoints, ArrayList<Point> freePoints) {
        int count = 1;
        int x = p.x;
        int y = p.y;
        Log.d("Test", "getGrade: \t" + p);

        p.offset(0, 1);
        while (playPoints.contains(p) && count < 5 && p.y < MAX_POINT) {
            Log.d("Test", "isContains: \t" + playPoints.contains(p));
            Log.d("Test", "getGradeoffY(1): \t" + p);
            count++;
            p.offset(0, 1);
        }
        isSideFree = freePoints.contains(p);
        Log.d("Test", "SideFree: \t" + isSideFree);
        p.set(x, y);
        Log.d("Test", "getGrade: \t" + p);
        p.offset(0, -1);
        while (playPoints.contains(p) && count < 5 && p.y >= 0) {
            Log.d("Test", "isContains: \t" + playPoints.contains(p));
            Log.d("Test", "getGradeoffY(-1): \t" + p);
            count++;
            p.offset(0, -1);
        }
        isOtherSideFree = freePoints.contains(p);
        Log.d("Test", "OtherSideFree: \t" + isOtherSideFree);
        p.set(x, y);
        return markGrade(count);
    }

    public int getLeftDiagonalMaxGrade(Point p, ArrayList<Point> playPoints, ArrayList<Point> freePoints) {
        int count = 1;
        int x = p.x;
        int y = p.y;
        Log.d("Test", "getGrade: \t" + p);

        p.offset(1, -1);
        while (playPoints.contains(p) && count < 5 && p.x < MAX_POINT && p.y >= 0) {
            Log.d("Test", "isContains: \t" + playPoints.contains(p));
            Log.d("Test", "getGradeoffL(1,-1): \t" + p);
            count++;
            p.offset(1, -1);
        }
        isSideFree = freePoints.contains(p);
        Log.d("Test", "SideFree: \t" + isSideFree);
        p.set(x, y);
        Log.d("Test", "getGrade: \t" + p);
        p.offset(-1, 1);
        while (playPoints.contains(p) && count < 5 && p.y < MAX_POINT && p.x >= 0) {
            Log.d("Test", "isContains: \t" + playPoints.contains(p));
            Log.d("Test", "getGradeoffL(-1,1): \t" + p);
            count++;
            p.offset(-1, 1);
        }
        isOtherSideFree = freePoints.contains(p);
        Log.d("Test", "OtherSideFree: \t" + isOtherSideFree);
        p.set(x, y);
        return markGrade(count);
    }

    public int getRightDiagonalMaxGrade(Point p, ArrayList<Point> playPoints, ArrayList<Point> freePoints) {
        int count = 1;
        int x = p.x;
        int y = p.y;
        Log.d("Test", "getGrade: \t" + p);

        p.offset(1, 1);
        while (playPoints.contains(p) && count < 5 && p.x < MAX_POINT && p.y < MAX_POINT) {
            Log.d("Test", "isContains: \t" + playPoints.contains(p));
            Log.d("Test", "getGradeoffR(1,1): \t" + p);
            count++;
            p.offset(1, 1);
        }
        isSideFree = freePoints.contains(p);
        Log.d("Test", "SideFree: \t" + isSideFree);
        p.set(x, y);
        Log.d("Test", "getGrade: \t" + p);
        p.offset(-1, -1);
        while (playPoints.contains(p) && count < 5 && p.x >= 0 && p.y >= 0) {
            Log.d("Test", "isContains: \t" + playPoints.contains(p));
            Log.d("Test", "getGradeoffR(-1,-1): \t" + p);
            count++;
            p.offset(-1, -1);
        }
        isOtherSideFree = freePoints.contains(p);
        Log.d("Test", "OtherSideFree: \t" + isOtherSideFree);
        p.set(x, y);
        return markGrade(count);
    }

}
