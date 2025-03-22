package com.createfuture.takehome.repository

import com.createfuture.takehome.api.ApiService
import com.createfuture.takehome.models.ApiCharacter


class CharacterRepository(private val apiService: ApiService) {
    suspend fun getCharacters(token: String): Result<List<ApiCharacter>> {
        return try {
            val response = apiService.getCharacters(token)
            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyList())
            } else {
                Result.failure(Exception("Failed to fetch characters: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}