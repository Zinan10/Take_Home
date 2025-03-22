package com.createfuture.takehome

import com.createfuture.takehome.repository.CharacterRepository

import com.createfuture.takehome.api.ApiService
import com.createfuture.takehome.models.ApiCharacter
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import retrofit2.Response

@ExperimentalCoroutinesApi
class CharacterRepositoryTest {

    @Mock
    private lateinit var apiService: ApiService

    private lateinit var repository: CharacterRepository

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this) // Initialize mocks
        repository = CharacterRepository(apiService) // Inject mock API service
    }

    @Test
    fun `getCharacters success should return character list`() = runTest {
        // Mock response data
        val charactersList = listOf(
            ApiCharacter("Jon Snow", "Male", "North", "In Winterfell", "Still alive", listOf("Lord Snow"), listOf("S1", "S2"), listOf("Kit Harington"))
        )

        // Mock API success response
        `when`(apiService.getCharacters("valid_token")).thenReturn(Response.success(charactersList))

        // Call repository method
        val result = repository.getCharacters("valid_token")

        // Verify success and correct data
        assertTrue(result.isSuccess)
        assertEquals(charactersList, result.getOrNull())
    }

    @Test
    fun `getCharacters API failure should return error`() = runTest {
        // Mock API failure response
        `when`(apiService.getCharacters("invalid_token")).thenReturn(Response.error(401, okhttp3.ResponseBody.create(null, "Unauthorized")))

        // Call repository method
        val result = repository.getCharacters("invalid_token")

        // Verify failure
        assertTrue(result.isFailure)
        assertNotNull(result.exceptionOrNull())
    }
}
