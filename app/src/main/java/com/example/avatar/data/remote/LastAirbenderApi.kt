package com.example.avatar.data.remote

import com.example.avatar.data.remote.dto.CharacterDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface LastAirbenderApi {

    @GET("/api/v1/characters")
    suspend fun getCharacters(
        @Query("page") page: Int? = null,
        @Query("perPage") perPage: Int? = null,
        @Query("name") name: String? = null,
        @Query("affiliation") affiliation: String? = null
    ): List<CharacterDto>

    @GET("/api/v1/characters/{id}")
    suspend fun getCharacterById(@Path("id") id: String): CharacterDto
}
