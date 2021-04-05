/**
 * Copyright (C) 2015 Fernando Cejas Open Source Project
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jj.base.baseclass;


import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.concurrent.atomic.AtomicReference;

import com.jj.base.utils.LogUtil;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.subscriptions.SubscriptionHelper;


public class DefaultSubscriber<T> implements Subscriber<T>, Disposable {
    private static final String TAG = "DefaultSubscriber";

    final AtomicReference<Subscription> s = new AtomicReference<Subscription>();
    private boolean autoRequest = true;

    public DefaultSubscriber(boolean autoRequest) {
        this.autoRequest = autoRequest;
    }

    public DefaultSubscriber() {
        autoRequest = true;
    }

    @Override
    public final void onSubscribe(Subscription s) {
        if (SubscriptionHelper.setOnce(this.s, s)) {
            onStart();
        }
    }

    protected void onStart() {
        if (autoRequest) {
            s.get().request(Long.MAX_VALUE);
        }
    }


    protected final void request(long n) {
        s.get().request(n);
    }

    protected final void cancel() {
        dispose();
    }

    @Override
    public final boolean isDisposed() {
        return s.get() == SubscriptionHelper.CANCELLED;
    }

    @Override
    public final void dispose() {
        SubscriptionHelper.cancel(s);
    }


    @Override
    public void onNext(T t) {
        // no-op by default.
         LogUtil.i(TAG, "onNext");
    }

    @Override
    public void onComplete() {
        // no-op by default.
         LogUtil.i(TAG, "onComplete");
    }

    @Override
    public void onError(Throwable exception) {
        // no-op by default.
         LogUtil.i(TAG, "onError");
    }

    public static DefaultSubscriber createEmptySubscriber() {
        return new DefaultSubscriber();
    }
}
