package com.jj.base.common.input.util;

import android.content.Context;
import android.util.Log;

public class StatusBarHeightUtil {

    private static boolean init = false;
    private static int statusBarHeight = 50;

    private static final String STATUS_BAR_DEF_PACKAGE = "android";
    private static final String STATUS_BAR_DEF_TYPE = "dimen";
    private static final String STATUS_BAR_NAME = "status_bar_height";

    public static synchronized int getStatusBarHeight(final Context context) {
        if (!init) {
            int resourceId = context.getResources().
                    getIdentifier(STATUS_BAR_NAME, STATUS_BAR_DEF_TYPE, STATUS_BAR_DEF_PACKAGE);
            if (resourceId > 0) {
                statusBarHeight = context.getResources().getDimensionPixelSize(resourceId);
                init = true;
                Log.d("StatusBarHeightUtil",
                        String.format("Get status bar height %d", statusBarHeight));
            }
        }

        return statusBarHeight;
    }
}
