package com.efrobot.salespromotion.activity;

import android.database.Cursor;

import com.efrobot.salespromotion.bean.ItemsContentBean;
import com.j256.ormlite.field.DatabaseField;

import java.io.Serializable;

/**
 * Created by zd on 2017/11/17.
 */
public class ModelContentBean implements Serializable {

    //{"_id integer primary key autoincrement", "itemNum integer", "sport text", "face integer", "action integer", "light text", "other text"}
    @DatabaseField(generatedId = true, columnName = "_id")
    private int id;
    @DatabaseField(columnName = "itemNum")
    private int itemNum;

    @DatabaseField(columnName = "modelName")
    private String modelName;
    @DatabaseField(columnName = "modelType")
    private int modelType;

    @DatabaseField(columnName = "itemType")
    private int itemType;
    //-1----无任何动作，1-----自定义动作，0------预设动作
    @DatabaseField(columnName = "sport")
    private String sport = "";
    @DatabaseField(columnName = "head")//头部数据
    private String head = "";
    @DatabaseField(columnName = "wheel")//轮子数据
    private String wheel = "";
    @DatabaseField(columnName = "wing")//翅膀数据
    private String wing = "";
    @DatabaseField(columnName = "face")//表情数据
    private String face = "";
    @DatabaseField(columnName = "action")//动作数据
    private String action = "";
    @DatabaseField(columnName = "light")//灯带数据
    private String light = "";
    @DatabaseField(columnName = "other")
    private String other = "";
    @DatabaseField(columnName = "media")
    private String media = "";
    @DatabaseField(columnName = "time")
    private String time = "";
    @DatabaseField(columnName = "music")
    private String music = "";
    @DatabaseField(columnName = "faceTime")//表情时间
    private String faceTime = "";
    @DatabaseField(columnName = "actionTime")//自定义时间
    private String actionTime = "";
    @DatabaseField(columnName = "actionSystemTime")//系统动作时间
    private String actionSystemTime = "";
    @DatabaseField(columnName = "maxTime")//时间
    private String maxTime = "";

    @DatabaseField(columnName = "openLightTime")
    private String openLightTime;
    @DatabaseField(columnName = "flickerLightTime")
    private String flickerLightTime;

    @DatabaseField(columnName = "startAppAction")//启动app的action
    private String startAppAction;

    @DatabaseField(columnName = "startAppName")//启动App的名称
    private String startAppName;

    @DatabaseField(columnName = "startGuestTimePart")//迎宾触发时间段限制
    private String startGuestTimePart;

    @DatabaseField(columnName = "alternateField")//迎宾备用字段
    private String alternateField;

    @DatabaseField(columnName = "danceName")//迎宾备用字段
    private String danceName;


    public ModelContentBean() {
    }

    public String getFaceTime() {
        return faceTime;
    }

    public void setFaceTime(String faceTime) {
        this.faceTime = faceTime;
    }

    public String getActionSystemTime() {
        return actionSystemTime;
    }

    public void setActionSystemTime(String actionSystemTime) {
        this.actionSystemTime = actionSystemTime;
    }

    public ModelContentBean(Cursor c) throws Exception {

        this.id = c.getInt(c.getColumnIndexOrThrow("_id"));
        this.itemNum = c.getInt(c.getColumnIndexOrThrow("itemNum"));

        this.modelName = c.getString(c.getColumnIndexOrThrow("modelName"));
        this.modelType = c.getInt(c.getColumnIndexOrThrow("modelType"));

        this.itemType = c.getInt(c.getColumnIndexOrThrow("itemType"));
        this.sport = c.getString(c.getColumnIndexOrThrow("sport"));
        this.face = c.getString(c.getColumnIndexOrThrow("face"));
        this.action = c.getString(c.getColumnIndexOrThrow("action"));
        this.light = c.getString(c.getColumnIndexOrThrow("light"));

        this.other = c.getString(c.getColumnIndexOrThrow("other"));
        this.media = c.getString(c.getColumnIndexOrThrow("media"));
        this.time = c.getString(c.getColumnIndexOrThrow("time"));
        this.music = c.getString(c.getColumnIndexOrThrow("music"));
        this.head = c.getString(c.getColumnIndexOrThrow("head"));
        this.wheel = c.getString(c.getColumnIndexOrThrow("wheel"));
        this.wing = c.getString(c.getColumnIndexOrThrow("wing"));
        this.openLightTime = c.getString(c.getColumnIndexOrThrow("openLightTime"));
        this.flickerLightTime = c.getString(c.getColumnIndexOrThrow("flickerLightTime"));
        this.faceTime = c.getString(c.getColumnIndexOrThrow("faceTime"));
        this.actionTime = c.getString(c.getColumnIndexOrThrow("actionTime"));
        this.actionSystemTime = c.getString(c.getColumnIndexOrThrow("actionSystemTime"));
        this.maxTime = c.getString(c.getColumnIndexOrThrow("maxTime"));
        this.startAppAction = c.getString(c.getColumnIndexOrThrow("startAppAction"));
        this.startAppName = c.getString(c.getColumnIndexOrThrow("startAppName"));
        this.startGuestTimePart = c.getString(c.getColumnIndexOrThrow("startGuestTimePart"));
        this.alternateField = c.getString(c.getColumnIndexOrThrow("alternateField"));
        this.danceName = c.getString(c.getColumnIndexOrThrow("danceName"));
    }

