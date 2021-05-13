package com.example.complant

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import com.example.complant.navigation.AlarmFragment
import com.example.complant.navigation.BoardFragment
import com.example.complant.navigation.DictionaryFragment
import com.example.complant.navigation.HomeFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.action_board -> {
                var BoardFragment = BoardFragment()
                supportFragmentManager.beginTransaction().replace(R.id.main_content, BoardFragment).commit()
                return true
            }
            R.id.action_dictionary -> {
                var DictionaryFragment = DictionaryFragment()
                supportFragmentManager.beginTransaction().replace(R.id.main_content, DictionaryFragment).commit()
                return true
            }
            R.id.action_home -> {
                var HomeFragment = HomeFragment()
                supportFragmentManager.beginTransaction().replace(R.id.main_content, HomeFragment).commit()
                return true
            }
            R.id.action_alarm -> {
                var AlarmFragment = AlarmFragment()
                supportFragmentManager.beginTransaction().replace(R.id.main_content, AlarmFragment).commit()
                return true
            }
            R.id.action_diary -> {
                var DictionaryFragment = DictionaryFragment()
                supportFragmentManager.beginTransaction().replace(R.id.main_content, DictionaryFragment).commit()
                return true
            }
        }
        return false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        bottom_navigation.setOnNavigationItemSelectedListener(this)
    }
}