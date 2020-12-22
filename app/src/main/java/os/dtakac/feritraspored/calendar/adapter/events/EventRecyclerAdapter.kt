package os.dtakac.feritraspored.calendar.adapter.events

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import os.dtakac.feritraspored.R
import os.dtakac.feritraspored.calendar.data.EventData
import os.dtakac.feritraspored.calendar.data.EventGroupData
import os.dtakac.feritraspored.calendar.data.EventSingleData
import os.dtakac.feritraspored.common.constants.EVENT_GROUP_PATTERN
import os.dtakac.feritraspored.common.constants.TIME_PATTERN
import os.dtakac.feritraspored.databinding.CellEventBinding
import os.dtakac.feritraspored.databinding.CellEventGroupBinding
import java.lang.IllegalStateException
import java.time.*
import java.time.format.DateTimeFormatter
import java.util.*

class EventRecyclerAdapter(
        private val eventListener: EventListener
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
                EventGroupViewHolder(binding, eventListener)
            }

            TYPE_EVENT -> {
                val binding = CellEventBinding
                        .inflate(LayoutInflater.from(parent.context), parent, false)
                EventViewHolder(binding, eventListener)
            }

            else -> throw IllegalStateException("No ViewHolder defined for view type.")
        }
    }

    interface EventListener {
        fun onEventChecked(data: EventSingleData, isChecked: Boolean)
        fun onGroupChecked(data: EventGroupData, isChecked: Boolean)
    }

    private class EventGroupViewHolder(
            private val binding: CellEventGroupBinding,
            private val eventListener: EventListener
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: EventGroupData) {
            binding.apply {
                tvLabel.text = buildTitleString(data.date)
                btnSelectAll.setOnClickListener {
                    eventListener.onGroupChecked(data, true)
                }
                btnUnselectAll.setOnClickListener {
                    eventListener.onGroupChecked(data, false)
                }
            }
        }

        private fun buildTitleString(date: LocalDate): String {
            return date.format(DateTimeFormatter.ofPattern(
                    EVENT_GROUP_PATTERN,
                    binding.root.resources.configuration.locale
            ))
        }
    }

    private class EventViewHolder(
            private val binding: CellEventBinding,
            private val eventListener: EventListener
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: EventSingleData) {
            binding.apply {
                val empty = root.resources.getString(R.string.placeholder_empty)
                tvTitle.text = data.title ?: empty
                tvDescription.text = data.description ?: empty
                tvTimes.text = buildTimesString(data.start, data.end)
                tvLocation.text = data.location ?: empty
                checkbox.isChecked = data.isChecked
                checkbox.setOnClickListener {
                    eventListener.onEventChecked(data, !data.isChecked)
                }
            }
        }

        private fun buildTimesString(start: ZonedDateTime, end: ZonedDateTime): String {
            val zoneId = ZoneId.of(TimeZone.getDefault().id)
            val startLocalized = start
                    .withZoneSameInstant(zoneId)
                    .format(DateTimeFormatter.ofPattern(
                            TIME_PATTERN,
                            binding.root.resources.configuration.locale
                    ))
            val endLocalized = end
                    .withZoneSameInstant(zoneId)
                    .format(DateTimeFormatter.ofPattern(
                            TIME_PATTERN,
                            binding.root.resources.configuration.locale
                    ))
            return binding.root.resources
                    .getString(R.string.template_time_span)
                    .format(startLocalized, endLocalized)
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