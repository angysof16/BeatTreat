package com.example.login.util

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.tasks.await

/**
 * Obtiene el FCM token del dispositivo y lo guarda en Firestore.
 *
 * El profesor dijo: "lo que yo quiero es que si el usuario se registra
 * en nuestra aplicación o haga login en nuestra aplicación, ahí son las
 * veces que nosotros tenemos que guardar este FSM token en la base de datos."
 *
 * También mencionó el uso de await() para no dejar el token en nulo:
 * "si usted ponen el await, el sigue derecho y esto sigue teniendo un valor de nulo"
 *
 * Llama esta función desde:
 *   - RegistroViewModel.registrar() → después de crear la cuenta
 *   - LoginViewModel.login()        → después de hacer sign in
 */
object FcmTokenHelper {

    private const val TAG = "FcmTokenHelper"

    /**
     * Registra el FCM token del dispositivo actual en el documento del usuario en Firestore.
     * Si falla, lo logea pero NO lanza excepción (no es crítico).
     */
    suspend fun registrarToken() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: run {
            Log.w(TAG, "No hay usuario autenticado para registrar FCM token.")
            return
        }

        try {
            // await() → espera a que se complete de forma asíncrona
            val token = FirebaseMessaging.getInstance().token.await()

            FirebaseFirestore.getInstance()
                .collection("users")
                .document(userId)
                .update("fcmToken", token)
                .await()

            Log.d(TAG, "✅ FCM token registrado para usuario $userId")

        } catch (e: Exception) {
            // Si el documento no existe todavía (ej: registro nuevo),
            // intentamos con set merge para no pisar otros campos.
            try {
                val token = FirebaseMessaging.getInstance().token.await()
                FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(userId)
                    .set(mapOf("fcmToken" to token), com.google.firebase.firestore.SetOptions.merge())
                    .await()
                Log.d(TAG, "✅ FCM token guardado con merge para usuario $userId")
            } catch (e2: Exception) {
                Log.e(TAG, "❌ Error al guardar FCM token: ${e2.message}")
            }
        }
    }

    /**
     * Obtiene el token actual sin guardarlo.
     * Útil para debug o para mostrarlo en pantalla.
     */
    suspend fun getToken(): String? {
        return try {
            FirebaseMessaging.getInstance().token.await()
        } catch (e: Exception) {
            Log.e(TAG, "Error al obtener FCM token: ${e.message}")
            null
        }
    }
}
