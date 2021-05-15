package com.example.complant

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.core.app.ActivityCompat
import com.example.complant.navigation.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main.*
import java.util.jar.Manifest

class MainActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 로딩 바를 작동
        progress_bar.visibility = View.VISIBLE

        // Bottom Navigation View
        bottom_navigation.setOnNavigationItemSelectedListener(this)
        // 페이지 시작 시 R.id.action_home 버튼이 작동되도록 설정
        bottom_navigation.selectedItemId = R.id.action_home

        //스토리지 접근 권한 요청
//        ActivityCompat.requestPermissions(this,
//        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1)
    }

    fun setToolbarDefault() {
        toolbar_title_image.visibility = View.VISIBLE
        toolbar_btn_back.visibility = View.GONE
        toolbar_username.visibility = View.GONE
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        setToolbarDefault()
        when(item.itemId) {
            R.id.action_board -> {
                var boardFragment = BoardFragment()
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.main_content, boardFragment)
                    .commit()
                return true
            }
            R.id.action_dictionary -> {
                var dictionaryFragment = DictionaryFragment()
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.main_content, dictionaryFragment)
                    .commit()
                return true
            }
            R.id.action_home -> {
                var homeFragment = HomeFragment()
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.main_content, homeFragment)
                    .commit()
                return true
            }
            R.id.action_alarm -> {
                var alarmFragment = AlarmFragment()
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.main_content, alarmFragment)
                    .commit()
                return true
            }
            R.id.action_diary -> {
                var diaryFragment = DiaryFragment()
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.main_content, diaryFragment)
                    .commit()
                return true
            }
        }
        return false
    }

}