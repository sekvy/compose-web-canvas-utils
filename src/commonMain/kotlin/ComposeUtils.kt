import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import model.UiDirection
import org.jetbrains.skia.Image
import org.jetbrains.skiko.ClipboardManager
import org.jetbrains.skiko.URIManager
import org.jetbrains.skiko.loadBytesFromPath

/**
 * Loads the image of the give path
 */
@Composable
fun OnLoadedImage(
    modifier: Modifier = Modifier,
    path: String
) {
    var bitmap: ImageBitmap? by remember {
        mutableStateOf(null)
    }
    Box(
        modifier = modifier
    ) {
        bitmap?.let {
            Image(
                contentScale = ContentScale.Crop,
                bitmap = it,
                contentDescription = ""
            )
        }
    }
    LaunchedEffect(path) {
        withContext(Dispatchers.Default) {
            bitmap = loadBitmap(path)
        }
    }
}

suspend fun loadBitmap(path: String) =
    Image.makeFromEncoded(
        loadBytesFromPath(path)
    ).toComposeImageBitmap()

@Composable
fun Dp.toPx(): Float = LocalDensity.current.density * value

@Composable
fun Float.pxToDp(): Dp = (this / LocalDensity.current.density).dp

// Debug option
const val showBorders: Boolean = false
fun Modifier.showBorder(color: Color = Color.Red): Modifier {
    return if (showBorders) border(2.dp, color) else this
}

fun State<IntOffset>.toUIDirection() = if (value.x > value.y) {
    UiDirection.Horizontal
} else {
    UiDirection.Vertical
}

fun BoxWithConstraintsScope.toUIDirection() = if (this.constraints.maxWidth > this.constraints.maxHeight) {
    UiDirection.Horizontal
} else {
    UiDirection.Vertical
}

fun setClipboard(content: String) {
    ClipboardManager().setText(content)
}
fun openWebpage(url: String) {
    URIManager().openUri(url)
}
