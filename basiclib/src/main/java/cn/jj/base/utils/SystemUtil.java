package cn.jj.base.utils;

import android.Manifest;
import android.app.ActivityManager;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.os.storage.StorageManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import androidx.core.content.PermissionChecker;

/**
 * Created by konglj on 2016/8/29.
 */
public class SystemUtil {
    private static final String TAG = "SystemUtil";

    public static final int BYTES_PER_KB = 1024;
    public static final int BYTES_PER_MB = 1024 * 1024;
    public static final int BYTES_PER_GB = 1024 * 1024 * 1024;

    public static final int SECOUND = 1000;
    public static final int MINUTE = 60 * SECOUND;
    public static final int HOUR = 60 * MINUTE;

    public static final String UNKNOWN_SIM = "unknown";
    /**
     * 存放su文件可能存在的路径，根据su文件是否存在来判断手机是否root
     */
    private static final String[] SU_PATH = {
            "/system/xbin/su",
            "/system/sbin/su",
            "/system/bin/su",
            "/system/sd/xbin/su",
            "/su/bin/su",
            "/sbin/su",
            "/data/local/xbin/su",
            "/data/local/bin/su"
    };

    private static DecimalFormat sDecimalFormat = new DecimalFormat("0.0");
    private static boolean isPhoneRooted = initPhoneRootStatus();


    //获取手机SIM卡运营商
    public static String getSimOperator() {
        TelephonyManager telManager = getTelephonyManager();
        return telManager != null ? telManager.getSimOperatorName() : "";
    }

    //获取手机SIM卡IMSI(国际移动用户识别码)
    public static String getSimIMSI() {
        TelephonyManager telManager = getTelephonyManager();
        String sim = telManager != null ? telManager.getSubscriberId() : UNKNOWN_SIM;
        return TextUtils.isEmpty(sim) ? UNKNOWN_SIM : sim;
    }

    //
    //判断SIM卡是否存在
    public static boolean isSimExist() {
        TelephonyManager telManager = getTelephonyManager();
        int state = telManager != null ? telManager.getSimState() : TelephonyManager.SIM_STATE_UNKNOWN;
        return state != TelephonyManager.SIM_STATE_UNKNOWN && state != TelephonyManager.SIM_STATE_ABSENT;
    }

