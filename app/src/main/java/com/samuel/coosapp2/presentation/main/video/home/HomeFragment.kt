package com.samuel.coosapp2.presentation.main.video.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.RadioGroup
import android.widget.TextView
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.ferfalk.simplesearchview.SimpleSearchView
import com.google.android.material.tabs.TabLayoutMediator
import com.samuel.coosapp2.R
import com.samuel.coosapp2.business.datasource.cache.CacheUtil
import com.samuel.coosapp2.business.domain.util.*
import com.samuel.coosapp2.databinding.FragmentHomeBinding
import com.samuel.coosapp2.presentation.UICommunicationListener
import com.samuel.coosapp2.presentation.main.video.home.util.getFilterFromValue
import com.samuel.coosapp2.presentation.main.video.home.util.getOrderFromValue
import com.samuel.coosapp2.presentation.util.processQueue
import com.samuel.coosapp2.presentation.util.viewBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home) {
    private val TAG: String = "HomeFragmentDebug"

    private lateinit var uiCommunicationListener: UICommunicationListener
    private val viewModel: HomeViewModel by activityViewModels()
    private lateinit var menu: Menu

    private val binding by viewBinding(FragmentHomeBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity() as AppCompatActivity).apply {
            setSupportActionBar(binding.toolBar)
            supportActionBar?.title = "Church Of Our Saviour"
        }

        // cause search view to be closed on back press if it is open
        val callback = requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            if (!binding.searchView.onBackPressed()) {
                this.handleOnBackPressed()
            }
        }
        callback.isEnabled = true

        setHasOptionsMenu(true)
        setUpTabLayout()
        subscribeObservers()
    }

    private fun setUpTabLayout() {
        val viewPager = binding.pager
        val pagerAdapter = VideoListPagerAdapter()
        viewPager.adapter = pagerAdapter
        val tabLayout = binding.tabLayout
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            when (position) {
                0 -> {
                    tab.text = getString(R.string.sermons_tab_item)
                    tab.icon = ResourcesCompat.getDrawable(resources, R.drawable.sermon_list_icon, null)
                }
                1 -> {
                    tab.text = getString(R.string.saved_tab_item)
                    tab.icon = ResourcesCompat.getDrawable(resources, R.drawable.saved_sermons_icon, null)
                }
            }
        }.attach()
    }

    private fun initSearchView(item: MenuItem) {
        val searchView = requireActivity().findViewById<SimpleSearchView>(R.id.searchView)
        searchView.setMenuItem(item)

        if (viewModel.state.value!!.query.isNotEmpty()) {
            searchView.showSearch()
        }

        searchView.setOnQueryTextListener(
            object: SimpleSearchView.OnQueryTextListener {
                override fun onQueryTextChange(newText: String): Boolean {
                    return false
                }

                override fun onQueryTextCleared(): Boolean {
                    return false
                }

                override fun onQueryTextSubmit(query: String): Boolean {
                    Log.d(TAG, "SearchView: (button) executing search...: $query")
                    executeNewQuery(query)
                    return true
                }

            })
    }

    private fun subscribeObservers() {
        viewModel.state.observe(viewLifecycleOwner, { state ->
            uiCommunicationListener.displayProgressBar(state.isLoading)

            processQueue(
                context = context,
                queue = state.queue,
                stateMessageCallback = object : StateMessageCallback {
                    override fun removeMessageFromStack() {
                        viewModel.onTriggerHomeEvent(HomeEvents.OnRemoveHeadFromQueue)
                    }
                })

            state.uriForNavigation?.let { uri ->
                viewModel.onTriggerHomeEvent(HomeEvents.UpdateUriForNavigation(null))
                handleNavigation(uri)
            }
        })
    }

    private fun handleNavigation(uri: String) {
        try {
            val bundle = bundleOf("videoUri" to uri)
            findNavController().navigate(R.id.action_homeFragment_to_detailFragment, bundle)
        } catch (e: Exception) {
            e.printStackTrace()
            viewModel.onTriggerHomeEvent(
                HomeEvents.Error(
                    stateMessage = StateMessage(
                        response = Response(
                            message = e.message,
                            uiComponentType = UIComponentType.Dialog(),
                            messageType = MessageType.Error()
                        )
                    )
                ))
        }
    }

    private fun executeNewQuery(query: String){
        viewModel.onTriggerHomeEvent(HomeEvents.UpdateQuery(query))
        resetUI()
    }

    private  fun resetUI(){
        uiCommunicationListener.hideSoftKeyboard()
    }

    private inner class VideoListPagerAdapter : FragmentStateAdapter(this) {
        override fun getItemCount(): Int {
            return 2
        }

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> {
                    FullListFragment()
                }
                1 -> {
                    SavedListFragment()
                }
                else -> {
                    throw Exception("Invalid position: $position")
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        this.menu = menu
        inflater.inflate(R.menu.search_list_menu, this.menu)
        val item = menu.findItem(R.id.action_search)
        initSearchView(item)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_filter_settings -> {
                showFilterDialog()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showFilterDialog(){
        activity?.let {
            viewModel.state.value?.let { state ->
                val filter = state.filter.value
                val order = state.order.value

                val dialog = MaterialDialog(it)
                    .noAutoDismiss()
                    .customView(R.layout.video_filter_dialog)

                val view = dialog.getCustomView()

                // check the buttons based on state data
                view.findViewById<RadioGroup>(R.id.filter_group).apply {
                    when (filter) {
                        CacheUtil.VIDEO_FILTER_DATE_CREATED -> check(R.id.filter_date)
                        CacheUtil.VIDEO_FILTER_PREACHER -> check(R.id.filter_preacher)
                    }
                }
                view.findViewById<RadioGroup>(R.id.order_group).apply {
                    when (order) {
                        CacheUtil.VIDEO_ORDER_ASC -> check(R.id.filter_asc)
                        CacheUtil.VIDEO_ORDER_DESC -> check(R.id.filter_desc)
                    }
                }

                // set newFilter and newOrder based on the checked buttons when the positive button is pressed
                view.findViewById<TextView>(R.id.positive_button).setOnClickListener {
                    val newFilter =
                        when (view.findViewById<RadioGroup>(R.id.filter_group).checkedRadioButtonId) {
                            R.id.filter_preacher -> CacheUtil.VIDEO_FILTER_PREACHER
                            R.id.filter_date -> CacheUtil.VIDEO_FILTER_DATE_CREATED
                            else -> CacheUtil.VIDEO_FILTER_DATE_CREATED
                        }
                    val newOrder =
                        when (view.findViewById<RadioGroup>(R.id.order_group).checkedRadioButtonId) {
                            R.id.filter_desc -> "-"
                            else -> ""
                        }

                    // apply changes to state and update videos (in the new filter and order) from cache
                    viewModel.apply {
                        onTriggerHomeEvent(HomeEvents.UpdateFilter(getFilterFromValue(newFilter)))
                        onTriggerHomeEvent(HomeEvents.UpdateOrder(getOrderFromValue(newOrder)))
                    }

                    dialog.dismiss()
                }

                view.findViewById<TextView>(R.id.negative_button).setOnClickListener {
                    Log.d(TAG, "FilterDialog: cancelling filter.")
                    dialog.dismiss()
                }

                dialog.show()
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try{
            uiCommunicationListener = context as UICommunicationListener
        }catch(e: ClassCastException){
            Log.e(TAG, "$context must implement UICommunicationListener" )
        }
    }

    override fun onPause() {
        super.onPause()
        val searchView = requireActivity().findViewById<SimpleSearchView>(R.id.searchView)
        searchView.closeSearch()
    }

    override fun onStop() {
        super.onStop()
    }
}