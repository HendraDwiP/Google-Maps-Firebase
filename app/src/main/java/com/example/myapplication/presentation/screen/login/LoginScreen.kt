package com.example.myapplication.presentation.screen.login

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.compose.material3.Icon
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.myapplication.R
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.example.myapplication.utils.Constant.CLIENT
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch


@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    @Suppress("DEPRECATION")
    val launcher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.StartActivityForResult()) {
            val account = GoogleSignIn.getSignedInAccountFromIntent(it.data)
            try {
                val result = account.getResult(ApiException::class.java)
                val credential = GoogleAuthProvider.getCredential(result.idToken, null)
                viewModel.loginWithGoogle(credential) {
                    navController.navigate("home") {
                        popUpTo("home") {
                            inclusive = true
                        }
                    }
                }
            } catch (it: ApiException) {
                Toast.makeText(context, "$it", Toast.LENGTH_SHORT).show()
            }
        }

    val coroutineScope = rememberCoroutineScope()

    val googleLoginState = viewModel.stateGoogle.value
    val state = viewModel.state.collectAsState(initial = null)

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var isLoginMode by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "INFINITE APP",
            fontSize = 32.sp,
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = if (isLoginMode) "LOGIN" else "REGISTER",
            fontSize = 24.sp,
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Email TextField
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                val icon = if (passwordVisible) Icons.Default.ThumbUp else Icons.Default.Face
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(imageVector = icon, contentDescription = null)
                }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        if (!isLoginMode) {
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("Konfirmasi Password") },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val icon = if (passwordVisible) Icons.Default.ThumbUp else Icons.Default.Face
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(imageVector = icon, contentDescription = null)
                    }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Tombol Login atau Register
        Button(
            onClick = {
                if (isLoginMode) {
                    coroutineScope.launch {
                        if (email.isBlank() || password.isBlank()) {
                            Toast.makeText(
                                context,
                                "Email dan password tidak boleh kosong",
                                Toast.LENGTH_SHORT
                            ).show()  // Don't forget to call show()
                        } else {
                            viewModel.loginUser(email, password) {
                                navController.navigate("home") {
                                    popUpTo("login") { inclusive = true }
                                }
                                email = ""
                                password = ""
                            }
                        }
                    }
                } else {
                    if (password == confirmPassword) {
                        // Logic for registration
                        Log.d("LoginScreen", "Password confirmed, registering user ${email} with ${password}")
                        coroutineScope.launch {
                            viewModel.registerUser(email, password) {
                                Toast.makeText(
                                    context,
                                    "Register Berhasil",
                                    Toast.LENGTH_SHORT
                                ).show()  // Call show() to display the toast
                                email = ""  // Clear the email after successful registration
                                password = ""  // Clear the password after successful registration
                                confirmPassword = ""  // Clear the confirm password after registration
                            }
                        }
                    } else {
                        Toast.makeText(
                            context,
                            "Konfirmasi password tidak sesuai",
                            Toast.LENGTH_SHORT
                        ).show()  // Call show() to display the toast
                    }
                }

            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (isLoginMode) "Login" else "Register")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Tombol Login dengan Google
        Button(
            onClick = {
                val googleLogin = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                    .requestIdToken(CLIENT)
                    .build()

                @Suppress("DEPRECATION")
                val googleLoginClient = GoogleSignIn.getClient(context, googleLogin)
                launcher.launch(googleLoginClient.signInIntent)
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color.White),
            border = ButtonDefaults.outlinedButtonBorder,
            modifier = Modifier.fillMaxWidth()
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo_google),
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (isLoginMode) "Login with Google" else "Register with Google",
                color = Color.Black
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Tombol untuk Ganti Mode
        TextButton(onClick = { isLoginMode = !isLoginMode }) {
            Text(
                text = if (isLoginMode) "Belum punya akun? Register di sini" else "Sudah punya akun? Login di sini",
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
    LaunchedEffect(key1 = state.value?.success) {
        coroutineScope.launch {
            if (state.value?.success?.isNotEmpty() == true) {
                val success = state.value?.success
                Toast.makeText(context, "$success", Toast.LENGTH_SHORT).show()
            }
        }
    }
    LaunchedEffect(key1 = state.value?.error) {
        coroutineScope.launch {
            if (state.value?.error?.isNotEmpty() == true) {
                val error = state.value?.error
                Toast.makeText(context, "$error", Toast.LENGTH_SHORT).show()
            }
        }
    }
    LaunchedEffect(key1 = googleLoginState.success) {
        coroutineScope.launch {
            if (googleLoginState.success != null) {
                Toast.makeText(context, "Login With Google Success", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    MyApplicationTheme {
        LoginScreen(navController = NavController(LocalContext.current))
    }
}
