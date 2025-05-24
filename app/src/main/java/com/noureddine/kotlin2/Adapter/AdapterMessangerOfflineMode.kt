//package com.noureddine.kotlin2.Adapter
//
//import android.content.Context
//import android.view.LayoutInflater
//import android.view.ViewGroup
//import androidx.recyclerview.widget.RecyclerView
//import com.noureddine.kotlin2.Adapter.AdapterMessanger.FriendViewHolder
//import com.noureddine.kotlin2.Adapter.AdapterMessanger.OnClickFriendListener
//import com.noureddine.kotlin2.Adapter.AdapterMessanger.OnClickOfflineFriendListener
//import com.noureddine.kotlin2.R
//import com.noureddine.kotlin2.model.Conversation
//import com.noureddine.kotlin2.model.User
//import com.noureddine.kotlin2.model.User_db
//
//class AdapterMessangerOfflineMode(private val listMessages: List<User_db>, private val onClickOfflineFriendListener: OnClickOfflineFriendListener): RecyclerView.Adapter<FriendViewHolder>(){
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendViewHolder {
//        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_friend, parent, false)
//        return FriendViewHolder(view)
//    }
//
//    override fun onBindViewHolder(holder: FriendViewHolder, position: Int) {
//        val user = listMessages[position]
//        //user: User, newFriend: Boolean, onClickFriendListener: OnClickFriendListener
//        (holder as FriendViewHolder).bind(user, onClickOfflineFriendListener)
//    }
//
//    override fun getItemCount(): Int {
//        return listMessages.size
//    }
//
//
//}






















