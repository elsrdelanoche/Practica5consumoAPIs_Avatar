package com.example.avatar.ui.character_list

import com.example.avatar.domain.model.Character

data class CharacterListState(
    val characters: List<Character> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val searchQuery: String = "",
    val showOnlyFavorites: Boolean = false,
    val selectedAffiliation: String? = null
)