package com.agnes.app.ui

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.agnes.app.R
import com.agnes.app.api.ApiClient
import com.agnes.app.databinding.ActivityMainBinding
import com.agnes.app.util.Preferences

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val fragments = listOf<androidx.fragment.app.Fragment>(
        ChatFragment(),
        ImageFragment(),
        VideoFragment()
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Preferences.init(this)
        loadApiConfig()
        setSupportActionBar(binding.toolbar)

        // ViewPager 适配器
        binding.viewpager.adapter = object :
            androidx.viewpager2.adapter.FragmentStateAdapter(this) {
            override fun getItemCount(): Int = fragments.size
            override fun createFragment(position: Int): androidx.fragment.app.Fragment = fragments[position]
        }

        // ViewPager 页面切换监听
        binding.viewpager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                binding.bottomNav.selectedItemId = when (position) {
                    0 -> R.id.nav_chat
                    1 -> R.id.nav_image
                    2 -> R.id.nav_video
                    else -> R.id.nav_chat
                }
            }
        })

        // 底部导航
        binding.bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_chat -> { binding.viewpager.currentItem = 0; true }
                R.id.nav_image -> { binding.viewpager.currentItem = 1; true }
                R.id.nav_video -> { binding.viewpager.currentItem = 2; true }
                else -> false
            }
        }

        // 返回键处理
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (binding.viewpager.currentItem == 0) finish()
                else binding.viewpager.currentItem = 0
            }
        })
    }

    private fun loadApiConfig() {
        val apiKey = Preferences.getApiKey()
        val baseUrl = Preferences.getApiBaseUrl()
        if (!apiKey.isNullOrBlank()) {
            ApiClient.setApiKey(apiKey)
            ApiClient.setBaseUrl(baseUrl)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_settings -> {
                startActivity(Intent(this, SettingsActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