    private static TelephonyManager getTelephonyManager() {
        TelephonyManager telManager = null;
        try {
            telManager = (TelephonyManager)
                    Utility.getApplication().getSystemService(Context.TELEPHONY_SERVICE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return telManager;
    }

    public static String getDeviceId() {
        return getTelephonyManager().getDeviceId();
    }

    //获取RAM使用率
    public static float getRAMRate() {
        ActivityManager am = (ActivityManager)
                Utility.getApplication().getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(mi);

        long freeMemory = mi.availMem;
        long totalMemory;
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
            totalMemory = mi.totalMem;
        } else {
            totalMemory = readTotalMemFromFile();
        }
        float rate = (totalMemory - freeMemory) * 1.0f / totalMemory;
        return numberFormat(rate);
    }

    private static long readTotalMemFromFile() {
        long totalMemory = 0;
        try {
            BufferedReader reader = new BufferedReader(new FileReader("/proc/meminfo"));
            String load = reader.readLine();
            reader.close();
            String[] mem = load.split("\\s+");
            totalMemory = Long.parseLong(mem[1]) * BYTES_PER_KB;
        } catch (Exception e) {
        }
        return totalMemory;
    }

    //获取ROM使用率
    public static float getROMRate() {
        File dataDir = Environment.getDataDirectory();
        File externalStorageDir = Environment.getExternalStorageDirectory();
        long totalBytes = dataDir.getTotalSpace() + externalStorageDir.getTotalSpace();
        long freeBytes = dataDir.getFreeSpace() + externalStorageDir.getFreeSpace();
        float rate = (totalBytes - freeBytes) * 1.0f / totalBytes;
        return numberFormat(rate);
    }

    //获取CPU使用率
    public static float getCPURate() {
        long[] cpuTime1 = getCpuTime();
        try {
            Thread.sleep(666);
        } catch (Exception e) {
        }
        long[] cpuTime2 = getCpuTime();
        long totalTime = cpuTime2[0] - cpuTime1[0];
        long idleTime = cpuTime2[1] - cpuTime1[1];
        float cpuRate = (totalTime - idleTime) * 1.0f / totalTime;
        return numberFormat(cpuRate);
    }

    // 获取系统总CPU使用时间
    private static long[] getCpuTime() {
        String[] cpuInfos = null;
        try {
            BufferedReader reader = new BufferedReader(new FileReader("/proc/stat"));
            String load = reader.readLine(); //读取第一行的数据
            reader.close();
            cpuInfos = load.split("\\s+");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        long userTime = parseLong(cpuInfos[1]);
        long niceTime = parseLong(cpuInfos[2]);
        long systemTime = parseLong(cpuInfos[3]);
        long idleTime = parseLong(cpuInfos[4]);
        long iowaitTime = parseLong(cpuInfos[5]);
        long irqTime = parseLong(cpuInfos[6]);
        long softirqTime = parseLong(cpuInfos[7]);
        long stealstolenTime = parseLong(cpuInfos[8]);
        long guestTime = parseLong(cpuInfos[9]);

        long totalCpu = userTime + niceTime + systemTime + idleTime + iowaitTime
                + irqTime + softirqTime + stealstolenTime + guestTime;
        long[] times = new long[2];
        times[0] = totalCpu;
        times[1] = idleTime;
        return times;
    }

    //保留两位有效数字(四舍五入)
    private static float numberFormat(float value) {
        float rate = Math.round(value * 100) / 100.0f;
        //为保证界面显示两位数以更为美观，对占用率进行修正
        if (rate < 0.1f) {
            rate = 0.1f;
        } else if (rate > 0.99f) {
            rate = 0.99f;
        }
        //如认为显示数据必须符合当下实际，上述代码可以移除
        return rate;
    }

    private static long parseLong(String value) {
        return TextUtils.isEmpty(value) ? 0 : Long.parseLong(value);
    }

    public static String getDeviceModel() {
        String deviceModel = null;
        try {
            Class<?> classType = Class.forName("android.os.SystemProperties");
            Method getMethod = classType.getDeclaredMethod("get", String.class);
            deviceModel = (String) getMethod.invoke(classType, "ro.product.model");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return deviceModel;
    }

    //格式化数据
    public static String getStorageFormatValue(float value) {
        String result = "0B";
        if (value > SystemUtil.BYTES_PER_GB) {
            result = sDecimalFormat.format(value / SystemUtil.BYTES_PER_GB) + "GB";
        } else if (value > SystemUtil.BYTES_PER_MB) {
            result = sDecimalFormat.format(value / SystemUtil.BYTES_PER_MB) + "MB";
        } else if (value > SystemUtil.BYTES_PER_KB) {
            result = sDecimalFormat.format(value / SystemUtil.BYTES_PER_KB) + "KB";
        } else if (value > 0) {
            result = sDecimalFormat.format(value) + "B";
        }
        return result;
    }

    /**
     * 获取所有已安装的app
     */
    public static List<PackageInfo> getInstalledPackageInfos(PackageManager pm) {
        return pm.getInstalledPackages(PackageManager.GET_PERMISSIONS);
    }

    /**
     * 获取所有已安装的并且具有网络访问权限app
     */
    public static List<PackageInfo> getNetPackageInfos(PackageManager pm) {
        List<PackageInfo> netPackageInfos = new ArrayList<PackageInfo>();
        List<PackageInfo> packinfos = getInstalledPackageInfos(pm);
        for (PackageInfo info : packinfos) {
            if (isNetApp(info, pm)) {
                netPackageInfos.add(info);
            }
        }
        return netPackageInfos;
    }

    //判断是否是网络app
    public static boolean isNetApp(PackageInfo info, PackageManager pm) {
        return PackageManager.PERMISSION_GRANTED ==
                pm.checkPermission(Manifest.permission.INTERNET, info.packageName);
    }

    public static boolean isSystemApp(PackageInfo info) {
        return (info.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0
                || (info.applicationInfo.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0;
    }

    public static boolean isSystemApp(Context context, String packageName) {
        PackageManager pm = context.getPackageManager();
        boolean status = false;
        try {
            PackageInfo packageInfo = pm.getPackageInfo(packageName, 0);
            status = (packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM)
                    == ApplicationInfo.FLAG_SYSTEM;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return status;
    }

    public static boolean isRootAvailable() {
        return isPhoneRooted;
    }

    private static boolean initPhoneRootStatus() {
        for (int i = 0; i < SU_PATH.length; i++) {
            File file = new File(SU_PATH[i]);
            if (file.exists()) {
                return true;
            }
        }
        Process process = null;
        BufferedReader br = null;
        try {
            process = Runtime.getRuntime().exec("which su");
            br = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = br.readLine();
            if (!TextUtils.isEmpty(line) && line.contains("su")) {
                return true;
            }
        } catch (IOException e) {
        } finally {
            IOUtil.closeQuietly(br);
            if (process != null) {
                process.destroy();
            }
        }
        return false;
    }

    public static void uninstallAppByNormal(Context context, String pkgName) {
        Uri uri = Uri.parse("package:" + pkgName);
        Intent uninstallIntent = new Intent(Intent.ACTION_UNINSTALL_PACKAGE, uri);
        uninstallIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        context.startActivity(uninstallIntent);
    }

    public static PackageInfo getApkInfo(Context context, String path) {
        PackageManager pm = context.getPackageManager();
        PackageInfo info = pm.getPackageArchiveInfo(path, PackageManager.GET_ACTIVITIES);
        if (info != null) {
            return info;
        }
        return null;
    }

    public static String getCurProcessName(Context context) {
        int pid = android.os.Process.myPid();
        ActivityManager mActivityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> processes = mActivityManager.getRunningAppProcesses();
        if (processes != null && !processes.isEmpty()) {
            for (ActivityManager.RunningAppProcessInfo appProcess : processes) {
                if (appProcess != null && appProcess.pid == pid) {
                    return appProcess.processName;
                }
            }
        }
        return null;
    }

    public static boolean isAppInstalled(Context context, String pkgName) {
        PackageManager pm = context.getPackageManager();
        try {
            PackageInfo info = pm.getPackageInfo(pkgName, 0);
            return info != null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean checkSDCardMount(Context context, String mountPoint) {
        if (mountPoint == null) {
            return false;
        }
        String state;
        StorageManager storageManager = getStorageManager(context);
        if (storageManager != null) {
            try {
                state = ReflectUtil.invoke("getVolumeState",
                        storageManager,
                        String.class,
                        new Class[]{String.class},
                        new Object[]{mountPoint});
                return Environment.MEDIA_MOUNTED.equals(state);
            } catch (IllegalArgumentException e) {
            }
        }
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    public static String getInternalSdcardPath(Context context) {
        String path = null;
        StorageManager storageManager = getStorageManager(context);
        if (storageManager != null) {
            String[] storagePathList;
            try {
                storagePathList = ReflectUtil.invoke("getVolumePaths",
                        storageManager,
                        String[].class, null, null);
            } catch (IllegalArgumentException e) {
                return path;
            }
            if (storagePathList != null) {
                if (storagePathList.length >= 1) {
                    if (checkSDCardMount(context, storagePathList[0]))
                        path = storagePathList[0];
                }
            }
        }
        if (TextUtils.isEmpty(path)) {
            // for lower than android 4.0 , still using /mnt/sdcard
            path = Environment.getExternalStorageDirectory().getAbsolutePath();
        }
        return path;
    }

    public static String getExternalSdcardPath(Context context) {
        String path = null;
        StorageManager storageManager = getStorageManager(context);
        if (storageManager != null) {
            String[] storagePathList = ReflectUtil.invoke("getVolumePaths", storageManager, String[].class, null, null);
            if (storagePathList != null) {
                if (storagePathList.length >= 2) {
                    if (checkSDCardMount(context, storagePathList[1]))
                        path = storagePathList[1];
                }
            }
        }
        if (TextUtils.isEmpty(path)) {
            // for lower than android 4.0 , still using /mnt/sdcard
            path = Environment.getExternalStorageDirectory().getAbsolutePath();
        }
        return path;
    }

    private static StorageManager getStorageManager(Context context) {
        try {
            return (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
        } catch (Exception e) {
            //bugly#3025 java.lang.IllegalStateException   Failed to find running mount service
        }
        return null;
    }

    public static File getRootDir(Context context, String rootDirName) {
        if (context == null) {
            return null;
        }
        String path = null;
        if (PermissionChecker.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            path = getInternalSdcardPath(context);
            if (TextUtils.isEmpty(path)) {
                path = getExternalSdcardPath(context);
            }
        }
        if (TextUtils.isEmpty(path)) {
            path = context.getDir(rootDirName, Context.MODE_PRIVATE).getAbsolutePath();
        }
        File localDir;
        if (!TextUtils.isEmpty(path)) {
            localDir = new File(path + File.separator + rootDirName);
            if (!localDir.exists()) {
                localDir.mkdirs();
            }
            if (localDir.exists()) {
                return localDir;
            }
        }
        localDir = new File(context.getCacheDir(), rootDirName);
        if (!localDir.exists()) {
            localDir.mkdirs();
        }
        return localDir;
    }

    public static String getBuildDisplay() {
        if (Build.DISPLAY != null) {
            return Build.DISPLAY.toLowerCase().replace(" ", "");
        }
        return "";
    }

    public static boolean checkSDCard() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    public static long getSDFreeSize() {
        String mobile = Build.MANUFACTURER;
        String device = Build.DEVICE;

        if (mobile.equals("ZTE") && device.equals("U930HD") && Environment.getExternalStorageState().equals(Environment.MEDIA_REMOVED)) {
            return 5;
        }
        File path = Environment.getExternalStorageDirectory();
        StatFs sf = new StatFs(path.getPath());
        long blockSize = sf.getBlockSize();
        long freeBlocks = sf.getAvailableBlocks();
        return (freeBlocks * blockSize) / BYTES_PER_MB; // MB
    }


    public static long getAvailableMemory(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(memoryInfo);
        return memoryInfo.availMem;
    }


    public static boolean isRTL() {
        if (Build.VERSION.SDK_INT < 17) {
            Locale l = Locale.getDefault();
            String language = l.getLanguage();
            //String country = l.getCountry().toLowerCase();
            return language != null && language.trim().equalsIgnoreCase("ar");
        } else {
            return Utility.getApplication().getResources()
                    .getConfiguration().getLayoutDirection() == View.LAYOUT_DIRECTION_RTL;
        }
    }

    public static boolean isKeyguarding() {
        KeyguardManager keyguardManager = (KeyguardManager) Utility.getApplication()
                .getSystemService(Context.KEYGUARD_SERVICE);
        return keyguardManager.inKeyguardRestrictedInputMode();
    }
}
