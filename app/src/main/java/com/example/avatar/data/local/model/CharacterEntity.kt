package com.example.avatar.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.example.avatar.domain.model.Character
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

@Entity(tableName = "characters")
data class CharacterEntity(
    @PrimaryKey val id: String,
    val name: String,
    val affiliation: String?,
    val photoUrl: String?,
    val allies: List<String>,
    val enemies: List<String>,
    val isFavorite: Boolean = false
)

class Converters {
    @TypeConverter
    fun fromString(value: String): List<String> {
        val listType = object : TypeToken<List<String>>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun fromList(list: List<String>): String {
        return Gson().toJson(list)
    }
}

fun CharacterEntity.toDomain(): Character {
    return Character(
        id = this.id,
        name = this.name,
        affiliation = this.affiliation,
        photoUrl = this.photoUrl ?: "",
        allies = this.allies,
        enemies = this.enemies,
        isFavorite = this.isFavorite
    )
}

fun Character.toEntity(): CharacterEntity {
    return CharacterEntity(
        id = this.id,
        name = this.name,
        affiliation = this.affiliation,
        photoUrl = this.photoUrl,
        allies = this.allies,
        enemies = this.enemies,
        isFavorite = this.isFavorite
    )
}