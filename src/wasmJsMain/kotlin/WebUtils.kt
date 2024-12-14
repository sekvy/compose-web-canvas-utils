import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import kotlinx.browser.document
import kotlinx.browser.window
import org.jetbrains.skiko.ClipboardManager
import org.jetbrains.skiko.URIManager
import org.w3c.dom.HTMLStyleElement
import org.w3c.dom.HashChangeEvent
import androidx.compose.ui.window.CanvasBasedWindow
import androidx.compose.ui.window.ComposeViewport

/**
 * Converts the color to a hex color string
 */
fun Color.toHexCode(removeAlpha: Boolean = true) =
    toArgb().toUInt().toString(16).drop(if (removeAlpha) 2 else 0)

/**
 * Creates an output like rgba(255, 255, 255, 0.5)
 */
fun Color.htmlRgba(): String {
    val r = (red * 255.0f + 0.5f).toInt()
    val g = (green * 255.0f + 0.5f).toInt()
    val b = (blue * 255.0f + 0.5f).toInt()
    val a = alpha
    return "rgba($r, $g, $b, $a)"
}

fun setClipboard(content: String) {
    ClipboardManager().setText(content)
}

fun openWebpage(url: String) {
    URIManager().openUri(url)
}

fun setBackground(backgroundColor: Color? = null) {
    requireNotNull(document.head).appendChild(
        (document.createElement("style") as HTMLStyleElement).apply {
            type = "text/css"
            backgroundColor?.let {
                appendChild(document.createTextNode("body { background-color: ${backgroundColor.htmlRgba()}; }"))
            }
        }
    )
}

val hashValue
    get() = window.location.hash.trim().replace("#", "").lowercase()

const val CANVAS_ELEMENT_ID = "ComposeTarget" // Hardwired into ComposeWindow

@OptIn(ExperimentalComposeUiApi::class)
fun renderInComposeWindow(
    canvasElementId: String? = null,
    backgroundColor: Color? = null,
    onHashChange: (hash: String) -> Unit,
    content: @Composable () -> Unit
) {
    setBackground(backgroundColor)
    onHashChange(hashValue)
    window.addEventListener("hashchange", { event ->
        if (event is HashChangeEvent) {
            onHashChange(hashValue)
        }
    })
    if (canvasElementId != null) {
        CanvasBasedWindow(
            canvasElementId = canvasElementId
        ) {
            content()
        }
    } else {
        ComposeViewport(document.body!!) {
            content()
        }
    }
}
