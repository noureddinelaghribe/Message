package com.noureddine.kotlin2.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.imageview.ShapeableImageView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.noureddine.kotlin2.Adapter.AdapterConversation
import com.noureddine.kotlin2.Adapter.AdapterMessanger
import com.noureddine.kotlin2.model.User
import com.noureddine.kotlin2.R
import com.noureddine.kotlin2.activity.MainActivity
import com.noureddine.kotlin2.model.Conversation
import com.noureddine.kotlin2.model.Message
import com.noureddine.kotlin2.utel.NotificationManagerMessage

class ChatActivity : AppCompatActivity() {

    lateinit var imgProfile: ShapeableImageView
    lateinit var tvname: TextView
    lateinit var tvstatus: TextView
    lateinit var btnCallAudio: ImageButton
    lateinit var btnCallVideo: ImageButton
    lateinit var btnBack: MaterialToolbar
    lateinit var recyclerView: RecyclerView
    lateinit var btnSendImage: ImageButton
    lateinit var etMessage: EditText
    lateinit var btnSend: ImageButton
    lateinit var extrasIntent : Intent
    lateinit var currentUser: User
    lateinit var idUserSender: String
    var conversationId: String = ""
    lateinit var database: DatabaseReference
    var listMessages: MutableList<Message> = mutableListOf()
    lateinit var adapterConversation: AdapterConversation



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_chat)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        imgProfile = findViewById(R.id.chat_avatar)
        tvname = findViewById(R.id.chat_name)
        tvstatus = findViewById(R.id.chat_status)
        btnCallAudio = findViewById(R.id.chat_call)
        btnCallVideo = findViewById(R.id.chat_video)
        btnBack = findViewById(R.id.chat_toolbar)
        recyclerView = findViewById(R.id.messages_recycler_view)
        btnSendImage = findViewById(R.id.btn_attach)
        etMessage = findViewById(R.id.input_message)
        btnSend = findViewById(R.id.btn_send)

        database = FirebaseDatabase.getInstance().reference

        extrasIntent = intent

        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Safe way to handle Parcelable extra
        val user = extrasIntent.getParcelableExtra<User>("user")
        if (user != null) {
            currentUser = user
        } else {
            Log.d("TAG", "onCreate: null user")
            // Handle null case (finish activity, show error, etc.)
            finish()
            return
        }

//        if (extrasIntent.getParcelableExtra("user") ?: User != null){
//            currentUser = (extrasIntent.getParcelableExtra("user") ?: User) as User
//        }else{
//            Log.d("TAG", "onCreate: null user")
//        }

        if (extrasIntent.getStringExtra("idUserSender") != null){
            idUserSender = extrasIntent.getStringExtra("idUserSender").toString()
        }else{
            Log.d("TAG", "onCreate: null idUserSender")
        }

        if (extrasIntent.getStringExtra("conversationId") != null){
            conversationId = extrasIntent.getStringExtra("conversationId").toString()
        }else{
            Log.d("TAG", "onCreate: null conversationId")
        }

        if (currentUser.img.toInt()==0){
            imgProfile.setImageResource(R.drawable.user1)
        }else{
            imgProfile.setImageResource(currentUser.img.toInt())
        }

        tvname.setText(currentUser.name)

        if (currentUser.state){
            tvstatus.setText("Online")
            tvstatus.setTextColor(ContextCompat.getColor(this, R.color.Green))
        }else{
            tvstatus.setText("Offline")
            tvstatus.setTextColor(ContextCompat.getColor(this, R.color.black))
        }



        if (!conversationId.isEmpty()){

            database.child("conversations").child(conversationId).child("messages").addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    listMessages.clear()
                    for (child in snapshot.children) {
                        val msg = child.getValue(Message::class.java)
                        msg?.let { listMessages.add(it) }

                        if (msg?.senderId.equals(idUserSender)){
                            database.child("conversations").child(conversationId).child("messages").child(child.key.toString()).child("read").setValue(true)
                        }

                        //Log.e("ChatActivity","Msg "+ child.key)

                    }

                    adapterConversation = AdapterConversation(idUserSender,listMessages)
                    recyclerView.adapter = adapterConversation

                    if (adapterConversation != null && adapterConversation.itemCount > 0) {
                        recyclerView.smoothScrollToPosition(adapterConversation.itemCount - 1)
                    }

                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("ChatActivity", "Could not read conversations", error.toException())
                }
            })

        }





        btnBack.setOnClickListener {
            var  i = Intent(this, MainActivity::class.java)
            startActivity(i)
        }


        btnSend.setOnClickListener {

            val msg = etMessage.text.toString().trim()
            if (!msg.isEmpty()){

                if (conversationId.isEmpty()){
                    conversationId = database.child("conversations").push().key.toString()
                    //val conversationId: String = database.child("conversations").push().key.toString()
                    database.child("conversations").child(conversationId).setValue(Conversation(
                        conversationId,
                        listOf( currentUser.uid, idUserSender),
                        msg,
                        System.currentTimeMillis(),
                    )
                    ).addOnSuccessListener {
                        var messageId: String = database.child("conversations").child(conversationId).child("messages").push().key.toString()
                        database.child("conversations").child(conversationId).child("messages").child(messageId).setValue(Message(
                            messageId,
                            currentUser.uid.toString(),//replace this to idUserSender
                            msg,
                            System.currentTimeMillis(),
                            false
                        ))
                    }
                }else{
                    var messageId: String = database.child("conversations").child(conversationId).child("messages").push().key.toString()
                    database.child("conversations").child(conversationId).child("lastMessage").setValue(msg)
                    database.child("conversations").child(conversationId).child("lastMessageTimestamp").setValue(System.currentTimeMillis())
                    database.child("conversations").child(conversationId).child("notify").setValue(false)
                    database.child("conversations").child(conversationId).child("messages").child(messageId).setValue(Message(
                        messageId,
                        currentUser.uid.toString(),
                        msg,
                        System.currentTimeMillis(),
                        false
                    ))
                }
            }

            etMessage.setText("")

        }







    }
}