
package com.jj.base.common.cache;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.text.TextUtils;

import com.jj.base.common.ThreadManager;
import com.jj.base.utils.FileUtils;
import com.jj.base.utils.IOUtil;
import com.jj.base.utils.LogUtil;
import com.jj.base.utils.MD5Util;
import com.jj.base.utils.SystemUtil;
import com.jj.base.utils.Utility;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

/**
 * 文件缓存管理，所有读写文件都必须确保在THREAD_IO线程下进行，避免使用锁
 */
public class FileCacheManager implements ICacheManager {

    private static final String TAG = "FileCacheManager";
    private static final long DEFAULT_EXPIRE_MILLLON_SECOND = TimeUnit.DAYS.toMillis(7);
    private static final String CACHE_FOLDER_NAME = "boost";
    private long mDefaultExpireMillionSecond = DEFAULT_EXPIRE_MILLLON_SECOND;
    private File mDefaultDataDir = null;
    private File mDefaultImgDir = null;

    static class Holder {
        final static FileCacheManager sInstance = new FileCacheManager();
    }

    public static FileCacheManager getInstance() {
        return Holder.sInstance;
    }

    private FileCacheManager() {
        File file = SystemUtil.getRootDir(Utility.getApplication(), CACHE_FOLDER_NAME);
        if (file != null) {
            mDefaultDataDir = new File(file, ".data");
            mDefaultImgDir = new File(file, ".image");
        }

        if (isParentDirAvailable()) {
            if (!mDefaultDataDir.exists())
                mDefaultDataDir.mkdirs();
            if (!mDefaultImgDir.exists())
                mDefaultImgDir.mkdirs();
        }
        checkExpired();
    }

    public void checkExpired() {
        ThreadManager.post(ThreadManager.THREAD_IO, () -> {
            if (mDefaultImgDir != null && mDefaultImgDir.exists()) {
                File[] files = mDefaultImgDir.listFiles();
                if (files != null) {
                    for (File f : files) {
                        if (System.currentTimeMillis() - f.lastModified() > mDefaultExpireMillionSecond) {
                            f.delete();
                        }
                    }
                }
            }
        });
    }

    @Override
    public Object get(String key, CacheType type) {
        if (TextUtils.isEmpty(key) || !isParentDirAvailable())
            return null;
        FutureTask<Object> futureTask = new FutureTask<>(
                () -> {
                    String fileName = MD5Util.encode(key);
                    if (TextUtils.isEmpty(fileName)) {
                        fileName = key;
                    }
                    if (type == CacheType.DATA) {
                        Object res= FileUtils.deserializeFromFile(new File(mDefaultDataDir, fileName));
                        return res;
                    } else {
                        File file = new File(mDefaultImgDir, fileName);
                        if (file.exists())
                            return BitmapFactory.decodeFile(file.getAbsolutePath());
                    }
                    return null;
                });
        return getFutureTaskResult(futureTask);
    }

