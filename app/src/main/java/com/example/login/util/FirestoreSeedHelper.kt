// ──────────────────────────────────────────────────────────────────────────────
// FILE: util/FirestoreSeedHelper.kt
// ──────────────────────────────────────────────────────────────────────────────
// Llama a FirestoreSeedHelper.seedIfEmpty(db) desde MainActivity o desde
// cualquier ViewModel la primera vez que la app inicie.
// Solo inserta datos si la colección "albums" está vacía.
// ──────────────────────────────────────────────────────────────────────────────
package com.example.login.util

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

object FirestoreSeedHelper {

    private val sampleAlbums = listOf(
        mapOf(
            "title"       to "Cum on Feel the Noize",
            "artist"      to "Quiet Riot",
            "genre"       to "Heavy Metal",
            "releaseYear" to 1983,
            "coverImage"  to "https://cdn.phototourl.com/free/2026-04-16-9c152d81-5c35-47ec-a295-aa26549c1c38.png",
            "description" to "Éxito definitivo de Quiet Riot que catapultó al grupo a la cima del rock duro de los 80."
        ),
        mapOf(
            "title"       to "A Night at the Opera",
            "artist"      to "Queen",
            "genre"       to "Rock Clásico",
            "releaseYear" to 1975,
            "coverImage"  to "https://cdn.phototourl.com/free/2026-04-16-7eb3fce1-c3ac-4657-b20b-56145f2845de.png",
            "description" to "Considerado uno de los mejores álbumes de la historia. Contiene Bohemian Rhapsody."
        ),
        mapOf(
            "title"       to "News of the World",
            "artist"      to "Queen",
            "genre"       to "Rock / Arena Rock",
            "releaseYear" to 1977,
            "coverImage"  to "https://cdn.phototourl.com/free/2026-04-16-7eb3fce1-c3ac-4657-b20b-56145f2845de.png",
            "description" to "Hogar de los himnos deportivos definitivos: We Will Rock You y We Are the Champions."
        ),
        mapOf(
            "title"       to "Un Verano Sin Ti",
            "artist"      to "Bad Bunny",
            "genre"       to "Reggaetón / Latin Pop",
            "releaseYear" to 2022,
            "coverImage"  to "https://cdn.phototourl.com/free/2026-04-16-f5b9a8aa-ad44-4c97-8521-3752902c1411.webp",
            "description" to "Obra maestra que celebra la cultura puertorriqueña. El álbum más escuchado del año."
        ),
        mapOf(
            "title"       to "X100PRE",
            "artist"      to "Bad Bunny",
            "genre"       to "Reggaetón / Trap Latino",
            "releaseYear" to 2018,
            "coverImage"  to "https://cdn.phototourl.com/free/2026-04-16-40786062-8389-4199-bf77-c95e86398801.webp",
            "description" to "Álbum debut de Bad Bunny que redefinió el reggaetón moderno."
        )
    )

    /**
     * Inserta álbumes de muestra en Firestore solo si la colección está vacía.
     * Llama esto al iniciar la app, por ejemplo desde MainActivity o desde un ViewModel.
     *
     * Ejemplo de uso:
     *   viewModelScope.launch { FirestoreSeedHelper.seedIfEmpty(firestore) }
     */
    suspend fun seedIfEmpty(db: FirebaseFirestore) {
        try {
            val existing = db.collection("albums").limit(1).get().await()
            if (!existing.isEmpty) return  // Ya hay datos, no inserta

            sampleAlbums.forEach { albumData ->
                db.collection("albums").add(albumData).await()
            }
            android.util.Log.d("FirestoreSeed", "✅ ${sampleAlbums.size} álbumes insertados en Firestore")
        } catch (e: Exception) {
            android.util.Log.e("FirestoreSeed", "❌ Error al hacer seed: ${e.message}")
        }
    }
}
