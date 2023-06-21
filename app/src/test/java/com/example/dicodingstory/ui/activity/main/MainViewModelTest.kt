package com.example.dicodingstory.ui.activity.main

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.recyclerview.widget.ListUpdateCallback
import com.example.dicodingstory.adapter.StoryAdapter
import com.example.dicodingstory.data.remote.response.ListStoryItem
import com.example.dicodingstory.repository.MainRepository
import com.example.dicodingstory.utils.DataDummy
import com.example.dicodingstory.utils.MainDispatcherRule
import com.example.dicodingstory.utils.getOrAwaitValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class MainViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @OptIn(ExperimentalCoroutinesApi::class)
    @get:Rule
    val mainDispatcherRules = MainDispatcherRule()

    @Mock
    private lateinit var mainRepository: MainRepository
    private lateinit var mainViewModel: MainViewModel
    private val dummyStories = DataDummy.generateDummyStories()

    @Before
    fun setUp() {
        mainViewModel = MainViewModel(mainRepository)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `when Get Stories Should Not Null and Return Success`() = runTest {
        val data: PagingData<ListStoryItem> = StoryPagingSource.snapshot(dummyStories.listStory)
        val expectedStories = MutableLiveData<PagingData<ListStoryItem>>()
        expectedStories.value = data
        `when`(mainRepository.getStoriesPaging("Bearer dummyToken")).thenReturn(expectedStories)
        val actualStories: PagingData<ListStoryItem> = mainViewModel.getStoriesPaging("Bearer dummyToken").getOrAwaitValue()
        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main,
        )
        differ.submitData(actualStories)
        assertNotNull(differ.snapshot())
        assertEquals(dummyStories.listStory, differ.snapshot())
        assertEquals(dummyStories.listStory.size, differ.snapshot().size)
        assertEquals(dummyStories.listStory[0], differ.snapshot()[0])
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `when Get Stories is Null and Return Error`() = runTest {
        val data : PagingData<ListStoryItem> = PagingData.from(emptyList())
        val expectedStory = MutableLiveData<PagingData<ListStoryItem>>()
        expectedStory.value = data
        `when`(mainRepository.getStoriesPaging("Bearer dummyToken")).thenReturn(expectedStory)
        val actualStories : PagingData<ListStoryItem> = mainViewModel.getStoriesPaging("Bearer dummyToken").getOrAwaitValue()
        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main
        )
        differ.submitData(actualStories)
        assertEquals(0, differ.snapshot().size)
    }

    class StoryPagingSource : PagingSource<Int, LiveData<List<ListStoryItem>>>() {
        companion object {
            fun snapshot(items: List<ListStoryItem>): PagingData<ListStoryItem> {
                return PagingData.from(items)
            }
        }
        override fun getRefreshKey(state: PagingState<Int, LiveData<List<ListStoryItem>>>): Int {
            return 0
        }
        override suspend fun load(params: LoadParams<Int>): LoadResult<Int, LiveData<List<ListStoryItem>>> {
            return LoadResult.Page(emptyList(), 0, 1)
        }
    }

    private val noopListUpdateCallback = object : ListUpdateCallback {
        override fun onInserted(position: Int, count: Int) {}
        override fun onRemoved(position: Int, count: Int) {}
        override fun onMoved(fromPosition: Int, toPosition: Int) {}
        override fun onChanged(position: Int, count: Int, payload: Any?) {}
    }

}