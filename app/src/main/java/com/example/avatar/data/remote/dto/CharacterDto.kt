package com.example.avatar.data.remote.dto

import com.example.avatar.domain.model.Character

data class CharacterDto(
    val _id: String,
    val allies: List<String>,
    val enemies: List<String>,
    val photoUrl: String?,
    val name: String,
    val affiliation: String?
)

fun CharacterDto.toCharacter(): Character {
    return Character(
        id = _id,
        name = name,
        photoUrl = photoUrl ?: "",
        affiliation = affiliation ?: "Sin afiliación",
        allies = allies,
        enemies = enemies
    )
}
