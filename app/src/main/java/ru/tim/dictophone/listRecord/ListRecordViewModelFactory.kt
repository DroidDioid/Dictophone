package ru.tim.dictophone.listRecord

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ru.tim.dictophone.database.RecordDatabaseDao
import ru.tim.dictophone.removeDialog.RemoveViewModel

/**
 * Фабрика для создания экземпляра [ListRecordViewModel] с параметром [dataSource]
 */
class ListRecordViewModelFactory(private val dataSource: RecordDatabaseDao) :
    ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ListRecordViewModel::class.java)) {
            return ListRecordViewModel(dataSource) as T
        } else {
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}