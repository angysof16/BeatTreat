package com.example.login.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.login.R
import com.example.login.model.AlbumPerfilUI
import com.example.login.model.PerfilData
import com.example.login.model.PerfilUI
import com.example.login.model.ResenaUI
import com.example.login.ui.theme.BeatTreatColors
import com.example.login.ui.theme.BeatTreatTheme

private val JaroFont = FontFamily(Font(R.font.jaro_regular, FontWeight.Normal))

data class ProfileState(
    val perfil: PerfilUI = PerfilData.perfilActual,
    val albumesFavoritos: List<AlbumPerfilUI> = PerfilData.albumesFavoritos,
    val resenas: List<ResenaUI> = PerfilData.resenasRecientes
)

@Composable
fun ProfileScreen(
    onSearchClick: () -> Unit = {},
    onEditProfileClick: () -> Unit = {},
    onSiguiendoClick: () -> Unit = {},
    onMessageClick: () -> Unit = {},
    onAlbumClick: (Int) -> Unit = {},
    onVerTodasResenasClick: () -> Unit = {},
    onResenaClick: (ResenaUI) -> Unit = {},        // ✅ NUEVO
    modifier: Modifier = Modifier
) {
    val state = remember { ProfileState() }
    ProfileScreenContent(
        state = state,
        onSearchClick = onSearchClick,
        onEditProfileClick = onEditProfileClick,
        onSiguiendoClick = onSiguiendoClick,
        onMessageClick = onMessageClick,
        onAlbumClick = onAlbumClick,
        onVerTodasResenasClick = onVerTodasResenasClick,
        onResenaClick = onResenaClick,
        modifier = modifier
    )
}

@Composable
fun ProfileScreenContent(
    state: ProfileState,
    onSearchClick: () -> Unit,
    onEditProfileClick: () -> Unit,
    onSiguiendoClick: () -> Unit,
    onMessageClick: () -> Unit,
    onAlbumClick: (Int) -> Unit,
    onVerTodasResenasClick: () -> Unit,
    onResenaClick: (ResenaUI) -> Unit,             // ✅ NUEVO
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        TopBarProfile(onSearchClick = onSearchClick)
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            item {
                ProfileHeader(
                    perfil = state.perfil,
                    onEditProfileClick = onEditProfileClick,
                    onSiguiendoClick = onSiguiendoClick,
                    onMessageClick = onMessageClick
                )
            }
            item {
                AlbumSection(albumes = state.albumesFavoritos, onAlbumClick = onAlbumClick)
                Spacer(modifier = Modifier.height(22.dp))
            }
            item {
                ReviewsSection(
                    resenas = state.resenas,
                    onVerTodasClick = onVerTodasResenasClick,
                    onResenaClick = onResenaClick   // ✅ NUEVO
                )
            }
            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }
}

@Composable
fun TopBarProfile(onSearchClick: () -> Unit, modifier: Modifier = Modifier) {
    Row(modifier = modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier.size(80.dp).clip(RoundedCornerShape(bottomEnd = 12.dp)).background(Color(0xFF1A1A1A)),
            contentAlignment = Alignment.Center
        ) {
            Image(painter = painterResource(id = R.drawable.logo_beattreat), contentDescription = "Logo", modifier = Modifier.size(56.dp).clip(RoundedCornerShape(12.dp)), contentScale = ContentScale.Fit)
        }
        Row(
            modifier = Modifier.weight(1f).background(MaterialTheme.colorScheme.primary, RoundedCornerShape(bottomStart = 12.dp)).padding(horizontal = 20.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("BeatTreat", color = Color.White, fontSize = 28.sp, fontFamily = JaroFont, modifier = Modifier.weight(1f))
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                IconButton(onClick = onSearchClick) { Icon(Icons.Filled.Search, contentDescription = "Buscar", tint = Color.White, modifier = Modifier.size(28.dp)) }
                IconButton(onClick = {}) { Icon(Icons.Filled.AccountCircle, contentDescription = "Perfil", tint = Color.White, modifier = Modifier.size(32.dp)) }
            }
        }
    }
}

@Composable
fun ProfileHeader(perfil: PerfilUI, onEditProfileClick: () -> Unit, onSiguiendoClick: () -> Unit, onMessageClick: () -> Unit, modifier: Modifier = Modifier) {
    Card(modifier = modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp), border = BorderStroke(2.dp, Color.Gray), colors = CardDefaults.cardColors(containerColor = Color.Transparent)) {
        Column(modifier = Modifier.fillMaxWidth().background(Color(0xFF1A1230))) {
            PerfilBanner(fotoBannerRes = perfil.fotoBannerRes)
            Row(modifier = Modifier.fillMaxWidth()) {
                PerfilAvatar(perfil = perfil)
                Spacer(modifier = Modifier.weight(1f))
                Column(modifier = Modifier.padding(top = 18.dp, end = 12.dp)) {
                    Row(modifier = Modifier.height(30.dp)) {
                        ButtonSmall(text = "Siguiendo", onClick = onSiguiendoClick)
                        Spacer(Modifier.width(6.dp))
                        ButtonSmall(text = "Message", onClick = onMessageClick)
                    }
                }
            }
            PerfilInfo(perfil = perfil)
        }
    }
}

@Composable
fun PerfilBanner(fotoBannerRes: Int, modifier: Modifier = Modifier) {
    if (fotoBannerRes != 0) {
        Image(painter = painterResource(id = fotoBannerRes), contentDescription = "Banner", modifier = modifier.fillMaxWidth().height(130.dp), contentScale = ContentScale.Crop)
    } else {
        Box(modifier = modifier.fillMaxWidth().height(130.dp).background(BeatTreatColors.SurfaceVariant))
    }
}

