package com.example.aqr.homework.tool;

import android.graphics.Point;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by Aqr on 2017/5/25.
 */

public class FiveInRowAI {
    public static final int HORIZONTAL = 0;
    public static final int VERTICAL = 1;
    public static final int RIGHT_DIAGONAL = 2;
    public static final int LEFT_DIAGONAL = 3;
    public static final int MAX_POINT = 10;
    //往前往后
    public static final boolean FORWARD = true;
    public static final boolean BACKWARD = false;

    private final Map<Point,ArrayList<FirstAIResult>> computerFirstResults = new HashMap<Point,ArrayList<FirstAIResult>>();
    private final Map<Point,ArrayList<FirstAIResult>> humanFirstResults = new HashMap<Point, ArrayList<FirstAIResult>>();

    private final FirstAIResult far = new FirstAIResult(1, null, HORIZONTAL);





    //标示分析结果当前点位是两头通（ALIVE）还是只有一头通（HALF_ALIVE），封死的棋子分析过程自动屏蔽，不作为待选棋子
    public static final int ALIVE = 1;
    public static final int HALF_ALIVE = 0;
    //private static final int DEAD = -1;
    private class AIRange{
        int xStart,yStart,xStop,yStop;
        private AIRange(int xStart, int yStart, int xStop, int yStop) {
            this.xStart = xStart;
            this.yStart = yStart;
            this.xStop = xStop;
            this.yStop = yStop;
        }
    }
    private static final int RANGE_STEP = 1;
    AIRange currentRange = new AIRange(0, 0, 0, 0);

    public FiveInRowAI(){

    }
    private void initRange(ArrayList<Point> computerPoints, ArrayList<Point> humanPoints){
        currentRange.xStart = humanPoints.get(0).x-RANGE_STEP;
        currentRange.yStart = humanPoints.get(0).y-RANGE_STEP;
        currentRange.xStop = humanPoints.get(0).x+RANGE_STEP;
        currentRange.yStop = humanPoints.get(0).y+RANGE_STEP;
        for (Point point : humanPoints) {
            if(point.x-RANGE_STEP<currentRange.xStart){
                currentRange.xStart = point.x-RANGE_STEP;
            }else if(point.x+RANGE_STEP>currentRange.xStop){
                currentRange.xStop = point.x+RANGE_STEP;
            }
            if(point.y-RANGE_STEP<currentRange.yStart){
                currentRange.yStart = point.y-RANGE_STEP;
            }else if(point.y+RANGE_STEP>currentRange.yStop){
                currentRange.yStop = point.y+RANGE_STEP;
            }
        }
        for (Point point : computerPoints) {
            if(point.x-RANGE_STEP<currentRange.xStart){
                currentRange.xStart = point.x-RANGE_STEP;
            }else if(point.x+RANGE_STEP>currentRange.xStop){
                currentRange.xStop = point.x+RANGE_STEP;
            }
            if(point.y-RANGE_STEP<currentRange.yStart){
                currentRange.yStart = point.y-RANGE_STEP;
            }else if(point.y+RANGE_STEP>currentRange.yStop){
                currentRange.yStop = point.y+RANGE_STEP;
            }
        }

        //如果范围扩大后超过了棋盘，则等于棋盘
        currentRange.xStart=currentRange.xStart<0?0:currentRange.xStart;
        currentRange.yStart=currentRange.yStart<0?0:currentRange.yStart;
        currentRange.xStop=currentRange.xStop>=MAX_POINT?MAX_POINT-1:currentRange.xStop;
        currentRange.yStop=currentRange.yStop>=MAX_POINT?MAX_POINT-1:currentRange.yStop;
    }
    private void initAIResults(){
        computerFirstResults.clear();
        humanFirstResults.clear();
//        //第二次总结果
//        computerSencodResults.clear();
//        humanSencodResults.clear();
//        //第二次分结果
//        computer4HalfAlives.clear();
//        computerDouble3Alives.clear();
//        computer3Alives.clear();
//        computerDouble2Alives.clear();
//        computer2Alives.clear();
//        computer3HalfAlives.clear();
//
//        //第二次分结果，人类
//        human4Alives.clear();
//        human4HalfAlives.clear();
//        humanDouble3Alives.clear();
//        human3Alives.clear();
//        humanDouble2Alives.clear();
//        human2Alives.clear();
//        human3HalfAlives.clear();
        System.gc();
    }

