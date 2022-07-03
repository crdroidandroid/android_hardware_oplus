/*
 * Copyright (C) 2018-2022 crDroid Android Project
 *               2022 The Evolution X Project
 * SPDX-License-Identifier: Apache-2.0
 */

package org.evolution.oplus.OPlusExtras.slider;

import android.content.Context;
import android.os.UserHandle;
import android.provider.Settings;
import android.util.Log;

public final class NightLightController extends SliderControllerBase {

    public static final int ID = 8;

    private static final String TAG = "NightLightController";

    private static final int NIGHTLIGHT_OFF = 73;
    private static final int NIGHTLIGHT_MIN = 74;
    private static final int NIGHTLIGHT_MID = 75;
    private static final int NIGHTLIGHT_MAX = 76;

    public NightLightController(Context context) {
        super(context);
    }

    @Override
    protected int processAction(int action) {
        Log.d(TAG, "slider action: " + action);
        switch (action) {
            case NIGHTLIGHT_OFF:
                 writeSettings(Settings.Secure.NIGHT_DISPLAY_ACTIVATED, 0);
                 writeSettings(Settings.Secure.NIGHT_DISPLAY_COLOR_TEMPERATURE, 4082);
                 return SliderConstants.MODE_NIGHTLIGHT_OFF;
            case NIGHTLIGHT_MIN:
                 writeSettings(Settings.Secure.NIGHT_DISPLAY_ACTIVATED, 1);
                 writeSettings(Settings.Secure.NIGHT_DISPLAY_COLOR_TEMPERATURE, 4082);
                 return SliderConstants.MODE_NIGHTLIGHT_MIN;
            case NIGHTLIGHT_MID:
                 writeSettings(Settings.Secure.NIGHT_DISPLAY_ACTIVATED, 1);
                 writeSettings(Settings.Secure.NIGHT_DISPLAY_COLOR_TEMPERATURE, 3358);
                 return SliderConstants.MODE_NIGHTLIGHT_MID;
            case NIGHTLIGHT_MAX:
                 writeSettings(Settings.Secure.NIGHT_DISPLAY_ACTIVATED, 1);
                 writeSettings(Settings.Secure.NIGHT_DISPLAY_COLOR_TEMPERATURE, 2956);
                 return SliderConstants.MODE_NIGHTLIGHT_MAX;
            default:
                return 0;
       }
    }

    @Override
    public void reset() {
         writeSettings(Settings.Secure.NIGHT_DISPLAY_ACTIVATED, 0);
         writeSettings(Settings.Secure.NIGHT_DISPLAY_COLOR_TEMPERATURE, 4082);
    }

    private boolean writeSettings(String key, int value) {
        return Settings.Secure.putIntForUser(mContext.getContentResolver(),
                    key, value, UserHandle.USER_CURRENT);
    }
}
