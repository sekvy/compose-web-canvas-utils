package ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import model.UiDirection
import showBorder
import toUIDirection
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class CustomScaffoldState {
    var scaffoldData by mutableStateOf(CustomScaffoldData())
    val uiDirection
        get() = scaffoldData.uiDirection
}

data class CustomScaffoldData(
    val uiDirection: UiDirection = UiDirection.Vertical,
)

@Composable
fun rememberScaffoldState(): CustomScaffoldState = remember {
    CustomScaffoldState()
}

@Composable
fun CustomScaffold(
    startContent: @Composable BoxScope.() -> Unit = {},
    centerContent: @Composable BoxScope.() -> Unit = {},
    endContent: @Composable BoxScope.() -> Unit = {},
    overlay: @Composable BoxWithConstraintsScope.() -> Unit = {},
    customScaffoldState: CustomScaffoldState = rememberScaffoldState()
) {
    Surface(
        modifier = Modifier.showBorder().fillMaxSize(),
        color = MaterialTheme.colors.background,
        elevation = 0.dp
    ) {
        BoxWithConstraints(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.TopStart
        ) {
            val uuDirection = toUIDirection()
            customScaffoldState.scaffoldData = CustomScaffoldData(uiDirection = uuDirection)
            if (uuDirection == UiDirection.Horizontal) {
                Row(
                    modifier = Modifier.fillMaxSize()
                ) {
                    // Top content
                    Box(modifier = Modifier.fillMaxHeight().wrapContentWidth().showBorder(Color.Blue), content = startContent)

                    // Center, same top/bottom bounds as the ui.CircleAnimation
                    Box(
                        modifier = Modifier.fillMaxHeight().fillMaxWidth().showBorder(Color.Blue).weight(1f),
                        content = centerContent
                    )

                    // Bottom content
                    Box(modifier = Modifier.fillMaxHeight().wrapContentWidth().showBorder(Color.Blue), content = endContent)
                }
            } else {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    // Top content
                    Box(modifier = Modifier.fillMaxWidth().wrapContentHeight().showBorder(), content = startContent)

                    // Center, same top/bottom bounds as the ui.CircleAnimation
                    Box(modifier = Modifier.fillMaxWidth().fillMaxHeight().weight(1f).showBorder(), content = centerContent)

                    // Bottom content
                    Box(modifier = Modifier.fillMaxWidth().wrapContentHeight().showBorder(), content = endContent)
                }
            }

            overlay()
        }
    }
}
