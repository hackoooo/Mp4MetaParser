package com.wandoujia.base.media

open class MediaHeaderBox(creationTime: Long,
                          modificationTime: Long,
                          size: Long,
                          type: String,
                          offset: Long)
    : MovieHeaderBox(creationTime, modificationTime, size, type, offset) {
    override fun toString(): String {
      return "MediaHeaderBox: offset:$offset, type:$type, size:$size, " +
          "creationTime:$creationTime, modificationTime: $modificationTime"
    }
  }