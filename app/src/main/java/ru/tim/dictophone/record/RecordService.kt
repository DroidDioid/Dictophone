package ru.tim.dictophone.record

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.media.MediaRecorder
import android.os.Build
import android.os.IBinder
import android.os.SystemClock
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.*
import ru.tim.dictophone.MainActivity
import ru.tim.dictophone.R
import ru.tim.dictophone.database.RecordDatabase
import ru.tim.dictophone.database.RecordDatabaseDao
import ru.tim.dictophone.database.RecordingItem
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

/** Сервис для выполнения записи с диктофона в фоновом режиме */
class RecordService : Service() {

    /** Имя файла для записи. */
    private var fileName: String? = null

    /** Путь к файлу для записи. */
    private var filePath: String? = null

    private var recorder: MediaRecorder? = null

    private var startingTimeMillis: Long = 0
    private var elapsedMillis: Long = 0

    private var database: RecordDatabaseDao? = null

    private val job = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + job)

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        isRunning = true
        database = RecordDatabase.getInstance(application).recordDatabaseDao
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startRecording()
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        if (recorder != null) {
            stopRecording()
        }
        isRunning = false
        super.onDestroy()
    }

    /** Создаёт, настраивает и запускает [recorder]. */
    private fun startRecording() {
        setFileNameAndPath()

        recorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            MediaRecorder(applicationContext)
        } else {
            MediaRecorder()
        }

        recorder?.apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setOutputFile(filePath)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setAudioChannels(1)
            setAudioEncodingBitRate(192000)
        }

        try {
            recorder?.prepare()
            recorder?.start()
            startingTimeMillis = SystemClock.elapsedRealtime()
            startForeground(1, createNotification())
        } catch (e: IOException) {
            Log.e(TAG, "prepare failed")
        }
    }

    /** Создаёт и возвращает уведомление о записи диктофоном звука. */
    private fun createNotification(): Notification {
        val builder = NotificationCompat.Builder(
            applicationContext,
            getString(R.string.notification_channel_id)
        )
        builder.setSmallIcon(R.drawable.ic_mic)
            .setContentTitle(getString(R.string.app_name))
            .setContentText(getString(R.string.notification_recording))
            .setOngoing(true)
            .setContentIntent(
                PendingIntent.getActivity(
                    applicationContext,
                    0,
                    Intent(applicationContext, MainActivity::class.java),
                    0
                )
            )
        return builder.build()
    }

    /** Создаёт файл, куда будет записываться звук с диктофона,
     *  и задаёт ему имя [fileName], а также путь к нему [filePath]. */
    private fun setFileNameAndPath() {
        var count = 0
        var file: File?
        val dateTime = SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.getDefault())
            .format(System.currentTimeMillis())

        do {
            fileName = (getString(R.string.default_file_name)
                    + "_" + dateTime + count + ".mp4")
            filePath = application.getExternalFilesDir(null)?.absolutePath
            filePath += "/$fileName"

            count++

            file = filePath?.let { File(it) }
        } while (file != null && file.exists() && !file.isDirectory)
    }

    /** Останавливает запись звука диктофоном, сбрасывает [recorder]
     * и сохраняет данные о записанном файле в базу данных [database]. */
    private fun stopRecording() {
        val recordingItem = RecordingItem()

        recorder?.stop()
        elapsedMillis = SystemClock.elapsedRealtime() - startingTimeMillis
        recorder?.release()
        Toast.makeText(
            this,
            getString(R.string.toast_recording_finish),
            Toast.LENGTH_SHORT
        ).show()

        recordingItem.name = fileName.toString()
        recordingItem.filePath = filePath.toString()
        recordingItem.length = elapsedMillis
        recordingItem.time = System.currentTimeMillis()

        recorder = null

        try {
            uiScope.launch {
                withContext(Dispatchers.IO) {
                    database?.insert(recordingItem)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception", e)
        }
    }

    companion object {
        private const val TAG = "RecordService"

        /** Свойство для отслеживания работы сервера */
        var isRunning = false
    }
}