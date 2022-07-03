/*
 * Copyright (C) 2018-2022 crDroid Android Project
 * SPDX-License-Identifier: Apache-2.0
 */

package org.evolution.oplus.OPlusExtras.slider;

import android.content.Context;
import android.os.UserHandle;
import android.provider.Settings;
import android.util.Log;

public final class BrightnessController extends SliderControllerBase {

    public static final int ID = 3;

    private static final String TAG = "BrightnessController";

    private static final int BRIGHTNESS_AUTO = 30;
    private static final int BRIGHTNESS_BRIGHTEST = 31;
    private static final int BRIGHTNESS_DARKEST = 32;

    private static final int DARKEST = 1;
    private static final int BRIGHTEST = 255;

    public BrightnessController(Context context) {
        super(context);
    }

    @Override
    protected int processAction(int action) {
        Log.d(TAG, "slider action: " + action);
        switch (action) {
            case BRIGHTNESS_AUTO:
                if (writeSettings(Settings.System.SCREEN_BRIGHTNESS_MODE,
                        Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC))
                    return SliderConstants.MODE_BRIGHTNESS_AUTO;
                break;
            case BRIGHTNESS_BRIGHTEST:
                if (writeSettings(Settings.System.SCREEN_BRIGHTNESS_MODE,
                        Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL) &&
                    writeSettings(Settings.System.SCREEN_BRIGHTNESS, BRIGHTEST))
                    return SliderConstants.MODE_BRIGHTNESS_BRIGHT;
                break;
            case BRIGHTNESS_DARKEST:
                if (writeSettings(Settings.System.SCREEN_BRIGHTNESS_MODE,
                        Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL) &&
                    writeSettings(Settings.System.SCREEN_BRIGHTNESS, DARKEST))
                    return SliderConstants.MODE_BRIGHTNESS_DARK;
                break;
            default:
                return 0;
        }
        return 0;
    }

    @Override
    public void reset() {
        writeSettings(Settings.System.SCREEN_BRIGHTNESS_MODE,
                Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC);
    }

    private boolean writeSettings(String key, int value) {
        return Settings.System.putIntForUser(mContext.getContentResolver(),
                    key, value, UserHandle.USER_CURRENT);
    }
}
