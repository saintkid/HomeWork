package com.example.aqr.homework;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;
import android.widget.ViewFlipper;

import com.example.aqr.homework.dao.GameDao;
import com.example.aqr.homework.domain.GameBroad;
import com.example.aqr.homework.tool.PointGrade;
import com.example.aqr.homework.view.FiveInaRowView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

import cn.bmob.v3.Bmob;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    //beforegame
    private TextView userName;
    private Button newGame;
    private Button oldGame;
    private Button gameBroad;
    private Button setting;
    private Button quitApp;
    private ArrayList<GameBroad> broad = new ArrayList<>();
    //ongame
    private static int time = 0;
    private static int numbers = 0;
    private FiveInaRowView fiveInaRowView;
    private Button quitGame;
    private Button saveGame;
    private static TextView gameTime;
    private static TextView gameMoves;
    private ArrayList<Point> whitePoints = new ArrayList<>();
    private ArrayList<Point> blackPoints = new ArrayList<>();
    //main
    private Toolbar toolbar;
    private static boolean isPause = false;
    private static String name = "gg";
    public static ViewFlipper gameFlipper;
    private MediaPlayer mediaPlayerBg = null;
    private static GameDao gameDao = new GameDao();
    private SharedPreferences sharedPreferences;
    public static boolean isChess = false;
    public static boolean isWin = false;
    public static boolean isDefeat = false;
    public boolean isBg = false;
    private AssetManager assetManager;


    public static Handler handler = new Handler() {
        @Override
        public void handleMessage(final Message msg) {
            switch (msg.what) {
                case 0:
                    //更新时间和落子数
                    if (!isPause) {
                        time += 100;
                        numbers = msg.arg1;

                        gameTime.setText("用时: " + time / 1000);
                        gameMoves.setText("落子数: " + numbers);
                    }


                    break;
                case 1:
                    //游戏结束保存结果

                    gameDao.saveGameBroad(name, time / 1000, msg.arg1);

                    gameDao.selectSaveGame(name);
                    gameDao.selectGameBroad();

                    break;
                case 2:
                    time = 0;
                    numbers = 0;
                    gameTime.setText("用时: " + time / 1000);
                    gameMoves.setText("落子数: " + numbers);
                    break;
                default:
                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_main);
        Bmob.initialize(this, "ed111e46bceee75b4fb083e8d183ea6a");
        init();


    }

    protected void init() {
        //beforegame
        gameDao.selectSaveGame(name);
        gameDao.selectGameBroad();

        userName = (TextView) findViewById(R.id.gameUser);
        userName.post(new Runnable() {
            @Override
            public void run() {
                userName.setText("欢迎你，" + name);
            }
        });
        newGame = (Button) findViewById(R.id.newGame);
        newGame.setOnClickListener(this);
        oldGame = (Button) findViewById(R.id.oldGame);
        oldGame.setOnClickListener(this);
        gameBroad = (Button) findViewById(R.id.gameBoard);
        gameBroad.setOnClickListener(this);
        setting = (Button) findViewById(R.id.setting);
        setting.setOnClickListener(this);
        quitApp = (Button) findViewById(R.id.quitAPP);
        quitApp.setOnClickListener(this);
        //ongame
        quitGame = (Button) findViewById(R.id.quitGame);
        quitGame.setOnClickListener(this);
        saveGame = (Button) findViewById(R.id.save);
        saveGame.setOnClickListener(this);
        fiveInaRowView = (FiveInaRowView) findViewById(R.id.fiveinrow);
        gameTime = (TextView) findViewById(R.id.gameTime);
        gameMoves = (TextView) findViewById(R.id.moveNumbers);
        //game
        gameFlipper = (ViewFlipper) findViewById(R.id.gameManager);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setLogo(R.drawable.ic_logo);
        toolbar.setTitle("五子棋");
        assetManager = getAssets();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        try {
            AssetFileDescriptor bg = assetManager.openFd("bg.mp3");
            mediaPlayerBg = new MediaPlayer();
            mediaPlayerBg.setDataSource(bg.getFileDescriptor(), bg.getStartOffset(), bg.getLength());
            mediaPlayerBg.prepare();
            mediaPlayerBg.setLooping(true);




        } catch (IOException e) {
            e.printStackTrace();
        }


        new Thread(new Runnable() {
            @Override
            public void run() {

                while (true) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if(gameFlipper.getDisplayedChild()==0&&mediaPlayerBg.isPlaying()){
                        mediaPlayerBg.pause();
                    }


                }
            }
        }).start();


    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.newGame:

                gameFlipper.setDisplayedChild(1);


                if (isBg) {
                    mediaPlayerBg.seekTo(0);
                    mediaPlayerBg.start();
                }

                break;
            case R.id.oldGame:
                if (isBg) {
                    mediaPlayerBg.seekTo(0);
                    mediaPlayerBg.start();
                }

                blackPoints.clear();
                whitePoints.clear();
                blackPoints.addAll(gameDao.getHPoints());
                whitePoints.addAll(gameDao.getCPoints());
                time = gameDao.getTime() * 1000;

                if (blackPoints.size() > 0 && whitePoints.size() > 0) {

                    fiveInaRowView.initOldGame(whitePoints, blackPoints);
                    gameFlipper.setDisplayedChild(1);
                    if (isBg) {
                        mediaPlayerBg.setLooping(true);
                        mediaPlayerBg.start();
                    }
                } else {
                    Toast toast = Toast.makeText(MainActivity.this, "没有保存的游戏！", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);

                    toast.show();
                }

                break;
            case R.id.gameBoard:
                broad.clear();

                broad.addAll(gameDao.getGameBroads());
                if (broad != null && broad.size() > 0) {
                    int i = broad.size();
                    String[] myBroad = new String[i];
                    for (int j = 0; j < i; j++) {
                        myBroad[j] = "玩家： " + broad.get(j).getName() + "用时： " + broad.get(j).getTime() + "落子数： " + broad.get(j).getMoveNumbers();
                    }
                    new AlertDialog.Builder(MainActivity.this).setTitle("排行榜")
                            .setItems(myBroad, null)
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .setCancelable(false)
                            .show();

                } else {
                    Toast toast = Toast.makeText(MainActivity.this, "没有榜单信息！", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();

                }

                break;
            case R.id.setting:
                //跳转到偏好设置界面，比如设置音乐开启/关闭
                Intent intent = new Intent(MainActivity.this, SettingActivity.class);
                startActivity(intent);

                break;
            case R.id.quitAPP:
                MainActivity.this.finish();
                break;
            //ongame
            case R.id.quitGame:
                fiveInaRowView.refresh();
                gameDao.selectSaveGame(name);
                gameDao.selectGameBroad();
                gameFlipper.setDisplayedChild(0);
                break;
            case R.id.save:
                isPause = true;
                gameDao.deleteGame();


                if (gameDao.saveGame(name, 0, fiveInaRowView.downWhitePoint, time / 1000) &&
                        gameDao.saveGame(name, 1, fiveInaRowView.downBlackPoint, time / 1000)
                        ) {
                    Toast toast = Toast.makeText(MainActivity.this, "保存游戏成功！", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    isPause = false;
                }


                break;
            default:
                break;
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        isChess = sharedPreferences.getBoolean("chess", false);
        isWin = sharedPreferences.getBoolean("win", false);
        isDefeat = sharedPreferences.getBoolean("defeat", false);
        isBg = sharedPreferences.getBoolean("bg", false);
        name = sharedPreferences.getString("user","gg");
        isPause = false;
    }

    @Override
    protected void onPause() {
        super.onPause();

        isPause = true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(fiveInaRowView.mediaPlayerDefeat!=null){
           fiveInaRowView.mediaPlayerDefeat.stop();
           fiveInaRowView.mediaPlayerDefeat.release();
        }
        if( mediaPlayerBg!=null){
            mediaPlayerBg.stop();
            mediaPlayerBg.release();
        }
        if(fiveInaRowView.mediaPlayerWin!=null){
           fiveInaRowView.mediaPlayerWin.stop();
           fiveInaRowView.mediaPlayerWin.release();
        }
        if(fiveInaRowView.mediaPlayerChess!=null){
           fiveInaRowView.mediaPlayerChess.stop();
           fiveInaRowView.mediaPlayerChess.release();
        }
    }
}
