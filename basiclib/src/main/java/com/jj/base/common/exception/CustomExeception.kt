package com.jj.base.common.exception

class FileNotExistException(val file: String? = null) : Exception("文件不存在:$file")