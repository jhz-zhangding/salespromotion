package com.efrobot.salespromotion.add;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.efrobot.library.mvp.utils.L;
import com.efrobot.salespromotion.Env.SalesConstant;
import com.efrobot.salespromotion.R;
import com.efrobot.salespromotion.adapter.ChooseDanceAdapter;
import com.efrobot.salespromotion.adapter.FaceAndActionAdapter;
import com.efrobot.salespromotion.base.SalesBaseActivity;
import com.efrobot.salespromotion.bean.DanceInfo;
import com.efrobot.salespromotion.bean.ItemsContentBean;
import com.efrobot.salespromotion.bean.MainItemContentBean;
import com.efrobot.salespromotion.db.MainDataManager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by Administrator on 2016/2/18.
 */
public class AddBodyShowView extends SalesBaseActivity<AddBodyShowPresenter> implements IAddBodyShowView, View.OnClickListener, AdapterView.OnItemClickListener, RadioGroup.OnCheckedChangeListener {
    private TextView msaybtn;
    private String mSayType;

    private TextView totalTime;
    private TextView mLightbtn, add_title;
    private int size;
    //用来区分，视频，图片，音频
//    private String selectType;
//    private static int videotag = -1;
//    private static int music_img_tag = -1;

    /***
     * 是否选择了图片
     */
    public boolean isSelectedPicture;
    /***
     * 是否选择了音乐
     */
    public boolean isSelectedMusic;
    /***
     * 是否选择了视频
     */
    public boolean isSelectedVideo;

    private double sayTime = 0;
    private double lightTime = 0;
    private double fileTime = 0;
    private double actionTime = 0;
    private double faceTime = 0;
    private int itemNum;
    private int itemType;

    private RecyclerView danceListView;
    private List<String> danceList;
    private ChooseDanceAdapter adapter;
    private TextView mSelectDanceTv;
    private ImageView mDelDanceBtn;
    private TextView goodsName;
    private TextView goodsGroup;
    private TextView goodsDetail;

