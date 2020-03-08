# Mp4MetaParser
Read and write mp4 creation time and modification time

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
