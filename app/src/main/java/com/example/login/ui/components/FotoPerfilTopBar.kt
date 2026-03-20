package com.example.login.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import com.example.login.ui.theme.BeatTreatColors

@Composable
fun FotoPerfilTopBar(
    fotoPerfilUrl: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (fotoPerfilUrl.isBlank()) {
        IconButton(onClick = onClick, modifier = modifier) {
            Icon(
                imageVector        = Icons.Filled.AccountCircle,
                contentDescription = "Perfil",
                tint               = Color.White,
                modifier           = Modifier.size(32.dp)
            )
        }
    } else {
        Box(
            modifier = modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(BeatTreatColors.SurfaceVariant)
                .clickable { onClick() }
        ) {
            SubcomposeAsyncImage(
                model              = fotoPerfilUrl,
                contentDescription = "Mi perfil",
                modifier           = Modifier.fillMaxSize(),
                contentScale       = ContentScale.Crop
            ) {
                when (painter.state) {
                    is AsyncImagePainter.State.Loading -> {
                        Box(
                            modifier         = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                color       = BeatTreatColors.Purple60,
                                modifier    = Modifier.size(20.dp),
                                strokeWidth = 2.dp
                            )
                        }
                    }
                    is AsyncImagePainter.State.Error -> {
                        Box(
                            modifier         = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector        = Icons.Filled.Person,
                                contentDescription = null,
                                tint               = Color.White.copy(alpha = 0.6f),
                                modifier           = Modifier.size(24.dp)
                            )
                        }
                    }
                    else -> SubcomposeAsyncImageContent()
                }
            }
        }
    }
}