    @Override
    public AddBodyShowPresenter createPresenter() {
        return new AddBodyShowPresenter(this);
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    protected int getContentViewResource() {
        return R.layout.activity_add;
    }

    /**
     * 表情、动作
     */
    private TextView mFace, mAction;
    /**
     * 显示表情和动作的GridView
     */
    private GridView mGridView;
    /**
     * 保存按钮
     */
    private Button mSaveBtn;
    /**
     * 内容 文本框
     */
    private EditText mEditText;

    private LinearLayout mEditAddNameLL;

    /**
     * 语音时长
     */
    private TextView tvSayTime;

    private LinearLayout relSay;
    /**
     * 表情和动作的Layout
     */
    private View faceActionView;
    /**
     * 自定义View
     */
    private View addMediaView;

    /**
     * 舞蹈View
     */
    private View addDanceView;

    /**
     * 自定义动作View
     */
    private View addCustom;
    /**
     * 动作表情
     */
    private TextView mfaceActionvalue;
    /**
     * 表情动作的RecyclerView
     */
    private RecyclerView tvAnctionsAndFaces;
    private LinearLayoutManager linearLayoutManager;
    /**
     * 动作表情View的标题
     */
    private TextView mTitle;
    /**
     * 灯带的 关、开、闪烁
     */
    private RadioButton mClose, mOpen, mFlicker;
    /**
     * 灯带group
     */
    private RadioGroup mRadioGroup;
    /**
     * 标记灯带的checked
     * 1关、0常亮、2闪烁
     */
    private String light = "1";
    /**
     * 创建动作脚本按钮
     */
    private TextView mCreateScript;

    /**
     * 返回按钮
     */

    private TextView mbackView;

    /**
     * 添加图片或视频
     */
    private TextView tvAddMedia;

    /**
     * 添加舞蹈
     */
    private TextView tvAddDance;
    /**
     * 上传文件
     */
    private TextView videoPull, musicPull, imagePull;
    /**
     * 显示选择的图片或视频
     */
    private ImageView ivPhoto;
    /**
     * 选择的图片，视频，音频的名称
     */
    private TextView imageName, videoName, musicName;
    /**
     * 删除选择的图片，视频，音频
     */
    private ImageView deleteImage, deleteVideo, deleteMusic;
    /**
     * 灯带选择视图
     */
    private RelativeLayout relBelt;

    /**
     * 常亮和闪烁的灯带时长选择
     */
    private Spinner openSpinner, flickerSpinner;

    ArrayList<TextView> viewList = new ArrayList<TextView>();
    //定义一个过滤器；
    private IntentFilter intentFilter;

    //定义一个广播监听器；
    private ResourceBroadcastReceiver resourcereceiver;

    private String mContent;

    private String selectDanceName = "";

    private String goodsNameStr = "";
    private String goodsGroupStr = "";
    private String goodsDetailStr = "";

    @Override
    protected void onViewInit() {
        super.onViewInit();

        Intent intent = getIntent();

        itemNum = 0;
        if (intent.hasExtra("itemNum")) {
            itemNum = intent.getIntExtra("itemNum", -1);
        } else if (intent.hasExtra("content")) {
            ItemsContentBean bean = (ItemsContentBean) intent.getSerializableExtra("content");
            if (bean != null) {
                itemNum = bean.getItemNum();
            }
        }
        mSayType = "语音";

        //替换默认商品词条
        List<MainItemContentBean> list = MainDataManager.getInstance(this).queryAllContent();
        if (null != list && list.size() > 0) {
            int lastIndex = list.size() - 1;
            goodsNameStr = "<" + list.get(lastIndex).getGoodsName() + ">";
            goodsGroupStr = "<" + list.get(lastIndex).getGoodsGroup() + ">";
            goodsDetailStr = "<" + list.get(lastIndex).getGoodsDescription() + ">";
        }


        msaybtn = (TextView) findViewById(R.id.say_value_btn);
        msaybtn.setText(mSayType);

        relSay = (LinearLayout) findViewById(R.id.relSay);
        mLightbtn = (TextView) findViewById(R.id.light_value_btn);
        mFace = (TextView) findViewById(R.id.add_face_btn);
        mAction = (TextView) findViewById(R.id.add_action_btn);
        mCreateScript = (TextView) findViewById(R.id.add_create_script);
        tvAddMedia = (TextView) findViewById(R.id.tvAddMedia);
        tvAddDance = (TextView) findViewById(R.id.tvAddDance);
        add_title = (TextView) findViewById(R.id.add_title);
        viewList.add(msaybtn);
//        viewList.add(mLightbtn);
        viewList.add(mFace);
        viewList.add(mAction);
        viewList.add(mCreateScript);
        viewList.add(tvAddMedia);
        viewList.add(tvAddDance);

        size = viewList.size();

        tvAnctionsAndFaces = (RecyclerView) findViewById(R.id.tvFaces);
        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        tvAnctionsAndFaces.setLayoutManager(linearLayoutManager);

        mGridView = (GridView) findViewById(R.id.add_grid_view);
        faceActionView = (View) findViewById(R.id.face_action);
        addMediaView = (View) findViewById(R.id.addMeida);
        addDanceView = (View) findViewById(R.id.addDance);

        mSaveBtn = (Button) findViewById(R.id.add_save_btn);

        mEditText = (EditText) findViewById(R.id.add_edit_text);
        goodsName = ((TextView) findViewById(R.id.add_edit_goods_name));
        goodsName.setText(goodsNameStr);
//        mEditAddNameLL = (LinearLayout) findViewById(R.id.add_edit_person_name_ll);
        goodsName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int index = mEditText.getSelectionStart();
                String text = goodsName.getText().toString();
                Editable edit = mEditText.getEditableText();//获取EditText的文字
                if (index < 0 || index >= edit.length()) {
                    edit.append(text);
                } else {
                    edit.insert(index, text);//光标所在位置插入文字
                }

            }
        });

        goodsGroup = ((TextView) findViewById(R.id.add_edit_goods_group));
        goodsGroup.setText(goodsGroupStr);
        goodsGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int index = mEditText.getSelectionStart();
                String text = goodsGroup.getText().toString();
                Editable edit = mEditText.getEditableText();//获取EditText的文字
                if (index < 0 || index >= edit.length()) {
                    edit.append(text);
                } else {
                    edit.insert(index, text);//光标所在位置插入文字
                }
            }
        });

        goodsDetail = ((TextView) findViewById(R.id.add_edit_goods_detail));
        goodsDetail.setText(goodsDetailStr);
        goodsDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int index = mEditText.getSelectionStart();
                String text = goodsDetail.getText().toString();
                Editable edit = mEditText.getEditableText();//获取EditText的文字
                if (index < 0 || index >= edit.length()) {
                    edit.append(text);
                } else {
                    edit.insert(index, text);//光标所在位置插入文字
                }
            }
        });


        tvSayTime = (TextView) findViewById(R.id.tvSayTime);
