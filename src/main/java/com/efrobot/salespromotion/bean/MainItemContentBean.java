package com.efrobot.salespromotion.bean;

import android.database.Cursor;

import com.j256.ormlite.field.DatabaseField;

import java.io.Serializable;

/**
 * Created by zd on 2017/11/13.
 */
public class MainItemContentBean implements Serializable {

    @DatabaseField(generatedId = true, columnName = "_id") private int id;

    @DatabaseField(columnName = "itemType")private int itemType;

    @DatabaseField(columnName = "goodsName")private String goodsName;

    @DatabaseField(columnName = "goodsGroup")private String goodsGroup;

    @DatabaseField(columnName = "goodsDescription")private String goodsDescription;

    @DatabaseField(columnName = "spareOne")private String spareOne;

    @DatabaseField(columnName = "spareTwo")private String spareTwo;

    public MainItemContentBean(Cursor c) throws Exception {
        this.id = c.getInt(c.getColumnIndexOrThrow("_id"));
        this.itemType = c.getInt(c.getColumnIndexOrThrow("itemType"));
        this.goodsName = c.getString(c.getColumnIndexOrThrow("goodsName"));
        this.goodsGroup = c.getString(c.getColumnIndexOrThrow("goodsGroup"));
        this.goodsDescription = c.getString(c.getColumnIndexOrThrow("goodsDescription"));
        this.spareOne = c.getString(c.getColumnIndexOrThrow("spareOne"));
        this.spareTwo = c.getString(c.getColumnIndexOrThrow("spareTwo"));

    }

    public MainItemContentBean() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getItemType() {
        return itemType;
    }

    public void setItemType(int itemType) {
        this.itemType = itemType;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public String getGoodsGroup() {
        return goodsGroup;
    }

    public void setGoodsGroup(String goodsGroup) {
        this.goodsGroup = goodsGroup;
    }

    public String getGoodsDescription() {
        return goodsDescription;
    }

    public void setGoodsDescription(String goodsDescription) {
        this.goodsDescription = goodsDescription;
    }

    public String getSpareOne() {
        return spareOne;
    }

    public void setSpareOne(String spareOne) {
        this.spareOne = spareOne;
    }

    public String getSpareTwo() {
        return spareTwo;
    }

    public void setSpareTwo(String spareTwo) {
        this.spareTwo = spareTwo;
    }
}
