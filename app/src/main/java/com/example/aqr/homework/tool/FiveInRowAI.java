package com.example.aqr.homework.tool;

import android.graphics.Point;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by Aqr on 2017/5/25.
 */

public class FiveInRowAI {
    //棋子方向
    public static final int HORIZONTAL = 0;
    public static final int VERTICAL = 1;
    public static final int RIGHT_DIAGONAL = 2;
    public static final int LEFT_DIAGONAL = 3;
    public static final int MAX_POINT = 15;
    //往前往后
    public static final boolean FORWARD = true;
    public static final boolean BACKWARD = false;
    //玩家类型
    public static final int HUMAN = 0;
    public static final int AI = 1;


    private final Map<Point, ArrayList<FirstAIResult>> computerFirstResults = new HashMap<Point, ArrayList<FirstAIResult>>();
    private final Map<Point, ArrayList<FirstAIResult>> humanFirstResults = new HashMap<Point, ArrayList<FirstAIResult>>();

    private final FirstAIResult far = new FirstAIResult(1, null, HORIZONTAL);

    //第二次总结果
    protected final ArrayList<SecondAIResult> computerSecondResults = new ArrayList<SecondAIResult>();

    protected final ArrayList<SecondAIResult> humanSecondResults= new ArrayList<SecondAIResult>();

    //第二次分结果，电脑
    protected final ArrayList<SecondAIResult> computer4HalfAlives = new ArrayList<SecondAIResult>();
    protected final ArrayList<SecondAIResult> computerDouble3Alives = new ArrayList<SecondAIResult>();
    protected final ArrayList<SecondAIResult> computer3Alives = new ArrayList<SecondAIResult>();
    protected final ArrayList<SecondAIResult> computerDouble2Alives = new ArrayList<SecondAIResult>();
    protected final ArrayList<SecondAIResult> computer2Alives = new ArrayList<SecondAIResult>();
    protected final ArrayList<SecondAIResult> computer3HalfAlives = new ArrayList<SecondAIResult>();

    //第二次分结果，人类
    protected final ArrayList<SecondAIResult> human4Alives = new ArrayList<SecondAIResult>();
    protected final ArrayList<SecondAIResult> human4HalfAlives = new ArrayList<SecondAIResult>();
    protected final ArrayList<SecondAIResult> humanDouble3Alives = new ArrayList<SecondAIResult>();
    protected final ArrayList<SecondAIResult> human3Alives = new ArrayList<SecondAIResult>();
    protected final ArrayList<SecondAIResult> humanDouble2Alives = new ArrayList<SecondAIResult>();
    protected final ArrayList<SecondAIResult> human2Alives = new ArrayList<SecondAIResult>();
    protected final ArrayList<SecondAIResult> human3HalfAlives = new ArrayList<SecondAIResult>();


    //标示分析结果当前点位是两头通（ALIVE）还是只有一头通（HALF_ALIVE），封死的棋子分析过程自动屏蔽，不作为待选棋子
    public static final int ALIVE = 1;
    public static final int HALF_ALIVE = 0;

    //private static final int DEAD = -1;
    private class AIRange {
        int xStart, yStart, xStop, yStop;

        private AIRange(int xStart, int yStart, int xStop, int yStop) {
            this.xStart = xStart;
            this.yStart = yStart;
            this.xStop = xStop;
            this.yStop = yStop;
        }
    }

    private static final int RANGE_STEP = 1;
    AIRange currentRange = new AIRange(0, 0, 0, 0);

    public FiveInRowAI() {

    }

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

    private void initAIResults() {
        computerFirstResults.clear();
        humanFirstResults.clear();
        //第二次总结果
        computerSecondResults.clear();
        humanSecondResults.clear();
        //第二次分结果
        computer4HalfAlives.clear();
        computerDouble3Alives.clear();
        computer3Alives.clear();
        computerDouble2Alives.clear();
        computer2Alives.clear();
        computer3HalfAlives.clear();

        //第二次分结果，人类
        human4Alives.clear();
        human4HalfAlives.clear();
        humanDouble3Alives.clear();
        human3Alives.clear();
        humanDouble2Alives.clear();
        human2Alives.clear();
        human3HalfAlives.clear();
        System.gc();
    }

