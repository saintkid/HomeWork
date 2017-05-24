package com.example.aqr.homework;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Aqr on 2017/5/24.
 */

public class FiveInaRowView extends View {
    private int mViewWidth;
    private float mLineHeight;
    private int MAX_LINE = 10;

    private Bitmap mWhiteStone;
    private Bitmap mBlackStone;

    private float ratio = 3*1.0f/4;
    private List<Point> downWhitePoint = new ArrayList<>();
    private List<Point> downBlackPoint = new ArrayList<>();

    private boolean isWhite = false;
    private boolean isGameOver = false;
    private boolean isWhiteWin = false;

    private Paint mPaint = new Paint();
    public FiveInaRowView(Context context) {
        super(context);
        setBackgroundColor(0x44ff0000);
        init();
    }

    public FiveInaRowView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setBackgroundColor(0x44ff0000);
        init();
    }

    public FiveInaRowView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setBackgroundColor(0x44ff0000);
        init();
    }
    public void init(){
        mPaint.setColor(0x88000000);
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mWhiteStone = BitmapFactory.decodeResource(getResources(), R.drawable.ic_stone_white);
        mBlackStone= BitmapFactory.decodeResource(getResources(), R.drawable.ic_stone_black);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int width = Math.min(widthSize,heightSize);

        if(widthMode == MeasureSpec.UNSPECIFIED){
            width = heightSize;
        }
        else if(heightMode == MeasureSpec.UNSPECIFIED){
            width = widthSize;
        }
        setMeasuredDimension(width,width);

       // super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mViewWidth = w;
        mLineHeight = mViewWidth*1.0f/MAX_LINE;

        int stoneWidth = (int) (mLineHeight*ratio);

        mWhiteStone = Bitmap.createScaledBitmap(mWhiteStone, stoneWidth, stoneWidth,false);
        mBlackStone = Bitmap.createScaledBitmap(mBlackStone, stoneWidth, stoneWidth,false);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(isGameOver) return false;
        int action = event.getAction();
        if(action ==  MotionEvent.ACTION_UP){
            int x = (int) event.getX();
            int y = (int) event.getY();
            Point p = getPoint(x,y);
            if(downBlackPoint.contains(p)||downWhitePoint.contains(p)){
                return false;

            }
            if(isWhite){
                downWhitePoint.add(p);
            }
            else{
                downBlackPoint.add(p);
            }
            isWhite = !isWhite;
            invalidate();


        }
        return true;

    }

    public Point getPoint(int x, int y){
        return new Point((int)(x/mLineHeight), (int)(y/mLineHeight));
    }

    private void checkGameOver(){
        if(isFiveInRow()){

            isGameOver = true;
        }
    }
    private boolean isFiveInRow( ){

        return false;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawBroad(canvas);
        drawStone(canvas);
    }
    private void drawBroad(Canvas canvas){
        int w = mViewWidth;
        float lineHeight = mLineHeight;
        for(int i = 0; i<MAX_LINE; i++){
            int startX = (int)(lineHeight/2);
            int endX = (int) (w -lineHeight/2);
            int y = (int) ((0.5+i)*lineHeight);
            canvas.drawLine(startX, y, endX, y,mPaint);
            canvas.drawLine(y, startX, y, endX, mPaint);
        }
    }
    private  void drawStone(Canvas canvas) {
        for(int i = 0; i < downWhitePoint.size(); i++){
            Point whitePoint = downWhitePoint.get(i);
            int pointX = (int) ((whitePoint.x+(1-ratio)/2)*mLineHeight);
            int pointY = (int) ((whitePoint.y+(1-ratio)/2)*mLineHeight);
            canvas.drawBitmap(mWhiteStone, pointX, pointY, null);


        }
        for(int i = 0; i < downBlackPoint.size(); i++){
            Point blackPoint = downBlackPoint.get(i);
            int pointX = (int) ((blackPoint.x+(1-ratio)/2)*mLineHeight);
            int pointY = (int) ((blackPoint.y+(1-ratio)/2)*mLineHeight);
            canvas.drawBitmap(mBlackStone, pointX, pointY, null);


        }
    }


}
