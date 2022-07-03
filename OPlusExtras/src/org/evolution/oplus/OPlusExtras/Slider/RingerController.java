/*
 * Copyright (C) 2018-2022 crDroid Android Project
 * SPDX-License-Identifier: Apache-2.0
 */

package org.evolution.oplus.OPlusExtras.slider;

import android.content.Context;
import android.media.AudioManager;
import android.util.Log;
import android.util.SparseIntArray;

public final class RingerController extends SliderControllerBase {

    public static final int ID = 5;

    private static final String TAG = "RingerController";

    private static final int RINGER_NORMAL = 50;
    private static final int RINGER_VIBRATE = 51;
    private static final int RINGER_SILENT = 52;

    private static final SparseIntArray MODES = new SparseIntArray();
    static {
        MODES.put(RINGER_NORMAL, AudioManager.RINGER_MODE_NORMAL);
        MODES.put(RINGER_VIBRATE, AudioManager.RINGER_MODE_VIBRATE);
        MODES.put(RINGER_SILENT, AudioManager.RINGER_MODE_SILENT);
    }

    private final AudioManager mAudioManager;

    public RingerController(Context context) {
        super(context);
        mAudioManager = context.getSystemService(AudioManager.class);
    }

    @Override
    protected int processAction(int action) {
        Log.d(TAG, "slider action: " + action);
        if (MODES.indexOfKey(action) >= 0) {
            mAudioManager.setRingerModeInternal(MODES.get(action));
            switch (action) {
                case RINGER_NORMAL:
                    return SliderConstants.MODE_RING;
                case RINGER_VIBRATE:
                    return SliderConstants.MODE_VIBRATE;
                case RINGER_SILENT:
                    return SliderConstants.MODE_SILENT;
            }
        }
        return 0;
    }

    @Override
    public void reset() {
        mAudioManager.setRingerModeInternal(AudioManager.RINGER_MODE_NORMAL);
    }
}
