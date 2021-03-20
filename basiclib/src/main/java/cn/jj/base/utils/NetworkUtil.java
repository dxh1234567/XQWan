package cn.jj.base.utils;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.telephony.CellInfo;
import android.telephony.CellInfoCdma;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellInfoWcdma;
import android.telephony.CellSignalStrengthWcdma;
import android.telephony.TelephonyManager;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.util.List;

import androidx.core.app.ActivityCompat;

import static android.content.Context.WIFI_SERVICE;

/**
 * Created by yangxl on 2016/12/17.
 */

public class NetworkUtil {
    private static final byte TYPE_NO_NETWORK = 0;
    private static final byte TYPE_WIFI = 1;
    private static final byte TYPE_2G = 2;
    private static final byte TYPE_3G = 3;
    private static final byte TYPE_4G = 4;
    private static final byte TYPE_UNKNOWN = 10;

    public static final int SIGNAL_STRENGTH_DEFALUT = -1;
    public static final int SIGNAL_STRENGTH_NONE_OR_UNKNOWN = 0;
    public static final int SIGNAL_STRENGTH_POOR = 1;
    public static final int SIGNAL_STRENGTH_MODERATE = 2;
    public static final int SIGNAL_STRENGTH_GOOD = 3;
    public static final int SIGNAL_STRENGTH_GREAT = 4;

    private static final int WCDMA_SIGNAL_STRENGTH_MODERATE = 5;
    private static final int WCDMA_SIGNAL_STRENGTH_GOOD = 8;
    private static final int WCDMA_SIGNAL_STRENGTH_GREAT = 12;

    //@note 异常后默认返回true
    public static boolean isNetworkAvailable(Context context) {
        return isNetworkAvailable(context, true);
    }

    private static boolean isNetworkAvailable(Context context, boolean bDefaultRetOfException) {
        if (context == null) {
            return false;
        }
        ConnectivityManager conmgr = null;
        try {
            conmgr = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
        } catch (Exception e) {
        }

        if (conmgr == null) {
            return false;
        }

        try {
            NetworkInfo net3g = conmgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            if (net3g != null) {
                State mobile = net3g.getState();// 显示3G网络连接状态
                if (mobile == State.CONNECTED || mobile == State.CONNECTING)
                    return true;
            }

            NetworkInfo netwifi = conmgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (netwifi != null) {
                State wifi = netwifi.getState(); // wifi
                // 如果3G网络和wifi网络都未连接，且不是处于正在连接状态 则进入Network Setting界面 由用户配置网络连接
                if (wifi == State.CONNECTED || wifi == State.CONNECTING)
                    return true;
            }

            NetworkInfo info = conmgr.getActiveNetworkInfo();
            if (info != null) {
                return info.isConnected();
            }
        } catch (Throwable e) {
            return bDefaultRetOfException;
        }

        return false;
    }

    private static byte getNetworkType(Context context) {
        byte type = TYPE_UNKNOWN;
        try {
            ConnectivityManager connectMgr = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo info = connectMgr.getActiveNetworkInfo();

            if (info != null) {
                switch (info.getType()) {
                    case ConnectivityManager.TYPE_WIFI:
                        type = TYPE_WIFI;
                        break;
                    case ConnectivityManager.TYPE_MOBILE:
                    case ConnectivityManager.TYPE_MOBILE_MMS:
                    case ConnectivityManager.TYPE_MOBILE_SUPL:
                    case ConnectivityManager.TYPE_MOBILE_DUN:
                    case ConnectivityManager.TYPE_MOBILE_HIPRI:
                        type = getNetworkSubType(info);
                        break;
                }
            } else {
                type = TYPE_NO_NETWORK;
            }
        } catch (Exception e) {
        } catch (Error e) {
        }

        return type;
    }

    private static byte getNetworkSubType(NetworkInfo info) {
        byte type = TYPE_UNKNOWN;
        switch (info.getSubtype()) {
            case TelephonyManager.NETWORK_TYPE_GPRS:
            case TelephonyManager.NETWORK_TYPE_EDGE:
            case TelephonyManager.NETWORK_TYPE_CDMA:
            case TelephonyManager.NETWORK_TYPE_1xRTT:
            case TelephonyManager.NETWORK_TYPE_IDEN:
                type = TYPE_2G;
                break;
            case TelephonyManager.NETWORK_TYPE_UMTS:
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
            case TelephonyManager.NETWORK_TYPE_HSDPA:
            case TelephonyManager.NETWORK_TYPE_HSUPA:
            case TelephonyManager.NETWORK_TYPE_HSPA:
            case TelephonyManager.NETWORK_TYPE_EVDO_B:
            case TelephonyManager.NETWORK_TYPE_EHRPD:
            case TelephonyManager.NETWORK_TYPE_HSPAP:
                type = TYPE_3G;
                break;
            case TelephonyManager.NETWORK_TYPE_LTE:
                type = TYPE_4G;
                break;
            case TelephonyManager.NETWORK_TYPE_UNKNOWN:
                // TYPE_UNKNOWN
                break;
        }
        return type;
    }

    public static boolean isWiFiNetwork(Context context) {
        return getNetworkType(context) == TYPE_WIFI;
    }

