package com.noureddine.kotlin2.Adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.imageview.ShapeableImageView
import com.noureddine.kotlin2.Adapter.AdapterMessanger.OnClickRequestFriendListener
import com.noureddine.kotlin2.model.FriendRequest
import com.noureddine.kotlin2.model.User
import com.noureddine.kotlin2.R
import kotlin.system.measureTimeMillis

class AdapterMessangerSearch(private val listMessages: List<User>, private val listFriend: MutableList<FriendRequest>, private val listFriendSend: MutableList<FriendRequest>, private val myContext: Context,
                             private val listenerAddFriend: ClickListener, private val onClickRequestFriendListener: OnClickRequestFriendListener): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_FRIEND = 1
        private const val VIEW_TYPE_REQUEST = 2
    }

    override fun getItemViewType(position: Int): Int {

        val user = listMessages[position]
        val friendRequest = listFriend.find { user.uid == it.receiverId || user.uid == it.senderId }
        return if ( (friendRequest != null) && (friendRequest.status == false) ){
            VIEW_TYPE_REQUEST
        } else {
            VIEW_TYPE_FRIEND
        }

//        return if (friendRequest?.status == true) {
//            VIEW_TYPE_FRIEND
//        } else {
//            VIEW_TYPE_REQUEST
//        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return when (viewType) {
            VIEW_TYPE_FRIEND -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_user, parent, false)
                UserViewHolder(view)
            }
            else -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_requast_friend, parent, false)
                RequestFriendViewHolder(view)
            }
        }


//        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_user,parent,false)
//        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val user = listMessages[position]

        when (holder.itemViewType) {
            VIEW_TYPE_FRIEND -> {
                (holder as UserViewHolder).bind( user, listFriendSend, listFriend, listenerAddFriend)
            }
            VIEW_TYPE_REQUEST -> {
                (holder as RequestFriendViewHolder).bind( user, myContext, onClickRequestFriendListener)
            }
        }


    }


    override fun getItemCount(): Int {
        return listMessages.size
    }


    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgProfile: ShapeableImageView = itemView.findViewById(R.id.profile_image)
        val tvName: TextView = itemView.findViewById(R.id.name_text)
        val btnAddFriend: MaterialButton = itemView.findViewById(R.id.add_friend_button)
        val tvSendRequest: TextView = itemView.findViewById(R.id.request_status_text)

        fun bind( user: User, listFriendSend: MutableList<FriendRequest>, listFriend: MutableList<FriendRequest>, listenerAddFriend: ClickListener) {
            if (user.img.toInt()==0){
                imgProfile.setImageResource(R.drawable.user1)
            }else{
                imgProfile.setImageResource(user.img.toInt())
            }

            tvName.setText(user.name)


            //listFriend.forEach { print(it.requestId) }
            val friendReceived = listFriendSend.find { it.receiverId == user.uid || it.senderId == user.uid}
            //println(friendReceived.requestId)
            //val friend = listFriend.find { it.receiverId == user.uid && it.senderId == user.uid}

            if (friendReceived != null){
                //wating accepte request from friend
                Log.d("TAG", "bind: wating accepte request from friend "+user.name)
                tvSendRequest.visibility = View.VISIBLE
                btnAddFriend.visibility = View.GONE
            }else{
                //others
                if (listFriend.any { it.receiverId == user.uid || it.senderId == user.uid}){
                    //friend
                    Log.d("TAG", "bind: friend "+user.name)
                    tvSendRequest.visibility = View.GONE
                    btnAddFriend.visibility = View.GONE
                }else{
                    Log.d("TAG", "bind: other "+user.name)
                    tvSendRequest.visibility = View.GONE
                    btnAddFriend.visibility = View.VISIBLE
                }

            }

            btnAddFriend.setOnClickListener {
                tvSendRequest.visibility = View.VISIBLE
                btnAddFriend.visibility = View.GONE
                listenerAddFriend.onClickAddFriend(user.uid)
            }
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













    interface ClickListener {
        fun onClickAddFriend(idUser: String)
    }


}