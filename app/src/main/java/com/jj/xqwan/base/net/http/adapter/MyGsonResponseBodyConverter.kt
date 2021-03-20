package com.jj.xqwan.base.net.http.adapter

import android.util.Log
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.google.gson.TypeAdapter
import com.jj.xqwan.base.net.ErrCode
import com.jj.xqwan.base.net.NetworkException
import okhttp3.ResponseBody
import retrofit2.Converter
import java.io.IOException


/**
 *  Created By duXiaHui
 *  on 2021/1/31
 */
internal class MyGsonResponseBodyConverter<T>(
    private val adapter: TypeAdapter<T>
) : Converter<ResponseBody, T> {
    private val parser: JsonParser = JsonParser()

    @Throws(IOException::class)
    override fun convert(value: ResponseBody): T {
        value.use {
            val obj = parser.parse(value.string()).asJsonObject
            val data = obj.get("data") ?: JsonObject()
            val errCode = obj.get("code").asInt
            if (errCode == ErrCode.ERR_OK) {
                if (data is JsonObject &&
                    data.has("hasNextPage") &&
                    !data.has("rows")
                ) {
                    //加载数据为分页数据，为便于处理，统一返回数据列表的可以为 "list"
                    var list: JsonArray? = null
                    data.keySet().forEach {
                        if (data.get(it) is JsonArray) {
                            list = data.get(it) as JsonArray
                            return@forEach
                        }
                    }
                    list?.let {
                        data.add("rows", it)
                    }
                }

                Log.e("9999", obj.toString())

                return adapter.fromJsonTree(data)
            } else {
                //errMsg字段不一定是string
                val errMsg = obj.get("message")?.toString()?.replace(": null", "") ?: ""
                throw NetworkException(errCode, errMsg)
            }
        }
    }
}