    public static boolean isMobileNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        try {
            Method getMobileDataEnabledMethod = ConnectivityManager.class
                    .getDeclaredMethod("getMobileDataEnabled");
            Boolean isEnabled = (Boolean) getMobileDataEnabledMethod
                    .invoke(connectivityManager);
            return isEnabled.booleanValue();
        } catch (Exception e) {
            if (connectivityManager != null) {
                NetworkInfo[] infos = connectivityManager.getAllNetworkInfo();
                if (infos != null) {
                    for (NetworkInfo networkInfo : infos) {

                        if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE && networkInfo.isConnected()) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public static String getWifiName(Context context) {
        try {
            WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(WIFI_SERVICE);
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            String name;
            if (wifiInfo != null && wifiInfo.getNetworkId() != -1) {
                name = wifiInfo.getSSID();
                if (name == null || name.length() == 0) {
                    return "wifi";
                }
                final int length = name.length();
                int start = 0, end = length - 1;
                while (start < length && name.charAt(start) == '"')
                    start++;
                while (end > start && name.charAt(end) == '"')
                    end--;
                if (start == 0 && end == length - 1)
                    return name;
                else
                    return name.substring(start, end + 1);
            }
        } catch (Throwable e) {

        }
        return "wifi";
    }

    public static int pingIp(String netAddress) {
        String delay = null;
        int result = 1000;//默认值
        Process p;
        BufferedReader buf = null;
        try {
            p = Runtime.getRuntime().exec("ping -c 4 -s 128 " + netAddress);
            buf = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String str;
            while ((str = buf.readLine()) != null) {
                if (str.contains("avg")) {
                    int i = str.indexOf("/", 20);
                    int j = str.indexOf(".", i);
                    delay = str.substring(i + 1, j);
                }
            }
            result = Integer.parseInt(delay);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IOUtil.closeQuietly(buf);
        }
        return result;
    }

    //移动网络信号强弱
    private static int getMonetSignalState(Context context) {
        if (Build.VERSION.SDK_INT < 17) {
            return SIGNAL_STRENGTH_DEFALUT;
        }
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return SIGNAL_STRENGTH_DEFALUT;
        }
        List<CellInfo> cellInfos = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getAllCellInfo();
        if (cellInfos == null || cellInfos.isEmpty()) {
            return SIGNAL_STRENGTH_DEFALUT;
        }
        CellInfo cellInfo = cellInfos.get(0);
        if (!cellInfo.isRegistered()) {
            return SIGNAL_STRENGTH_DEFALUT;
        }
        if (cellInfo instanceof CellInfoGsm) {
            return ((CellInfoGsm) cellInfo).getCellSignalStrength().getLevel();
        } else if (cellInfo instanceof CellInfoCdma) {
            return ((CellInfoCdma) cellInfo).getCellSignalStrength().getLevel();
        } else if (cellInfo instanceof CellInfoLte) {
            return ((CellInfoLte) cellInfo).getCellSignalStrength().getLevel();
        } else if (cellInfo instanceof CellInfoWcdma) {
            if (Build.VERSION.SDK_INT >= 18) {
                return ((CellInfoWcdma) cellInfo).getCellSignalStrength().getLevel();
            }
            CellSignalStrengthWcdma cellSignalStrengthWcdma = (CellSignalStrengthWcdma) ReflectUtil
                    .obtainNonStaticFieldValue(cellInfo, "mCellSignalStrengthWcdma");
            if (cellSignalStrengthWcdma == null) {
                return SIGNAL_STRENGTH_DEFALUT;
            }
            Integer asu = (Integer) ReflectUtil.obtainNonStaticFieldValue
                    (cellSignalStrengthWcdma, "mSignalStrength");
            if (asu == null) {
                return SIGNAL_STRENGTH_DEFALUT;
            }
            if (asu <= 2 || asu == 99) {
                return SIGNAL_STRENGTH_NONE_OR_UNKNOWN;
            } else if (asu >= WCDMA_SIGNAL_STRENGTH_GREAT) {
                return SIGNAL_STRENGTH_GREAT;
            } else if (asu >= WCDMA_SIGNAL_STRENGTH_GOOD) {
                return SIGNAL_STRENGTH_GOOD;
            } else if (asu >= WCDMA_SIGNAL_STRENGTH_MODERATE) {
                return SIGNAL_STRENGTH_MODERATE;
            } else {
                return SIGNAL_STRENGTH_POOR;
            }
        }
        return SIGNAL_STRENGTH_DEFALUT;
    }

    //wifi网络信号强弱
    private static int getWifiSignalState(Context context) {
        int state = getWifiRssi(context);
        if (state > -50 && state <= 0) {
            return SIGNAL_STRENGTH_GREAT;
        } else if (state > -70 && state <= -50) {
            return SIGNAL_STRENGTH_GOOD;
        } else if (state > -100 && state <= -70) {
            return SIGNAL_STRENGTH_MODERATE;
        } else if (state <= -100) {
            return SIGNAL_STRENGTH_POOR;
        }
        return SIGNAL_STRENGTH_NONE_OR_UNKNOWN;
    }

    private static int getWifiRssi(Context context) {
        return ((WifiManager) context.getSystemService(WIFI_SERVICE)).getConnectionInfo().getRssi();
    }

    public static int getSignalState(Context context) {
        return isWiFiNetwork(context) ? getWifiSignalState(context) : getMonetSignalState(context);
    }
}
