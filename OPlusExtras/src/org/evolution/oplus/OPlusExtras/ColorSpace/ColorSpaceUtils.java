/*
 * Copyright (C) 2020 The LineageOS Project
 *               2022 The Evolution X Project
 * SPDX-License-Identifier: Apache-2.0
 */

package org.evolution.oplus.OPlusExtras.colorspace;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.UserHandle;
import android.view.Display;

import android.provider.Settings;
import androidx.preference.PreferenceManager;

import org.evolution.oplus.OPlusExtras.OPlusExtras;
import org.evolution.oplus.OPlusExtras.R;

public final class ColorSpaceUtils {

    private static final String COLORSPACE_CONTROL = "colorspace_control";
    private static int defaultColorSpace;
    private static final String DISPLAY_COLOR_MODE = "display_color_mode";
    private Context mContext;
    protected static boolean isAppInList = false;

    protected static final int STATE_DEFAULT = 0;
    protected static final int STATE_VIVID= 1;
    protected static final int STATE_NATURAL = 2;
    protected static final int STATE_CINEMATIC = 3;
    protected static final int STATE_BRILLIANT = 4;

    private static final int COLORSPACE_STATE_DEFAULT = 0;
    private static final int COLORSPACE_STATE_VIVID = 0;
    private static final int COLORSPACE_STATE_NATURAL = 256;
    private static final int COLORSPACE_STATE_CINEMATIC = 258;
    private static final int COLORSPACE_STATE_BRILLIANT= 259;

    private static final String COLORSPACE_VIVID = "colorspace.vivid=";
    private static final String COLORSPACE_NATURAL = "colorspace.natural=";
    private static final String COLORSPACE_CINEMATIC = "colorspace.cinematic=";
    private static final String COLORSPACE_BRILLIANT = "colorspace.brilliant=";

    private SharedPreferences mSharedPrefs;

    protected ColorSpaceUtils(Context context) {
        mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        mContext = context;
    }

    public static void startService(Context context) {
       if (OPlusExtras.isFeatureSupported(context, R.bool.config_deviceSupportsPerAppColorSpace)) {
        context.startServiceAsUser(new Intent(context, ColorSpaceService.class),
                UserHandle.CURRENT);
        }
    }

    private void writeValue(String profiles) {
        mSharedPrefs.edit().putString(COLORSPACE_CONTROL, profiles).apply();
    }

    private String getValue() {
        String value = mSharedPrefs.getString(COLORSPACE_CONTROL, null);

        if (value == null || value.isEmpty()) {
            value = COLORSPACE_VIVID + ":" + COLORSPACE_NATURAL + ":" +  COLORSPACE_CINEMATIC + ":" + COLORSPACE_BRILLIANT;
            writeValue(value);
        }
        return value;
    }

    protected void writePackage(String packageName, int mode) {
        String value = getValue();
        value = value.replace(packageName + ",", "");
        String[] modes = value.split(":");
        String finalString;

        switch (mode) {
            case STATE_VIVID:
                modes[0] = modes[0] + packageName + ",";
                break;
            case STATE_NATURAL:
                modes[1] = modes[1] + packageName + ",";
                break;
            case STATE_CINEMATIC:
                modes[2] = modes[2] + packageName + ",";
                break;
            case STATE_BRILLIANT:
                modes[3] = modes[3] + packageName + ",";
                break;
        }

        finalString = modes[0] + ":" + modes[1]  + ":" + modes[2] + ":" + modes[3];

        writeValue(finalString);
    }

    protected int getStateForPackage(String packageName) {
        String value = getValue();
        String[] modes = value.split(":");
        int state = STATE_DEFAULT;
        if (modes[0].contains(packageName + ",")) {
            state = STATE_VIVID;
        } else if (modes[1].contains(packageName + ",")) {
            state = STATE_NATURAL;
        } else if (modes[2].contains(packageName + ",")) {
            state = STATE_CINEMATIC;
        } else if (modes[3].contains(packageName + ",")) {
            state = STATE_BRILLIANT;
        }
        return state;
    }

    protected void setColorSpace(String packageName) {
        String value = getValue();
        String modes[];
        int colorspace = defaultColorSpace;
        isAppInList = false;
            if (value != null) {
            modes = value.split(":");
            if (modes[0].contains(packageName + ",")) {
                colorspace = COLORSPACE_STATE_VIVID;
		isAppInList = true;
           } else if (modes[1].contains(packageName + ",")) {
                colorspace = COLORSPACE_STATE_NATURAL;
		isAppInList = true;
           } else if (modes[2].contains(packageName + ",")) {
                colorspace = COLORSPACE_STATE_CINEMATIC;
                isAppInList = true;
           } else if (modes[3].contains(packageName + ",")) {
                colorspace = COLORSPACE_STATE_BRILLIANT;
                isAppInList = true;
           }
          }

	Settings.System.putInt(mContext.getContentResolver(), DISPLAY_COLOR_MODE, colorspace);
    }
}
