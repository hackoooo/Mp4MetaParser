package com.wandoujia.base.media

open class MovieHeaderBox(var creationTime: Long,
                          var modificationTime: Long,
                          size: Long,
                          type: String,
                          offset: Long)
    : Box(size, type, offset) {
    override fun toString(): String {
      return "MovieHeaderBox: offset:$offset, type:$type, size:$size, " +
          "creationTime:$creationTime, modificationTime: $modificationTime"
    }
  }