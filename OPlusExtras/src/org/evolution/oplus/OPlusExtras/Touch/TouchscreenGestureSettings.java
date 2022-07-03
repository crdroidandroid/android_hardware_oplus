/*
 * Copyright (C) 2016 The CyanogenMod project
 *               2017-2020 The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package org.evolution.oplus.OPlusExtras.touch;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.UserHandle;
import android.view.MenuItem;

import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragment;
import androidx.preference.PreferenceManager;

import com.android.internal.lineage.hardware.LineageHardwareManager;
import com.android.internal.lineage.hardware.TouchscreenGesture;

import com.android.settingslib.collapsingtoolbar.CollapsingToolbarBaseActivity;

import org.evolution.oplus.OPlusExtras.R;

import java.lang.System;

public class TouchscreenGestureSettings extends CollapsingToolbarBaseActivity
        implements PreferenceFragment.OnPreferenceStartFragmentCallback {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .replace(com.android.settingslib.widget.R.id.content_frame, getNewFragment())
                    .commit();
        }
    }

    private PreferenceFragment getNewFragment() {
        return new MainSettingsFragment();
    }

    @Override
    public boolean onPreferenceStartFragment(PreferenceFragment preferenceFragment,
            Preference preference) {
        Fragment instantiate = Fragment.instantiate(this, preference.getFragment(),
            preference.getExtras());
        getFragmentManager().beginTransaction().replace(
                com.android.settingslib.widget.R.id.content_frame, instantiate).addToBackStack(preference.getKey()).commit();

        return true;
    }

    public static class MainSettingsFragment extends PreferenceFragment {

        private static final String KEY_TOUCHSCREEN_GESTURE = "touchscreen_gesture";
        private static final String TOUCHSCREEN_GESTURE_TITLE = KEY_TOUCHSCREEN_GESTURE + "_%s_title";

        private TouchscreenGesture[] mTouchscreenGestures;

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {

            setPreferencesFromResource(R.xml.touchscreen_gesture_settings, rootKey);

            if (isTouchscreenGesturesSupported(getContext())) {
                initTouchscreenGestures();
            }
        }

        private void initTouchscreenGestures() {
            final LineageHardwareManager manager = LineageHardwareManager.getInstance(getContext());
            mTouchscreenGestures = manager.getTouchscreenGestures();
            final int[] actions = getDefaultGestureActions(getContext(), mTouchscreenGestures);
            for (final TouchscreenGesture gesture : mTouchscreenGestures) {
                getPreferenceScreen().addPreference(new TouchscreenGesturePreference(
                        getContext(), gesture, actions[gesture.id]));
            }
        }

        private class TouchscreenGesturePreference extends ListPreference {
            private final Context mContext;
            private final TouchscreenGesture mGesture;

            public TouchscreenGesturePreference(final Context context,
                                                final TouchscreenGesture gesture,
                                                final int defaultAction) {
                super(context);
                mContext = context;
                mGesture = gesture;

                setKey(buildPreferenceKey(gesture));
                setEntries(R.array.touchscreen_gesture_action_entries);
                setEntryValues(R.array.touchscreen_gesture_action_values);
                setDefaultValue(String.valueOf(defaultAction));

                setSummary("%s");
                setDialogTitle(R.string.touchscreen_gesture_action_dialog_title);
                setTitle(ResourceUtils.getLocalizedString(
                        context.getResources(), gesture.name, TOUCHSCREEN_GESTURE_TITLE));
            }

            @Override
            public boolean callChangeListener(final Object newValue) {
                final int action = Integer.parseInt(String.valueOf(newValue));
                final LineageHardwareManager manager = LineageHardwareManager.getInstance(mContext);
                if (!manager.setTouchscreenGestureEnabled(mGesture, action > 0)) {
                    return false;
                }
                return super.callChangeListener(newValue);
            }

            @Override
            protected boolean persistString(String value) {
                if (!super.persistString(value)) {
                    return false;
                }
                final int action = Integer.parseInt(String.valueOf(value));
                sendUpdateBroadcast(mContext, mTouchscreenGestures);
                return true;
            }
        }

        public static void restoreTouchscreenGestureStates(final Context context) {
            if (!isTouchscreenGesturesSupported(context)) {
                return;
            }

            final LineageHardwareManager manager = LineageHardwareManager.getInstance(context);
            final TouchscreenGesture[] gestures = manager.getTouchscreenGestures();
            final int[] actionList = buildActionList(context, gestures);
            for (final TouchscreenGesture gesture : gestures) {
                manager.setTouchscreenGestureEnabled(gesture, actionList[gesture.id] > 0);
            }

            sendUpdateBroadcast(context, gestures);
        }

        private static boolean isTouchscreenGesturesSupported(final Context context) {
            final LineageHardwareManager manager = LineageHardwareManager.getInstance(context);
            return manager.isSupported(LineageHardwareManager.FEATURE_TOUCHSCREEN_GESTURES);
        }

        private static int[] getDefaultGestureActions(final Context context,
                final TouchscreenGesture[] gestures) {
            final int[] defaultActions = context.getResources().getIntArray(
                    R.array.config_defaultTouchscreenGestureActions);
            if (defaultActions.length >= gestures.length) {
                return defaultActions;
            }

            final int[] filledDefaultActions = new int[gestures.length];
            System.arraycopy(defaultActions, 0, filledDefaultActions, 0, defaultActions.length);
            return filledDefaultActions;
        }

        private static int[] buildActionList(final Context context,
                final TouchscreenGesture[] gestures) {
            final int[] result = new int[gestures.length];
            final int[] defaultActions = getDefaultGestureActions(context, gestures);
            final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            for (final TouchscreenGesture gesture : gestures) {
                final String key = buildPreferenceKey(gesture);
                final String defaultValue = String.valueOf(defaultActions[gesture.id]);
                result[gesture.id] = Integer.parseInt(prefs.getString(key, defaultValue));
            }
            return result;
        }

        private static String buildPreferenceKey(final TouchscreenGesture gesture) {
            return "touchscreen_gesture_" + gesture.id;
        }

        private static void sendUpdateBroadcast(final Context context,
                final TouchscreenGesture[] gestures) {
            final Intent intent = new Intent(TouchscreenGestureConstants.UPDATE_PREFS_ACTION);
            final int[] keycodes = new int[gestures.length];
            final int[] actions = buildActionList(context, gestures);
            for (final TouchscreenGesture gesture : gestures) {
                keycodes[gesture.id] = gesture.keycode;
            }
            intent.putExtra(TouchscreenGestureConstants.UPDATE_EXTRA_KEYCODE_MAPPING, keycodes);
            intent.putExtra(TouchscreenGestureConstants.UPDATE_EXTRA_ACTION_MAPPING, actions);
            intent.setFlags(Intent.FLAG_RECEIVER_REGISTERED_ONLY);
            context.sendBroadcastAsUser(intent, UserHandle.CURRENT);
        }

        @Override
        public void setDivider(Drawable divider) {
            super.setDivider(new ColorDrawable(Color.TRANSPARENT));
        }

        @Override
        public void setDividerHeight(int height) {
            super.setDividerHeight(0);
        }

        @Override
        public void onSaveInstanceState(Bundle outState) {
            super.onSaveInstanceState(outState);
        }

        @Override
        public void onResume() {
            super.onResume();
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
        }
    }
}
