package com.tmall.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.StringRes;
import android.widget.Toast;

import com.tmall.pojo.User;

/**
 * Created by SONG on 2016/5/6.
 */
public class TmallUtil {
    // 首选项存储 *********************************************
    private static final String PREFS_NAME = "tmall_settings";
    private static final int PREFS_MODE = Context.MODE_PRIVATE;

    /**
     * 在首选项存储中保存用户信息
     *
     * @param context
     * @param user
     */
    public static void saveUser(Context context, User user) {
        SharedPreferences preferences = context.getSharedPreferences(
                PREFS_NAME, PREFS_MODE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("id", user.getId());
        editor.putString("username", user.getUsername());
        editor.putString("imageUrl", user.getImageUrl());
        editor.commit();
    }

    /**
     * 获得首选项存储中的用户信息
     *
     * @param context
     * @return
     */
    public static User getUser(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(
                PREFS_NAME, PREFS_MODE);
        Integer id = preferences.getInt("id", 0);
        String username = preferences.getString("username", "");
        String imageUrl = preferences.getString("imageUrl", "");

        User user = new User();
        user.setId(id);
        user.setUsername(username);
        user.setImageUrl(imageUrl);
        return user;
    }

    // Toast *****************************************
    public static void showToast(Context context, CharSequence text) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }

    public static void showToast(Context context, @StringRes int resId) {
        Toast.makeText(context, resId, Toast.LENGTH_SHORT).show();
    }
}
