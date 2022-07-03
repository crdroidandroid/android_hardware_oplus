/*
 * Copyright (C) 2018-2022 crDroid Android Project
 * SPDX-License-Identifier: Apache-2.0
 */

package org.evolution.oplus.OPlusExtras.slider;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.input.InputManager;
import android.os.FileObserver;
import android.os.RemoteException;
import android.os.UserHandle;
import android.os.Vibrator;
import android.provider.Settings;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.KeyEvent;

import androidx.annotation.Keep;

import com.android.internal.os.DeviceKeyHandler;

import java.util.Arrays;

@Keep
public class KeyHandler implements DeviceKeyHandler {
    private static final String TAG = KeyHandler.class.getSimpleName();
    private static final boolean DEBUG = false;

    private final Context mContext;
    private final NotificationController mNotificationController;
    private final FlashlightController mFlashlightController;
    private final BrightnessController mBrightnessController;
    private final RotationController mRotationController;
    private final RingerController mRingerController;
    private final NotificationRingerController mNotificationRingerController;
    private final RefreshController mRefreshController;
    private final ExtraDimController mExtraDimController;
    private final NightLightController mNightLightController;
    private final ColorSpaceController mColorSpaceController;

    private SliderControllerBase mSliderController;

    private final InputManager mInputManager;

    private final BroadcastReceiver mSliderUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int usage = intent.getIntExtra(SliderConstants.EXTRA_SLIDER_USAGE, 0);
            int[] actions = intent.getIntArrayExtra(SliderConstants.EXTRA_SLIDER_ACTIONS);

            Log.d(TAG, "update usage " + usage + " with actions " +
                    Arrays.toString(actions));

            if (mSliderController != null) {
                mSliderController.reset();
            }

            switch (usage) {
                case NotificationController.ID:
                    mSliderController = mNotificationController;
                    mSliderController.update(actions);
                    break;
                case FlashlightController.ID:
                    mSliderController = mFlashlightController;
                    mSliderController.update(actions);
                    break;
                case BrightnessController.ID:
                    mSliderController = mBrightnessController;
                    mSliderController.update(actions);
                    break;
                case RotationController.ID:
                    mSliderController = mRotationController;
                    mSliderController.update(actions);
                    break;
                case RingerController.ID:
                    mSliderController = mRingerController;
                    mSliderController.update(actions);
                    break;
                case NotificationRingerController.ID:
                    mSliderController = mNotificationRingerController;
                    mSliderController.update(actions);
                    break;
                case RefreshController.ID:
                    mSliderController = mRefreshController;
                    mSliderController.update(actions);
                    break;
                case ExtraDimController.ID:
                    mSliderController = mExtraDimController;
                    mSliderController.update(actions);
                    break;
                case NightLightController.ID:
                    mSliderController = mNightLightController;
                    mSliderController.update(actions);
                    break;
                case ColorSpaceController.ID:
                    mSliderController = mColorSpaceController;
                    mSliderController.update(actions);
                    break;
            }

            mSliderController.restoreState(context, false);
        }
    };

    public KeyHandler(Context context) {
        mContext = context;

        mNotificationController = new NotificationController(mContext);
        mFlashlightController = new FlashlightController(mContext);
        mBrightnessController = new BrightnessController(mContext);
        mRotationController = new RotationController(mContext);
        mRingerController = new RingerController(mContext);
        mNotificationRingerController = new NotificationRingerController(mContext);
        mRefreshController = new RefreshController(mContext);
        mExtraDimController = new ExtraDimController(mContext);
        mNightLightController = new NightLightController(mContext);
        mColorSpaceController = new ColorSpaceController(mContext);

        mContext.registerReceiver(mSliderUpdateReceiver,
                new IntentFilter(SliderConstants.ACTION_UPDATE_SLIDER_SETTINGS));

        mInputManager = mContext.getSystemService(InputManager.class);
    }

    public KeyEvent handleKeyEvent(KeyEvent event) {
        if (event.getAction() != KeyEvent.ACTION_DOWN) {
            return event;
        }

        String deviceName = mInputManager.getInputDevice(event.getDeviceId()).getName();

        if (!deviceName.equals("oplus,hall_tri_state_key") && !deviceName.equals("oplus,tri-state-key")) {
            return event;
        }

        mSliderController.processEvent(mContext);

        return null;
    }
}
