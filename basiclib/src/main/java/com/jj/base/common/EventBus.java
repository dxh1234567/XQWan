package com.jj.base.common;

/**
 * Created by yangxl on 2016/11/21.
 *
 * @note 只适用于动态注册或自定义的事件，不适用于静态广播事件
 * 用于解耦事件通知类
 * @note !!!每个事件可带两个参数{value1,value2}，定义一个事件类型时，必须说明这两个参数的含义!!!
 */


import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArraySet;

public class EventBus {

    public static final String TAG = "EventBus";

    private static EventBus sEventBus;
    private final ConcurrentMap<Integer, Set<Listener>> mNotificationMap =
            new ConcurrentHashMap<>();

    /**
     * 回调接口
     */
    public interface Listener<T, Param> {
        void notify(int receiveType, T value1, Param value2);
    }

    public void startup() {

    }

    public void cleanup() {
        if (sEventBus != null) {
            sEventBus.clearAll();
            sEventBus = null;
        }
    }

    public static EventBus getInstance() {
        if (sEventBus == null) {
            sEventBus = new EventBus();
        }
        return sEventBus;
    }

    public void addListener(int type, Listener listener) {
        if (listener == null) {
            throw new NullPointerException(" listener must not be null!");
        }
        Set<Listener> value = mNotificationMap.get(type);
        if (value == null) {
            value = new CopyOnWriteArraySet<>();
            mNotificationMap.put(type, value);
        }
        value.add(listener);
    }

    public void removeListener(int type, Listener listener) {
        Set<Listener> value = mNotificationMap.get(type);
        if (value != null) {
            value.remove(listener);
            if (value.isEmpty()) {
                mNotificationMap.remove(type);
            }
        }
    }

    public void clearListener(int type) {
        Set<Listener> value = mNotificationMap.get(type);
        if (value != null) {
            value.clear();
            mNotificationMap.remove(type);
        }
    }

    private void clearAll() {
        for (Map.Entry<Integer, Set<Listener>> entry : mNotificationMap.entrySet()) {
            Set<Listener> value = entry.getValue();
            if (value != null) {
                value.clear();
            }
        }
        mNotificationMap.clear();
    }

    public <T, Param> void notify(int type, T arg1, Param arg2) {
        Set<Listener> listenerList = mNotificationMap.get(type);
        if (listenerList != null && !listenerList.isEmpty()) {
            notify(listenerList, type, arg1, arg2);
        }
    }

    private <T, Param> void notify(Set<Listener> listenerList, int type, T arg1, Param arg2) {
        Iterator<Listener> it = listenerList.iterator();
        while (it.hasNext()) {
            Listener<T, Param> listener = it.next();
            if (listener != null) {
                listener.notify(type, arg1, arg2);
            }
        }

    }
}