    public ModelContentBean(ItemsContentBean c) throws Exception {

        this.id = c.getId();
        this.itemNum = c.getItemNum();

//        this.modelName = c.getModelName();
//        this.modelType = c.getModelType();

        this.itemType = c.getItemType();
        this.sport = c.getSport();
        this.face = c.getFace();
        this.action = c.getAction();
        this.light = c.getLight();

        this.other = c.getOther();
        this.media = c.getMedia();
        this.time = c.getTime();
        this.music = c.getMusic();
        this.head = c.getHead();
        this.wheel = c.getWheel();
        this.wing = c.getWing();
        this.openLightTime = c.getOpenLightTime();
        this.flickerLightTime = c.getFlickerLightTime();
        this.faceTime = c.getFaceTime();
        this.actionTime = c.getActionTime();
        this.actionSystemTime = c.getActionSystemTime();
        this.maxTime = c.getMaxTime();
        this.startAppAction = c.getStartAppAction();
        this.startAppName = c.getStartAppName();
        this.startGuestTimePart = c.getStartGuestTimePart();
        this.alternateField = c.getAlternateField();
        this.danceName = c.getDanceName();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getItemNum() {
        return itemNum;
    }

    public void setItemNum(int itemNum) {
        this.itemNum = itemNum;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public int getModelType() {
        return modelType;
    }

    public void setModelType(int modelType) {
        this.modelType = modelType;
    }

    public int getItemType() {
        return itemType;
    }

    public void setItemType(int itemType) {
        this.itemType = itemType;
    }


    public String getSport() {
        return sport;
    }

    public void setSport(String sport) {
        this.sport = sport;
    }

    public String getFace() {
        return face;
    }

    public void setFace(String face) {
        this.face = face;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getLight() {
        return light;
    }

    public void setLight(String light) {
        this.light = light;
    }

    public String getOther() {
        return other;
    }

    public void setOther(String other) {
        this.other = other;
    }

    public String getMedia() {
        return media;
    }

    public String getMusic() {
        return music;
    }

    public void setMedia(String media) {
        this.media = media;
    }

    public void setMusic(String music) {
        this.music = music;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setHead(String head) {
        this.head = head;
    }

    public String getHead() {
        return head;
    }

    public String getMaxTime() {
        return maxTime;
    }

    public void setMaxTime(String maxTime) {
        this.maxTime = maxTime;
    }

    public void setWheel(String wheel) {
        this.wheel = wheel;
    }

    public String getWheel() {
        return wheel;
    }

    public void setWing(String wing) {
        this.wing = wing;
    }

    public String getWing() {
        return wing;
    }

    public String getOpenLightTime() {
        return openLightTime;
    }

    public void setOpenLightTime(String openLightTime) {
        this.openLightTime = openLightTime;
    }

    public String getFlickerLightTime() {
        return flickerLightTime;
    }

    public void setFlickerLightTime(String flickerLightTime) {
        this.flickerLightTime = flickerLightTime;
    }

    public String getActionTime() {
        return actionTime;
    }

    public void setActionTime(String actionTime) {
        this.actionTime = actionTime;
    }

    public String getStartAppAction() {
        return startAppAction;
    }

    public void setStartAppAction(String startAppAction) {
        this.startAppAction = startAppAction;
    }

    public String getStartAppName() {
        return startAppName;
    }

    public void setStartAppName(String startAppName) {
        this.startAppName = startAppName;
    }

    public String getStartGuestTimePart() {
        return startGuestTimePart;
    }

    public void setStartGuestTimePart(String startGuestTimePart) {
        this.startGuestTimePart = startGuestTimePart;
    }

    public String getAlternateField() {
        return alternateField;
    }

    public void setAlternateField(String alternateField) {
        this.alternateField = alternateField;
    }

    public String getDanceName() {
        return danceName;
    }

    public void setDanceName(String danceName) {
        this.danceName = danceName;
    }

    @Override
    public String toString() {
        return "ItemsContentBean{" +
                "id=" + id +
                ", itemNum=" + itemNum +
                ", itemType=" + itemType +
                ", sport='" + sport + '\'' +
                ", face=" + face +
                ", action=" + action +
                ", light='" + light + '\'' +
                ", other='" + other + '\'' +
                ", media='" + media + '\'' +
                ", time='" + time + '\'' +
                ", music='" + music + '\'' +
                ", head='" + head + '\'' +
                ", wheel='" + wheel + '\'' +
                ", wing='" + wing + '\'' +
                ", openLightTime='" + openLightTime + '\'' +
                ", flickerLightTime='" + flickerLightTime + '\'' +
                ", faceTime='" + faceTime + '\'' +
                ", actionTime='" + actionTime + '\'' +
                ", actionSystemTime='" + actionSystemTime + '\'' +
                ", maxTime='" + maxTime + '\'' +
                ", startAppAction='" + startAppAction + '\'' +
                ", startAppName='" + startAppName + '\'' +
                ", startGuestTimePart='" + startGuestTimePart + '\'' +
                ", alternateField='" + alternateField + '\'' +
                '}';
    }
}