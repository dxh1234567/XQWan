package com.jj.base.utils;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;

import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;
import androidx.core.graphics.drawable.DrawableCompat;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

public class BitmapUtil {

    public static final String SCHEMA_CONTENT = "content";
    private static final String DOCUMENT_AUTHORITY = "com.android.providers.media.documents";
    private static final String GOOGLE_PHOTO_AUTHORITY = "com.google.android.apps.docs.storage";
    private static final String GOOGLE_PHOTOS_CONTENT_PROVIDER_AUTHORITY = "com.google.android.apps.photos.contentprovider";

    public static final int ROTATE_TYPE_180_DEGREES = 3;

    public static final int ROTATE_TYPE_LEFT = 1;

    public static final int ROTATE_TYPE_NONE = 0;

    public static final int ROTATE_TYPE_RIGHT = 2;

    public static final int REQ_WIDTH = 960;

    private final static int SCALED_BITMAP_WIDTH = 80;

    private final static float MIN_HUE_VALUE = 35.0f;
    private final static float MAX_HUE_VALUE = 190.0f;

    private final static float MAX_SATURATION = 0.4f;

    private final static float MAX_BRIGHTNESS = 0.78f;
    private final static int DARK_COLOR_ALPHA = 230;
    private final static int DARK_COLOR_RED = 50;
    private final static int DARK_COLOR_GREEN = 50;
    private final static int DARK_COLOR_BLUE = 50;
    private final static float DARK_COLOR_MIN_RATE = 0.2f;

    public static void recycleBitmap(Bitmap paramBitmap) {
        if ((paramBitmap != null) && (!paramBitmap.isRecycled()))
            paramBitmap.recycle();
    }

    private static Bitmap rotate(Bitmap paramBitmap, float paramFloat) {

        if ((paramBitmap == null) || (paramFloat == 0.0F)) {
            return null;
        }

        Bitmap localBitmap = paramBitmap;
        int i = paramBitmap.getWidth();
        int j = paramBitmap.getHeight();

        Matrix localMatrix = new Matrix();
        localMatrix.postRotate(paramFloat);

        localBitmap = Bitmap.createBitmap(paramBitmap, 0, 0, i, j, localMatrix, true);

        return localBitmap;
    }

    public static Bitmap rotateBitmap(Bitmap paramBitmap, int paramInt) {
        Bitmap localBitmap;

        if ((paramBitmap == null) || (paramBitmap.isRecycled())) {
            return null;
        }

        float f = 0.0F;

        switch (paramInt) {
            case 1:
                f = -90.0F;
                break;

            case 2:
                f = 90.0F;
                break;

            case 3:
                f = 180.0F;
                break;

            default:
        }

        localBitmap = rotate(paramBitmap, f);
        return localBitmap;
    }

    public static boolean rotateBitmap(String filePath, int angle) {//ROTATE_TYPE_180_DEGREES
        Bitmap bitmap = decodeOriginalFile(filePath);
        if (bitmap == null) {
            return false;
        }

        Bitmap rotate = rotateBitmap(bitmap, angle);
        if (rotate != null) {
            Boolean ret = saveOriBitmapToFile(rotate, filePath);

            rotate.recycle();
            bitmap.recycle();
            return ret;
        }

        bitmap.recycle();
        return false;
    }


    public static boolean saveOriBitmapToFile(Bitmap previewBitmap, String originalFile) {

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(originalFile, options);

        Bitmap.CompressFormat compressFormat = Bitmap.CompressFormat.JPEG;

        if ((options != null) && (options.outMimeType != null) && (options.outMimeType.contains("png"))) {
            compressFormat = Bitmap.CompressFormat.PNG;
        }

        File file = new File(originalFile);

        boolean bool1 = previewBitmap.isRecycled();
        if (bool1 == true) {
            return false;
        }

        try {
            boolean bool2 = previewBitmap.compress(compressFormat, 100, new FileOutputStream(
                    file, false));

            if (bool2 == false) {
                file.delete();
                return false;
            }

        } catch (Exception e) {
            return false;
        }

        return true;
    }


    /**
     * ????????????
     *
     * @return
     */
    public static Bitmap createBitmap(Bitmap src, Bitmap watermark) {
        if (src == null) {
            return null;
        }
        int w = src.getWidth();
        int h = src.getHeight();
        int ww = watermark.getWidth();
        int wh = watermark.getHeight();
        //create the new blank bitmap
        Bitmap newb = Bitmap.createBitmap(w, h, Config.ARGB_8888);//?????????????????????SRC???????????????????????????
        Canvas cv = new Canvas(newb);
        //draw src into
        cv.drawBitmap(src, 0, 0, null);//??? 0???0??????????????????src
        //draw watermark into
        cv.drawBitmap(watermark, w, h, null);
        //save all clip
        cv.save();//??????
        //store
        cv.restore();//??????
        return newb;
    }


    public static Bitmap drawableToBitamp(Drawable drawable) {
        return drawableToBitamp(drawable, 1, 1);
    }

