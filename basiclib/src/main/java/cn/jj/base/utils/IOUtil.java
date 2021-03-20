package cn.jj.base.utils;

/**
 * IO操作工具类
 * Created by jiayuanbin on 2016/8/4.
 */

import android.content.Context;
import android.database.Cursor;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class IOUtil {

    public static void closeQuietly(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }
    }

    public static void closeQuietly(Cursor cursor) {
        if (cursor != null) {
            try {
                cursor.close();
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }
    }

    private static void copy(String src, String dest) {
        try {
            copy(new FileInputStream(src), dest);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void copy(InputStream is, String dest) {
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        try {
            File file = new File(dest);
            if(file.exists()){
                file.createNewFile();
            }
            bis = new BufferedInputStream(is);
            bos = new BufferedOutputStream(new FileOutputStream(file));
            byte[] bs = new byte[1024 * 12];
            int len;
            while ((len = bis.read(bs)) > 0) {
                bos.write(bs, 0, len);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeQuietly(bis);
            closeQuietly(bos);
        }
    }

    /**
     * 拷贝文件到指定路径
     *
     * @param src
     * @param dest
     */
    public static void copyFile(String src, String dest) {
        copy(src, dest);
    }

    public static List<String> getAssetResourceLines(Context context, String path, String asset) {
        InputStream is = null;
        BufferedReader bufferedReader = null;
        try {
            is = context.getAssets().open(path + File.separator + asset);
            bufferedReader = new BufferedReader(new InputStreamReader(is));
            List<String> result = new ArrayList<String>();
            String lineStr = null;
            while ((lineStr = bufferedReader.readLine()) != null) {
                lineStr = lineStr.trim();
                if (lineStr.length() > 0) {
                    result.add(lineStr);
                }
            }
            return result;
        } catch (Exception e) {
            return null;
        } finally {
            closeQuietly(is);
            closeQuietly(bufferedReader);
        }
    }

    public static void copyAssetsFile(Context context, String fileName, boolean rewrite) {
        File file = new File(context.getFilesDir(), fileName);
        if (file.exists() && !rewrite) {
            return;
        }
        InputStream in = null;
        long size = 0;
        try {
            in = context.getAssets().open(fileName);
            size = in.available();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (in != null && (!file.exists() || file.length() != size)) {
            copy(in, file.getPath());
            closeQuietly(in);
        } else {
            closeQuietly(in);
        }
    }
}
