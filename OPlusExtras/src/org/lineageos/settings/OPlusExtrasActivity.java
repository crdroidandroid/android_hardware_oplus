/*
 * Copyright (C) 2017 The OmniROM Project
 *               2022 The Evolution X Project
 * SPDX-License-Identifier: GPL-2.0-or-later
 */

package org.lineageos.settings;

import android.app.Fragment;
import android.os.Bundle;
import androidx.preference.PreferenceFragment;
import androidx.preference.PreferenceManager;

import com.android.settingslib.collapsingtoolbar.CollapsingToolbarBaseActivity;
import com.android.settingslib.widget.R;

public class OPlusExtrasActivity extends CollapsingToolbarBaseActivity {

    private OPlusExtras mOPlusExtrasFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Fragment fragment = getFragmentManager().findFragmentById(R.id.content_frame);
        if (fragment == null) {
            mOPlusExtrasFragment = new OPlusExtras();
            getFragmentManager().beginTransaction()
                .add(R.id.content_frame, mOPlusExtrasFragment)
                .commit();
        } else {
            mOPlusExtrasFragment = (OPlusExtras) fragment;
        }
    }
}
