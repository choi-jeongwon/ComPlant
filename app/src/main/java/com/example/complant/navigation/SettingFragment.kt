package com.example.complant.navigation

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.complant.LoginActivity
import com.example.complant.MainActivity
import com.example.complant.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_my_page.view.*
import kotlinx.android.synthetic.main.fragment_setting.*
import kotlinx.android.synthetic.main.fragment_setting.view.*

class SettingFragment : Fragment() {
    var mainActivity: MainActivity? = null
    var auth: FirebaseAuth? = null

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
        var view =
            LayoutInflater.from(activity).inflate(R.layout.fragment_setting, container, false)
        auth = FirebaseAuth.getInstance()

        // 1. 내 정보 변경
        view.btn_my_info_setting.setOnClickListener {
            mainActivity?.goMyInformationFragment()
        }
        view.my_info_setting.setOnClickListener {
            mainActivity?.goMyInformationFragment()
        }

        // 2. 메인 메시지 설정
        view.btn_main_message_setting.setOnClickListener {
            mainActivity?.goMessageListFragment()
        }

        view.main_message_setting.setOnClickListener {
            mainActivity?.goMessageListFragment()
        }

        // 회원 탈퇴
//        view.btn_quit_setting.setOnClickListener { }
//        view.quit_setting.setOnClickListener { }

        // 3. 로그아웃
        view.btn_logout_setting.setOnClickListener {
            activity?.finish() // activity 종료
            startActivity(
                Intent(
                    activity,
                    LoginActivity::class.java
                )
            ) // 현재 activity를 종료하고 LoginActivity를 호출
            auth?.signOut() // firebase signout
        }
        view.logout_setting.setOnClickListener {
            activity?.finish() // activity 종료
            startActivity(
                Intent(
                    activity,
                    LoginActivity::class.java
                )
            ) // 현재 activity를 종료하고 LoginActivity를 호출
            auth?.signOut() // firebase signout
        }

        return view
    }
}