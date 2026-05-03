package com.example.beattreat.data.datasource.implementation.firestore

import com.example.beattreat.data.datasource.FollowRemoteDataSource
import com.example.beattreat.data.dto.FirestoreUserDto
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

/**
 * FIX Sprint 3 — Bug #2
 *
 * Problema original: transaction.update() lanza excepción si el campo
 * followersCount / followingCount NO existe todavía en el documento
 * (usuarios registrados antes del Sprint 3 no tienen esos campos).
 *
 * Solución: usar transaction.set(..., SetOptions.merge()) para los contadores.
 * Con merge, si el campo no existe lo crea; si ya existe lo actualiza.
 * Para los incrementos usamos FieldValue.increment() dentro de un map con merge.
 */
class FollowFirestoreDataSourceImpl @Inject constructor(
    private val db: FirebaseFirestore
) : FollowRemoteDataSource {

    companion object {
        private const val USERS_COL       = "users"
        private const val FOLLOWERS_COL   = "followers"
        private const val FOLLOWING_COL   = "following"
        private const val FOLLOWERS_COUNT = "followersCount"
        private const val FOLLOWING_COUNT = "followingCount"
    }

    override suspend fun followOrUnfollow(currentUserId: String, targetUserId: String): Boolean {
        val currentUserRef = db.collection(USERS_COL).document(currentUserId)
        val targetUserRef  = db.collection(USERS_COL).document(targetUserId)
        val followingRef   = currentUserRef.collection(FOLLOWING_COL).document(targetUserId)
        val followerRef    = targetUserRef.collection(FOLLOWERS_COL).document(currentUserId)

        return db.runTransaction { transaction ->
            val followingDoc = transaction.get(followingRef)

            if (followingDoc.exists()) {
                // UNFOLLOW — elimina docs y decrementa contadores
                transaction.delete(followingRef)
                transaction.delete(followerRef)
                // FIX: set+merge en lugar de update para no fallar si el campo no existe
                transaction.set(
                    currentUserRef,
                    mapOf(FOLLOWING_COUNT to FieldValue.increment(-1)),
                    SetOptions.merge()
                )
                transaction.set(
                    targetUserRef,
                    mapOf(FOLLOWERS_COUNT to FieldValue.increment(-1)),
                    SetOptions.merge()
                )
                false
            } else {
                // FOLLOW — crea docs e incrementa contadores
                val timestamp = mapOf("timestamp" to System.currentTimeMillis())
                transaction.set(followingRef, timestamp)
                transaction.set(followerRef, timestamp)
                // FIX: set+merge crea el campo si no existía
                transaction.set(
                    currentUserRef,
                    mapOf(FOLLOWING_COUNT to FieldValue.increment(1)),
                    SetOptions.merge()
                )
                transaction.set(
                    targetUserRef,
                    mapOf(FOLLOWERS_COUNT to FieldValue.increment(1)),
                    SetOptions.merge()
                )
                true
            }
        }.await()
    }

    override suspend fun isFollowing(currentUserId: String, targetUserId: String): Boolean {
        val doc = db.collection(USERS_COL)
            .document(currentUserId)
            .collection(FOLLOWING_COL)
            .document(targetUserId)
            .get()
            .await()
        return doc.exists()
    }

    override suspend fun getFollowersCount(userId: String): Int {
        val doc = db.collection(USERS_COL).document(userId).get().await()
        return (doc.getLong(FOLLOWERS_COUNT) ?: 0L).toInt()
    }

    override suspend fun getFollowingCount(userId: String): Int {
        val doc = db.collection(USERS_COL).document(userId).get().await()
        return (doc.getLong(FOLLOWING_COUNT) ?: 0L).toInt()
    }

    override suspend fun getFollowerIds(userId: String): List<String> {
        val snapshot = db.collection(USERS_COL)
            .document(userId)
            .collection(FOLLOWERS_COL)
            .get()
            .await()
        return snapshot.documents.map { it.id }
    }

    override suspend fun getFollowingIds(userId: String): List<String> {
        val snapshot = db.collection(USERS_COL)
            .document(userId)
            .collection(FOLLOWING_COL)
            .get()
            .await()
        return snapshot.documents.map { it.id }
    }

    override suspend fun getFollowersUsers(userId: String): List<FirestoreUserDto> {
        val ids = getFollowerIds(userId)
        return ids.mapNotNull { followerId ->
            runCatching {
                db.collection(USERS_COL).document(followerId).get().await()
                    .toObject(FirestoreUserDto::class.java)
            }.getOrNull()
        }
    }

    override suspend fun getFollowingUsers(userId: String): List<FirestoreUserDto> {
        val ids = getFollowingIds(userId)
        return ids.mapNotNull { followingId ->
            runCatching {
                db.collection(USERS_COL).document(followingId).get().await()
                    .toObject(FirestoreUserDto::class.java)
            }.getOrNull()
        }
    }
}