package com.noureddine.kotlin2.Adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.google.android.material.imageview.ShapeableImageView
import com.noureddine.kotlin2.model.User
import com.noureddine.kotlin2.R
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.google.android.material.button.MaterialButton
import com.noureddine.kotlin2.model.FriendRequest
import com.noureddine.kotlin2.activity.ChatActivity
import com.noureddine.kotlin2.model.Conversation
import com.noureddine.kotlin2.model.User_db
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.collections.forEachIndexed
import kotlin.text.get

class AdapterMessanger(): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
//private val idCurrentUser: String, private val listMessages: List<User>, private val allConvs: List<Conversation>, val listFriend: MutableList<FriendRequest> , private val context: Context, private val onClickRequestFriendListener: OnClickRequestFriendListener
    companion object {
        private const val VIEW_TYPE_FRIEND = 1
        private const val VIEW_TYPE_REQUEST = 2
        private val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

        private lateinit var idCurrentUser: String
        private var listMessages: List<User> = mutableListOf()
        private var allConvs: List<Conversation> = mutableListOf()
        private var listFriend: MutableList<FriendRequest> = mutableListOf()
        private lateinit var context: Context
        private lateinit var onClickRequestFriendListener: OnClickRequestFriendListener
        private lateinit var onClickFriendListener: OnClickFriendListener

        private var listMessagesOffline: List<User_db> = mutableListOf()
        private lateinit var onClickOfflineFriend: OnClickOfflineFriendListener

    }

    interface OnClickRequestFriendListener {
        fun onAcceptClick(idUser: String)
        fun onDeclineClick(idUser: String)
    }

    interface OnClickFriendListener {
        fun onClickFriend(user: User, conversationId: String)
    }

    interface OnClickOfflineFriendListener {
        fun onClickOfflineFriend(user: User_db)
    }

    // Method to update the adapter data
    @SuppressLint("NotifyDataSetChanged")
    fun updateData( uidCurrentUser: String, ulistMessages: List<User>, uallConvs: List<Conversation>,
                    ulistFriend: MutableList<FriendRequest> , ucontext: Context, uonClickRequestFriendListener: OnClickRequestFriendListener,
                    uonClickFriendListener: OnClickFriendListener) {

        idCurrentUser = uidCurrentUser
        listMessages = ulistMessages
        allConvs = uallConvs
        listFriend = ulistFriend
        context = ucontext
        onClickRequestFriendListener = uonClickRequestFriendListener
        onClickFriendListener = uonClickFriendListener

        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun offlineMode(listMessages: List<User_db>, onClickOfflineFriendListener: OnClickOfflineFriendListener){
        listMessagesOffline = listMessages
        onClickOfflineFriend = onClickOfflineFriendListener
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun clearMessagesOffline(){
        listMessagesOffline  = emptyList()
        notifyDataSetChanged()
    }


    override fun getItemViewType(position: Int): Int {

        if (!listFriend.isEmpty()){
            val friendRequest = listFriend[position]

            return if (friendRequest?.status == true) {
                VIEW_TYPE_FRIEND
            } else {
                VIEW_TYPE_REQUEST
            }
        }else{
            return VIEW_TYPE_FRIEND
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return when (viewType) {
            VIEW_TYPE_FRIEND -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_friend, parent, false)
                FriendViewHolder(view)
            }
            else -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_requast_friend, parent, false)
                RequestFriendViewHolder(view)
            }
        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val positionUser = position;

        if (!listMessagesOffline.isEmpty()) {
            Log.d("TAG", "onBindViewHolder: Offline")
            (holder as FriendViewHolder).bind(listMessagesOffline[positionUser], onClickOfflineFriend)
        } else if (!listMessages.isEmpty()) {
            var user = listMessages[positionUser]
            when (holder.itemViewType) {
                VIEW_TYPE_FRIEND -> {
                    Log.d("TAG", "onBindViewHolder: Online")
                    (holder as FriendViewHolder).bind(idCurrentUser, user, allConvs, onClickFriendListener)
                }
                VIEW_TYPE_REQUEST -> {
                    (holder as RequestFriendViewHolder).bind(user, context, onClickRequestFriendListener)
                }
            }
        }

    }

    override fun getItemCount(): Int {
        if (listMessagesOffline.isEmpty()){
            return listFriend.size
        }else{
            return listMessagesOffline.size
        }
    }

    class FriendViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgProfile: ShapeableImageView = itemView.findViewById(R.id.profile_image)
        val status: View = itemView.findViewById(R.id.status_indicator)
        val tvName: TextView = itemView.findViewById(R.id.name_text)
        val tvLastMessage: TextView = itemView.findViewById(R.id.message_text)
        val tvLastTime: TextView = itemView.findViewById(R.id.time_text)
        val containerStatusMessage: LinearLayout = itemView.findViewById(R.id.message_status_container)
        val unreadCount: TextView = itemView.findViewById(R.id.unread_count)
        val unreadBb: MaterialCardView = itemView.findViewById(R.id.unread_badge)
        val readStatus: ImageView = itemView.findViewById(R.id.read_status)
        val linearFriend: ConstraintLayout = itemView.findViewById(R.id.linear_friend)

        fun bind( idCurrentUser: String, user: User, allConvs: List<Conversation>, onClickFriendListener: OnClickFriendListener) {
            if (user.img.toInt()==0){
                imgProfile.setImageResource(R.drawable.user1)
            }else{
                imgProfile.setImageResource(user.img.toInt())
            }

            tvName.setText(user.name)

            if (user.state){
                status.setBackgroundResource(R.drawable.status_indicator_online)
            }else{
                status.setBackgroundResource(R.drawable.status_indicator_offline)
            }

            var conv: Conversation? = null

            for (c in allConvs){
                if (c.participants.contains(idCurrentUser) == true && c.participants.contains(user.uid.toString()) == true){
                    conv=c
                }
            }

            if (conv==null){
                tvLastMessage.setText("New friend.")
            }else{
                tvLastMessage.setText(conv?.lastMessage.toString())
                tvLastTime.setText(dateFormat.format(conv?.lastMessageTimestamp))
            }

            linearFriend.setOnClickListener {
                onClickFriendListener.onClickFriend(user, conv?.conversationId.toString())
            }

            unreadCount.setText("2")
            unreadBb.visibility = View.GONE
            readStatus.visibility = View.GONE

        }

        fun bind(user: User_db, onClickOfflineFriendListener: OnClickOfflineFriendListener) {
            if (user.img.toInt()==0){
                imgProfile.setImageResource(R.drawable.user1)
            }else{
                imgProfile.setImageResource(user.img.toInt())
            }

            tvName.setText(user.name)


            if (user.lastMessageText.isEmpty()){
                tvLastMessage.setText("New friend.")
            }else{
                tvLastMessage.setText(user.lastMessageText)
                tvLastTime.setText(dateFormat.format(user.messageTimestamp))
            }

            linearFriend.setOnClickListener {
                onClickOfflineFriendListener.onClickOfflineFriend(user)
            }

            unreadCount.setText("2")
            unreadBb.visibility = View.GONE
            readStatus.visibility = View.GONE

        }

    }

    class RequestFriendViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val imgProfile: ShapeableImageView = itemView.findViewById(R.id.profile_image)
        val tvName: TextView = itemView.findViewById(R.id.name_text)
        val tvstatus: TextView = itemView.findViewById(R.id.status_text)
        val btnAccept: MaterialButton = itemView.findViewById(R.id.accept_button)
        val btnDecline: MaterialButton = itemView.findViewById(R.id.decline_button)


        fun bind(user: User, context: Context, onClickRequestFriendListener: OnClickRequestFriendListener) {

            tvstatus.visibility = View.GONE
            btnAccept.visibility = View.VISIBLE
            btnDecline.visibility = View.VISIBLE

            if (user.img.toInt()==0){
                imgProfile.setImageResource(R.drawable.user1)
            }else{
                imgProfile.setImageResource(user.img.toInt())
            }

            tvName.setText(user.name)

            btnAccept.setOnClickListener {
                tvstatus.visibility = View.VISIBLE
                btnAccept.visibility = View.GONE
                btnDecline.visibility = View.GONE
                tvstatus.text = "Request Accepted"
                tvstatus.setTextColor(ContextCompat.getColor(context, R.color.Green))
                onClickRequestFriendListener.onAcceptClick(user.uid)
            }

            btnDecline.setOnClickListener {
                tvstatus.visibility = View.VISIBLE
                btnAccept.visibility = View.GONE
                btnDecline.visibility = View.GONE
                tvstatus.text = "Request Declined"
                tvstatus.setTextColor(ContextCompat.getColor(context, R.color.Red))
                onClickRequestFriendListener.onDeclineClick(user.uid)
            }

        }

    }



}