package com.createfuture.takehome.viewModel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.createfuture.takehome.models.ApiCharacter
import com.createfuture.takehome.repository.CharacterRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule
import org.mockito.kotlin.whenever

@ExperimentalCoroutinesApi
class CharacterViewModelTest {

    // Allow LiveData updates in tests
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    // Use Mockito rule for mocking
    @get:Rule
    val mockitoRule: MockitoRule = MockitoJUnit.rule()

    // Mock the repository
    @Mock
    private lateinit var repository: CharacterRepository

    private lateinit var viewModel: CharacterViewModel

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher) // Set test dispatcher
        viewModel = CharacterViewModel() // Create ViewModel normally

        // Use reflection to inject the mock repository
        val repositoryField = CharacterViewModel::class.java.getDeclaredField("repository")
        repositoryField.isAccessible = true
        repositoryField.set(viewModel, repository)
    }

    @Test
    fun `loadCharacters success should update characters list`() = runTest {
        // Mock data
        val charactersList = listOf(
            ApiCharacter("Jon Snow", "Male", "North", "In Winterfell", "Still alive", listOf("Lord Snow"), listOf("S1", "S2"), listOf("Kit Harington"))
        )

        // Mock repository success response
        whenever(repository.getCharacters("valid_token")).thenReturn(Result.success(charactersList))

        // Call the function
        viewModel.loadCharacters("valid_token")

        advanceUntilIdle() // Wait for coroutine to finish

        // Verify that characters list is updated
        assertEquals(charactersList, viewModel.characters.value)
        assertNull(viewModel.errorMessage.value) // No error
        assertFalse(viewModel.isLoading.value) // Not loading anymore
    }

    @Test
    fun `loadCharacters failure should update errorMessage`() = runTest {
        // Mock repository failure response
        val errorMessage = "API error"
        whenever(repository.getCharacters("invalid_token")).thenReturn(Result.failure(Exception(errorMessage)))

        // Call the function
        viewModel.loadCharacters("invalid_token")

        advanceUntilIdle() // Wait for coroutine to finish

        // Verify that error message is updated
        assertEquals(errorMessage, viewModel.errorMessage.value)
        assertTrue(viewModel.characters.value.isEmpty()) // No data
        assertFalse(viewModel.isLoading.value) // Not loading anymore
    }
}
