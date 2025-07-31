package com.example.libbook.viewmodels


import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.libbook.data.local.BookShopDatabase
import com.example.libbook.data.models.User
import com.example.libbook.data.remote.RetrofitInstance
import com.example.libbook.repository.BookShopRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// Represents the result of a login attempt.
sealed class LoginResult {
    data object Idle : LoginResult()
    data class Success(val user: User) : LoginResult()
    data class Error(val message: String) : LoginResult()
}

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: BookShopRepository
    private val _loginResult = MutableStateFlow<LoginResult>(LoginResult.Idle)
    val loginResult: StateFlow<LoginResult> = _loginResult

    init {
        val bookShopDao = BookShopDatabase.getDatabase(application).bookShopDao()
        repository = BookShopRepository(bookShopDao, RetrofitInstance.api)
    }

    fun login(username: String, password: String) {
        viewModelScope.launch {
            val user = repository.getUser(username)
            if (user == null) {
                _loginResult.value = LoginResult.Error("User not found.")
            } else if (user.password != password) { // Plain text check per brief.
                _loginResult.value = LoginResult.Error("Incorrect password.")
            } else {
                _loginResult.value = LoginResult.Success(user)
            }
        }
    }

    fun resetLoginState() {
        _loginResult.value = LoginResult.Idle
    }
}