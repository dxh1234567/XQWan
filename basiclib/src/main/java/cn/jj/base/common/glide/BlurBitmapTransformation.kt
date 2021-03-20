package cn.jj.base.common.glide

import android.graphics.Bitmap
import com.bumptech.glide.load.Key
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import cn.jj.base.utils.BitmapUtil
import cn.jj.base.utils.Utility
import java.security.MessageDigest

class BlurBitmapTransformation(private val blurRadius: Float, private val scaleDegree: Float) : BitmapTransformation() {
    override fun transform(pool: BitmapPool, toTransform: Bitmap, outWidth: Int, outHeight: Int): Bitmap {
        return BitmapUtil.blurBitmap(Utility.getApplication(),
                blurRadius,
                scaleDegree,
                toTransform)
    }

    override fun updateDiskCacheKey(messageDigest: MessageDigest) {
        messageDigest.update("${javaClass.name}_$blurRadius$scaleDegree".toByteArray(Key.CHARSET))
    }

    override fun hashCode(): Int {
        return (javaClass.name.hashCode() + blurRadius * 10 + scaleDegree.toInt() * 100).toInt()
    }

    override fun equals(other: Any?): Boolean {
        return other is BlurBitmapTransformation &&
                other.blurRadius == blurRadius &&
                other.scaleDegree == scaleDegree
    }

    override fun toString(): String {
        return "BlurBitmapTransformation(blurRadius=$blurRadius,scaleDegree=$scaleDegree)"
    }
}
