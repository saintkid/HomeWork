package com.example.aqr.homework.dao;

import android.graphics.Point;
import android.util.Log;


import com.example.aqr.homework.domain.SaveGame;
import com.example.aqr.homework.domain.GameBroad;
import com.example.aqr.homework.domain.User;


import java.util.ArrayList;

import java.util.List;


import cn.bmob.v3.BmobBatch;
import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BatchResult;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListListener;
import cn.bmob.v3.listener.SaveListener;

/**
 * Created by Aqr on 2017/5/28.
 */

public class GameDao {
    private boolean isSave = true;
    private boolean isSelect = true;
    private int time = 0;
    private ArrayList<Point> cPoints = new ArrayList<>();
    private ArrayList<Point> hPoints = new ArrayList<>();
    private ArrayList<GameBroad> gameBroads = new ArrayList<>();
    private ArrayList<SaveGame> id = new ArrayList<>();


    public GameDao() {

    }

    //User manager
    public boolean selectUser(String name, String password) {
        isSelect = true;


        BmobQuery<User> query = new BmobQuery<>();
        query.addWhereEqualTo("name", name);
        query.addWhereEqualTo("password", password);
        query.findObjects(new FindListener<User>() {
            @Override
            public void done(List<User> list, BmobException e) {
                if (e == null) {
                    if (list.size() == 0) {
                        isSelect = false;
                    }
                } else {
                    isSelect = false;
                }

            }
        });
        sleep(3000);
        return isSelect;

    }

    public boolean saveUser(String name, String password) {
        isSave = true;
        User user = new User();
        user.setName(name);
        user.setPassword(password);
        user.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if (e == null) {

                } else {
                    isSave = false;
                }
            }
        });
        sleep(3000);
        return isSave;
    }

    //GameBroad manager
    public void selectGameBroad() {
        gameBroads.clear();

        BmobQuery<GameBroad> query = new BmobQuery<>();
        query.order("time,moveNumbers");
        query.findObjects(new FindListener<GameBroad>() {
            @Override
            public void done(List<GameBroad> list, BmobException e) {
                if (e == null) {

                    int i = 0;
                    for (GameBroad gameBroad : list) {
                        if (i < 10) {
                            gameBroads.add(gameBroad);
                        } else {
                            break;
                        }
                        i++;
                    }


                }
            }
        });
    }

    public ArrayList<GameBroad> getGameBroads() {
        return gameBroads;
    }

    public boolean saveGameBroad(String name, int time, int numbers) {
        isSave = true;
        GameBroad gameBroad = new GameBroad();
        gameBroad.setName(name);
        gameBroad.setTime(time);
        gameBroad.setMoveNumbers(numbers);
        gameBroad.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if (e == null) {

                } else {
                    isSave = false;
                }
            }
        });
        sleep(3000);
        return isSave;

    }

    //SaveGame manager
    public void selectSaveGame(String name) {
        cPoints.clear();
        hPoints.clear();
        id.clear();


        BmobQuery<SaveGame> query = new BmobQuery<>();
        query.addWhereEqualTo("name", name);

        query.findObjects(new FindListener<SaveGame>() {
            @Override
            public void done(List<SaveGame> list, BmobException e) {
                if (e == null) {
                    //Log.d("TestSaveGame","select: "+list.size());

                    for (SaveGame saveGame : list) {
                        Point point = new Point(saveGame.getxPoint(), saveGame.getyPoint());
                        time = saveGame.getTime();

                        // Log.d("TestSaveGame","Point: "+point);
                        id.add(saveGame);
                        if (saveGame.getType() == 0) {

                            hPoints.add(point);

                        } else if (saveGame.getType() == 1) {
                            cPoints.add(point);
                        }

                    }


                } else {
                    Log.d("TestSaveGame", "ERROR:\n" + "Message: " + e.getMessage() + "\nID:" + e.getErrorCode());
                }
            }
        });


    }

    public ArrayList<Point> getHPoints() {
        for (Point p : hPoints) {
            Log.d("TestSaveGame", "get0Point: " + p);
        }
        return hPoints;
    }

    public ArrayList<Point> getCPoints() {
        for (Point p : cPoints) {
            Log.d("TestSaveGame", "get1Point: " + p);
        }
        return cPoints;
    }

    public int getTime() {
        return time;
    }

    public boolean saveGame(String name, int type, ArrayList<Point> points, int time) {
        isSave = true;
        List<BmobObject> saveGames = new ArrayList<>();

        new BmobBatch().deleteBatch(saveGames).doBatch(new QueryListListener<BatchResult>() {
            @Override
            public void done(List<BatchResult> list, BmobException e) {

            }
        });
        for (Point point : points) {
            SaveGame saveGame = new SaveGame();
            saveGame.setName(name);
            saveGame.setType(type);
            saveGame.setxPoint(point.x);
            saveGame.setyPoint(point.y);
            saveGame.setTime(time);
            saveGames.add(saveGame);
        }
        new BmobBatch().insertBatch(saveGames).doBatch(new QueryListListener<BatchResult>() {
            @Override
            public void done(List<BatchResult> list, BmobException e) {
                if (e == null) {


                    for (int i = 0; i < list.size(); i++) {
                        BmobException ex = list.get(i).getError();
                        if (ex == null) {

                        } else {
                            isSave = false;
                        }

                    }
                } else {

                }

            }
        });
        sleep(2000);
        return isSave;

    }

    public void deleteGame() {
        List<BmobObject> deleteGames = new ArrayList<>();
        deleteGames.addAll(id);
        new BmobBatch().deleteBatch(deleteGames).doBatch(new QueryListListener<BatchResult>() {
            @Override
            public void done(List<BatchResult> list, BmobException e) {

            }
        });

        sleep(3000);
    }

    public void sleep(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }


}
