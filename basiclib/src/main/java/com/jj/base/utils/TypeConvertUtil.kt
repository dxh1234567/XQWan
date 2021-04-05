@file:JvmName("TypeConvertUtil")

package com.jj.base.utils

import android.annotation.SuppressLint
import android.content.Context
import android.text.format.Formatter
import android.util.SparseArray
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by yangxl on 2017/2/22.
 * java基本类型转换的公共类
 */
const val BLACKE_CHAR = " "

fun strToLong(str: String?, defaultValue: Long): Long {
    var result = defaultValue
    try {
        result = java.lang.Long.valueOf(str)
    } catch (e: Exception) {
    }
    return result
}

fun strToInteger(str: String?, defaultValue: Int): Int {
    var result = defaultValue
    try {
        result = Integer.valueOf(str)
    } catch (e: Exception) {
    }
    return result
}

fun <T> asList(sparseArray: SparseArray<T>?): List<T>? {
    if (sparseArray == null) return null
    val arrayList: MutableList<T> =
        ArrayList(sparseArray.size())
    for (i in 0 until sparseArray.size()) arrayList.add(sparseArray.valueAt(i))
    return arrayList
}

fun formatSize(context: Context?, size: Long): String {
    val formatSize = Formatter.formatFileSize(context, size)
    return checkBlackChar(formatSize)
}

private fun checkBlackChar(str: String): String {
    return if (str.contains(BLACKE_CHAR)) {
        str
    } else { //某些Android系统，格式化出来的数字与单位之间没有空格
        var index: Int
        index = 0
        while (index < str.length) {
            val c = str[index]
            if (c > 'A' && c < 'Z') {
                break
            }
            index++
        }
        str.substring(0, index) + BLACKE_CHAR + str.substring(index)
    }
}

fun <T> iteratorToString(
    iterator: Iterator<T>,
    separator: String?
): String {
    val builder = StringBuilder()
    while (iterator.hasNext()) {
        val item = iterator.next()
        builder.append(item.toString()).append(separator)
    }
    return builder.toString()
}

@JvmOverloads
fun timeToString(
    time: Long,
    format: String = "yyyy-MM-dd HH:mm:ss:SSS",
    timeZoneId: String = "GMT"
): String {
    val sdf =
        SimpleDateFormat(format, Locale.getDefault())
    sdf.timeZone = TimeZone.getTimeZone(timeZoneId)
    return sdf.format(Date(time))
}

@JvmOverloads
fun strToTime(
    format: String,
    timeStr: String?,
    timeZoneId: String = "GMT"
): Long {
    val sdf =
        SimpleDateFormat(format, Locale.getDefault())
    sdf.timeZone = TimeZone.getTimeZone(timeZoneId)
    try {
        return sdf.parse(timeStr).time
    } catch (e: Exception) {
    }
    return 0
}

fun isSameDay(date1: Date, date2: Date): Boolean {
    val c1 = Calendar.getInstance()
    val c2 = Calendar.getInstance()
    c1.time = date1
    c2.time = date2
    return (c1[Calendar.YEAR] == c2[Calendar.YEAR]
            && c1[Calendar.MONTH] == c2[Calendar.MONTH]
            && c1[Calendar.DAY_OF_MONTH] == c2[Calendar.DAY_OF_MONTH])
}

fun isSameYearMonth(date1: Date, date2: Date): Boolean {
    val c1 = Calendar.getInstance()
    val c2 = Calendar.getInstance()
    c1.time = date1
    c2.time = date2
    return (c1[Calendar.YEAR] == c2[Calendar.YEAR]
            && c1[Calendar.MONTH] == c2[Calendar.MONTH])
}

/**
 * utcToLocal
 *
 * @param utcTime 2019-10-09T03:17:42.000Z
 * @return 2019-10-09 11:17:42
 */
@SuppressLint("SimpleDateFormat")
fun utcToLocal(utcTime: String?): String {
    val utcFormat =
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    utcFormat.timeZone = TimeZone.getTimeZone("UTC")
    var gpsUTCDate: Date? = null
    gpsUTCDate = try {
        utcFormat.parse(utcTime)
    } catch (e: ParseException) {
        e.printStackTrace()
        return ""
    }
    val localFormat =
        SimpleDateFormat("yyyy-MM-dd HH:mm:ss") //当地时间格式
    localFormat.timeZone = TimeZone.getDefault()
    return localFormat.format(gpsUTCDate.time)
}

fun toObject(intArray: IntArray): Array<Int?> {
    val result = arrayOfNulls<Int>(intArray.size)
    for (i in intArray.indices) {
        result[i] = Integer.valueOf(intArray[i])
    }
    return result
}

fun toPrimitive(IntegerArray: Array<Int>): IntArray {
    val result = IntArray(IntegerArray.size)
    for (i in IntegerArray.indices) {
        result[i] = IntegerArray[i]
    }
    return result
}

/**
 * milliseconds->h:m:s
 */
fun formatVideoTime(time: Int) =
    when {
        time > 0 -> {
            val result = StringBuilder("")
            var t = time / 1000
            val h = t / 3600
            t %= 3600
            val m = t / 60
            val s = t % 60
            val block = { it: Int, suffix: String, ignore: Boolean ->
                when {
                    it > 0 -> {
                        if (it < 10) result.append("0").append(it).append(suffix)
                        else result.append(it).append(suffix)
                    }
                    !ignore -> result.append("00").append(suffix)
                    else -> {/* no-op */
                    }
                }
            }
            block(h, ":", true)
            block(m, ":", false)
            block(s, "", false)
            result.toString()
        }
        else -> "00:00"
    }