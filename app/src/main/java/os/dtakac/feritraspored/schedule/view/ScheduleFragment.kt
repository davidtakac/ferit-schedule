package os.dtakac.feritraspored.schedule.view

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import org.koin.android.viewmodel.ext.android.viewModel
import os.dtakac.feritraspored.R
import os.dtakac.feritraspored.common.extensions.*
import os.dtakac.feritraspored.common.view.debounce.onDebouncedClick
import os.dtakac.feritraspored.databinding.FragmentScheduleBinding
import os.dtakac.feritraspored.schedule.viewmodel.ScheduleViewModel
import kotlin.math.absoluteValue
import kotlin.math.roundToInt
import kotlin.math.roundToLong

class ScheduleFragment : Fragment() {
    private var _binding: FragmentScheduleBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ScheduleViewModel by viewModel()
    private var scrollAnimator: ObjectAnimator? = null
    private val scrollSpeed by lazy { resources.displayMetrics.toPixels(dp = 2.2f) }
    private val scrollInterpolator by lazy { DecelerateInterpolator(2.5f) }

    private val customTabs by lazy {
        val colorParams = CustomTabColorSchemeParams.Builder()
                .setToolbarColor(requireContext().getColorCompat(R.color.colorStatusBar))
                .build()
        CustomTabsIntent.Builder()
                .setDefaultColorSchemeParams(colorParams)
                .build()
    }

    private val scheduleWebViewClient = object : WebViewClient() {
        override fun onPageFinished(view: WebView?, url: String?) {
            viewModel.onPageDrawn()
        }

        override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
            viewModel.onUrlClicked(url)
            return true
        }
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        _binding = FragmentScheduleBinding.inflate(inflater, container, false)
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
        viewModel.onViewCreated()
    }

    override fun onResume() {
        super.onResume()
        viewModel.onResume()
    }

    private fun initObservers() {
        viewModel.scheduleData.observe(viewLifecycleOwner) {
            binding.wvSchedule.loadDataWithBaseURL(
                    it.baseUrl,
                    if (resources.configuration.isNightMode()) it.dataDark else it.data,
                    it.mimeType,
                    it.encoding,
                    null
            )
            binding.toolbar.title = it.title ?: getString(R.string.label_schedule)
        }
        viewModel.javascript.observe(viewLifecycleOwner) { data ->
            binding.wvSchedule.evaluateJavascript(data.js) {
                data.callback.invoke(it)
            }
        }
        viewModel.webViewScroll.observe(viewLifecycleOwner) {
            scrollWebView(it)
        }
        viewModel.clearWebViewScroll.observe(viewLifecycleOwner) {
            scrollAnimator?.cancel()
        }
        viewModel.openInExternalBrowser.observe(viewLifecycleOwner) {
            startActivity(Intent(Intent.ACTION_VIEW, it))
        }
        viewModel.openInCustomTabs.observe(viewLifecycleOwner) {
            customTabs.launchUrl(requireContext(), it)
        }
        viewModel.showChangelog.observe(viewLifecycleOwner) {
            childFragmentManager.showChangelog()
        }
        viewModel.snackBarMessage.observe(viewLifecycleOwner) {
            Snackbar.make(binding.root, it, Snackbar.LENGTH_SHORT)
                    .setAnchorView(binding.navBar.root)
                    .show()
        }
        viewModel.isLoaderVisible.observe(viewLifecycleOwner) { shouldShow ->
            binding.loader.apply { if (shouldShow) show() else hide() }
        }
        viewModel.errorMessage.observe(viewLifecycleOwner) {
            binding.error.tvError.text = it?.buildString(resources)
        }
        viewModel.isErrorGone.observe(viewLifecycleOwner) {
            binding.error.root.isGone = it
        }
        viewModel.areControlsEnabled.observe(viewLifecycleOwner) {
            binding.navBar.apply {
                btnPrevious.isEnabled = it
                btnCurrent.isEnabled = it
                btnNext.isEnabled = it
            }
            binding.toolbar.menu.findItem(R.id.item_menu_refresh).isEnabled = it
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initViews() {
        binding.wvSchedule.apply {
            setBackgroundColor(context.getColorCompat(android.R.color.transparent))
            webViewClient = scheduleWebViewClient
            settings.javaScriptEnabled = true
        }
        binding.toolbar.apply {
            inflateMenu(R.menu.schedule_menu)
            menu.findItem(R.id.item_menu_refresh).onDebouncedClick {
                viewModel.onRefreshClicked()
            }
            menu.findItem(R.id.item_menu_settings).onDebouncedClick {
                findNavController().navigate(R.id.actionSettings)
            }
            menu.findItem(R.id.item_menu_browser).onDebouncedClick {
                viewModel.onOpenInExternalBrowserClicked()
            }
        }
        binding.navBar.apply {
            btnPrevious.onDebouncedClick {
                viewModel.onPreviousWeekClicked()
            }
            btnCurrent.onDebouncedClick {
                viewModel.onCurrentWeekClicked()
            }
            btnNext.onDebouncedClick {
                viewModel.onNextWeekClicked()
            }
        }
        binding.error.btnBugReport.setOnClickListener {
            context?.openEmailEditor(
                    subject = getString(R.string.subject_bug_report),
                    content = getString(R.string.template_bug_report)
                            .format(binding.error.tvError.text.toString())
            )
        }
        binding.loader.hide()
        binding.error.tvError.movementMethod = ScrollingMovementMethod()
    }

    private fun scrollWebView(elementPositionDp: Float) {
        if (scrollAnimator?.isStarted != true) {
            val elementPositionPx = resources.displayMetrics.toPixels(elementPositionDp).roundToInt()
            val currentVerticalPosition = binding.wvSchedule.scrollY
            val anim = ObjectAnimator.ofInt(
                    binding.wvSchedule,
                    "scrollY",
                    currentVerticalPosition,
                    elementPositionPx
            )
            val distance = (currentVerticalPosition - elementPositionPx).absoluteValue
            anim.duration = (distance / scrollSpeed).roundToLong()
            anim.interpolator = scrollInterpolator
            scrollAnimator = anim
            scrollAnimator?.start()
        }
    }
}