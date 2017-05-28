package com.example.aqr.homework;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.example.aqr.homework.dao.GameDao;
import com.example.aqr.homework.tool.GameBroad;
import com.example.aqr.homework.view.FiveInaRowView;

import java.util.ArrayList;
import java.util.Objects;

public class MainActivity extends Activity implements View.OnClickListener{

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
    private ArrayList<Point> freePoints = new ArrayList<>();
    //main
    private static String name;
    public static ViewFlipper gameFlipper;
    private static GameDao gameDao = new GameDao();
    public static Handler handler = new Handler(){
        @Override
        public void handleMessage(final Message msg) {
            switch(msg.what){
                case 0:
                    //更新时间和落子数
                    time+=100;
                    numbers+=msg.arg1;
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            gameTime.setText("用时: "+time/1000);
                            gameMoves.setText("落子数: "+numbers);
                        }
                    });


                    break;
                case 1:
                    //游戏结束保存结果
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            gameDao.saveGameBroad(name,msg.arg1,msg.arg2);
                        }
                    }).start();

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
        init();


    }
    protected void init(){
        //beforegame
        userName = (TextView) findViewById(R.id.gameUser);
        userName.post(new Runnable() {
            @Override
            public void run() {
                userName.setText("欢迎你，用户名");
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
        saveGame =(Button) findViewById(R.id.save);
        saveGame.setOnClickListener(this);
        fiveInaRowView = (FiveInaRowView) findViewById(R.id.fiveinrow);
        gameTime = (TextView) findViewById(R.id.gameTime);
        gameMoves = (TextView) findViewById(R.id.moveNumbers);
        //game
        gameFlipper = (ViewFlipper) findViewById(R.id.gameManager);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            //beforegame
            case R.id.newGame:
                gameFlipper.setDisplayedChild(1);
                break;
            case R.id.oldGame:
                whitePoints = gameDao.selectSaveGame(name, 1);
                blackPoints = gameDao.selectSaveGame(name, 0);
                freePoints = gameDao.selectSaveGame(name, 2);
                if(freePoints!=null) {
                    fiveInaRowView.initOldGame(whitePoints, blackPoints, freePoints);
                }
                else{
                    Toast toast = Toast.makeText(MainActivity.this,"没有保存的游戏！",Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER,0,0);

                    toast.show();

                }
                break;
            case R.id.gameBoard:
                broad = gameDao.selectGameBroad();
                if(broad != null){

                }
                else{
                    Toast toast = Toast.makeText(MainActivity.this,"没有榜单信息！",Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER,0,0);

                    toast.show();

                }
                break;
            case R.id.setting:
                //跳转到偏好设置界面，比如设置音乐开启/关闭

                break;
            case R.id.quitAPP:
                MainActivity.this.finish();
                break;
            //ongame
            case R.id.quitGame:
                fiveInaRowView.refresh();
                gameFlipper.setDisplayedChild(0);
                break;
            case R.id.save:
                gameDao.saveGame(name, 0, fiveInaRowView.downWhitePoint);
                gameDao.saveGame(name, 1, fiveInaRowView.downBlackPoint);
                gameDao.saveGame(name, 2, fiveInaRowView.freePoint);
                break;
            default:
                break;
        }

    }


}
