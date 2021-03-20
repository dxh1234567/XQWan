package cn.jj.base.baseclass;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import androidx.multidex.MultiDexApplication;

import cn.jj.base.common.ThreadManager;
import cn.jj.base.utils.LogUtil;
import cn.jj.base.utils.Utility;

/**
 * Created by yangxl on 2017/12/25.
 */

public class BaseApplication extends MultiDexApplication {

    static BaseApplication application;

    @Override
    public void onCreate() {
        super.onCreate();
        application = this;
        Utility.Init(this);
        initActivityLifecycle();
        ThreadManager.startup();
    }

    private void initActivityLifecycle() {
        Application.ActivityLifecycleCallbacks activityLifecycleCallbacks = new Application.ActivityLifecycleCallbacks() {

            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                Utility.setCurrentActiveActivity(activity);
            }

            @Override
            public void onActivityStarted(Activity activity) {
                Utility.setCurrentActiveActivity(activity);

            }

            @Override
            public void onActivityResumed(Activity activity) {
                Utility.setCurrentActiveActivity(activity);

            }

            @Override
            public void onActivityPaused(Activity activity) {
            }

            @Override
            public void onActivityStopped(Activity activity) {
                if (Utility.getCurrentActiveActivity() == activity) {
                    Utility.setCurrentActiveActivity(null);
                }
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
            }

            @Override
            public void onActivityDestroyed(Activity activity) {
                if (Utility.getCurrentActiveActivity() == activity) {
                    Utility.setCurrentActiveActivity(null);
                }
            }
        };
        registerActivityLifecycleCallbacks(activityLifecycleCallbacks);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        LogUtil.appenderClose();
    }
}