package ru.tim.dictophone.record

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import ru.tim.dictophone.MainActivity
import ru.tim.dictophone.R
import ru.tim.dictophone.database.RecordDatabase
import ru.tim.dictophone.database.RecordDatabaseDao
import ru.tim.dictophone.databinding.FragmentRecordBinding
import java.io.File

/** Отображает окно управления записью звука с диктофона. */
class RecordFragment : Fragment() {

    private val recordViewModel: RecordViewModel by viewModels()
    private lateinit var binding: FragmentRecordBinding
    private lateinit var database: RecordDatabaseDao

    /** Проверяет и запрашивает разрешение на запись звука. */
    private val checkAudioPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            when {
                granted -> {
                    if (RecordService.isRunning) {
                        onRecord(false)
                        recordViewModel.stopTimer()
                    } else {
                        onRecord(true)
                        recordViewModel.startTimer()
                    }
                }
                !shouldShowRequestPermissionRationale(Manifest.permission.RECORD_AUDIO) -> {
                    Toast.makeText(
                        activity,
                        R.string.toast_recording_permissions_do_not_ask,
                        Toast.LENGTH_LONG
                    ).show()
                }
                else -> {
                    onRecord(false)
                    recordViewModel.stopTimer()

                    Toast.makeText(
                        activity,
                        R.string.toast_recording_permissions,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        createChannel(
            getString(R.string.notification_channel_id),
            getString(R.string.notification_channel_name)
        )

        binding = DataBindingUtil.inflate<FragmentRecordBinding>(
            inflater,
            R.layout.fragment_record,
            container,
            false
        )

        database = RecordDatabase.getInstance(requireContext()).recordDatabaseDao

        binding.viewModel = recordViewModel
        binding.lifecycleOwner = viewLifecycleOwner

        // Если сервис не запущен, сбрасываем таймер
        if (!RecordService.isRunning) {
            recordViewModel.stopTimer()
        } else {
            binding.playButton.setImageResource(R.drawable.ic_stop)
        }

        binding.playButton.setOnClickListener {
            checkAudioPermission.launch(Manifest.permission.RECORD_AUDIO)
        }

        return binding.root
    }

    /**
     * Запускает сервис и начинает запись, если [start] = ```true```.
     * Останавливает сервис и запись, если [start] = ```false```.
     */
    private fun onRecord(start: Boolean) {
        val intent = Intent(activity, RecordService::class.java)

        if (start) {
            binding.playButton.setImageResource(R.drawable.ic_stop)
            Toast.makeText(activity, R.string.toast_recording_start, Toast.LENGTH_SHORT).show()

            activity?.startService(intent)
            activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        } else {
            binding.playButton.setImageResource(R.drawable.ic_mic)

            activity?.stopService(intent)
            activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }

    /** Создаёт канал уведомлений. */
    private fun createChannel(channelId: String, channelName: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                setShowBadge(false)
                setSound(null, null)
            }

            val notificationManager =
                requireActivity().getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }
}