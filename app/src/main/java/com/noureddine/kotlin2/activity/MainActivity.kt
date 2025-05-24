package com.noureddine.kotlin2.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import android.widget.TextView.OnEditorActionListener
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.search.SearchBar
import com.google.android.material.search.SearchView
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.noureddine.kotlin2.Adapter.AdapterMessanger
import com.noureddine.kotlin2.Adapter.AdapterMessanger.OnClickFriendListener
import com.noureddine.kotlin2.Adapter.AdapterMessanger.OnClickOfflineFriendListener
import com.noureddine.kotlin2.Adapter.AdapterMessanger.OnClickRequestFriendListener
import com.noureddine.kotlin2.Adapter.AdapterMessangerSearch
import com.noureddine.kotlin2.Database.AppDatabase
import com.noureddine.kotlin2.model.Participant_db
import com.noureddine.kotlin2.model.FriendRequest
import com.noureddine.kotlin2.model.Notification
import com.noureddine.kotlin2.model.User
import com.noureddine.kotlin2.R
import com.noureddine.kotlin2.ViewModel.MyViewModel
import com.noureddine.kotlin2.model.Conversation
import com.noureddine.kotlin2.model.Conversation_db
import com.noureddine.kotlin2.model.Message
import com.noureddine.kotlin2.model.Message_db
import com.noureddine.kotlin2.model.User_db
import com.noureddine.kotlin2.utel.AppConstants.DATABASE_NAME
import com.noureddine.kotlin2.utel.NotificationManagerMessage
import com.noureddine.kotlin2.utel.SharedPreferencesManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity(), AdapterMessangerSearch.ClickListener, OnClickRequestFriendListener, OnClickFriendListener, OnClickOfflineFriendListener{

    lateinit var recyclerView: RecyclerView
    lateinit var searchView: SearchView
    lateinit var searchBar: SearchBar
    lateinit var recyclerViewSearch: RecyclerView
    lateinit var adapterMessanger: AdapterMessanger
//    lateinit var adapterMessangerOfflineMode: AdapterMessangerOfflineMode
    lateinit var adapterMessangerSearch: AdapterMessangerSearch
    var listMessages: MutableList<User> = mutableListOf()
    var listMessagesSearch: MutableList<User> = mutableListOf()
    lateinit var database: DatabaseReference
    private var auth = Firebase.auth
    lateinit var imgProfile: ShapeableImageView
    lateinit var currentUser: User
    val allConvs = mutableListOf<Conversation>()
    var backPressedTime = 0L

    val TAG = "TAG-MainActivity"
    var listFriendSend: MutableList<FriendRequest> = mutableListOf()
    var listFriend: MutableList<FriendRequest> = mutableListOf()
    var listAllUsers: MutableList<User> = mutableListOf()

    lateinit var viewModel: MyViewModel
    lateinit var prefsManager: SharedPreferencesManager
    lateinit var textOfflineMode: TextView



    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        recyclerView = findViewById(R.id.recyclerView)
        searchView = findViewById<SearchView>(R.id.materialSearchView)
        searchBar = findViewById<SearchBar>(R.id.searchBar)
        recyclerViewSearch = findViewById<RecyclerView>(R.id.recyclerViewSearch)
        imgProfile = findViewById(R.id.profile)
        textOfflineMode = findViewById(R.id.OfflineMode)

        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this)

        recyclerViewSearch.setHasFixedSize(true)
        recyclerViewSearch.layoutManager = LinearLayoutManager(this)

        database = FirebaseDatabase.getInstance().reference

        adapterMessanger = AdapterMessanger()
        recyclerView.adapter = adapterMessanger

        viewModel = ViewModelProvider(this).get(MyViewModel::class.java)

        prefsManager = SharedPreferencesManager(this)


        listenToConnection()



    }



    fun loadCurrentUser(){
        database.child("users").child(auth.uid.toString()).get().addOnSuccessListener { dataSnapshot ->
            val user = dataSnapshot.getValue(User::class.java)
            if (user != null) {
                if (user.uid.toString().equals(auth.uid.toString())){
                    currentUser = user
                    prefsManager.saveUser(user)
                }
            }
            if (currentUser.img.toInt()==0){
                imgProfile.setImageResource(R.drawable.user1)
            }else{
                imgProfile.setImageResource(currentUser.img.toLong().toInt())
            }

            loadCurrentUidFriends()
            loadAllUsersForSearch()

        }
    }


    fun loadCurrentUidFriends(){
        database.child("friends").addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                listFriend.clear()
                for (child in snapshot.children) {
                    val friendRequest = child.getValue(FriendRequest::class.java)
                    if(friendRequest != null){
                        if( ( (friendRequest.receiverId.equals(currentUser.uid) || (friendRequest.senderId.equals(currentUser.uid))) && ((friendRequest.status == true)) )
                            || ( friendRequest.receiverId.equals(currentUser.uid) && friendRequest.status == false ) ){
                            listFriend.add(friendRequest)
                        }else if( (friendRequest.receiverId.equals(currentUser.uid) || (friendRequest.senderId.equals(currentUser.uid))) && ((friendRequest.status == false)) ){
                            listFriendSend.add(friendRequest)
                        }
                    }
                }

                setupSearchBar()
                loadFriends()
                loadDataCoversation()

            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }


    fun loadFriends(){
        database.child("users").addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                listMessages.clear()
                for (userSnapshot in snapshot.children){
                    val user = userSnapshot.getValue(User::class.java)
                    if (user != null) {
                        if (!user.uid.toString().equals(auth.uid.toString())){
                            if (listFriend.any { it.receiverId == user.uid || it.senderId == user.uid}){
                                listMessages.add(user)
                                viewModel.insertUser(user)
                            }
                        }
                    }
                }

                adapterMessanger.updateData( currentUser.uid.toString(), listMessages, allConvs, listFriend, this@MainActivity, this@MainActivity, this@MainActivity)

            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }


    fun loadDataCoversation(){
        database.child("conversations").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                allConvs.clear()
                for (child in snapshot.children) {
                    val conv = child.getValue(Conversation::class.java)
                    if (conv?.participants?.contains(currentUser.uid.toString()) == true){
                        conv?.let {
                            allConvs.add(it)

                            val other =  it.participants.filter { it != currentUser.uid }.get(0)

                            lifecycleScope.launch {
                                val userId = getUserIdByUid(other).toLong()
                                var conversationId = getConversationIdByUid(it.conversationId).toLong()
                                insertParticipant(userId,conversationId)
                                insertMessages(userId, conversationId, it.messages)
                            }

                            if(!it.notify){
                                //val other = it.participants.first { it != currentUser.uid.toString() }
                                val sortedMessages: MutableList<Message> = it.messages.values.toMutableList()
                                sortedMessages.sortByDescending { it.timestamp }
                                val lastEntry: Message? = sortedMessages.firstOrNull()
                                val isReceiver = lastEntry?.senderId != other
                                if (isReceiver){
                                    database.child("users").child(other).get().addOnSuccessListener { dataSnapshot ->
                                        val user = dataSnapshot.getValue(User::class.java)
                                        if (user != null) {
                                            val notificationManagerMessage = NotificationManagerMessage(user.notificationId, this@MainActivity)
                                            notificationManagerMessage.createCustomNotification( this@MainActivity, Notification( it, user))
                                            database.child("conversations").child(it .conversationId).child("notify").setValue(true)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                allConvs.sortByDescending { it.lastMessageTimestamp }
                // استخراج قائمة معرفات المستخدمين بترتيب ظهورهم في الرسائل
                val orderedUserIds = allConvs.map { it.participants.filter { it != currentUser.uid }.get(0) }.distinct() // لإزالة التكرار
                // إنشاء خريطة تربط كل معرف بترتيبه
                val userIdToOrder = mutableMapOf<String, Int>()
                orderedUserIds.forEachIndexed { index, userId ->
                    userIdToOrder[userId] = index
                }
                // ترتيب المستخدمين حسب ترتيب ظهورهم في الرسائل
                val sortedUsers = listMessages.sortedBy { user ->
                    // إذا لم يكن المعرف موجودًا في الرسائل، ضعه في النهاية
                    userIdToOrder[user.uid] ?: Int.MAX_VALUE
                }
                adapterMessanger.updateData( currentUser.uid.toString(), sortedUsers, allConvs, listFriend, this@MainActivity, this@MainActivity, this@MainActivity)
            }

            override fun onCancelled(error: DatabaseError) {}
        })

    }


    fun loadAllUsersForSearch(){
        database.child("users").get().addOnSuccessListener { dataSnapshot ->
            listAllUsers.clear()
            for (userSnapshot in dataSnapshot.children){
                val user = userSnapshot.getValue(User::class.java)
                if (user != null) {
                    if (!user.uid.toString().equals(auth.uid.toString())){
                        listAllUsers.add(user)
                    }
                }
            }
        }
    }


    fun offlineMode(){

        loadCurrentUserOffline()
        loadDataOffline()

    }

    fun loadCurrentUserOffline(){
        currentUser = prefsManager.getUser()
        if (currentUser.img.toInt()==0){
            imgProfile.setImageResource(R.drawable.user1)
        }else{
            imgProfile.setImageResource(currentUser.img.toLong().toInt())
        }
    }


    fun loadDataOffline(){

        viewModel.allUsers.observe(this@MainActivity) { users ->
            val sortedUsers = users
            Log.e(TAG, "loadDataOffline: "+sortedUsers.size)
            for (u in sortedUsers){
                Log.e(TAG, "loadDataOffline: "+u.name+" "+u.lastMessageText)
            }
//            adapterMessangerOfflineMode = AdapterMessangerOfflineMode(users,this)
//            recyclerView.adapter = adapterMessangerOfflineMode

            adapterMessanger.offlineMode(sortedUsers,this@MainActivity)

        }



    }

    fun clearDB(){
        viewModel.clearUsers()
        viewModel.clearConversations()
        viewModel.clearParticipants()
        viewModel.clearMessages()
    }


    override fun onBackPressed() {
        if (backPressedTime + 2000 > System.currentTimeMillis()) {
            super.onBackPressed()
        } else {
            Toast.makeText(this, "Press again to exit.", Toast.LENGTH_SHORT).show()
        }
        backPressedTime = System.currentTimeMillis()
    }


    private fun performSearch(query: String) {
        listMessagesSearch.clear()
        if (query.isEmpty()) {
            listMessagesSearch.addAll(listAllUsers)
        } else {
            listMessagesSearch.addAll(listAllUsers.filter {
                it.name.contains(query, ignoreCase = true)
            })
        }
        adapterMessangerSearch = AdapterMessangerSearch( listMessagesSearch, listFriend, listFriendSend, this, this, this)
        recyclerViewSearch.adapter = adapterMessangerSearch
    }


    private fun setupSearchBar() {
        // Set placeholder text
        searchBar.setHint("Search...")

        // Connect searchBar with searchView
        searchBar.setOnClickListener(View.OnClickListener { v: View? ->
            searchView.show()
        })

        // Handle search query submission
        searchView.getEditText()
            .setOnEditorActionListener(OnEditorActionListener { textView: TextView?, actionId: Int, event: KeyEvent? ->
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    searchView.hide()
                    true
                }
                false
            })


        // Assuming you have a SearchView instance called 'searchView'
        val searchEditText = searchView.getEditText() // This method may vary based on your SearchView implementation

        if (searchEditText != null) {
            searchEditText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                    // Called before the text is changed.
                }

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    // Called as the text is being changed.
                    Log.d("SearchView", "Text is: " + s)
                    val query = s.toString()
                    performSearch(query)
                    // For example, you might trigger a search/filter operation here.
                }

                override fun afterTextChanged(s: Editable?) {
                    // Called after the text has been changed.
                    // You can perform operations based on the final text here.
                }
            })
        } else {
            Log.e("SearchView", "Could not retrieve the inner EditText from the SearchView.")
        }


        // Listen for query text changes
        searchView.addTransitionListener(SearchView.TransitionListener { searchView: SearchView?, previousState: SearchView.TransitionState?, newState: SearchView.TransitionState? ->
            if (newState == SearchView.TransitionState.HIDDEN) {
                // Do something when search view is hidden
                //Toast.makeText(this, "newState == SearchView.TransitionState.HIDDEN", Toast.LENGTH_SHORT).show();
            } else {
                //Toast.makeText(this, "newState == SHOW", Toast.LENGTH_SHORT).show();
            }
        })
    }


    override fun onClickAddFriend(idUser: String) {
        addFriend(idUser)
    }


