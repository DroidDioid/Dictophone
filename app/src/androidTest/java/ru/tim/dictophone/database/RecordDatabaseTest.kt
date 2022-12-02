package ru.tim.dictophone.database

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.After
import org.junit.Before

import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import ru.tim.dictophone.util.getOrAwaitValue
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class RecordDatabaseTest {

    private lateinit var recordDatabaseDao: RecordDatabaseDao
    private lateinit var database: RecordDatabase

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun createDb() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        database = Room.inMemoryDatabaseBuilder(context, RecordDatabase::class.java)
            .allowMainThreadQueries()
            .build()

        recordDatabaseDao = database.recordDatabaseDao
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        database.close()
    }

    @Test
    fun testInsertIntoDatabase() {
        recordDatabaseDao.insert(RecordingItem())
        val count = recordDatabaseDao.getCount().getOrAwaitValue()
        assertEquals(count, 1)
    }

    @Test
    fun testUpdateDatabase() {
        val newName = "new_name"
        val recordingItem = RecordingItem(id = 9)
        recordDatabaseDao.insert(recordingItem)
        recordingItem.name = newName
        recordDatabaseDao.update(recordingItem)
        val dbRecordingItem = recordDatabaseDao.getRecord(recordingItem.id).getOrAwaitValue()
        assertNotNull(dbRecordingItem)
        assertEquals(dbRecordingItem!!.name, newName)
    }

    @Test
    fun testGetRecordFromDatabase() {
        val recordingItem = RecordingItem(id = 6)
        recordDatabaseDao.insert(recordingItem)
        val dbRecordingItem = recordDatabaseDao.getRecord(recordingItem.id).getOrAwaitValue()
        assertNotNull(dbRecordingItem)
        assertEquals(dbRecordingItem, recordingItem)
    }

    @Test
    fun testClearAllInDatabase() {
        recordDatabaseDao.insert(RecordingItem())
        recordDatabaseDao.insert(RecordingItem())
        recordDatabaseDao.insert(RecordingItem())

        var count = recordDatabaseDao.getCount().getOrAwaitValue()
        assertEquals(count, 3)

        recordDatabaseDao.clearAll()
        count = recordDatabaseDao.getCount().getOrAwaitValue()
        assertEquals(count, 0)
    }

    @Test
    fun testRemoveRecordFromDatabase() {
        recordDatabaseDao.insert(RecordingItem())
        recordDatabaseDao.insert(RecordingItem())

        val recordingItem = RecordingItem()
        recordDatabaseDao.insert(recordingItem)

        var count = recordDatabaseDao.getCount().getOrAwaitValue()
        assertEquals(count, 3)

        recordDatabaseDao.removeRecord(recordingItem.id)
        count = recordDatabaseDao.getCount().getOrAwaitValue()
        assertEquals(count, 3)

        val dbRecordingItem = recordDatabaseDao.getRecord(recordingItem.id).getOrAwaitValue()
        assertNull(dbRecordingItem)
    }

    @Test
    fun testGetAllRecordsFromDatabase() {
        val list = listOf(RecordingItem(id = 1), RecordingItem(id = 2), RecordingItem(id = 4))
        recordDatabaseDao.insert(list[0])
        recordDatabaseDao.insert(list[1])
        recordDatabaseDao.insert(list[2])
        val dbList = recordDatabaseDao.getAllRecords().getOrAwaitValue()
        assertEquals(list.toSet(), dbList.toSet())
    }

    @Test
    fun testGetCountOfRecordsFromDatabase() {
        val count = recordDatabaseDao.getCount().getOrAwaitValue()
        assertEquals(count, 0)
    }
}