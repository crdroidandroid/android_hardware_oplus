/*
 * Copyright (C) 2018-2022 crDroid Android Project
 * SPDX-License-Identifier: Apache-2.0
 */

package org.lineageos.settings.slider;

import android.content.Context;
import android.content.Intent;
import android.os.UserHandle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;

import org.lineageos.settings.FileUtils;

public abstract class SliderControllerBase {

    private static final String TAG = "SliderControllerBase";

    protected final Context mContext;

    private Vibrator mVibrator;

    private int[] mActions = null;

    public SliderControllerBase(Context context) {
        mContext = context;
        mVibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        if (mVibrator == null || !mVibrator.hasVibrator()) {
            mVibrator = null;
        }
    }

    public final void update(int[] actions) {
        if (actions != null && actions.length == 3) {
            mActions = actions;
        }
    }

    protected abstract int processAction(int action);

    public final int processEvent(Context context) {
        int result = restoreState(context, true);
        if (result > 0) {
            doHapticFeedback();
        }

        return result;
    }

    public static void sendUpdateBroadcast(Context context, int position, int result) {
        Intent intent = new Intent(SliderConstants.ACTION_UPDATE_SLIDER_POSITION);
        intent.putExtra(SliderConstants.EXTRA_SLIDER_POSITION, position);
        intent.putExtra(SliderConstants.EXTRA_SLIDER_POSITION_VALUE, result);
        context.sendBroadcastAsUser(intent, UserHandle.CURRENT);
        intent.setFlags(Intent.FLAG_RECEIVER_REGISTERED_ONLY);
        Log.d(TAG, "slider change to positon " + position);
    }

    public abstract void reset();

    public final int restoreState(Context context, boolean notify) {
        int ret = 0;
        if (mActions == null) {
            return ret;
        }

        try {
            int state = Integer.parseInt(FileUtils.readOneLine(SliderConstants.SLIDER_STATE).trim());
            ret = processAction(mActions[state - 1]);
            if (ret > 0 && notify) {
                sendUpdateBroadcast(context, state - 1, ret);
            }
        } catch (Exception e) {
            Log.e(TAG, "Failed to restore slider state", e);
        }
        return ret;
    }

    private void doHapticFeedback() {
        if (mVibrator != null && mVibrator.hasVibrator()) {
            mVibrator.vibrate(VibrationEffect.get(VibrationEffect.EFFECT_CLICK));
        }
    }
}
