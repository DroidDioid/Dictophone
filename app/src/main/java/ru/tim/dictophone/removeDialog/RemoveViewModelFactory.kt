package ru.tim.dictophone.removeDialog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.CoroutineDispatcher
import ru.tim.dictophone.database.RecordDatabaseDao

/**
 * Фабрика для создания экземпляра [RemoveViewModel] с параметром [databaseDao]
 */
class RemoveViewModelFactory(
    private val databaseDao: RecordDatabaseDao,
    private val defaultDispatcher: CoroutineDispatcher
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RemoveViewModel::class.java)) {
            return RemoveViewModel(databaseDao, defaultDispatcher) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}