// Solo se muestra si el review tiene coordenadas válidas y fue creado en las últimas 24 horas.
package com.example.beattreat.ui.FeedSiguiendo

 //Se construye desde ResenaDetalladaUI
 // createdAt > now - 24h
 // latitude != null && longitude != null

data class ReviewMapItem(
    val firestoreDocId: String,
    val latitude: Double,
    val longitude: Double,
    // Info del review (mostrada en el InfoWindow al tocar el marcador)
    val autorNombre: String,
    val autorUsuario: String,
    val autorFotoUrl: String,
    val albumNombre: String,
    val albumArtista: String,
    val albumImagenUrl: String,
    val calificacion: Float,
    val textoResumen: String,
    val fecha: String,
    val likes: Int
)