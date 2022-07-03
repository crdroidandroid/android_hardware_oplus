/*
 * Copyright (C) 2019 The OmniROM Project
 * SPDX-License-Identifier: Apache-2.0
 */

package org.lineageos.settings.services;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.provider.Settings;
import android.service.dreams.DreamService;
import android.service.dreams.IDreamManager;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.util.Log;
import android.util.TypedValue;

import java.io.BufferedReader;
import java.io.FileReader;
import java.lang.StringBuffer;
import java.lang.Math;

import org.lineageos.settings.FileUtils;
import org.lineageos.settings.R;

public class FPSInfoService extends Service {
    private View mView;
    private Thread mCurFPSThread;
    private final String TAG = "FPSInfoService";
    private String mFps = null;
    private int mPosition;

    private static final int MEASURED_FPS_NODE = R.string.node_fps_info_service;

    private static final String POSITION_KEY = "device_settings_fps_position";
    private static final String COLOR_KEY = "device_settings_fps_color";
    private static final String SIZE_KEY = "device_settings_fps_text_size";

    private static final int POSITION_TOP_LEFT = 0;
    private static final int POSITION_TOP_CENTER = 1;
    private static final int POSITION_TOP_RIGHT = 2;
    private static final int POSITION_BOTTOM_LEFT = 3;
    private static final int POSITION_BOTTOM_CENTER = 4;
    private static final int POSITION_BOTTOM_RIGHT = 5;
    private static final int POSITION_CENTER_LEFT = 6;
    private static final int POSITION_CENTER_CENTER = 7;
    private static final int POSITION_CENTER_RIGHT = 8;

    private static final int[] colorArray = {Color.GREEN, Color.RED, Color.MAGENTA, Color.BLUE, Color.CYAN, Color.YELLOW, Color.BLACK, Color.WHITE, Color.GRAY, Color.LTGRAY, Color.DKGRAY};
    private static final int[] sizeArray = {12, 14, 16, 18, 20, 22, 24, 26, 28};

    private IDreamManager mDreamManager;

    private class FPSView extends View {
        private Paint mOnlinePaint;
        private float mAscent;
        private int mFH;
        private int mMaxWidth;

        private int mNeededWidth;
        private int mNeededHeight;

        private boolean mDataAvail;

        private Handler mCurFPSHandler = new Handler() {
            public void handleMessage(Message msg) {
                if(msg.obj == null || msg.what != 1)
                    return;

                mFps = parseMeasuredFps((String) msg.obj);
                mDataAvail = true;
                updateDisplay();
            }
        };

        FPSView(Context c) {
            super(c);
            float density = c.getResources().getDisplayMetrics().density;
            int paddingPx = Math.round(10 * density);
            setPadding(paddingPx, paddingPx, paddingPx, paddingPx);
            setBackgroundColor(Color.argb(0x0, 0, 0, 0));

            final int textSize = Math.round(getSize(c) * density);

            Typeface typeface = Typeface.create("google-sans", Typeface.BOLD);

            mOnlinePaint = new Paint();
            mOnlinePaint.setTypeface(typeface);
            mOnlinePaint.setAntiAlias(true);
            mOnlinePaint.setTextSize(textSize);

            int mColorIndex = getColorIndex(c);
            if (mColorIndex < colorArray.length) {
                mOnlinePaint.setColor(colorArray[mColorIndex]);
            } else {
                mOnlinePaint.setColor(getColorAccent(c));
            }
            mOnlinePaint.setShadowLayer(5.0f, 0.0f, 0.0f, Color.BLACK);

            mAscent = mOnlinePaint.ascent();
            float descent = mOnlinePaint.descent();
            mFH = (int) (descent - mAscent + .5f);

            final String maxWidthStr = "FPS: XYZ";
            mMaxWidth = (int) mOnlinePaint.measureText(maxWidthStr);

            updateDisplay();
        }

        @Override
        protected void onAttachedToWindow() {
            super.onAttachedToWindow();
        }

        @Override
        protected void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            mCurFPSHandler.removeMessages(1);
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            setMeasuredDimension(resolveSize(mNeededWidth, widthMeasureSpec),
                    resolveSize(mNeededHeight, heightMeasureSpec));
        }

        private String getFPSInfoString() {
            return mFps;
        }

        @Override
        public void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            if (!mDataAvail) {
                return;
            }

            final int W = mNeededWidth;
            final int LEFT = getWidth() - 1;

            int x = LEFT - mPaddingLeft;
            int top = mPaddingTop + 2;

            int y = mPaddingTop - (int) mAscent;

