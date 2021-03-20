package cn.jj.base.utils;

import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.text.TextUtils;

import java.util.List;

import androidx.core.content.pm.ShortcutInfoCompat;
import androidx.core.content.pm.ShortcutManagerCompat;
import androidx.core.graphics.drawable.IconCompat;

/**
 * Created by konglj on 2017/11/16 0016.
 */

public class ShortcutUtils {
    private static final java.lang.String TAG = "ShortcutUtils";

    private static final String ACTION_ADD_SHORTCUT = "com.android.launcher.action.INSTALL_SHORTCUT";

    public static boolean addShortcut(Context context,
                                      String name,
                                      Bitmap icon,
                                      Intent intent) {
        boolean res = false;
        if (TextUtils.isEmpty(name)) {
            return res;
        }
        ShortcutInfoCompat info = new ShortcutInfoCompat.Builder(context, name)
                .setShortLabel(name)
                .setIcon(IconCompat.createWithBitmap(icon))
                .setIntent(intent)
                .build();
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
                0,
                ShortcutManagerCompat.createShortcutResultIntent(context, info),
                0);
        if (ShortcutManagerCompat.isRequestPinShortcutSupported(context)) {
            try {
                ShortcutManagerCompat.requestPinShortcut(context, info, pendingIntent.getIntentSender());
                res = true;
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
        }
        if (!res) {
            Intent broadcastIntent = new Intent(ACTION_ADD_SHORTCUT);
            broadcastIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, name);
            broadcastIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON, icon);
            broadcastIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent);
            context.sendBroadcast(intent);
        }
        return res;
    }

    public static boolean isShortcutExist(Context context,
                                          String name,
                                          ComponentName componentName) {
        boolean result = false;
        if (TextUtils.isEmpty(name)) {
            return false;
        }
        ContentResolver cr = context.getContentResolver();
        Uri uri = getUriFromLauncher(context);
        try {
            Cursor c = cr.query(uri,
                    new String[]{"intent"},
                    "title=?",
                    new String[]{name},
                    null);
            if (c != null && c.getCount() > 0) {
                try {
                    while (c.moveToNext()) {
                        String dbValue = c.getString(0);
                        if (dbValue.contains(componentName.flattenToString())) {
                            result = true;
                            break;
                        }
                    }
                } catch (Exception ex) {
                    result = false;
                } finally {
                    IOUtil.closeQuietly(c);
                }
            }

        } catch (Exception e) {
        }
        return result;
    }

    private static Uri getUriFromLauncher(Context context) {
        StringBuilder uriStr = new StringBuilder();
        String authority = getAuthorityFromPermission(context,
                "com.android.launcher.permission.READ_SETTINGS");
        if (authority == null || TextUtils.isEmpty(authority.trim())) {
            authority = getAuthorityFromPermission(context,
                    getCurrentLauncherPackageName(context) + ".permission.READ_SETTINGS");
        }
        uriStr.append("content://");
        if (TextUtils.isEmpty(authority)) {
            int sdkInt = android.os.Build.VERSION.SDK_INT;
            if (sdkInt < 8) { // Android 2.1.x(API 7)以及以下的
                uriStr.append("com.android.launcher.settings");
            } else if (sdkInt < 19) {// Android 4.4以下
                uriStr.append("com.android.launcher2.settings");
            } else {// 4.4以及以上
                uriStr.append("com.android.launcher3.settings");
            }
        } else {
            uriStr.append(authority);
        }
        uriStr.append("/favorites?notify=true");
        return Uri.parse(uriStr.toString());
    }


    public static String getCurrentLauncherPackageName(Context context) {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        ResolveInfo res = context.getPackageManager().resolveActivity(intent, 0);
        if (res == null || res.activityInfo == null) {
            // should not happen. A home is always installed, isn't it?
            return "";
        }
        if (res.activityInfo.packageName.equals("android")) {
            return "";
        } else {
            return res.activityInfo.packageName;
        }
    }

    public static String getAuthorityFromPermission(Context context, String permission) {
        if (TextUtils.isEmpty(permission)) {
            return null;
        }
        try {
            List<PackageInfo> packs = context.getPackageManager().getInstalledPackages(PackageManager.GET_PROVIDERS);
            if (packs == null || packs.isEmpty()) {
                return null;
            }
            for (PackageInfo pack : packs) {
                ProviderInfo[] providers = pack.providers;
                if (providers != null) {
                    for (ProviderInfo provider : providers) {
                        if (permission.equals(provider.readPermission) ||
                                permission.equals(provider.writePermission)) {
                            return provider.authority;
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String createShortcutName(ComponentName componentName) {
        String pkgName = componentName.getPackageName();
        if (TextUtils.isEmpty(pkgName)) {
            return null;
        }
        return pkgName + "_boost_shortcut";
    }
}
