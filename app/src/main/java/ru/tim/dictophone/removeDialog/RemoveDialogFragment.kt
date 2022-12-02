package ru.tim.dictophone.removeDialog

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.Dispatchers
import ru.tim.dictophone.R
import ru.tim.dictophone.database.RecordDatabase

/**
 * Отображает диалоговое окно для удаления записи диктофона.
 * Пользователь может подтвердить или отменить удаление.
 */
class RemoveDialogFragment : DialogFragment() {

    private lateinit var removeViewModel: RemoveViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val application = requireNotNull(activity).application
        val database = RecordDatabase.getInstance(application).recordDatabaseDao

        val viewModelFactory = RemoveViewModelFactory(database, Dispatchers.IO)
        removeViewModel =
            ViewModelProvider(this, viewModelFactory).get(RemoveViewModel::class.java)

        removeViewModel.toastText.observe(this) { resId ->
            Toast.makeText(context, getString(resId), Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val itemPath = arguments?.getString(ARG_ITEM_PATH)
        val itemId = arguments?.getLong(ARG_ITEM_ID)

        return AlertDialog.Builder(activity)
            .setTitle(R.string.dialog_title_delete)
            .setMessage(R.string.dialog_text_delete)
            .setPositiveButton(R.string.dialog_action_yes) { dialog, _ ->
                try {
                    itemId?.let { removeViewModel.removeItem(it) }
                    itemPath?.let { removeViewModel.removeFile(it) }
                } catch (e: Exception) {
                    Log.e(DELETE_FILE_TAG, "Remove file error", e)
                }
                dialog.cancel()
            }
            .setNegativeButton(R.string.dialog_action_no) { dialog, _ ->
                dialog.cancel()
            }
            .create()
    }

    companion object {
        private const val ARG_ITEM_PATH = "recording_item_path"
        private const val ARG_ITEM_ID = "recording_item_id"
        private const val DELETE_FILE_TAG = "delete_file_dialog"

        fun newInstance(itemId: Long, itemPath: String?): RemoveDialogFragment {
            val args = Bundle().apply {
                putLong(ARG_ITEM_ID, itemId)
                putString(ARG_ITEM_PATH, itemPath)
            }
            return RemoveDialogFragment().apply { arguments = args }
        }
    }
}