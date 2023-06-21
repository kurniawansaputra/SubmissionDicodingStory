package com.example.dicodingstory.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.dicodingstory.R
import com.example.dicodingstory.databinding.ItemRowStoryBinding
import com.example.dicodingstory.data.remote.response.ListStoryItem
import com.example.dicodingstory.ui.activity.detailstory.DetailStoryActivity
import com.example.dicodingstory.ui.activity.detailstory.DetailStoryActivity.Companion.EXTRA_STORY
import kotlin.random.Random

class StoryAdapter(private val context: Context): PagingDataAdapter<ListStoryItem, StoryAdapter.ViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemRowStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val storyItem = getItem(position)

        if (storyItem != null) {
            holder.bind(storyItem)
        }
    }

    inner class ViewHolder(private val binding: ItemRowStoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(storyItem: ListStoryItem) {
            with(binding) {
                val name = storyItem.name
                val desc = storyItem.description
                val imageUrl = storyItem.photoUrl

                val randomNumber = Random.nextInt(120, 300)
                val randomImage = "https://picsum.photos/id/$randomNumber/128/128"

                textName.text = name
                textDesc.text = desc

                Glide.with(context)
                    .load(randomImage)
                    .centerCrop()
                    .placeholder(R.color.colorPrimary500)
                    .into(ivProfile)

                Glide.with(context)
                    .load(imageUrl)
                    .centerCrop()
                    .placeholder(R.color.colorPrimary500)
                    .into(ivPhoto)

                containerStory.setOnClickListener {
                    val intent = Intent(context, DetailStoryActivity::class.java)
                    intent.putExtra(EXTRA_STORY, storyItem)
                    context.startActivity(intent)
                }
            }
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListStoryItem>() {
            override fun areItemsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }
}