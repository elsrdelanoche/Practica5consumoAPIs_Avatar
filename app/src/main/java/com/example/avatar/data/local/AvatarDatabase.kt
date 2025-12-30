package com.example.avatar.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.avatar.data.local.model.CharacterEntity
import com.example.avatar.data.local.model.Converters

@Database(
    entities = [CharacterEntity::class],
    version = 3,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class AvatarDatabase : RoomDatabase() {
    abstract fun characterDao(): CharacterDao
}
