package com.example.beattreat.data.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.beattreat.MainActivity
import com.example.beattreat.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Servicio que maneja las notificaciones push de Firebase Cloud Messaging.
 *
 * El profesor explicó:
 *  "Esta clase la cual va a estar encargada de saber recibir todas las
 *   notificaciones push que vienen de nuestro back."
 *
 * Extiende FirebaseMessagingService (obligatorio).
 *
 * Se declara en AndroidManifest.xml con:
 *   <service
 *     android:name=".data.services.BeatTreatMessagingService"
 *     android:exported="false">
 *     <intent-filter>
 *       <action android:name="com.google.firebase.MESSAGING_EVENT"/>
 *     </intent-filter>
 *   </service>
 *
 * IMPORTANTE: el permiso POST_NOTIFICATIONS se pide en la UI con
 *   NotificationPermissionHandler (ver MainScreen/ProfileScreen).
 */
class BeatTreatMessagingService : FirebaseMessagingService() {

    companion object {
        private const val CHANNEL_ID   = "beattreat_notifications"
        private const val CHANNEL_NAME = "BeatTreat"
        private const val CHANNEL_DESC = "Notificaciones de likes y seguidores"
    }

    /**
     * Se llama cuando llega un mensaje push con la app en PRIMER PLANO o en background.
     * El profesor dijo: "para notificaciones que llegan cuando la app está abierta,
     * tienes que meterte más con esta clase."
     */
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        val title = remoteMessage.notification?.title ?: "BeatTreat"
        val body  = remoteMessage.notification?.body  ?: ""
        val data  = remoteMessage.data

        mostrarNotificacion(title, body, data)
    }

    /**
     * Se llama cuando el FCM token se renueva.
     * El profesor dijo: "este token es el identificador único que se le da a
     * su aplicación y a su celular cuando aceptan que esta tenga Firebase Cloud Messaging."
     *
     * Cuando se renueva, lo guardamos en Firestore para que las Cloud Functions
     * puedan seguir enviando notificaciones.
     */
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        guardarTokenEnFirestore(token)
    }

    /**
     * Muestra la notificación en el sistema.
     * Crea el canal de notificaciones (requerido en Android 8+).
     */
    private fun mostrarNotificacion(title: String, body: String, data: Map<String, String>) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Crear canal (Android 8+ / API 26+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply { description = CHANNEL_DESC }
            notificationManager.createNotificationChannel(channel)
        }

        // Intent para abrir la app al tocar la notificación
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            // Pasar datos de la notificación para navegar al lugar correcto
            data?.forEach { (key, value) -> putExtra(key, value) }
        }
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.logo_beattreat)
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }

    /**
     * Guarda el nuevo FCM token en Firestore para que las Cloud Functions
     * puedan seguir enviando notificaciones al dispositivo actual.
     */
    private fun guardarTokenEnFirestore(token: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        CoroutineScope(Dispatchers.IO).launch {
            try {
                FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(userId)
                    .update("fcmToken", token)
            } catch (e: Exception) {
                // Si el documento no existe todavía, ignoramos el error.
                // El token se guardará cuando el usuario complete el registro.
            }
        }
    }
}
