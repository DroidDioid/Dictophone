package ru.tim.dictophone.listRecord

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.tim.dictophone.R
import ru.tim.dictophone.database.RecordingItem
import java.util.concurrent.TimeUnit

/**
 * Адаптер для списка из окна отображения записей [ListRecordFragment].
 */
class ListRecordAdapter(private val callbacks: Callbacks) :
    ListAdapter<RecordingItem, ListRecordAdapter.ViewHolder>(RecordComparator) {

    /** Интерфейс для связи с [ListRecordFragment]. */
    interface Callbacks {
        fun onItemClick(recordingItem: RecordingItem)
        fun onItemLongClick(recordingItem: RecordingItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater
            .inflate(R.layout.list_item_record, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val fileName: TextView = itemView.findViewById(R.id.file_name_text)
        private val fileLength: TextView = itemView.findViewById(R.id.file_length_text)
        private val cardView: View = itemView.findViewById(R.id.card_view)

        fun bind(recordingItem: RecordingItem) {
            fileName.text = recordingItem.name

            val itemDuration: Long = recordingItem.length
            val minutes = TimeUnit.MILLISECONDS.toMinutes(itemDuration)
            val seconds = (TimeUnit.MILLISECONDS.toSeconds(itemDuration)
                    - TimeUnit.MINUTES.toSeconds(minutes))

            fileLength.text = String.format("%02d:%02d", minutes, seconds)

            cardView.setOnClickListener {
                callbacks.onItemClick(recordingItem)
            }

            cardView.setOnLongClickListener {
                callbacks.onItemLongClick(recordingItem)
                true
            }
        }

    }

    object RecordComparator : DiffUtil.ItemCallback<RecordingItem>() {
        override fun areItemsTheSame(oldItem: RecordingItem, newItem: RecordingItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: RecordingItem, newItem: RecordingItem): Boolean {
            return oldItem == newItem
        }
    }

}