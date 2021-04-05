package com.jj.base.common.glide

import android.graphics.Bitmap
import com.bumptech.glide.load.Key
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import com.bumptech.glide.util.Util
import java.nio.ByteBuffer
import java.security.MessageDigest

class PercentRoundedCorners(
    private val percentRadius: Float,
    private var corners: Int = Corners.CORNER_ALL
) :
    BitmapTransformation() {

    override fun transform(
        pool: BitmapPool,
        toTransform: Bitmap,
        outWidth: Int,
        outHeight: Int
    ): Bitmap {
        val i = Math.min(toTransform.width, toTransform.height) * percentRadius / 2
        if (i > 0) {
            return CustomRoundedCorners.roundedCorners(pool, toTransform, i, corners)
        } else {
            return toTransform
        }
    }

    override fun equals(other: Any?): Boolean {
        return other is PercentRoundedCorners &&
                other.percentRadius == percentRadius &&
                other.corners == corners
    }

    override fun hashCode(): Int {
        return Util.hashCode(
            ID.hashCode(),
            Util.hashCode(percentRadius + corners)
        )
    }

    override fun updateDiskCacheKey(messageDigest: MessageDigest) {
        messageDigest.update(ID_BYTES)

        val radiusData = ByteBuffer.allocate(8).putFloat(percentRadius).putInt(corners).array()
        messageDigest.update(radiusData)
    }

    companion object {

        private val ID = "PercentRoundedCorners"
        private val ID_BYTES = ID.toByteArray(Key.CHARSET)
    }

}
