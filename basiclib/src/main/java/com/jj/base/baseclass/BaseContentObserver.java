package com.jj.base.baseclass;

import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;

import com.jj.base.common.ThreadManager;


/**
 * Created by yangxl on 2017/8/3.
 */

public abstract class BaseContentObserver extends ContentObserver {
    protected Context context;

    protected ContentResolver resolver;

    public BaseContentObserver(Context context) {
        super(ThreadManager.getHandler(ThreadManager.THREAD_UI));
        this.context = context;
        resolver = context.getContentResolver();
    }

    //注册观察
    public void startObserver() {
        Uri[] uris = getUris();
        boolean notifyForDescendants = notifyForDescendants();
        for (Uri uri : uris) {
            resolver.registerContentObserver(uri, notifyForDescendants, this);
        }
    }

    //解除观察
    public void stopObserver() {
        resolver.unregisterContentObserver(this);
    }

    public abstract Uri[] getUris();

    public abstract boolean notifyForDescendants();
}
