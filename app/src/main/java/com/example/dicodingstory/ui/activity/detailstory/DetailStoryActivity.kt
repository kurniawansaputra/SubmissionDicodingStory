package com.example.dicodingstory.ui.activity.detailstory

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.dicodingstory.R
import com.example.dicodingstory.databinding.ActivityDetailStoryBinding
import com.example.dicodingstory.data.remote.response.ListStoryItem
import kotlin.random.Random

class DetailStoryActivity : AppCompatActivity() {
    private lateinit var name: String
    private lateinit var desc: String
    private lateinit var imageUrl: String

    private lateinit var binding: ActivityDetailStoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailStoryBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        init()
    }

    private fun init() {
        setToolbar()
        playAnimation()
        setDetail()
    }

    private fun setToolbar() {
        binding.apply {
            toolbar.setNavigationOnClickListener {
                onBackPressedDispatcher.onBackPressed()
            }
        }
    }

    private fun setDetail() {
        val story = if (Build.VERSION.SDK_INT >= 33) {
            intent.getParcelableExtra(EXTRA_STORY, ListStoryItem::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(EXTRA_STORY)
        }

        if (story != null) {
            name = story.name.toString()
            desc = story.description.toString()
            imageUrl = story.photoUrl.toString()

            val randomNumber = Random.nextInt(120, 300)
            val randomImage = "https://picsum.photos/id/$randomNumber/128/128"

            binding.apply {
                Glide.with(this@DetailStoryActivity)
                    .load(randomImage)
                    .centerCrop()
                    .placeholder(R.color.colorPrimary500)
                    .into(ivProfile)

                Glide.with(this@DetailStoryActivity)
                    .load(imageUrl)
                    .centerCrop()
                    .placeholder(R.color.colorPrimary500)
                    .into(ivPhoto)

                textName.text = name
                textDesc.text = desc
            }
        }
    }

    private fun playAnimation() {
        val cardImageStory = ObjectAnimator.ofFloat(binding.cardPhoto, View.ALPHA, 1f).setDuration(500)
        val ivProfile = ObjectAnimator.ofFloat(binding.ivProfile, View.ALPHA, 1f).setDuration(500)
        val textName = ObjectAnimator.ofFloat(binding.textName, View.ALPHA, 1f).setDuration(500)
        val textDesc = ObjectAnimator.ofFloat(binding.textDesc, View.ALPHA, 1f).setDuration(500)

        val together = AnimatorSet().apply {
            playTogether(ivProfile, textName)
        }

        AnimatorSet().apply {
            playSequentially(together, textDesc, cardImageStory)
            start()
        }
    }

    companion object {
        const val EXTRA_STORY = "extra_story"
    }
}