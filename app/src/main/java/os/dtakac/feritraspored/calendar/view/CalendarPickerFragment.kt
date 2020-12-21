package os.dtakac.feritraspored.calendar.view

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import os.dtakac.feritraspored.R
import os.dtakac.feritraspored.calendar.adapter.calendars.CalendarItemDecoration
import os.dtakac.feritraspored.calendar.adapter.calendars.CalendarRecyclerAdapter
import os.dtakac.feritraspored.calendar.viewmodel.CalendarViewModel
import os.dtakac.feritraspored.common.constants.REQUEST_READ_CALENDAR
import os.dtakac.feritraspored.common.extensions.navGraphViewModel
import os.dtakac.feritraspored.databinding.FragmentCalendarPickerBinding

class CalendarPickerFragment : Fragment(), CalendarRecyclerAdapter.ClickListener {
    private var _binding: FragmentCalendarPickerBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CalendarViewModel by navGraphViewModel(R.id.nav_graph_calendar)
    private val adapter by lazy { CalendarRecyclerAdapter(this) }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCalendarPickerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkPermissionsAndInitialize()
        initViews()
        initObservers()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_READ_CALENDAR -> {
                if (grantResults.getOrNull(0) == PackageManager.PERMISSION_GRANTED) {
                    viewModel.getCalendars()
                } else {
                    findNavController().popBackStack()
                }
            }

            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    override fun onClick(calendarId: String) {
        viewModel.onCalendarPicked(calendarId)
    }

    private fun checkPermissionsAndInitialize() {
        if(checkSelfPermission(requireContext(), Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_GRANTED) {
            viewModel.getCalendars()
        } else {
            requestPermissions(arrayOf(Manifest.permission.READ_CALENDAR), REQUEST_READ_CALENDAR)
        }
    }

    private fun initObservers() {
        viewModel.calendarData.observe(viewLifecycleOwner) {
            adapter.setCalendars(it)
        }
        viewModel.isCalendarsLoaderVisible.observe(viewLifecycleOwner) { shouldShow ->
            binding.loader.apply { if (shouldShow) show() else hide() }
        }
    }

    private fun initViews() {
        binding.rvCalendars.apply {
            adapter = this@CalendarPickerFragment.adapter
            layoutManager = LinearLayoutManager(
                    requireContext(),
                    LinearLayoutManager.VERTICAL,
                    false
            )
            addItemDecoration(CalendarItemDecoration())
        }
        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
        binding.loader.hide()
    }
}