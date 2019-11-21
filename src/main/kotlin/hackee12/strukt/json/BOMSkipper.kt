package hackee12.strukt.json

import com.google.gson.stream.JsonReader
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.Reader

fun jsonReader(jsonFile: File): JsonReader {
    val fr = FileReader(jsonFile)
    val br = BufferedReader(fr)
    return JsonReader(skipBOM(br))
}

private fun skipBOM(reader: Reader): Reader {
    reader.mark(1)
    val oneCharArray = CharArray(1)
    reader.read(oneCharArray)
    if (oneCharArray[0] != BOM) reader.reset()
    return reader
}

private const val BOM: Char = '\ufeff'