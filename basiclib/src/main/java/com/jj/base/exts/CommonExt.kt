package com.jj.base.exts

import android.view.View
import com.jj.base.common.view.OnMultiClickListener
import com.jj.base.utils.LogUtil
import com.jj.base.utils.LogUtil2
import com.jj.base.utils.SystemUtil
import com.jj.base.utils.Utility
import com.jj.logger.DiskLogAdapter
import com.jj.logger.Logger
import com.tencent.mars.xlog.Xlog

fun printLog(block: () -> Unit) {
    if (Utility.isDebug) {
        block()
    }
}

// 5M
fun initLogger2(maxBytes: Int = 1024 * 1024 * 5) =
    if (LogUtil2.isWriteFile) {
        Logger.clearLogAdapters()
        Logger.addLogAdapter(
            DiskLogAdapter(
                SystemUtil.getRootDir(Utility.getApplication(), getProductId())?.path,
                maxBytes,
                Utility.getApplication()
            )
        )
    } else {
        Logger.clearLogAdapters()
    }

fun initLogger(
    consoleLogOpen: Boolean = Utility.isDebug,
    level: Int = if (Utility.isDebug) LogUtil.LEVEL_VERBOSE else LogUtil.LEVEL_INFO,
    mode: Int = LogUtil.AppednerModeAsync,
    namePrefix: String = "",
    cacheDays: Int = 10
) {
    val logDir = SystemUtil.getRootDir(Utility.getApplication(), getProductId()).path + "/xlog"
    if (LogUtil.getImpl() !is Xlog) {
        LogUtil.setLogImp(Xlog())
    }
    LogUtil.setConsoleLogOpen(consoleLogOpen)
    LogUtil.appenderFlush()
    LogUtil.appenderOpen(level, mode, logDir, logDir, namePrefix, cacheDays)
}


fun View.setMultiClickListener(block: (v: View) -> Unit) {
    setOnClickListener(object : OnMultiClickListener() {
        override fun onMultiClick(v: View) {
            block(v)
        }
    })
}


//用于生成产品存储目录，不要轻易改动，要兼容旧版本
fun getProductId() =
    (Utility.getApplication().packageName.split("\\.".toRegex()).takeIf {
        it.isNotEmpty()
    }?.let {
        it[2]
            .let {
                if (it.endsWith("live"))
                    "jjlive"
                else
                    it
            }
    } ?: "jjlive")
        .plus(if (Utility.getApplication().packageName.contains("test")) "-test" else "")
