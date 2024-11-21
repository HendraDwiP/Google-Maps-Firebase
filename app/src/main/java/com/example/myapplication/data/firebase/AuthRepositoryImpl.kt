package com.example.myapplication.data.firebase

import android.util.Log
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : AuthRepository {
    override fun loginUser(email: String, password: String): Flow<Resource<AuthResult>> {
        return flow {
            emit(Resource.Loading())
            val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            emit(Resource.Success(result))
        }.catch {
            emit(Resource.Error(it.message.toString()))
        }
    }

    override fun registerUser(email: String, password: String): Flow<Resource<AuthResult>> {
        return flow {
            emit(Resource.Loading())
            try {
                if (email.isBlank() || password.length < 6) {
                    throw IllegalArgumentException("Invalid email or password")
                }
                val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
                if (result.user == null) {
                    throw Exception("User registration failed")
                }

                emit(Resource.Success(result))
            } catch (e: Exception) {
                //Log.e("AuthRepository", "Registration Error: ${e.javaClass.simpleName}", e)
                emit(Resource.Error(e.localizedMessage ?: "Registration Failed"))
            }
        }
    }

    override fun loginWithGoogle(credential: AuthCredential): Flow<Resource<AuthResult>> {
        return flow {
            emit(Resource.Loading())
            val result = firebaseAuth.signInWithCredential(credential).await()
            emit(Resource.Success(result))
        }.catch {
            emit(Resource.Error(it.message.toString()))
        }
    }
}