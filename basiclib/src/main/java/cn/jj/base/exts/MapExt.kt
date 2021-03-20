package cn.jj.base.exts

fun Map<String, *>.toInt(key: String, default: Int = -1): Int {
    return this[key]?.let {
        when (it) {
            is String -> it.toInt()
            is Double -> it.toInt()
            is Int -> it
            else -> default
        }
    } ?: default
}

fun Map<String, *>.toLong(key: String, default: Long = -1L): Long {
    return this[key]?.let {
        when (it) {
            is String -> it.toLong()
            is Float -> it.toLong()
            is Double -> it.toLong()
            is Int -> it.toLong()
            is Long -> it
            else -> default
        }
    } ?: default
}

fun Map<String, *>.toBoolean(key: String, default: Boolean = false): Boolean {
    return this[key]?.let {
        when (it) {
            is String -> it.toBoolean()
            is Double -> it >= 0
            is Int -> it >= 0
            is Boolean -> it
            else -> default
        }
    } ?: default
}

fun Map<String, *>.toStr(key: String, default: String = ""): String {
    return this[key]?.let {
        when (it) {
            is String -> it
            is Float, is Double, is Int, is Long -> "$it"
            else -> it.toString()
        }
    } ?: default
}