    private Point getFirstPoint(ArrayList<Point> humanPoints) {
        Point point = humanPoints.get(0);
        if(point.x == 0 || point.y == 0 || point.x== MAX_POINT && point.y == MAX_POINT)
            return new Point(MAX_POINT/2, MAX_POINT/2);
        else{
            return new Point(point.x-1,point.y);
        }
    }


    public Point doAI(ArrayList<Point> computerPoints, ArrayList<Point> humanPoints, ArrayList<Point> freePoints){
        if(humanPoints.size()==1){//第一步
            return getFirstPoint(humanPoints);
        }
        //初始化计算范围
        initRange(computerPoints, humanPoints);
        //清除以前的结果
        initAIResults();
        // 开始分析，扫描所有空白点，形成第一次分析结果
        Point bestPoint = doFirstAI(computerPoints, humanPoints,freePoints);
        if(bestPoint!=null){
            //System.out.println("这个棋子最重要，只能下这个棋子");
            return bestPoint;
        }
      /*  // 分析第一次结果，找到自己的最佳点位
        bestPoint = doComputerSencondAnalysis(computerFirstResults,computerSencodResults);
        if(bestPoint!=null){
            //System.out.println("快要赢了，就下这个棋子");
            return bestPoint;
        }
        computerFirstResults.clear();
        System.gc();
        // 分析第一次结果，找到敌人的最佳点位
        bestPoint = doHumanSencondAnalysis(humanFirstResults,humanSencodResults);
        if(bestPoint!=null){
            //System.out.println("再不下这个棋子就输了");
            return bestPoint;
        }
        humanFirstResults.clear();
        System.gc();
        //没找到绝杀点，第三次结果分析
        return doThirdAnalysis();*/
      return null;
    }



