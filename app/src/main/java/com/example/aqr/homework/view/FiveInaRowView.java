package com.example.aqr.homework.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Message;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.example.aqr.homework.MainActivity;
import com.example.aqr.homework.R;
import com.example.aqr.homework.dao.GameDao;
import com.example.aqr.homework.tool.GameOverManager;
import com.example.aqr.homework.tool.FiveAIByAqr;
import com.example.aqr.homework.tool.PointGrade;


import java.io.IOException;
import java.util.ArrayList;


/**
 * Created by Aqr on 2017/5/24.
 */

public class FiveInaRowView extends View implements DialogInterface.OnClickListener {

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


    public ArrayList<Point> downWhitePoint = new ArrayList<>();
    public ArrayList<Point> downBlackPoint = new ArrayList<>();
    public ArrayList<Point> freePoint = new ArrayList<>();


    private boolean isWhite = false;
    private boolean isGameOver = false;
    private boolean isWhiteWin = false;
    private boolean isStartGame = false;

    private Paint mPaint = new Paint();

    //private MediaPlayer mediaPlayer = new MediaPlayer();
    private GameOverManager gameOverManager = new GameOverManager();
    private FiveAIByAqr fiveAIByAqr = new FiveAIByAqr();
    AlertDialog.Builder gameOverDialog = new AlertDialog.Builder(getContext());
   public MediaPlayer mediaPlayerWin = null;
   public MediaPlayer mediaPlayerDefeat = null;
   public MediaPlayer mediaPlayerChess = null;

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
        AssetManager assetManager = getContext().getAssets();
        try {
            AssetFileDescriptor win = assetManager.openFd("win.mp3");
            mediaPlayerWin = new MediaPlayer();
            mediaPlayerWin.setDataSource(win.getFileDescriptor(), win.getStartOffset(), win.getLength());
            mediaPlayerWin.prepare();

            AssetFileDescriptor defeat = assetManager.openFd("defeat.mp3");
            mediaPlayerDefeat = new MediaPlayer();
            mediaPlayerDefeat.setDataSource(defeat.getFileDescriptor(), defeat.getStartOffset(), defeat.getLength());
            mediaPlayerDefeat.prepare();

            AssetFileDescriptor chess = assetManager.openFd("chess.mp3");
            mediaPlayerChess = new MediaPlayer();
            mediaPlayerChess.setDataSource(chess.getFileDescriptor(), chess.getStartOffset(), chess.getLength());
            mediaPlayerChess.prepare();
        }catch (IOException e){
            e.printStackTrace();
        }


        initFreePoint();
        new Thread() {
            @Override
            public void run() {

                while (true) {
                    try {
                        sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    try {
                        if(isStartGame) {
                            Message msg = new Message();
                            msg.what = 0;
                            msg.arg1 = downBlackPoint.size();
                            MainActivity.handler.sendMessage(msg);
                        }

                    } catch (IllegalStateException e) {
                        e.printStackTrace();
                    }
                }
            }

        }.start();


    }

    public void initOldGame(ArrayList<Point> downWhitePoint, ArrayList<Point> downBlackPoint) {
        this.downWhitePoint.clear();
        this.downWhitePoint.addAll(downWhitePoint);
        this.downBlackPoint.clear();
        this.downBlackPoint.addAll(downBlackPoint);
        this.freePoint.clear();
        initFreePoint();
        for(Point bp: downBlackPoint){
            freePoint.remove(bp);
        }
        for(Point wp: downWhitePoint){
            freePoint.remove(wp);
        }
        invalidate();


    }

    public void refresh() {
        downWhitePoint.clear();
        downBlackPoint.clear();
        freePoint.clear();
        initFreePoint();
        isGameOver = false;
        isWhiteWin = false;
        isWhite = false;
        isStartGame = false;
        invalidate();
        MainActivity.handler.sendEmptyMessage(2);
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

        if (gameOverManager.isFiveInRow(downBlackPoint) || gameOverManager.isFiveInRow(downWhitePoint)) {
            isWhiteWin = !gameOverManager.isFiveInRow(downBlackPoint);
            isGameOver = true;
        } else if (isWhite && !isGameOver) {
            fiveAIByAqr.setParams(downWhitePoint, downBlackPoint, freePoint);
            Point point = fiveAIByAqr.getBestPoint();
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
            isStartGame = true;
            isWhite = !isWhite;

        }
        return true;

    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawBroad(canvas);
        drawStone(canvas);
        if(MainActivity.isChess){
            mediaPlayerChess.seekTo(0);
            mediaPlayerChess.start();
        }
        checkGameOver();
        if (isGameOver) {


            gameOverDialog.setCancelable(false);
            if (isWhiteWin) {
               if(MainActivity.isDefeat) {
                   mediaPlayerDefeat.seekTo(0);
                   mediaPlayerDefeat.start();
               }
                gameOverDialog.setTitle("你输了");
            }
            else {
                if(MainActivity.isWin) {
                    mediaPlayerWin.seekTo(0);
                    mediaPlayerWin.start();
                }
                gameOverDialog.setTitle("你赢了");
            }
            gameOverDialog.setNegativeButton("结束", this);
            gameOverDialog.setPositiveButton("再来一局", this);

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


    @Override
    public void onClick(DialogInterface dialog, int which) {

        Message msg;
        switch (which) {
            case -1:
                if(!isWhiteWin) {
                    msg = new Message();
                    msg.what = 1;
                    msg.arg1 = downBlackPoint.size();
                    MainActivity.handler.sendMessage(msg);
                }
                if(mediaPlayerDefeat.isPlaying()) mediaPlayerDefeat.pause();
                if(mediaPlayerWin.isPlaying()) mediaPlayerWin.pause();

                refresh();
                dialog.dismiss();
                break;
            case -2:
                if(!isWhiteWin) {
                    msg = new Message();
                    msg.what = 1;
                    msg.arg1 = downBlackPoint.size();
                    MainActivity.handler.sendMessage(msg);
                }
                if(mediaPlayerDefeat.isPlaying()) mediaPlayerDefeat.pause();
                if(mediaPlayerWin.isPlaying()) mediaPlayerWin.pause();
                refresh();
                MainActivity.gameFlipper.setDisplayedChild(0);

                dialog.dismiss();
                break;
            default:
                break;
        }

    }
}
