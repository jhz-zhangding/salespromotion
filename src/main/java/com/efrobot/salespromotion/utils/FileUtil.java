package com.efrobot.salespromotion.utils;

import android.content.Context;
import android.text.TextUtils;

import com.base.utils.L;

import org.apache.http.util.EncodingUtils;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @项目名称：餐厅个性化
 * @类名称：FileUtil
 * @类描述：文件工具类
 * @创建人：wangyonghui
 * @创建时间：2017/5/2515:59
 * @修改时间：2017/5/2515:59
 * @备注：
 */
public class FileUtil {
    /**
     * 读取文件
     *
     * @param fileName
     * @return
     */
    public static String readFile(String fileName) {
        String res = null;
        try {
            File file = new File(fileName);
            if (file.exists()) {
                FileInputStream fis = new FileInputStream(file);
                int length = fis.available();
                byte[] buffer = new byte[length];
                fis.read(buffer);
                res = EncodingUtils.getString(buffer, "UTF-8");
                fis.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return res;
    }

    /**
     * 复制文件到指定文件
     *
     * @param fromFile 源文件
     * @param toFile   复制到的文件
     * @return true 成功，false 失败
     */
    public static int copy(String fromFile, String toFile, boolean isDelete) {
        try {
            File src = new File(fromFile);
            File dest = new File(toFile);
            if (src.isDirectory()) {
                if (!dest.isDirectory()) {
                    dest.mkdirs();
                }
                String files[] = src.list();
                for (String file : files) {
                    File srcFile = new File(src, file);
                    File destFile = new File(dest, file);
                    // 递归复制
                    copy(srcFile.toString(), destFile.toString(), isDelete);
                }
            } else {
                InputStream in = new FileInputStream(src);
                OutputStream out = new FileOutputStream(dest);
                byte[] buffer = new byte[1024];
                int length;
                while ((length = in.read(buffer)) > 0) {
                    out.write(buffer, 0, length);
                }
                in.close();
                out.close();
                if (isDelete) {
                    src.delete();
                }
            }
            return 0;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }

    }

    /**
     * 删除文件或文件夹
     *
     * @param file
     */
    public static void deleteFile(File file) {
        if (file.exists()) { // 判断文件是否存在
            if (file.isFile()) { // 判断是否是文件
                file.delete(); // delete()方法 你应该知道 是删除的意思;
            } else if (file.isDirectory()) { // 否则如果它是一个目录
                File files[] = file.listFiles(); // 声明目录下所有的文件 files[];
                for (int i = 0; i < files.length; i++) { // 遍历目录下所有的文件
                    deleteFile(files[i]); // 把每个文件 用这个方法进行迭代
                }
            }
            file.delete();
        }
    }

    /**
     * 得到目录中所有zip文件
     *
     * @param path
     * @return
     */
    public static List<String> getFileName(File path) {
        if (!path.exists()) {
            return null;
        }
        List<String> nameArray = new ArrayList<>();
        File fa[] = path.listFiles();
        for (int i = 0; i < fa.length; i++) {
            File fs = fa[i];
            if (fs.exists() && fs.getName().endsWith(".zip")) {
                if (fs.getName().contains("-")) {
                    String string = fs.getName().substring(fs.getName().indexOf("-") + 1, fs.getName().length());
                    if (Utils.isValidDate(string)) {
                        nameArray.add(fs.getAbsolutePath());
                    }
                }
            }
        }
        //按时间进行排序
        Collections.sort(nameArray, new Comparator<String>() {
            @Override
            public int compare(String s, String t1) {
                String one = s.substring(s.indexOf("-") + 1, s.length());
                String two = t1.substring(t1.indexOf("-") + 1, t1.length());
                L.d("FileUtil", "getFileName one=" + one + "two=" + two);
                return Utils.compareDate(one, two);
            }
        });
        return nameArray;
    }


    /**
     * 得到目录中所有zip文件
     *
     * @param path
     * @return
     */
    public static List<String> getFirstFileName(String path) {
        File file = new File(path);
        if (!file.exists()) {
            return null;
        }
        List<String> nameArray = new ArrayList<>();
        File fa[] = file.listFiles();
        for (File fs : fa) {
            if (fs.exists() && fs.getName().endsWith(".zip")) {
                nameArray.add(fs.getAbsolutePath());
            }
        }
        return nameArray;
    }

    /**
     * 得到指定目录
     *
     * @param path
     * @return
     */
    public static String getDirectories(String path) {
        File file = new File(path);
        File[] files = file.listFiles();
        String fileName = null;
        if (files != null && files.length > 0) {
            for (int i = 0; i < files.length; i++) {
                fileName = files[i].getPath();
                if (fileName.contains("editdiyzipfile")) {
                    L.e("getDirectories", fileName);
                    return fileName;
                }
            }
            getDirectories(fileName);
        }
        return fileName;
    }

    /**
     * 复制assets下文件到SD卡
     */
    public static void CopyAssets(Context context, String assetDir, String dir) {
        String[] files;
        try {
            files = context.getResources().getAssets().list(assetDir);
        } catch (IOException e1) {
            return;
        }
        File mWorkingPath = new File(dir);
        // 如果文件路径不存在
        if (!mWorkingPath.exists()) {
            mWorkingPath.mkdirs();
        }
        for (int i = 0; i < files.length; i++) {
            try {
                // 获得每个文件的名字
                String fileName = files[i];
                // 根据路径判断是文件夹还是文件
                if (!fileName.contains(".")) {
                    if (0 == assetDir.length()) {
                        CopyAssets(context, fileName, dir + fileName + "/");
                    } else {
                        CopyAssets(context, assetDir + "/" + fileName, dir + "/"
                                + fileName + "/");
                    }
                    continue;
                }
                File outFile = new File(mWorkingPath, fileName);
                if (outFile.exists())
                    outFile.delete();
                InputStream in = null;
                if (0 != assetDir.length())
                    in = context.getAssets().open(assetDir + "/" + fileName);
                else
                    in = context.getAssets().open(fileName);
                OutputStream out = new FileOutputStream(outFile);
                // Transfer bytes from in to out
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                in.close();
                out.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static String getFileNameFromUrl(String url) {
        if (!TextUtils.isEmpty(url)) {
            return url.substring(url.lastIndexOf("/") + 1);
        }

        return "";
    }

    /**
     * 关闭流
     *
     * @param closeables io
     */
    public static void close(Closeable... closeables) {
        for (Closeable io : closeables) {
            if (io != null) {
                try {
                    io.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static String formatSize(long fileS) {
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString = "";
        String wrongSize = "0B";
        if (fileS == 0) {
            return wrongSize;
        }
        if (fileS < 1024) {
            fileSizeString = df.format((double) fileS) + "B";
        } else if (fileS < 1048576) {
            fileSizeString = df.format((double) fileS / 1024) + "KB";
        } else if (fileS < 1073741824) {
            fileSizeString = df.format((double) fileS / 1048576) + "MB";
        } else {
            fileSizeString = df.format((double) fileS / 1073741824) + "GB";
        }
        return fileSizeString;
    }
}
