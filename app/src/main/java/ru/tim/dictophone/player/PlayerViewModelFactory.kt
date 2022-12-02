package ru.tim.dictophone.player

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ru.tim.dictophone.removeDialog.RemoveViewModel

/**
 * Фабрика для создания экземпляра [PlayerViewModel]
 * @property mediaPath Путь к медиафайлу
 * @property application
 */
class PlayerViewModelFactory(
    private val mediaPath: String?,
    private val application: Application
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PlayerViewModel::class.java)) {
            return PlayerViewModel(mediaPath, application) as T
        }
        throw IllegalArgumentException("unknown ViewModel class")
    }
}