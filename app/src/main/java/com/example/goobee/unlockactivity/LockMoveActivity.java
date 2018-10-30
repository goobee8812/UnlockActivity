package com.example.goobee.unlockactivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.TypedArray;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.TimeZone;

public class LockMoveActivity extends AppCompatActivity {

    private static final String TAG = "TestMainActivity";
    ImageView mImgRabbitAnim;
    ImageView mImgFingerAnim;
    ImageView mImgCloudAnim;
    FrameAnimation frameRabbitAnimation;
    FrameAnimation frameFingerAnimation;
    FrameAnimation frameCloudAnimation;

    TextView mTimeWeek;
    TextView mTimeDate;
    ImageView mHourShi;
    ImageView mHourGe;
    ImageView mMinShi;
    ImageView mMinGe;
    LinearLayout timeLy;

    private HashMap<String, Integer> numMap = new HashMap<>();

    int xPoint = 0;
    int yPoint = 0;

    private static final int REFRESH_ANIM_VIEW = 0x0100; //刷新动画
    private static final int REFRESH_ANIM_VIEW_HIDE = 0x0102; //刷新动画
    private static final int REFRESH_TIME_VIEW = 0x0104;        //刷新时间
    private static final int REFRESH_DATE_VIEW = 0x0105;        //刷新日期

    Handler mHandle = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int event = msg.what;
            Object data = msg.obj;
            switch ((int) event) {
                case REFRESH_ANIM_VIEW: {
                    if (frameRabbitAnimation != null) {
                        if (frameRabbitAnimation.isPause()) {
                            frameRabbitAnimation.restartAnimation();
                        }
                    } else {
                        initRabbitAinm();
                    }
                    if (frameFingerAnimation != null) {
                        if (frameFingerAnimation.isPause()) {
                            frameFingerAnimation.restartAnimation();
                        }
                    } else {
                        initFingerAinm();
                    }

                    if (frameCloudAnimation != null) {
                        if (frameCloudAnimation.isPause()) {
                            frameCloudAnimation.restartAnimation();
                        }
                    } else {
                        initCloudAinm();
                    }

                }
                break;
                case REFRESH_ANIM_VIEW_HIDE: {
                    if (frameRabbitAnimation != null) {
                        frameRabbitAnimation.pauseAnimation();
                    }
                }
                break;
                case REFRESH_TIME_VIEW:
                    changeTime();
                    break;
                case REFRESH_DATE_VIEW:
                    updateDate();
                    break;
            }
        }
    };

    private void changeTime() {
        String date_time = getDateTime("HH:mm");
        Log.d(TAG, "changeTime: " + date_time);
        mHourShi.setImageResource(numMap.get(date_time.substring(0,1)));
        mHourGe.setImageResource(numMap.get(date_time.substring(1,2)));
        mMinShi.setImageResource(numMap.get(date_time.substring(3,4)));
        mMinGe.setImageResource(numMap.get(date_time.substring(4,5)));
    }
    private String getDateTime(String StringFormat) {
        SimpleDateFormat format = new SimpleDateFormat(StringFormat, Locale.CHINA);
        return format.format(new Date());
    }
    private void updateDate(){
        final Calendar c = Calendar.getInstance();
        String mYear;
        String mMonth;
        String mDay;
        String mWay;
        c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        mYear = String.valueOf(c.get(Calendar.YEAR)); // 获取当前年份
        mMonth = String.valueOf(c.get(Calendar.MONTH) + 1);// 获取当前月份
        mDay = String.valueOf(c.get(Calendar.DAY_OF_MONTH));// 获取当前月份的日期号码
        mWay = String.valueOf(c.get(Calendar.DAY_OF_WEEK));
        if("1".equals(mWay)){
            mWay ="星期天";
        }else if("2".equals(mWay)){
            mWay ="星期一";
        }else if("3".equals(mWay)){
            mWay ="星期二";
        }else if("4".equals(mWay)){
            mWay ="星期三";
        }else if("5".equals(mWay)){
            mWay ="星期四";
        }else if("6".equals(mWay)){
            mWay ="星期五";
        }else if("7".equals(mWay)){
            mWay ="星期六";
        }
        mTimeWeek.setText(mWay);
        mTimeDate.setText(mYear + "年" + mMonth + "月" + mDay + "日");
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lock_screen_activity);

        initNumMap();
        //系统ACTION_TIME_TICK一分钟一次
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_TIME_TICK);
        timeTickReceiver = new TimeTickReceiver();
        registerReceiver(timeTickReceiver, intentFilter);

        timeLy = findViewById(R.id.lock_time_ll);
        timeLy.bringToFront();
        mImgRabbitAnim = findViewById(R.id.lock_rabbit_iv);
        mImgFingerAnim = findViewById(R.id.lock_finger_iv);
        mImgCloudAnim = findViewById(R.id.lock_cloud_iv);
        mTimeWeek = findViewById(R.id.lock_time_week);
        mTimeDate = findViewById(R.id.lock_time_date);
        mHourShi = findViewById(R.id.lock_time_hour_shi);
        mHourGe = findViewById(R.id.lock_time_hour_ge);
        mMinShi = findViewById(R.id.lock_time_min_shi);
        mMinGe = findViewById(R.id.lock_time_min_ge);

        mImgFingerAnim.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        xPoint = (int)motionEvent.getX();
                        yPoint = (int)motionEvent.getY();
                        Log.d(TAG, "-old--->x= " + xPoint + " y= " + yPoint );
                        break;
                    case MotionEvent.ACTION_UP:
                        int newX = (int)motionEvent.getX();
                        int newY = (int)motionEvent.getY();
                        Log.d(TAG, "-new--->x= " + newX + " y= " + newY );
                        if (Math.abs(yPoint-newY) < 20){  //判断水平滑动
                            if (newX - xPoint > 20){  //判断向右滑动
                                if (mImgFingerAnim != null) {
                                    if (frameFingerAnimation != null) {
                                        frameFingerAnimation.release();
                                    }
                                    mImgFingerAnim.setImageDrawable(null);
                                }

                                if (mImgRabbitAnim != null) {
                                    if (frameRabbitAnimation != null) {
                                        frameRabbitAnimation.release();
                                    }
                                    mImgRabbitAnim.setImageDrawable(null);
                                    initRabbitUnlockAinm();
                                }
                            }
                        }
                        break;
                    default:
                        break;
                }
                return true;
            }
        });

