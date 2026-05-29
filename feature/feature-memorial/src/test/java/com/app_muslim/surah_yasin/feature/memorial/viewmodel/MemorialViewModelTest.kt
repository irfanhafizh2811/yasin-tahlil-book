package com.app_muslim.surah_yasin.feature.memorial.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.app_muslim.surah_yasin.core.data.repository.MemorialRepository
import com.app_muslim.surah_yasin.feature.memorial.model.Memorial
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.*
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@ExperimentalCoroutinesApi
@RunWith(JUnit4::class)
class MemorialViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()
    private val mockRepository = mockk<MemorialRepository>()
    private lateinit var viewModel: MemorialViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        MockKAnnotations.init(this)
        viewModel = MemorialViewModel(mockRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll()
    }

    @Test
    fun initialState_isCorrect() {
        assertEquals(MemorialUiState(), viewModel.uiState.value)
        assertFalse(viewModel.uiState.value.isLoading)
        assertTrue(viewModel.uiState.value.memorials.isEmpty())
    }

    @Test
    fun loadMemorials_updatesStateCorrectly() = runTest {
        val testMemorials = listOf(
            Memorial(
                id = "1",
                title = "Test Memorial 1",
                deceasedName = "John Doe",
                createdAt = System.currentTimeMillis(),
                totalPrayers = 10
            ),
            Memorial(
                id = "2", 
                title = "Test Memorial 2",
                deceasedName = "Jane Smith",
                createdAt = System.currentTimeMillis(),
                totalPrayers = 5
            )
        )

        every { mockRepository.getAllMemorials() } returns flowOf(testMemorials)

        viewModel.loadMemorials()
        testDispatcher.scheduler.advanceUntilIdle()

        val currentState = viewModel.uiState.value
        assertFalse(currentState.isLoading)
        assertEquals(2, currentState.memorials.size)
        assertEquals("Test Memorial 1", currentState.memorials[0].title)
        assertEquals("Test Memorial 2", currentState.memorials[1].title)
    }

    @Test
    fun createMemorial_callsRepository() = runTest {
        val newMemorial = Memorial(
            title = "New Memorial",
            deceasedName = "New Person",
            createdAt = System.currentTimeMillis(),
            totalPrayers = 0
        )

        coEvery { mockRepository.insertMemorial(any()) } returns 1L

        viewModel.createMemorial(
            title = newMemorial.title,
            deceasedName = newMemorial.deceasedName,
            description = null,
            photoUri = null
        )

        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { mockRepository.insertMemorial(any()) }
    }

    @Test
    fun incrementPrayerCount_updatesRepository() = runTest {
        val memorialId = "1"
        
        coEvery { mockRepository.updatePrayerCount(any(), any()) } just Runs

        viewModel.incrementPrayerCount(memorialId)
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { mockRepository.updatePrayerCount(memorialId.toLong(), 1) }
    }

    @Test
    fun searchMemorials_filtersResults() = runTest {
        val allMemorials = listOf(
            Memorial(
                id = "1",
                title = "John's Memorial",
                deceasedName = "John Doe",
                createdAt = System.currentTimeMillis(),
                totalPrayers = 10
            ),
            Memorial(
                id = "2",
                title = "Mary's Memorial", 
                deceasedName = "Mary Smith",
                createdAt = System.currentTimeMillis(),
                totalPrayers = 5
            )
        )

        val filteredMemorials = listOf(allMemorials[0])

        every { mockRepository.searchMemorials("John") } returns flowOf(filteredMemorials)

        viewModel.searchMemorials("John")
        testDispatcher.scheduler.advanceUntilIdle()

        val currentState = viewModel.uiState.value
        assertEquals(1, currentState.memorials.size)
        assertEquals("John's Memorial", currentState.memorials[0].title)
    }

    @Test
    fun deleteMemorial_removesFromRepository() = runTest {
        val memorial = Memorial(
            id = "1",
            title = "To Delete",
            deceasedName = "Test Person",
            createdAt = System.currentTimeMillis(),
            totalPrayers = 0
        )

        coEvery { mockRepository.deleteMemorial(any()) } just Runs

        viewModel.deleteMemorial(memorial)
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { mockRepository.deleteMemorial(any()) }
    }

    @Test
    fun error_handling_updatesStateCorrectly() = runTest {
        val exception = RuntimeException("Test error")
        
        every { mockRepository.getAllMemorials() } throws exception

        viewModel.loadMemorials()
        testDispatcher.scheduler.advanceUntilIdle()

        val currentState = viewModel.uiState.value
        assertFalse(currentState.isLoading)
        assertTrue(currentState.error != null)
    }
}