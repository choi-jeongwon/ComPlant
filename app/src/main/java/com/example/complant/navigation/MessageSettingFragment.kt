package com.example.complant.navigation

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


        view.btn_message_contents_update?.setOnClickListener {
            // 수정하기
           // mainActivity?.goMessageListFragment()
            mainActivity?.goBack()
        }
        return view
    }
}