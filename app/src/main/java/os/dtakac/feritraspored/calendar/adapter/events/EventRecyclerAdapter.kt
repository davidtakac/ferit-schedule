package os.dtakac.feritraspored.calendar.adapter.events

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import os.dtakac.feritraspored.calendar.data.EventData
import os.dtakac.feritraspored.calendar.data.EventGroupData
import os.dtakac.feritraspored.calendar.data.EventSingleData
import os.dtakac.feritraspored.databinding.CellEventBinding
import os.dtakac.feritraspored.databinding.CellEventGroupBinding
import java.lang.IllegalStateException

class EventRecyclerAdapter(
        private val checkListener: CheckListener
) : ListAdapter<EventData, RecyclerView.ViewHolder>(EventDiffCallback()) {
    companion object {
        private const val TYPE_GROUP = 1
        private const val TYPE_EVENT = 2
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is EventGroupData -> TYPE_GROUP
            is EventSingleData -> TYPE_EVENT
            else -> throw IllegalStateException("No view type for instance of EventData.")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        when (getItemViewType(position)) {
            TYPE_GROUP -> (holder as EventGroupViewHolder).bind(item as EventGroupData)
            TYPE_EVENT -> (holder as EventViewHolder).bind(item as EventSingleData)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_GROUP -> {
                val binding = CellEventGroupBinding
                        .inflate(LayoutInflater.from(parent.context), parent, false)
                EventGroupViewHolder(binding, checkListener)
            }

            TYPE_EVENT -> {
                val binding = CellEventBinding
                        .inflate(LayoutInflater.from(parent.context), parent, false)
                EventViewHolder(binding, checkListener)
            }

            else -> throw IllegalStateException("No ViewHolder defined for view type.")
        }
    }

    interface CheckListener {
        fun onChecked(data: EventData, isChecked: Boolean)
    }

    private class EventGroupViewHolder(
            private val binding: CellEventGroupBinding,
            private val checkListener: CheckListener
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: EventGroupData) {
            binding.apply {
                tvLabel.text = data.title
                checkbox.isChecked = data.isChecked
                checkbox.setOnCheckedChangeListener { _, isChecked ->
                    checkListener.onChecked(data, isChecked)
                }
            }
        }
    }

    private class EventViewHolder(
            private val binding: CellEventBinding,
            private val checkListener: CheckListener
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: EventSingleData) {
            binding.apply {
                tvTitle.text = data.title
                tvDescription.text = data.description
                tvTimes.text = data.times
                checkbox.isChecked = data.isChecked
                checkbox.setOnCheckedChangeListener { _, isChecked ->
                    checkListener.onChecked(data, isChecked)
                }
            }
        }
    }

    private class EventDiffCallback : DiffUtil.ItemCallback<EventData>() {
        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: EventData, newItem: EventData): Boolean {
            // all [EventData] implementations are data classes
            return oldItem == newItem
        }

        override fun areItemsTheSame(oldItem: EventData, newItem: EventData): Boolean {
            return oldItem.id == newItem.id
        }
    }
}