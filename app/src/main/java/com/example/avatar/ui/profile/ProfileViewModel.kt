package com.example.avatar.ui.profile

import androidx.lifecycle.ViewModel
import com.example.avatar.domain.repository.CharacterRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repository: CharacterRepository
) : ViewModel() {
    val profile = repository.getUserProfile()
}
