package com.noureddine.kotlin2.utel

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.Toast
import com.noureddine.kotlin2.model.Notification
import com.noureddine.kotlin2.utel.NotificationManagerMessage.Companion.onClickFriendListener

class NotificationClickReceiver : BroadcastReceiver() {
    @SuppressLint("ServiceCast")
    override fun onReceive(context: Context, intent: Intent) {
        val notification = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("mainNotification", Notification::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra("mainNotification")
        } ?: return

        when (intent.action) {
            NotificationManagerMessage.ACTION_MESSAGE_CLICK -> {
                Toast.makeText(context, "تم النقر على الرسالة!", Toast.LENGTH_LONG).show()
            }
            NotificationManagerMessage.ACTION_SHOW_REPLY_LAYOUT -> {
                Toast.makeText(context, "تم النقر على الرد!", Toast.LENGTH_LONG).show()
                onClickFriendListener.onClickFriend(notification.user, notification.conversation.conversationId)
            }
        }

    }
}
