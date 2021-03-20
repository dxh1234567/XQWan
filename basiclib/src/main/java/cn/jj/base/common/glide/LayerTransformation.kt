package cn.jj.base.common.glide

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import cn.jj.base.utils.BitmapUtil
import cn.jj.base.utils.Utility
import com.bumptech.glide.load.Key
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import java.security.MessageDigest

class LayerTransformation(
    private val layerDrawable: Drawable,
    private val bgStroke: Int,
    private val up: Boolean
) : BitmapTransformation() {
    private val BG_ID = "cn.jj.base.common.glide.LayerTransformation_bg"
    private val FG_ID = "cn.jj.base.common.glide.LayerTransformation_fg"
    private val BG_ID_BYTES = BG_ID.toByteArray(Key.CHARSET)
    private val FG_ID_BYTES = BG_ID.toByteArray(Key.CHARSET)

    override fun transform(
        pool: BitmapPool,
        toTransform: Bitmap,
        outWidth: Int,
        outHeight: Int
    ): Bitmap {
        val drawable = LayerDrawable(
            if (up) {
                arrayOf(
                    BitmapDrawable(Utility.getApplication().resources, toTransform),
                    layerDrawable
                )
            } else {
                arrayOf(
                    layerDrawable,
                    BitmapDrawable(Utility.getApplication().resources, toTransform)
                )
            }
        )
        drawable.setLayerInset(if (up) 0 else 1, bgStroke, bgStroke, bgStroke, bgStroke)
        return BitmapUtil.drawableToBitamp(drawable, outWidth, outHeight)
    }

    override fun updateDiskCacheKey(messageDigest: MessageDigest) {
        messageDigest.update(if (up) FG_ID_BYTES else BG_ID_BYTES)
    }

    override fun hashCode(): Int {
        //@note layerDrawable  一般不会变化，所以直接返回确定值
        return if (up) FG_ID.hashCode() else BG_ID.hashCode()
    }

    override fun equals(obj: Any?): Boolean {
        return obj is LayerTransformation
    }

    override fun toString(): String {
        return "LayerTransformation(bgStroke=$bgStroke,layerDrawable=$layerDrawable)"
    }
}
