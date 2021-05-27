package com.example.complant.navigation

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.complant.MainActivity
import com.example.complant.R
import kotlinx.android.synthetic.main.fragment_message_setting.view.*
import java.util.*

class MessageSettingFragment : Fragment() {
    var mainActivity: MainActivity? = null

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
        var view = LayoutInflater.from(activity).inflate(R.layout.fragment_message_setting, container, false)
        var message_date : String? = null
        var message_start_time : String? = null
        var message_end_time : String? = null
        var message_contents_input : String? = null

        view.btn_message_date?.setOnClickListener {
            var calendar = Calendar.getInstance()
            var year = calendar.get(Calendar.YEAR)
            var month = calendar.get(Calendar.MONTH)
            var day = calendar.get(Calendar.DAY_OF_MONTH)
            
            var listener = DatePickerDialog.OnDateSetListener { datePicker, i, i2, i3 ->
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
                    view.txt_start_time.setText("오전 ${i}:${i2}")
                } else {
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
                    view.txt_end_time.setText("오전 ${i}:${i2}")
                } else {
                    view.txt_end_time.setText("오후 ${i - 12}:${i2}")
                }
            }

            // boolean is24HourView : true일 때 24시간으로 표기
            var picker = TimePickerDialog(view.context, listener, hour, minute, false)
            picker.show()
        }

        view.btn_message_contents_update?.setOnClickListener {
            // 수정하기
            var messageListFragment = MessageListFragment()
            val bundle = Bundle()

            message_date = view.txt_message_date.text.toString()
            message_start_time = view.txt_start_time.text.toString()
            message_end_time = view.txt_end_time.text.toString()
            message_contents_input = view.txt_message_contents_input.text.toString()


            bundle.putString("message_date", message_date)
            bundle.putString("message_start_time", message_start_time)
            bundle.putString("message_end_time", message_end_time)
            bundle.putString("message_contents_input", message_contents_input)

            messageListFragment.arguments = bundle


             mainActivity?.goMessageListFragment1(messageListFragment)
            //mainActivity?.goBack()
        }
        return view
    }
}