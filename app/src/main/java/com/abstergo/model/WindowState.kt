package com.abstergo.model

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import java.util.UUID

data class WindowState(
    val id: String = UUID.randomUUID().toString(),
    val appSource: AppSource,
    val title: String = appSource.displayName,
    var offset: Offset = Offset(40f, 80f),
    var size: DpSize = DpSize(340.dp, 480.dp),
    var zIndex: Float = 1f,
    var isMinimized: Boolean = false
)
