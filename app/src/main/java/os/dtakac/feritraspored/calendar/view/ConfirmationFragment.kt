package os.dtakac.feritraspored.calendar.view

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import os.dtakac.feritraspored.R
import os.dtakac.feritraspored.calendar.viewmodel.CalendarViewModel
import os.dtakac.feritraspored.common.constants.REQUEST_WRITE_CALENDAR
import os.dtakac.feritraspored.common.extensions.navGraphViewModel
import os.dtakac.feritraspored.databinding.FragmentConfirmationBinding

class ConfirmationFragment : Fragment() {
    private var _binding: FragmentConfirmationBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CalendarViewModel by navGraphViewModel(R.id.nav_graph_calendar)

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        _binding = FragmentConfirmationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initObservers()
        initViews()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_WRITE_CALENDAR -> {
                if (grantResults.getOrNull(0) == PackageManager.PERMISSION_GRANTED) {
                    viewModel.onAddClicked()
                } else {
                    findNavController().popBackStack()
                }
            }

            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    private fun initObservers() {
        viewModel.eventsReviewData.observe(viewLifecycleOwner) {
            binding.tvEventsNumber.text = it.buildString(resources)
        }
        viewModel.calendarReviewData.observe(viewLifecycleOwner) {
            binding.tvCalendar.text = getString(R.string.template_calendar_review)
                    .format(it.name ?: getString(R.string.events), it.account)
        }
    }

    private fun initViews() {
        binding.btnConfirmAndAdd.setOnClickListener {
            checkWriteCalendarPermission()
        }
        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun checkWriteCalendarPermission() {
        if(ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_CALENDAR) == PackageManager.PERMISSION_GRANTED) {
            viewModel.onAddClicked()
        } else {
            requestPermissions(arrayOf(Manifest.permission.WRITE_CALENDAR), REQUEST_WRITE_CALENDAR)
        }
    }
}