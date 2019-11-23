package hackee12.strukt.core.swagger20

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import hackee12.strukt.core.SO
import hackee12.strukt.core.exception.fail

class Swagger21(private val rootJE: JsonElement) {
    fun parse(parentPath: String, parentJE: JsonElement, entryName: String): List<SO> =
            parse(parentPath, parentJE.asJsonObject[entryName], entryName, inRequired = false, inAllOf = false)

    fun parse22(parentPath: String, parentJE: JsonElement, entryName: String): List<SO> {
        val sos = parse(parentPath, parentJE.asJsonObject[entryName], "", inRequired = false, inAllOf = false)
        return sos.subList(1, sos.size)
    }

    private fun parse(parentPath: String, targetJE: JsonElement, entryName: String, inRequired: Boolean, inAllOf: Boolean): List<SO> {
        return when {
            targetJE.isJsonNull -> fail("This is a bug. JsonNull was not expected.")
            targetJE.isJsonPrimitive -> fail("This is a bug. JsonPrimitive was not expected.")
            targetJE.isJsonObject -> {
                val jo = targetJE.asJsonObject
                when {
                    jo.isRef() -> fromRef(parentPath, targetJE, entryName, inRequired, inAllOf)
                    jo.isSchema() -> fromSchema(parentPath, targetJE, entryName, inRequired, inAllOf)
                    jo.isArray() -> fromArray(parentPath, targetJE, entryName, inRequired, inAllOf)
                    jo.isAllOf() -> fromAllOf(parentPath, targetJE, entryName, inRequired)
                    jo.isContainer() -> fromContainer(parentPath, targetJE, entryName, inRequired, inAllOf)
                    else -> fromPlain(parentPath, targetJE, entryName, inRequired)
                }
            }
            targetJE.isJsonArray -> fail("This is a bug. JsonArray was not expected.")
            else -> fail("This should never happen.")
        }
    }

    private fun fromRef(parentPath: String, targetJE: JsonElement, entryName: String, inRequired: Boolean, inAllOf: Boolean): List<SO> {
        val targetJO = targetJE.asJsonObject
        val refString = targetJO[REF].asString
        val refPath = refString.substringAfter("#/").split("/")
        if (refPath.size != 2) fail("This is a bug.")
        val definedIn = refPath[0]
        val definedJOId = refPath[1]
        val definedInJE = rootJE.asJsonObject[definedIn]
        val je = definedInJE.asJsonObject[definedJOId]
        val sos = mutableListOf<SO>()
        sos.addAll(parse(parentPath, je, entryName, inRequired, inAllOf))
        return sos
    }

    private fun fromSchema(parentPath: String, targetJE: JsonElement, entryName: String, inRequired: Boolean, inAllOf: Boolean): List<SO> {
        val sos = mutableListOf<SO>()
        sos.addAll(parse(parentPath, targetJE.asJsonObject[SCHEMA], entryName, inRequired, inAllOf))
        return sos
    }

    private fun fromArray(parentPath: String, targetJE: JsonElement, entryName: String, inRequired: Boolean, inAllOf: Boolean): List<SO> {
        val sob = SO.SOB()
                .parentPath(parentPath)
                .entryName(entryName)
        for (entry in targetJE.asJsonObject.entrySet()) {
            val (k, v) = entry
            when (k) {
                TYPE -> sob.type(v.asString)
                DESCRIPTION -> sob.description(v.asString)
                TITLE -> sob.description(v.asString)
                // TODO: analyze
                ITEMS -> {
                }
                else -> sob.leftovers(k)
            }
        }
        val so = sob.build()
        val sos = mutableListOf(so)
        sos.addAll(parse(so.path, targetJE.asJsonObject[ITEMS], "", inRequired, inAllOf))
        return sos
    }

    private fun fromAllOf(parentPath: String, targetJE: JsonElement, entryName: String, inRequired: Boolean): List<SO> {
        val sob = SO.SOB()
                .parentPath(parentPath)
                .entryName(entryName)
        for (entry in targetJE.asJsonObject.entrySet()) {
            val (k, v) = entry
            when (k) {
                TYPE -> sob.type(v.asString)
                DESCRIPTION -> sob.description(v.asString)
                TITLE -> sob.description(v.asString)
                // TODO: analyze
                ALL_OF -> {
                }
                else -> sob.leftovers(k)
            }
        }
        val so = sob.build()
        val sos = mutableListOf(so)
        for (entry in targetJE.asJsonObject[ALL_OF].asJsonArray) {
            sos.addAll(parse(so.path, entry, "", inRequired, inAllOf = true))
        }
        return sos
    }

