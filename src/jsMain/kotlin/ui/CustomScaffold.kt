package ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import model.UiDirection
import showBorder

@Composable
fun CustomScaffold(
    uiDirection: UiDirection = UiDirection.Vertical,
    startContent: @Composable BoxScope.() -> Unit = {},
    centerContent: @Composable BoxScope.() -> Unit = {},
    endContent: @Composable BoxScope.() -> Unit = {},
    overlay: @Composable (BoxWithConstraintsScope.() -> Unit)
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
            if (uiDirection == UiDirection.Horizontal) {
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