    private Point getFirstPoint(ArrayList<Point> humanPoints) {
        Point point = humanPoints.get(0);
        if (point.x == 0 || point.y == 0 || point.x == MAX_POINT && point.y == MAX_POINT)
            return new Point(MAX_POINT / 2, MAX_POINT / 2);
        else {
            return new Point(point.x - 1, point.y);
        }
    }


    public Point doAI(ArrayList<Point> computerPoints, ArrayList<Point> humanPoints, ArrayList<Point> freePoints) {
        Point bestPoint;
        if (humanPoints.size() == 1) {//第一步
            return getFirstPoint(humanPoints);
        }

        //初始化计算范围
        initRange(computerPoints, humanPoints);
        //清除以前的结果
        initAIResults();
        // 开始分析，扫描所有空白点，形成第一次分析结果
       bestPoint = doFirstAI(computerPoints, humanPoints, freePoints);
        if (bestPoint != null) {
            //System.out.println("这个棋子最重要，只能下这个棋子");
            return bestPoint;
        }
        bestPoint = doSecondAI(computerFirstResults,computerSecondResults,AI);
        if(bestPoint != null) {
            return  bestPoint;
        }
		computerFirstResults.clear();

        bestPoint = doSecondAI(humanFirstResults,humanSecondResults,HUMAN);
        if(bestPoint != null){
            return  bestPoint;
        }

        
        humanFirstResults.clear();
        System.gc();
        return doThirdAI();
    }


    public Point doFirstAI(ArrayList<Point> computerPoints, ArrayList<Point> humanPoints, ArrayList<Point> freePoints) {
        int size = freePoints.size();
        Point computerPoint = null;
        Point humanPoint = null;
        int x, y;
        FirstAIResult firstAIResult;
        for (int i = 0; i < size; i++) {
            computerPoint = freePoints.get(i);
            //先把X、Y坐标记下来，因为在分析过程中会改变原来的对象
            x = computerPoint.x;
            y = computerPoint.y;
            if (x < currentRange.xStart || x > currentRange.xStop || y < currentRange.yStart || y > currentRange.yStop) {
                continue;
            }


            //尝试在此位置上下一个棋子，并分析在“横向”这个方向上我方可形成的状态，如活4，活3，半活4，活2等所有状态
            firstAIResult = tryAndCountResult(computerPoints, humanPoints, computerPoint, HORIZONTAL);
            computerPoint.set(x, y);//回复点位的原值，以供下次分析
            if (firstAIResult != null) {//无返回结果此方向上不可能达到五个棋子，
                if (firstAIResult.count == 5)//等于5表示在此点上下棋子即可连成5个，胜利了，不再往下进行分析
                    return computerPoint;
                //记录第一次分析结果
                addToFirstAIResult(firstAIResult, computerFirstResults);
            }

            //在“纵向”这个方向上重复上面的步骤
            firstAIResult = tryAndCountResult(computerPoints, humanPoints, computerPoint, VERTICAL);
            computerPoint.set(x, y);
            if (firstAIResult != null) {//死棋，不下
                if (firstAIResult.count == 5)
                    return computerPoint;

                addToFirstAIResult(firstAIResult, computerFirstResults);
            }

            //正斜向

            firstAIResult = tryAndCountResult(computerPoints, humanPoints, computerPoint, LEFT_DIAGONAL);
            computerPoint.set(x, y);
            if (firstAIResult != null) {//死棋，不下
                if (firstAIResult.count == 5)
                    return computerPoint;

                addToFirstAIResult(firstAIResult, computerFirstResults);
            }

            //反斜向
            firstAIResult = tryAndCountResult(computerPoints, humanPoints, computerPoint, RIGHT_DIAGONAL);
            computerPoint.set(x, y);
            if (firstAIResult != null) {//死棋，不下
                if (firstAIResult.count == 5)
                    return computerPoint;

                addToFirstAIResult(firstAIResult, computerFirstResults);
            }


            //在“横向”上分析此棋子可在敌方形成如何状态，如敌方的活3、半活4等
            firstAIResult = tryAndCountResult(humanPoints, computerPoints, computerPoint, HORIZONTAL);
            computerPoint.set(x, y);
            if (firstAIResult != null) {//死棋，不下
                if (firstAIResult.count == 5)
                    humanPoint = computerPoint;

                addToFirstAIResult(firstAIResult, humanFirstResults);
            }

            //“纵向”
            firstAIResult = tryAndCountResult(humanPoints, computerPoints, computerPoint, VERTICAL);
            computerPoint.set(x, y);
            if (firstAIResult != null) {//死棋，不下
                if (firstAIResult.count == 5)
                    humanPoint = computerPoint;

                addToFirstAIResult(firstAIResult, humanFirstResults);
            }

            //“正斜”
            firstAIResult = tryAndCountResult(humanPoints, computerPoints, computerPoint, LEFT_DIAGONAL);
            computerPoint.set(x, y);
            if (firstAIResult != null) {//死棋，不下
                if (firstAIResult.count == 5)
                    humanPoint = computerPoint;

                addToFirstAIResult(firstAIResult, humanFirstResults);
            }


            //“反斜”
            firstAIResult = tryAndCountResult(humanPoints, computerPoints, computerPoint, RIGHT_DIAGONAL);
            computerPoint.set(x, y);
            if (firstAIResult != null) {//死棋，不下
                if (firstAIResult.count == 5)
                    humanPoint = computerPoint;

                addToFirstAIResult(firstAIResult, humanFirstResults);
            }
        }
        //如果没有绝杀棋子，第一次分析不需要返回结果
        return humanPoint;

    }