    @Override
    public InputStream getStream(String key, CacheType type) {
        if (TextUtils.isEmpty(key) || !isParentDirAvailable())
            return null;
        FutureTask<InputStream> futureTask = new FutureTask<>(
                () -> {
                    String fileName = MD5Util.encode(key);
                    if (TextUtils.isEmpty(fileName)) {
                        fileName = key;
                    }
                    LogUtil.i(TAG, "start key=" + key);
                    File file = new File(type == CacheType.DATA ? mDefaultDataDir : mDefaultImgDir, fileName);
                    if (file.exists()) {
                        try {
                            return new FileInputStream(file);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                    LogUtil.i(TAG, "end key=" + key);
                    return null;
                });
        return getFutureTaskResult(futureTask);
    }

    @Override
    public File getCachedFile(String key, CacheType type) {
        if (TextUtils.isEmpty(key) || !isParentDirAvailable())
            return null;
        FutureTask<File> futureTask = new FutureTask<>(
                () -> {
                    String fileName = MD5Util.encode(key);
                    if (TextUtils.isEmpty(fileName)) {
                        fileName = key;
                    }
                    LogUtil.i(TAG, "start key=" + key);
                    File file = new File(type == CacheType.DATA ? mDefaultDataDir : mDefaultImgDir, fileName);
                    if (file.exists()) {
                        return file;
                    }
                    LogUtil.i(TAG, "end key=" + key);
                    return null;
                });
        return getFutureTaskResult(futureTask);
    }

    @Override
    public void put(String key, Object o, CacheType type) throws IllegalArgumentException {
        put(key, o, -1, type);
    }

    private <T> T getFutureTaskResult(FutureTask<T> task) {
        if (!ThreadManager.runningOn(ThreadManager.THREAD_IO)) {
            ThreadManager.postIO(task);
        }else {
            task.run();
        }
        try {
            return task.get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private String genFileName(String key) {
        String fileName = MD5Util.encode(key);
        if (TextUtils.isEmpty(fileName)) {
            fileName = key;
        }
        return fileName;
    }

    // TODO: 2018/4/26 过时逻辑暂未实现
    @Override
    public void put(String key, Object value, long expired, CacheType type) {
        if (TextUtils.isEmpty(key) || value == null || !isParentDirAvailable())
            return;
        if (!ThreadManager.runningOn(ThreadManager.THREAD_IO)) {
            ThreadManager.postIO(() -> put(key, value, expired, type));
        }
        checkValueType(value, type);
        String fileName = genFileName(key);
        File file = new File(type == CacheType.DATA ? mDefaultDataDir : mDefaultImgDir, fileName);
        LogUtil.i(TAG, "start key=" + key);
        switch (type) {
            case DATA: {
                FileUtils.serializeToFile((Serializable) value, file);
                break;
            }
            case IMAGE: {
                FileOutputStream fos = null;
                try {
                    fos = new FileOutputStream(file);
                    ((Bitmap) value).compress(CompressFormat.PNG, 100, fos);
                } catch (Exception e) {
                } finally {
                    IOUtil.closeQuietly(fos);
                }
                break;
            }
        }
        LogUtil.i(TAG, "end key=" + key);
        return;
    }

    private void checkValueType(Object value, CacheType type) {
        if ((type == CacheType.DATA && value instanceof Serializable) ||
                (type == CacheType.IMAGE && value instanceof Bitmap)) {
            return;
        }
        throw new IllegalArgumentException("CacheType and value must be same");
    }

    @Override
    public void delete(String key, CacheType type) {
        if (TextUtils.isEmpty(key) || !isParentDirAvailable())
            return;
        if (!ThreadManager.runningOn(ThreadManager.THREAD_IO)) {
            ThreadManager.postIO(() -> delete(key, type));
        }
        String fileName = MD5Util.encode(key);
        if (TextUtils.isEmpty(fileName)) {
            fileName = key;
        }
        new File(type == CacheType.DATA ? mDefaultDataDir : mDefaultImgDir, fileName).delete();
    }

    /**
     * 清理所有缓存，谨慎调用，会导致所有cache都被清除
     */
    @Override
    public boolean clear() {
        if (!isParentDirAvailable())
            return false;
        if (!ThreadManager.runningOn(ThreadManager.THREAD_IO)) {
            ThreadManager.postIO(() -> clear());
        }
        boolean result = FileUtils.deleteFile(mDefaultDataDir);
        result = result && FileUtils.deleteFile(mDefaultImgDir);
        return result;
    }

    @Override
    public boolean contains(String key, CacheType type) {
        if (TextUtils.isEmpty(key) || !isParentDirAvailable())
            return false;

        String fileName = MD5Util.encode(key);
        if (TextUtils.isEmpty(fileName)) {
            fileName = key;
        }
        File file = new File(type == CacheType.DATA ? mDefaultDataDir : mDefaultImgDir, fileName);
        return file.exists() && file.length() > 0;
    }

    @Override
    public void destroy() {
    }


    private boolean isParentDirAvailable() {
        boolean available = mDefaultDataDir != null
                && (mDefaultDataDir.exists() || mDefaultDataDir.mkdirs())
                && mDefaultDataDir.isDirectory();
        available = available
                && (mDefaultImgDir != null && (mDefaultImgDir.exists() || mDefaultImgDir.mkdirs()) && mDefaultImgDir
                .isDirectory());
        return available;
    }
}
