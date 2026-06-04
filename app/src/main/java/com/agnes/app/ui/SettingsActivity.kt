package com.agnes.app.ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.agnes.app.R
import com.agnes.app.api.AgnesRepository
import com.agnes.app.api.ApiClient
import com.agnes.app.databinding.ActivitySettingsBinding
import com.agnes.app.util.Preferences
import kotlinx.coroutines.*

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private val repository = AgnesRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.etApiKey.setText(Preferences.getApiKey() ?: "")
        setTitle(R.string.setting_title)

        binding.btnSave.setOnClickListener {
            val apiKey = binding.etApiKey.text?.toString()?.trim()
            if (apiKey.isNullOrBlank()) {
                binding.etApiKey.error = "请输入 API Key"
                return@setOnClickListener
            }
            Preferences.setApiKey(apiKey)
            ApiClient.setApiKey(apiKey)
            Toast.makeText(this, R.string.setting_saved, Toast.LENGTH_SHORT).show()
            finish()
        }

        binding.btnTest.setOnClickListener {
            val apiKey = binding.etApiKey.text?.toString()?.trim()
            if (apiKey.isNullOrBlank()) {
                binding.etApiKey.error = "请先输入 API Key"
                return@setOnClickListener
            }

            binding.tvStatus.visibility = android.view.View.VISIBLE
            binding.tvStatus.text = getString(R.string.status_connecting)
            binding.btnTest.isEnabled = false

            lifecycleScope.launch(Dispatchers.IO) {
                try {
                    ApiClient.setApiKey(apiKey)
                    val result = repository.testConnection()
                    withContext(Dispatchers.Main) {
                        if (result.isSuccess) {
                            binding.tvStatus.text = getString(R.string.status_connected)
                            binding.tvStatus.setTextColor(getColor(R.color.agnes_green))
                        } else {
                            binding.tvStatus.text = "${getString(R.string.status_failed)}: ${result.exceptionOrNull()?.message}"
                            binding.tvStatus.setTextColor(getColor(R.color.agnes_red))
                        }
                        binding.btnTest.isEnabled = true
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        binding.tvStatus.text = "${getString(R.string.status_failed)}: ${e.message}"
                        binding.tvStatus.setTextColor(getColor(R.color.agnes_red))
                        binding.btnTest.isEnabled = true
                    }
                }
            }
        }
    }
}