    public Point doFirstAI(ArrayList<Point> computerPoints, ArrayList<Point> humanPoints, ArrayList<Point> freePoints){
        int size = freePoints.size();
        Point computerPoint = null;
        Point humanPoint = null;
        int x,y;
        FirstAIResult firstAIResult;
        for (int i = 0; i < size; i++) {
            computerPoint = freePoints.get(i);
            //先把X、Y坐标记下来，因为在分析过程中会改变原来的对象
            x = computerPoint.x;
            y = computerPoint.y;
            if(x<currentRange.xStart || x>currentRange.xStop || y<currentRange.yStart || y>currentRange.yStop){
                continue;
            }


            //尝试在此位置上下一个棋子，并分析在“横向”这个方向上我方可形成的状态，如活4，活3，半活4，活2等所有状态
            firstAIResult = tryAndCountResult(computerPoints,humanPoints, computerPoint, HORIZONTAL);
            computerPoint.set(x,y);//回复点位的原值，以供下次分析
            if(firstAIResult!=null){//无返回结果此方向上不可能达到五个棋子，
                if(firstAIResult.count==5)//等于5表示在此点上下棋子即可连成5个，胜利了，不再往下进行分析
                    return computerPoint;
                //记录第一次分析结果
                addToFirstAIResult(firstAIResult,computerFirstResults);
            }

            //在“纵向”这个方向上重复上面的步骤
            firstAIResult = tryAndCountResult(computerPoints,humanPoints, computerPoint, VERTICAL);
            computerPoint.set(x,y);
            if(firstAIResult!=null){//死棋，不下
                if(firstAIResult.count==5)
                    return computerPoint;

                addToFirstAIResult(firstAIResult,computerFirstResults);
            }

            //正斜向

            firstAIResult = tryAndCountResult(computerPoints,humanPoints, computerPoint, LEFT_DIAGONAL);
            computerPoint.set(x,y);
            if(firstAIResult!=null){//死棋，不下
                if(firstAIResult.count==5)
                    return computerPoint;

                addToFirstAIResult(firstAIResult,computerFirstResults);
            }

            //反斜向
            firstAIResult = tryAndCountResult(computerPoints,humanPoints, computerPoint, RIGHT_DIAGONAL);
            computerPoint.set(x,y);
            if(firstAIResult!=null){//死棋，不下
                if(firstAIResult.count==5)
                    return computerPoint;

                addToFirstAIResult(firstAIResult,computerFirstResults);
            }




            //在“横向”上分析此棋子可在敌方形成如何状态，如敌方的活3、半活4等
            firstAIResult = tryAndCountResult(humanPoints,computerPoints, computerPoint, HORIZONTAL);
            computerPoint.set(x,y);
            if(firstAIResult!=null){//死棋，不下
                if(firstAIResult.count==5)
                    humanPoint = computerPoint;

                addToFirstAIResult(firstAIResult,humanFirstResults);
            }

            //“纵向”
            firstAIResult = tryAndCountResult(humanPoints,computerPoints, computerPoint, VERTICAL);
            computerPoint.set(x,y);
            if(firstAIResult!=null){//死棋，不下
                if(firstAIResult.count==5)
                    humanPoint = computerPoint;

                addToFirstAIResult(firstAIResult,humanFirstResults);
            }

            //“正斜”
            firstAIResult = tryAndCountResult(humanPoints,computerPoints, computerPoint, LEFT_DIAGONAL);
            computerPoint.set(x,y);
            if(firstAIResult!=null){//死棋，不下
                if(firstAIResult.count==5)
                    humanPoint = computerPoint;

                addToFirstAIResult(firstAIResult,humanFirstResults);
            }


            //“反斜”
            firstAIResult = tryAndCountResult(humanPoints,computerPoints, computerPoint, RIGHT_DIAGONAL);
            computerPoint.set(x,y);
            if(firstAIResult!=null){//死棋，不下
                if(firstAIResult.count==5)
                    humanPoint = computerPoint;

                addToFirstAIResult(firstAIResult,humanFirstResults);
            }
        }
        //如果没有绝杀棋子，第一次分析不需要返回结果
        return humanPoint;

    }

    private FirstAIResult tryAndCountResult(ArrayList<Point> computerPoints,ArrayList<Point> humanPoints, Point point,int direction) {
        int x = point.x;
        int y = point.y;
        FirstAIResult fr = null;

        int maxCountOnThisDirection = maxCountOnThisDirection(point, humanPoints, direction, 1);
        if(maxCountOnThisDirection<5){
            //无意义的棋子
            return null;//此方向不足五个空位，已排除己方已下的棋子
        }else if(maxCountOnThisDirection==5){
            //半死状态，当是一头通
            fr = far.init(point, direction,HALF_ALIVE);
        }else{
            //两头皆通
            fr = far.init(point, direction,ALIVE);
        }

        //在前和后的方向上计算一次
        point.set(x,y);
        countPoint(computerPoints,humanPoints,point,fr,direction,FORWARD);
        countPoint(computerPoints,humanPoints,point,fr,direction,BACKWARD);


        if(fr.count<=1 || (fr.count==2 && fr.aliveState==HALF_ALIVE)){//活1，半活2及其以下结果，抛弃
            return null;
        }
        //返回复制的结果
        return fr.cloneResult();
    }
    private int maxCountOnThisDirection(Point point,ArrayList<Point> humanPoints,int direction,int count){
        int x=point.x,y=point.y;

        switch (direction) {
            //横向
            case HORIZONTAL:
                point.offset(-1, 0);
                while (!humanPoints.contains(point) && point.x>=0 && count<6) {
                    point.offset(-1, 0);
                    count ++;
                }
                point.set(x,y);
                point.offset(1,0);
                while (!humanPoints.contains(point) && point.x<MAX_POINT && count<6) {
                    point.offset(1,0);
                    count ++;
                }
                break;
            //纵向
            case VERTICAL:
                point.offset(0,-1);
                while (!humanPoints.contains(point) && point.y>=0) {
                    point.offset(0,-1);
                    count ++;
                }
                point.set(x,y);
                point.offset(0,1);
                while (!humanPoints.contains(point) && point.y<MAX_POINT&& count<6) {
                    point.offset(0,1);
                    count ++;
                }
                break;
            //正斜向 /
            case LEFT_DIAGONAL:
                point.offset(-1,1);
                while (!humanPoints.contains(point) && point.x>=0 && point.y<MAX_POINT) {
                    point.offset(-1,1);
                    count ++;
                }
                point.set(x,y);
                point.offset(1,-1);
                while (!humanPoints.contains(point) && point.x<MAX_POINT && point.y>=0 && count<6) {
                    count ++;
                }
                break;
            //反斜 /
            case RIGHT_DIAGONAL:
                point.offset(-1,-1);
                while (!humanPoints.contains(point) && point.x>=0 && point.y>=0) {
                    point.offset(-1,-1);
                    count ++;
                }
                point.set(x,y);
                point.offset(1,1);
                while (!humanPoints.contains(point) && point.x<9 && point.y<9 && count<6) {
                    point.offset(1,1);
                    count ++;
                }
                break;
        }
        return count;
    }