//        mTimeWeek.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (mImgFingerAnim != null) {
//                    if (frameFingerAnimation != null) {
//                        frameFingerAnimation.release();
//                    }
//                    mImgFingerAnim.setImageDrawable(null);
//                }
//
//                if (mImgRabbitAnim != null) {
//                    if (frameRabbitAnimation != null) {
//                        frameRabbitAnimation.release();
//                    }
//                    mImgRabbitAnim.setImageDrawable(null);
//                    initRabbitUnlockAinm();
//                }
//            }
//        });
    }

    private void initRabbitAinm() {
        frameRabbitAnimation = new FrameAnimation(mImgRabbitAnim, getRabbitRes(), 100, true);
        frameRabbitAnimation.setAnimationListener(new FrameAnimation.AnimationListener() {
            @Override
            public void onAnimationStart() {

            }

            @Override
            public void onAnimationEnd() {

            }

            @Override
            public void onAnimationRepeat() {

            }
        });
    }

    private void initRabbitUnlockAinm() {
        frameRabbitAnimation = new FrameAnimation(mImgRabbitAnim, getUnlockRabbitRes(), 100, false);
        frameRabbitAnimation.setAnimationListener(new FrameAnimation.AnimationListener() {
            @Override
            public void onAnimationStart() {

            }

            @Override
            public void onAnimationEnd() {
                //结束后跳转界面
                Intent intent = new Intent(LockMoveActivity.this,Main2Activity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onAnimationRepeat() {

            }
        });
    }

    private void initFingerAinm() {
        frameFingerAnimation = new FrameAnimation(mImgFingerAnim, getFingerRes(), 100, true);
        frameFingerAnimation.setAnimationListener(new FrameAnimation.AnimationListener() {
            @Override
            public void onAnimationStart() {

            }

            @Override
            public void onAnimationEnd() {

            }

            @Override
            public void onAnimationRepeat() {

            }
        });
    }

    private void initCloudAinm() {
        frameCloudAnimation = new FrameAnimation(mImgCloudAnim, getCloudRes(), 100, true);
        frameCloudAnimation.setAnimationListener(new FrameAnimation.AnimationListener() {
            @Override
            public void onAnimationStart() {

            }

            @Override
            public void onAnimationEnd() {

            }

            @Override
            public void onAnimationRepeat() {

            }
        });
    }

    @Override
    public void onResume() {
        if (mImgRabbitAnim != null){
            mHandle.sendEmptyMessage(REFRESH_ANIM_VIEW);
        }
        mHandle.sendEmptyMessage(REFRESH_TIME_VIEW);
        mHandle.sendEmptyMessage(REFRESH_DATE_VIEW);
        super.onResume();
    }

    @Override
    public void onStop() {
        if (frameRabbitAnimation != null) {
            frameRabbitAnimation.pauseAnimation();
        }
        if (frameFingerAnimation != null) {
            frameFingerAnimation.pauseAnimation();
        }
        if (frameCloudAnimation != null) {
            frameCloudAnimation.pauseAnimation();
        }
        super.onStop();
    }

    @Override
    public void onDestroy() {
        if (mImgRabbitAnim != null) {
            if (frameRabbitAnimation != null) {
                frameRabbitAnimation.release();
            }
            mImgRabbitAnim.setImageDrawable(null);
        }
        if (mImgFingerAnim != null) {
            if (frameFingerAnimation != null) {
                frameFingerAnimation.release();
            }
            mImgFingerAnim.setImageDrawable(null);
        }
        if (mImgCloudAnim != null) {
            if (frameCloudAnimation != null) {
                frameCloudAnimation.release();
            }
            mImgCloudAnim.setImageDrawable(null);
        }
        unregisterReceiver(timeTickReceiver);
        super.onDestroy();
    }

    /**
     * 获取需要播放的动画资源
     */
    private int[] getRabbitRes() {
        TypedArray typedArray = getResources().obtainTypedArray(R.array.alilo_lock_rabbit);
        int len = typedArray.length();
        int[] resId = new int[len];
        for (int i = 0; i < len; i++) {
            resId[i] = typedArray.getResourceId(i, -1);
        }
        typedArray.recycle();
        return resId;
    }

    /**
     * 获取需要播放的动画资源
     */
    private int[] getUnlockRabbitRes() {
        TypedArray typedArray = getResources().obtainTypedArray(R.array.alilo_unlock_rabbit);
        int len = typedArray.length();
        int[] resId = new int[len];
        for (int i = 0; i < len; i++) {
            resId[i] = typedArray.getResourceId(i, -1);
        }
        typedArray.recycle();
        return resId;
    }
    /**
     * 获取需要播放的动画资源
     */
    private int[] getFingerRes() {
        TypedArray typedArray = getResources().obtainTypedArray(R.array.alilo_lock_finger);
        int len = typedArray.length();
        int[] resId = new int[len];
        for (int i = 0; i < len; i++) {
            resId[i] = typedArray.getResourceId(i, -1);
        }
        typedArray.recycle();
        return resId;
    }

    /**
     * 获取需要播放的动画资源
     */
    private int[] getCloudRes() {
        TypedArray typedArray = getResources().obtainTypedArray(R.array.alilo_lock_cloud);
        int len = typedArray.length();
        int[] resId = new int[len];
        for (int i = 0; i < len; i++) {
            resId[i] = typedArray.getResourceId(i, -1);
        }
        typedArray.recycle();
        return resId;
    }

    private void initNumMap() {
        numMap.put("0",R.drawable.alilo_time_0);
        numMap.put("1",R.drawable.alilo_time_1);
        numMap.put("2",R.drawable.alilo_time_2);
        numMap.put("3",R.drawable.alilo_time_3);
        numMap.put("4",R.drawable.alilo_time_4);
        numMap.put("5",R.drawable.alilo_time_5);
        numMap.put("6",R.drawable.alilo_time_6);
        numMap.put("7",R.drawable.alilo_time_7);
        numMap.put("8",R.drawable.alilo_time_8);
        numMap.put("9",R.drawable.alilo_time_9);
    }


    private TimeTickReceiver timeTickReceiver;
    class TimeTickReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            mHandle.sendEmptyMessage(REFRESH_TIME_VIEW);
        }
    }
}