    private FirstAIResult tryAndCountResult(ArrayList<Point> computerPoints, ArrayList<Point> humanPoints, Point point, int direction) {
        int x = point.x;
        int y = point.y;
        FirstAIResult fr = null;

        int maxCountOnThisDirection = maxCountOnThisDirection(point, humanPoints, direction, 1);
        if (maxCountOnThisDirection < 5) {
            //无意义的棋子
            return null;//此方向不足五个空位，已排除己方已下的棋子
        } else if (maxCountOnThisDirection == 5) {
            //半死状态，当是一头通
            fr = far.init(point, direction, HALF_ALIVE);
        } else {
            //两头皆通
            fr = far.init(point, direction, ALIVE);
        }

        //在前和后的方向上计算一次
        point.set(x, y);
        countPoint(computerPoints, humanPoints, point, fr, direction, FORWARD);
        countPoint(computerPoints, humanPoints, point, fr, direction, BACKWARD);


        if (fr.count <= 1 || (fr.count == 2 && fr.aliveState == HALF_ALIVE)) {//活1，半活2及其以下结果，抛弃
            return null;
        }
        //返回复制的结果
        return fr.cloneResult();
    }

    private int maxCountOnThisDirection(Point point, ArrayList<Point> humanPoints, int direction, int count) {
        int x = point.x, y = point.y;

        switch (direction) {
            //横向
            case HORIZONTAL:
                point.offset(-1, 0);
                while (!humanPoints.contains(point) && point.x >= 0 && count < 6) {
                    point.offset(-1, 0);
                    count++;
                }
                point.set(x, y);
                point.offset(1, 0);
                while (!humanPoints.contains(point) && point.x < MAX_POINT && count < 6) {
                    point.offset(1, 0);
                    count++;
                }
                break;
            //纵向
            case VERTICAL:
                point.offset(0, -1);
                while (!humanPoints.contains(point) && point.y >= 0) {
                    point.offset(0, -1);
                    count++;
                }
                point.set(x, y);
                point.offset(0, 1);
                while (!humanPoints.contains(point) && point.y < MAX_POINT && count < 6) {
                    point.offset(0, 1);
                    count++;
                }
                break;
            //正斜向 /
            case LEFT_DIAGONAL:
                point.offset(-1, 1);
                while (!humanPoints.contains(point) && point.x >= 0 && point.y < MAX_POINT) {
                    point.offset(-1, 1);
                    count++;
                }
                point.set(x, y);
                point.offset(1, -1);
                while (!humanPoints.contains(point) && point.x < MAX_POINT && point.y >= 0 && count < 6) {
                    count++;
                }
                break;
            //反斜 /
            case RIGHT_DIAGONAL:
                point.offset(-1, -1);
                while (!humanPoints.contains(point) && point.x >= 0 && point.y >= 0) {
                    point.offset(-1, -1);
                    count++;
                }
                point.set(x, y);
                point.offset(1, 1);
                while (!humanPoints.contains(point) && point.x < MAX_POINT && point.y < MAX_POINT && count < 6) {
                    point.offset(1, 1);
                    count++;
                }
                break;
        }
        return count;
    }

