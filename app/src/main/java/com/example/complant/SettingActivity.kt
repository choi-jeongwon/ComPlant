package com.example.complant

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_setting.*

class SettingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)

        // 1. ID / PW 변경
        btn_img_id_pw_setting.setOnClickListener {  }
        btn_id_pw_setting.setOnClickListener {  }

        // 2. 회원 탈퇴
        btn_img_quit_setting.setOnClickListener {  }
        btn_quit_setting.setOnClickListener {  }

        // 3. 메인 메시지 설정
        btn_img_main_message_setting.setOnClickListener {  }
        btn_main_message_setting.setOnClickListener {  }

        // 4. 프로필 사진 변경
        btn_img_profile_image_setting.setOnClickListener {  }
        btn_profile_image_setting.setOnClickListener {  }

        // 5. 반려식물 별칭 변경
        btn_img_plant_name_setting.setOnClickListener {  }
        btn_img_plant_name_setting.setOnClickListener {  }

    }


}