//        prepareSayLv = (ListView) findViewById(R.id.add_prepare_say_words_lv);

        videoPull = (TextView) findViewById(R.id.videoPull);
        musicPull = (TextView) findViewById(R.id.musicPull);
        imagePull = (TextView) findViewById(R.id.imagePull);

        videoName = (TextView) findViewById(R.id.videoName);
        imageName = (TextView) findViewById(R.id.imageName);
        musicName = (TextView) findViewById(R.id.musicName);

        deleteImage = (ImageView) findViewById(R.id.deleteImage);
        deleteMusic = (ImageView) findViewById(R.id.deleteMusic);
        deleteVideo = (ImageView) findViewById(R.id.deleteVideo);
        totalTime = (TextView) findViewById(R.id.totalTime);


        InputFilter[] filters = {new NameLengthFilter(Integer.MAX_VALUE)};
        mEditText.setFilters(filters);
        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                float time = (float) (mEditText.getText().length() * 0.27);
                tvSayTime.setText("语音时长：" + time + "秒");
                if (!isSelectedMusic && !isSelectedVideo) {
                    msaybtn.setText(mSayType + "(" + time + "'')");
                    sayTime = (double) time;
                }

                getMaxTime();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mfaceActionvalue = (TextView) findViewById(R.id.face_action_value);
        mTitle = (TextView) findViewById(R.id.face_action_title);
        mRadioGroup = (RadioGroup) findViewById(R.id.add_light_group);
        relBelt = (RelativeLayout) findViewById(R.id.relBelt);
        mClose = (RadioButton) findViewById(R.id.add_light_close);
        mOpen = (RadioButton) findViewById(R.id.add_light_open);
        mFlicker = (RadioButton) findViewById(R.id.add_light_flicker);
        openSpinner = (Spinner) findViewById(R.id.open_light_spin);
        flickerSpinner = (Spinner) findViewById(R.id.flicker_light_spin);
        openSpinner.setEnabled(false);
        flickerSpinner.setEnabled(false);
        mbackView = (TextView) findViewById(R.id.add_back);

        mSelectDanceTv = (TextView) findViewById(R.id.add_dance_name_tv);
        mDelDanceBtn = (ImageView) findViewById(R.id.add_dance_name_del_img);
        mDelDanceBtn.setOnClickListener(this);

        danceListView = (RecyclerView) findViewById(R.id.add_dance_list_view);
        //创建默认的线性LayoutManager  横向的GridLayoutManager
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        danceListView.setLayoutManager(gridLayoutManager);
        //如果可以确定每个item的高度是固定的，设置这个选项可以提高性能
        danceListView.setHasFixedSize(true);
        getDanceList();
        if (danceList.size() == 0) {

        }
        adapter = new ChooseDanceAdapter(danceList, onChooseDanceAdapterItemListener, mContent);
        danceListView.setAdapter(adapter);

        /**
         * 隐藏键盘
         */
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        //文件管理数据接受广播
        intentFilter = new IntentFilter();
        //添加过滤的Action值；
        intentFilter.addAction("efrobot.robot.resoure");

        //实例化广播监听器；
        resourcereceiver = new ResourceBroadcastReceiver();

        //将广播监听器和过滤器注册在一起；
        registerReceiver(resourcereceiver, intentFilter);
        openSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!"无".equals(openSpinner.getSelectedItem().toString())) {
                    mLightbtn.setText("灯带" + "(" + openSpinner.getSelectedItem().toString() + "'')");
                    lightTime = Double.parseDouble(openSpinner.getSelectedItem().toString());
                    getMaxTime();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        flickerSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!"无".equals(flickerSpinner.getSelectedItem().toString())) {
                    mLightbtn.setText("灯带" + "(" + flickerSpinner.getSelectedItem().toString() + "'')");
                    lightTime = Double.parseDouble(flickerSpinner.getSelectedItem().toString());
                    getMaxTime();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }


    private ChooseDanceAdapter.OnChooseDanceAdapterItemListener onChooseDanceAdapterItemListener = new ChooseDanceAdapter.OnChooseDanceAdapterItemListener() {
        @Override
        public void onItemClick(View v, String danceName, int position) {
//            list.clear();
//            FunctionBean functionBean = new FunctionBean();
//            functionBean.setFunctionType(3);
//            functionBean.setFunctionName("跳舞-" + danceName);
//            list.add(functionBean);
//            mListener.onItemClick(list);
//            dismiss();
            selectDanceName = danceName;
            mSelectDanceTv.setText(selectDanceName);
            mDelDanceBtn.setVisibility(View.VISIBLE);
        }
    };

    /***
     * 解决软键盘弹出时任务栏不隐藏和单击输入框以外区域输入法不隐藏的bug
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (isShouldHideInput(v, ev)) {
                int uiFlags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN; // hide status bar

                if (android.os.Build.VERSION.SDK_INT >= 19) {
                    uiFlags |= 0x00001000;    //SYSTEM_UI_FLAG_IMMERSIVE_STICKY: hide navigation bars - compatibility: building API level is lower thatn 19, use magic number directly for higher API target level
                } else {
                    uiFlags |= View.SYSTEM_UI_FLAG_LOW_PROFILE;
                }
                getWindow().getDecorView().setSystemUiVisibility(uiFlags);
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
            return super.dispatchTouchEvent(ev);
        }
        // 必不可少，否则所有的组件都不会有TouchEvent了
        if (getWindow().superDispatchTouchEvent(ev)) {
            return true;
        }
        return onTouchEvent(ev);
    }

    @Override
    public void setLight(String light) {
        this.light = light;
        if (light.equals("0")) {
            mOpen.setChecked(true);
            flickerSpinner.setEnabled(false);
            openSpinner.setEnabled(true);
            mLightbtn.setText("灯带" + "(" + openSpinner.getSelectedItem().toString() + "'')");
            if (!"无".equals(openSpinner.getSelectedItem().toString())) {
                lightTime = Double.parseDouble(openSpinner.getSelectedItem().toString());
            }
            getMaxTime();
        } else if (light.equals("1")) {
            mClose.setChecked(true);
            mFlicker.setChecked(false);
            mLightbtn.setText("灯带");
            flickerSpinner.setEnabled(false);
            openSpinner.setEnabled(false);
        } else if (light.equals("2")) {
            mFlicker.setChecked(true);
            flickerSpinner.setEnabled(true);
            openSpinner.setEnabled(false);
            mLightbtn.setText("灯带" + "(" + flickerSpinner.getSelectedItem().toString() + "'')");
            if (!"无".equals(openSpinner.getSelectedItem().toString())) {
                lightTime = Double.parseDouble(flickerSpinner.getSelectedItem().toString());
            }
            getMaxTime();
        }
    }

    /**
     * 设置灯带常亮时长和闪烁时长
     */
    @Override
    public void setOpenAndFlickerTime(String open, String flicker) {
        Resources res = getContext().getResources();
        final String[] light = res.getStringArray(R.array.light);
        for (int i = 0; i < light.length; i++) {
            if (light[i].equals(open)) {
                openSpinner.setSelection(i);
            }
            if (light[i].equals(flicker)) {
                flickerSpinner.setSelection(i);
            }
        }
    }

    @Override
    public Spinner getOpenSpinner() {
        return openSpinner;
    }

    @Override
    public Spinner getFlickerSpinner() {
        return flickerSpinner;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        L.e("=========>>>>onDestroy", "onDestroy");
        if (resourcereceiver != null)
            getContext().unregisterReceiver(resourcereceiver);
    }

    @Override
    public void setFirstViewShow() {
        updateView(msaybtn);
    }

    /**
     * 自定义数据设置
     *
     * @param musicPath
     * @param mediaPath
     */
    @Override
    public void setMedia2(String musicPath, String mediaPath) {
        L.e("==============>>>", "设置媒体显示=============》》》》》》》》》》》》》》》");
        int dosmusic = -1;
        int dosmedia = -1;

        isSelectedPicture = false;
        isSelectedMusic = false;
        isSelectedVideo = false;

        if (!TextUtils.isEmpty(musicPath)) {
            dosmusic = musicPath.lastIndexOf("/");
        }
        if (!TextUtils.isEmpty(mediaPath)) {
            dosmedia = mediaPath.lastIndexOf("/");
        }

        /***
         * 选了音频
         */
        if (dosmusic != -1) {
            String musicname = musicPath.substring(dosmusic + 1, musicPath.length());
            musicName.setText(musicname);
            deleteMusic.setVisibility(View.VISIBLE);
            isSelectedMusic = true;
        }
        /***
         * 判断是否选择了视频或者图片
         */
        if (dosmedia != -1) {
            String name = mediaPath.substring(dosmedia + 1, mediaPath.length());

            if (name.contains("mp4")) {
                /**
                 * 选择了视频
                 */
                videoName.setText(name);
                deleteVideo.setVisibility(View.VISIBLE);
                isSelectedVideo = true;
            } else {
                /**
                 * 选择了图片
                 */
                imageName.setText(name);
                deleteImage.setVisibility(View.VISIBLE);
                isSelectedPicture = true;
            }
        }

        if (isSelectedVideo) {
            musicPull.setSelected(true);
            imagePull.setSelected(true);
        } else {

            if (isSelectedMusic) {
                videoPull.setSelected(true);
            }
        }


        videoPull.setEnabled(!videoPull.isSelected());
        musicPull.setEnabled(!musicPull.isSelected());
        imagePull.setEnabled(!imagePull.isSelected());
        initSay();

    }


    /**
     * title添加词条的改变
     *
     * @param content
     */
    @Override
    public void setTitle(String content) {
        add_title.setText("添加" + content);

    }

    /**
     * 更新页面
     *
     * @param view 页面
     */
    public void updateView(View view) {

        if (view.isSelected()) {
            return;
        }

        for (int j = 0; j < size; j++) {
            viewList.get(j).setSelected(viewList.get(j).equals(view));
        }

        relSay.setVisibility(View.GONE);
        relBelt.setVisibility(View.GONE);
        faceActionView.setVisibility(View.GONE);
        addMediaView.setVisibility(View.GONE);
        addDanceView.setVisibility(View.GONE);
        if (msaybtn.equals(view)) {
            relSay.setVisibility(View.VISIBLE);
        } else if (view.equals(mLightbtn)) {
            relBelt.setVisibility(View.VISIBLE);
        } else if (view.equals(mFace)) {
            tvAnctionsAndFaces.setVisibility(View.VISIBLE);
            faceActionView.setVisibility(View.VISIBLE);
        } else if (view.equals(mAction)) {
            tvAnctionsAndFaces.setVisibility(View.VISIBLE);
            faceActionView.setVisibility(View.VISIBLE);
        } else if (view.equals(mCreateScript)) {
            addCustom.setVisibility(View.VISIBLE);
        } else if (view.equals(tvAddMedia)) {
            addMediaView.setVisibility(View.VISIBLE);
        } else if (view.equals(tvAddDance)) {
            addDanceView.setVisibility(View.VISIBLE);
        }

    }


    @Override
    protected void setOnListener() {
        super.setOnListener();


        videoPull.setOnClickListener(this);
        musicPull.setOnClickListener(this);
        imagePull.setOnClickListener(this);


        deleteImage.setOnClickListener(this);
        deleteMusic.setOnClickListener(this);
        deleteVideo.setOnClickListener(this);
        mSaveBtn.setOnClickListener(this);
        mbackView.setOnClickListener(this);

        mGridView.setOnItemClickListener(this);
        mRadioGroup.setOnCheckedChangeListener(this);

        for (int i = 0; i < size; i++) {
            final TextView view = viewList.get(i);
            view.setOnClickListener(this);
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            /**
             * 退出页面
             */
            case R.id.add_back:
                ((AddBodyShowPresenter) mPresenter).exitAdd();
//                music_img_tag = -1;
//                videotag = -1;
                break;
            /**
             * 说话
             */
            case R.id.say_value_btn:
                /**
                 * 闪光灯
                 */
                updateView(v);
                add_title.setText("添加语音");
                break;
            case R.id.light_value_btn:
                updateView(v);
                add_title.setText("添加灯带");
                break;

            /**
             * 添加运动
             */
            case R.id.add_create_script:
                add_title.setText("创建动作");
                updateView(v);
                break;

            /**
             * 添加表情
             */
            case R.id.add_face_btn:
                ((AddBodyShowPresenter) mPresenter).showFaceAndAction(1);
                mTitle.setText("表情:");
                add_title.setText("添加表情");
                updateView(v);
                break;
            /**
             * 添加动作
             */
            case R.id.add_action_btn:
                ((AddBodyShowPresenter) mPresenter).showFaceAndAction(2);
                mTitle.setText("动作:");
                add_title.setText("添加动作");
                updateView(v);
                break;
            /**
             * 保存数据
             */
            case R.id.add_save_btn:
                ((AddBodyShowPresenter) mPresenter).saveData();
//                music_img_tag = -1;
//                videotag = -1;
                break;
            case R.id.tvAddMedia:
                /**
                 * 添加图片或者视频
                 */
                updateView(v);
                add_title.setText("添加自定义");
                break;
            case R.id.tvAddDance:
                updateView(v);
                add_title.setText("添加舞蹈");
                break;
            case R.id.videoPull:
                if (isSelectedMusic || isSelectedPicture) {
                    mPresenter.showToast("音乐图片模式下暂不支持上传视频");
                    return;
                }
                ((AddBodyShowPresenter) mPresenter).toAddMedia("video");
                break;
            case R.id.imagePull:
                if (isSelectedVideo) {
                    mPresenter.showToast("视频模式下暂不支持上传图片");
                    return;
                }
                ((AddBodyShowPresenter) mPresenter).toAddMedia("image");
                break;
            case R.id.musicPull:
                if (isSelectedVideo) {
                    mPresenter.showToast("视频模式下暂不支持上传音乐");
                    return;
                }
                ((AddBodyShowPresenter) mPresenter).toAddMedia("music");
                break;
            case R.id.deleteMusic:
                musicName.setText("");
                tvAddMedia.setText("自定义");
                ((AddBodyShowPresenter) mPresenter).addMusic = "";
                if (TextUtils.isEmpty(imageName.getText().toString())) {
                    videoPull.setSelected(false);
                    videoPull.setEnabled(!videoPull.isSelected());

                }
                deleteMusic.setVisibility(View.INVISIBLE);
                isSelectedMusic = false;
                fileTime = 0;
                getMaxTime();

                initSay();


                break;
            case R.id.deleteVideo:
                imagePull.setSelected(false);
                musicPull.setSelected(false);
                musicPull.setEnabled(!musicPull.isSelected());
                imagePull.setEnabled(!imagePull.isSelected());
                videoName.setText("");
                tvAddMedia.setText("自定义");
                ((AddBodyShowPresenter) mPresenter).addMedia = "";
                fileTime = 0;
                getMaxTime();
                deleteVideo.setVisibility(View.INVISIBLE);
                isSelectedVideo = false;
                initSay();
                break;
            case R.id.deleteImage:
                imageName.setText("");
                if (TextUtils.isEmpty(musicName.getText().toString())) {
                    videoPull.setSelected(false);
                    videoPull.setEnabled(!videoPull.isSelected());
                }
                ((AddBodyShowPresenter) mPresenter).addMedia = "";
                deleteImage.setVisibility(View.INVISIBLE);
                isSelectedPicture = false;
                if (TextUtils.isEmpty(((AddBodyShowPresenter) mPresenter).addMusic))
                    isSelectedPicture = false;
                break;
            case R.id.add_dance_name_del_img:
                mSelectDanceTv.setText("");
                mDelDanceBtn.setVisibility(View.GONE);
                if (adapter != null) {
                    adapter.updateContent("");
                }
                break;
        }
    }

    private void initSay() {
        float time = (float) (mEditText.getText().length() * 0.27);

        if (!isSelectedMusic && !isSelectedVideo) {
            msaybtn.setText(mSayType + "(" + time + "'')");
            sayTime = (double) time;
        } else {
            msaybtn.setText(mSayType);
            sayTime = 0;
        }
    }

    @Override
    public void setAdapter(BaseAdapter mAdapter) {
        mGridView.setAdapter(mAdapter);
    }

    @Override
    public String getEditContent() {
        return mEditText.getText().toString().trim();
    }

    @Override
    public String getDanceName() {
        return selectDanceName;
    }

    @Override
    public void setDanceName(String name) {
        if (adapter != null) {
            adapter.updateContent(name);
        }
        mSelectDanceTv.setText(name);
        mDelDanceBtn.setVisibility(View.VISIBLE);
    }

    @Override
    public void setEditContent(String content) {
        String str = content.replace(SalesConstant.ProjectInfo.PRODUCT_NAME, goodsNameStr).
                replace(SalesConstant.ProjectInfo.PRODUCT_GROUP, goodsGroupStr).
                replace(SalesConstant.ProjectInfo.PRODUCT_DETAIL, goodsDetailStr);
        mEditText.setText(str);
        mEditText.setSelection(mEditText.getText().length());
        float time = (float) (mEditText.getText().length() * 0.27);

        tvSayTime.setText("语音时长：" + time + "秒");
        msaybtn.setText(mSayType + "(" + time + "'')");
        sayTime = (double) time;
        getMaxTime();
    }

    /**
     * 设置左边表情选项的内容
     */
    @Override
    public void setFaceTime(double time) {
        faceTime = time;
        //删除表情
        if (time > 0) {
            mFace.setText("表情(" + time + "'')");
            getMaxTime();
        } else {
            mFace.setText("表情");
            getMaxTime();
        }
    }

    @Override
    public void setSystemActionTime(double time) {
        actionTime = Double.parseDouble(String.format(Locale.CHINA, "%.2f", time));
        //删除表情
        if (actionTime > 0) {
            mAction.setText("动作(" + actionTime + "'')");
            getMaxTime();
        } else {
            mAction.setText("动作");
            getMaxTime();
        }
    }

    /**
     * 设置选中的表情数据
     *
     * @param adapter
     */
    @Override
    public void setActionsAndFaces(FaceAndActionAdapter adapter) {
        if (adapter != null) {
            tvAnctionsAndFaces.setAdapter(adapter);
        }
    }

