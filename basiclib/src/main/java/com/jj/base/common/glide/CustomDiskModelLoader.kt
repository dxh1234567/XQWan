package com.jj.base.common.glide

import com.bumptech.glide.Priority
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.Options
import com.bumptech.glide.load.data.DataFetcher
import com.bumptech.glide.load.model.ModelLoader
import com.bumptech.glide.load.model.ModelLoaderFactory
import com.bumptech.glide.load.model.MultiModelLoaderFactory
import com.bumptech.glide.signature.ObjectKey
import com.jj.base.common.cache.FileCacheManager
import com.jj.base.common.cache.ICacheManager

import java.io.IOException
import java.io.InputStream

class CustomDiskModelLoader : ModelLoader<String, InputStream> {
    override fun buildLoadData(model: String, width: Int, height: Int, options: Options): ModelLoader.LoadData<InputStream>? {
        return ModelLoader.LoadData(ObjectKey(model), CustomDiskDataFetcher(model))
    }

    override fun handles(model: String): Boolean {
        return FileCacheManager.getInstance().contains(model, ICacheManager.CacheType.IMAGE)
    }

    inner class CustomDiskDataFetcher internal constructor(private val model: String) : DataFetcher<InputStream> {
        private var data: InputStream? = null

        override fun loadData(priority: Priority, callback: DataFetcher.DataCallback<in InputStream>) {
            data = FileCacheManager.getInstance().getStream(model,
                    ICacheManager.CacheType.IMAGE)
            if (data == null) {
                callback.onLoadFailed(RuntimeException("null value for this key"))
            } else {
                callback.onDataReady(data)
            }
        }

        override fun cleanup() {
            if (data != null) {
                try {
                    data!!.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }

            }
        }

        override fun cancel() {}

        override fun getDataClass(): Class<InputStream> {
            return InputStream::class.java
        }

        override fun getDataSource(): DataSource {
            return DataSource.LOCAL
        }
    }

    class Factory : ModelLoaderFactory<String, InputStream> {

        override fun build(multiFactory: MultiModelLoaderFactory): ModelLoader<String, InputStream> {
            return CustomDiskModelLoader()
        }

        override fun teardown() {

        }
    }
}
