// vistaActual: alterna entre LISTA y MAPA
// reviewsMapItems: reviews de las últimas 24h con coordenadas válidas
// selectedMapItem: review seleccionado al tocar un marcador

package com.example.beattreat.ui.FeedSiguiendo

import com.example.beattreat.ui.Resena.ResenaDetalladaUI

/** Vista actualmente seleccionada en FeedSiguiendo. */
enum class FeedVista { LISTA, MAPA }


/**
 * Estado de la pantalla Feed "Siguiendo".
 *
 * Muestra reviews en tiempo real de los usuarios que sigo.
 * Sprint 4: agrega vista de mapa con marcadores de las últimas 24h.
 */
data class FeedSiguiendoUIState(
    // ── Vista lista (existente) ──────────────────────────────────────────────
    val reviews: List<ResenaDetalladaUI> = emptyList(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val likedReviewIds: Set<String> = emptySet(),
    val likesCounts: Map<String, Int> = emptyMap(),
    val sinSeguidos: Boolean = false,

    // ── Sprint 4: Vista mapa ─────────────────────────────────────────────────
    /** Vista actualmente visible: lista de reviews O mapa */
    val vistaActual: FeedVista = FeedVista.LISTA,

    /**
     * Reviews de las últimas 24 horas que tienen coordenadas (latitude/longitude).
     * Se derivan del mismo Flow que [reviews]; el ViewModel filtra los que
     * tienen ubicación y fueron creados en las últimas 24h.
     */
    val reviewsMapItems: List<ReviewMapItem> = emptyList(),

    /**
     * Review que el usuario acaba de tocar en el mapa.
     * null = ningún marcador seleccionado (InfoWindow cerrado).
     */
    val selectedMapItem: ReviewMapItem? = null,

    /** true mientras se están cargando los datos del mapa por primera vez */
    val isMapLoading: Boolean = false
)