            String s = getFPSInfoString();
            canvas.drawText(s, x - mMaxWidth,
                    y - 1, mOnlinePaint);
            y += mFH;
        }

        private String parseMeasuredFps(String data) {
            String result = "err";
            try {
                float fps = Float.parseFloat(data.trim().split("\\s+")[1]);
                result = String.valueOf(Math.round(fps));
            } catch (NumberFormatException e) {
                Log.e(TAG, "NumberFormatException occured at parsing FPS data");
            }
            return "FPS: " + result;
        }

        private int getColorAccent(Context context) {
            TypedValue typedValue = new TypedValue();
            ContextThemeWrapper contextThemeWrapper = new ContextThemeWrapper(context,
                    android.R.style.Theme_DeviceDefault);
            contextThemeWrapper.getTheme().resolveAttribute(android.R.attr.colorAccent,
                    typedValue, true);
            return typedValue.data; 
        }

        void updateDisplay() {
            if (!mDataAvail) {
                return;
            }

            int neededWidth = mPaddingLeft + mPaddingRight + mMaxWidth + 40;
            int neededHeight = mPaddingTop + mPaddingBottom + 70;  //In case incomplete display on largest display size.
            if (neededWidth != mNeededWidth || neededHeight != mNeededHeight) {
                mNeededWidth = neededWidth;
                mNeededHeight = neededHeight;
                requestLayout();
            } else {
                invalidate();
            }
        }

        public Handler getHandler(){
            return mCurFPSHandler;
        }
    }

    private String getFile() {
        String file = getApplicationContext().getString(MEASURED_FPS_NODE);
        if (FileUtils.fileWritable(file)) {
            return file;
        }
        return null;
    }


    protected class CurFPSThread extends Thread {
        private boolean mInterrupt = false;
        private Handler mHandler;

        public CurFPSThread(Handler handler){
            mHandler = handler;
        }

        public void interrupt() {
            mInterrupt = true;
        }

        @Override
        public void run() {
            try {
                while (!mInterrupt) {
                    sleep(1000);
                    StringBuffer sb = new StringBuffer();
                    String fpsVal = FPSInfoService.readOneLine(getFile());
                    mHandler.sendMessage(mHandler.obtainMessage(1, fpsVal));
                }
            } catch (InterruptedException e) {
                return;
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();

        mView = new FPSView(this);
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_SECURE_SYSTEM_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE|
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            PixelFormat.TRANSLUCENT);

        mPosition = getPosition(this);
        switch (mPosition) {
            case POSITION_TOP_LEFT:
                params.gravity = Gravity.LEFT | Gravity.TOP;
                break;
            case POSITION_TOP_CENTER:
                params.gravity = Gravity.CENTER | Gravity.TOP;
                break;
            case POSITION_TOP_RIGHT:
                params.gravity = Gravity.RIGHT | Gravity.TOP;
                break;
            case POSITION_BOTTOM_LEFT:
                params.gravity = Gravity.LEFT | Gravity.BOTTOM;
                break;
            case POSITION_BOTTOM_CENTER:
                params.gravity = Gravity.CENTER | Gravity.BOTTOM;
                break;
            case POSITION_BOTTOM_RIGHT:
                params.gravity = Gravity.RIGHT | Gravity.BOTTOM;
                break;
            case POSITION_CENTER_LEFT:
                params.gravity = Gravity.LEFT | Gravity.CENTER;
                break;
            case POSITION_CENTER_CENTER:
                params.gravity = Gravity.CENTER | Gravity.CENTER;
                break;
            case POSITION_CENTER_RIGHT:
                params.gravity = Gravity.RIGHT | Gravity.CENTER;
                break;
            default:
                break;
        }
        params.setTitle("FPS Info");

        startThread();

        mDreamManager = IDreamManager.Stub.asInterface(
                ServiceManager.checkService(DreamService.DREAM_SERVICE));
        IntentFilter screenStateFilter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        screenStateFilter.addAction(Intent.ACTION_SCREEN_OFF);
        registerReceiver(mScreenStateReceiver, screenStateFilter);

        WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        wm.addView(mView, params);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopThread();
        ((WindowManager) getSystemService(WINDOW_SERVICE)).removeView(mView);
        mView = null;
        unregisterReceiver(mScreenStateReceiver);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private static String readOneLine(String fname) {
        BufferedReader br;
        String line = null;
        try {
            br = new BufferedReader(new FileReader(fname), 512);
            try {
                line = br.readLine();
            } finally {
                br.close();
            }
        } catch (Exception e) {
            return null;
        }
        return line;
    }

    private BroadcastReceiver mScreenStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
                Log.d(TAG, "ACTION_SCREEN_ON " + isDozeMode());
                if (!isDozeMode()) {
                    startThread();
                    mView.setVisibility(View.VISIBLE);
                }
            } else if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                Log.d(TAG, "ACTION_SCREEN_OFF");
                mView.setVisibility(View.GONE);
                stopThread();
            }
        }
    };

    private boolean isDozeMode() {
        try {
            if (mDreamManager != null && mDreamManager.isDreaming()) {
                return true;
            }
        } catch (RemoteException e) {
            return false;
        }
        return false;
    }

    private void startThread() {
        Log.d(TAG, "started CurFPSThread");
        mCurFPSThread = new CurFPSThread(mView.getHandler());
        mCurFPSThread.start();
    }

    private void stopThread() {
        if (mCurFPSThread != null && mCurFPSThread.isAlive()) {
            Log.d(TAG, "stopping CurFPSThread");
            mCurFPSThread.interrupt();
            try {
                mCurFPSThread.join();
            } catch (InterruptedException e) {
            }
        }
        mCurFPSThread = null;
    }

    private static int getPosition(Context context) {
        return Settings.System.getInt(context.getContentResolver(), POSITION_KEY, POSITION_TOP_LEFT);
    }

    public static void setPosition(Context context, int position) {
        Settings.System.putInt(context.getContentResolver(), POSITION_KEY, position);
    }

    public static boolean isPositionChanged(Context context, int position) {
        return getPosition(context) != position;
    }

    private static int getColorIndex(Context context) {
        return Settings.System.getInt(context.getContentResolver(), COLOR_KEY, 0);
    }

    public static void setColorIndex(Context context, int index) {
        Settings.System.putInt(context.getContentResolver(), COLOR_KEY, index);
    }

    public static boolean isColorChanged(Context context, int index) {
        return getColorIndex(context) != index;
    }

    private static int getSizeIndex(Context context) {
        return Settings.System.getInt(context.getContentResolver(), SIZE_KEY, 2);
    }

    private static int getSize(Context context) {
        return sizeArray[getSizeIndex(context)];
    }

    public static void setSizeIndex(Context context, int index) {
        Settings.System.putInt(context.getContentResolver(), SIZE_KEY, index);
    }

    public static boolean isSizeChanged(Context context, int index) {
        return getSizeIndex(context) != index;
    }
}
