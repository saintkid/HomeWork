package com.example.aqr.homework.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.example.aqr.homework.R;
import com.example.aqr.homework.tool.CheckFiveInRow;
import com.example.aqr.homework.tool.FiveAIByAqr;


import java.util.ArrayList;


/**
 * Created by Aqr on 2017/5/24.
 */

public class FiveInaRowView extends View {

    private int MAX_LINE = 15;
    private static final String INSTANCE = "instance";
    private static final String INSTANCE_GAME_OVER = "instance_game_over";
    private static final String INSTANCE_WHITE_ARRAY = "instance_white_array";
    private static final String INSTANCE_BLACK_ARRAY = "instance_black_array";

    private int mViewWidth;
    private float mLineHeight;
    private float ratio = 3 * 1.0f / 4;

    private Bitmap mWhiteStone;
    private Bitmap mBlackStone;


    private ArrayList<Point> downWhitePoint = new ArrayList<>();
    private ArrayList<Point> downBlackPoint = new ArrayList<>();
    private ArrayList<Point> freePoint = new ArrayList<>();

    private boolean isWhite = false;
    private boolean isGameOver = false;
    private boolean isWhiteWin = false;

    private Paint mPaint = new Paint();

    private CheckFiveInRow checkFiveInRow = new CheckFiveInRow();
    //private FiveInRowAI fiveInRowAI = new FiveInRowAI();
    private FiveAIByAqr fiveAIByAqr = new FiveAIByAqr();

    public FiveInaRowView(Context context) {
        super(context);
        setBackgroundColor(0xffd3b89b);
        init();
    }

    public FiveInaRowView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setBackgroundColor(0xffd3b89b);
        init();
    }

    public FiveInaRowView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setBackgroundColor(0xffd3b89b);
        init();
    }

    public void init() {
        mPaint.setColor(0x88000000);
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mWhiteStone = BitmapFactory.decodeResource(getResources(), R.drawable.ic_stone_white);
        mBlackStone = BitmapFactory.decodeResource(getResources(), R.drawable.ic_stone_black);
        initFreePoint();


    }

    public void refresh() {
        downWhitePoint.clear();
        downBlackPoint.clear();
        freePoint.clear();
        initFreePoint();
        isGameOver = false;
        isWhiteWin = false;
        isWhite = false;
        invalidate();
    }

    public void initFreePoint() {
        for (int x = 0; x < MAX_LINE; x++) {
            for (int y = 0; y < MAX_LINE; y++) {
                freePoint.add(new Point(x, y));
            }
        }
    }

    public Point getPoint(int x, int y) {
        return new Point((int) (x / mLineHeight), (int) (y / mLineHeight));
    }

    private void checkGameOver() {

        if (checkFiveInRow.isFiveInRow(downBlackPoint) || checkFiveInRow.isFiveInRow(downWhitePoint)) {
            isWhiteWin = !checkFiveInRow.isFiveInRow(downBlackPoint);
            isGameOver = true;
        } else if (isWhite && !isGameOver) {
            fiveAIByAqr.setParams(downWhitePoint, downBlackPoint, freePoint);
            Point point = fiveAIByAqr.getBestPoint();
            // Point point = fiveInRowAI.doAI(downWhitePoint,downBlackPoint,freePoint);
            downWhitePoint.add(point);
            freePoint.remove(point);
            invalidate();
            isWhite = !isWhite;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int width = Math.min(widthSize, heightSize);

        if (widthMode == MeasureSpec.UNSPECIFIED) {
            width = heightSize;
        } else if (heightMode == MeasureSpec.UNSPECIFIED) {
            width = widthSize;
        }
        setMeasuredDimension(width, width);

        // super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mViewWidth = w;
        mLineHeight = mViewWidth * 1.0f / MAX_LINE;
        int stoneWidth = (int) (mLineHeight * ratio);
        mWhiteStone = Bitmap.createScaledBitmap(mWhiteStone, stoneWidth, stoneWidth, false);
        mBlackStone = Bitmap.createScaledBitmap(mBlackStone, stoneWidth, stoneWidth, false);


    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (isGameOver) return false;
        int action = event.getAction();
        if (action == MotionEvent.ACTION_UP) {
            int x = (int) event.getX();
            int y = (int) event.getY();
            Point p = getPoint(x, y);
            if (downBlackPoint.contains(p) || downWhitePoint.contains(p)) {
                return false;

            }
            freePoint.remove(p);
            downBlackPoint.add(p);

            //downWhitePoint.add(fiveInRowAI.doAI(downWhitePoint,downBlackPoint,freePoint));


            invalidate();
            isWhite = !isWhite;

        }
        return true;

    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawBroad(canvas);
        drawStone(canvas);
        checkGameOver();
        if (isGameOver) {
            AlertDialog.Builder gameOverDialog = new AlertDialog.Builder(getContext());
            gameOverDialog.setCancelable(false);
            if (isWhiteWin) gameOverDialog.setTitle("你输了");
            else gameOverDialog.setTitle("你赢了");
            gameOverDialog.setNegativeButton("结束", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();

                }
            });
            gameOverDialog.setPositiveButton("再来一局", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    refresh();
                    dialog.dismiss();
                }
            });
            gameOverDialog.show();

        }


    }

    private void drawBroad(Canvas canvas) {
        int w = mViewWidth;
        float lineHeight = mLineHeight;
        for (int i = 0; i < MAX_LINE; i++) {
            int startX = (int) (lineHeight / 2);
            int endX = (int) (w - lineHeight / 2);
            int y = (int) ((0.5 + i) * lineHeight);
            canvas.drawLine(startX, y, endX, y, mPaint);
            canvas.drawLine(y, startX, y, endX, mPaint);
        }
    }

    private void drawStone(Canvas canvas) {
        for (int i = 0; i < downWhitePoint.size(); i++) {
            Point whitePoint = downWhitePoint.get(i);
            int pointX = (int) ((whitePoint.x + (1 - ratio) / 2) * mLineHeight);
            int pointY = (int) ((whitePoint.y + (1 - ratio) / 2) * mLineHeight);
            canvas.drawBitmap(mWhiteStone, pointX, pointY, null);


        }
        for (int i = 0; i < downBlackPoint.size(); i++) {
            Point blackPoint = downBlackPoint.get(i);
            int pointX = (int) ((blackPoint.x + (1 - ratio) / 2) * mLineHeight);
            int pointY = (int) ((blackPoint.y + (1 - ratio) / 2) * mLineHeight);
            canvas.drawBitmap(mBlackStone, pointX, pointY, null);


        }
    }


    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(INSTANCE, super.onSaveInstanceState());
        bundle.putBoolean(INSTANCE_GAME_OVER, isGameOver);
        bundle.putParcelableArrayList(INSTANCE_BLACK_ARRAY, downBlackPoint);
        bundle.putParcelableArrayList(INSTANCE_WHITE_ARRAY, downWhitePoint);
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            isGameOver = bundle.getBoolean(INSTANCE_GAME_OVER);
            downBlackPoint = bundle.getParcelableArrayList(INSTANCE_BLACK_ARRAY);
            downWhitePoint = bundle.getParcelableArrayList(INSTANCE_WHITE_ARRAY);
            super.onRestoreInstanceState(bundle.getParcelable(INSTANCE));
            return;
        }
        super.onRestoreInstanceState(state);
    }
}
