package com.example.login.ui.components

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat

/**
 * Composable que solicita el permiso de notificaciones al usuario.
 *
 * El profesor explicó exactamente este flujo:
 *  1. Verificar si la versión de Android >= 33 (Tiramisu)
 *     → en versiones anteriores no es obligatorio pedir el permiso
 *  2. Verificar si el permiso ya fue aceptado anteriormente
 *     → si ya fue aceptado, no mostrar el diálogo de nuevo
 *  3. Si no fue aceptado, lanzar el launcher para pedirlo
 *
 * El profesor dijo: "en mi caso pues no pasa nada. Digamos que si
 * el usuario pues no recibe las notificaciones, pues nada,
 * se pierde ese feature, pero del resto bien."
 *
 * Uso:
 *   NotificationPermissionHandler(
 *     onPermissionGranted = { /* registrar token, etc */ },
 *     onPermissionDenied  = { /* opcional: mostrar mensaje */ }
 *   )
 */
@Composable
fun NotificationPermissionHandler(
    onPermissionGranted: () -> Unit = {},
    onPermissionDenied: () -> Unit  = {}
) {
    val context = LocalContext.current

    // Launcher que muestra el diálogo del sistema para pedir el permiso
    // El profesor mostró exactamente este patrón con rememberLauncherForActivityResult
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            onPermissionGranted()
        } else {
            onPermissionDenied()
        }
    }

    LaunchedEffect(Unit) {
        // Solo aplicable en Android 13+ (API 33 / Tiramisu)
        // El profesor dijo: "si estoy en una versión posterior 33 o mayor,
        // sí le tengo que pedir permiso al usuario"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

            val permiso = Manifest.permission.POST_NOTIFICATIONS

            // Verificar si ya fue aceptado antes
            val yaAceptado = ContextCompat.checkSelfPermission(
                context, permiso
            ) == PackageManager.PERMISSION_GRANTED

            if (yaAceptado) {
                // Ya tenía permiso → ejecutar callback sin preguntar
                onPermissionGranted()
            } else {
                // No tiene permiso → lanzar el diálogo del sistema
                notificationPermissionLauncher.launch(permiso)
            }
        } else {
            // Android < 13 → notificaciones siempre permitidas
            onPermissionGranted()
        }
    }
}
