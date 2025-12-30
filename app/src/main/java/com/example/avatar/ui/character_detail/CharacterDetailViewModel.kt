package com.example.avatar.ui.character_detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.avatar.domain.model.Character
import com.example.avatar.domain.repository.CharacterRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CharacterDetailViewModel @Inject constructor(
    private val repository: CharacterRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _character = MutableStateFlow<Character?>(null)
    val character = _character.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    init {
        savedStateHandle.get<String>("characterId")?.let { id ->
            loadCharacter(id)
        }
    }

    private fun loadCharacter(id: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _character.value = repository.getCharacterById(id)
            _isLoading.value = false
        }
    }

    fun toggleFavorite() {
        val currentChar = _character.value ?: return
        viewModelScope.launch {
            val newStatus = !currentChar.isFavorite
            repository.toggleFavorite(currentChar.id, newStatus)
            _character.value = currentChar.copy(isFavorite = newStatus)
        }
    }
}