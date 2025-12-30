package com.example.avatar.domain.repository

import androidx.paging.PagingData
import com.example.avatar.domain.model.Character
import kotlinx.coroutines.flow.Flow

interface CharacterRepository {

    fun getCharactersPaged(
        query: String = "",
        affiliation: String = "",
        onlyFavorites: Boolean = false
    ): Flow<PagingData<Character>>

    suspend fun getCharacterById(id: String): Character?

    suspend fun toggleFavorite(id: String, isFavorite: Boolean)

    suspend fun refreshCharacters()

    fun getUserProfile(): Flow<UserProfile?>
}

data class UserProfile(
    val name: String?,
    val email: String?,
    val photoUrl: String?,
    val favoriteCount: Int = 0
)
