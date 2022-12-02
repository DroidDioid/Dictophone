package ru.tim.dictophone.listRecord

import androidx.lifecycle.ViewModel
import ru.tim.dictophone.database.RecordDatabaseDao

/**
 * ViewModel для окна отображения списка записей из базы данных [dataSource].
 */
class ListRecordViewModel(private val dataSource: RecordDatabaseDao) : ViewModel() {

    /** LiveData для наблюдения за всеми записями в базе данных. */
    val records = dataSource.getAllRecords()
}