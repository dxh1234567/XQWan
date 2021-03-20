package cn.jj.base.common.input.fliters

import android.text.InputFilter
import android.text.Spanned
import android.text.TextUtils


class NumRangeInputFilter(val max: Long) : InputFilter {
    override fun filter(
        source: CharSequence,
        start: Int,
        end: Int,
        dest: Spanned,
        dstart: Int,
        dend: Int
    ): CharSequence? {

        val sourceText = source.toString()
        val destText = dest.toString()

        // 新输入的字符串为空（删除剪切等）
        if (TextUtils.isEmpty(sourceText)) {
            return null
        }

        if (!TextUtils.isDigitsOnly(source)) {
            return null
        }

        // 拼成字符串
        val temp = (destText.substring(0, dstart)
                + sourceText.substring(start, end)
                + destText.substring(dend, dest.length))

        if (temp.toInt() > max) {
            return dest.subSequence(dstart, dend)
        }

        return source
    }
}
