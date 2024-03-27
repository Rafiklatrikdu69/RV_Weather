package com.bouchenna.rv_weather.service

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat

class MyFirebaseMessagingService : FirebaseMessagingService()  {

    private val PERMISSION_REQUEST_CODE = 123


    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        val t: String? = message.notification?.title
        Log.d("messagerie", "message : $t")

        if(message.data.size>0){
            val title = message.data["title"]
            val body = message.data["body"]
            showNotification(applicationContext, title, body)
        }
        else {
            val title = message.notification!!.title
            val body = message.notification!!.body
            showNotification(applicationContext, title, body)
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("token", "new token")

    }




    fun showNotification(context: Context, title: String?, body: String?) {
        val channelId = "my_channel_id"
        val notificationId = 1

        Log.d("messagerie", "showMessagerie : $title")


        // Création de la notification
        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info) // Icône de la notification
            .setContentTitle(title) // Titre de la notification
            .setContentText(body) // Texte de la notification
            .setPriority(NotificationCompat.PRIORITY_DEFAULT) // Priorité de la notification

        val notificationManager = NotificationManagerCompat.from(context)

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.d("perm ", "perm DEnied")
            return
        }
        else{
            Log.d("perm ", "perm good")
            notificationManager.notify(notificationId, builder.build())
        }

    }

}