    private void countPoint(ArrayList<Point> myPoints, ArrayList<Point> humanPoints, Point point, FirstAIResult fr, int direction, boolean forward) {
        if (myPoints.contains(pointToNext(point, direction, forward))) {
            fr.count++;
            if (myPoints.contains(pointToNext(point, direction, forward))) {
                fr.count++;
                if (myPoints.contains(pointToNext(point, direction, forward))) {
                    fr.count++;
                    if (myPoints.contains(pointToNext(point, direction, forward))) {
                        fr.count++;
                    } else if (humanPoints.contains(point) || isOutSideOfWall(point, direction)) {
                        fr.aliveState = HALF_ALIVE;
                    }
                } else if (humanPoints.contains(point) || isOutSideOfWall(point, direction)) {
                    fr.aliveState = HALF_ALIVE;
                }
            } else if (humanPoints.contains(point) || isOutSideOfWall(point, direction)) {
                fr.aliveState = HALF_ALIVE;
            }
        } else if (humanPoints.contains(point) || isOutSideOfWall(point, direction)) {
            fr.aliveState = HALF_ALIVE;
        }
    }

    private Point pointToNext(Point point, int direction, boolean forward) {
        switch (direction) {
            case HORIZONTAL:
                if (forward)
                    point.x++;
                else
                    point.x--;
                break;
            case VERTICAL:
                if (forward)
                    point.y++;
                else
                    point.y--;
                break;
            case LEFT_DIAGONAL:
                if (forward) {
                    point.x++;
                    point.y--;
                } else {
                    point.x--;
                    point.y++;
                }
                break;
            case RIGHT_DIAGONAL:
                if (forward) {
                    point.x++;
                    point.y++;
                } else {
                    point.x--;
                    point.y--;
                }
                break;
        }
        return point;
    }

    private boolean isOutSideOfWall(Point point, int direction) {
        if (direction == HORIZONTAL) {
            return point.x < 0 || point.x >= MAX_POINT;//最大的X和Y值均在墙外所以用等号
        } else if (direction == VERTICAL) {
            return point.y < 0 || point.y >= MAX_POINT;
        } else {//这里可能有问题
            return point.x < 0 || point.y < 0 || point.x >= MAX_POINT || point.y >= MAX_POINT;
        }
    }

    private void addToFirstAIResult(FirstAIResult result, Map<Point, ArrayList<FirstAIResult>> dest) {
        if (dest.containsKey(result.point)) {
            dest.get(result.point).add(result);
        } else {
            ArrayList<FirstAIResult> list = new ArrayList<FirstAIResult>(1);
            list.add(result);
            dest.put(result.point, list);
        }
    }


