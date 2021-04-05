package com.jj.base.exts

import android.content.Intent
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import androidx.core.content.FileProvider
import com.jj.base.utils.Utility
import java.io.File

fun File.shareByIntent() {
    if (exists()) {
        val intent = Intent().apply {
            action = Intent.ACTION_SEND
            type = getMimeType()
        }
        val uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            FileProvider.getUriForFile(Utility.getApplication(), Utility.getApplication().packageName + ".provider", this)
        } else {
            Uri.fromFile(this)
        }
        intent.putExtra(Intent.EXTRA_STREAM, uri)

        Utility.getApplication().startActivity(Intent.createChooser(intent, "").apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        })
    }
}

fun File.getMimeType(): String {
    val mmr = MediaMetadataRetriever()
    var mime = "*/*"
    if (absolutePath != null) {
        try {
            mmr.setDataSource(absolutePath)
            mime = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_MIMETYPE)
        } catch (e: IllegalStateException) {
            return mime
        } catch (e: IllegalArgumentException) {
            return mime
        } catch (e: RuntimeException) {
            return mime
        }

    }
    return mime
}