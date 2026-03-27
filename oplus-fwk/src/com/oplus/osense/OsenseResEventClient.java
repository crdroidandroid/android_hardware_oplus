package com.oplus.osense;

import com.oplus.osense.eventinfo.EventConfig;
import com.oplus.osense.eventinfo.OsenseEventCallback;
import android.os.Bundle;

public class OsenseResEventClient {

    private static OsenseResEventClient sInstance;

    public static OsenseResEventClient getInstance() {
        if (sInstance == null) {
            sInstance = new OsenseResEventClient();
        }
        return sInstance;
    }

    public int registerEventCallback(OsenseEventCallback callback, EventConfig eventConfig) {
        return 0;
    }

    public int unregisterEventCallback(OsenseEventCallback callback, EventConfig eventConfig) {
        return 0;
    }

    public void requestSceneAction(Bundle bundle) {
        return;
    }
}
