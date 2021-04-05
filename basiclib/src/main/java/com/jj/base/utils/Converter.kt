@file:JvmName("Converter")

package com.jj.base.utils

fun <T> addPlusChar(data: T): String {
    return "+".plus(data.toString())
}

fun dropMoreThan4(string: String): String {
    var result = string.take(4)
    result = result.dropLastWhile {
        it == '0' && result.contains(".")
    }.dropLastWhile {
        it == '.'
    }
    return result
}

fun number(number: Double): String {
    if (number < 9999) {
        return number.toString()
    }
    if (number < 99999999) {
        return "${dropMoreThan4((number / 10000f).toString())}万"
    }
    return "${dropMoreThan4((number / 100000000f).toString())}亿"
}

fun number(number: Float): String {
    if (number < 9999) {
        return number.toString()
    }
    if (number < 99999999) {
        return "${dropMoreThan4((number / 10000f).toString())}万"
    }
    return "${dropMoreThan4((number / 100000000f).toString())}亿"
}

fun number(number: Long): String {
    if (number < 9999) {
        return number.toString()
    }
    if (number < 99999999) {
        return "${dropMoreThan4((number / 10000f).toString())}万"
    }
    return "${dropMoreThan4((number / 100000000f).toString())}亿"
}

fun number(number: Int): String {
    if (number < 9999) {
        return number.toString()
    }
    if (number < 99999999) {
        return "${dropMoreThan4((number / 10000f).toString())}万"
    }
    return "${dropMoreThan4((number / 100000000f).toString())}亿"
}