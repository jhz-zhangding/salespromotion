package com.efrobot.salespromotion.utils;

import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.base.utils.L;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.URLDecoder;


/**
 * 文件操作
 * Created by Administrator on 2016/11/14.
 */

public class DataFileUtils {
    private static final String TAG = "FileUtils";

    /**
     * 在SD卡上创建文件夹
     */
    //创建文件
    public static void createAllFileOnUSB(String robotPath, String copuUsbPath) {

        File file = new File(copuUsbPath);
        String path = file.getParent();
        File filePath = new File(path);
        if (!filePath.exists()) {
            filePath.mkdirs();
        }
        if (file.exists()) {
            file.delete();
        }
        copyOneFile(robotPath, copuUsbPath);
    }

    /**
     * 复制单个文件
     *
     * @param oldPath String 原文件路径 如：c:/fqf.txt
     * @param newPath String 复制后路径 如：f:/fqf.txt
     * @return boolean
     */
    public static void copyOneFile(String oldPath, String newPath) {
        try {
            int bytesum = 0;
            int byteread = 0;
            File oldfile = new File(oldPath);
            if (oldfile.exists()) { //文件存在时
                InputStream inStream = new FileInputStream(oldPath); //读入原文件
                FileOutputStream fs = new FileOutputStream(newPath);
                byte[] buffer = new byte[1024];
                while ((byteread = inStream.read(buffer)) != -1) {
                    bytesum += byteread; //字节数 文件大小
                    fs.write(buffer, 0, byteread);
                }
                inStream.close();
            }
        } catch (Exception e) {
            System.out.println("复制单个文件操作出错");
            e.printStackTrace();

        }

    }


    public static boolean stringToFile(String path, String fileName, String data) {
        boolean flag = true;
        BufferedReader reader = null;
        BufferedWriter writer = null;
        try {
            File distFile = createFileOnSDCard(fileName, path);
            if (!distFile.getParentFile().exists()) {
                distFile.getParentFile().mkdirs();
            }
            reader = new BufferedReader(new StringReader(URLDecoder.decode(data, "UTF-8")));
            writer = new BufferedWriter(new FileWriter(distFile));
            char buf[] = new char[1024]; //字符缓冲区
            int len;
            while ((len = reader.read(buf)) != -1) {
                writer.write(buf, 0, len);
            }
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
            flag = false;
        } finally {
            try {
                if (null != reader) {
                    reader.close();
                    reader = null;
                }
                if (null != writer) {
                    writer.close();
                    writer = null;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return flag;
    }


    //删除整个文件夹下的数据
    static void deleteAllFiles(File root) {
        File files[] = root.listFiles();
        if (files != null)
            for (File f : files) {
                if (f.isDirectory()) { // 判断是否为文件夹
                    deleteAllFiles(f);
                    try {
                        f.delete();
                    } catch (Exception e) {
                    }
                } else {
                    if (f.exists()) { // 判断是否存在
                        deleteAllFiles(f);
                        try {
                            f.delete();
                        } catch (Exception e) {
                        }
                    }
                }
            }
        if (root.exists()) {
            root.delete();
        }
    }


    public static boolean createFile(String sDir) {
        File destDir = new File(sDir);
        return destDir.exists() || destDir.mkdirs();
    }

    /**
     * 在SD卡上创建文件
     */
    //创建文件
    public static File createFileOnSDCard(String fileName, String dir) throws IOException {
        File file = new File(dir + "/" + fileName);
        if (createFile(dir)) {
            if (file.exists()) {
                file.delete();
            }
            file.createNewFile();
        }
        return file;

    }


    /**
     * 获取文件大小
     *
     * @param file
     * @return
     */
    public static long getFileSize(String file) {
        L.i(TAG, "Begin getFileSize");
        long size = -1;
        File f = new File(file);
        if (f.exists() && f.isFile()) {
            size = f.length();
        } else {
            Log.e("FileUtils", "file doesn't exist or is not a file");
        }
        return size;
    }


    /**
     * 获取文件编码格式
     */
    public static String charsetDetect(String path) {
        String _charset = "";
        try {
            File file = new File(path);
            InputStream fs = new FileInputStream(file);
            byte[] buffer = new byte[3];
            fs.read(buffer);
            fs.close();

            if (buffer[0] == -17 && buffer[1] == -69 && buffer[2] == -65)
                _charset = "UTF-8";
            else
                _charset = "GBK";
        } catch (IOException e) {
            e.printStackTrace();
        }
        return _charset;
    }

    public static String getFileName(String path) {
        String filename;
        int start = path.lastIndexOf("/");
        int end = path.length();
        if (start != -1 && end != -1) {
            filename = path.substring(start + 1, end);
        } else {
            filename = String.valueOf(System.currentTimeMillis());
        }
        return filename;
    }

    public static SDCardInfo getSDCardInfo(String pathSD) {

        File pathFile;
        if (!TextUtils.isEmpty(pathSD)) {
            pathFile = new File(pathSD);
            String sDcString = Environment.getExternalStorageState();
            try {
                android.os.StatFs statfs = new android.os.StatFs(pathFile.getPath());

                // 获取SDCard上BLOCK总数
                long nTotalBlocks = statfs.getBlockCount();

                // 获取SDCard上每个block的SIZE
                long nBlocSize = statfs.getBlockSize();

                // 获取可供程序使用的Block的数量
                long nAvailaBlock = statfs.getAvailableBlocks();

                // 获取剩下的所有Block的数量(包括预留的一般程序无法使用的块)
                long nFreeBlock = statfs.getFreeBlocks();

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {

                    nBlocSize = statfs.getBlockSizeLong();

                    nTotalBlocks = statfs.getBlockCountLong();

                    nAvailaBlock = statfs.getAvailableBlocksLong();

                } else

                {

                    nBlocSize = statfs.getBlockSize();

                    nTotalBlocks = statfs.getBlockCount();

                    nAvailaBlock = statfs.getAvailableBlocks();

                }
                SDCardInfo info = new SDCardInfo();
                // 计算SDCard 总容量大小MB
                info.total = nTotalBlocks * nBlocSize;

                // 计算 SDCard 剩余大小MB
                info.free = nAvailaBlock * nBlocSize;

                return info;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static class SDCardInfo {
        public long total;

        public long free;
    }
}
