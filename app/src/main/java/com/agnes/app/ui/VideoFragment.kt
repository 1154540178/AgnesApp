package com.agnes.app.ui

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.agnes.app.R
import com.agnes.app.api.AgnesRepository
import com.agnes.app.ui.VideoAdapter.VideoItem
import kotlinx.coroutines.*

class VideoFragment : Fragment() {

    private var _binding: FragmentVideoBinding? = null
    private val binding get() = _binding!!

    private val repository = AgnesRepository()
    private val videoAdapter = VideoAdapter { /* 点击播放可后续扩展 */ }
    private var currentJob: Job? = null

    private val durations = arrayOf("5秒", "10秒", "15秒", "30秒")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVideoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val durationAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, durations)
        binding.spinnerDuration.adapter = durationAdapter
        binding.spinnerDuration.setSelection(0)

        binding.rvVideos.layoutManager = LinearLayoutManager(context)
        binding.rvVideos.adapter = videoAdapter

        binding.btnGenerate.setOnClickListener {
            val prompt = binding.etPrompt.text?.toString()?.trim()
            if (prompt.isNullOrBlank()) {
                binding.etPrompt.error = getString(R.string.error_empty)
                return@setOnClickListener
            }
            generateVideo(prompt)
        }
    }

    private fun generateVideo(prompt: String) {
        showLoading(true)

        currentJob = viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            try {
                val taskIdResult = repository.generateVideo(prompt)
                if (taskIdResult.isFailure) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, taskIdResult.exceptionOrNull()?.message ?: "创建失败", Toast.LENGTH_LONG).show()
                        showLoading(false)
                    }
                    return@launch
                }
                val taskId = taskIdResult.getOrNull() ?: return@launch

                withContext(Dispatchers.Main) {
                    binding.tvStatus.text = "正在生成视频..."
                }

                val result = repository.pollVideoTask(taskId)
                withContext(Dispatchers.Main) {
                    showLoading(false)
                    if (result.isSuccess) {
                        val videoUrl = result.getOrNull() ?: ""
                        videoAdapter.addVideo(VideoItem(prompt, videoUrl))
                        Toast.makeText(context, "视频生成完成!", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, result.exceptionOrNull()?.message ?: "生成失败", Toast.LENGTH_LONG).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    showLoading(false)
                    Toast.makeText(context, "错误: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun showLoading(show: Boolean) {
        binding.progressBar.visibility = if (show) View.VISIBLE else View.GONE
        binding.tvStatus.visibility = if (show) View.VISIBLE else View.GONE
        binding.btnGenerate.isEnabled = !show
        binding.etPrompt.isEnabled = !show
    }

    override fun onDestroyView() {
        super.onDestroyView()
        currentJob?.cancel()
        _binding = null
    }
}
