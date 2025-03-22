package com.createfuture.takehome.viewModel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.createfuture.takehome.repository.CharacterRepository
import com.createfuture.takehome.api.RetrofitClient
import com.createfuture.takehome.models.ApiCharacter // Use the correct package

import kotlinx.coroutines.launch

class CharacterViewModel() : ViewModel() {
    private val repository = CharacterRepository(RetrofitClient.apiService)

    private val _characters = mutableStateOf<List<ApiCharacter>>(emptyList())
    val characters: State<List<ApiCharacter>> get() = _characters

    private val _errorMessage = mutableStateOf<String?>(null)
    val errorMessage: State<String?> get() = _errorMessage

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> get() = _isLoading

    fun loadCharacters(token: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.getCharacters(token)
            _isLoading.value = false

            when {
                result.isSuccess -> _characters.value = (result.getOrNull() ?: emptyList())
                result.isFailure -> _errorMessage.value = result.exceptionOrNull()?.message
            }
        }
    }
}


