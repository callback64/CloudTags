package com.callback.tagcloud

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import java.time.format.TextStyle
import kotlin.math.*

/*import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.text.BasicText

import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.acos
import kotlin.math.cos
import kotlin.math.sin*/

/*@Composable
fun TagCloud(
    tags: List<String>,
    modifier: Modifier = Modifier,
    onChoose: ((String)->Unit)? = null
) {

    //val some by remember { mutableStateOf(0) }
    //var rotationX by remember { mutableStateOf(0.0f) }
    //var rotationY by remember { mutableStateOf(0.0f) }

}*/

@Composable
fun TagCloud(
    tags: List<String>,
    modifier: Modifier = Modifier,
    onChoose: ((String)->Unit)? = null
) {
    // Углы вращения по двум осям
    var rotationX by remember { mutableStateOf(0f) }
    var rotationY by remember { mutableStateOf(0f) }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp)
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume() // Отмечаем событие как обработанное
                    rotationX += dragAmount.y * 0.5f // Изменяем угол по оси X
                    rotationY -= dragAmount.x * 0.5f // Изменяем угол по оси Y
                }
            },
        contentAlignment = Alignment.Center
    ) {
        val radius = 400f // Радиус сферы
        val centerX = 0f
        val centerY = 0f

        tags.forEachIndexed { index, tag ->
            // Распределение на сфере
            val phi = acos(1 - 2f * (index + 0.5f) / tags.size) // Угол по вертикали
            val theta = (Math.PI * (1 + Math.sqrt(5.0)) * index).toFloat() // Угол по горизонтали

            // Вычисляем координаты на сфере
            val x3D = radius * sin(phi) * cos(theta)
            val y3D = radius * sin(phi) * sin(theta)
            val z3D = radius * cos(phi)

            // Применяем вращение
            val cosY = cos(Math.toRadians(rotationY.toDouble())).toFloat()
            val sinY = sin(Math.toRadians(rotationY.toDouble())).toFloat()
            val cosX = cos(Math.toRadians(rotationX.toDouble())).toFloat()
            val sinX = sin(Math.toRadians(rotationX.toDouble())).toFloat()

            val rotatedX = x3D * cosY - z3D * sinY
            val rotatedZ = x3D * sinY + z3D * cosY
            val rotatedY = y3D * cosX - rotatedZ * sinX
            val finalZ = y3D * sinX + rotatedZ * cosX

            // Перспективное преобразование
            val scale = (finalZ + radius) / (2 * radius)
            val x2D = rotatedX * scale
            val y2D = rotatedY * scale

            // Рисуем тег
            BasicText(
                text = tag,
                //style = TextStyle(fontSize = (16 * scale).sp),
                modifier = Modifier
                    .graphicsLayer(
                        translationX = x2D + centerX,
                        translationY = y2D + centerY,
                        scaleX = scale,
                        scaleY = scale,
                        alpha = scale
                    )
                    .clickable { onChoose?.invoke(tag) }
            )
        }
    }
}