package com.wandoujia.base.media

/**
   * @param size size of this box
   * @param type type of this box
   * @param offset offset from the beginning of file
   */
  open class Box(val size: Long, val type: String, val offset: Long) {
    // return the box bound's offset in the file
    fun boundOffset(): Long {
      return offset + size
    }
  }