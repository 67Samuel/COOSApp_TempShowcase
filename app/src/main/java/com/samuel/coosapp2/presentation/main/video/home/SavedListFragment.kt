package com.samuel.coosapp2.presentation.main.video.home

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.ferfalk.simplesearchview.SimpleSearchView
import com.samuel.coosapp2.R
import com.samuel.coosapp2.business.domain.models.MyVideo
import com.samuel.coosapp2.business.domain.util.Constants
import com.samuel.coosapp2.databinding.FragmentSavedListBinding
import com.samuel.coosapp2.presentation.UICommunicationListener
import com.samuel.coosapp2.presentation.main.video.home.util.ChildFragments.*
import com.samuel.coosapp2.presentation.main.video.home.util.VideoFilterOptions
import com.samuel.coosapp2.presentation.main.video.home.util.VideoListAdapter
import com.samuel.coosapp2.presentation.main.video.home.util.VideoOrderOptions
import com.samuel.coosapp2.presentation.util.TopSpacingItemDecoration
import com.samuel.coosapp2.presentation.util.viewBinding

class SavedListFragment : Fragment(R.layout.fragment_saved_list),
    VideoListAdapter.Interaction,
    SwipeRefreshLayout.OnRefreshListener 
{
    private val TAG: String = "SavedListFragmentDebug"

    lateinit var uiCommunicationListener: UICommunicationListener

    private var recyclerAdapter: VideoListAdapter? = null // can leak memory so need to null
    private val viewModel: HomeViewModel by activityViewModels()

    private val binding by viewBinding(FragmentSavedListBinding::bind)

    // initial values that will be initialised when fragment starts
    private var videoListInitialized = false // for getting videos from the network only once
    private lateinit var query: String
    private lateinit var filterOption: VideoFilterOptions
    private lateinit var orderOption: VideoOrderOptions

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.swipeRefresh.setOnRefreshListener(this)
        binding.swipeRefresh.setDistanceToTriggerSync(Constants.SWIPE_REFRESH_DISTANCE_TO_TRIGGER_SYNC_VIDEO_LIST)
        initRecyclerView()
        initRecyclerViewValues()
        subscribeObservers()
        initSavedListSearchFab()
        viewModel.onTriggerHomeEvent(HomeEvents.GetVideoFromCache(SAVED_LIST))
    }

    private fun initRecyclerViewValues() {
        query = viewModel.state.value?.query ?: ""
        filterOption = viewModel.state.value?.filter ?: VideoFilterOptions.DATE_CREATED
        orderOption = viewModel.state.value?.order ?: VideoOrderOptions.ASC
    }

    private fun initSavedListSearchFab() {
        binding.savedListSearchFab.setOnClickListener {
            binding.savedListSearchFab.hide()
            binding.savedListSearchFab.visibility = GONE
            val searchView = requireActivity().findViewById<SimpleSearchView>(R.id.searchView)
            searchView.closeSearch()
            searchView.showTabLayout()
            viewModel.onTriggerHomeEvent(HomeEvents.UpdateQuery(""))
        }
    }

    private fun initRecyclerView() {
        binding.videoRecyclerview.apply {
            layoutManager = LinearLayoutManager(this@SavedListFragment.context)
            val topSpacingDecorator = TopSpacingItemDecoration(30)
            removeItemDecoration(topSpacingDecorator) // does nothing if not applied already
            addItemDecoration(topSpacingDecorator)

            recyclerAdapter = VideoListAdapter(this@SavedListFragment)
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)

                    // fab extend logic
                    if (newState == RecyclerView.SCROLL_STATE_IDLE
                        && !binding.savedListSearchFab.isExtended
                        && recyclerView.computeVerticalScrollOffset() == 0
                    ) {
                        binding.savedListSearchFab.extend()
                    }

                    // load more pages logic
                    if (!recyclerView.canScrollVertically(1) // if the recyclerview can't scroll down
                        && newState == RecyclerView.SCROLL_STATE_IDLE // and the recyclerview is currently not scrolling)
                    ) {
                        val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                        val lastPosition = layoutManager.findLastVisibleItemPosition()
                        Log.d(TAG,
                            "onScrollStateChanged: exhausted? ${viewModel.state.value?.savedListIsQueryExhausted}")
                        if (
                            lastPosition == recyclerAdapter?.itemCount?.minus(1) // if you can see the last view
                            && viewModel.state.value?.isLoading == false // and loading is false
                            && viewModel.state.value?.savedListIsQueryExhausted == false // and query is exhausted
                        ) {
                            Log.d(TAG, "onScrollStateChanged: attempting to load next page...")
                            viewModel.onTriggerHomeEvent(HomeEvents.NextPage(SAVED_LIST))
                        }
                    }
                }

                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    // fab shrink logic
                    if (dy != 0 && binding.savedListSearchFab.isExtended) {
                        binding.savedListSearchFab.shrink()
                    }
                    super.onScrolled(recyclerView, dx, dy)
                }
            })
            adapter = recyclerAdapter
        }
    }

    private fun subscribeObservers() {
        viewModel.state.observe(viewLifecycleOwner, { state ->
            Log.d(TAG, "subscribeObservers: $state")
            handleChangedRecyclerViewValues(state.query, state.filter, state.order)

            handleNoVideosFound(state.savedVideoList.isEmpty())

            recyclerAdapter?.apply {
                submitList(videoList = state.savedVideoList)
            }
        })
    }

    private fun handleNoVideosFound(videoListIsEmpty: Boolean) {
        if (videoListIsEmpty) {
            binding.videoRecyclerview.visibility = GONE
            binding.noSermonsFoundImage.visibility = VISIBLE
        } else {
            binding.videoRecyclerview.visibility = VISIBLE
            binding.noSermonsFoundImage.visibility = GONE
        }
    }

    private fun handleChangedRecyclerViewValues(newQuery: String, newFilterOption: VideoFilterOptions, newOrderOption: VideoOrderOptions) {
        if (query.isNotEmpty()) {
            binding.savedListSearchFab.visibility = VISIBLE
        } else {
            binding.savedListSearchFab.visibility = GONE
        }
        if (query != newQuery || filterOption != newFilterOption || orderOption != newOrderOption) {
            query = newQuery
            filterOption = newFilterOption
            orderOption = newOrderOption
            viewModel.onTriggerHomeEvent(HomeEvents.GetVideoFromCache(SAVED_LIST))
        } else if (!videoListInitialized && viewModel.state.value!!.savedVideoList.isEmpty()) {
            videoListInitialized = true
            viewModel.onTriggerHomeEvent(HomeEvents.GetVideoFromNetwork(SAVED_LIST))
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            uiCommunicationListener = context as UICommunicationListener
        } catch (e: ClassCastException) {
            Log.e(TAG, "$context must implement UICommunicationListener")
        }
    }

    override fun onItemSelected(position: Int, item: MyVideo) {
        viewModel.onTriggerHomeEvent(HomeEvents.UpdateUriForNavigation(item.uri))
    }

    override fun onRefresh() {
        viewModel.onTriggerHomeEvent(HomeEvents.GetVideoFromNetwork(SAVED_LIST))
        binding.swipeRefresh.isRefreshing = false
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onStop() {
        super.onStop()
    }
}