package com.jj.base.exts

import android.text.ParcelableSpan
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ImageSpan

fun SpannableStringBuilder.appendAndSetSpan(
    text: String,
    vararg span: ParcelableSpan
): SpannableStringBuilder {
    val oldL = length
    append(text)
    span.forEach {
        setSpan(it, oldL, length, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
    }
    return this
}

fun SpannableStringBuilder.appendAndSetImageSpan(
    text: String,
    vararg span: ImageSpan
): SpannableStringBuilder {
    val oldL = length
    append(text)
    span.forEach {
        setSpan(it, oldL, length, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
    }
    return this
}

