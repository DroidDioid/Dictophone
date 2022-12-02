package ru.tim.dictophone.removeDialog

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.*
import ru.tim.dictophone.R
import ru.tim.dictophone.database.RecordDatabaseDao
import ru.tim.dictophone.util.SingleLiveEvent
import java.io.File

/**
 * ViewModel для диалогового окна удаления записи из базы данных [dataSource] и из файловой системы.
 */
class RemoveViewModel(
    private val dataSource: RecordDatabaseDao,
    private val defaultDispatcher: CoroutineDispatcher
) : ViewModel() {

    private val job = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + job)


    private val _toastText = SingleLiveEvent<Int>()

    /**
     * Хранит текст тоста.
     * При изменении значения оповещает только одного подписчика только один раз.
     */
    val toastText: LiveData<Int> = _toastText


    /**
     * Удаляет запись с заданным [itemId] из базы данных
     */
    fun removeItem(itemId: Long) {
        try {
            uiScope.launch {
                withContext(defaultDispatcher) {
                    dataSource.removeRecord(itemId)
                }
            }
        } catch (e: Exception) {
            Log.e(REMOVE_TAG, "Remove item error", e)
        }
    }

    /**
     * Удаляет файл по заданному [path], если он существует
     */
    fun removeFile(path: String) {
        val file = File(path)
        if (file.exists()) {
            file.delete()
            _toastText.value = R.string.file_deleted_text
        }
    }

    companion object {
        private const val REMOVE_TAG = "removeItem"
    }

}