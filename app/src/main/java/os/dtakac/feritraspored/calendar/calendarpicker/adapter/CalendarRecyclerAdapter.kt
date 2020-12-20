package os.dtakac.feritraspored.calendar.calendarpicker.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import os.dtakac.feritraspored.R
import os.dtakac.feritraspored.calendar.data.CalendarData
import os.dtakac.feritraspored.common.extensions.getColorCompat
import os.dtakac.feritraspored.databinding.CellCalendarBinding

class CalendarRecyclerAdapter(
        private val clickListener: ClickListener
) : RecyclerView.Adapter<CalendarRecyclerAdapter.CalendarViewHolder>() {
    private val calendars: MutableList<CalendarData> = mutableListOf()

    override fun getItemCount(): Int {
        return calendars.size
    }

    override fun onBindViewHolder(holder: CalendarViewHolder, position: Int) {
        holder.bind(calendars[position], clickListener)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarViewHolder {
        val binding = CellCalendarBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
        )
        return CalendarViewHolder(binding)
    }

    fun setCalendars(calendars: List<CalendarData>) {
        this.calendars.clear()
        this.calendars.addAll(calendars)
        notifyDataSetChanged()
    }

    class CalendarViewHolder(
            private val binding: CellCalendarBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: CalendarData, clickListener: ClickListener) {
            binding.apply {
                ivColor.setColorFilter(data.color ?: root.context.getColorCompat(R.color.colorPrimary))
                tvName.text = data.name ?: root.context.getString(R.string.events)
                tvAccount.text = data.account
                root.setOnClickListener { clickListener.onClick(data.id) }
            }
        }
    }

    interface ClickListener {
        fun onClick(calendarId: String)
    }
}