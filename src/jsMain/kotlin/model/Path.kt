package model

/**
 * A Path, a completed path is represented as a [Screen]
 */
interface Path<T : Screen> {
    val pathPattern: String
    fun complete(path: String): T
}
