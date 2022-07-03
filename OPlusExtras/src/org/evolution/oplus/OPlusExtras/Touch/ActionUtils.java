package org.evolution.oplus.OPlusExtras.touch;

import android.app.KeyguardManager;
import android.content.Context;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.UserHandle;

import java.util.List;

public class ActionUtils {

    private static Intent getLaunchableIntent(Context context, Intent intent) {
        PackageManager pm = context.getPackageManager();
        List<ResolveInfo> resInfo = pm.queryIntentActivities(intent, 0);
        if (resInfo.isEmpty()) {
            return null;
        }
        return pm.getLaunchIntentForPackage(resInfo.get(0).activityInfo.packageName);
    }

    private static Intent getBrowserIntent(Context context) {
        return getLaunchableIntent(context,
                new Intent(Intent.ACTION_VIEW, Uri.parse("http:")));
    }

    private static Intent getDialerIntent() {
        return new Intent(Intent.ACTION_DIAL, null);
    }

    private static Intent getEmailIntent(Context context) {
        return getLaunchableIntent(context,
                new Intent(Intent.ACTION_VIEW, Uri.parse("mailto:")));
    }

    private static Intent getMessagesIntent(Context context) {
        return getLaunchableIntent(context,
                new Intent(Intent.ACTION_VIEW, Uri.parse("sms:")));
    }

    private static Intent getWechatPayIntent(Context context) {
        Intent intent = new Intent();
        ComponentName cn=new ComponentName("com.tencent.mm",
                "com.tencent.mm.plugin.offline.ui.WalletOfflineCoinPurseUI");
        intent.setComponent(cn);
        return intent;
    }

    private static Intent getAlipayPayIntent(Context context) {
        Intent intent = new Intent();
        ComponentName cn=new ComponentName("com.eg.android.AlipayGphone",
                "com.alipay.mobile.onsitepay.merge.OnsitepayActivityStandard");
        intent.setComponent(cn);
        return intent;
    }

    private static Intent getWechatScanIntent(Context context) {
        Intent intent = new Intent();
        ComponentName cn=new ComponentName("com.tencent.mm",
                "com.tencent.mm.plugin.scanner.ui.BaseScanUI");
        intent.setComponent(cn);
        return intent;
    }

    private static Intent getAlipayScanIntent(Context context) {
        Intent intent = new Intent();
        ComponentName cn=new ComponentName("com.eg.android.AlipayGphone",
                "com.alipay.mobile.scan.as.main.MainCaptureActivity");
        intent.setComponent(cn);
        return intent;
    }

    private static Intent getAlipayTripIntent(Context context) {
        Intent intent = new Intent();
        ComponentName cn=new ComponentName("com.eg.android.AlipayGphone",
                "com.alipay.android.phone.wallet.aptrip.ui.ApTripActivity");
        intent.setComponent(cn);
        return intent;
    }

    private static Intent getWalletTripIntent(Context context) {
        Intent intent = new Intent();
        ComponentName cn=new ComponentName("com.finshell.wallet",
                "com.nearme.wallet.nfc.ui.NfcConsumeActivity");
        intent.setComponent(cn);
        return intent;
    }

    private static Intent getOPlusExtrasIntent(Context context) {
        Intent intent = new Intent();
        ComponentName cn=new ComponentName("org.evolution.oplus.OPlusExtras",
                "org.evolution.oplus.OPlusExtras.OPlusExtrasActivity");
        intent.setComponent(cn);
        return intent;
    }

    private static Intent getGoogleMapsIntent(Context context) {
        Intent intent = new Intent();
        ComponentName cn=new ComponentName("com.google.android.apps.maps",
                "com.google.android.maps.MapsActivity");
        intent.setComponent(cn);
        return intent;
    }

    private static Intent getGoogleSearchIntent(Context context) {
        Intent intent = new Intent();
        ComponentName cn=new ComponentName("com.google.android.googlequicksearchbox",
                "com.google.android.googlequicksearchbox.SearchActivity");
        intent.setComponent(cn);
        return intent;
    }

    public static Intent getIntentByAction(Context context, int action) {
        Intent intent = null;
        if (action == TouchscreenGestureConstants.ACTION_BROWSER) {
            intent = getBrowserIntent(context);
        } else if (action == TouchscreenGestureConstants.ACTION_DIALER) {
            intent = getDialerIntent();
        } else if (action == TouchscreenGestureConstants.ACTION_EMAIL) {
            intent = getEmailIntent(context);
        } else if (action == TouchscreenGestureConstants.ACTION_MESSAGES) {
            intent = getMessagesIntent(context);
        } else if (action == TouchscreenGestureConstants.ACTION_WECHAT_PAY) {
            intent = getWechatPayIntent(context);
        } else if (action == TouchscreenGestureConstants.ACTION_ALIPAY_PAY) {
            intent = getAlipayPayIntent(context);
        } else if (action == TouchscreenGestureConstants.ACTION_WECHAT_SCAN) {
            intent = getWechatScanIntent(context);
        } else if (action == TouchscreenGestureConstants.ACTION_ALIPAY_SCAN) {
            intent = getAlipayScanIntent(context);
        } else if (action == TouchscreenGestureConstants.ACTION_ALIPAY_TRIP) {
            intent = getAlipayTripIntent(context);
        } else if (action == TouchscreenGestureConstants.ACTION_WALLET_TRIP) {
            intent = getWalletTripIntent(context);
        } else if (action == TouchscreenGestureConstants.ACTION_OPLUS_EXTRAS) {
            intent = getOPlusExtrasIntent(context);
        } else if (action == TouchscreenGestureConstants.ACTION_GOOGLE_MAPS) {
            intent = getGoogleMapsIntent(context);
        } else if (action == TouchscreenGestureConstants.ACTION_GOOGLE_SEARCH) {
            intent = getGoogleSearchIntent(context);
        }
        return intent;
    }

    public static void triggerAction(Context context, int action) {
        Intent intent = getIntentByAction(context, action);
        if (intent == null) {
            return;
        }
        KeyguardManager km = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
        if (km.isKeyguardLocked()) {
            intent = new Intent();
            intent.setClassName("org.evolution.oplus.OPlusExtras", "org.evolution.oplus.OPlusExtras.touch.ScreenOffLaunchGestureActivity");
            intent.putExtra(ScreenOffLaunchGestureActivity.ACTION_KEY, action);
        }
        startActivitySafely(context, intent);
    }

    public static void startActivitySafely(Context context, Intent intent) {
        if (intent == null) {
            return;
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_SINGLE_TOP
                | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        try {
            UserHandle user = new UserHandle(UserHandle.USER_CURRENT);
            context.startActivityAsUser(intent, null, user);
        } catch (Exception e) {
            // Ignore
        }
    }
}
