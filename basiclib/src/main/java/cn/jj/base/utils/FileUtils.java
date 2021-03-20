package cn.jj.base.utils;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.nio.channels.FileLock;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class FileUtils {
    private final static String TAG = "FileUtils";

    public static boolean deleteFile(File file) {
        if (file.isDirectory()) {
            File children[] = file.listFiles();
            if (children != null) {
                for (File f : children) {
                    deleteFile(f);
                }
            }
        }
        boolean res = file.delete();
        return res;
    }

    public static boolean deleteSubFile(File file, String regex) {
        boolean res = false;
        if (file.isDirectory()) {
            File children[] = file.listFiles();
            if (children != null) {
                for (File f : children) {
                    if (f.getName().matches(regex)) {
                        res &= deleteFile(f);
                    }
                }
            }
        }
        return res;
    }

    public static boolean deleteFolder(String filePath) {
        return deleteFile(new File(filePath));
    }

    public static void copy(String sourceFilePath, String targetFilePath) throws IOException {

        File desFile = new File(targetFilePath);
        desFile.getParentFile().mkdirs();

        InputStream is = new FileInputStream(sourceFilePath);
        FileOutputStream fis = new FileOutputStream(targetFilePath);

        byte[] buf = new byte[1024];
        int hasReaded = 0;
        while ((hasReaded = is.read(buf)) > 0) {
            fis.write(buf, 0, hasReaded);
        }
        is.close();
        fis.close();
    }

    /**
     * 复制整个文件夹内容
     *
     * @param oldPath String 原文件路径 如：c:/fqf
     * @param newPath String 复制后路径 如：f:/fqf/ff
     */
    public static void copyFolder(String oldPath, String newPath) {

        try {
            (new File(newPath)).mkdirs(); //如果文件夹不存在 则建立新文件夹
            File a = new File(oldPath);
            String[] file = a.list();
            File temp;
            for (int i = 0; i < file.length; i++) {
                if (oldPath.endsWith(File.separator)) {
                    temp = new File(oldPath + file[i]);
                } else {
                    temp = new File(oldPath + File.separator + file[i]);
                }

                if (temp.isFile()) {
                    FileInputStream input = new FileInputStream(temp);
                    FileOutputStream output = new FileOutputStream(newPath + "/" +
                            (temp.getName()).toString());
                    byte[] b = new byte[1024 * 5];
                    int len;
                    while ((len = input.read(b)) != -1) {
                        output.write(b, 0, len);
                    }
                    output.flush();
                    output.close();
                    input.close();
                }
                if (temp.isDirectory()) {//如果是子文件夹
                    copyFolder(oldPath + "/" + file[i], newPath + "/" + file[i]);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void copy(InputStream is, String targetFilePath) {
        FileOutputStream fis = null;
        try {
            File desFile = new File(targetFilePath);
            desFile.getParentFile().mkdirs();
            fis = new FileOutputStream(targetFilePath);
            byte[] buf = new byte[1024];
            int hasReaded;
            while ((hasReaded = is.read(buf)) > 0) {
                fis.write(buf, 0, hasReaded);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtil.closeQuietly(is);
            IOUtil.closeQuietly(fis);
        }
    }

    public static boolean saveFile(InputStream stream, File target) {
        boolean res = true;
        FileOutputStream os = null;
        if (target.exists()) {
            target.delete();
        }
        File temp_f = new File(target.getParentFile(), target.getName() + ".tmp");
        try {
            os = new FileOutputStream(temp_f);
            byte[] buffer = new byte[8 * 1024];
            int readedLength = stream.read(buffer);
            while (readedLength >= 0) {
                os.write(buffer, 0, readedLength);
                readedLength = stream.read(buffer);
            }
            temp_f.renameTo(target);
            temp_f.delete();
        } catch (Exception e) {
            e.printStackTrace();
            res = false;
        } finally {
            IOUtil.closeQuietly(os);
            IOUtil.closeQuietly(stream);
        }
        return res;
    }

    public static void saveFile(String content, File target, boolean append) {
        OutputStream stream = null;
        try {
            stream = new FileOutputStream(target, append);
            byte[] by = content.getBytes();
            stream.write(by);
            stream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtil.closeQuietly(stream);
        }
    }

    public static byte[] toBytes(File file) {
        FileInputStream in = null;
        ByteArrayOutputStream out = null;
        try {
            out = new ByteArrayOutputStream();
            in = new FileInputStream(file);
            byte[] buf = new byte[2028];
            int hasReaded = 0;
            while ((hasReaded = in.read(buf)) > 0) {
                out.write(buf, 0, hasReaded);
            }
            return out.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtil.closeQuietly(in);
            IOUtil.closeQuietly(out);
        }
        return null;
    }

    public static String readFile(File file) {
        String content = "";
        InputStream is = null;
        BufferedReader bufferedReader = null;
        if (!file.isDirectory()) {
            try {
                is = new FileInputStream(file);
                bufferedReader = new BufferedReader(new InputStreamReader(is));
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    content += line + "\n";
                }
                content = replaceBlank(content);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                IOUtil.closeQuietly(is);
                IOUtil.closeQuietly(bufferedReader);
            }
        }
        return content;
    }

    public static String replaceBlank(String str) {
        String dest = "";
        if (str != null) {
            Pattern p = Pattern.compile("\\s*|\t|\r|\n");
            Matcher m = p.matcher(str);
            dest = m.replaceAll("");
        }
        return dest;
    }

    public static boolean exists(String filepath) {
        if (TextUtils.isEmpty(filepath)) {
            return false;
        }
        return new File(filepath).exists();
    }

    public static long getFileSize(File file) {
        long size = 0;
        File[] fileList = file.listFiles();
        if (fileList == null || fileList.length <= 0) {
            return 0;
        }
        for (File f : fileList) {
            if (f.isDirectory()) {
                size = size + getFileSize(f);
            } else {
                size = size + f.length();
            }
        }
        return size;
    }

    /**
     * 添加图片到系统索引库  此处修改过异常，在原来代码基础上增加了catch后新的方法 异常地址是http://mobile.umeng.com/apps/84a300b229b0426585545e15/error_types/show?error_type_id=51e5455856240b922b003a48_6302294227374170271_4.5.9
     *
     * @param context
     * @param path    文件的路径
     * @return
     */
    public static void addImageToMediaStore(final Context context, final String path) {

        ContentResolver contentResolver = context.getContentResolver();
        ContentValues newValues = new ContentValues(6);
        File file = new File(path);
        String fileName = file.getName();
        String title = fileName.substring(0, fileName.lastIndexOf("."));
        newValues.put(MediaStore.Images.Media.TITLE, title);
        newValues.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
        newValues.put(MediaStore.Images.Media.DATA, path);
        newValues.put(MediaStore.Images.Media.DATE_MODIFIED,
                System.currentTimeMillis() / 1000);
        newValues.put(MediaStore.Images.Media.SIZE, file.length());
        newValues.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
        if (Build.VERSION.SDK_INT >= 16) {
            newValues.put(MediaStore.Images.Media.HEIGHT, ScreenUtil.getScreenHeight());
            newValues.put(MediaStore.Images.Media.WIDTH, ScreenUtil.getScreenWidth());
        }
        try {
            contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, newValues);
        } catch (Exception e) {
            addImageToMediaStoreNew(context, path);
        }
    }

    private static void addImageToMediaStoreNew(final Context context, final String path) {
        // 首先保存图片
        File file = new File(path);
        String fileName = file.getName();
        // 其次把文件插入到系统图库
        try {
            MediaStore.Images.Media.insertImage(context.getContentResolver(),
                    file.getAbsolutePath(), fileName, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 最后通知图库更新
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                Uri.parse("file://" + file.getAbsolutePath())));
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri
                .getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri
                .getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri
                .getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri
                .getAuthority());
    }

    /**
     * 获取目录下所有文件(按时间排序)
     */
    public static List<File> getSubFiles(File rootFile) {
        List<File> subFiles = null;
        File[] files = rootFile.listFiles();
        if (files != null && files.length > 0) {
            subFiles = new ArrayList<>(files.length);
            for (File file : files) {
                subFiles.add(file);
            }
        }
        if (subFiles == null || subFiles.isEmpty()) {
            return null;
        }

        Collections.sort(subFiles, (f1, f2) ->
                f1.lastModified() < f2.lastModified() ? 1 :
                        (f1.lastModified() == f2.lastModified() ? 0 : -1));
        return subFiles;
    }

    public static boolean unZipFiles(FileInputStream in, String destPath) {
        if (!destPath.endsWith("/")) {
            destPath += "/";
        }
        ZipInputStream zipInput = null;
        try {
            zipInput = new ZipInputStream(in);
            ZipEntry zipEntry;
            while ((zipEntry = zipInput.getNextEntry()) != null) {
                String fileName = zipEntry.getName();
                File tempFile = new File(destPath + fileName);
                if (!tempFile.getParentFile().exists()) {
                    tempFile.getParentFile().mkdir();
                }
                if (zipEntry.isDirectory()) {
                    if (!tempFile.exists()) {
                        tempFile.mkdir();
                    }
                    continue;
                }
                if (!tempFile.exists()) {
                    tempFile.createNewFile();
                }
                FileOutputStream tempOutputStream = new FileOutputStream(tempFile);
                byte[] buffer = new byte[4096];
                int hasRead = 0;
                while ((hasRead = zipInput.read(buffer)) > 0) {
                    tempOutputStream.write(buffer, 0, hasRead);
                }
                tempOutputStream.flush();
                tempOutputStream.close();
            }
            zipInput.close();
            return true;
        } catch (Throwable e) {
            e.printStackTrace();
        }
        IOUtil.closeQuietly(zipInput);
        return false;
    }

    public static Object deserializeFromFile(File file) {
        if (file == null || !file.exists())
            return null;
        FileInputStream fis = null;
        ObjectInputStream ois = null;
        FileLock lock = null;
        try {
            fis = new FileInputStream(file);
            lock = fis.getChannel().lock(0L, Long.MAX_VALUE, true);
            ois = new ObjectInputStream(fis);
            return ois.readUnshared();
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            if (lock != null)
                try {
                    lock.release();
                } catch (IOException e) {
                }

            IOUtil.closeQuietly(ois);
            IOUtil.closeQuietly(fis);
        }
        return null;
    }

    public static boolean serializeToFile(Serializable o, File file) {
        if (o == null || file == null)
            return false;
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (IOException e) {
        }

        FileOutputStream fos = null;
        ObjectOutputStream oos = null;
        FileLock lock = null;
        try {
            fos = new FileOutputStream(file);
            oos = new ObjectOutputStream(fos);
            lock = fos.getChannel().lock();
            oos.writeUnshared(o);
            oos.flush();
            fos.flush();
            return true;
        } catch (Throwable e) {
        } finally {
            if (oos != null) {
                try {
                    oos.reset();
                } catch (IOException e) {
                }
            }
            if (lock != null) {
                try {
                    lock.release();
                } catch (IOException e) {
                }
            }
            IOUtil.closeQuietly(oos);
            IOUtil.closeQuietly(fos);
        }
        return false;
    }

    /**
     * 获取文件扩展名
     *
     * @param path 文件路径
     */
    public static String getExtensionName(String path) {
        if (!TextUtils.isEmpty(path)) {
            int dot = path.lastIndexOf('.');
            if ((dot > -1) && (dot < (path.length() - 1))) {
                return path.substring(dot + 1);
            }
        }
        return "";
    }
}
