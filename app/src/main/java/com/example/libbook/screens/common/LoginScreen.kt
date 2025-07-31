package com.example.libbook.screens.common


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.libbook.navigation.Screen
import com.example.libbook.viewmodels.AuthViewModel
import com.example.libbook.viewmodels.LoginResult
import kotlinx.coroutines.flow.collectLatest
import com.example.libbook.R

@Composable
fun LoginScreen(
    navController: NavController,
    authViewModel: AuthViewModel = viewModel()
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val loginState by authViewModel.loginResult.collectAsState()

    // Listen for login results and navigate accordingly
    LaunchedEffect(Unit) {
        authViewModel.loginResult.collectLatest { result ->
            if (result is LoginResult.Success) {
                val destination = if (result.user.isAdmin) {
                    Screen.AdminStockLevels.route
                } else {
                    Screen.CustomerHome.createRoute(result.user.username)
                }
                navController.navigate(destination) {
                    popUpTo(Screen.Login.route) { inclusive = true }
                }
                authViewModel.resetLoginState()
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Color(0xFF0A1A41)
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Welcome to LibBook", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold,
                color = Color.White)
            Spacer(modifier = Modifier.height(32.dp))



            Image(
                painter = painterResource(id = R.drawable.girl_image),
                contentDescription = "Background",
                modifier = Modifier.size(250.dp),
                contentScale = ContentScale.Crop,
                alignment = Alignment.Center
            )

            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Enter Username",color=Color.White) },
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                singleLine = true,
                textStyle = TextStyle(color = Color.White)
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password" ,color=Color.White)},
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                textStyle = TextStyle(color = Color.White)
            )
            Spacer(modifier = Modifier.height(16.dp))

            if (loginState is LoginResult.Error) {
                Text(
                    text = (loginState as LoginResult.Error).message,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            Button(
                onClick = {
                    authViewModel.login(username, password)
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors
                    (containerColor = Color(0xFF9BA9FF))
            ) {
                Text("Login")
            }
            Spacer(modifier = Modifier.height(8.dp))

            Divider(modifier = Modifier.padding(vertical = 16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Don't have an account? ",
                    color = Color(0xFF9BA9FF)
                )
                Text(
                    text = "Sign Up",
                    color = Color(0xFF9BA9FF),
                    modifier = Modifier.clickable {

                    }
                )
            }


        }
    }
}

//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(16.dp),
//        verticalArrangement = Arrangement.Center,
//        horizontalAlignment = Alignment.CenterHorizontally
//    ) {
//        Text("Book Shop Login", style = MaterialTheme.typography.headlineMedium)
//        Spacer(modifier = Modifier.height(32.dp))
//
//        OutlinedTextField(
//            value = username,
//            onValueChange = { username = it },
//            label = { Text("Username") },
//            modifier = Modifier.fillMaxWidth()
//        )
//        Spacer(modifier = Modifier.height(8.dp))
//
//        OutlinedTextField(
//            value = password,
//            onValueChange = { password = it },
//            label = { Text("Password") },
//            visualTransformation = PasswordVisualTransformation(),
//            modifier = Modifier.fillMaxWidth()
//        )
//        Spacer(modifier = Modifier.height(16.dp))
//
//        if (loginState is LoginResult.Error) {
//            Text(
//                text = (loginState as LoginResult.Error).message,
//                color = MaterialTheme.colorScheme.error,
//                style = MaterialTheme.typography.bodyMedium
//            )
//            Spacer(modifier = Modifier.height(8.dp))
//        }
//
//        Button(
//            onClick = { authViewModel.login(username, password) },
//            modifier = Modifier.fillMaxWidth()
//        ) {
//            Text("Login")
//        }
//    }
//}