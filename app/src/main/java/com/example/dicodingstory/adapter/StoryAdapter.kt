package com.example.dicodingstory.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.dicodingstory.R
import com.example.dicodingstory.databinding.ItemRowStoryBinding
import com.example.dicodingstory.data.remote.response.ListStoryItem
import com.example.dicodingstory.ui.activity.DetailStoryActivity
import com.example.dicodingstory.ui.activity.DetailStoryActivity.Companion.EXTRA_STORY
import kotlin.random.Random

class StoryAdapter(private var storyList: List<ListStoryItem>, private val context: Context): RecyclerView.Adapter<StoryAdapter.ViewHolder>() {
    class ViewHolder (val binding: ItemRowStoryBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemRowStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            with(storyList[position]) {
                val name = name
                val desc = description
                val imageUrl = photoUrl

                val randomNumber = Random.nextInt(120, 300)
                val randomImage = "https://picsum.photos/id/$randomNumber/128/128"

                binding.apply {
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
                        intent.putExtra(EXTRA_STORY, storyList[position])
                        context.startActivity(intent)
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int = storyList.size
}