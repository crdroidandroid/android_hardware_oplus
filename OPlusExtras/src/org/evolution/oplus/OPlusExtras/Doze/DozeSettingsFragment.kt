/*
 * Copyright (C) 2021 The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package org.evolution.oplus.OPlusExtras.doze;

import android.app.Activity
import android.app.AlertDialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Switch
import androidx.preference.*

import com.android.settingslib.widget.MainSwitchPreference
import com.android.settingslib.widget.OnMainSwitchChangeListener

import org.evolution.oplus.OPlusExtras.R;

class DozeSettingsFragment : PreferenceFragment(), Preference.OnPreferenceChangeListener,
    OnMainSwitchChangeListener {
    private lateinit var alwaysOnDisplayPreference: SwitchPreference
    private lateinit var switchBar: MainSwitchPreference

    private var pickUpPreference: ListPreference? = null
    private var pocketPreference: SwitchPreference? = null

    private val handler = Handler(Looper.getMainLooper())

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.doze_settings)

        val prefs = activity.getSharedPreferences("doze_settings", Activity.MODE_PRIVATE)!!
        if (savedInstanceState == null && !prefs.getBoolean("first_help_shown", false)) {
            AlertDialog.Builder(context)
                .setTitle(R.string.doze_settings_help_title)
                .setMessage(R.string.doze_settings_help_text)
                .setNegativeButton(R.string.dialog_ok) { _, _ ->
                    prefs.edit().putBoolean("first_help_shown", true).apply()
                }
                .show()
        }

        val dozeEnabled = DozeUtils.isDozeEnabled(context)
        switchBar = findPreference(DozeUtils.DOZE_ENABLE)!!
        switchBar.addOnSwitchChangeListener(this)
        switchBar.isChecked = dozeEnabled

        alwaysOnDisplayPreference = findPreference(DozeUtils.ALWAYS_ON_DISPLAY)!!
        alwaysOnDisplayPreference.isEnabled = dozeEnabled
        alwaysOnDisplayPreference.isChecked = DozeUtils.isAlwaysOnEnabled(context)
        alwaysOnDisplayPreference.onPreferenceChangeListener = this

        val pickupSensorCategory =
            preferenceScreen.findPreference<PreferenceCategory>(DozeUtils.CATEGORY_PICKUP_SENSOR)
        if (getString(R.string.pickup_sensor_type).isEmpty()) {
            preferenceScreen.removePreference(pickupSensorCategory)
        }

        val proximitySensorCategory =
            preferenceScreen.findPreference<PreferenceCategory>(DozeUtils.CATEGORY_PROXIMITY_SENSOR)
        if (getString(R.string.pocket_sensor_type).isEmpty()) {
            preferenceScreen.removePreference(proximitySensorCategory)
        }

        pickUpPreference = findPreference(DozeUtils.GESTURE_PICK_UP_KEY)
        pickUpPreference?.isEnabled = dozeEnabled
        pickUpPreference?.onPreferenceChangeListener = this

        pocketPreference = findPreference(DozeUtils.GESTURE_POCKET_KEY)
        pocketPreference?.isEnabled = dozeEnabled
        pocketPreference?.onPreferenceChangeListener = this

        // Hide AOD if not supported and set all its dependents otherwise
        if (!DozeUtils.alwaysOnDisplayAvailable(context)) {
            preferenceScreen.removePreference(alwaysOnDisplayPreference)
        } else {
            pickupSensorCategory?.dependency = DozeUtils.ALWAYS_ON_DISPLAY
            proximitySensorCategory?.dependency = DozeUtils.ALWAYS_ON_DISPLAY
        }
    }

    override fun onPreferenceChange(preference: Preference, newValue: Any?): Boolean {
        if (preference.key == DozeUtils.ALWAYS_ON_DISPLAY) {
            DozeUtils.enableAlwaysOn(context, newValue as Boolean)
        }
        handler.post { DozeUtils.checkDozeService(context) }
        return true
    }

    override fun onSwitchChanged(switchView: Switch, isChecked: Boolean) {
        DozeUtils.enableDoze(context, isChecked)
        DozeUtils.checkDozeService(context)

        switchBar.isChecked = isChecked

        if (!isChecked) {
            DozeUtils.enableAlwaysOn(context, false)
            alwaysOnDisplayPreference.isChecked = false
        }

        alwaysOnDisplayPreference.isEnabled = isChecked
        pickUpPreference?.isEnabled = isChecked
        pocketPreference?.isEnabled = isChecked
    }

}
