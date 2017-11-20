package com.efrobot.salespromotion.utils;

import com.efrobot.salespromotion.main.MainActivity;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;

import java.io.File;

/**
 * Created by shuai on 2017/5/26.
 */
public class ZipUtils {


    public static void AddFolder(String zipPath, String filePath) {

        try {
            ZipFile zipFile = new ZipFile(zipPath);
            zipFile.setFileNameCharset("GBK");
            ZipParameters parameters = new ZipParameters();

            parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
            parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);
            zipFile.addFolder(filePath, parameters);
            File filedelect = new File(MainActivity.usbPath + "/editdiyzipfile/editdiysource");
            final File to = new File(filedelect.getAbsolutePath() + System.currentTimeMillis());
            filedelect.renameTo(to);
            DataFileUtils.deleteAllFiles(to);
        } catch (ZipException e) {
            e.printStackTrace();
        }
    }


}
