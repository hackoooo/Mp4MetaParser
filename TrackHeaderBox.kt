package com.wandoujia.base.media

open class TrackHeaderBox(creationTime: Long,
                          modificationTime: Long,
                          size: Long,
                          type: String,
                          offset: Long)
    : MovieHeaderBox(creationTime, modificationTime, size, type, offset) {
    override fun toString(): String {
      return "TrackHeaderBox: offset:$offset, type:$type, size:$size, " +
          "creationTime:$creationTime, modificationTime: $modificationTime"
    }
  }