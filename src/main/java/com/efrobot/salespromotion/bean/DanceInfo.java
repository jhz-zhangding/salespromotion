package com.efrobot.salespromotion.bean;


/**
 * @项目名称:
 * @类名称：
 * @类描述：
 * @创建人：luyuqin
 * @创建时间：2017/3/1718:00
 * @修改时间：2017/3/1718:00
 * @备注：
 */
public class DanceInfo {

    private long id;//数据库id

    private String danceName;

    private String mp3Name;

    private String mp3Path;

    private String mp4Name;

    private String mp4Path;

    private String scriptName;

    private String scriptPath;

    private String mp4PathBusiness;

    private int danceStatus; //0 没启动跳舞  1   启动跳舞

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDanceName() {
        return danceName;
    }

    public void setDanceName(String danceName) {
        this.danceName = danceName;
    }

    public int getDanceStatus() {
        return danceStatus;
    }

    public void setDanceStatus(int danceStatus) {
        this.danceStatus = danceStatus;
    }

    public String getMp3Name() {
        return mp3Name;
    }

    public void setMp3Name(String mp3Name) {
        this.mp3Name = mp3Name;
    }

    public String getMp3Path() {
        return mp3Path;
    }

    public void setMp3Path(String mp3Path) {
        this.mp3Path = mp3Path;
    }

    public String getMp4Name() {
        return mp4Name;
    }

    public void setMp4Name(String mp4Name) {
        this.mp4Name = mp4Name;
    }

    public String getMp4Path() {
        return mp4Path;
    }

    public void setMp4Path(String mp4Path) {
        this.mp4Path = mp4Path;
    }

    public String getMp4PathBusiness() {
        return mp4PathBusiness;
    }

    public void setMp4PathBusiness(String mp4PathBusiness) {
        this.mp4PathBusiness = mp4PathBusiness;
    }

    public String getScriptName() {
        return scriptName;
    }

    public void setScriptName(String scriptName) {
        this.scriptName = scriptName;
    }

    public String getScriptPath() {
        return scriptPath;
    }

    public void setScriptPath(String scriptPath) {
        this.scriptPath = scriptPath;
    }


    public void clearMp3() {
        mp3Name = "";
        mp3Path = "";
    }

    public void clearMp4() {
        mp4Name = "";
        mp4Path = "";
        mp4PathBusiness = "";
    }

    public void clearScript() {
        scriptName = "";
        scriptPath = "";
    }
}
