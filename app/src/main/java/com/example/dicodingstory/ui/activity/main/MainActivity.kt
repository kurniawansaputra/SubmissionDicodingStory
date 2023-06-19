package com.example.dicodingstory.ui.activity.main

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v4.os.IResultReceiver._Parcel
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.dicodingstory.R
import com.example.dicodingstory.adapter.StoryAdapter
import com.example.dicodingstory.data.remote.Result
import com.example.dicodingstory.data.remote.response.ListStoryItem
import com.example.dicodingstory.databinding.ActivityMainBinding
import com.example.dicodingstory.databinding.LayoutWarningBinding
import com.example.dicodingstory.hawkstorage.HawkStorage
import com.example.dicodingstory.ui.activity.addstory.AddStoryActivity
import com.example.dicodingstory.ui.activity.login.LoginActivity
import com.example.dicodingstory.ui.activity.maps.MapsActivity
import com.example.dicodingstory.utils.hideLoading
import com.example.dicodingstory.utils.showLoading

class MainActivity : AppCompatActivity() {
    private lateinit var token: String

    private val factory: MainViewModelFactory = MainViewModelFactory.getInstance()
    private val mainViewModel: MainViewModel by viewModels {
        factory
    }

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        init()
    }

    private fun init() {
        setPref()
        setToolbar()
        swipeRefresh()
        setListStory()
        setListener()
    }

    private fun setPref() {
        val user = HawkStorage.instance(this).getUser()
        token = user.loginResult?.token.toString()
    }

    private fun setToolbar() {
        setSupportActionBar(binding.toolbar)
        title = null
    }

    private fun setListener() {
        binding.apply {
            fabAddStory.setOnClickListener {
                val intent = Intent(this@MainActivity, AddStoryActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun setListStory() {
        mainViewModel.getStories(token).observe(this) {
            if (it != null) {
                when (it) {
                    is Result.Loading -> {
                        setLoading(true)
                    }
                    is Result.Success -> {
                        setLoading(false)
                        setRefresh(false)
                        val error = it.data.error
                        if (error == false) {
                            val storyAdapter = StoryAdapter(it.data.listStory as List<ListStoryItem>, this)
                            binding.rvStories.adapter = storyAdapter
                            binding.rvStories.setHasFixedSize(true)
                        }
                    }
                    is Result.Error -> {
                        setLoading(false)
                        setRefresh(false)
                        Toast.makeText(this, it.error, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun setRefresh(isRefresh: Boolean) {
        binding.swipeRefresh.isRefreshing = isRefresh
    }

    private fun swipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener {
            setListStory()
        }
    }

    private fun setLoading(isLoading: Boolean) {
        if (isLoading) {
            showLoading(this)
        } else {
            hideLoading()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.option_menu_main, menu)
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menuMaps -> {
                goToMaps()
                true
            }
            R.id.menuLogout -> {
                dialogLogOut()
                true
            }
            else -> true
        }
    }

    private fun dialogLogOut() {
        val binding: LayoutWarningBinding = LayoutWarningBinding.inflate(layoutInflater)
        val builder: AlertDialog.Builder = AlertDialog.Builder(layoutInflater.context)
        builder.setView(binding.root)
        val dialog: AlertDialog = builder.create()
        binding.apply {
            textTitle.text = getString(R.string.log_out)
            textMessage.text = getString(R.string.are_you_sure_you_want_to_log_out_the_app)
            textNo.text = getString(R.string.no)
            textYes.text = getString(R.string.log_out)
            textNo.setOnClickListener {
                dialog.dismiss()
            }
            textYes.setOnClickListener {
                logout()
                dialog.dismiss()
            }
        }
        dialog.setCancelable(true)
        dialog.show()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    private fun goToMaps() {
        val intent = Intent(this, MapsActivity::class.java)
        startActivity(intent)
    }

    private fun logout() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
        HawkStorage.instance(this).deleteAll()
    }
}