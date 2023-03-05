package model

/**
 * A screen represented by a path
 */
interface Screen {
    val path: String
}

/**
 * Find a Screen using a path and a list of supported paths.
 *
 * i.e.
 * paths = listOf(ArticlePath(pathPattern = "articles/$"))
 * path = "articles/my_article_123"
 *
 * Finds ArticlePath, which is completed to ArticleScreen()
 */
fun fromPath(paths: List<Path<out Screen>>, path: String): Screen? {
    // Associate paths to path segments
    val associatedPaths = paths.associateWith { it }.mapKeys {
        it.key.pathPattern.split("/").filter { it.isNotEmpty() }
    }

    // Split the given string path into segments
    val segments = path.split("/").filter { it.isNotEmpty() }
    // Search for matching paths classes
    var foundPaths = mapOf<List<String>, Path<*>>()
    segments.forEachIndexed { i, s ->
        foundPaths = associatedPaths.filter {
            val segment = it.key.getOrNull(i)
            segment == "$" || segment == s
        }
    }

    // Complete the found path class if any
    val found = foundPaths.values.firstOrNull()
    return if (found != null) {
        val completedPath = found.pathPattern.split("/").filter { it.isNotEmpty() }
            .mapIndexed { index: Int, s: String ->
                if (s == "$") {
                    segments[index]
                } else {
                    s
                }
            }
        // The completed path will have its "$" segments filled in and be converted to a Screen.
        found.complete(completedPath.joinToString("/"))
    } else null
}