@Composable
fun PerfilAvatar(perfil: PerfilUI, modifier: Modifier = Modifier) {
    Box(modifier = modifier.padding(start = 16.dp)) {
        if (perfil.fotoPerfilRes != 0) {
            Image(painter = painterResource(id = perfil.fotoPerfilRes), contentDescription = perfil.nombre, modifier = Modifier.size(80.dp).clip(CircleShape), contentScale = ContentScale.Crop)
        } else {
            Icon(Icons.Filled.AccountCircle, contentDescription = perfil.nombre, tint = Color.White, modifier = Modifier.size(80.dp).clip(CircleShape))
        }
        Box(modifier = Modifier.size(22.dp).align(Alignment.BottomEnd).background(Color.Black, CircleShape), contentAlignment = Alignment.Center) {
            Icon(Icons.Filled.Edit, contentDescription = "Editar", tint = Color.White, modifier = Modifier.size(13.dp))
        }
    }
}

@Composable
fun PerfilInfo(perfil: PerfilUI, modifier: Modifier = Modifier) {
    Column(modifier = modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
        Text(perfil.nombre, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Text(perfil.usuario, color = Color.LightGray, fontSize = 14.sp)
        Spacer(modifier = Modifier.height(12.dp))
        Row {
            Text("${perfil.siguiendo} Siguiendo", color = Color.White, fontSize = 14.sp)
            Spacer(modifier = Modifier.width(16.dp))
            Text("${perfil.seguidores} Seguidores", color = Color.White, fontSize = 14.sp)
        }
    }
}

@Composable
fun ButtonSmall(text: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Button(onClick = onClick, modifier = modifier, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5B1FA6))) {
        Text(text = text, color = Color.White, fontSize = 12.sp)
    }
}

@Composable
fun AlbumSection(albumes: List<AlbumPerfilUI>, onAlbumClick: (Int) -> Unit, modifier: Modifier = Modifier) {
    Column(modifier = modifier.padding(horizontal = 16.dp)) {
        Text("Álbumes Favoritos", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(12.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            items(albumes) { album -> AlbumPerfilItem(album = album, onClick = { onAlbumClick(album.id) }) }
        }
    }
}

@Composable
fun AlbumPerfilItem(album: AlbumPerfilUI, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Column(modifier = modifier.clickable { onClick() }) {
        if (album.imagenRes != 0) {
            Image(painter = painterResource(id = album.imagenRes), contentDescription = album.nombre, modifier = Modifier.size(105.dp).clip(RoundedCornerShape(8.dp)), contentScale = ContentScale.Crop)
        } else {
            Box(modifier = Modifier.size(105.dp).clip(RoundedCornerShape(8.dp)).background(BeatTreatColors.SurfaceVariant))
        }
        Text(album.nombre, color = Color.White, fontSize = 11.sp, modifier = Modifier.width(105.dp).background(Color(0xFF24124A)).padding(vertical = 5.dp, horizontal = 6.dp))
    }
}

@Composable
fun ReviewsSection(
    resenas: List<ResenaUI>,
    onVerTodasClick: () -> Unit,
    onResenaClick: (ResenaUI) -> Unit = {},        // ✅ NUEVO
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.padding(horizontal = 16.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Reseñas recientes", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable { onVerTodasClick() }) {
                Text("Ver todas", color = Color.White, fontSize = 12.sp)
                Spacer(modifier = Modifier.width(4.dp))
                Icon(Icons.Filled.ArrowForward, contentDescription = "Ver todas", tint = Color.White, modifier = Modifier.size(14.dp))
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        resenas.forEach { resena ->
            ReviewCard(resena = resena, onClick = { onResenaClick(resena) })
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun ReviewCard(resena: ResenaUI, onClick: () -> Unit = {}, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth().clickable { onClick() }, // ✅ clickeable
        border = BorderStroke(1.dp, Color.Gray),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF2A0A57))
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(12.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (resena.autorFotoRes != 0) {
                        Image(painter = painterResource(id = resena.autorFotoRes), contentDescription = resena.autorNombre, modifier = Modifier.size(42.dp).clip(CircleShape), contentScale = ContentScale.Crop)
                    } else {
                        Icon(Icons.Filled.AccountCircle, contentDescription = resena.autorNombre, tint = Color.White, modifier = Modifier.size(42.dp))
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(resena.autorNombre, color = Color.White, fontSize = 14.sp)
                        Text(resena.autorUsuario, color = Color.LightGray, fontSize = 11.sp)
                    }
                }
                ReviewStats(comentarios = resena.comentarios, likes = resena.likes)
                IconButton(onClick = {}) { Icon(Icons.Filled.MoreVert, contentDescription = "Opciones", tint = Color.White, modifier = Modifier.size(18.dp)) }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(resena.texto, color = Color.White, fontSize = 12.sp)
        }
    }
}

@Composable
fun ReviewStats(comentarios: Int, likes: Int, modifier: Modifier = Modifier) {
    Row(modifier = modifier.background(Color.Black, CircleShape).padding(horizontal = 10.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(" $comentarios", color = Color.White, fontSize = 11.sp)
        Spacer(modifier = Modifier.width(8.dp))
        Text(" $likes", color = Color.White, fontSize = 11.sp)
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    BeatTreatTheme { ProfileScreen() }
}