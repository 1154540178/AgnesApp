package com.agnes.app.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.agnes.app.R
import com.agnes.app.databinding.ItemLoadingBinding
import com.agnes.app.databinding.ItemMessageAiBinding
import com.agnes.app.databinding.ItemMessageUserBinding
import com.agnes.app.model.Message

class ChatAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val messages = mutableListOf<Message>()

    companion object {
        const val TYPE_USER = 0
        const val TYPE_AI = 1
        const val TYPE_ERROR = 2
    }

    fun addMessage(message: Message) {
        messages.add(message)
        notifyItemInserted(messages.size - 1)
    }

    fun clear() {
        messages.clear()
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return when (messages[position]) {
            is Message.User -> TYPE_USER
            is Message.Error -> TYPE_ERROR
            else -> TYPE_AI
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            TYPE_USER -> {
                val binding = ItemMessageUserBinding.inflate(inflater, parent, false)
                UserViewHolder(binding)
            }
            TYPE_ERROR -> {
                val binding = ItemMessageAiBinding.inflate(inflater, parent, false)
                ErrorViewHolder(binding)
            }
            else -> {
                val binding = ItemMessageAiBinding.inflate(inflater, parent, false)
                AIViewHolder(binding)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messages[position]
        when (holder) {
            is UserViewHolder -> holder.bind(message as Message.User)
            is AIViewHolder -> holder.bind(message as Message.AI)
            is ErrorViewHolder -> holder.bind(message as Message.Error)
        }
    }

    override fun getItemCount() = messages.size

    class UserViewHolder(private val binding: ItemMessageUserBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(message: Message.User) {
            binding.tvMessageUser.text = message.content
        }
    }

    class AIViewHolder(private val binding: ItemMessageAiBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(message: Message.AI) {
            binding.tvMessageAi.text = message.content
        }
    }

    class ErrorViewHolder(private val binding: ItemMessageAiBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(message: Message.Error) {
            binding.tvMessageAi.setTextColor(
                ContextCompat.getColor(binding.root.context, R.color.agnes_red)
            )
            binding.tvMessageAi.text = "${message.message}"
        }
    }
}