    public Point doSecondAI(Map<Point, ArrayList<FirstAIResult>> firstAIResults, ArrayList<SecondAIResult> secondAIResults, int playerType) {
        ArrayList<FirstAIResult> list = null;
        SecondAIResult sr = null;
        for (Point p : firstAIResults.keySet()) {
            sr = new SecondAIResult(p);
            list = firstAIResults.get(p);
            for (FirstAIResult result : list) {
                if(result.count==4){
                    if(result.aliveState==ALIVE){//经过前面的过滤，双方都排除了绝杀棋，有活4就下这一步了，再下一步就赢了
                        if(playerType == AI) return result.point;//如果有绝杀，第一轮已返回，在此轮活4已经是好的棋子，直接返回，不再往下分析
                        else if(playerType == HUMAN) human4Alives.add(sr);

                    }else{
                        sr.halfAlive4 ++;
                        if(playerType == AI) computer4HalfAlives.add(sr);
                        else if(playerType == HUMAN) human4HalfAlives.add(sr);

                    }
                }else if(result.count==3){
                    if(result.aliveState==ALIVE){
                        sr.alive3++;
                        if(sr.alive3==1){
                            if(playerType == AI) computer3Alives.add(sr);
                            else if(playerType == HUMAN) human3Alives.add(sr);
                        }else{
                            if(playerType == AI) computerDouble3Alives.add(sr);
                            else if(playerType == HUMAN) humanDouble3Alives.add(sr);
                        }
                    }else{
                        sr.halfAlive3++;
                        if(playerType == AI)  computer3HalfAlives.add(sr);
                        else if(playerType == HUMAN) human3HalfAlives.add(sr);
                    }
                }else{//半活2在第一阶段已被排除，不再处理
                    sr.alive2++;
                    if(sr.alive2==1){
                        if(playerType == AI)   computer2Alives.add(sr);
                        else if(playerType == HUMAN) human2Alives.add(sr);
                    }else{
                        if(playerType == AI)  computerDouble2Alives.add(sr);
                        else if(playerType == HUMAN) humanDouble2Alives.add(sr);
                    }
                }
            }
            secondAIResults.add(sr);
        }
        //没有找到活4
        return null;

    }


    public Point doThirdAI() {

        if(!computer4HalfAlives.isEmpty()){
            return computer4HalfAlives.get(0).point;
        }
        System.gc();
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");
        Collections.sort(computerSecondResults);
        System.gc();

        //即将单活4，且我没有半活4以上的，只能堵
        Point mostBestPoint = getBestPoint(human4Alives, computerSecondResults);
        if(mostBestPoint!=null)
            return mostBestPoint;

       // System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");
        Collections.sort(humanSecondResults);
        System.gc();

        mostBestPoint = getBestPoint();
        if(mostBestPoint!=null)
            return mostBestPoint;

        //拿出各自排第一的，谁好就下谁
        return computerSecondResults.get(0).point;
    }
    protected Point getBestPoint(){
        //即将单活4，且我没有半活4以上的，只能堵
        Point mostBest = getBestPoint(computerDouble3Alives, humanSecondResults);
        if(mostBest!=null)
            return mostBest;

        mostBest = getBestPoint(computer3Alives, humanSecondResults);
        if(mostBest!=null)
            return mostBest;

        mostBest = getBestPoint(humanDouble3Alives, computerSecondResults);
        if(mostBest!=null)
            return mostBest;

        mostBest = getBestPoint(human3Alives, computerSecondResults);
        if(mostBest!=null)
            return mostBest;

        mostBest = getBestPoint(computerDouble2Alives, humanSecondResults);
        if(mostBest!=null)
            return mostBest;

        mostBest = getBestPoint(computer2Alives, humanSecondResults);
        if(mostBest!=null)
            return mostBest;

        mostBest = getBestPoint(computer3HalfAlives, humanSecondResults);
        if(mostBest!=null)
            return mostBest;

        mostBest = getBestPoint(human4HalfAlives, computerSecondResults);
        if(mostBest!=null)
            return mostBest;

        mostBest = getBestPoint(humanDouble2Alives, computerSecondResults);
        if(mostBest!=null)
            return mostBest;

        mostBest = getBestPoint(human2Alives, computerSecondResults);
        if(mostBest!=null)
            return mostBest;

        mostBest = getBestPoint(human3HalfAlives, computerSecondResults);
        return mostBest;
    }

    protected Point getBestPoint(ArrayList<SecondAIResult> computer,ArrayList<SecondAIResult> human){

        if(!computer.isEmpty()){
            if(computer.size()>1){

                for (SecondAIResult h : human) {
                    if(computer.contains(h)){
                        return h.point;
                    }
                }
                return computer.get(0).point;
            }else{
                return computer.get(0).point;
            }
        }
        return null;
    }



}
