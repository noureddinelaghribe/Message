package com.noureddine.kotlin2.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.noureddine.kotlin2.R
import com.noureddine.kotlin2.model.Message
import java.text.SimpleDateFormat
import java.util.Locale

class AdapterConversation(val idCurrentUser: String, val messages: List<Message>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_SENT = 1
        private const val VIEW_TYPE_RECEIVED = 2
    }

    private val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())


    override fun getItemViewType(position: Int): Int {
        val message = messages[position]
        return if (message.senderId != idCurrentUser) {
            VIEW_TYPE_SENT
        } else {
            VIEW_TYPE_RECEIVED
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return when (viewType) {
            VIEW_TYPE_SENT -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_message_sent, parent, false)
                SentMessageViewHolder(view)
            }
            else -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_message_received, parent, false)
                ReceivedMessageViewHolder(view)
            }
        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val message = messages[position]

        when (holder.itemViewType) {
            VIEW_TYPE_SENT -> (holder as SentMessageViewHolder).bind(message)
            VIEW_TYPE_RECEIVED -> (holder as ReceivedMessageViewHolder).bind(message)
        }

    }


    override fun getItemCount(): Int {
        return messages.size
    }




    inner class SentMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val messageText: TextView = itemView.findViewById(R.id.sent_message_text)
        private val timeText: TextView = itemView.findViewById(R.id.sent_message_time)
        private val readStatus: ImageView = itemView.findViewById(R.id.sent_message_status)

        fun bind(message: Message) {
            messageText.text = message.text

            message.timestamp?.let {
                timeText.text = dateFormat.format(it)
            }

            readStatus.visibility = if (message.read) View.VISIBLE else View.GONE
        }
    }

    inner class ReceivedMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val messageText: TextView = itemView.findViewById(R.id.received_message_text)
        private val timeText: TextView = itemView.findViewById(R.id.received_message_time)

        fun bind(message: Message) {
            messageText.text = message.text

            message.timestamp?.let {
                timeText.text = dateFormat.format(it)
            }
        }
    }

}