package com.wandoujia.base.media

import java.io.IOException
import java.io.RandomAccessFile

/**
 * Parse and modify mp4 meta data
 * Just support parse and write the creation time and modification time of the mp4 file currently.
 *
 * @see https://developer.apple.com/library/archive/documentation/QuickTime/QTFF/QTFFChap2/qtff2.html
 * @see https://blog.csdn.net/yefengzhichen/article/details/85562733
 * @see https://download.tsi.telecom-paristech.fr/gpac/mp4box.js/filereader.html
 *
 * @author maxiaohui 2020/03/07
 *
 * Usage:
   val file = RandomAccessFile(filePath, "rw")
   var mp4Box: Mp4MetaParser.Mp4Box? = null
   try {
     mp4Box = Mp4MetaParser.parse(file)
     Log.e("test", mp4Box.toString())
     mp4Box.readTime()
     Log.e("test","updating ...")
     mp4Box.updateTime(System.currentTimeMillis()-86400*2000, System.currentTimeMillis()-86400*1000)
     mp4Box.readTime()
   } catch (e: Exception){
     // log etc.
   }
   finally {
     mp4Box?.closeQuietly()
   }
 */
open class Mp4MetaParser(private val file: RandomAccessFile) {

  companion object{
    private const val TAG = "Mp4MetaParser"
    private const val BOX_SIZE_BYTES = 4
    private const val BOX_TYPE_BYTES = 4
    const val BOX_HEADER_BYTES = BOX_SIZE_BYTES + BOX_TYPE_BYTES
    const val MOVIE_HEADER_VERSION_BYTES = 1
    const val MOVIE_HEADER_FLAG_BYTES = 3
    const val OFFSET_OUT_OF_BOUND = -1L
    /**
     * @see https://www.unixtimeconverter.io/-2082844800
     */
    const val TIME_STAMP_1904_01 = -2082844800L

    const val TYPE_FTYP = "ftyp"
    const val TYPE_MOOV = "moov"
    const val TYPE_MVHD = "mvhd"
    const val TYPE_TRAK = "trak"
    const val TYPE_TKHD = "tkhd"
    const val TYPE_MDIA = "mdia"
    const val TYPE_MDHD = "mdhd"

//    private val dateFormat = SimpleDateFormat("YYYY-MM-dd HH:m:s")

    fun parse(file: RandomAccessFile): Mp4Box {
      return Mp4Box.parse(Mp4MetaParser(file))
    }

    fun print1904To1970Timestamp(creationTime: Long, modificationTime: Long){
      val createTimestampMills = (creationTime + TIME_STAMP_1904_01) * 1000
      val modificationTimestampMills = (modificationTime + TIME_STAMP_1904_01) * 1000
//      Log.e(TAG, "${dateFormat.format(createTimestampMills)} ${dateFormat.format(modificationTimestampMills)}")
    }

    fun change1970To1904Timestamp(timestampMills: Long): Long {
      return timestampMills / 1000 - TIME_STAMP_1904_01
    }
  }
  fun closeQuietly() {
    try {
      file.close()
    } catch (e: Exception) {
      e.printStackTrace()
    }
  }

  // Java has no unsigned int data type, so we convert it to long type
  private fun readUnsignedIntAsLong(): Long {
    return file.readInt().toLong() and 0xFFFFFFFFL
  }

  fun readBox(): Box {
    var size =  readUnsignedIntAsLong()
    var type = ByteArray(BOX_TYPE_BYTES)
    if (file.read(type) != BOX_TYPE_BYTES) {
      throw IOException("read box size error")
    }
    return Box(size, String(type), file.filePointer - BOX_HEADER_BYTES)
  }

  fun seekNextBox(box: Box): Long {
    return seekNextBox(box, file.length())
  }

  fun seekNextBox(box: Box, offsetLimit: Long): Long {
    val nextBoxOffset = box.boundOffset()
    if (nextBoxOffset >= offsetLimit) {
      return OFFSET_OUT_OF_BOUND
    }
    file.seek(nextBoxOffset)
    return file.filePointer
  }

  fun readMovieHeaderBox(box: Box): MovieHeaderBox {
    file.skipBytes(MOVIE_HEADER_VERSION_BYTES + MOVIE_HEADER_FLAG_BYTES)
    val creationTime = readUnsignedIntAsLong()
    val modificationTime = readUnsignedIntAsLong()
    return MovieHeaderBox(creationTime, modificationTime, box.size, box.type, box.offset)
  }

  fun writeMovieHeaderBox(box: MovieHeaderBox) {
    file.seek(box.offset)
    file.skipBytes(BOX_HEADER_BYTES + MOVIE_HEADER_VERSION_BYTES + MOVIE_HEADER_FLAG_BYTES)
    file.writeInt(box.creationTime.toInt())
    file.writeInt(box.modificationTime.toInt())
  }

  fun readTrackHeaderBox(box: Box): TrackHeaderBox {
    // The first six fields of track header box is the same as movie header box!
    val box = readMovieHeaderBox(box)
    return TrackHeaderBox(box.creationTime, box.modificationTime, box.size, box.type, box.offset)
  }

  fun readMediaHeaderBox(box: Box): MediaHeaderBox {
    // The first six fields of media header box is the same as movie header box!
    val box = readMovieHeaderBox(box)
    return MediaHeaderBox(box.creationTime, box.modificationTime, box.size, box.type, box.offset)
  }
}

