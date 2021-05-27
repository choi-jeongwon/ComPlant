package com.example.complant.navigation

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.complant.MainActivity
import com.example.complant.R
import kotlinx.android.synthetic.main.fragment_my_page.view.*
import kotlinx.android.synthetic.main.fragment_setting.*
import kotlinx.android.synthetic.main.fragment_setting.view.*

class SettingFragment : Fragment() {
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
        var view = LayoutInflater.from(activity).inflate(R.layout.fragment_setting, container, false)

        // 1. ID / PW 변경
        view.btn_img_id_pw_setting.setOnClickListener {  }
        view.btn_id_pw_setting.setOnClickListener {  }

        // 2. 회원 탈퇴
        view.btn_img_quit_setting.setOnClickListener {  }
        view.btn_quit_setting.setOnClickListener {  }

        // 3. 메인 메시지 설정
        view.btn_img_main_message_setting.setOnClickListener {
            mainActivity?.goMessageListFragment()
        }
        view.btn_main_message_setting.setOnClickListener {
            mainActivity?.goMessageListFragment()
        }

        // 4. 프로필 사진 변경
        view.btn_img_profile_image_setting.setOnClickListener {  }
        view.btn_profile_image_setting.setOnClickListener {  }

        // 5. 반려식물 별칭 변경
        view.btn_img_plant_name_setting.setOnClickListener {  }
        view.btn_img_plant_name_setting.setOnClickListener {  }


        return view
    }



}