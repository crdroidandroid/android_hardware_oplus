/*
 * Copyright (C) 2018-2022 crDroid Android Project
 * SPDX-License-Identifier: Apache-2.0
 */

package org.lineageos.settings.slider;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.UserHandle;
import android.provider.Settings;
import androidx.preference.SwitchPreference;
import androidx.preference.PreferenceManager;

import android.media.AudioManager;

public class SliderConstants {

    public static final String SLIDER_STATE
            = "/proc/tristatekey/tri_state";

    public static final String NOTIF_SLIDER_PANEL_KEY = "notification_slider";
    public static final String NOTIF_SLIDER_USAGE_KEY = "slider_usage";
    public static final String NOTIF_SLIDER_ACTION_TOP_KEY = "action_top_position";
    public static final String NOTIF_SLIDER_ACTION_MIDDLE_KEY = "action_middle_position";
    public static final String NOTIF_SLIDER_ACTION_BOTTOM_KEY = "action_bottom_position";

    public static final String EXTRA_SLIDER_USAGE = "usage";
    public static final String EXTRA_SLIDER_ACTIONS = "actions";

    public static final String NOTIF_SLIDER_FOR_NOTIFICATION = "1";
    public static final String NOTIF_SLIDER_FOR_FLASHLIGHT = "2";
    public static final String NOTIF_SLIDER_FOR_BRIGHTNESS = "3";
    public static final String NOTIF_SLIDER_FOR_ROTATION = "4";
    public static final String NOTIF_SLIDER_FOR_RINGER = "5";
    public static final String NOTIF_SLIDER_FOR_NOTIFICATION_RINGER = "6";
    public static final String NOTIF_SLIDER_FOR_EXTRADIM = "7";
    public static final String NOTIF_SLIDER_FOR_NIGHTLIGHT = "8";
    public static final String NOTIF_SLIDER_FOR_REFRESH = "9";
    public static final String NOTIF_SLIDER_FOR_COLORSPACE = "10";

    public static final String ACTION_UPDATE_SLIDER_POSITION = "org.lineageos.settings.slider.UPDATE_SLIDER_POSITION";
    public static final String ACTION_UPDATE_SLIDER_SETTINGS = "org.lineageos.settings.slider.UPDATE_SLIDER_SETTINGS";
    public static final String EXTRA_SLIDER_POSITION = "position";
    public static final String EXTRA_SLIDER_POSITION_VALUE = "position_value";

    public static final int MODE_TOTAL_SILENCE = 600;
    public static final int MODE_ALARMS_ONLY = 601;
    public static final int MODE_PRIORITY_ONLY = 602;
    public static final int MODE_NONE = 603;
    public static final int MODE_VIBRATE = 604;
    public static final int MODE_RING = 605;
    public static final int MODE_SILENT = 620;
    public static final int MODE_FLASHLIGHT_ON = 621;
    public static final int MODE_FLASHLIGHT_OFF = 622;
    public static final int MODE_FLASHLIGHT_BLINK = 623;
    public static final int MODE_BRIGHTNESS_BRIGHT = 630;
    public static final int MODE_BRIGHTNESS_DARK = 631;
    public static final int MODE_BRIGHTNESS_AUTO = 632;
    public static final int MODE_ROTATION_AUTO = 640;
    public static final int MODE_ROTATION_0 = 641;
    public static final int MODE_ROTATION_90 = 642;
    public static final int MODE_ROTATION_270 = 643;
    public static final int MODE_EXTRADIM_OFF = 647;
    public static final int MODE_EXTRADIM_MIN = 648;
    public static final int MODE_EXTRADIM_MID = 649;
    public static final int MODE_EXTRADIM_MAX = 650;
    public static final int MODE_NIGHTLIGHT_OFF = 651;
    public static final int MODE_NIGHTLIGHT_MIN = 652;
    public static final int MODE_NIGHTLIGHT_MID = 653;
    public static final int MODE_NIGHTLIGHT_MAX = 654;
    public static final int MODE_REFRESH_AUTO = 644;
    public static final int MODE_REFRESH_MIN = 645;
    public static final int MODE_REFRESH_MAX = 646;
    public static final int MODE_COLORSPACE_VIVID = 655;
    public static final int MODE_COLORSPACE_NATURAL = 656;
    public static final int MODE_COLORSPACE_CINEMATIC = 657;
    public static final int MODE_COLORSPACE_BRILLIANT = 658;

    // Holds <preference_key> -> <proc_node> mapping
    public static final Map<String, String> sBooleanNodePreferenceMap = new HashMap<>();
    public static final Map<String, String> sStringNodePreferenceMap = new HashMap<>();
}
