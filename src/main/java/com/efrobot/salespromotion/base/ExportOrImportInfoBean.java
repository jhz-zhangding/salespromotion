package com.efrobot.salespromotion.base;

/**
 * Created by lhy on 2017/10/26
 */
public class ExportOrImportInfoBean {
    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public int getCompleteCount() {
        return completeCount;
    }

    public void setCompleteCount(int completeCount) {
        this.completeCount = completeCount;
    }

    public int getErrorCount() {
        return errorCount;
    }

    public void setErrorCount(int errorCount) {
        this.errorCount = errorCount;
    }

    public int getSuccessCount() {
        return successCount;
    }

    public void setSuccessCount(int successCount) {
        this.successCount = successCount;
    }

    @Override
    public String toString() {
        return "ExportOrImportInfoBean{" +
                "totalCount=" + totalCount +
                ", completeCount=" + completeCount +
                ", errorCount=" + errorCount +
                ", successCount=" + successCount +
                '}';
    }

    private int totalCount; //总数
    private int completeCount;   //已经完成
    private int errorCount;  //错误个数
    private int successCount;
}
