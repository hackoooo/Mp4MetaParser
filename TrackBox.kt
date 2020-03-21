package com.wandoujia.base.media

import com.wandoujia.base.media.Mp4MetaParser.Companion.OFFSET_OUT_OF_BOUND
import com.wandoujia.base.media.Mp4MetaParser.Companion.TYPE_MDIA
import com.wandoujia.base.media.Mp4MetaParser.Companion.TYPE_TKHD

open class TrackBox(val container: Box) {
  var trackHeaderBox: TrackHeaderBox? = null
  var mediaBox: MediaBox? = null
    companion object{
      fun parse(parser: Mp4MetaParser, containerBox: Box): TrackBox {
        val result = TrackBox(containerBox)
        do {
          var box = parser.readBox()
          when (box.type) {
            TYPE_TKHD -> {
              result.trackHeaderBox = parser.readTrackHeaderBox(box)
            }
            TYPE_MDIA -> {
              result.mediaBox = MediaBox.parse(parser, box)
            }
            else -> {
              //ignored.
            }
          }
        } while (parser.seekNextBox(box, containerBox.boundOffset()) != OFFSET_OUT_OF_BOUND)
        if (result.trackHeaderBox == null) {
          throw Mp4BoxException("could not find track header box")
        }
        if (result.mediaBox == null) {
          throw Mp4BoxException("could not find media box in track")
        }
        return result
      }
    }

    fun updateTime(parser: Mp4MetaParser, createTimestamp: Long, modificationTimestamp: Long) {
      trackHeaderBox?.apply {
        creationTime = createTimestamp
        modificationTime = modificationTimestamp
        parser.writeMovieHeaderBox(this)
      }
      mediaBox?.mediaHeaderBox?.apply {
        creationTime = createTimestamp
        modificationTime = modificationTimestamp
        parser.writeMovieHeaderBox(this)
      }
    }

  override fun toString(): String {
      return """ TrackBox: offset:$String, type:${container.type}, size:${container.size}
        $trackHeaderBox
        $mediaBox """
    }
  }
