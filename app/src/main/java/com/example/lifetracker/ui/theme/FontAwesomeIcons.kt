package com.example.lifetracker.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material.LocalContentColor
import com.guru.fontawesomecomposelib.FaIcon
import com.guru.fontawesomecomposelib.FaIcons
import com.guru.fontawesomecomposelib.FaIconType

object FontAwesomeIcons {
    val Home = FaIcons.Home
    val List = FaIcons.ListAlt
    val Dumbbell = FaIcons.Dumbbell
    val ChartLine = FaIcons.ChartLine
    val User = FaIcons.User
    val Plus = FaIcons.Plus
    val Images = FaIcons.Images
}

@Composable
fun FontAwesomeIcon(
    icon: FaIconType.SolidIcon,
    tint: Color = Color.White,  // Changed default to white
    modifier: Modifier = Modifier
) {
    FaIcon(
        faIcon = icon,
        size = 20.dp,  // Changed from 24.dp to match navigation
        tint = tint,
        modifier = modifier
    )
}
