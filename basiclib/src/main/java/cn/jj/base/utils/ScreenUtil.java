package cn.jj.base.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.os.Build;
import androidx.annotation.DimenRes;
import androidx.annotation.IntegerRes;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.WindowManager;

import java.lang.reflect.Method;

import static android.util.DisplayMetrics.DENSITY_HIGH;

public class ScreenUtil {

    public static final String TAG = "ScreenUtil";
    private static final int DP_TO_PX = TypedValue.COMPLEX_UNIT_DIP;
    private static final int SP_TO_PX = TypedValue.COMPLEX_UNIT_SP;
    private static final int PX_TO_DP = TypedValue.COMPLEX_UNIT_MM + 1;
    private static final int PX_TO_SP = TypedValue.COMPLEX_UNIT_MM + 2;
    private static final int DP_TO_PX_SCALE_H = TypedValue.COMPLEX_UNIT_MM + 3;
    private static final int DP_SCALE_H = TypedValue.COMPLEX_UNIT_MM + 4;
    private static final int DP_TO_PX_SCALE_W = TypedValue.COMPLEX_UNIT_MM + 5;

    private final static float BASE_SCREEN_WIDH = 720f;
    private final static float BASE_SCREEN_HEIGHT = 1280f;
    private final static float BASE_SCREEN_DENSITY = 2f;
    private static Float sScaleW, sScaleH;

    private static int sNavigationHeight = Integer.MIN_VALUE;

    private static DisplayMetrics getDisplayMetrics() {
        Context context = Utility.getApplication().getApplicationContext();
        if (context != null) {
            Resources res = context.getResources();
            if (res != null) {
                return res.getDisplayMetrics();
            }
        }
        return null;
    }

    public static float getDensity() {
        if (getDisplayMetrics() != null) {
            return getDisplayMetrics().density;
        } else {
            return 1.5f;
        }
    }

    public static int getDensityDpi() {
        if (getDisplayMetrics() != null) {
            return getDisplayMetrics().densityDpi;
        } else {
            return DENSITY_HIGH;
        }
    }

    /**
     * 如果要计算的值已经经过dip计算，则使用此结果，如果没有请使用getScaleFactorWithoutDip
     */
    public static float getScaleFactorW() {
        if (sScaleW == null) {
            sScaleW = (getScreenWidth() * BASE_SCREEN_DENSITY) / (getDensity() * BASE_SCREEN_WIDH);
        }
        return sScaleW;
    }

    public static float getScaleFactorH() {
        if (sScaleH == null) {
            sScaleH = (getScreenHeight() * BASE_SCREEN_DENSITY)
                    / (getDensity() * BASE_SCREEN_HEIGHT);
        }
        return sScaleH;
    }

    public static int getScreenWidth() {
        DisplayMetrics metrics = getDisplayMetrics();
        if (metrics == null) {
            return 0;
        }
        return metrics.widthPixels;
    }

    public static int getScreenHeight() {
        DisplayMetrics metrics = getDisplayMetrics();
        if (metrics == null) {
            return 0;
        }
        return metrics.heightPixels;
    }

