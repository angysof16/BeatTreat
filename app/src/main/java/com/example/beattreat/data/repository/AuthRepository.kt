package com.example.beattreat.data.repository

import com.example.beattreat.data.datasource.AuthRemoteDataSource
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val authRemoteDataSource: AuthRemoteDataSource
) {

    suspend fun signIn(email: String, password: String): Result<Unit> {
        return try {
            authRemoteDataSource.signIn(email, password)
            Result.success(Unit)

        } catch (e: FirebaseAuthInvalidCredentialsException) {
            // correo o contraseña incorrecta
            Result.failure(Exception("Correo o contraseña incorrectos"))

        } catch (e: FirebaseAuthInvalidUserException) {
            // La cuenta fue eliminada / deshabilitada
            when (e.errorCode) {
                "ERROR_USER_DISABLED" ->
                    Result.failure(Exception("Esta cuenta ha sido deshabilitada"))
                else ->
                    Result.failure(Exception("No existe una cuenta con ese correo"))
            }

        } catch (e: java.net.UnknownHostException) {
            Result.failure(Exception("Sin conexión a internet. Verifica tu red."))

        } catch (e: java.net.SocketTimeoutException) {
            Result.failure(Exception("La conexión tardó demasiado. Intenta de nuevo."))

        } catch (e: Exception) {
            Result.failure(Exception("Error al iniciar sesión. Intenta de nuevo."))
        }
    }

    suspend fun signUp(email: String, password: String): Result<Unit> {
        return try {
            authRemoteDataSource.signUp(email, password)
            Result.success(Unit)

        } catch (e: FirebaseAuthUserCollisionException) {
            // correo ya tiene una cuenta registrada
            Result.failure(Exception("Este correo ya está registrado"))

        } catch (e: FirebaseAuthWeakPasswordException) {
            // Contraseña debil
            Result.failure(Exception("La contraseña es demasiado débil (mínimo 6 caracteres)"))

        } catch (e: FirebaseAuthInvalidCredentialsException) {
            // email inválido
            Result.failure(Exception("El formato del correo no es válido"))

        } catch (e: java.net.UnknownHostException) {
            Result.failure(Exception("Sin conexión a internet. Verifica tu red."))

        } catch (e: java.net.SocketTimeoutException) {
            Result.failure(Exception("La conexión tardó demasiado. Intenta de nuevo."))

        } catch (e: Exception) {
            Result.failure(Exception("Error al crear la cuenta. Intenta de nuevo."))
        }
    }

    fun signOut() {
        authRemoteDataSource.signOut()
    }
}