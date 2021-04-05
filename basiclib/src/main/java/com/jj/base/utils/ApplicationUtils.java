package com.jj.base.utils;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.app.KeyguardManager;
import android.app.Notification;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.accessibility.AccessibilityManager;

import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.jj.base.common.ThreadManager;

public class ApplicationUtils {
    private final static String TAG = "ApplicationUtils";
    private static final int MAX_NUMS_RUNNING_SERVICE_INFO = 120;
    static Context context = Utility.getApplication();

    public static ArrayList<PackageInfo> getAllApp(Context context) {
        PackageManager packageManager = context.getPackageManager();
        ArrayList<PackageInfo> packages = (ArrayList<PackageInfo>) packageManager
                .getInstalledPackages(0);

        return packages;
    }

    public static List<ResolveInfo> getLaunchableApps(Context context) {

        Intent launcherIntent = new Intent("android.intent.action.MAIN", null);
        launcherIntent.addCategory("android.intent.category.LAUNCHER");

        PackageManager packageManager = context.getPackageManager();

        List<ResolveInfo> resolveInfos = null;
        try {
            resolveInfos = packageManager.queryIntentActivities(launcherIntent, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resolveInfos;
    }

    public static Intent getApplicationLauncherIntent(String packageName,
                                                      String className) {
        Intent launcherIntent = new Intent("android.intent.action.MAIN", null);
        launcherIntent.addCategory("android.intent.category.LAUNCHER");
        launcherIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        launcherIntent.setClassName(packageName, className);

        return launcherIntent;
    }

    public static void openApplicationLauncher(Context context,
                                               String packageName, String className) {
        Intent launcherIntent = new Intent("android.intent.action.MAIN", null);
        launcherIntent.addCategory("android.intent.category.LAUNCHER");
        launcherIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        launcherIntent.setClassName(packageName, className);

        context.startActivity(launcherIntent);
    }

    public static void openApplicationLauncher(Context context,
                                               ComponentName component) {

        Intent launcherIntent = new Intent("android.intent.action.MAIN", null);
        launcherIntent.addCategory("android.intent.category.LAUNCHER");
        launcherIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        launcherIntent.setComponent(component);

        context.startActivity(launcherIntent);
    }

    public static boolean isSystemApp(ApplicationInfo applicationInfo) {

        return (applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0;
    }

    public static String getAppName(String pkgName) {
        PackageManager pm = Utility.getApplication().getPackageManager();
        PackageInfo pkgInfo = null;
        try {
            pkgInfo = pm.getPackageInfo(pkgName, 0);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        String appName = pkgInfo.applicationInfo.loadLabel(pm).toString().trim();
        if (TextUtils.isEmpty(appName)) {
            appName = pm.getApplicationLabel(pkgInfo.applicationInfo).toString();
        }
        //去掉特殊的空格字符 0xc2/0xa0对应ascII中的特殊空格
        if (!TextUtils.isEmpty(appName) && (appName.charAt(0) == 0xc2 || appName.charAt(0) == 0xa0)) {
            appName = appName.substring(1);
        }
        return appName;
    }

    /**
     * 判断某个服务是否正在运行的方法
     *
     * @return true代表正在运行，false代表服务没有正在运行
     */
    public static boolean isServiceRunning(Class serviceClass) {
        String serviceName = serviceClass.getName();
        ActivityManager myAM = (ActivityManager) Utility.getApplication()
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> myList = myAM.getRunningServices(Integer.MAX_VALUE);
        if (myList == null || myList.isEmpty()) {
            return false;
        }
        for (ActivityManager.RunningServiceInfo service : myList) {
            if (serviceName.equals(service.service.getClassName()))
                return true;
        }
        return false;
    }

    public static boolean isServiceRunning(String className) {
        try {
            return isServiceRunning(Class.forName(className));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean isNotificationEnabled() {
        Context context = Utility.getApplication();
        String pkgName = context.getPackageName();
        try {
            final String flat = Settings.Secure.getString(context.getContentResolver(), "enabled_notification_listeners");
            if (!TextUtils.isEmpty(flat)) {
                final String[] names = flat.split(":");
                for (int i = 0; i < names.length; i++) {
                    final ComponentName cn = ComponentName.unflattenFromString(names[i]);
                    if (cn != null) {
                        if (TextUtils.equals(pkgName, cn.getPackageName())) {
                            return true;
                        }
                    }
                }
            }
        } catch (Exception e) {

        }
        return false;
    }

    public static boolean startApp(ComponentName componentName) {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setComponent(componentName);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        return ApplicationUtils.startActivityForResultSafely(context, intent, 0);
    }

    public static boolean startActivityForResultSafely(
            Context context, Intent intent, int requestCode) {
        try {
            if (context instanceof Activity) {
                ((Activity) context).startActivityForResult(intent, requestCode);
            } else {
                context.startActivity(intent);
            }
            return true;
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean startActivityForResultSafely(
            Context context, Intent intent, Bundle opts) {
        try {
            if (Constant.ATLEAST_JB) {
                context.startActivity(intent, opts);
            } else {
                context.startActivity(intent);
            }
            return true;
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean startActivityForResultSafely(
            Context context, Intent[] intents) {
        try {
            context.startActivities(intents);
            return true;
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * xiaojf add this replace up function for reason of "BUG_A #6919 [4.6.0]oppo find7 360加速球清理后，点击面板返回键无响应，清理前好使"
     * 一些手机使用清理软件清理后，et的返回键不能使用，辅助功能开关虽然打开着，但是功能无法使用，用之前函数即getAllAccessibilityServices的方法获取到的开关状态不正确，所以现在
     * 需要换一种方式检测
     */
    public static boolean isAccessibilityServiceOn(Class serviceClass) {
        Context context = Utility.getApplication();
        String packageName = context.getPackageName();
        String className = serviceClass.getName();
        try {
            String concat = new StringBuffer().append(packageName)
                    .append('/')
                    .append(className)
                    .toString();
            String simple = new StringBuffer().append(packageName)
                    .append("/")
                    .append(className.substring(packageName.length()))
                    .toString();
            AccessibilityManager am = (AccessibilityManager) context.getSystemService(Context.ACCESSIBILITY_SERVICE);
            List<AccessibilityServiceInfo> serviceInfos = am.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_ALL_MASK);
            //List<AccessibilityServiceInfo> installedAccessibilityServiceList = am.getInstalledAccessibilityServiceList();
            for (AccessibilityServiceInfo info : serviceInfos) {
                String infoId = info.getId();
                if (concat.equals(infoId) || simple.equals(infoId)) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return isAccessibilityServiceOn_old(serviceClass);
        } catch (Throwable t) {
            t.printStackTrace();
            return isAccessibilityServiceOn_old(serviceClass);
        }
    }

    private static boolean isAccessibilityServiceOn_old(Class serviceClass) {
        Context context = Utility.getApplication();
        String packageName = context.getPackageName();
        String concat = new StringBuffer().append(packageName).append('/').append(serviceClass.getName()).toString();

        TextUtils.SimpleStringSplitter colonSplitter = new TextUtils.SimpleStringSplitter(':');
        String settingValue = Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
        if (settingValue != null) {
            colonSplitter.setString(settingValue);
            while (colonSplitter.hasNext()) {
                String accessabilityService = colonSplitter.next();
                if (accessabilityService.equals(concat)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 拨打电话
     */
    public static void startCall(Context ctx, String phoneNum) {
        try {
            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneNum));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ctx.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 启动发送短信页面
     *
     * @param ctx
     * @param phoneNum
     */
    public static void startSms(Context ctx, String phoneNum) {
        try {
            Uri smsToUri = Uri.parse("smsto:" + phoneNum);
            Intent mIntent = new Intent(Intent.ACTION_SENDTO, smsToUri);
            mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ctx.startActivity(mIntent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean startBrowser(Context ctx, Uri uri) {
        if (context == null || uri == null) {
            return false;
        }
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(uri);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ResolveInfo resolveInfo = context.getPackageManager().resolveActivity(i, 0);
        if (resolveInfo != null) {
            try {
                context.startActivity(i);
                return true;
            } catch (ActivityNotFoundException e) {
                LogUtil.i(TAG, e.toString());
            }
        }
        return false;
    }

    /**
     * @param ctx
     * @param emailAddress
     */
    public static void startEmail(Context ctx, String emailAddress) {
        try {
            Intent intent = new Intent("android.intent.action.SENDTO");
            intent.setData(Uri.parse("mailto:"));
            intent.putExtra("android.intent.extra.EMAIL", new String[]{emailAddress});
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ctx.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 通过mimetype和id启动与该联系人的聊天界面
     *
     * @param ctx
     * @param mimetype
     * @param data_id  不是contact_id,是data表中的_id
     */
    public static void startContactUriByMimetype(Context ctx, String mimetype, String data_id) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setDataAndType(Uri.withAppendedPath(
                    ContactsContract.Data.CONTENT_URI, data_id),
                    mimetype);
            ctx.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void expandStatusBar(Context context) {
        try {
            Object statusBarManager = context.getSystemService("statusbar");
            Method expand;
            if (Build.VERSION.SDK_INT <= 16) {
                expand = statusBarManager.getClass().getMethod("expand");
            } else {
                expand = statusBarManager.getClass().getMethod("expandNotificationsPanel");
            }
            expand.setAccessible(true);
            expand.invoke(statusBarManager);
        } catch (Exception localException) {
            localException.printStackTrace();
        }
    }

    public static void collapseStatusBar(Context context) {
        try {
            Object statusBarManager = context.getSystemService("statusbar");
            Method collapse;

            if (Build.VERSION.SDK_INT <= 16) {
                collapse = statusBarManager.getClass().getMethod("collapse");
            } else {
                collapse = statusBarManager.getClass().getMethod("collapsePanels");
            }
            collapse.invoke(statusBarManager);
        } catch (Exception localException) {
            localException.printStackTrace();
        }
    }

    /**
     * 通过intent来判断此intent是否存在
     *
     * @param intent
     * @return
     */
    public static boolean isIntentAvailable(Intent intent) {
        try {
            final PackageManager packageManager = context.getPackageManager();
            List<ResolveInfo> list = packageManager.queryIntentActivities(intent, PackageManager.GET_ACTIVITIES);
            return list != null && list.size() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void showSystemRecentApps(Context context) throws Exception {
        Class serviceManagerClass = Class.forName("android.os.ServiceManager");
        Method getService = serviceManagerClass.getMethod("getService", String.class);
        IBinder retbinder = (IBinder) getService.invoke(serviceManagerClass, "statusbar");
        Class statusBarClass = Class.forName(retbinder.getInterfaceDescriptor());
        Object statusBarObject = statusBarClass.getClasses()[0].getMethod(
                "asInterface", IBinder.class).invoke(null,
                retbinder);
        Method clearAll = statusBarClass.getMethod("toggleRecentApps");
        clearAll.setAccessible(true);
        clearAll.invoke(statusBarObject);
    }

    /**
     * 通知是否可被清除
     * Convenience method to check the notification's flags for
     * either {@link Notification#FLAG_ONGOING_EVENT} or
     * {@link Notification#FLAG_NO_CLEAR}.
     */
    public static boolean isNotificationClearable(int flags) {
        return ((flags & Notification.FLAG_ONGOING_EVENT) == 0)
                && ((flags & Notification.FLAG_NO_CLEAR) == 0);
    }

    public static boolean isScreenLocked(Context context) {
        KeyguardManager keyguardManager = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
        if (Build.VERSION.SDK_INT >= 16) {
            return keyguardManager.isKeyguardLocked();
        }
        return keyguardManager.inKeyguardRestrictedInputMode();
    }

    public static Drawable getAppIcon(ActivityInfo info, int density) {
        int iconRes = info.getIconResource();
        Drawable icon = null;
        // Get the preferred density icon from the app's resources
        if (iconRes != 0) {
            try {
                Resources resources = context.getPackageManager().getResourcesForApplication(info.applicationInfo);
                if (Constant.ATLEAST_JB && density >= DisplayMetrics.DENSITY_LOW) {
                    icon = resources.getDrawableForDensity(iconRes, density);
                } else {
                    icon = resources.getDrawable(iconRes);
                }
            } catch (Exception exc) {
            }
        }
        // Get the default density icon
        try {
            if (icon == null) {
                icon = info.loadIcon(context.getPackageManager());
            }
        } catch (Exception e) {
        }
        if (icon == null) {
            Resources resources = Resources.getSystem();
            if (Constant.ATLEAST_JB && density >= DisplayMetrics.DENSITY_LOW) {
                icon = resources.getDrawableForDensity(android.R.mipmap.sym_def_app_icon, density);
            } else {
                icon = resources.getDrawable(android.R.mipmap.sym_def_app_icon);
            }
        }
        return icon;
    }

    public static Drawable getAppIcon(ComponentName componentName) {
        return getAppIcon(componentName, 0);
    }

    public static Drawable getAppIcon(ComponentName componentName, int density) {
        PackageManager pm = context.getPackageManager();
        try {
            ActivityInfo activityInfo = pm.getActivityInfo(componentName, 0);
            if (activityInfo != null) {
                return ApplicationUtils.getAppIcon(activityInfo, density).mutate();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ContextCompat.getDrawable(context, android.R.mipmap.sym_def_app_icon);
    }


    public static void removeMenuLongClickToast(Toolbar toolbar, int[] ids) {
        if (toolbar != null && ids != null && ids.length > 0) {
            ThreadManager.post(ThreadManager.THREAD_UI, () -> {
                Context context = toolbar.getContext();
                if (context != null) {
                    if (context instanceof Activity) {
                        if (((Activity) context).isFinishing()) {
                            return;
                        }
                    }
                    for (int id : ids) {
                        View v = toolbar.findViewById(id);
                        if (v != null) {
                            v.setOnLongClickListener(v1 -> false);
                        }
                    }
                }
            });
        }
    }

    public static void getCallInfo(String tag) {
        StackTraceElement[] ste = new Throwable().getStackTrace();
        for (StackTraceElement s : ste) {
            String className = s.getClassName().contains(".") ? s
                    .getClassName().substring(
                            s.getClassName().lastIndexOf("."),
                            s.getClassName().length()) : s.getClassName();

            Log.e(tag, String.format("------%s.%s [%s]",
                    className, s.getMethodName(), s.getLineNumber()
            ));
        }
    }

    public static int getLauncherIconDensity(int requiredSize) {
        // Densities typically defined by an app.
        int[] densityBuckets = new int[]{
                DisplayMetrics.DENSITY_LOW,
                DisplayMetrics.DENSITY_MEDIUM,
                DisplayMetrics.DENSITY_TV,
                DisplayMetrics.DENSITY_HIGH,
                DisplayMetrics.DENSITY_XHIGH,
                Constant.ATLEAST_JB ? DisplayMetrics.DENSITY_XXHIGH :
                        DisplayMetrics.DENSITY_XHIGH,
                Constant.ATLEAST_JB_MR2 ? DisplayMetrics.DENSITY_XXXHIGH :
                        Constant.ATLEAST_JB ? DisplayMetrics.DENSITY_XXHIGH :
                                DisplayMetrics.DENSITY_XHIGH
        };

        int density = DisplayMetrics.DENSITY_XHIGH;
        for (int i = densityBuckets.length - 1; i >= 0; i--) {
            float expectedSize = 48 * densityBuckets[i]
                    / DisplayMetrics.DENSITY_DEFAULT;
            if (expectedSize >= requiredSize) {
                density = densityBuckets[i];
            }
        }

        return density;
    }

    public static boolean isAppExisted(String themePkgName) {
        PackageManager pm = Utility.getApplication().getPackageManager();
        try {
            pm.getPackageInfo(themePkgName, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (NameNotFoundException e) {
        }
        return false;
    }

    public static boolean hasPermissionAccessNetworkStats(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            return true;
        }
        try {
            PackageManager packageManager = context.getPackageManager();
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(context.getPackageName(), 0);
            AppOpsManager appOpsManager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
            int mode = appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, applicationInfo.uid, applicationInfo.packageName);
            return mode == AppOpsManager.MODE_ALLOWED;
        } catch (NameNotFoundException e) {
            return false;
        }
    }

    public static boolean intoSystemAppDetailSetting(Context context, String packageName) {
        try {
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + packageName));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
            return true;
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return false;
    }

    public static String getTopActivityPackageName(Context context) {
        String packageName = null;
        ActivityManager activityManager = (ActivityManager) (context.getSystemService(android.content.Context.ACTIVITY_SERVICE));
        List<ActivityManager.RunningTaskInfo> runningTaskInfos = activityManager
                .getRunningTasks(1);
        if (runningTaskInfos != null && !runningTaskInfos.isEmpty()) {
            ComponentName f = runningTaskInfos.get(0).topActivity;
            packageName = f.getPackageName();
        }
        return packageName;
    }

    public static boolean checkPlayServices(Context context) {
//        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
//        int resultCode = apiAvailability.isGooglePlayServicesAvailable(context);
//        return resultCode == ConnectionResult.SUCCESS;
        return false;
    }

    public static boolean startMarket(Context context, String packageName) {
        if (null == context || TextUtils.isEmpty(packageName))
            return false;
        Uri uri = Uri.parse("market://details?id=" + packageName);
        Intent intent;
        if (checkPlayServices(context)) {
            intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.setPackage("com.android.vending");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ResolveInfo resolveInfo = context.getPackageManager().resolveActivity(intent, 0);
            if (resolveInfo != null) {
                try {
                    ComponentName comp = new ComponentName("com.android.vending",
                            "com.google.android.finsky.activities.LaunchUrlHandlerActivity");
                    intent.setComponent(comp);
                    context.startActivity(intent);
                    return true;
                } catch (ActivityNotFoundException e) {
                    LogUtil.i(TAG, "Google Play is not installed");
                }
            }
        }

        intent = new Intent(Intent.ACTION_VIEW, uri);
        if (!(context instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        try {
            context.startActivity(intent);
            return true;
        } catch (ActivityNotFoundException e) {
            LogUtil.i(TAG, "Market is not installed");
        }
        return startBrowser(context, uri);
    }

    public static boolean installApk(Context context, String path) {
        if (TextUtils.isEmpty(path)) {
            return false;
        }
        if (!checkInstallPermission(context)) {
            return false;
        }
        File file = new File(path);
        Intent install = new Intent(Intent.ACTION_VIEW);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            install.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Uri contentUri = FileProvider.getUriForFile(context, context.getPackageName() + ".provider", file);
            install.setDataAndType(contentUri, "application/vnd.android.package-archive");
        } else {
            install.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        }
        install.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return startActivityForResultSafely(context, install, 0);
    }

    private static boolean checkInstallPermission(Context context) {
        if (Constant.ATLEAST_O) {
            if (context.getPackageManager().canRequestPackageInstalls()) {
                return true;
            } else {
                ToastUtil.showShort(context, "安装应用需要打开未知来源权限，请去设置中开启权限");
                startActivityForResultSafely(context, new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES), 0);
                return false;
            }
        } else {
            return true;
        }
    }
}