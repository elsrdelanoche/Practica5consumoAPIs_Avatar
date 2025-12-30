package com.example.avatar.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.example.avatar.data.local.CharacterDao
import com.example.avatar.data.local.model.toDomain
import com.example.avatar.data.local.model.toEntity
import com.example.avatar.data.remote.LastAirbenderApi
import com.example.avatar.data.remote.dto.toCharacter
import com.example.avatar.domain.model.Character
import com.example.avatar.domain.repository.CharacterRepository
import com.example.avatar.domain.repository.UserProfile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class CharacterRepositoryImpl @Inject constructor(
    private val api: LastAirbenderApi,
    private val dao: CharacterDao,
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : CharacterRepository {

    override fun getCharactersPaged(
        query: String,
        affiliation: String,
        onlyFavorites: Boolean
    ): Flow<PagingData<Character>> {
        return Pager(
            config = PagingConfig(pageSize = 20),
            pagingSourceFactory = { dao.getPagedCharacters(query, affiliation, onlyFavorites) }
        ).flow.map { pagingData ->
            pagingData.map { it.toDomain() }
        }
    }

    override suspend fun getCharacterById(id: String): Character? {
        return dao.getCharacterById(id)?.toDomain()
    }

    override suspend fun toggleFavorite(id: String, isFavorite: Boolean) {
        dao.updateFavoriteStatus(id, isFavorite)
        
        // Sync with Firestore
        val userId = auth.currentUser?.uid ?: return
        try {
            if (isFavorite) {
                firestore.collection("users").document(userId)
                    .collection("favorites").document(id)
                    .set(mapOf("active" to true)).await()
            } else {
                firestore.collection("users").document(userId)
                    .collection("favorites").document(id)
                    .delete().await()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun refreshCharacters() {
        try {
            // Simple refresh strategy: get first 500 and replace local cache
            val remoteCharacters = api.getCharacters(perPage = 500).map { it.toCharacter() }
            
            // Sync favorites from Firestore before clearing local
            val userId = auth.currentUser?.uid
            val remoteFavorites = if (userId != null) {
                firestore.collection("users").document(userId)
                    .collection("favorites").get().await()
                    .documents.map { it.id }.toSet()
            } else emptySet()

            val entitiesToInsert = remoteCharacters.map { character ->
                character.copy(isFavorite = remoteFavorites.contains(character.id)).toEntity()
            }
            
            dao.clearAll()
            dao.insertAll(entitiesToInsert)
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }

    override fun getUserProfile(): Flow<UserProfile?> = flow {
        val user = auth.currentUser
        if (user != null) {
            val userId = user.uid
            val favoriteCount = try {
                firestore.collection("users").document(userId)
                    .collection("favorites").get().await().size()
            } catch (e: Exception) {
                0
            }
            
            emit(UserProfile(
                name = user.displayName,
                email = user.email,
                photoUrl = user.photoUrl?.toString(),
                favoriteCount = favoriteCount
            ))
        } else {
            emit(null)
        }
    }
}
