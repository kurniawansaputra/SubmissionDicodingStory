package com.example.dicodingstory.ui.activity

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.dicodingstory.R
import com.example.dicodingstory.adapter.StoryAdapter
import com.example.dicodingstory.databinding.ActivityMainBinding
import com.example.dicodingstory.databinding.LayoutWarningBinding
import com.example.dicodingstory.hawkstorage.HawkStorage
import com.example.dicodingstory.model.ListStoryItem
import com.example.dicodingstory.util.hideLoading
import com.example.dicodingstory.util.showLoading
import com.example.dicodingstory.viewmodel.MainViewModel

class MainActivity : AppCompatActivity() {
    private lateinit var token: String
    private lateinit var mainViewModel: MainViewModel

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        init()
    }

    private fun init() {
        mainViewModel = ViewModelProvider(this)[MainViewModel::class.java]

        setObsListStories()
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
        mainViewModel.getStories(token)
    }

    private fun setObsListStories() {
        mainViewModel.isRefresh.observe(this) {
            setRefresh(it)
        }
        mainViewModel.isLoading.observe(this) {
            setLoading(it)
        }
        mainViewModel.onFailure.observe(this) {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        }
        mainViewModel.stories.observe(this) {
            val error = it.error
            if (error == false) {
                val storyAdapter = StoryAdapter(it.listStory as List<ListStoryItem>, this)
                binding.rvStories.adapter = storyAdapter
                binding.rvStories.setHasFixedSize(true)
            }
        }
    }

    private fun setRefresh(isRefresh: Boolean) {
        binding.swipeRefresh.isRefreshing = isRefresh
    }

    private fun swipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener {
            mainViewModel.getStories(token)
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