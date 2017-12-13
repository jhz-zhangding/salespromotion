package com.efrobot.salespromotion.bean;

import android.database.Cursor;

import com.j256.ormlite.field.DatabaseField;

import java.io.Serializable;

/**
 * Created by zd on 2017/11/17.
 */
public class ModelNameBean  implements Serializable {

    @DatabaseField(generatedId = true, columnName = "_id")
    private int id;
    @DatabaseField(columnName = "modelName")
    private String modelName;
    @DatabaseField(columnName = "modelType")
    private int modelType;

    public ModelNameBean(Cursor c) throws Exception {
        this.id = c.getInt(c.getColumnIndexOrThrow("_id"));
        this.modelName = c.getString(c.getColumnIndexOrThrow("modelName"));
        this.modelType = c.getInt(c.getColumnIndexOrThrow("modelType"));
    }

    public ModelNameBean() {
    }

    public ModelNameBean(String name, int modelTyep) throws Exception {
        this.modelName = name;
        this.modelType = modelTyep;
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

    public int getModelType() {
        return modelType;
    }

    public void setModelType(int modelType) {
        this.modelType = modelType;
    }
}
