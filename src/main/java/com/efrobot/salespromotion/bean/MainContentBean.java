package com.efrobot.salespromotion.bean;

import java.util.List;

/**
 * Created by zd on 2017/12/18.
 */
public class MainContentBean {

    private List<ModelContentBean> contentBean;

    private List<PlayModeBean> modeBean;

    public List<ModelContentBean> getContentBean() {
        return contentBean;
    }

    public void setContentBean(List<ModelContentBean> contentBean) {
        this.contentBean = contentBean;
    }

    public List<PlayModeBean> getModeBean() {
        return modeBean;
    }

    public void setModeBean(List<PlayModeBean> modeBean) {
        this.modeBean = modeBean;
    }
}
