package com.efrobot.salespromotion.interfaces;

/**
 * Created by lhy on 2017/6/30
 */
public interface IZipFileListener {
    void onStart();

    void onProcess(int current);

    void onSuccess(String filePath);

    void onError(int code, String message);
}
