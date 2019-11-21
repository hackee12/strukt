package hackee12.strukt.core.exception

import hackee12.strukt.core.SO

class StruktException(message: String) : RuntimeException(message)

fun fail(message: String): Nothing {
    throw StruktException(message)
}
