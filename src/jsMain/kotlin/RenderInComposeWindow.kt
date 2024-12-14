import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.window.CanvasBasedWindow
import androidx.compose.ui.window.ComposeViewport
import kotlinx.browser.document
import kotlinx.browser.window
import org.w3c.dom.HashChangeEvent

/**
 * Renders Compose content in the browser's canvas element #[CANVAS_ELEMENT_ID], auto-sizing the element.
 *
 * @param backgroundColor Background color of the body element.
 */
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
