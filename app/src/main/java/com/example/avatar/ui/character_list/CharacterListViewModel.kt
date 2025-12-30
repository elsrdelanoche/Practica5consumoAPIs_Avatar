package com.example.avatar.ui.character_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.avatar.domain.model.Character
import com.example.avatar.domain.repository.CharacterRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CharacterListViewModel @Inject constructor(
    private val repository: CharacterRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _selectedAffiliation = MutableStateFlow("")
    val selectedAffiliation = _selectedAffiliation.asStateFlow()

    private val _showOnlyFavorites = MutableStateFlow(false)
    val showOnlyFavorites = _showOnlyFavorites.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing = _isRefreshing.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val characters: Flow<PagingData<Character>> = combine(
        _searchQuery,
        _selectedAffiliation,
        _showOnlyFavorites
    ) { query, affiliation, onlyFavs ->
        Triple(query, affiliation, onlyFavs)
    }.flatMapLatest { (query, affiliation, onlyFavs) ->
        repository.getCharactersPaged(query, affiliation, onlyFavs)
    }.cachedIn(viewModelScope)

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun onAffiliationSelect(affiliation: String) {
        _selectedAffiliation.value = if (_selectedAffiliation.value == affiliation) "" else affiliation
    }

    fun toggleFavoritesFilter() {
        _showOnlyFavorites.value = !_showOnlyFavorites.value
    }

    fun toggleFavorite(id: String, isFavorite: Boolean) {
        viewModelScope.launch {
            repository.toggleFavorite(id, !isFavorite)
        }
    }

    fun refreshData() {
        viewModelScope.launch {
            _isRefreshing.value = true
            try {
                repository.refreshCharacters()
            } catch (e: Exception) {
                // Error handling handled by Paging or UI via Flow
            } finally {
                _isRefreshing.value = false
            }
        }
    }
}
