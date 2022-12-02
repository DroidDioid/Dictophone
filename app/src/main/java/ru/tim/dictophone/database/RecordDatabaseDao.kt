package ru.tim.dictophone.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface RecordDatabaseDao {

    @Insert
    fun insert(record: RecordingItem)

    @Update
    fun update(record: RecordingItem)

    @Query("SELECT * from recording_table WHERE id = :key")
    fun getRecord(key: Long?): LiveData<RecordingItem?>

    @Query("DELETE from recording_table")
    fun clearAll()

    @Query("DELETE from recording_table WHERE id = :key")
    fun removeRecord(key: Long?)

    @Query("SELECT * FROM recording_table ORDER BY id DESC")
    fun getAllRecords(): LiveData<MutableList<RecordingItem>>

    @Query("SELECT COUNT(*) FROM recording_table")
    fun getCount(): LiveData<Int>

}