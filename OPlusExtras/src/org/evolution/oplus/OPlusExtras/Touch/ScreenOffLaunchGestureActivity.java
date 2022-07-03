package org.evolution.oplus.OPlusExtras.touch;

import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class ScreenOffLaunchGestureActivity extends Activity {

    static final String ACTION_KEY = "action";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int action = 0;
        try {
            action = getIntent().getExtras().getInt(ACTION_KEY);
        } catch(Exception ignored) {
        }
        final Intent intent = ActionUtils.getIntentByAction(this, action);
        if (intent == null) {
            finish();
        }
        new Handler().post(() -> {
            KeyguardManager keyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
            keyguardManager.requestDismissKeyguard(ScreenOffLaunchGestureActivity.this, new KeyguardManager.KeyguardDismissCallback() {
                @Override
                public void onDismissSucceeded() {
                    ActionUtils.startActivitySafely(ScreenOffLaunchGestureActivity.this, intent);
                    finish();
                }

                @Override
                public void onDismissError() {
                    finish();
                }

                @Override
                public void onDismissCancelled() {
                    finish();
                }
            });
        });
    }
}
