package com.example.beattreat.ui.FeedSiguiendo

import com.example.beattreat.ui.Resena.ResenaDetalladaUI

/**
 * Estado de la pantalla Feed "Siguiendo".
 *
 * Muestra reviews en tiempo real de los usuarios que sigo.
 * Los datos se actualizan automáticamente sin recargar la pantalla.
 */
data class FeedSiguiendoUIState(
    val reviews: List<ResenaDetalladaUI> = emptyList(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    // IDs de reviews que el usuario actual ya dio like
    val likedReviewIds: Set<String> = emptySet(),
    // Contadores de likes por reviewId (firestoreDocId)
    val likesCounts: Map<String, Int> = emptyMap(),
    // true si el usuario no sigue a nadie
    val sinSeguidos: Boolean = false
)
