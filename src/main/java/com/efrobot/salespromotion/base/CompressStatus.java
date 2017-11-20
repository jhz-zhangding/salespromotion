package com.efrobot.salespromotion.base;

/**
 * @项目名称：餐厅个性化
 * @类名称：CompressStatus
 * @类描述：解压状态
 * @创建人：wangyonghui
 * @创建时间：2017/5/2510:38
 * @修改时间：2017/5/2510:38
 * @备注：
 */
public class CompressStatus {
    public final static int START = 10000;//开始解压
    public final static int HANDLING = 10001;//进行中
    public final static int ERROR = 10003;//解压失败
    public final static int SUCCESS = 10004;//数据拷贝完成
    public final static int PACKAGE_BAD = 10005;//压缩包损坏
    public final static int ERROR_UNKNOWN = -1;

    public final static String PERCENT = "PERCENT";
    public final static String FILE_NAME = "FILE_NAME";
    public final static String FIRSTIMPORT = "firstImport";//第一次内置数据
    public final static String USBIMPORT = "usbImport";//开始解压

}
