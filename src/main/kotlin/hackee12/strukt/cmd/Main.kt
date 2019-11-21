package hackee12.strukt.cmd

import com.google.gson.JsonParser
import hackee12.strukt.core.swagger20.Swagger21
import hackee12.strukt.json.jsonReader
import java.io.File
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    if (args.size != 3) exitProcess(64)
    val jsonName = args[0]
    val definedIn = args[1]
    val entryName = args[2]
    val jsonReader = jsonReader(File(jsonName))
    val rootJE = JsonParser().parse(jsonReader)
    Swagger21(rootJE)
            .parse22(parentPath = "", parentJE = rootJE.asJsonObject[definedIn], entryName = entryName)
            .forEach { println(it.asTSV()) }
}