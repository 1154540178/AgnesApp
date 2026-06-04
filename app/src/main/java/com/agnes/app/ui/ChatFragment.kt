package com.agnes.app.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.agnes.app.R
import com.agnes.app.api.AgnesRepository
import com.agnes.app.databinding.FragmentChatBinding
import com.agnes.app.model.Message
import kotlinx.coroutines.*
import androidx.lifecycle.lifecycleScope

class ChatFragment : Fragment() {

    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!

    private val repository = AgnesRepository()
    private val adapter = ChatAdapter()
    private var job: Job? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvMessages.adapter = adapter
        binding.rvMessages.layoutManager = LinearLayoutManager(context).apply {
            stackFromEnd = true
        }

        binding.btnSend.setOnClickListener {
            val text = binding.etInput.text?.toString()?.trim()
            if (text.isNullOrBlank()) {
                Toast.makeText(context, R.string.error_empty, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            binding.etInput.text?.clear()
            sendMessage(text)
        }
    }

    private fun sendMessage(text: String) {
        adapter.addMessage(Message.User(text))
        binding.rvMessages.scrollToPosition(adapter.itemCount - 1)

        job = viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            try {
                val result = repository.sendMessage(text)
                withContext(Dispatchers.Main) {
                    if (result.isSuccess) {
                        adapter.addMessage(Message.AI(result.getOrNull() ?: "空回复"))
                    } else {
                        adapter.addMessage(Message.Error(result.exceptionOrNull()?.message ?: "请求失败"))
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    adapter.addMessage(Message.Error(e.message ?: "未知错误"))
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        job?.cancel()
        _binding = null
    }
}
