// Utilidad que obtiene las coordenadas GPS actuales del dispositivo
// Se usa al publicar un review para guardar la ubicación en Firestore

// Requiere permiso de ubicacion que se pide en EscribirResenaScreen antes de publicar
// Si el usuario lo deniega, latitude/longitude quedan en null y el review se publica igualmente, pero sin aparecer en el mapa
// ──────────────────────────────────────────────────────────────────────────────
package com.example.beattreat.util

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

object LocationHelper {

    /**
     * Obtiene la ubicación actual del dispositivo de forma suspendida
     *
     * Usa getCurrentLocation con prioridad PRIORITY_BALANCED_POWER_ACCURACY para no consumir demasiada batería
     *
     * @return [Location] con lat/lng si el GPS está activo y el permiso está concedido,
     * null en cualquier otro caso (sin lanzar excepción).
     */
    @SuppressLint("MissingPermission")
    suspend fun getCurrentLocation(context: Context): Location? {
        return suspendCancellableCoroutine { continuation ->
            val fusedClient = LocationServices.getFusedLocationProviderClient(context)

            val task = fusedClient.getCurrentLocation(
                Priority.PRIORITY_BALANCED_POWER_ACCURACY,
                // CancellationToken: null - usa el default
                null
            )

            task.addOnSuccessListener { location ->
                // puede ser null si GPS apagado
                continuation.resume(location)
            }

            task.addOnFailureListener {
                // error - no bloquea la publicación
                continuation.resume(null)
            }

            continuation.invokeOnCancellation {
                task.addOnCanceledListener { continuation.resume(null) }
            }
        }
    }
}