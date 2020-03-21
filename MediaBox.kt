package com.wandoujia.base.media

import com.wandoujia.base.media.Mp4MetaParser.Companion.OFFSET_OUT_OF_BOUND
import com.wandoujia.base.media.Mp4MetaParser.Companion.TYPE_MDHD

open class MediaBox(val container: Box) {
    var mediaHeaderBox: MediaHeaderBox? = null
    companion object {
      fun parse(parser: Mp4MetaParser, containerBox: Box): MediaBox {
        val result = MediaBox(containerBox)
        do {
          var box = parser.readBox()
          if(box.type == TYPE_MDHD){
            result.mediaHeaderBox = parser.readMediaHeaderBox(box)
            break
          }
        } while (parser.seekNextBox(box, containerBox.boundOffset()) != OFFSET_OUT_OF_BOUND)

        if (result.mediaHeaderBox == null) {
          throw Mp4BoxException("could not find media header in media box")
        }
        return result
      }
    }
    override fun toString(): String {
      return """ MediaBox: offset:$String, type:${container.type}, size:${container.size}
        $mediaHeaderBox """
    }
  }