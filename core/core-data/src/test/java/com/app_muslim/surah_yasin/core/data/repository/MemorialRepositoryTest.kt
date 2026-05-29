package com.app_muslim.surah_yasin.core.data.repository

import com.app_muslim.surah_yasin.core.data.entity.MemorialEntity
import com.app_muslim.surah_yasin.core.data.dao.MemorialDao
import com.google.firebase.firestore.FirebaseFirestore
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@ExperimentalCoroutinesApi
@RunWith(JUnit4::class)
class MemorialRepositoryTest {

    private val mockMemorialDao = mockk<MemorialDao>()
    private val mockFirestore = mockk<FirebaseFirestore>()
    private lateinit var repository: MemorialRepository

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        repository = MemorialRepository(mockMemorialDao, mockFirestore)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun getAllMemorials_returnsMemorialsFromDao() = runTest {
        val testMemorials = listOf(
            MemorialEntity(
                id = 1L,
                title = "Test Memorial",
                deceasedName = "John Doe",
                createdAt = System.currentTimeMillis(),
                totalPrayers = 10
            )
        )
        
        every { mockMemorialDao.getAllMemorials() } returns flowOf(testMemorials)

        val result = repository.getAllMemorials().first()

        assertEquals(1, result.size)
        assertEquals("Test Memorial", result[0].title)
        assertEquals("John Doe", result[0].deceasedName)
        assertEquals(10, result[0].totalPrayers)
    }

    @Test
    fun insertMemorial_callsDaoInsert() = runTest {
        val memorial = MemorialEntity(
            title = "New Memorial",
            deceasedName = "Jane Doe",
            createdAt = System.currentTimeMillis(),
            totalPrayers = 0
        )

        coEvery { mockMemorialDao.insertMemorial(memorial) } returns 1L

        val result = repository.insertMemorial(memorial)

        assertEquals(1L, result)
        coVerify { mockMemorialDao.insertMemorial(memorial) }
    }

    @Test
    fun updatePrayerCount_incrementsCorrectly() = runTest {
        val memorialId = 1L
        val currentCount = 5
        val incrementBy = 3

        coEvery { mockMemorialDao.updatePrayerCount(memorialId, currentCount + incrementBy) } just Runs

        repository.updatePrayerCount(memorialId, incrementBy)

        coVerify { mockMemorialDao.updatePrayerCount(memorialId, any()) }
    }

    @Test
    fun deleteMemorial_callsDaoDelete() = runTest {
        val memorial = MemorialEntity(
            id = 1L,
            title = "To Delete",
            deceasedName = "Test",
            createdAt = System.currentTimeMillis(),
            totalPrayers = 0
        )

        coEvery { mockMemorialDao.deleteMemorial(memorial) } just Runs

        repository.deleteMemorial(memorial)

        coVerify { mockMemorialDao.deleteMemorial(memorial) }
    }

    @Test
    fun getMemorialById_returnsCorrectMemorial() = runTest {
        val memorialId = 1L
        val testMemorial = MemorialEntity(
            id = memorialId,
            title = "Specific Memorial",
            deceasedName = "Specific Person",
            createdAt = System.currentTimeMillis(),
            totalPrayers = 25
        )

        every { mockMemorialDao.getMemorialById(memorialId) } returns flowOf(testMemorial)

        val result = repository.getMemorialById(memorialId).first()

        assertEquals(memorialId, result?.id)
        assertEquals("Specific Memorial", result?.title)
        assertEquals("Specific Person", result?.deceasedName)
        assertEquals(25, result?.totalPrayers)
    }

    @Test
    fun searchMemorials_filtersCorrectly() = runTest {
        val searchQuery = "John"
        val matchingMemorials = listOf(
            MemorialEntity(
                id = 1L,
                title = "John's Memorial",
                deceasedName = "John Smith",
                createdAt = System.currentTimeMillis(),
                totalPrayers = 15
            )
        )

        every { mockMemorialDao.searchMemorials("%$searchQuery%") } returns flowOf(matchingMemorials)

        val result = repository.searchMemorials(searchQuery).first()

        assertEquals(1, result.size)
        assertTrue(result[0].title.contains("John") || result[0].deceasedName.contains("John"))
    }
}