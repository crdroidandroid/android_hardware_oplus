/*
 * Copyright (C) 2020 The LineageOS Project
 *               2024 The crDroid Project
 * SPDX-License-Identifier: Apache-2.0
 */

package org.lineageos.settings.refreshrate;

import android.os.Bundle;
import android.view.MenuItem;

import com.android.settingslib.collapsingtoolbar.CollapsingToolbarBaseActivity;

public class RefreshActivity extends CollapsingToolbarBaseActivity {
    private static final String TAG_REFRESH = "refresh";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getFragmentManager().beginTransaction().replace(
                    com.android.settingslib.collapsingtoolbar.R.id.content_frame,
                new RefreshSettingsFragment(), TAG_REFRESH).commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return false;
    }
}
