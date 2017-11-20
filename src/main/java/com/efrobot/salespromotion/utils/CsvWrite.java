package com.efrobot.salespromotion.utils;

import android.database.Cursor;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by shuai on 2016/9/9.
 */
public class CsvWrite {

    public static void ExportToCSV(Cursor c, String fileName) {

        int rowCount;
        int colCount;
        FileWriter fw;
        BufferedWriter bfw;
        //获取sd卡根目录
        String sdCardDir = Environment.getExternalStorageDirectory() + "/efrobot/salespromotion/compression/";
        File file = new File(sdCardDir);
        if (!file.exists())
            file.mkdirs();
        //保存文件目录
        File saveFile = new File(file, fileName);
        try {

            rowCount = c.getCount();
            colCount = c.getColumnCount();
            fw = new FileWriter(saveFile);
            bfw = new BufferedWriter(fw);
            bfw.write(0xFEFF);
            if (rowCount > 0) {
                c.moveToFirst();
                // 写入表头
                for (int i = 0; i < colCount; i++) {
                    bfw.write('&' + c.getColumnName(i));
                }
                // 写好表头后换行
                bfw.newLine();
                // 写入数据
                for (int i = 0; i < rowCount; i++) {
                    c.moveToPosition(i);

                    Log.v("导出数据", "正在导出第" + (i + 1) + "条");
                    for (int j = 0; j < colCount; j++) {
                        bfw.write('&' + c.getString(j));

                    }
                    bfw.newLine();
                }
            }
            // 将缓存数据写入文件
            bfw.flush();
            // 释放缓存
            bfw.close();
            Log.v("导出数据", "导出完毕！");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            c.close();
        }
    }

    /**
     * 名称相同，路径不同的文件路径对应
     *
     * @param mediaPath 所有的文件路径
     * @param fileName
     */
    public static void ExportToCopyPath(ArrayList<String> mediaPath, String fileName) throws IOException {
        FileWriter fw;
        BufferedWriter bfw;
        //获取sd卡根目录
        String sdCardDir = Environment.getExternalStorageDirectory() + "/efrobot/salespromotion/compression/";
        File file1 = new File(sdCardDir);
        if (!file1.exists())
            file1.mkdirs();
        //保存文件目录
        File saveFile = new File(file1, fileName);
        if (saveFile.exists()) {
            saveFile.delete();
        }
        saveFile.createNewFile();

        fw = new FileWriter(saveFile);
        bfw = new BufferedWriter(fw);
        bfw.write(0xFEFF);
        bfw.write('&' + "path");
        bfw.write('&' + "name");
        bfw.newLine();

        if (mediaPath == null || mediaPath.isEmpty())
            return;

        HashMap<String, String> pathMap = new HashMap<>();
        int size = mediaPath.size();
        for (int i = 0; i < size; i++) {
            String s = mediaPath.get(i);
            if (!TextUtils.isEmpty(s) && !"null".equals(s.toLowerCase())) {
                File file = new File(s);
                String filename = file.getName();
                if (pathMap.containsKey(filename)) {
                    if (!pathMap.get(filename).equals(s)) {
                        int index = filename.lastIndexOf(".");
                        if (index != -1) {
                            String name = filename.substring(0, index);
                            String type = filename.substring(index, filename.length());
//                            String time = getTime(System.currentTimeMillis());
                            pathMap.put(name + "-" + i + "-" + System.currentTimeMillis() + type, s);
                        }
                    }
                } else {
                    pathMap.put(filename, s);
                }
            }
        }

        try {
            Iterator iter = pathMap.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry entry = (Map.Entry) iter.next();
                String val = (String) entry.getValue();
                bfw.write('&' + val);
                String key = (String) entry.getKey();
                bfw.write('&' + key);
                bfw.newLine();
            }
            // 将缓存数据写入文件
            bfw.flush();
            // 释放缓存
            bfw.close();
            Log.v("路径对应文件", "写入完毕");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
