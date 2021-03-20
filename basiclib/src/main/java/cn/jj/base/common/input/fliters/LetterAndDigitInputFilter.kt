package cn.jj.base.common.input.fliters

import android.text.InputFilter
import android.text.Spanned


class LetterAndDigitInputFilter : InputFilter {
    override fun filter(
        source: CharSequence,
        start: Int,
        end: Int,
        dest: Spanned,
        dstart: Int,
        dend: Int
    ): CharSequence? {

        for (i in start until end) {
            if (!Character.isLetterOrDigit(source[i])) {
                return dest.subSequence(dstart, dend)
            }
        }

        return source
    }
}
