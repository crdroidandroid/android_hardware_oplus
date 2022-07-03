/*
 * Copyright (C) 2016 The OmniROM Project
 *               2022 The Evolution X Project
 * SPDX-License-Identifier: GPL-2.0-or-later
 */

package org.evolution.oplus.OPlusExtras.kcal;

import android.app.Fragment;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceFragment;
import androidx.preference.PreferenceManager;
import androidx.viewpager.widget.ViewPager;

import com.android.settingslib.collapsingtoolbar.CollapsingToolbarBaseActivity;
import com.android.settingslib.widget.R;

public class KcalSettingsActivity extends CollapsingToolbarBaseActivity {

    private ViewPager viewPager;
    private LinearLayout sliderDotspanel;
    private int dotscount;
    private ImageView[] dots;

    private KcalSettings mKcalSettingsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.kcal);

        setupImageSlider();

        Fragment fragment = getFragmentManager().findFragmentById(R.id.fragment_kcal);
        if (fragment == null) {
            mKcalSettingsFragment = new KcalSettings();
            getFragmentManager().beginTransaction()
                .add(R.id.fragment_kcal, mKcalSettingsFragment)
                .commit();
        } else {
            mKcalSettingsFragment = (KcalSettings) fragment;
        }
    }

    private void setupImageSlider() {
        viewPager = (ViewPager) findViewById(R.id.preview);
        sliderDotspanel = (LinearLayout) findViewById(R.id.SliderDots);

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getApplicationContext());
        viewPager.setAdapter(viewPagerAdapter);

        dotscount = viewPagerAdapter.getCount();
        dots = new ImageView[dotscount];

        for(int i = 0; i < dotscount; i++) {
            dots[i] = new ImageView(getApplicationContext());
            dots[i].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.inactive_dot));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(8, 0, 8, 0);
            sliderDotspanel.addView(dots[i], params);
        }

        dots[0].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.active_dot));

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {

                for(int i = 0; i< dotscount; i++){
                    dots[i].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.inactive_dot));
                }
                dots[position].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.active_dot));
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }
}
