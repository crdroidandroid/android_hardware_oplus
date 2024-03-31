/*
 * Copyright (C) 2018-2022 crDroid Android Project
 * SPDX-License-Identifier: Apache-2.0
 */

package org.lineageos.settings.slider;

import android.content.Context;
import android.os.RemoteException;
import android.util.Log;
import android.view.IWindowManager;
import android.view.Surface;
import android.view.WindowManagerGlobal;

public final class RotationController extends SliderControllerBase {

    public static final int ID = 4;

    private static final String TAG = "RotationController";

    private static final int ROTATION_AUTO = 40;
    private static final int ROTATION_0 = 41;
    private static final int ROTATION_90 = 42;
    private static final int ROTATION_270 = 43;

    public RotationController(Context context) {
        super(context);
    }

    @Override
    protected int processAction(int action) {
        Log.d(TAG, "slider action: " + action);
        switch (action) {
            case ROTATION_AUTO:
                return setRotation(false, 0) ? SliderConstants.MODE_ROTATION_AUTO : 0;
            case ROTATION_0:
                return setRotation(true, Surface.ROTATION_0) ? SliderConstants.MODE_ROTATION_0 : 0;
            case ROTATION_90:
                return setRotation(true, Surface.ROTATION_90) ? SliderConstants.MODE_ROTATION_90 : 0;
            case ROTATION_270:
                return setRotation(true, Surface.ROTATION_270) ? SliderConstants.MODE_ROTATION_270 : 0;
        }
        return 0;
    }

    @Override
    public void reset() {
        setRotation(false, 0);
    }

    private boolean setRotation(boolean locked, int rotation) {
        try {
            IWindowManager wm = WindowManagerGlobal.getWindowManagerService();
            if (locked) {
                wm.freezeRotation(rotation, /* caller= */ "RotationController#setRotation");
            } else {
                wm.thawRotation(/* caller= */ "RotationController#restoreRotationStateLocked");
            }
            return true;
        } catch (RemoteException exc) {
            Log.w(TAG, "Unable to save auto-rotate setting");
            return false;
        }
    }
}
