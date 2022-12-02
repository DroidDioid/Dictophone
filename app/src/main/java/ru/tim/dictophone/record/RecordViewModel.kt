package ru.tim.dictophone.record

import android.app.Application
import android.content.Context
import android.os.CountDownTimer
import android.os.SystemClock
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

/** ViewModel для окна записи звука [RecordFragment]. */
class RecordViewModel(app: Application) : AndroidViewModel(app) {

    /** Свойство для хранения доступа к [SharedPreferences][android.content.SharedPreferences]. */
    private val prefs = app.getSharedPreferences("ru.tim.dictophone", Context.MODE_PRIVATE)

    private val _elapsedTime = MutableLiveData<String>()

    /** Свойство для хранения времени прошедшего с начала записи звука. */
    val elapsedTime: LiveData<String>
        get() = _elapsedTime

    private lateinit var timer: CountDownTimer

    init {
        // Если сервер запущен (то есть уже идёт запись),
        // то нужно создать таймер для отображения его пользователю.
        if (RecordService.isRunning) {
            createTimer()
        }
    }

    /** Преобразует время [time] из милисекунд в формат HH:mm:ss и возвращает в виде строки. */
    fun timeFormatter(time: Long): String {
        return String.format(
            "%02d:%02d:%02d",
            TimeUnit.MILLISECONDS.toHours(time) % 60,
            TimeUnit.MILLISECONDS.toMinutes(time) % 60,
            TimeUnit.MILLISECONDS.toSeconds(time) % 60
        )
    }

    /** Создаёт и запускает таймер [timer],
     * обновляющий свойство [_elapsedTime] с интервалом [INTERVAL]. */
    private fun createTimer() {
        viewModelScope.launch {
            val triggerTime = loadTime()
            timer = object : CountDownTimer(triggerTime, INTERVAL) {

                override fun onTick(millisUntilFinished: Long) {
                    _elapsedTime.value = timeFormatter(SystemClock.elapsedRealtime() - triggerTime)
                }

                override fun onFinish() {
                    resetTimer()
                }

            }
            timer.start()
        }
    }

    /** Фиксирует время начала записи и запускает функцию создания таймера [createTimer]. */
    fun startTimer() {
        val triggerTime = SystemClock.elapsedRealtime()

        viewModelScope.launch {
            saveTime(triggerTime)
            createTimer()
        }
    }

    /** Останавливает работу таймера [timer] и вызывает функцию [resetTimer].*/
    fun stopTimer() {
        if (this::timer.isInitialized) {
            timer.cancel()
        }
        resetTimer()
    }

    /** Сбрасывает данные о времени */
    fun resetTimer() {
        _elapsedTime.value = timeFormatter(0)
        viewModelScope.launch { saveTime(0) }
    }

    /** Сохраняет время старта записи [triggerTime] в милисекундах в SharedPreferences */
    private suspend fun saveTime(triggerTime: Long) =
        withContext(Dispatchers.IO) {
            prefs.edit().putLong(TRIGGER_TIME, triggerTime).apply()
        }

    /** Возвращает время страта записи в милисекундах из SharedPreferences */
    private suspend fun loadTime(): Long =
        withContext(Dispatchers.IO) {
            prefs.getLong(TRIGGER_TIME, 0)
        }

    companion object {
        private const val TRIGGER_TIME = "TRIGGER_AT"
        private const val INTERVAL: Long = 1_000L
    }
}