//    @Override
//    public void setMaxTime(String time) {
//        totalTime.setText("播放总时长:(" + time + "'')");
//    }

    /**
     * 设置滚动到最新添加的表情
     *
     * @param position
     */
    @Override
    public void setCurrentActionOrFace(int position) {
        if (position == -1)
            return;
        L.e("====>>>", "position=" + position);
        tvAnctionsAndFaces.smoothScrollToPosition(position);
    }


    /**
     * 自定义动作
     *
     * @param time
     */
    @Override
    public void setActionTime(String time) {
        if (!TextUtils.isEmpty(time)) {
            actionTime = Double.parseDouble(time);
            mCreateScript.setText("创建动作(" + time + "'')");
        } else {
            mCreateScript.setText("创建动作");
            actionTime = 0;
        }
        getMaxTime();
    }

    @Override
    public String getLight() {
        return light;
    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        setFullScreen();
    }

    @Override
    public void setCreateScriptText(String text) {

        if (!TextUtils.isEmpty(text))
            mCreateScript.setText("创建动作(" + text + ")");
        else
            mCreateScript.setText("创建动作");
    }

    @Override
    public void setActionEnable(boolean b) {
//        mAction.setEnabled(b);
//        findViewById(R.id.delete_create_script).setVisibility(b ? View.GONE : View.VISIBLE);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ((AddBodyShowPresenter) mPresenter).onItemClick(parent, view, position, id);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if (mClose.getId() == checkedId) {
            light = "1";
            flickerSpinner.setEnabled(false);
            openSpinner.setEnabled(false);
            openSpinner.setSelection(0);
            flickerSpinner.setSelection(0);
            mLightbtn.setText("灯带");
            lightTime = 0;
            getMaxTime();
        } else if (mOpen.getId() == checkedId) {
            light = "0";
            flickerSpinner.setEnabled(false);
            openSpinner.setEnabled(true);
            flickerSpinner.setSelection(0);
            mLightbtn.setText("灯带");
            lightTime = 0;
            getMaxTime();
        } else if (mFlicker.getId() == checkedId) {
            light = "2";
            openSpinner.setEnabled(false);
            flickerSpinner.setEnabled(true);
            openSpinner.setSelection(0);
            mLightbtn.setText("灯带");
            lightTime = 0;
            getMaxTime();

        }
    }


    @Override
    public void setRightHeadLayoutGone(boolean isGone) {
//        if (isGone) {
//            head_right_layout.setVisibility(View.INVISIBLE);
//        } else {
//            head_right_layout.setVisibility(View.VISIBLE);
//        }

    }

    @Override
    public void setRightWheelLayoutGone(boolean isGone) {


    }

    @Override
    public void setRightWingLayoutGone(boolean isGone) {
//        if (isGone) {
//            wing_right_layout.setVisibility(View.INVISIBLE);
//        } else {
//            wing_right_layout.setVisibility(View.VISIBLE);
//        }
    }


    @Override
    public double getMaxTime() {
        double[] time = {sayTime, lightTime, fileTime, actionTime, faceTime};
        double max = time[0];
        for (int i = 1; i < time.length; i++) {
            if (max < time[i]) {
                max = time[i];
            }
        }
        BigDecimal bd = new BigDecimal(max);

        bd = bd.setScale(2, BigDecimal.ROUND_HALF_UP);
        totalTime.setText("播放总时长:(" + bd.toString() + "'')");
        ((AddBodyShowPresenter) mPresenter).totalTime = bd.toString();
        return max;
    }

    @Override
    public String getGuestStartTime() {
        return "";
    }

    @Override
    public String getGuestEndTime() {
        return "";
    }

    @Override
    public void setGuestTime(String time) {
        if (!TextUtils.isEmpty(time) && time.contains("@#")) {
            String[] times = time.split("@#");
        }
    }

    @Override
    public void setMediaTime(double mediaTime) {
        fileTime = mediaTime;
        tvAddMedia.setText("自定义(" + mediaTime + "'')");
        getMaxTime();
    }


    @Override
    public void setActionEnabled(String enabled) {
        if ("0".equals(enabled)) {
            mCreateScript.setEnabled(false);
            mAction.setEnabled(true);
        } else if ("1".equals(enabled)) {
            mCreateScript.setEnabled(true);
            mAction.setEnabled(false);
        } else {
            mCreateScript.setEnabled(true);
            mAction.setEnabled(true);
        }
    }

    @Override
    public void setLightEnnabled(boolean isContentLight) {
        if (isContentLight) {
            mLightbtn.setEnabled(false);
            setLight("1");
            flickerSpinner.setSelection(0);
            openSpinner.setSelection(0);

        } else {
            mLightbtn.setEnabled(true);
        }
    }


    class ResourceBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            if (intent.getAction().equals("efrobot.robot.resoure")) {
                final String path = intent.getStringExtra("path");
                final String selectType = intent.getStringExtra("type");
                Log.e("zhang", "接受广播多媒体地址=====" + path);
                if (!TextUtils.isEmpty(path)) {
                    if ("music".equals(selectType)) {

                        ((AddBodyShowPresenter) mPresenter).getMediaTime(path, new AddBodyShowPresenter.OnGetDuration() {
                            @Override
                            public void onGet(String mGetDuration) {

                                if (TextUtils.isEmpty(mGetDuration)) {
                                    return;
                                }


                                deleteMusic.setVisibility(View.VISIBLE);
                                /***
                                 * 音频路径
                                 */
                                ((AddBodyShowPresenter) mPresenter).addMusic = path;
                                isSelectedMusic = true;
                                tvAddMedia.setText("自定义(" + mGetDuration + "'')");
                                videoPull.setSelected(true);
                                fileTime = Double.parseDouble(mGetDuration);
                                initSay();
                                setMediaInfo(selectType, path);
                            }
                        });


                    } else if ("video".equals(selectType)) {


                        ((AddBodyShowPresenter) mPresenter).getMediaTime(path, new AddBodyShowPresenter.OnGetDuration() {
                            @Override
                            public void onGet(String mGetDuration) {
                                if (TextUtils.isEmpty(mGetDuration)) {
                                    return;
                                }
                                deleteVideo.setVisibility(View.VISIBLE);
                                /***
                                 * 媒体路径------视频
                                 */
                                ((AddBodyShowPresenter) mPresenter).addMedia = path;
                                isSelectedVideo = true;
                                tvAddMedia.setText("自定义(" + mGetDuration + "'')");
                                musicPull.setSelected(true);
                                imagePull.setSelected(true);
                                fileTime = Double.parseDouble(mGetDuration);
                                initSay();

                                setMediaInfo(selectType, path);
                            }
                        });

                    } else if ("image".equals(selectType)) {
                        deleteImage.setVisibility(View.VISIBLE);
                        /***
                         * 媒体路径----图片
                         */
                        ((AddBodyShowPresenter) mPresenter).addMedia = path;
                        isSelectedPicture = true;
                        videoPull.setSelected(true);

                        setMediaInfo(selectType, path);
                    }


                } else {
                    getMaxTime();
                    imageName.setText("");
                    musicName.setText("");
                    videoName.setText("");
                    imagePull.setSelected(false);
                    musicName.setSelected(false);
                    videoPull.setSelected(false);
                    ivPhoto.setImageResource(R.mipmap.tupainjiazai);
                    ((AddBodyShowPresenter) mPresenter).addMedia = "";
                    ((AddBodyShowPresenter) mPresenter).addMusic = "";
                }

            } else if (intent.getAction().equals(Intent.ACTION_USER_PRESENT)) {
                Log.d(TAG, "onReceive ACTION_USER_PRESENT: ");
            }
        }

    }

    private void setMediaInfo(String selectType, String path) {
        videoPull.setEnabled(!videoPull.isSelected());
        musicPull.setEnabled(!musicPull.isSelected());
        imagePull.setEnabled(!imagePull.isSelected());

        if (TextUtils.isEmpty(path))
            return;
        try {
            File file = new File(path);
            L.e("--->>>", "media  path=" + path.substring(1) + "     " + file.exists());
            if (file.exists()) {
                int dos = path.lastIndexOf("/");
                if (dos != -1) {
                    String name = path.substring(dos + 1, path.length());
                    if ("music".equals(selectType)) {
                        musicName.setText(name);
                    } else if ("video".equals(selectType)) {
                        videoName.setText(name);
                    } else if ("image".equals(selectType)) {
                        imageName.setText(name);
                    }
                }
            } else {
                videoName.setText("");
                musicName.setText("");
                imageName.setText("");
                ivPhoto.setImageResource(R.mipmap.tupainjiazai);
                ((AddBodyShowPresenter) mPresenter).addMedia = "";
                ((AddBodyShowPresenter) mPresenter).addMusic = "";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        getMaxTime();
    }

    public static final String FIND_DANCE_ALL = "findDanceAll";//查询所有

    private void getDanceList() {
        if (danceList == null) {
            danceList = new ArrayList<>();
        } else {
            danceList.clear();
        }
        Uri contentUri1 = Uri.parse("content://com.efrobot.dance.common/map_table");
        Bundle bundle = this.getContentResolver().call(contentUri1, FIND_DANCE_ALL, null, null);
        if (bundle != null) {
            String jsonArray = bundle.getString("jsonDanceArray");
            List<DanceInfo> list = new Gson().fromJson(jsonArray, new TypeToken<List<DanceInfo>>() {
            }.getType());
            if (list != null)
                for (int i = 0; i < list.size(); i++) {
                    /***
                     * 跳舞昵称
                     */
                    String danceName = list.get(i).getDanceName();
                    if (TextUtils.isEmpty(danceName)) {
                        continue;
                    }
                    if (danceName.contains("/")) {
                        String[] arr = danceName.split("/");
                        danceList.add(arr[0]);
//                        for (String me : arr) {
//                            if (!TextUtils.isEmpty(me)) {
//                                danceList.add(me);
//                            }
//                        }
                    } else {
                        danceList.add(danceName);
                    }
                }
        }

    }


    //    /**
//     * 设置图片和名称
//     *
//     * @param path
//     */
//    @Override
//    public void setMedia(String path) {

//    }
}
