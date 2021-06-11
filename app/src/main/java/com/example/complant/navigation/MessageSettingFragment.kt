package com.example.complant.navigation

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.complant.MainActivity
import com.example.complant.R
import com.example.complant.navigation.model.Message11
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_message_setting.view.*
import java.util.*

class MessageSettingFragment : Fragment() {
    var mainActivity: MainActivity? = null
    var firestore: FirebaseFirestore? = null
    var auth: FirebaseAuth? = null
    var userUid: String? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = activity as MainActivity?
    }

    override fun onDetach() {
        super.onDetach()
        mainActivity = null
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = LayoutInflater.from(activity)
            .inflate(R.layout.fragment_message_setting, container, false)

        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        userUid = FirebaseAuth.getInstance().currentUser?.uid

        var messageInfo = Message11.Messages()

        view.btn_message_date?.setOnClickListener {
            var calendar = Calendar.getInstance()
            var year = calendar.get(Calendar.YEAR)
            var month = calendar.get(Calendar.MONTH)
            var day = calendar.get(Calendar.DAY_OF_MONTH)

            var listener = DatePickerDialog.OnDateSetListener { datePicker, i, i2, i3 ->
                if (i2 + 1 < 10 && i3 < 10)
                    view.txt_message_date.setText("${i}-0${i2 + 1}-0${i3}")
                else if (i2 + 1 < 10)
                    view.txt_message_date.setText("${i}-0${i2 + 1}-${i3}")
                else if (i3 < 10)
                    view.txt_message_date.setText("${i}-${i2 + 1}-0${i3}")
                else
                    view.txt_message_date.setText("${i}-${i2 + 1}-${i3}")
            }

            var picker = DatePickerDialog(view.context, listener, year, month, day)
            picker.show()
        }

        view.btn_start_time?.setOnClickListener {
            var calendar = Calendar.getInstance()
            var hour = calendar.get(Calendar.HOUR)
            var minute = calendar.get(Calendar.MINUTE)

            var listener = TimePickerDialog.OnTimeSetListener { timePicker, i, i2 ->
                if (i < 12) {
                    if (i < 10 && i2 < 10)
                        view.txt_start_time.setText("오전 0${i}:0${i2}")
                    else if (i < 10)
                        view.txt_start_time.setText("오전 0${i}:${i2}")
                    else if (i2 < 10)
                        view.txt_start_time.setText("오전 ${i}:0${i2}")
                    else
                        view.txt_start_time.setText("오전 ${i}:${i2}")

                } else {
                    if (i - 12 < 10 && i2 < 10)
                        view.txt_start_time.setText("오후 0${i - 12}:0${i2}")
                    else if (i - 12 < 10)
                        view.txt_start_time.setText("오후 0${i - 12}:${i2}")
                    else if (i2 < 10)
                        view.txt_start_time.setText("오후 ${i - 12}:0${i2}")
                    else
                        view.txt_start_time.setText("오후 ${i - 12}:${i2}")
                }
            }

            // boolean is24HourView : true일 때 24시간으로 표기
            var picker = TimePickerDialog(view.context, listener, hour, minute, false)
            picker.show()
        }

        view.btn_end_time?.setOnClickListener {
            var calendar = Calendar.getInstance()
            var hour = calendar.get(Calendar.HOUR)
            var minute = calendar.get(Calendar.MINUTE)

            var listener = TimePickerDialog.OnTimeSetListener { timePicker, i, i2 ->
                if (i < 12) {
                    if (i < 10 && i2 < 10)
                        view.txt_end_time.setText("오전 0${i}:0${i2}")
                    else if (i < 10)
                        view.txt_end_time.setText("오전 0${i}:${i2}")
                    else if (i2 < 10)
                        view.txt_end_time.setText("오전 ${i}:0${i2}")
                    else
                        view.txt_end_time.setText("오전 ${i}:${i2}")

                } else {
                    if (i - 12 < 10 && i2 < 10)
                        view.txt_end_time.setText("오후 0${i - 12}:0${i2}")
                    else if (i - 12 < 10)
                        view.txt_end_time.setText("오후 0${i - 12}:${i2}")
                    else if (i2 < 10)
                        view.txt_end_time.setText("오후 ${i - 12}:0${i2}")
                    else
                        view.txt_end_time.setText("오후 ${i - 12}:${i2}")
                }
            }

            // boolean is24HourView : true일 때 24시간으로 표기
            var picker = TimePickerDialog(view.context, listener, hour, minute, false)
            picker.show()
        }

        view.btn_message_contents_update?.setOnClickListener {
            messageInfo.uid = userUid
            messageInfo.date = view.txt_message_date.text.toString()
            messageInfo.startTime = view.txt_start_time.text.toString()
            messageInfo.endTime = view.txt_end_time.text.toString()
            messageInfo.content = view.txt_message_contents_input.text.toString()
            messageInfo.timestamp = System.currentTimeMillis()

            if (messageInfo.date != null && messageInfo.startTime != null && messageInfo.endTime != null && messageInfo.content != null) {
                firestore?.collection("messages")?.document(userUid!!)?.collection("userMessages")
                    ?.document(messageInfo.timestamp.toString())
                    ?.set(messageInfo)
            }

            mainActivity?.goBack()
        }
        return view
    }
}