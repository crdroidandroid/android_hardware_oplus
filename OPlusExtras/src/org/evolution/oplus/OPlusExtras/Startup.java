/*
 * Copyright (C) 2016 The OmniROM Project
 *               2022 The Evolution X Project
 * SPDX-License-Identifier: GPL-2.0-or-later
 */

package org.evolution.oplus.OPlusExtras;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.provider.Settings;
import androidx.preference.PreferenceManager;

import org.evolution.oplus.OPlusExtras.colorspace.ColorSpaceUtils;
import org.evolution.oplus.OPlusExtras.doze.DozeUtils;
import org.evolution.oplus.OPlusExtras.FileUtils;
import org.evolution.oplus.OPlusExtras.modeswitch.*;
import org.evolution.oplus.OPlusExtras.preferences.*;
import org.evolution.oplus.OPlusExtras.refreshrate.RefreshUtils;
import org.evolution.oplus.OPlusExtras.services.FPSInfoService;
import org.evolution.oplus.OPlusExtras.touch.TouchscreenGestureSettings;

public class Startup extends BroadcastReceiver {

    private static final String TAG = "BootReceiver";

    @Override
    public void onReceive(final Context context, final Intent bootintent) {

        AdrenoBoostPreference.restore(context);
        BluePreference.restore(context);
        ContrastPreference.restore(context);
        ColorSpaceUtils.startService(context);
        DozeUtils.checkDozeService(context);
        GreenPreference.restore(context);
        HuePreference.restore(context);
        MaxBrightnessPreference.restore(context);
        OPlusExtras.restoreSliderStates(context);
        RedPreference.restore(context);
        RefreshUtils.startService(context);
        SaturationPreference.restore(context);
        TouchscreenGestureSettings.MainSettingsFragment.restoreTouchscreenGestureStates(context);
        ValuePreference.restore(context);
        VibratorStrengthPreference.restore(context);

        boolean enabled = false;
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        enabled = sharedPrefs.getBoolean(OPlusExtras.KEY_DC_SWITCH, false);
        if (enabled) {
        restore(DCModeSwitch.getFile(context), enabled);
               }
        enabled = sharedPrefs.getBoolean(OPlusExtras.KEY_HBM_SWITCH, false);
        if (enabled) {
        restore(HBMModeSwitch.getFile(context), enabled);
               }
        enabled = sharedPrefs.getBoolean(OPlusExtras.KEY_FPS_INFO, false);
        if (enabled) {
            context.startService(new Intent(context, FPSInfoService.class));
               }
        enabled = sharedPrefs.getBoolean(OPlusExtras.KEY_FSYNC_SWITCH, false);
        if (enabled) {
        restore(FSyncModeSwitch.getFile(context), enabled);
               }
        enabled = sharedPrefs.getBoolean(OPlusExtras.KEY_POWERSHARE_SWITCH, false);
        if (enabled) {
            restore(PowerShareModeSwitch.getFile(context), enabled);
               }
        enabled = sharedPrefs.getBoolean(OPlusExtras.KEY_QUIET_MODE_SWITCH, false);
        if (enabled) {
            restore(QuietModeSwitch.getFile(context), enabled);
               }
        enabled = sharedPrefs.getBoolean(OPlusExtras.KEY_GAME_SWITCH, false);
        if (enabled) {
            restore(GameModeSwitch.getFile(context), enabled);
               }
        enabled = sharedPrefs.getBoolean(OPlusExtras.KEY_TP_EDGE_LIMIT_SWITCH, false);
        if (enabled) {
            restore(TPEdgeLimitModeSwitch.getFile(context), enabled);
               }
        enabled = sharedPrefs.getBoolean(OPlusExtras.KEY_POWER_EFFICIENT_WQ_SWITCH, false);
        if (enabled) {
            restore(PowerEfficientWorkqueueModeSwitch.getFile(context), enabled);
               }
        enabled = sharedPrefs.getBoolean(OPlusExtras.KEY_TOUCHBOOST_SWITCH, false);
        if (enabled) {
            restore(TouchBoostModeSwitch.getFile(context), enabled);
               }
        enabled = sharedPrefs.getBoolean(OPlusExtras.KEY_OTG_SWITCH, false);
        if (enabled) {
            restore(OTGModeSwitch.getFile(context), enabled);
               }
        enabled = sharedPrefs.getBoolean(OPlusExtras.KEY_USB2_SWITCH, false);
        if (enabled) {
        restore(USB2FastChargeModeSwitch.getFile(context), enabled);
        }
    }

    private void restore(String file, boolean enabled) {
        if (file == null) {
            return;
        }
        if (enabled) {
            FileUtils.writeValue(file, "1");
        }
    }

    private void restore(String file, String value) {
        if (file == null) {
            return;
        }
        FileUtils.writeValue(file, value);
    }
}
