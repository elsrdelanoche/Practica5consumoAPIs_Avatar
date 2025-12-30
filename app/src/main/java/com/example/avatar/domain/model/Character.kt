package com.example.avatar.domain.model

data class Character(
    val id: String,
    val name: String,
    val photoUrl: String,
    val affiliation: String?,
    val allies: List<String> = emptyList(),
    val enemies: List<String> = emptyList(),
    val isFavorite: Boolean = false
)