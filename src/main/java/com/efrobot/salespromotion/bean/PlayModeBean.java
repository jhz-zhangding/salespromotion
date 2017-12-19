package com.efrobot.salespromotion.bean;

import android.database.Cursor;

import com.j256.ormlite.field.DatabaseField;

import java.io.Serializable;

/**
 * Created by zd on 2017/12/18.
 */
public class PlayModeBean implements Serializable {

    @DatabaseField(generatedId = true, columnName = "_id")
    private int id;

    @DatabaseField(columnName = "modelName")
    private String modelName;

    @DatabaseField(columnName = "itemNum")
    private int itemNum;

    @DatabaseField(columnName = "playMode")
    private int playMode;

    public PlayModeBean() {
    }

    public PlayModeBean(Cursor c) throws Exception {
        this.id = c.getInt(c.getColumnIndexOrThrow("_id"));
        this.modelName = c.getString(c.getColumnIndexOrThrow("modelName"));
        this.itemNum = c.getInt(c.getColumnIndexOrThrow("itemNum"));
        this.playMode = c.getInt(c.getColumnIndexOrThrow("playMode"));

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public int getItemNum() {
        return itemNum;
    }

    public void setItemNum(int itemNum) {
        this.itemNum = itemNum;
    }

    public int getPlayMode() {
        return playMode;
    }

    public void setPlayMode(int playMode) {
        this.playMode = playMode;
    }
}
