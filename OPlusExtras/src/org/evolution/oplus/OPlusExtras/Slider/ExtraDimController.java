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

public final class ExtraDimController extends SliderControllerBase {

    public static final int ID = 7;

    private static final String TAG = "ExtraDimController";

    private static final int EXTRADIM_OFF = 69;
    private static final int EXTRADIM_MIN = 70;
    private static final int EXTRADIM_MID = 71;
    private static final int EXTRADIM_MAX = 72;


    public ExtraDimController(Context context) {
        super(context);
    }

    @Override
    protected int processAction(int action) {
        Log.d(TAG, "slider action: " + action);
        switch (action) {
            case EXTRADIM_OFF:
                 writeSettings(Settings.Secure.REDUCE_BRIGHT_COLORS_ACTIVATED, 0);
                 writeSettings(Settings.Secure.REDUCE_BRIGHT_COLORS_LEVEL, 40);
                 return SliderConstants.MODE_EXTRADIM_OFF;
            case EXTRADIM_MIN:
                 writeSettings(Settings.Secure.REDUCE_BRIGHT_COLORS_ACTIVATED, 1);
                 writeSettings(Settings.Secure.REDUCE_BRIGHT_COLORS_LEVEL, 25);
                 return SliderConstants.MODE_EXTRADIM_MIN;
            case EXTRADIM_MID:
                 writeSettings(Settings.Secure.REDUCE_BRIGHT_COLORS_ACTIVATED, 1);
                 writeSettings(Settings.Secure.REDUCE_BRIGHT_COLORS_LEVEL, 50);
                 return SliderConstants.MODE_EXTRADIM_MID;
            case EXTRADIM_MAX:
                 writeSettings(Settings.Secure.REDUCE_BRIGHT_COLORS_ACTIVATED, 1);
                 writeSettings(Settings.Secure.REDUCE_BRIGHT_COLORS_LEVEL, 75);
                 return SliderConstants.MODE_EXTRADIM_MAX;
            default:
                return 0;
       }
    }

    @Override
    public void reset() {
         writeSettings(Settings.Secure.REDUCE_BRIGHT_COLORS_ACTIVATED, 0);
         writeSettings(Settings.Secure.REDUCE_BRIGHT_COLORS_LEVEL, 40);
    }

    private boolean writeSettings(String key, int value) {
        return Settings.Secure.putIntForUser(mContext.getContentResolver(),
                    key, value, UserHandle.USER_CURRENT);
    }
}