    // TODO: REVISE THIS FUNCTION
    private fun fromContainer(parentPath: String, targetJE: JsonElement, entryName: String, inRequired: Boolean, inAllOf: Boolean): List<SO> {

        // TODO: REVISE THIS FUNCTION
        fun resolveProperties(parentPath: String, parentJE: JsonElement, inAllOf: Boolean): List<SO> {

            val requiredJA: JsonArray? = parentJE.asJsonObject[REQUIRED]?.asJsonArray
            val rpSet = mutableSetOf<String>()
            requiredJA?.forEach { rpSet.add(it.asString) }

            val allProps = mutableListOf<Map.Entry<String, JsonElement>>()
            parentJE.asJsonObject[PROPERTIES]?.asJsonObject?.entrySet()?.toCollection(allProps)
            // TODO: NEEDS TEST COVERAGE
            parentJE.asJsonObject.entrySet().filter { (k, _) -> k == ADDITIONAL_PROPERTIES }.forEach { allProps.add(it) }

            val sos = mutableListOf<SO>()
            for (p in allProps) {
                val (k, v) = p
                sos.addAll(parse(parentPath, v, k, k in rpSet, inAllOf))
            }
            return sos
        }

        val sos = mutableListOf<SO>()
        if (inAllOf) {
            sos.addAll(resolveProperties(parentPath, targetJE, inAllOf = true))
        } else {
            val sob = SO.SOB()
            sob.parentPath(parentPath)
            sob.entryName(entryName)
            if (inRequired) sob.inRequired(true)
            val targetJO: JsonObject = targetJE.asJsonObject
            for (entry in targetJO.entrySet()) {
                val (k, v) = entry
                // TODO: analyze
                if (v.isJsonPrimitive) when (k) {
                    TYPE -> sob.type(v.asString)
                    DESCRIPTION -> sob.description(v.asString)
                    TITLE -> sob.title(v.asString)
                    // TODO: analyze
                    REQUIRED -> {
                    }
                    // TODO: analyze
                    PROPERTIES -> {
                    }
                    else -> sob.leftovers(k)
                }
            }
            val so = sob.build()
            sos.add(so)
            sos.addAll(resolveProperties(so.path, targetJE, inAllOf = false))
        }
        return sos
    }


    private fun fromPlain(parentPath: String, targetJE: JsonElement, entryName: String, inRequired: Boolean): List<SO> {
        val sob = SO.SOB()
        sob.parentPath(parentPath)
        sob.entryName(entryName)
        if (inRequired) sob.inRequired(true)
        val targetJO = targetJE.asJsonObject
        for (entry in targetJO.entrySet()) {
            val (k, v) = entry
            if (v.isJsonPrimitive || v.isJsonArray) when (k) {
                TYPE -> sob.type(v.asString)
                IS_REQUIRED -> sob.isRequired(v.asBoolean)
                DESCRIPTION -> sob.description(v.asString)
                TITLE -> sob.title(v.asString)
                NAME -> sob.name(v.asString)
                ID -> sob.id(v.asString)
                OPERATION_ID -> sob.operationId(v.asString)
                FORMAT -> sob.format(v.asString)
                PATTERN -> sob.pattern(v.asString)
                EXAMPLE -> sob.example(v.asString)
                ENUM -> sob.authorizedValues(v.toString())
                MIN -> sob.min(v.asInt)
                MAX -> sob.max(v.asInt)
                MIN_LENGTH -> sob.minLength(v.asInt)
                MAX_LENGTH -> sob.maxLength(v.asInt)
                MIN_OCCURRENCE -> sob.minOccurrence(v.asInt)
                MAX_OCCURRENCE -> sob.maxOccurrence(v.asInt)
                else -> sob.leftovers(k)
            }
        }
        return listOf(sob.build())
    }

    private fun JsonElement.isRef(): Boolean = asJsonObject.has(REF)
    private fun JsonElement.isSchema(): Boolean = asJsonObject.has(SCHEMA)
    private fun JsonElement.isAllOf(): Boolean = asJsonObject.has(ALL_OF)
    private fun JsonElement.isArray(): Boolean = asJsonObject.has(ITEMS) && asJsonObject[TYPE].asString == ARRAY
    private fun JsonElement.isContainer(): Boolean = asJsonObject.has(TYPE) && asJsonObject[TYPE].asString == OBJECT

}

private const val REF = "\$ref"
private const val SCHEMA = "schema"
private const val ALL_OF = "allOf"
private const val ITEMS = "items"
private const val ARRAY = "array"

private const val TYPE = "type"
private const val OBJECT = "object"
private const val IS_REQUIRED = "isRequired"
private const val DESCRIPTION = "description"
private const val TITLE = "title"
private const val NAME = "name"
private const val ID = "id"
private const val OPERATION_ID = "operationId"

private const val REQUIRED = "required"
private const val PROPERTIES = "properties"
private const val ADDITIONAL_PROPERTIES = "additionalProperties"

private const val FORMAT = "format"
private const val PATTERN = "pattern"
private const val EXAMPLE = "example"
private const val ENUM = "enum"

private const val MIN = "min"
private const val MAX = "max"
private const val MIN_LENGTH = "minLength"
private const val MAX_LENGTH = "maxLength"
private const val MIN_OCCURRENCE = "minOccurrence"
private const val MAX_OCCURRENCE = "maxOccurrence"
