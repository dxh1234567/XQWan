package com.jj.base.common.glide

import android.content.Context

import com.bumptech.glide.Glide
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.module.AppGlideModule

import java.io.InputStream

@GlideModule
class VoiceGlideModule : AppGlideModule() {
    override fun registerComponents(context: Context, glide: Glide, registry: Registry) {
        registry.prepend(String::class.java, InputStream::class.java,
                CustomDiskModelLoader.Factory())
    }
}
