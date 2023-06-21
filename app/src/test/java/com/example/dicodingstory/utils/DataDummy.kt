package com.example.dicodingstory.utils

import com.example.dicodingstory.data.remote.response.ListStoryItem
import com.example.dicodingstory.data.remote.response.StoriesResponse

object DataDummy {
    fun generateDummyStories() : StoriesResponse {
        val listStory = ArrayList<ListStoryItem>()
        for (i in 0..10 ) {
            val story = ListStoryItem(
                id = "story-$i",
                name = "Name $i",
                description = "Description $i",
                photoUrl = "https://picsum.photos/200/300.jpg",
                createdAt = "2022-01-08T06:34:18.598Z",
                lon = -10.212,
                lat = -16.002,
            )
            listStory.add(story)
        }
        return StoriesResponse(
            error = false,
            message = "Stories fetched successfully",
            listStory = listStory
        )
    }
}