package com.wandoujia.base.media

import com.wandoujia.base.media.Mp4MetaParser.Companion.change1970To1904Timestamp
import com.wandoujia.base.media.Mp4MetaParser.Companion.print1904To1970Timestamp

open class Mp4Box(val parser: Mp4MetaParser, val container: Box) {
  var moovBox: MoovBox? = null

  companion object{
    fun parse(parser: Mp4MetaParser): Mp4Box {
      var box = parser.readBox()
      if (box.type != Mp4MetaParser.TYPE_FTYP) {
        throw Mp4BoxException("this is not a valid mp4 file")
      }

      val result = Mp4Box(parser, box)
      while (parser.seekNextBox(box) != Mp4MetaParser.OFFSET_OUT_OF_BOUND) {
        box = parser.readBox()
        if (box.type == Mp4MetaParser.TYPE_MOOV) {
          result.moovBox = MoovBox.parse(parser, box)
          break
        }
      }

      if (result.moovBox == null) {
        throw Mp4BoxException("could not find moov box")
      }
      return result
    }
  }

    /**
     * @param createTimestamp mills since 1970
     * @param modificationTimestamp mills since 1970
     */
    fun updateTime(createTimeMills: Long, modificationTimeMills: Long) {
      moovBox?.apply {
        val targetCreateTime = change1970To1904Timestamp(createTimeMills)
        val targetModificationTime = change1970To1904Timestamp(modificationTimeMills)
        updateTime(parser, targetCreateTime, targetModificationTime)
      }
    }

    fun readTime(){
      moovBox?.apply {
        movieHeaderBox?.apply {
          print1904To1970Timestamp(creationTime, modificationTime)
        }
        trackBoxList.forEach {
          it.trackHeaderBox?.apply {
            print1904To1970Timestamp(creationTime, modificationTime)
          }
          it.mediaBox?.mediaHeaderBox?.apply {
            print1904To1970Timestamp(creationTime, modificationTime)
          }
        }
      }
    }

    fun closeQuietly() {
      parser.closeQuietly()
    }

    override fun toString(): String {
      return """ Mp4Box: offset:${container.offset}, type:${container.type}, size:${container.size}
        $moovBox
      """
    }
  }