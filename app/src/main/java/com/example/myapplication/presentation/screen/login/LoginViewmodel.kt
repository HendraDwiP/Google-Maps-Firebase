package com.example.myapplication.presentation.screen.login

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.firebase.AuthRepository
import com.example.myapplication.data.firebase.Resource
import com.google.firebase.auth.AuthCredential
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {
    private val _state = Channel<LoginState>()
    val state = _state.receiveAsFlow()

    private val _stateGoogle = mutableStateOf(LoginGoogleState())
    val stateGoogle: State<LoginGoogleState> = _stateGoogle

    fun loginUser(email: String, password: String, home: () -> Unit) {
        viewModelScope.launch {
            repository.loginUser(email = email, password = password).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        _state.send(LoginState(success = "Login Berhasil"))
                        home()
                    }

                    is Resource.Loading -> {
                        _state.send(LoginState(loading = true))
                    }

                    is Resource.Error -> {
                        _state.send(LoginState(error = result.message))
                    }
                }
            }
        }
    }

    fun registerUser(email: String, password: String, home: () -> Unit) {
        viewModelScope.launch {
            repository.registerUser(email = email, password = password).collect { result ->
                Log.d("LoginViewModel", "Repository result: $result $email")  // Debug log
                when (result) {
                    is Resource.Success -> {
                        _state.send(LoginState(success = "Register Berhasil"))
                        home()
                    }

                    is Resource.Loading -> {
                        _state.send(LoginState(loading = true))
                    }

                    is Resource.Error -> {
                        _state.send(LoginState(error = result.message))
                    }
                }
            }
        }
    }

    fun loginWithGoogle(credential: AuthCredential, home: () -> Unit) {
        viewModelScope.launch {
            repository.loginWithGoogle(credential).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        _stateGoogle.value = LoginGoogleState(success = result.data)
                        home()
                    }

                    is Resource.Loading -> {
                        _stateGoogle.value = LoginGoogleState(loading = true)
                    }

                    is Resource.Error -> {
                        _stateGoogle.value = LoginGoogleState(error = result.message)
                    }
                }
            }
        }
    }
}