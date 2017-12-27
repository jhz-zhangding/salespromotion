package com.efrobot.salespromotion.service;

import android.app.Dialog;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.efrobot.library.OnRobotStateChangeListener;
import com.efrobot.library.RobotManager;
import com.efrobot.library.RobotState;
import com.efrobot.library.mvp.utils.L;
import com.efrobot.library.speech.SpeechManager;
import com.efrobot.library.task.GroupManager;
import com.efrobot.salespromotion.Env.SalesConstant;
import com.efrobot.salespromotion.R;
import com.efrobot.salespromotion.SalesApplication;
import com.efrobot.salespromotion.bean.MainItemContentBean;
import com.efrobot.salespromotion.bean.ModelContentBean;
import com.efrobot.salespromotion.db.MainDataManager;
import com.efrobot.salespromotion.db.ModelContentManager;
import com.efrobot.salespromotion.interfaces.OnKeyEventListener;
import com.efrobot.salespromotion.provider.SalesSpeechProvider;
import com.efrobot.salespromotion.utils.BitmapUtils;
import com.efrobot.salespromotion.utils.FileUtils;
import com.efrobot.salespromotion.utils.MoveManager;
import com.efrobot.salespromotion.utils.MusicPlayer;
import com.efrobot.salespromotion.utils.PreferencesUtils;
import com.efrobot.salespromotion.utils.TtsUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class SalesPromotionService extends Service implements OnKeyEventListener, OnRobotStateChangeListener {

    private static final String TAG = SalesPromotionService.class.getSimpleName();

    private String currentModelName = "";

    private List<ModelContentBean> lists;

    //活动大场景ID
    private int mainType = -1;

    private SpeechManager speechManager;

    private Context context;

    private boolean
            isFaceFinish = true,
            isTtsFinish = true,
            isActionFinish = true,
            isMediaFinish = true,
            isMusicFinish = true,
            isPictureFinish = true,
            isDanceFinish = true;

    //设置图片显示3秒消失
    private long picDismissTime = 3000;

    //当前播放下条目
    private int currentIndex = 0;

    //当前播放模式 循环 顺序
    private int mPlayMode = 0;

    //机器人运动管理器
    private GroupManager groupManager;

    //音乐管理器
    private MusicPlayer musicPlayer;

    private final int POWER_TYPE = 1;
    private final int GAME_TYPE = 2;
    private final int HOME_TYPE = 3;
    private final int BACK_TYPE = 4;
    private int powerIndex = 0;
    private int gameIndex = 0;
    private int homeIndex = 0;
    private int backIndex = 0;

    private MoveManager moveManager;
    //声音管理器
    private AudioManager audio;

    private String goodsName = "";
    private String goodsGroup = "";
    private String goodsDetail = "";
    private String picPath = "";

    public static boolean isNeedReceiveTtsEnd = false;
    public static boolean isUserBreak = false;

    private List<String> picPaths = new ArrayList<>();
    private int picIndex = 0;
    private int currentType;
    private String tts;
    private DanceBroadcastReceiver broadcastReceiver;

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        L.e(TAG, "onStartCommand");
        this.context = this;

        currentModelName = intent.getStringExtra("currentModelName");
        currentType = intent.getIntExtra("currentType", 1);


        if (currentModelName == null || TextUtils.isEmpty(currentModelName)) {
            return super.onStartCommand(intent, flags, startId);
        }

        initEvent();
        initData();


        String robotName = RobotState.getInstance(this).getRobotName() == null ? "小虎" : RobotState.getInstance(this).getRobotName();
        tts = "进入工作状态，您可以用倥鼠控制" + robotName + "啦";
        mHandle.sendEmptyMessageDelayed(SAY_START_WORDS, 3000);


        mHandle.sendEmptyMessage(CLEAR_SPEECH_SLEEP);
        mHandle.sendEmptyMessageDelayed(START_AUTO_PLAY, 10000);
        return super.onStartCommand(intent, flags, startId);
    }

    private void initEvent() {
        SalesApplication.from(this).setSalesPromotionService(this);
        SpeechManager.getInstance(this).controlHead(this, true);
        groupManager = RobotManager.getInstance(this.getApplicationContext()).getGroupInstance();
        RobotManager.getInstance(this).registerHeadKeyStateChangeListener(this);
        registerEvent();

        musicPlayer = new MusicPlayer(null);
        moveManager = new MoveManager(this);
        audio = (AudioManager) getSystemService(Service.AUDIO_SERVICE);
    }

    private void initData() {
        mainType = PreferencesUtils.getInt(this, SalesConstant.LAST_OPEN_ACTIVITY_ID);
        //替换默认商品词条
        List<MainItemContentBean> list = MainDataManager.getInstance(this).queryAllContent();
        if (null != list && list.size() > 0) {
            int lastIndex = list.size() - 1;
            goodsName = list.get(lastIndex).getGoodsName();
            goodsGroup = list.get(lastIndex).getGoodsGroup();
            goodsDetail = list.get(lastIndex).getGoodsDescription();
            picPath = list.get(lastIndex).getSpareOne() == null ? "" : list.get(lastIndex).getSpareOne();
        }

        if (!TextUtils.isEmpty(picPath)) {
            if (picPath.contains("@#")) {
                String[] strings = picPath.split("@#");
                for (int i = 0; i < strings.length; i++) {
                    picPaths.add(strings[i]);
                }
            } else {
                picPaths.add(picPath);
            }
        }

        mPlayMode = PreferencesUtils.getInt(this, SalesConstant.POWER_PLAY_MODE, SalesConstant.CIRCLE_MODE);
    }

    private long wordSpeed = 270;
    public final int TTS_FINISH = 1;
    private final int PLAY_MORE_ACTION = 2;
    public final int VIDEO_FINISH = 3;
    public final int MUSIC_NEED_SAY = 4;
    public final int CLEAR_SPEECH_SLEEP = 5;
    public final int SEND_CLEAR_PIC = 6;
    public final int SHOW_GOODS_PIC = 7;
    public final int START_AUTO_PLAY = 8;
    public final int PICTURE_FINISH = 9;
    public final int SAY_START_WORDS = 10;
    private int clearPicTime = 3000;
    public Handler mHandle = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case TTS_FINISH:
                    isNeedReceiveTtsEnd = false;
                    isTtsFinish = true;
                    isFaceFinish = true;
                    L.e(TAG, "--------------------TTS_FINISH");
                    finishAfterDoSomeThing();
                    break;
                case PICTURE_FINISH:
                    if (pictureDialog != null) {
                        pictureDialog.dismiss();
                    }
                    isPictureFinish = true;
                    break;
                case PLAY_MORE_ACTION:
                    if (actionList.size() > 0) {
                        if (currentCount > actionList.size() - 1) {
                            isActionFinish = true;
                            L.e(TAG, "--------------------ACTION_FINISH");
                            finishAfterDoSomeThing();
                        } else {
                            L.i("执行动作", "currentCount = " + currentCount);
                            checkAction(actionList.get(currentCount));
                        }
                    }

                    break;
                case VIDEO_FINISH:
                    isMediaFinish = true;
                    L.e(TAG, "--------------------VIDEO_FINISH");
                    finishAfterDoSomeThing();
                    break;
                case MUSIC_NEED_SAY:

                    break;
                case CLEAR_SPEECH_SLEEP:
                    L.w(TAG, "removeSpeechState");
                    removeSpeechState(SalesPromotionService.this, 13);
                    removeSpeechState(SalesPromotionService.this, 11);
                    sendEmptyMessageDelayed(CLEAR_SPEECH_SLEEP, 5000);
                    closeSpeechDiscern(getApplicationContext());
                    break;
                case SEND_CLEAR_PIC:
                    L.e(TAG, "SEND_CLEAR_PIC" + System.currentTimeMillis());
                    if (goodsPicDialog != null) {
                        goodsPicDialog.dismiss();
                    }
                    break;
                case SHOW_GOODS_PIC:
                    if (goodsPicDialog != null) {
                        goodsPicDialog.dismiss();
                    }
                    if (picPaths.size() > 0) {
                        if (picIndex >= picPaths.size()) {
                            picIndex = 0;
                        }
                        String mCurrentPath = picPaths.get(picIndex);
                        showGoodsPic(mCurrentPath);
                        picIndex++;
                    }
                    break;
                case START_AUTO_PLAY:
                    startPlayMode(currentType);
                    break;
                case SAY_START_WORDS:
                    TtsUtils.sendTts(context, tts);
                    break;
            }
        }
    };

    //发送移除睡眠
    public void removeSpeechState(Context context, int i) {
        try {
            Uri e = Uri.parse("content://com.efrobot.speechlibrary.provider");
            context.getContentResolver().call(e, "removeSpeechState", String.valueOf(i), (Bundle) null);
        } catch (Exception var4) {
            var4.printStackTrace();
        }
    }

    public void closeSpeechDiscern(Context context) {
        try {
            Uri e = Uri.parse("content://com.efrobot.speechlibrary.provider");
            context.getContentResolver().call(e, "closeSpeechDiscern", context.getPackageName(), (Bundle) null);
        } catch (Exception var3) {
            var3.printStackTrace();
        }

    }

    private void finishAfterDoSomeThing() {
        if (isAllPlayFinish()) {
            if (mPlayMode == SalesConstant.CIRCLE_MODE) { //循环模式
                startPlay(currentModel);
            } else if (mPlayMode == SalesConstant.ORDER_MODE) {

            }
        }
    }

    private Dialog pictureDialog;

    private int currentModel = -1;

    private void startPlay(int type) {
        if (mHandle != null && !mHandle.hasMessages(CLEAR_SPEECH_SLEEP)) {
            mHandle.sendEmptyMessage(CLEAR_SPEECH_SLEEP);
        }

        if (mHandle != null && mHandle.hasMessages(SHOW_GOODS_PIC)) {
            if (goodsPicDialog != null && goodsPicDialog.isShowing()) {
                goodsPicDialog.dismiss();
            }
            mHandle.removeMessages(SHOW_GOODS_PIC);
        }
        if (mHandle != null && mHandle.hasMessages(START_AUTO_PLAY)) {
            mHandle.removeMessages(START_AUTO_PLAY);
        }

        isPause = false;
//        clearAllMessage();
        switch (type) {
            case SalesConstant.ItemType.POWER_TYPE:
                currentIndex = powerIndex;
                break;
            case SalesConstant.ItemType.GAME_TYPE:
                currentIndex = gameIndex;
                break;
            case SalesConstant.ItemType.HOME_TYPE:
                currentIndex = homeIndex;
                break;
            case SalesConstant.ItemType.BACK_TYPE:
                currentIndex = backIndex;
                break;
        }
        currentModel = type;

        if (lists != null && lists.size() > 0) {
            isTtsFinish = true;
            isFaceFinish = true;
            isActionFinish = true;
            isMediaFinish = true;
            isMusicFinish = true;
            isPictureFinish = true;
            isDanceFinish = true;

            if (currentIndex >= lists.size() || currentIndex < 0) {
                currentIndex = 0;
            }
            L.e(TAG, "startPlay currentIndex = " + currentIndex);
            ModelContentBean currentBean = lists.get(currentIndex);

            if (currentBean != null) {
                /** 跳舞时其他的执行不了 **/
                if (!TextUtils.isEmpty(currentBean.getDanceName())) {
                    registerDynamicStateReceiver();
                    enterDanceModelDiy(currentBean.getDanceName(), System.currentTimeMillis() + "");
                    isDanceFinish = false;
                } else {
                    isDanceFinish = true;


                    /** 播放语音 **/
                    if (!TextUtils.isEmpty(currentBean.getOther())) {
//                TtsUtils.closeTTs(UltrasonicService.this);
                        isTtsFinish = false;

                        String finalTtsString = currentBean.getOther().replace(SalesConstant.ProjectInfo.PRODUCT_NAME, goodsName).
                                replace(SalesConstant.ProjectInfo.PRODUCT_GROUP, goodsGroup).
                                replace(SalesConstant.ProjectInfo.PRODUCT_DETAIL, goodsDetail);
                        if (!TextUtils.isEmpty(picPath)) {
                            if (finalTtsString.contains(goodsName)) {
                                String[] ttsStrings = finalTtsString.split(goodsName);
                                int lastLength = 0;
                                for (int i = 0; i < ttsStrings.length; i++) {
                                    int len = lastLength + ttsStrings[i].length();
                                    lastLength = len + goodsName.length();
                                    L.e(TAG, "SHOW_GOODS_PIC：" + len * wordSpeed);
                                    mHandle.sendEmptyMessageDelayed(SHOW_GOODS_PIC, len * wordSpeed);
                                }
                            }
                        }

                        if (!TextUtils.isEmpty(currentBean.getFace())) {
                            isFaceFinish = false;
                            TtsUtils.sendTts(getApplicationContext(), finalTtsString + "@#;" + currentBean.getFace());
                        } else {
                            isFaceFinish = true;
                            TtsUtils.sendTts(getApplicationContext(), finalTtsString);
                            L.e(TAG, "startPlay sendTts：" + finalTtsString);
                        }
                        isNeedReceiveTtsEnd = true;

//                        int ttsLength = finalTtsString.length();
//                        long ttsTime = ttsLength * wordSpeed;
//                        L.e(TAG, "ttsLength=" + ttsLength + "- - ttsTime=" + ttsTime);
//                        if (mHandle != null) {
//                            if (mHandle.hasMessages(TTS_FINISH)) {
//                                mHandle.removeMessages(TTS_FINISH);
//                            }
//                            mHandle.sendEmptyMessageDelayed(TTS_FINISH, ttsTime + 600);
//                        }
                    } else {
                        isTtsFinish = true;
                        isFaceFinish = true;
                    }

                    /** 播放动作 **/
                    executeAction(currentBean);

                    /** 视频 图片 **/
                    if (!TextUtils.isEmpty(currentBean.getMedia())) {

                        if (currentBean.getMedia().toLowerCase().endsWith(".mp4")) {
                            //播放视频
                            SalesApplication.from(this).playGuestVideoByPath(currentBean.getMedia());
                            isPictureFinish = true;
                        } else {
                            //播放图片
                            if (!TextUtils.isEmpty(currentBean.getMedia())) {
                                File mFile = new File(currentBean.getMedia());
                                if (mFile.exists()) {
                                    pictureDialog = new Dialog(this, R.style.Dialog_Fullscreen);
                                    View currentView = LayoutInflater.from(SalesPromotionService.this).inflate(R.layout.ul_picture_dialog, null);
                                    ImageView adPlayerPic = (ImageView) currentView.findViewById(R.id.ul_picture_img);
                                    playAdPicture(adPlayerPic, mFile);
                                    pictureDialog.setContentView(currentView);
                                    pictureDialog.getWindow().setType((WindowManager.LayoutParams.TYPE_SYSTEM_ALERT));
                                    pictureDialog.show();
                                    mHandle.sendEmptyMessageDelayed(PICTURE_FINISH, picDismissTime);
                                }
                            }
                            isMediaFinish = true;
                        }
                    } else {
                        isPictureFinish = true;
                        isMediaFinish = true;
                    }

                    /** 播放音乐 **/
                    if (!TextUtils.isEmpty(currentBean.getMusic())) {
                        isMusicFinish = false;
                        playMusic(currentBean.getMusic());
                    } else isMusicFinish = true;
                }

                currentIndex++;
                if (type == SalesConstant.ItemType.POWER_TYPE) {
                    powerIndex = currentIndex;
                } else if (type == SalesConstant.ItemType.GAME_TYPE) {
                    gameIndex = currentIndex;
                } else if (type == SalesConstant.ItemType.HOME_TYPE) {
                    homeIndex = currentIndex;
                } else if (type == SalesConstant.ItemType.BACK_TYPE) {
                    backIndex = currentIndex;
                }


            } else {
                if (groupManager != null) {
                    groupManager.reset();
                }
            }

        } else {
            TtsUtils.sendTts(context, "当前暂无数据");
        }
    }

    private String getSpByType(int currentType) {
        String playModeTypeSp = "";
        if (currentType == SalesConstant.ItemType.POWER_TYPE) {
            playModeTypeSp = SalesConstant.POWER_PLAY_MODE;
        } else if (currentType == SalesConstant.ItemType.GAME_TYPE) {
            playModeTypeSp = SalesConstant.GAME_PLAY_MODE;
        } else if (currentType == SalesConstant.ItemType.HOME_TYPE) {
            playModeTypeSp = SalesConstant.HOME_PLAY_MODE;
        } else if (currentType == SalesConstant.ItemType.BACK_TYPE) {
            playModeTypeSp = SalesConstant.BACK_PLAY_MODE;
        }
        return playModeTypeSp;
    }


    private Dialog goodsPicDialog;

    private void showGoodsPic(String picPath) {
        File mFile = new File(picPath);
        if (mFile.exists()) {
            goodsPicDialog = new Dialog(this, R.style.Dialog_Fullscreen);
            View currentView = LayoutInflater.from(SalesPromotionService.this).inflate(R.layout.ul_picture_dialog, null);
            ImageView adPlayerPic = (ImageView) currentView.findViewById(R.id.ul_picture_img);
            playAdPicture(adPlayerPic, mFile);
            goodsPicDialog.setContentView(currentView);

            WindowManager.LayoutParams lp = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT | WindowManager.LayoutParams.TYPE_SYSTEM_ERROR |
                            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | PixelFormat.TRANSPARENT);
            lp.type = WindowManager.LayoutParams.TYPE_TOAST;
            //WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE是关键！！！！！
            lp.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
            goodsPicDialog.getWindow().setAttributes(lp);

            goodsPicDialog.getWindow().setType((WindowManager.LayoutParams.TYPE_SYSTEM_ALERT));
            //图片展示时间只有clearPicTime秒
            goodsPicDialog.show();
            sendRemovePicMess();
        }
    }

    //移除图片
    private void sendRemovePicMess() {
        if (mHandle.hasMessages(SEND_CLEAR_PIC)) {
            mHandle.removeMessages(SEND_CLEAR_PIC);
        }
        mHandle.sendEmptyMessageDelayed(SEND_CLEAR_PIC, clearPicTime);
    }

    private boolean isAllPlayFinish() {
        L.e("isAllPlayFinish", "isTtsFinish = " + isTtsFinish + "--isFaceFinish = " + isFaceFinish + "--isMusicFinish = " + isMusicFinish + "--isPictureFinish = " + isPictureFinish +
                "--isActionFinish = " + isActionFinish + "--isMediaFinish = " + isMediaFinish);
        return isTtsFinish == true && isFaceFinish == true && isMusicFinish == true &&
                isPictureFinish == true && isActionFinish == true && isMediaFinish == true;
    }


    /**
     * 执行动作
     *
     * @param currentBean 当前Bean
     */
    private List<Integer> actionList;
    private int currentCount;

    private void executeAction(ModelContentBean currentBean) {
        actionList = new ArrayList<Integer>();
        String actionStr = currentBean.getAction();
        //自带动作
        if (!TextUtils.isEmpty(actionStr)) {
            if (actionStr.contains("、")) {

                String[] faceArr = actionStr.split("、");
                int faceCount = faceArr.length;
                for (int i = 0; i < faceCount; i++) {
                    if (!TextUtils.isEmpty(faceArr[i].trim())) {
                        actionList.add(Integer.parseInt(faceArr[i]));
                        L.e(TAG, "faceStr--faceArr->" + faceCount + "     " + faceArr[i]);
                    }
                }
            } else {
                actionList.add(Integer.parseInt(actionStr));
            }
        }

        if (actionList != null && actionList.size() > 0) {
            currentCount = 0;
            isActionFinish = false;
            checkAction(actionList.get(currentCount));
        } else
            isActionFinish = true;
    }

    /**
     * 播放图片
     *
     * @param view, mFile
     * @return
     */
    public void playAdPicture(ImageView view, File mFile) {
        try {
            if (mFile != null && mFile.exists()) {
                Bitmap bitmap = view.getDrawingCache();
                if (bitmap != null)
                    bitmap.recycle();
                Log.d("TAG", "playAdPicture: ...................");
                Bitmap mp = BitmapUtils.getimage(mFile.getPath());
                view.setImageBitmap(mp);
                view.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 检测动作
     *
     * @param action
     * @return
     */
    private void checkAction(int action) {
        L.i("执行动作", "action = " + action);
        if (action != -1) {
            //执行脚本
            String sport = FileUtils.getActionFile(this, "action/action_" + action);
            groupManager.execute(sport, groupTaskListener);
            L.i("执行动作", "actionJson = " + sport);
        }

    }

    /**
     * 播放音乐
     *
     * @param music 音乐路径
     */
    public boolean musicNeedSay = false;

    private void playMusic(String music) {
        //判断文件是否存在
        File file = new File(music);
        if (!file.exists()) {
            isMusicFinish = true;
            return;
        }

        if (musicPlayer != null) {
            musicPlayer.stop();
        }
        assert musicPlayer != null;
        musicPlayer.playUrl(music, new MusicPlayer.OnMusicCompletionListener() {
            @Override
            public void onCompletion(boolean isPlaySuccess) {
                L.e(TAG, "执行播放音乐结束");
                isMusicFinish = true;
                musicNeedSay = false;
                mHandle.removeMessages(MUSIC_NEED_SAY);
                L.e(TAG, "--------------------MUSIC_FINISH");
                finishAfterDoSomeThing();
            }

            @Override
            public void onPrepare(int Duration) {
                // TODO需要循環发送说说话表情
                musicNeedSay = true;
                mHandle.sendEmptyMessage(MUSIC_NEED_SAY);
            }
        });

    }

    /**
     * 进入跳舞模式 Diy
     */
    public void enterDanceModelDiy(final String value, final String time) {
        String nickName = "跳舞时, 避障关闭，请与" + RobotManager.getInstance(context).getRobotName() + "保持一定距离";
        TtsUtils.sendTts(this, nickName);
        mHandle.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent mIntent = new Intent("com.efrobot.dance.ENTER_DANCE_MODEL");
                mIntent.putExtra("dance_name", value);
                mIntent.putExtra("key", "speechDiy");
                mIntent.putExtra("time", time);
                mIntent.putExtra("TIME_INFO", System.currentTimeMillis());
                mIntent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
                getApplicationContext().sendBroadcast(mIntent);
            }
        }, nickName.length() * 270);

//        new Timer().schedule(new TimerTask() {
//            @Override
//            public void run() {
//                Intent mIntent = new Intent("com.efrobot.dance.ENTER_DANCE_MODEL");
//                mIntent.putExtra("dance_name", value);
//                mIntent.putExtra("key", "speechDiy");
//                mIntent.putExtra("time", time);
//                mIntent.putExtra("TIME_INFO", System.currentTimeMillis());
//                mIntent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
//                getApplicationContext().sendBroadcast(mIntent);
//            }
//        }, nickName.length() * 270);
    }

    /**
     * 执行脚本监听
     */
    GroupManager.OnGroupTaskExecuteListener groupTaskListener = new GroupManager.OnGroupTaskExecuteListener() {
        @Override
        public void onStart() {
            //开始执行脚本动作
            currentCount++;
        }

        @Override
        public void onStop() {
            isActionFinish = true;
            groupManager.removeListener(groupTaskListener);
            L.e(TAG, "执行动作结束");
            if (mHandle != null)
                mHandle.sendEmptyMessage(PLAY_MORE_ACTION);
        }
    };

    /**
     * 暂停
     **/
    private boolean isPause = false;

    public void pause() {
        hitRobotHead();
        L.e(TAG, "pause()");
        isPause = true;
        isNeedReceiveTtsEnd = false;
        removeSpeechState(SalesPromotionService.this, 13);
        removeSpeechState(SalesPromotionService.this, 11);
        stopAllPlaying();
        if (mHandle != null) {
            mHandle.removeMessages(SHOW_GOODS_PIC);
        }
        if (goodsPicDialog != null && goodsPicDialog.isShowing()) {
            goodsPicDialog.dismiss();
        }
        if (groupManager != null) {
            groupManager.stop();
            groupManager.reset();
        }
    }

    private void hitRobotHead() {
        Intent intent = new Intent("cn.efrobot.speech.ACTION_REMOTE_CONTROLLER");
        JSONObject json = new JSONObject();
        try {
            json.put("type", "2");
            json.put("detail", "");
            json.put("reply", "");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        intent.putExtra("content", json.toString());
        intent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
        sendBroadcast(intent);
    }

    private void stopAllPlaying() {
        clearAllMessage();

        if (!isTtsFinish) {
            isTtsFinish = true;
            isFaceFinish = true;
        }

        //音乐说话表情停止
        musicNeedSay = false;

        if (musicPlayer != null)
            musicPlayer.stop();
        isMusicFinish = true;

        //图片停止
        if (pictureDialog != null && pictureDialog.isShowing()) {
            pictureDialog.dismiss();
            isPictureFinish = true;
        }

        if (goodsPicDialog != null && goodsPicDialog.isShowing()) {
            goodsPicDialog.dismiss();
        }

        if (!isActionFinish) {
            groupManager.stop();
            isActionFinish = true;
        }

        if (!isMediaFinish) {
            SalesApplication.from(this).dismissGuestVideo();
            isMediaFinish = true;
        }

    }

    private void registerEvent() {
        speechManager = SpeechManager.getInstance(this);
        speechManager.registerKeyListener(new SpeechManager.ISpeechRegisterResultListener() {
            @Override
            public void onRegisterSuccess() {
                Log.e(TAG, "registerKeyListener onRegisterSuccess");
            }

            @Override
            public void onRegisterFail() {
                Log.e(TAG, "registerKeyListener onRegisterFail");
            }
        });
        SalesSpeechProvider.setOnKeyEventListener(this);
        speechManager.registerSpeechListener(new SpeechManager.ISpeechRegisterResultListener() {
            @Override
            public void onRegisterSuccess() {
                Log.e(TAG, "registerSpeechListener onRegisterSuccess");
            }

            @Override
            public void onRegisterFail() {
                Log.e(TAG, "registerSpeechListener onRegisterSuccess");
            }
        });
        //注册面罩广播
        registerLiboard();
    }

    private void registerLiboard() {
        IntentFilter dynamic_filter = new IntentFilter();
        dynamic_filter.addAction(ROBOT_MASK_CHANGE);            //添加动态广播的Action
        registerReceiver(lidBoardReceive, dynamic_filter);
    }

    /**
     * 监听盖子状态
     */
    public final static String ROBOT_MASK_CHANGE = "android.intent.action.MASK_CHANGED";
    public final static String KEYCODE_MASK_ONPROGRESS = "KEYCODE_MASK_ONPROGRESS"; //开闭状态
    public final static String KEYCODE_MASK_CLOSE = "KEYCODE_MASK_CLOSE"; //关闭面罩
    public final static String KEYCODE_MASK_OPEN = "KEYCODE_MASK_OPEN";  //打开面罩
    private BroadcastReceiver lidBoardReceive = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (ROBOT_MASK_CHANGE.equals(intent.getAction())) {
                boolean isOpen = intent.getBooleanExtra(KEYCODE_MASK_OPEN, false);
                boolean isOpening = intent.getBooleanExtra(KEYCODE_MASK_ONPROGRESS, false);
                if (isOpen || isOpening) {
                    L.e(TAG, "监听盖子状态:" + "isOpen = " + isOpen + " isOpening" + isOpening);
                    stopAllPlaying();
                    stopSelf();
                    unregisterReceiver(lidBoardReceive);
                }

            }
        }
    };

    public void againPlayCurrentItem() {
        pause();
        currentIndex--;
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                startPlay(currentModel);
            }
        }, 600);
    }

    private void startPlayMode(int type) {

        String mSpPlayMode = getSpByType(type);
        mPlayMode = PreferencesUtils.getInt(this, mSpPlayMode, SalesConstant.CIRCLE_MODE);
        L.e(TAG, "startPlayMode : " + "type = " + type + "  mSpPlayMode  = " + mSpPlayMode + "  mPlayMode = " + mPlayMode);
        if (currentModel != type) {
            isUserBreak = true;
            lists = ModelContentManager.getInstance(this).queryItem(currentModelName, type);
            stopAllPlaying();
            startPlay(type);
        } else {
            //单条模式才能按键继续
            if (mPlayMode == SalesConstant.ORDER_MODE || isPause) {
                if (isAllPlayFinish())
                    startPlay(type);
            }
        }
    }

    private boolean checkTime() {
        boolean isCanPress = false;
        currentTime = System.currentTimeMillis();
        if (currentTime - lastKeyDownTime > 600) {
            isCanPress = true;
        }
        return isCanPress;
    }

    private long lastKeyDownTime = 0;
    private long currentTime = 0;
    private boolean isLongPressKey = false;

    @Override
    public void onKeyDown(int keyCode) {
        L.e(TAG, "onKeyDown:" + keyCode);
        switch (keyCode) {
            case KeyEvent.KEYCODE_POWER:
                if (!checkTime()) return;
                startPlayMode(SalesConstant.ItemType.POWER_TYPE);
                break;
            case KeyEvent.KEYCODE_MENU:
                if (!checkTime()) return;
                startPlayMode(SalesConstant.ItemType.GAME_TYPE);
                break;
            case KeyEvent.KEYCODE_HOME:
                if (!checkTime()) return;
                startPlayMode(SalesConstant.ItemType.HOME_TYPE);
                break;
            case KeyEvent.KEYCODE_BACK:
                if (!checkTime()) return;
                startPlayMode(SalesConstant.ItemType.BACK_TYPE);
                break;
            case KeyEvent.KEYCODE_ENTER:
                L.e(TAG, "onKeyDown: KeyEvent.KEYCODE_ENTER");
                try {
                    if (!isLongPressKey) {
                        if (!isPause) {
                            pause();
                            currentIndex--;
                        }
                        if (mHandle != null && !mHandle.hasMessages(CLEAR_SPEECH_SLEEP)) {
                            mHandle.sendEmptyMessage(CLEAR_SPEECH_SLEEP);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case KeyEvent.KEYCODE_DPAD_UP:
                moveManager.doDownExecute(keyCode);
                break;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                moveManager.doDownExecute(keyCode);
                break;
            case KeyEvent.KEYCODE_DPAD_LEFT:
                moveManager.doDownExecute(keyCode);
                break;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                moveManager.doDownExecute(keyCode);
                break;
            case KeyEvent.KEYCODE_VOLUME_UP:
                audio.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE, AudioManager.FLAG_PLAY_SOUND | AudioManager.FLAG_SHOW_UI);
                break;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                audio.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_LOWER, AudioManager.FLAG_PLAY_SOUND | AudioManager.FLAG_SHOW_UI);
                break;
        }
    }

    @Override
    public void onKeyUp(int keyCode) {
        lastKeyDownTime = currentTime;
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_UP:
                moveManager.doUpExecute();
                break;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                moveManager.doUpExecute();
                break;
            case KeyEvent.KEYCODE_DPAD_LEFT:
                moveManager.doUpExecute();
                break;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                moveManager.doUpExecute();
                break;
            case KeyEvent.KEYCODE_ENTER:
                if (isLongPressKey) {
                    isLongPressKey = false;
                }
                break;
        }
    }

    @Override
    public void onKeyLongPress(int keyCode) {
        L.e(TAG, "onKeyLongPress:" + keyCode);
        switch (keyCode) {
            case KeyEvent.KEYCODE_ENTER:
                isLongPressKey = true;
                try {
                    if (mHandle != null && mHandle.hasMessages(CLEAR_SPEECH_SLEEP)) {
                        mHandle.removeMessages(CLEAR_SPEECH_SLEEP);
                    }
                    String speechText = "您可以和" + RobotManager.getInstance(this).getRobotName() + "聊天啦";
                    TtsUtils.sendTts(this, speechText);
                    speechManager.openSpeechDiscern(this);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    /**
     * 跳舞自动结束
     */

    private void registerDynamicStateReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(DANCE_COMPLETE_PLAY);
        broadcastReceiver = new DanceBroadcastReceiver();
        registerReceiver(broadcastReceiver, filter);
    }


    String DANCE_COMPLETE_PLAY = "com.efrobot.dance.ROBOT_DANCE_FINISH";

    private class DanceBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(DANCE_COMPLETE_PLAY)) {
                unregisterReceiver(this);
                isDanceFinish = true;
                L.e(TAG, "--------------------com.efrobot.dance.ROBOT_DANCE_FINISH");
                finishAfterDoSomeThing();
            }
        }
    }

    private void clearAllMessage() {
        if (mHandle != null) {
            mHandle.removeMessages(TTS_FINISH);
            mHandle.removeMessages(PLAY_MORE_ACTION);
            mHandle.removeMessages(VIDEO_FINISH);
            mHandle.removeMessages(MUSIC_NEED_SAY);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isPause = false;
        SalesApplication.from(this).setSalesPromotionService(null);
        SpeechManager.getInstance(this).controlHead(this, false);
        L.e(TAG, "onDestroy()");
        try {
            isNeedReceiveTtsEnd = false;
            speechManager.unregisterKeyListener(this);
            speechManager.unRegisterSpeechListener(this);
            if (broadcastReceiver != null) {
                unregisterReceiver(broadcastReceiver);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        stopAllPlaying();
        if (mHandle != null) {
            mHandle.removeCallbacksAndMessages(null);
            mHandle.removeMessages(CLEAR_SPEECH_SLEEP);
            mHandle.removeMessages(SAY_START_WORDS);
            mHandle = null;
        }

    }

    @Override
    public void onRobotSateChange(int robotStateIndex, int newState) {
        if (robotStateIndex == RobotState.ROBOT_STATE_INDEX_HEAD_KEY) {
//            isPause = true;
//            if (mHandle != null)
//                mHandle.removeMessages(TTS_FINISH);
//
//            if (!isTtsFinish) {
//                isTtsFinish = true;
//                isFaceFinish = true;
//            }

//            if (!isPictureFinish)
//                if (pictureDialog != null && pictureDialog.isShowing()) {
//                    pictureDialog.dismiss();
//                    isPictureFinish = true;
//                }

            if (!isActionFinish) {
                isActionFinish = true;
            }

//            if (!isMusicFinish) {
//                if (musicPlayer != null)
//                    musicPlayer.stop();
//                isMusicFinish = true;
//
//                musicNeedSay = false;
//                if (mHandle != null)
//                    mHandle.removeMessages(MUSIC_NEED_SAY);
//            }

//            if (!isMediaFinish) {
//                isMediaFinish = true;
//                SalesApplication.from(this).dismissGuestVideo();
//            }

        }

    }
}
