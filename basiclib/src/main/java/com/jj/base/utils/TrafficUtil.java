package com.jj.base.utils;

import android.Manifest;
import android.app.usage.NetworkStats;
import android.app.usage.NetworkStatsManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.TrafficStats;
import android.os.Build;
import android.os.RemoteException;
import android.os.SystemClock;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import android.telephony.TelephonyManager;

/**
 * Created by wangym on 2018/1/19.
 */

public class TrafficUtil {
    private static final int DATA_TYPE_RECEIVED = 0;
    private static final int DATA_TYPE_TRANSMITTED = 1;

    @RequiresApi(api = Build.VERSION_CODES.M)
    private static long getUidBytes(Context context, int uid, int netWorkType, int dataType) {
        NetworkStatsManager networkStatsManager = (NetworkStatsManager) context.getSystemService(Context.NETWORK_STATS_SERVICE);
        NetworkStats networkStats;
        long currentTimeMillis = System.currentTimeMillis();
        int result = 0;
        try {
            networkStats = networkStatsManager.querySummary(netWorkType,
                    getSubscriberId(context, netWorkType),
                    currentTimeMillis - SystemClock.elapsedRealtime(),
                    currentTimeMillis);
            NetworkStats.Bucket bucket = new NetworkStats.Bucket();
            int bucketId;
            if (null != networkStats) {
                while (networkStats.hasNextBucket()) {
                    networkStats.getNextBucket(bucket);
                    bucketId = bucket.getUid();
                    if (uid == bucketId) {
                        result += (dataType == DATA_TYPE_RECEIVED ? bucket.getRxBytes() : bucket.getTxBytes());
                    }
                }
            }
        } catch (RemoteException e) {
            return -1;
        }
        return result;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public static long getUidRxBytesMobile(Context context, int uid) {
        return getUidBytes(context, uid, ConnectivityManager.TYPE_MOBILE, DATA_TYPE_RECEIVED);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public static long getUidTxBytesMobile(Context context, int uid) {
        return getUidBytes(context, uid, ConnectivityManager.TYPE_MOBILE, DATA_TYPE_TRANSMITTED);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public static long getUidRxBytesWifi(Context context, int uid) {
        return getUidBytes(context, uid, ConnectivityManager.TYPE_WIFI, DATA_TYPE_RECEIVED);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public static long getUidTxBytesWifi(Context context, int uid) {
        return getUidBytes(context, uid, ConnectivityManager.TYPE_WIFI, DATA_TYPE_TRANSMITTED);
    }

    private static String getSubscriberId(Context context, int networkType) {
        if (ConnectivityManager.TYPE_MOBILE == networkType) {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                return tm.getSubscriberId();
            }
        }
        return "";
    }

    private static long getTotalRxBytes(int uid) {
        return TrafficStats.getUidRxBytes(uid) == TrafficStats.UNSUPPORTED ? 0 : TrafficStats.getTotalRxBytes();
    }

    private static long getTotalTxBytes(int uid) {
        return TrafficStats.getUidTxBytes(uid) == TrafficStats.UNSUPPORTED ? 0 : TrafficStats.getTotalTxBytes();
    }

    public static long getUidRxBytes(Context context, int uid) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return Math.max(0, getUidRxBytesMobile(context, uid)) + Math.max(0, getUidRxBytesWifi(context, uid));
        } else {
            return getTotalRxBytes(uid);
        }
    }

    public static long getUidTxBytes(Context context, int uid) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return Math.max(0, getUidTxBytesMobile(context, uid)) + Math.max(0, getUidTxBytesWifi(context, uid));
        } else {
            return getTotalTxBytes(uid);
        }
    }
}