    private static float applyDimension(int unit, float value, DisplayMetrics metrics) {
        if (metrics == null) {
            return 0;
        }
        switch (unit) {
            case DP_TO_PX:
            case SP_TO_PX:
                return TypedValue.applyDimension(unit, value, metrics);
            case PX_TO_DP:
                return value / metrics.density;
            case PX_TO_SP:
                return value / metrics.scaledDensity;
            case DP_TO_PX_SCALE_H:
                return TypedValue.applyDimension(DP_TO_PX, value * getScaleFactorH(), metrics);
            case DP_SCALE_H:
                return value * getScaleFactorH();
            case DP_TO_PX_SCALE_W:
                return TypedValue.applyDimension(DP_TO_PX, value * getScaleFactorW(), metrics);
        }
        return 0;
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dp2px(float dpValue) {
        return (int) applyDimension(DP_TO_PX, dpValue, getDisplayMetrics());
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dp(float pxValue) {
        return (int) applyDimension(PX_TO_DP, pxValue, getDisplayMetrics());
    }

    /**
     * 将px值转换为sp值，保证文字大小不变
     */
    public static int px2sp(float pxValue) {
        return (int) applyDimension(PX_TO_SP, pxValue, getDisplayMetrics());
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     */
    public static int sp2px(float spValue) {
        return (int) applyDimension(SP_TO_PX, spValue, getDisplayMetrics());

    }

    public static int getNavigationHeight() {
        if (sNavigationHeight == Integer.MIN_VALUE) {
            if (!isExceptProcessNavigationBar()) {
                synchronized (ScreenUtil.class) {
                    sNavigationHeight = getNavigationHeightFromResource(Utility.getApplication());
                }
            } else {
                sNavigationHeight = 0;
            }
        }
        return sNavigationHeight;
    }

    private static int getNavigationHeightFromResource(Context context) {

        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        int navigationBarHeight = 0;
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier("config_showNavigationBar",
                "bool", "android");
        if (resourceId > 0) {
            boolean hasNav = resources.getBoolean(resourceId);
            if (hasNav) {
                resourceId = resources.getIdentifier("navigation_bar_height",
                        "dimen", "android");
                if (resourceId > 0) {
                    navigationBarHeight = resources
                            .getDimensionPixelSize(resourceId);
                }
            }
        }

        if (navigationBarHeight <= 0) {
            DisplayMetrics dMetrics = new DisplayMetrics();
            display.getMetrics(dMetrics);
            int screenHeight = Math.max(dMetrics.widthPixels, dMetrics.heightPixels);
            int realHeight = 0;
            try {
                Method mt = display.getClass().getMethod("getRealSize", Point.class);
                Point size = new Point();
                mt.invoke(display, size);
                realHeight = Math.max(size.x, size.y);
            } catch (NoSuchMethodException e) {
                Method mt = null;
                try {
                    mt = display.getClass().getMethod("getRawHeight");
                } catch (NoSuchMethodException e2) {
                    try {
                        mt = display.getClass().getMethod("getRealHeight");
                    } catch (NoSuchMethodException e3) {
                    }
                }
                if (mt != null) {
                    try {
                        realHeight = (int) mt.invoke(display);
                    } catch (Exception e1) {
                    }
                }
            } catch (Exception e) {
            }
            // 如果是橫屏,这种计算方式是不是会有问题.
            navigationBarHeight = realHeight - screenHeight;
        }

        return navigationBarHeight;
    }

    public static boolean isExceptProcessNavigationBar() {
        String deviceModel = SystemUtil.getDeviceModel();
        if (!TextUtils.isEmpty(deviceModel)) {
            if (deviceModel.equals("ZTE U950") || deviceModel.equals("ZTE U817") || deviceModel.equals("ZTE V955")
                    || deviceModel.equals("ZTE Q505T") || deviceModel.equals("GT-S5301L")
                    || deviceModel.equals("LG-E425f") || deviceModel.equals("GT-S5303B")
                    || deviceModel.equals("I-STYLE2.1") || deviceModel.equals("SCH-S738C")
                    || deviceModel.equals("S120 LOIN") || deviceModel.equals("START 765")
                    || deviceModel.equals("LG-E425j") || deviceModel.equals("Archos 50 Titanium")
                    || deviceModel.equals("ZTE N880G") || deviceModel.equals("O+ 8.91")
                    || deviceModel.equals("ZP330") || deviceModel.equals("Wise+")
                    || deviceModel.equals("HUAWEI Y511-U30") || deviceModel.equals("Che1-L04")
                    || deviceModel.equals("ASUS_T00I") || deviceModel.equals("Lenovo A319")
                    || deviceModel.equals("Bird 72_wet_a_jb3") || deviceModel.equals("Sendtel Wise")
                    || deviceModel.equals("cross92_3923") || deviceModel.equals("HTC X920e")
                    || deviceModel.equals("ONE TOUCH 4033X") || deviceModel.equals("GSmart Roma")
                    || deviceModel.equals("A74B") || deviceModel.equals("Doogee Y100 Pro")
                    || deviceModel.equals("M4 SS1050") || deviceModel.equals("Ibiza_F2")
                    || deviceModel.equals("Lenovo P70-A") || deviceModel.equals("Y635-L21")
                    || deviceModel.equals("hi6210sft") || deviceModel.equals("TurboX6Z")
                    || deviceModel.equals("ONE TOUCH 4015A") || deviceModel.equals("LENNY2")
                    || deviceModel.equals("A66A*") || deviceModel.equals("ONE TOUCH 4033X")
                    || deviceModel.equals("LENNY2") || deviceModel.equals("PGN606")
                    || deviceModel.equals("MEU AN400") || deviceModel.equals("ONE TOUCH 4015X")
                    || deviceModel.equals("4013M") || deviceModel.equals("HUAWEI MT1-T00")
                    || deviceModel.equals("CHM-UL00")) {
                return true;
            }
        }
        return "OPPO".equals(Build.MANUFACTURER) || "Meizu".equals(Build.MANUFACTURER);
    }

    /**
     * 获取状态栏的高度
     */
    public static int getStatusHeight() {
        Context context = Utility.getApplication().getApplicationContext();
        int statusHeight = -1;
        try {
            Class<?> clazz = Class.forName("com.android.internal.R$dimen");
            int height = Integer.parseInt(clazz.getField("status_bar_height")
                    .get(null).toString());
            statusHeight = context.getResources().getDimensionPixelSize(height);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return statusHeight;
    }

    public static int getDimensionPixelSize(@DimenRes int id) {
        Context context = Utility.getApplication().getApplicationContext();
        return context.getResources().getDimensionPixelSize(id);
    }

    public static int getInteger(@IntegerRes int id) {
        Context context = Utility.getApplication().getApplicationContext();
        return context.getResources().getInteger(id);
    }
}