//    fun loadListFriend(uid : String){
//        database.child("friends").child(uid).addValueEventListener(object : ValueEventListener{
//            override fun onDataChange(snapshot: DataSnapshot) {
//                for (child in snapshot.children) {
//                    val friendRequest = child.getValue(FriendRequest::class.java)
//                    if(friendRequest != null){
//                        val fk = friendRequest.receiverId
//                        val fv = friendRequest.status
//                        listFriend.add(friendRequest)
//                        Log.d(TAG, "onDataChangeFriends: "+fk+" "+fv)
//                    }
//                }
//            }
//
//            override fun onCancelled(error: DatabaseError) {}
//
//        })
//    }


    fun addFriend(idUser: String){
        var requestId: String = database.child("friends").push().key.toString()
        database.child("friends").child(requestId).setValue(FriendRequest( requestId, currentUser.uid, idUser,false))
    }

    fun confirmFriend(idUser: String){
        listFriend.forEach { println(it.senderId+" "+it.receiverId) }
        var friendRequest = listFriend.find { it.receiverId == currentUser.uid && it.senderId == idUser}
        if (friendRequest != null){
            database.child("friends").child(friendRequest.requestId).child("status").setValue(true)
            Log.d(TAG, "confirmFriend: status true")
        }else{
            Log.d(TAG, "confirmFriend: null")
        }
        //database.child("friends").child(idUser).child(currentUser.uid).setValue(true.toString())
    }

    fun deleteFriend(idUser: String){
        var idRequest = listFriend.find { it.receiverId == currentUser.uid && it.senderId == idUser}?.requestId
        Log.d(TAG, "confirmFriend: idRequest "+idRequest)
        if (idRequest != null){
            database.child("friends").child(idRequest).removeValue()
        }
    //database.child("friends").child(currentUser.uid).child(idUser).removeValue()
    //database.child("friends").child(idUser).child(currentUser.uid).removeValue()
    }


    fun listenToConnection(): Boolean{
        // الحصول على مسار حالة الاتصال
        val connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected")  // مسار خاص بحالة الاتصال :contentReference[oaicite:0]{index=0}

        val connected = false

        // إضافة مستمع لحظة بلحظة
        connectedRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // استخراج القيمة المنطقية؛ إذا لم تكن موجودة نعتبرها false :contentReference[oaicite:1]{index=1}
                val connected = snapshot.getValue(Boolean::class.java) ?: true
                if (connected) {
                    Log.d("Presence", "متصل بخادم Firebase")
                    //onlineMode()
                    setStateUser(true)
                    clearDB()
                    loadCurrentUser()
                    adapterMessanger.clearMessagesOffline()
                    textOfflineMode.visibility = View.GONE
                } else {
                    Log.d("Presence", "غير متصل بخادم Firebase")
                    setStateUser(false)
                    offlineMode()
                    textOfflineMode.visibility = View.VISIBLE
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // طباعة الخطأ عند الإلغاء أو الفشل :contentReference[oaicite:4]{index=4}
                Log.w("Presence", "فشل الاستماع لحالة الاتصال", error.toException())
            }
        })

        return connected
    }


    fun setStateUser(state: Boolean){
        database.child("users").child(auth.uid.toString()).child("state").setValue(state)
            .addOnSuccessListener {
                Log.d(TAG, "Data written successfully")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error writing data", e)
            }
    }


    override fun onDestroy() {
        super.onDestroy()
        Log.d("Presence", "غير متصل بخادم Firebase")  // حالة الاتصال false :contentReference[oaicite:3]{index=3}
        setStateUser(false)
    }

    override fun onAcceptClick(idUser: String) {
        confirmFriend(idUser)
    }

    override fun onDeclineClick(idUser: String) {
        deleteFriend(idUser)
    }


    override fun onClickOfflineFriend(user: User_db) {

        Log.d(TAG, "onClickOfflineFriend")

        Log.d(TAG, "onClickFriend: "+user.name)

        Toast.makeText(this,"Offline Mode", Toast.LENGTH_SHORT).show()

//        var  i = Intent(this, ChatActivity::class.java)
//            .putExtra("userDb",user)
//            .putExtra("idUserSender",currentUser.uid)
//        startActivity(i)

    }


    override fun onClickFriend(user: User, conversationId: String) {

        Log.d(TAG, "onClickFriend")

        Log.d(TAG, "onClickFriend: "+user.name)
        Log.d(TAG, "onClickFriend: "+currentUser.uid)
        Log.d(TAG, "onClickFriend: "+conversationId)

        Toast.makeText(this,"Online Mode", Toast.LENGTH_SHORT).show()

//        var  i = Intent(this, ChatActivity::class.java)
//            .putExtra("user",user)
//            .putExtra("idUserSender",currentUser.uid)
//            .putExtra("conversationId",conversationId)
//        startActivity(i)

    }


    suspend fun getUserIdByUid(uid: String): String {
        return viewModel.getUserIdByUid(uid).toString()
    }

    suspend fun getConversationIdByUid(uid: String): String {
        var conversationId = viewModel.getConversationIdByUid(uid)
        Log.d(TAG, "getConversationIdByUid conversationId 1 : "+conversationId)
        if (conversationId == null) {
            Log.d(TAG, "getConversationIdByUid conversationId 2 : null "+uid)
            val v = viewModel.insertConversation(Conversation_db(uid))
            Log.d(TAG, "getConversationIdByUid conversationId  : "+v)
            conversationId = viewModel.getConversationIdByUid(uid)
            Log.d(TAG, "getConversationIdByUid conversationId 3 : "+conversationId)
        }
        Log.d(TAG, "getConversationIdByUid conversationId 4 : "+conversationId)
        return conversationId.toString()
    }

    fun insertParticipant(userId: Long, conversationId: Long) {
        viewModel.insertParticipant(Participant_db(conversationId, userId))
    }

    suspend fun insertMessages(userId: Long, conversationId: Long, messages: Map<String, Message>) {
        for ((_,message) in messages) {
            viewModel.insertMessage(Message_db(conversationId, userId, message.text, message.timestamp, message.read, if (message.senderId == currentUser.uid) true else false))
        }
    }





}

