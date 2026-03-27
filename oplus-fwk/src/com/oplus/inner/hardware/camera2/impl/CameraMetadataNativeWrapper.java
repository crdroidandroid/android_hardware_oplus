package com.oplus.inner.hardware.camera2.impl;

import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.util.Log;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class CameraMetadataNativeWrapper {
    private static final String TAG = "CameraMetadataNativeWrapper";

    public CameraMetadataNativeWrapper() {
    }

    public static long getMetadataPtr(Object obj) {
        if (obj == null) return 0L;

        try {
            Object nativeMeta = obj;

            // 1. Unwrap CameraCharacteristics
            if (obj instanceof CameraCharacteristics) {
                try {
                    Method getNativeCopy = CameraCharacteristics.class.getDeclaredMethod("getNativeCopy");
                    getNativeCopy.setAccessible(true);
                    nativeMeta = getNativeCopy.invoke(obj);
                } catch (Exception e) {
                    Field f = CameraCharacteristics.class.getDeclaredField("mProperties");
                    f.setAccessible(true);
                    nativeMeta = f.get(obj);
                }
            }
            // 2. Unwrap CaptureRequest
            else if (obj instanceof CaptureRequest) {
                try {
                    Method getNativeCopy = CaptureRequest.class.getDeclaredMethod("getNativeCopy");
                    getNativeCopy.setAccessible(true);
                    nativeMeta = getNativeCopy.invoke(obj);
                } catch (Exception e) {
                    Field f = CaptureRequest.class.getDeclaredField("mLogicalCameraSettings");
                    f.setAccessible(true);
                    nativeMeta = f.get(obj);
                }
            }
            // 3. Unwrap CaptureResult
            else if (obj instanceof CaptureResult) {
                try {
                    Method getNativeCopy = CaptureResult.class.getDeclaredMethod("getNativeCopy");
                    getNativeCopy.setAccessible(true);
                    nativeMeta = getNativeCopy.invoke(obj);
                } catch (Exception e) {
                    Field f = CaptureResult.class.getDeclaredField("mResults");
                    f.setAccessible(true);
                    nativeMeta = f.get(obj);
                }
            }

            if (nativeMeta == null) {
                Log.e(TAG, "Unwrapped metadata is null!");
                return 0L;
            }

            // 4. Extract the C++ pointer from the unwrapped CameraMetadataNative object
            Field ptrField = nativeMeta.getClass().getDeclaredField("mMetadataPtr");
            ptrField.setAccessible(true);
            long ptr = ptrField.getLong(nativeMeta);

            return ptr;

        } catch (Exception e) {
            Log.e(TAG, "Failed to get ptr from " + obj.getClass().getName(), e);
            return 0L;
        }
    }
}
