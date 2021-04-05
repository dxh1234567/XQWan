package com.jj.xqwan.base;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

import java.io.Serializable;

/**
 * Created By duXiaHui
 * on 2021/3/25
 */
public class LiveDataBaseBean implements Serializable {
    private boolean isNeed = true;

    public void setNeed(boolean need) {
        isNeed = need;
    }

    public boolean isNeed() {
        return isNeed;
    }


}
