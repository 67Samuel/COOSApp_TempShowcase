package com.samuel.coosapp2.presentation.main.video.home.util

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.*
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade
import com.bumptech.glide.request.RequestOptions
import com.samuel.coosapp2.R
import com.samuel.coosapp2.business.domain.models.MyVideo
import com.samuel.coosapp2.databinding.VideoListItemBinding

class VideoListAdapter(
    private val interaction: Interaction? = null
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val requestOptions = RequestOptions
        .placeholderOf(R.drawable.black_logo)
        .error(R.drawable.black_logo)

    private val TAG: String = "ListAdapterDebug"

    val DIFF_CALLBACK = object : DiffUtil.ItemCallback<MyVideo>() {

        override fun areItemsTheSame(oldItem: MyVideo, newItem: MyVideo): Boolean {
            return oldItem.uri == newItem.uri
        }

        override fun areContentsTheSame(oldItem: MyVideo, newItem: MyVideo): Boolean {
            return oldItem == newItem
        }

    }
    private val differ =
        AsyncListDiffer(
            VideoRecyclerChangeCallback(this),
            AsyncDifferConfig.Builder(DIFF_CALLBACK).build()
        )


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return VideoViewHolder(
            VideoListItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            requestOptions = requestOptions,
            interaction = interaction,
        )
    }

    internal inner class VideoRecyclerChangeCallback(
        private val adapter: VideoListAdapter
    ) : ListUpdateCallback {

        override fun onChanged(position: Int, count: Int, payload: Any?) {
            adapter.notifyItemRangeChanged(position, count, payload)
        }

        override fun onInserted(position: Int, count: Int) {
            adapter.notifyItemRangeChanged(position, count)
        }

        override fun onMoved(fromPosition: Int, toPosition: Int) {
            adapter.notifyDataSetChanged()
        }

        override fun onRemoved(position: Int, count: Int) {
            adapter.notifyDataSetChanged()
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is VideoViewHolder -> {
                holder.bind(differ.currentList.get(position))
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    fun submitList(videoList: List<MyVideo>?, ){
        val newList = videoList?.toMutableList()
        differ.submitList(newList)
    }

    class VideoViewHolder
    constructor(
        private val binding: VideoListItemBinding,
        private val requestOptions: RequestOptions,
        private val interaction: Interaction?
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: MyVideo) {
            binding.root.setOnClickListener {
                interaction?.onItemSelected(adapterPosition, item)
            }

            Glide.with(binding.root)
                .setDefaultRequestOptions(requestOptions)
                .load(item.thumbnail)
                .transition(withCrossFade())
                .into(binding.videoThumbnail)
            binding.videoTitle.text = item.title
            binding.videoDescription.text = item.description
        }
    }

    interface Interaction {

        fun onItemSelected(position: Int, item: MyVideo)

    }
}
