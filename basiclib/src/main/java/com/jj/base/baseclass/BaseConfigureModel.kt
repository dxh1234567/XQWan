package com.jj.base.baseclass

import android.content.Context
import android.content.SharedPreferences
import com.jj.base.utils.Utility
import java.util.*

/**
 * Created by yangxl on 2016/12/13.
 */

abstract class BaseConfigureModel( pref: String="boost_common_pref", mode: Int = Context.MODE_PRIVATE ) {

    private val mSharedPreferences: SharedPreferences = Utility.getApplication().getSharedPreferences(pref, mode)

    /**
     * 是否同步，基本很少用，如有需要后续再对其他字段进行添加，此处以boolean做实例
     */
    @JvmOverloads
    protected fun putBoolean(key: String, value: Boolean, isSync: Boolean = false) {
        if (isSync) {
            mSharedPreferences.edit().putBoolean(key, value).commit()
        } else {
            mSharedPreferences.edit().putBoolean(key, value).apply()
        }
    }

    protected fun getBoolean(key: String, def: Boolean): Boolean {
        return mSharedPreferences.getBoolean(key, def)
    }

    protected fun putString(key: String, value: String) {
        mSharedPreferences.edit().putString(key, value).apply()
    }

    protected fun getString(key: String, def: String): String {
        return mSharedPreferences.getString(key, def) as String
    }


    protected fun putLong(key: String, value: Long) {
        mSharedPreferences.edit().putLong(key, value).apply()
    }

    protected fun getLong(key: String, def: Long): Long {
        return mSharedPreferences.getLong(key, def)
    }

    protected fun putInt(key: String, value: Int) {
        mSharedPreferences.edit().putInt(key, value).apply()
    }

    protected fun getInt(key: String, def: Int): Int {
        return mSharedPreferences.getInt(key, def)
    }

    protected fun putFloat(key: String, value: Float) {
        mSharedPreferences.edit().putFloat(key, value).apply()
    }

    protected fun getFloat(key: String, def: Float): Float {
        return mSharedPreferences.getFloat(key, def)
    }

    protected fun getStrsFromPrefByKey(key: String): MutableList<String> {
        val tok = StringTokenizer(getString(key, ""), "|")
        val list = ArrayList<String>()
        while (tok.hasMoreTokens()) {
            val `val` = tok.nextToken()
            if (`val` != "") {
                list.add(`val`)
            }
        }
        return list
    }

    protected fun saveStrsToPerfByKey(values: List<String>, key: String) {
        val value = StringBuilder()
        for (str in values) {
            if (value.isNotEmpty())
                value.append('|')
            value.append(str)
        }
        putString(key, value.toString())
    }

    /**
     * @return 是否改变
     */
    protected fun removeStrsFromPrefByType(removedValues: List<String>, key: String): Boolean {
        val strs = getStrsFromPrefByKey(key)
        var changed = false
        for (str in removedValues) {
            if (strs.indexOf(str) != -1) {
                changed = true
                strs.remove(str)
            }
        }
        if (changed) {
            saveStrsToPerfByKey(strs, key)
        }
        return changed
    }

    fun commit() {
        mSharedPreferences.edit().commit()
    }

    companion object {
        val INVALIDE_VALUE = -1
    }
}
/**
 * 封装对值读写
 */
