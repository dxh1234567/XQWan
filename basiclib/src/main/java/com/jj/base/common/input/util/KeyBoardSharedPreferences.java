package com.jj.base.common.input.util;

import android.content.Context;
import android.content.SharedPreferences;

class KeyBoardSharedPreferences {

    private static final String FILE_NAME = "keyboard.config";

    private static final String KEY_KEYBOARD_HEIGHT = "key_keyboard_height";

    private static volatile SharedPreferences sharedPreferences;

    public static boolean save(Context context, int keyboardHeight) {
        return with(context).edit()
                .putInt(KEY_KEYBOARD_HEIGHT, keyboardHeight)
                .commit();
    }

    private static SharedPreferences with(Context context) {
        if (sharedPreferences == null) {
            synchronized (KeyBoardSharedPreferences.class) {
                if (sharedPreferences == null) {
                    sharedPreferences = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
                }
            }
        }

        return sharedPreferences;
    }

    public static int get(Context context, int defaultHeight) {
        return with(context).getInt(KEY_KEYBOARD_HEIGHT, defaultHeight);
    }

}
