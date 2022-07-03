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

public final class ColorSpaceController extends SliderControllerBase {

    public static final int ID = 10;

    private static final String TAG = "ColorSpaceController";

    private static final int COLORSPACE_VIVID = 77;
    private static final int COLORSPACE_NATURAL = 78;
    private static final int COLORSPACE_CINEMATIC = 79;
    private static final int COLORSPACE_BRILLIANT = 80;

    public ColorSpaceController(Context context) {
        super(context);
    }

    @Override
    protected int processAction(int action) {
        Log.d(TAG, "slider action: " + action);
        switch (action) {
            case COLORSPACE_VIVID:
                 writeSettings(Settings.System.DISPLAY_COLOR_MODE, 0);
                 return SliderConstants.MODE_COLORSPACE_VIVID;
            case COLORSPACE_NATURAL:
                 writeSettings(Settings.System.DISPLAY_COLOR_MODE, 256);
                 return SliderConstants.MODE_COLORSPACE_NATURAL;
            case COLORSPACE_CINEMATIC:
                 writeSettings(Settings.System.DISPLAY_COLOR_MODE, 258);
                 return SliderConstants.MODE_COLORSPACE_CINEMATIC;
            case COLORSPACE_BRILLIANT:
                 writeSettings(Settings.System.DISPLAY_COLOR_MODE, 259);
                 return SliderConstants.MODE_COLORSPACE_BRILLIANT;
            default:
                return 0;
       }
    }

    @Override
    public void reset() {
         writeSettings(Settings.System.DISPLAY_COLOR_MODE, 0);
    }

    private boolean writeSettings(String key, int value) {
        return Settings.System.putIntForUser(mContext.getContentResolver(),
                    key, value, UserHandle.USER_CURRENT);
    }
}
