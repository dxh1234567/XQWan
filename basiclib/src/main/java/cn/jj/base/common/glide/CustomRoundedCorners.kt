package cn.jj.base.common.glide

import android.graphics.*
import android.os.Build
import androidx.annotation.NonNull
import cn.jj.base.common.glide.Corners.Companion.CORNER_ALL
import cn.jj.base.common.glide.Corners.Companion.CORNER_BOTTOM_LEFT
import cn.jj.base.common.glide.Corners.Companion.CORNER_BOTTOM_RIGHT
import cn.jj.base.common.glide.Corners.Companion.CORNER_TOP_LEFT
import cn.jj.base.common.glide.Corners.Companion.CORNER_TOP_RIGHT
import com.bumptech.glide.load.Key
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import com.bumptech.glide.load.resource.bitmap.TransformationUtils.getBitmapDrawableLock
import com.bumptech.glide.util.Util
import java.nio.ByteBuffer
import java.security.MessageDigest

class CustomRoundedCorners(
    private val cornerRadius: Float,
    private var corners: Int = CORNER_ALL
) : BitmapTransformation() {

    override fun transform(
        pool: BitmapPool,
        toTransform: Bitmap,
        outWidth: Int,
        outHeight: Int
    ): Bitmap {
        return if (cornerRadius > 0) {
            roundedCorners(pool, toTransform, cornerRadius.toFloat(), corners)
        } else {
            toTransform
        }
    }

    override fun equals(other: Any?): Boolean {
        return other is CustomRoundedCorners &&
                other.cornerRadius == cornerRadius &&
                other.corners == corners
    }

    override fun hashCode(): Int {
        return Util.hashCode(
            ID.hashCode(),
            Util.hashCode(cornerRadius + corners)
        )
    }

    override fun updateDiskCacheKey(messageDigest: MessageDigest) {
        messageDigest.update(ID_BYTES)
        val radiusData = ByteBuffer.allocate(8).putFloat(cornerRadius).putInt(corners).array()
        messageDigest.update(radiusData)
    }

    companion object {
        private val ID = "RoundedCorners"
        private val ID_BYTES = ID.toByteArray(Key.CHARSET)


        fun roundedCorners(
            pool: BitmapPool,
            inBitmap: Bitmap,
            cornerRadius: Float,
            corners: Int
        ): Bitmap {
            // Alpha is required for this transformation.
            val safeConfig: Bitmap.Config = getAlphaSafeConfig(inBitmap)
            val toTransform: Bitmap = getAlphaSafeBitmap(pool, inBitmap)
            val result = pool[toTransform.width, toTransform.height, safeConfig]
            result.setHasAlpha(true)
            val shader = BitmapShader(
                toTransform, Shader.TileMode.CLAMP,
                Shader.TileMode.CLAMP
            )
            val paint = Paint()
            paint.isAntiAlias = true
            paint.shader = shader
            val rect = RectF(0f, 0f, result.width.toFloat(), result.height.toFloat())
            getBitmapDrawableLock().lock()
            try {
                val canvas = Canvas(result)
                canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)
                canvas.drawRoundRect(rect, cornerRadius, cornerRadius, paint)
                val notRoundedCorners = corners xor CORNER_ALL
                if ((notRoundedCorners and CORNER_TOP_LEFT) != 0) {
                    canvas.drawRect(0f, 0f, cornerRadius, cornerRadius, paint)
                }
                if ((notRoundedCorners and CORNER_TOP_RIGHT) != 0) {
                    canvas.drawRect(rect.right - cornerRadius, 0f, rect.right, cornerRadius, paint)
                }
                if ((notRoundedCorners and CORNER_BOTTOM_LEFT) != 0) {
                    canvas.drawRect(
                        0f,
                        rect.bottom - cornerRadius,
                        cornerRadius,
                        rect.bottom,
                        paint
                    )
                }
                if ((notRoundedCorners and CORNER_BOTTOM_RIGHT) != 0) {
                    canvas.drawRect(
                        rect.right - cornerRadius,
                        rect.bottom - cornerRadius,
                        rect.right,
                        rect.bottom,
                        paint
                    )
                }
                canvas.setBitmap(null)
            } finally {
                getBitmapDrawableLock().unlock()
            }
            if (toTransform != inBitmap) {
                pool.put(toTransform)
            }
            return result
        }

        private fun getAlphaSafeConfig(@NonNull inBitmap: Bitmap): Bitmap.Config {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) { // Avoid short circuiting the sdk check.
                if (Bitmap.Config.RGBA_F16 == inBitmap.config) { // NOPMD
                    return Bitmap.Config.RGBA_F16
                }
            }
            return Bitmap.Config.ARGB_8888
        }

        private fun getAlphaSafeBitmap(
            @NonNull pool: BitmapPool, @NonNull maybeAlphaSafe: Bitmap
        ): Bitmap {
            val safeConfig = getAlphaSafeConfig(maybeAlphaSafe)
            if (safeConfig == maybeAlphaSafe.config) {
                return maybeAlphaSafe
            }
            val argbBitmap =
                pool[maybeAlphaSafe.width, maybeAlphaSafe.height, safeConfig]
            Canvas(argbBitmap).drawBitmap(maybeAlphaSafe, 0f, 0f, null /*paint*/)
            // We now own this Bitmap. It's our responsibility to replace it in the pool outside this method
// when we're finished with it.
            return argbBitmap
        }

    }
}
