package com.noureddine.kotlin2.utel

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.RemoteViews
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.noureddine.kotlin2.Adapter.AdapterMessanger.OnClickFriendListener
import com.noureddine.kotlin2.model.User
import com.noureddine.kotlin2.model.Conversation
import com.noureddine.kotlin2.model.Notification
import com.noureddine.kotlin2.R
import java.text.SimpleDateFormat
import java.util.Locale

class NotificationManagerMessage(private val notificationId: Int, private val myOnClickFriendListener: OnClickFriendListener) {

    companion object {
        private const val CHANNEL_ID = "my_channel_id"
        const val ACTION_MESSAGE_CLICK = "com.noureddine.kotlin2.ACTION_MESSAGE_CLICK"
        const val ACTION_SHOW_REPLY_LAYOUT = "com.noureddine.kotlin2.ACTION_SHOW_REPLY_LAYOUT"
        lateinit var notificationLayout: RemoteViews
        lateinit var mainNotification: Notification
        lateinit var onClickFriendListener: OnClickFriendListener
        var NOTIFICATION_ID: Int = 0
    }

    init {
        // قم بتهيئة المتغير الثابت في دالة المنشئ
        onClickFriendListener = myOnClickFriendListener
        Companion.NOTIFICATION_ID = notificationId
    }

    private val NOTIFICATION_ID = 4554 //notificationId

    @SuppressLint("RemoteViewLayout")
    fun createCustomNotification(context: Context, notification: Notification) {
        // Store the notification for potential later use
        mainNotification = notification

        // 1. Create notification channel
        createNotificationChannel(context)

        // 2. Create pending intents for different click actions
        val messageIntent = createActionIntent(context, ACTION_MESSAGE_CLICK)
        val showReplyIntent = createActionIntent(context, ACTION_SHOW_REPLY_LAYOUT)

        // 3. Create custom notification layout
        notificationLayout = RemoteViews(context.packageName, R.layout.notification_message).apply {
            // Set text
            setTextViewText(R.id.senderName, notification.user.name)

            // Format timestamp
            val formattedTime = SimpleDateFormat("HH:mm", Locale.getDefault())
                .format(notification.conversation.lastMessageTimestamp)
            setTextViewText(R.id.time, formattedTime)

            setTextViewText(R.id.messageText, notification.conversation.lastMessage)
            //setTextViewText(R.id.Reply, "REPLY")

            // Set image
            //setImageViewResource(R.id.senderAvatar, setImageViewResource(notification.img.toInt()))
            setImageViewResource(R.id.senderAvatar, notification.user.img.toInt())

            // Add click listeners
            setOnClickPendingIntent(R.id.linearlayout_message, messageIntent)
            setOnClickPendingIntent(R.id.Reply, showReplyIntent)

        }

        // 4. Create main content intent
//        val mainIntent = Intent(context, ChatActivity::class.java).apply {
//            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//            onClickFriendListener.onClickFriend(notification.user, notification.conversationId)
//        }
//
//        val mainPendingIntent: PendingIntent = PendingIntent.getActivity(
//            context, 0, mainIntent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
//        )

        // 5. Build the notification
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.user1)
            .setCustomContentView(notificationLayout!!)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
//            .setContentIntent(mainPendingIntent)
            .setAutoCancel(true)


        // 6. Show the notification
        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, builder.build())

    }


    private fun createNotificationChannel(context: Context) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is not in the support library
        val name = "My Notification Channel"
        val descriptionText = "Channel for message notifications"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
            description = descriptionText
        }

        // Register the channel with the system
        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    private fun createActionIntent(context: Context, action: String): PendingIntent {
        val intent = Intent(context, NotificationClickReceiver::class.java).apply {
            this.action = action
            putExtra("mainNotification", mainNotification)
        }
        return PendingIntent.getBroadcast(
            context, action.hashCode(), intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

//    class NotificationClickReceiver : BroadcastReceiver() {
//        override fun onReceive(context: Context, intent: Intent) {
//            when (intent.action) {
//                ACTION_MESSAGE_CLICK -> {
//                    Toast.makeText(context, "تم النقر على الرسالة!", Toast.LENGTH_LONG).show()
//                }
//                ACTION_SHOW_REPLY_LAYOUT -> {
//                    Toast.makeText(context, "تم النقر على الرد!", Toast.LENGTH_LONG).show()
//                    onClickFriendListener.onClickFriend(mainNotification.user!!, mainNotification.conversationId!!)
//                }
//            }
//        }
//    }

}