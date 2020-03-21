package com.wandoujia.base.media

import com.wandoujia.base.media.Mp4MetaParser.Companion.OFFSET_OUT_OF_BOUND
import com.wandoujia.base.media.Mp4MetaParser.Companion.TYPE_MVHD
import com.wandoujia.base.media.Mp4MetaParser.Companion.TYPE_TRAK

open class MoovBox(val container: Box) {
  var movieHeaderBox: MovieHeaderBox? = null
    val trackBoxList = ArrayList<TrackBox>()

    companion object{
      fun parse(parser: Mp4MetaParser, containerBox: Box):MoovBox {
        val result = MoovBox(containerBox)
        do {
          var box = parser.readBox()
          when(box.type){
            TYPE_MVHD -> {
              result.movieHeaderBox = parser.readMovieHeaderBox(box)
            }
            TYPE_TRAK -> {
              result.trackBoxList.add(TrackBox.parse(parser, box))
            }
            else -> {
              //ignored.
            }
          }
        } while (parser.seekNextBox(box, containerBox.boundOffset()) != OFFSET_OUT_OF_BOUND)

        if (null == result.movieHeaderBox) {
          throw Mp4BoxException("could not find movie header box")
        }
        if (result.trackBoxList.isEmpty()){
          throw Mp4BoxException("could not find track box")
        }
        return result
      }
    }

  fun updateTime(parser: Mp4MetaParser, createTimestamp: Long, modificationTimestamp: Long) {
    movieHeaderBox?.apply {
      creationTime = createTimestamp
      modificationTime = modificationTimestamp
      parser.writeMovieHeaderBox(this)
    }
    trackBoxList.forEach {
      it.updateTime(parser, createTimestamp, modificationTimestamp)
    }
  }

  override fun toString(): String {
      return """ MoovBox: offset:${container.offset}, type:${container.type}, size:${container.size}
        $movieHeaderBox
        $trackBoxList """
    }
  }