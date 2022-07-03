/*
 * Copyright (C) 2016 The OmniROM Project
 * SPDX-License-Identifier: GPL-2.0-or-later
 */

package org.evolution.oplus.OPlusExtras.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.AttributeSet;
import androidx.preference.PreferenceManager;

import org.evolution.oplus.OPlusExtras.OPlusExtras;
import org.evolution.oplus.OPlusExtras.FileUtils;
import org.evolution.oplus.OPlusExtras.R;

public class AutoHBMThresholdPreference extends CustomSeekBarPreference {

    public AutoHBMThresholdPreference(Context context, AttributeSet attrs) {
        super(context, attrs);

        mInterval = context.getResources().getInteger(R.integer.auto_hbm_threshold_preference_interval);
        mShowSign = false;
        mUnits = "";
        mContinuousUpdates = false;
        int[] mAllValues = context.getResources().getIntArray(R.array.auto_hbm_threshold_preference_array);
        mMinValue = mAllValues[1];
        mMaxValue = mAllValues[2];
        mDefaultValueExists = true;
        mDefaultValue = mAllValues[0];
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        mValue = Integer.parseInt(sharedPrefs.getString(OPlusExtras.KEY_AUTO_HBM_THRESHOLD, context.getString(R.string.auto_hbm_threshold_preference_default)));

        setPersistent(false);
    }

    @Override
    protected void changeValue(int newValue) {
        SharedPreferences.Editor prefChange = PreferenceManager.getDefaultSharedPreferences(getContext()).edit();
        prefChange.putString(OPlusExtras.KEY_AUTO_HBM_THRESHOLD, String.valueOf(newValue)).commit();
    }
}
