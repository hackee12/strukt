package hackee12.strukt.core

import java.util.StringJoiner

data class SO private constructor(
        val parentPath: String,
        val entryName: String,

        val name: String?,
        val title: String?,
        val id: String?,
        val operationId: String?,

        val type: String,
        val inRequired: Boolean?,
        val isRequired: Boolean?,
        val description: String?,
        val format: String?,
        val pattern: String?,
        val example: String?,
        val authorizedValues: String?,
        val min: Int?,
        val max: Int?,
        val minLength: Int?,
        val maxLength: Int?,
        val minOccurrence: Int?,
        val maxOccurrence: Int?,
        val minItems: Int?,
        val maxItems: Int?,

        val refDescription: String?,
        val schemaDescription: String?,

        val leftovers: List<String>,

        val path: String
) {

    class SOB(
            private var parentPath: String = "",
            private var entryName: String = "<unknown>",

            private var name: String? = null,
            private var title: String? = null,
            private var id: String? = null,
            private var operationId: String? = null,

            private var type: String = "<unknown>",
            private var inRequired: Boolean? = null,
            private var isRequired: Boolean? = null,
            private var description: String? = null,
            private var format: String? = null,
            private var pattern: String? = null,
            private var example: String? = null,
            private var authorizedValues: String? = null,
            private var min: Int? = null,
            private var max: Int? = null,
            private var minLength: Int? = null,
            private var maxLength: Int? = null,
            private var minOccurrence: Int? = null,
            private var maxOccurrence: Int? = null,
            private var minItems: Int? = null,
            private var maxItems: Int? = null,

            private var refDescription: String? = null,
            private var schemaDescription: String? = null,

            private val leftovers: MutableList<String> = mutableListOf()

    ) {
        fun parentPath(parentPath: String) = apply { this.parentPath = parentPath }
        fun entryName(entryName: String) = apply { this.entryName = entryName }

        fun name(name: String) = apply { this.name = name }
        fun title(title: String) = apply { this.title = title }
        fun id(id: String) = apply { this.id = id }
        fun operationId(operationId: String) = apply { this.operationId = operationId }

        fun type(type: String) = apply { this.type = type }
        fun inRequired(inRequired: Boolean) = apply { this.inRequired = inRequired }
        fun isRequired(isRequired: Boolean) = apply { this.isRequired = isRequired }
        fun description(desc: String) = apply { this.description = desc }
        fun format(format: String) = apply { this.format = format }
        fun pattern(pattern: String) = apply { this.pattern = pattern }
        fun example(example: String) = apply { this.example = example }
        fun authorizedValues(authorizedValues: String) = apply { this.authorizedValues = authorizedValues }
        fun min(min: Int) = apply { this.min = min }
        fun max(max: Int) = apply { this.max = max }
        fun minLength(minLength: Int) = apply { this.minLength = minLength }
        fun maxLength(maxLength: Int) = apply { this.maxLength = maxLength }
        fun minOccurrence(minOccurrence: Int) = apply { this.minOccurrence = minOccurrence }
        fun maxOccurrence(maxOccurrence: Int) = apply { this.maxOccurrence = maxOccurrence }
        fun minItems(minItems: Int) = apply { this.minItems = minItems }
        fun maxItems(maxItems: Int) = apply { this.maxItems = maxItems }

        fun refDescription(rDesk: String) = apply { this.refDescription = rDesk }
        fun schemaDescription(sDesk: String) = apply { this.schemaDescription = sDesk }

        fun leftovers(entryKey: String) = apply { this.leftovers.add(entryKey) }

        fun build(): SO {
            val sj = StringJoiner(".")
            if (parentPath.isNotEmpty()) sj.add(parentPath)
            if (entryName.isNotEmpty()) {
                if (type == "array") sj.add("$entryName[]") else sj.add(entryName)
            }

            return SO(parentPath = parentPath,
                    entryName = entryName,

                    name = name,
                    title = title,
                    id = id,
                    operationId = operationId,

                    type = type,
                    inRequired = inRequired,
                    isRequired = isRequired,
                    description = description,
                    format = format,
                    pattern = pattern,
                    example = example,
                    authorizedValues = authorizedValues,
                    min = min,
                    max = max,
                    minLength = minLength,
                    maxLength = maxLength,
                    minOccurrence = minOccurrence,
                    maxOccurrence = maxOccurrence,
                    minItems = minItems,
                    maxItems = maxItems,

                    refDescription = refDescription,
                    schemaDescription = schemaDescription,

                    leftovers = leftovers,

                    path = sj.toString()
            )
        }
    }

    fun asText(): String = path
    fun asTSV(): String = "$path \t $type \t ${if (isRequired == true || inRequired == true) MANDATORY else OPTIONAL} \t ${authorizedValues ?: ""}"
}

private const val MANDATORY = "Mandatory"
private const val OPTIONAL = "Optional"