    public static Bitmap drawableToBitamp(Drawable drawable, int defaultWidth, int defaultHeight) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }
        int w = drawable.getIntrinsicWidth();
        int h = drawable.getIntrinsicHeight();
        Bitmap bitmap;
        if (drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(defaultWidth, defaultHeight, Config.ARGB_8888);
            w = defaultWidth;
            h = defaultHeight;
        } else {
            Config config =
                    drawable.getOpacity() != PixelFormat.OPAQUE ? Config.ARGB_8888
                            : Config.RGB_565;
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), config);
        }
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, w, h);
        drawable.draw(canvas);
        return bitmap;
    }

    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, int cornerRadius) {
        try {
            int roundPx = cornerRadius;
            Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                    bitmap.getHeight(), Config.ARGB_8888);
            Canvas canvas = new Canvas(output);
            final Paint paint = new Paint();
            final Rect rect = new Rect(0, 0, bitmap.getWidth(),
                    bitmap.getHeight());
            final RectF rectF = new RectF(new Rect(0, 0, bitmap.getWidth(),
                    bitmap.getHeight()));

            paint.setAntiAlias(true);
            canvas.drawARGB(0, 0, 0, 0);
            paint.setColor(Color.BLACK);
            canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));

            final Rect src = new Rect(0, 0, bitmap.getWidth(),
                    bitmap.getHeight());

            canvas.drawBitmap(bitmap, src, rect, paint);
            return output;
        } catch (Exception e) {
            return bitmap;
        }
    }

    public static Bitmap decodeOriginalFile(String filePath) {

        BitmapFactory.Options opts = new BitmapFactory.Options();

        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, opts);

        opts.inJustDecodeBounds = false;
        Bitmap bmp = null;

        try {
            bmp = BitmapFactory.decodeFile(filePath, opts);
        } catch (OutOfMemoryError err) {
            err.printStackTrace();
        }

        return bmp;
    }

    public static String getPicPath(Context context, Uri uri) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT &&
                DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (FileUtils.isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                if (split != null && split.length > 1) { //?????????????????????
                    final String type = split[0];
                    if ("primary".equalsIgnoreCase(type)) {
                        return Environment.getExternalStorageDirectory() + "/"
                                + split[1];
                    }
                }
            }
            // DownloadsProvider
            else if (FileUtils.isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"),
                        Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (FileUtils.isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                if (split != null && split.length > 1) {//?????????????????????
                    final String type = split[0];

                    Uri contentUri = null;
                    if ("image".equals(type)) {
                        contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                    } else if ("video".equals(type)) {
                        contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                    } else if ("audio".equals(type)) {
                        contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                    }

                    final String selection = "_id=?";
                    final String[] selectionArgs = new String[]{split[1]};

                    return getDataColumn(context, contentUri, selection,
                            selectionArgs);
                }
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            // Return the remote address
            if (FileUtils.isGooglePhotosUri(uri)) {
                return uri.getLastPathSegment();
            }
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri,
                                       String selection, String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};

        try {
            cursor = context.getContentResolver().query(uri, projection,
                    selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    public static Drawable getTintDrawable(Drawable drawable, @ColorInt int color, Rect bound) {
        if (null == drawable) {
            return null;
        }
        Drawable drawable1 = mutateDrawable(drawable);
        if (bound == null) {
            drawable1.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        } else {
            drawable1.setBounds(bound);
        }
        DrawableCompat.setTint(drawable1, color);
        return drawable1;
    }

    @Nullable
    public static Drawable mutateDrawable(Drawable drawable) {
        if (null == drawable) {
            return null;
        }
        Drawable.ConstantState state = drawable.getConstantState();
        return DrawableCompat.wrap(state == null ? drawable : state.newDrawable()).mutate();
    }

    public static Bitmap scalImage(Bitmap bgimage, double newWidth,
                                   double newHeight) {
        int width = bgimage.getWidth();
        int height = bgimage.getHeight();
        Matrix matrix = new Matrix();
        // ?????????????????????
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // ??????????????????
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap bitmap = Bitmap.createBitmap(bgimage, 0, 0, width,
                height, matrix, true);
        return bitmap;
    }


    /**
     * if return True, recommend set the text color to black
     *
     * @param bitmap
     * @return
     */
    public static boolean isHighBrightnessBitmap(Bitmap bitmap) {
        float result[] = getAverageColorInfoForBitmap(bitmap);
        int color = (int) result[0];
        float rate = result[1];
        if (rate > DARK_COLOR_MIN_RATE) {
            return false;
        }

        float[] hsv = new float[3];
        Color.RGBToHSV(Color.red(color), Color.green(color), Color.blue(color), hsv);
        return isHighBrightnessBitmap(hsv[0], hsv[1], hsv[2]);
    }

    /**
     * if return True, recommend set the text color to black
     */
    public static boolean isHighBrightnessBitmap(float hue, float sat, float val) {
        boolean isBrightness = false;
        if (hue <= MAX_HUE_VALUE && hue >= MIN_HUE_VALUE) { // ??????????????????
            if (val >= MAX_BRIGHTNESS) {
                isBrightness = true;
            }
        } else { // ??????????????????
            if (sat <= MAX_SATURATION && val >= MAX_BRIGHTNESS) {
                isBrightness = true;
            }
        }
        return isBrightness;
    }

    /**
     * ??????Bitmap??????????????????????????????????????????
     *
     * @param bitmap ?????????????????????
     * @return float[2] result[0] is average color [0..0xffffffff], result[1] is black color rate [0.. 1]
     */
    private static float[] getAverageColorInfoForBitmap(Bitmap bitmap) {
        if (bitmap == null || bitmap.isRecycled()) {
            return new float[]{0, 1};
        }

        int redValue = 0;
        int greenValue = 0;
        int blueValue = 0;

        int rate = bitmap.getWidth() / SCALED_BITMAP_WIDTH;
        rate = rate == 0 ? 1 : rate;
        int newW = Math.max(bitmap.getWidth() / rate, 0);
        int newH = Math.max(bitmap.getHeight() / rate, 0);
        Bitmap tmpBitmap = null;
        try {
            tmpBitmap = (rate == 1 ? bitmap : Bitmap.createScaledBitmap(bitmap, newW, newH, false));
        } catch (OutOfMemoryError e) {
            return new float[]{0, 1};
        }
        int index = 0;
        int blackCount = 0;
        int[] colorMapPixels = new int[newW * newH];
        tmpBitmap.getPixels(colorMapPixels, 0, newW, 0, 0, newW, newH);
        for (int y = 0; y < newH; y++) {
            for (int x = 0; x < newW; x++) {
                int color = colorMapPixels[index];
                int alpha = Color.alpha(color);
                int red = Color.red(color);
                int green = Color.green(color);
                int blue = Color.blue(color);
                redValue += red;
                greenValue += green;
                blueValue += blue;
                if (alpha > DARK_COLOR_ALPHA && red < DARK_COLOR_RED && green < DARK_COLOR_GREEN && blue < DARK_COLOR_BLUE) {
                    blackCount++;
                }
                index++;
            }
        }
        if (tmpBitmap != bitmap) {
            tmpBitmap.recycle();
        }
        float[] result = new float[]{Color.rgb(redValue / index, greenValue / index, blueValue / index), 1.0f * blackCount / index};
        return result;
    }

    public static Bitmap blurBitmap(Context context, float blurRadius, float scaleDegree, Bitmap image) {
        if (!Constant.ATLEAST_JB_MR1) {
            return image;
        }
        // ??????????????????????????????
        int width = Math.round(image.getWidth() * scaleDegree);
        int height = Math.round(image.getHeight() * scaleDegree);

        // ?????????????????????????????????????????????
        Bitmap inputBitmap = Bitmap.createScaledBitmap(image, width, height, false);
        // ????????????????????????????????????
        Bitmap outputBitmap = Bitmap.createBitmap(inputBitmap);

        // ??????RenderScript????????????
        RenderScript rs = RenderScript.create(context);
        // ???????????????????????????RenderScript???????????????
        ScriptIntrinsicBlur blurScript = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));

        // ??????RenderScript???????????????VM???????????????,??????????????????Allocation?????????????????????????????????
        // ??????Allocation????????????????????????????????????,????????????copyTo()?????????????????????
        Allocation tmpIn = Allocation.createFromBitmap(rs, inputBitmap);
        Allocation tmpOut = Allocation.createFromBitmap(rs, outputBitmap);

        // ???????????????????????????, 25f??????????????????
        blurScript.setRadius(blurRadius);
        // ??????blurScript?????????????????????
        blurScript.setInput(tmpIn);
        // ???????????????????????????????????????
        blurScript.forEach(tmpOut);
        // ??????????????????Allocation???
        tmpOut.copyTo(outputBitmap);
        return outputBitmap;
    }

    public static Bitmap compressImage(Bitmap bitmap, int limitSize) {
        // ????????????????????????????????????
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, output);
        float zoom = (float) Math.sqrt(limitSize * 1024 / (float) output.toByteArray().length); //??????????????????

        // ??????????????????
        Matrix matrix = new Matrix();
        matrix.setScale(zoom, zoom);

        // ???????????????????????????bitmap?????????
        Bitmap resultBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

        output.reset();

        resultBitmap.compress(Bitmap.CompressFormat.JPEG, 100, output);

        // ????????????????????????????????????????????????limitSize(K)????????????????????????????????????
        while (output.toByteArray().length > limitSize * 1024) {
            matrix.setScale(0.9f, 0.9f);//???????????? 1/10
            resultBitmap = Bitmap.createBitmap(resultBitmap, 0, 0, resultBitmap.getWidth(), resultBitmap.getHeight(), matrix, true);
            output.reset();
            resultBitmap.compress(Bitmap.CompressFormat.JPEG, 100, output);
        }
        ByteArrayInputStream bis = new ByteArrayInputStream(output.toByteArray());
        return BitmapFactory.decodeStream(bis, null, null);
    }
}
