package cn.jj.base.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.UUID;

/**
 * Created by yangxl on 2017/8/21.
 */

public class GetUUIDUtils {
    private static String mUUID = null;
    private static final String filename = "androidid.os";
    public static final String PUSH_MESSAGE_UUID_DEFAULT = "";
    private static final int BIT = 10;
    private static SharedPreferences mMainSharedPreferences;
    private static final int START = 12345;
    public static final int MAIN_ID = 12345;
    private static final int MAIN_PREF_FILE_ID = 12345;
    public static final String MAIN_PREF_FILE_NAME = String.valueOf(12345);
    private static final String PUSH_MESSAGE_UUID = String.valueOf(12355);

    public GetUUIDUtils() {
    }

    public static String getUUID(Context context) {
        if (mUUID != null && !"".equals(mUUID.trim())) {
            return cutUUIDString(mUUID, 36);
        } else {
            String uuid = getPushMessageUUID(context);
            boolean sdCardExist;
            File newUUID2;
            File newUUID3;
            if (!TextUtils.isEmpty(uuid)) {
                sdCardExist = Environment.getExternalStorageState().equals("mounted");
                if (sdCardExist) {
                    newUUID2 = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/.hahapicture/" + "androidid.os");
                    newUUID3 = newUUID2.getParentFile();
                    if (!newUUID3.exists()) {
                        newUUID3.mkdirs();
                    }

                    try {
                        newUUID2.createNewFile();
                    } catch (IOException var6) {
                        var6.printStackTrace();
                    }

                    write(newUUID2, uuid);
                }

                mUUID = uuid;
                return cutUUIDString(uuid, 36);
            } else {
                sdCardExist = Environment.getExternalStorageState().equals("mounted");
                if (sdCardExist) {
                    newUUID2 = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/.hahapicture/" + "androidid.os");
                    String newUUID1;
                    if (newUUID2.exists()) {
                        newUUID1 = read(newUUID2);
                        if (!TextUtils.isEmpty(newUUID1)) {
                            setPushMessageUUID(context, newUUID1);
                            mUUID = newUUID1;
                            return cutUUIDString(newUUID1, 36);
                        }
                    } else {
                        newUUID3 = newUUID2.getParentFile();
                        if (!newUUID3.exists()) {
                            newUUID3.mkdirs();
                        }

                        try {
                            newUUID2.createNewFile();
                        } catch (IOException var7) {
                            var7.printStackTrace();
                        }
                    }

                    newUUID1 = UUID.randomUUID().toString();
                    write(newUUID2, newUUID1);
                    setPushMessageUUID(context, newUUID1);
                    mUUID = newUUID1;
                    return cutUUIDString(newUUID1, 36);
                } else {
                    String newUUID = UUID.randomUUID().toString();
                    setPushMessageUUID(context, newUUID);
                    return cutUUIDString(newUUID, 36);
                }
            }
        }
    }

    public static String cutUUIDString(String uuid, int size) {
        return uuid.length() > 36 ? uuid.substring(0, 36) : uuid;
    }

    private static String getPushMessageUUID(Context context) {
        SharedPreferences pref = getSharedPreferences(context.getApplicationContext(), 12345);
        return pref.getString(PUSH_MESSAGE_UUID, "");
    }

    private static final SharedPreferences getSharedPreferences(Context context, int id) {
        switch (id) {
            case 12345:
                if (mMainSharedPreferences == null) {
                    mMainSharedPreferences = context.getSharedPreferences(MAIN_PREF_FILE_NAME, 0);
                }

                return mMainSharedPreferences;
            default:
                return null;
        }
    }

    private static void setPushMessageUUID(Context context, String uuid) {
        SharedPreferences pref = getSharedPreferences(context.getApplicationContext(), 12345);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(PUSH_MESSAGE_UUID, uuid);
        editor.commit();
    }

    private static void write(File file, String content) {
        try {
            FileOutputStream e = new FileOutputStream(file, false);
            e.write(content.getBytes());
            e.close();
        } catch (Exception var3) {
            var3.printStackTrace();
        }

    }

    private static String read(File file) {
        try {
            FileInputStream e = new FileInputStream(file);
            BufferedReader br = new BufferedReader(new InputStreamReader(e));
            StringBuilder sb = new StringBuilder("");
            String line = null;

            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            br.close();
            return sb.toString();
        } catch (Exception var5) {
            var5.printStackTrace();
            return null;
        }
    }
}
