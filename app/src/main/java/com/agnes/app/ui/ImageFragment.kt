package com.agnes.app.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.agnes.app.R
import com.agnes.app.api.AgnesRepository
import com.agnes.app.databinding.FragmentImageBinding
import com.agnes.app.ui.ImageAdapter.ImageItem
import kotlinx.coroutines.*

class ImageFragment : Fragment() {

    private var _binding: FragmentImageBinding? = null
    private val binding get() = _binding!!

    private val repository = AgnesRepository()
    private val imageAdapter = ImageAdapter { /* 点击预览可后续扩展 */ }
    private var currentJob: Job? = null

    private val sizes = arrayOf("512x512", "1024x1024", "1024x768", "768x1024", "1024x576", "576x1024")
    private val counts = arrayOf("1", "2", "4")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentImageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sizeAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, sizes)
        binding.spinnerSize.adapter = sizeAdapter
        binding.spinnerSize.setSelection(1)

        val countAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, counts)
        binding.spinnerCount.adapter = countAdapter
        binding.spinnerCount.setSelection(0)

        binding.rvImages.layoutManager = GridLayoutManager(context, 2)
        binding.rvImages.adapter = imageAdapter

        binding.btnGenerate.setOnClickListener {
            val prompt = binding.etPrompt.text?.toString()?.trim()
            if (prompt.isNullOrBlank()) {
                binding.etPrompt.error = getString(R.string.error_empty)
                return@setOnClickListener
            }
            val size = sizes[binding.spinnerSize.selectedItemPosition]
            val count = counts[binding.spinnerCount.selectedItemPosition].toInt()
            generateImage(prompt, size, count)
        }
    }

    private fun generateImage(prompt: String, size: String, count: Int) {
        showLoading(true)
        imageAdapter.clear()

        currentJob = viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            try {
                val result = repository.generateImage(prompt, size, count)
                withContext(Dispatchers.Main) {
                    showLoading(false)
                    if (result.isSuccess) {
                        result.getOrNull()?.forEach { img ->
                            imageAdapter.addImage(ImageItem(img.imageUrl, img.prompt))
                        }
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
