package com.example.beattreat

/**
 * ID del usuario actualmente autenticado en el backend REST.
 * Se quema como "1" porque los usuarios de Firebase Auth no están
 * sincronizados con los del backend SQL/Postgres.
 * Úsalo en cualquier ViewModel que necesite el ID del usuario actual.
 */
const val CURRENT_USER_ID = 1
