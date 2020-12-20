package os.dtakac.feritraspored.calendar.calendarpicker.view

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import org.koin.android.viewmodel.ext.android.viewModel
import os.dtakac.feritraspored.calendar.calendarpicker.adapter.CalendarRecyclerAdapter
import os.dtakac.feritraspored.calendar.calendarpicker.viewmodel.CalendarPickerViewModel
import os.dtakac.feritraspored.common.constants.REQUEST_READ_CALENDAR
import os.dtakac.feritraspored.databinding.FragmentCalendarPickerBinding

class CalendarPickerFragment : Fragment(), CalendarRecyclerAdapter.ClickListener {
    private var _binding: FragmentCalendarPickerBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CalendarPickerViewModel by viewModel()
    private val args: CalendarPickerFragmentArgs by navArgs()
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
        findNavController().navigate(CalendarPickerFragmentDirections.actionEvents(
                scheduleUrl = args.scheduleUrl,
                calendarId = calendarId
        ))
    }

    private fun checkPermissionsAndInitialize() {
        if(checkSelfPermission(requireContext(), Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_GRANTED) {
            viewModel.getCalendars()
        } else {
            requestPermissions(arrayOf(Manifest.permission.READ_CALENDAR), REQUEST_READ_CALENDAR)
        }
    }

    private fun initObservers() {
        viewModel.calendars.observe(viewLifecycleOwner) {
            adapter.setCalendars(it)
        }
        viewModel.isLoaderVisible.observe(viewLifecycleOwner) { shouldShow ->
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