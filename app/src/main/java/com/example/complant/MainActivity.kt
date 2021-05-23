package com.example.complant

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.complant.navigation.*
import com.google.android.gms.tasks.Task
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
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
        ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),1)

    }

    // 툴바 기본 상태
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

    fun onChangeAddPhotoActivity() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            startActivity(Intent(this, AddPhotoActivity::class.java))
        } else {
            Toast.makeText(this, "스토리지 읽기 권한이 없습니다.", Toast.LENGTH_LONG).show()
        }
    }

    // HomeFragment -> MyPageFragment 이동 함수
    fun goMyPage() {
        //FragmentManager에 Bundle로 Data를 담아 전달
        val myPageFragment = MyPageFragment()
        var bundle = Bundle()
        var uid = FirebaseAuth.getInstance().currentUser?.uid
        bundle.putString("destinationUid", uid)
        myPageFragment.arguments = bundle
        supportFragmentManager.beginTransaction().add(R.id.main_content, myPageFragment).addToBackStack("mypage").commit()
    }

    //뒤로가기 함수
    fun goBack() {
        onBackPressed()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // 원형 프로필 사진을 클릭했을 경우(프로필 사진 변경) 처리
        if(requestCode == MyPageFragment.PICK_PROFILE_FROM_ALBUM && resultCode == Activity.RESULT_OK) {
            var imageUri = data?.data
            var uid = FirebaseAuth.getInstance().currentUser?.uid
            var storageRef = FirebaseStorage.getInstance().reference.child("userProfileImages").child(uid!!) // 이미지를 저장할 폴더명 UserProfileImages
            storageRef.putFile(imageUri!!).continueWithTask { task: Task<UploadTask.TaskSnapshot> ->
                return@continueWithTask storageRef.downloadUrl
            }.addOnSuccessListener { uri ->
                var map = HashMap<String, Any>()
                map["image"] = uri.toString()
                FirebaseFirestore.getInstance().collection("profileImages").document(uid).set(map)
            }
        }
    }
}