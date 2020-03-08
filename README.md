# Mp4MetaParser
Support read and write mp4 creation time and modification time.

You can do this job using many 3rd party libraries, such ffmpeg etc, but the disavantage is that the size of the library is too large.
If you just want to modify the meta data such creation and modification time of a mp4 file, you can use this, its size is just 11 KB.

# Usage
```kotlin
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
```
