package com.efrobot.salespromotion.utils;

import android.util.Log;

import com.efrobot.library.mvp.utils.L;
import com.efrobot.salespromotion.base.CompressStatus;
import com.efrobot.salespromotion.interfaces.IZipFileListener;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.progress.ProgressMonitor;

import java.io.File;

public class ZipUtil {
    private static final String TAG = "ZipUtil";

    /**
     * 解压工具类
     *
     * @throws ZipException
     */
    public static void unZipFileWithProgress(String fromPath, String toPath, boolean isDelete, IZipFileListener listener) {
        Log.e(TAG, "begin extract..from path=" + fromPath + " to path=" + toPath);
        String name = "";
        try {
            File zipFile = new File(fromPath);
            ZipFile zFile = new ZipFile(zipFile);
            zFile.setFileNameCharset("GBK");
            File fileFolder = new File(toPath);
            if (fileFolder.isDirectory() && !fileFolder.exists()) {
                fileFolder.mkdirs();
            }
            name = zFile.getFile().getName();
            if (zFile.isValidZipFile()) {
                ProgressMonitor progressMonitor = zFile.getProgressMonitor();
                zFile.setRunInThread(false);
                zFile.extractAll(toPath);
                if (listener != null) {
                    listener.onStart();
                }
                int precentDone = 0;
                boolean isZip = true;
                int progress = -1;
                int count = -1;
                while (isZip) {
                    // 每隔500ms,发送一个解压进度出去
                    Thread.sleep(500);
                    precentDone = progressMonitor.getPercentDone();
                    Log.e(TAG, "unZipFileWithProgress:progress= " + progress + " precentDone=" + precentDone);
                    //如果4秒解压进度未变化视为异常
                    if (progress == precentDone) {
                        count++;
                        if (count >= 8) {
                            isZip = false;
                            if (listener != null) {
                                listener.onError(CompressStatus.ERROR_UNKNOWN, "未知异常！");
                                return;
                            }
                        }
                    }
                    progress = precentDone;
                    if (precentDone >= 100) {
                        isZip = false;
                    }
                    if (listener != null) {
                        listener.onProcess(progress);
                    }
                }
                if (listener != null) {
                    if (isDelete) {
                        zipFile.delete();
                    }
                    listener.onSuccess(toPath);
                }
            } else {

                Log.e(TAG, "unZipFileWithProgress:bad name= " + name);
                if (listener != null) {
                    listener.onError(CompressStatus.PACKAGE_BAD, name);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (listener != null) {
                listener.onError(CompressStatus.ERROR_UNKNOWN, name);
            }
        }
    }

    public static int synUnCompressFile(String fromPath, String toPath, boolean isDelete) {
        L.i(TAG, "begin unCompress..from path=" + fromPath + " to path=" + toPath);
        try {
            File zipFile = new File(fromPath);
            ZipFile zFile = new ZipFile(zipFile);
            zFile.setFileNameCharset("GBK");
            File fileFolder = new File(toPath);
            if (fileFolder.isDirectory() && !fileFolder.exists()) {
                fileFolder.mkdirs();
            }
            if (zFile.isValidZipFile()) {
                ProgressMonitor progressMonitor = zFile.getProgressMonitor();
                zFile.setRunInThread(false);
                zFile.extractAll(toPath);
                int percentDone = 0;
                boolean isZip = true;
                int progress = -1;
                int count = -1;
                while (isZip) {
                    // 每隔500ms
                    Thread.sleep(500);
                    percentDone = progressMonitor.getPercentDone();
                    L.i(TAG, "progress= " + progress + " percentDone=" + percentDone);
                    //如果4秒解压进度未变化视为异常
                    if (progress == percentDone) {
                        count++;
                        if (count >= 8) {
                            return CompressStatus.ERROR_UNKNOWN;
                        }
                    }
                    progress = percentDone;
                    if (percentDone >= 100) {
                        isZip = false;
                    }
                }
                if (isDelete) {
                    zipFile.delete();
                }
            } else {
                return CompressStatus.PACKAGE_BAD;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return CompressStatus.ERROR_UNKNOWN;
        }
        return CompressStatus.SUCCESS;
    }

}
