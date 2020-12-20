package os.dtakac.feritraspored.calendar.eventpicker.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import org.koin.android.viewmodel.ext.android.viewModel
import os.dtakac.feritraspored.calendar.eventpicker.viewmodel.EventPickerViewModel
import os.dtakac.feritraspored.databinding.FragmentEventPickerBinding

class EventPickerFragment : Fragment() {
    private var _binding: FragmentEventPickerBinding? = null
    private val binding get() = _binding!!

    private val viewModel: EventPickerViewModel by viewModel()
    private val args: EventPickerFragmentArgs by navArgs()

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEventPickerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        initObservers()
        viewModel.getEvents(args.scheduleUrl)
    }

    private fun initViews() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
        binding.loader.hide()
    }

    private fun initObservers() {

    }
}