package com.jj.base.common.view;

import android.app.Activity;
import android.os.Bundle;
import androidx.annotation.Nullable;

import com.jj.base.common.ThreadManager;


/**
 * Created by yangxl on 2017/9/13.
 * 空Activity，为了解决InputMethodManager造成的内存泄漏
 */

public class EmptyActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThreadManager.postDelayed(ThreadManager.THREAD_UI, () -> finish(), 300);
    }
}
