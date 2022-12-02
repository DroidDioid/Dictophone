package ru.tim.dictophone.listRecord

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import ru.tim.dictophone.R
import ru.tim.dictophone.database.RecordDatabase
import ru.tim.dictophone.database.RecordDatabaseDao
import ru.tim.dictophone.database.RecordingItem
import ru.tim.dictophone.databinding.FragmentListRecordBinding
import ru.tim.dictophone.player.PlayerFragment
import ru.tim.dictophone.removeDialog.RemoveDialogFragment
import java.io.File

/**
 * Отображает список записей.
 * Реализует интерфейс [ListRecordAdapter.Callbacks] для получения данных из адаптера списка.
 */
class ListRecordFragment : Fragment(), ListRecordAdapter.Callbacks {

    private lateinit var database: RecordDatabaseDao
    private val listRecordViewModel: ListRecordViewModel by viewModels {
        ListRecordViewModelFactory(database)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding = DataBindingUtil.inflate<FragmentListRecordBinding>(
            inflater,
            R.layout.fragment_list_record,
            container,
            false
        )

        val application = requireNotNull(activity).application
        database = RecordDatabase.getInstance(application).recordDatabaseDao

        binding.listRecordViewModel = listRecordViewModel
        binding.lifecycleOwner = this

        val adapter = ListRecordAdapter(this)
        binding.recyclerView.adapter = adapter

        listRecordViewModel.records.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }

        return binding.root
    }

    /** Отображает диалоговое окно проигрывателя [PlayerFragment]. */
    private fun playRecord(filePath: String) {
        PlayerFragment.newInstance(filePath).show(parentFragmentManager, DIALOG_PLAYBACK)
    }

    /** Обрабатывает нажатие на [recordingItem]. */
    override fun onItemClick(recordingItem: RecordingItem) {
        val filePath = recordingItem.filePath

        val file = File(filePath)
        if (file.exists()) {
            playRecord(filePath)
        } else {
            Toast.makeText(context, R.string.file_is_not_exist_text, Toast.LENGTH_SHORT).show()
        }
    }

    /** Обрабатывает долгое нажатие на [recordingItem]. */
    override fun onItemLongClick(recordingItem: RecordingItem) {
        removeItemDialog(recordingItem)
    }

    /** Отображает диалоговое окно удаления записи [RemoveDialogFragment]. */
    private fun removeItemDialog(recordingItem: RecordingItem) {
        RemoveDialogFragment.newInstance(recordingItem.id, recordingItem.filePath)
            .show(parentFragmentManager, DIALOG_REMOVE)
    }

    companion object {
        private const val DIALOG_PLAYBACK = "DialogPlayback"
        private const val DIALOG_REMOVE = "DialogRemove"
    }
}