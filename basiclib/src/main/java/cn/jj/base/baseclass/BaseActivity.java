package cn.jj.base.baseclass;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.umeng.analytics.MobclickAgent;

import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import cn.jj.base.common.helper.FragmentBackHandler;
import cn.jj.base.utils.ApplicationUtils;
import cn.jj.base.utils.FocusedViewLeaker;
import cn.jj.basiclib.R;

public abstract class BaseActivity extends AppCompatActivity {
    public static final String ACTION_FLOAT_BTN_SHOWN = "custom.action.ACTION_FLOAT_BTN_SHOWN";
    public static final String KEY_FLOAT_BTN_SHOWN = "key_float_btn_shown";

    protected Toolbar toolbar;
    private boolean destoyed;
    private Method noteStateNotSavedMethod;
    private Object fragmentMgr;
    private String[] activityClassName = {"Activity", "FragmentActivity"};
    private LocalBroadcastManager localBroadcastManager;
    private boolean isHideFloatBtn = false;

    @CallSuper
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        try {
            super.onCreate(savedInstanceState);
        } catch (Exception e) {
            //恢复时，可能导致异常，此处直接catch。比如：Fragment中使用findNavController，在恢复时为空
            finish();
            return;
        }
        if (savedInstanceState != null && !canRestore()) {
            finish();
            return;
        }
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        setContentView(obtainLayoutResID());
        destoyed = false;
        initTitleView();
        localBroadcastManager = LocalBroadcastManager.getInstance(this);
    }

    protected abstract int obtainLayoutResID();

    protected String getActivityTitle() {
        return "";
    }

    protected Drawable getNavigationIcon() {
        return ContextCompat.getDrawable(this, R.drawable.ic_back);
    }

    protected View.OnClickListener getNavigationOnClickListener() {
        return v -> onBackPressed();
    }

    private void initTitleView() {
        toolbar = findToolbar();
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            TextView titleTx = findViewById(R.id.custom_title);
            if (titleTx != null) {
                titleTx.setText(getActivityTitle());
            } else {
                toolbar.setTitle(getActivityTitle());
            }
            Drawable icon = getNavigationIcon();
            toolbar.setNavigationIcon(icon);
            toolbar.setNavigationOnClickListener(icon == null ? null : getNavigationOnClickListener());
        }
    }

    @CallSuper
    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
        if (!getPackageName().equalsIgnoreCase(ApplicationUtils.getTopActivityPackageName(this))) {
            isHideFloatBtn = true;
            sendLocalBroadcast(false);
        }
    }

    @CallSuper
    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        if (isHideFloatBtn) {
            sendLocalBroadcast(true);
            isHideFloatBtn = false;
        }
    }

    @Override
    @CallSuper
    public void onBackPressed() {
        try {
            if (!FragmentBackHandler.INSTANCE.handleBackPress(this)) {
                if (!dealBack()) {
                    super.onBackPressed();
                }
            }
        } catch (Exception e) {
            //java.lang.IllegalStateException: Can not perform this action after onSaveInstanceState
            finish();
        }
    }

    protected boolean dealBack() {
        return false;
    }

    protected void back() {
        super.onBackPressed();
    }

    protected boolean canRestore() {
        return true;
    }

    @CallSuper
    @Override
    protected void onStop() {
        super.onStop();
    }

    @CallSuper
    @Override
    protected void onStart() {
        super.onStart();
    }

    public boolean isDestroyed() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
            return super.isDestroyed();
        else
            return destoyed;
    }

    @CallSuper
    @Override
    protected void onDestroy() {
        super.onDestroy();
        destoyed = true;
        FocusedViewLeaker.fixFocusedViewLeak(getApplication());
    }

    //@note 解决崩溃：IllegalStateException: Can not perform this action after onSaveInstanceState
    // http://blog.csdn.net/edisonchang/article/details/49873669
    @CallSuper
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        invokeFragmentManagerNoteStateNotSaved();
    }

    private void invokeFragmentManagerNoteStateNotSaved() {
        try {
            if (noteStateNotSavedMethod != null && fragmentMgr != null) {
                noteStateNotSavedMethod.invoke(fragmentMgr);
                return;
            }
            Class cls = getClass();
            do {
                cls = cls.getSuperclass();
            } while (!(activityClassName[0].equals(cls.getSimpleName())
                    || activityClassName[1].equals(cls.getSimpleName())));

            Field fragmentMgrField = prepareField(cls, "mFragments");
            if (fragmentMgrField != null) {
                fragmentMgr = fragmentMgrField.get(this);
                noteStateNotSavedMethod = getDeclaredMethod(fragmentMgr, "noteStateNotSaved");
                if (noteStateNotSavedMethod != null) {
                    noteStateNotSavedMethod.invoke(fragmentMgr);
                }
            }
        } catch (Exception ex) {
        }
    }

    private Field prepareField(Class<?> c, String fieldName) throws NoSuchFieldException {
        while (c != null) {
            try {
                Field f = c.getDeclaredField(fieldName);
                f.setAccessible(true);
                return f;
            } finally {
                c = c.getSuperclass();
            }
        }
        throw new NoSuchFieldException();
    }

    private Method getDeclaredMethod(Object object, String methodName, Class<?>... parameterTypes) {
        Method method = null;
        for (Class<?> clazz = object.getClass(); clazz != Object.class; clazz = clazz.getSuperclass()) {
            try {
                method = clazz.getDeclaredMethod(methodName, parameterTypes);
                return method;
            } catch (Exception e) {
            }
        }
        return null;
    }

    private Toolbar findToolbar() {
        View view = findViewById(R.id.custom_toolbar);
        if (view instanceof Toolbar) {
            return (Toolbar) view;
        }
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View child = ((ViewGroup) view).getChildAt(i);
                if (child instanceof Toolbar) {
                    return (Toolbar) child;
                }
            }
        }
        return null;
    }

    private void sendLocalBroadcast(boolean extra) {
        Intent intent = new Intent(ACTION_FLOAT_BTN_SHOWN);
        intent.putExtra(KEY_FLOAT_BTN_SHOWN, extra);
        localBroadcastManager.sendBroadcast(intent);
    }

    @Override
    public void dump(@NonNull String prefix, @Nullable FileDescriptor fd, @NonNull PrintWriter writer, @Nullable String[] args) {
        //解决部分机型在FragmentManager.tostring时的空指针异常
        //https://bugly.qq.com/v2/crash-reporting/crashes/30ead2fefd/24905/report?pid=1&crashDataType=undefined&start=0
        try {
            super.dump(prefix, fd, writer, args);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
