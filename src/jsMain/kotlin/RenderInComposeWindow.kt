@file:Suppress(
    "INVISIBLE_MEMBER",
    "INVISIBLE_REFERENCE",
    "EXPOSED_PARAMETER_TYPE"
) // WORKAROUND: ComposeWindow and ComposeLayer are internal
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.window.ComposeWindow
import kotlinx.browser.window
import org.jetbrains.skiko.SkikoTouchEvent
import org.jetbrains.skiko.SkikoTouchEventKind
import org.jetbrains.skiko.wasm.onWasmReady
import org.w3c.dom.HashChangeEvent
import org.w3c.dom.Touch
import org.w3c.dom.TouchEvent
import org.w3c.dom.events.WheelEvent

// Resizes the canvas to the full size of the browser.
// Modified version of the same class found in https://github.com/OliverO2/compose-counting-grid
private class ComposeManagedBrowserCanvas(
    private val composeWindow: ComposeWindow,
    private val onNewWindowSize: (width: Int, height: Int) -> Unit,
    private val onScroll: (deltaY: Double) -> Unit,
    private val onHashChange: (hash: String) -> Unit
) {
    private var width by composeWindow.canvas::width
    private var height by composeWindow.canvas::height
    private val innerWidth by window::innerWidth
    private val innerHeight by window::innerHeight

    init {
        resize(innerWidth, innerHeight)
        onNewWindowSize(innerWidth, innerHeight)
        window.addEventListener("resize", {
            resize(innerWidth, innerHeight)
        })
        window.addEventListener("wheel", { event ->
            if (event is WheelEvent) {
                event.stopPropagation()
                onScroll(event.deltaY)
            }
        })
        window.addEventListener("touchstart", { event ->
            onTouchEvent(event as TouchEvent, SkikoTouchEventKind.STARTED)
        })
        window.addEventListener("touchend", { event ->
            onTouchEvent(event as TouchEvent, SkikoTouchEventKind.ENDED)
        })
        window.addEventListener("touchcancel", { event ->
            onTouchEvent(event as TouchEvent, SkikoTouchEventKind.CANCELLED)
        })
        window.addEventListener("touchmove", { event ->
            onTouchEvent(event as TouchEvent, SkikoTouchEventKind.MOVED)
        })

        onHashChange(hashValue)
        window.addEventListener("hashchange", { event ->
            if (event is HashChangeEvent) {
                onHashChange(hashValue)
            }
        })
    }

    private fun onTouchEvent(event: TouchEvent, kind: SkikoTouchEventKind) {
        /*
          * Using event.changedTouches.asList() crashes with
          * Could not load content for webpack://page/commonMainSources/libraries/stdlib/common/src/generated/_Arrays.kt?e98b
          * (Fetch through target failed: Unsupported URL scheme; Fallback: HTTP error: status code 404, net::ERR_UNKNOWN_URL_SCHEME)
         */
        val touches: MutableList<Touch> = mutableListOf()
        for (x in 0..event.changedTouches.length) {
            val touch = event.changedTouches.item(x)
            if (touch != null) {
                touches.add(touch)
            }
        }
        composeWindow.layer.layer.skikoView?.onTouchEvent(
            touches.map {
                it.toSkikoTouchEvent(
                    kind = kind,
                    timestamp = event.timeStamp.toLong()
                )
            }.toTypedArray()
        )
    }

    private fun Touch.toSkikoTouchEvent(kind: SkikoTouchEventKind, timestamp: Long) =
        SkikoTouchEvent(
            x = this.clientX.toDouble(),
            y = this.clientY.toDouble(),
            kind = kind,
            timestamp = timestamp
        )

    private fun resize(newWidth: Int, newHeight: Int) {
        if (newWidth == width && newHeight == height) return
        onNewWindowSize(newWidth, newHeight)

        console.info(
            "ComposeManagedBrowserCanvas: resizing canvas from ($width, $height) to ($newWidth, $newHeight), scale=${composeWindow.layer.layer.contentScale}"
        )

        width = newWidth
        height = newHeight

        with(composeWindow.layer) {
            // The only way to update the underlying SkiaLayer's size seems to be via `attachTo`.
            // However, `detach` is not implemented, so this may be unstable.
            layer.attachTo(composeWindow.canvas, autoDetach = true)
            layer.needRedraw()
            setSize(width, height)
        }
    }
}

/**
 * Renders Compose content in the browser's canvas element #[CANVAS_ELEMENT_ID], auto-sizing the element.
 *
 * @param backgroundColor Background color of the body element.
 */
fun renderInComposeWindow(
    backgroundColor: Color? = null,
    onNewWindowSize: (width: Int, height: Int) -> Unit,
    onScroll: (deltaY: Double) -> Unit,
    onHashChange: (hash: String) -> Unit,
    content: @Composable () -> Unit
) {
    setBackground(backgroundColor)
    onWasmReady {
        ComposeWindow().apply {
            // Attach auto-resizer
            ComposeManagedBrowserCanvas(
                this,
                onNewWindowSize = onNewWindowSize,
                onScroll = onScroll,
                onHashChange = onHashChange
            )
            setContent {
                content()
            }
        }
    }
}
