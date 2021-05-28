package com.example.complant.navigation

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import com.example.complant.MainActivity
import com.example.complant.R
import com.example.complant.Receiver
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_home.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.concurrent.timer

class HomeFragment : Fragment() {
    var mainActivity: MainActivity? = null
    lateinit var context1 : Context
    lateinit var alarmManager: AlarmManager

    var receive_temp : String? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = activity as MainActivity?
    }

    override fun onDetach() {
        super.onDetach()
        mainActivity = null
    }

//    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    @RequiresApi(Build.VERSION_CODES.KITKAT)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = LayoutInflater.from(activity).inflate(R.layout.fragment_home, container, false)

        view.main_account_button.setOnClickListener {
            mainActivity?.goMyPage()
        }
//------------------------------------------------------------------
        //resetAlarm(context)

        context1 = view.context
        alarmManager = context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        view.delete_create.setOnClickListener {
            val second = edit_timer.text.toString().toInt() * 1000
            val intent = Intent(context1, Receiver::class.java)
            intent.putExtra("a", "abc")
            val pendingIntent = PendingIntent.getBroadcast(context1, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT )


            receive_temp = intent.getStringExtra("aaa")


            Log.d("MainActivity" ," Create: " + Date().toString())


            //----------




            alarmManager.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + second, pendingIntent)
        }
        view.delete_update.setOnClickListener {
            val second = edit_timer.text.toString().toInt() * 1000
            val intent = Intent(context1, Receiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(context1, 0, intent,PendingIntent.FLAG_UPDATE_CURRENT )
            Log.d("MainActivity" ," Update: " + Date().toString())
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + second, pendingIntent)
        }
        view.delete_cancel.setOnClickListener {
            val intent = Intent(context1, Receiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(context1, 0, intent,PendingIntent.FLAG_UPDATE_CURRENT )
            Log.d("MainActivity" ," Cancel: " + Date().toString())
            alarmManager.cancel(pendingIntent)
        }

            //--------------------------------------------------------------------
        return view
    }

//    fun resetAlarm(context:Context?) {
//        val alarmManager = context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
//        val intent = Intent(context, Receiver::class.java)
//
//        // Used for filtering inside Broadcast receiver
//        intent.action = "MyBroadcastReceiverAction"
//        val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0)
//
//        var calendar = Calendar.getInstance()
//        calendar.set(Calendar.YEAR, 2021)
//        calendar.set(Calendar.MONTH, 5)
//        calendar.set(Calendar.DAY_OF_MONTH, 28)
//        calendar.set(Calendar.HOUR_OF_DAY, 20)
//        calendar.set(Calendar.MINUTE, 6)
//        calendar.set(Calendar.SECOND, 0)
//
//        //다음날 0시에 맞추기 위해 24시간을 뜻하는 상수인 AlarmManager.INTERVAL_DAY를 더해줌.
//        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.timeInMillis + AlarmManager.INTERVAL_DAY, AlarmManager.INTERVAL_DAY, pendingIntent)
//
//        var format : SimpleDateFormat = SimpleDateFormat("MM/dd kk:mm:ss")
//        var setResetTime : String = format.format(Date(calendar.timeInMillis+AlarmManager.INTERVAL_DAY))
//
//        Log.d("resetAlarm", "ResetHour : " + setResetTime)
//
//    }

}