    private void countPoint(ArrayList<Point> myPoints, ArrayList<Point> humanPoints, Point point, FirstAIResult fr,int direction,boolean forward) {
        if(myPoints.contains(pointToNext(point,direction,forward))){
            fr.count ++;
            if(myPoints.contains(pointToNext(point,direction,forward))){
                fr.count ++;
                if(myPoints.contains(pointToNext(point,direction,forward))){
                    fr.count ++;
                    if(myPoints.contains(pointToNext(point,direction,forward))){
                        fr.count ++;
                    }else if(humanPoints.contains(point) || isOutSideOfWall(point,direction)){
                        fr.aliveState=HALF_ALIVE;
                    }
                }else if(humanPoints.contains(point) || isOutSideOfWall(point,direction)){
                    fr.aliveState=HALF_ALIVE;
                }
            }else if(humanPoints.contains(point) || isOutSideOfWall(point,direction)){
                fr.aliveState=HALF_ALIVE;
            }
        }else if(humanPoints.contains(point) || isOutSideOfWall(point,direction)){
            fr.aliveState=HALF_ALIVE;
        }
    }

    private Point pointToNext(Point point,int direction,boolean forward){
        switch (direction) {
            case HORIZONTAL:
                if(forward)
                    point.x++;
                else
                    point.x--;
                break;
            case VERTICAL:
                if(forward)
                    point.y++;
                else
                    point.y--;
                break;
            case LEFT_DIAGONAL:
                if(forward){
                    point.x++;
                    point.y--;
                }else{
                    point.x--;
                    point.y++;
                }
                break;
            case RIGHT_DIAGONAL:
                if(forward){
                    point.x++;
                    point.y++;
                }else{
                    point.x--;
                    point.y--;
                }
                break;
        }
        return point;
    }

    private boolean isOutSideOfWall(Point point,int direction){
        if(direction==HORIZONTAL){
            return point.x<0 || point.x>=MAX_POINT;//最大的X和Y值均在墙外所以用等号
        }else if(direction==VERTICAL){
            return point.y<0 || point.y>=MAX_POINT;
        }else{//这里可能有问题
            return point.x<0 || point.y<0 || point.x>=MAX_POINT || point.y>=MAX_POINT;
        }
    }

    private void addToFirstAIResult(FirstAIResult result,Map<Point,ArrayList<FirstAIResult>> dest){
        if(dest.containsKey(result.point)){
            dest.get(result.point).add(result);
        }else{
            ArrayList<FirstAIResult> list = new ArrayList<FirstAIResult>(1);
            list.add(result);
            dest.put(result.point, list);
        }
    }

}
