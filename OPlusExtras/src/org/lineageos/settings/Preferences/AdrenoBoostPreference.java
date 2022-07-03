/*
 * Copyright (C) 2016 The OmniROM Project
 *               2022 The Evolution X Project
 * SPDX-License-Identifier: GPL-2.0-or-later
 */

package org.lineageos.settings.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.preference.PreferenceManager;
import androidx.preference.PreferenceViewHolder;
import android.util.AttributeSet;

import org.lineageos.settings.OPlusExtras;
import org.lineageos.settings.FileUtils;
import org.lineageos.settings.R;

public class AdrenoBoostPreference extends CustomSeekBarPreference {

    private static int mDefVal;

    private static final int NODE = R.string.node_adrenoboost_preference;

    public AdrenoBoostPreference(Context context, AttributeSet attrs) {
        super(context, attrs);

        mInterval = context.getResources().getInteger(R.integer.adrenoboost_preference_interval);
        mShowSign = false;
        mUnits = "";
        mContinuousUpdates = false;

        int[] mAllValues = context.getResources().getIntArray(R.array.adrenoboost_preference_array);
        mMinValue = mAllValues[1];
        mMaxValue = mAllValues[2];
        mDefaultValueExists = true;
        mDefVal = mAllValues[0];
        mDefaultValue = mDefVal;
        mValue = Integer.parseInt(loadValue(context));

        setPersistent(false);
    }

    private static String getFile(Context context) {
        String file = context.getString(NODE);
        if (FileUtils.fileWritable(file)) {
            return file;
        }
        return null;
    }

    public static boolean isSupported(Context context) {
        return FileUtils.fileWritable(getFile(context));
    }

    public static void restore(Context context) {
        if (!isSupported(context)) {
            return;
        }

        int[] mAllValues = context.getResources().getIntArray(R.array.adrenoboost_preference_array);
        mDefVal = mAllValues[0];
        String storedValue = PreferenceManager.getDefaultSharedPreferences(context).getString(OPlusExtras.KEY_ADRENOBOOST, String.valueOf(mDefVal));
        FileUtils.writeValue(getFile(context), storedValue);
    }

    public static String loadValue(Context context) {
        return FileUtils.getFileValue(getFile(context), String.valueOf(mDefVal));
    }

    private void saveValue(String newValue) {
        FileUtils.writeValue(getFile(getContext()), newValue);
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getContext()).edit();
        editor.putString(OPlusExtras.KEY_ADRENOBOOST, newValue);
        editor.apply();
    }

    @Override
    protected void changeValue(int newValue) {
        saveValue(String.valueOf(newValue));
    }
}
