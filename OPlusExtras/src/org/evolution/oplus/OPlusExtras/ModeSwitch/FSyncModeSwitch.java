/*
 * Copyright (C) 2016 The OmniROM Project
 *               2022 The Evolution X Project
 * SPDX-License-Identifier: GPL-2.0-or-later
 */

package org.evolution.oplus.OPlusExtras.modeswitch;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.preference.Preference;
import androidx.preference.Preference.OnPreferenceChangeListener;

import org.evolution.oplus.OPlusExtras.FileUtils;
import org.evolution.oplus.OPlusExtras.R;

public class FSyncModeSwitch implements OnPreferenceChangeListener {

    private static final int NODE = R.string.node_fsync_mode_switch;

    public static String getFile(Context context) {
        String file = context.getResources().getString(NODE);
        if (FileUtils.fileWritable(file)) {
            return file;
        }
        return null;
    }

    public static boolean isSupported(Context context) {
        return FileUtils.fileWritable(getFile(context));
    }

    public static boolean isCurrentlyEnabled(Context context) {
        return FileUtils.getFileValueAsBoolean(getFile(context), false,
            context.getResources().getString(R.string.node_fsync_mode_switch_true),
            context.getResources().getString(R.string.node_fsync_mode_switch_false));
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        Boolean enabled = (Boolean) newValue;
        FileUtils.writeValue(getFile(preference.getContext()), enabled ? "1" : "0");
        return true;
    }
}
