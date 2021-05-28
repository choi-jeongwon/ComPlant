package com.example.complant

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startActivity
import com.example.complant.navigation.HomeFragment
import java.util.*

class Receiver : BroadcastReceiver() {
    var name : String? = null
    override fun onReceive(context: Context?, intent: Intent?) {
        // We use this to make sure that we execute code, only when this exact
        // // Alarm triggered our Broadcast receiver

        //if (intent?.action == "MyBroadcastReceiverAction") {



            name = intent?.getStringExtra("a")
        Log.d("Receiver", name + Date().toString())

        //var intent2 = Intent(context, HomeFragment::class.java)
        //intent2.putExtra("aaa", name)
        //context?.startActivity(intent2)

        //}
    }
}