package com.example.complant.navigation

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.complant.MainActivity
import com.example.complant.R
import com.example.complant.navigation.model.MainPageDTO
import com.example.complant.navigation.model.UserInfoDTO
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_home.view.*
import kotlinx.android.synthetic.main.fragment_my_information.*
import kotlinx.android.synthetic.main.fragment_my_information.view.*
import kotlinx.android.synthetic.main.fragment_my_page.view.*

class MyInformationFragment : Fragment() {
    var mainActivity: MainActivity? = null
    var firestore : FirebaseFirestore? = null
    var uid : String? = null
   // var auth : FirebaseAuth? = null
    var currentUserId : String? = null
//    var currentProfileName : String? = null
//    var currentPlantName : String? = null

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
        var view = LayoutInflater.from(activity).inflate(R.layout.fragment_my_information, container, false)
        firestore = FirebaseFirestore.getInstance()
        uid = FirebaseAuth.getInstance().currentUser?.uid
        currentUserId = FirebaseAuth.getInstance().currentUser?.email

      //  var userInfoDTO = UserInfoDTO.UserInfo()

        // 프래그먼트에 들어오면 현재 정보(정보 변경 전)를 보여준다.
        view.my_info_current_id.setText(currentUserId)

        // DB에서 profile name과 plant name을 가져와서 현재 정보를 보여준다.
        firestore?.collection("userInfo")
            ?.document(uid!!)
            ?.collection("info")
            ?.document(uid!!)
            ?.addSnapshotListener { snapshot, e ->
            if(snapshot == null) return@addSnapshotListener
            var userInfoDTO = snapshot.toObject(UserInfoDTO.UserInfo::class.java)
                view.my_info_current_profile_name.setText(userInfoDTO?.profileName)
                view.edit_profile_name_setting.setText(userInfoDTO?.profileName)
                view.my_info_current_plant_name.setText(userInfoDTO?.plantName)
                view.edit_plant_name_setting.setText(userInfoDTO?.plantName)
                view.my_info_current_plant_type.setText(userInfoDTO?.plantType)
                view.edit_plant_type_setting.setText(userInfoDTO?.plantType)
            }


        // 프로필 이미지 클릭 시 프로필 이미지 수정
        view?.profile_image_setting?.setOnClickListener {
            var photoPickerIntent = Intent(Intent.ACTION_PICK) // 이미지 고르기
            photoPickerIntent.type = "image/*"
            activity?.startActivityForResult(photoPickerIntent,
                MyPageFragment.PICK_PROFILE_FROM_ALBUM
            )
        }

        // 올린 이미지 가져오기
        getProfileImage()

        // 완료 버튼을 누르면 정보 DB에 업데이트
        view.btn_my_info_update.setOnClickListener {
            var tsDocUserInfo = firestore?.collection("userInfo")
                ?.document(uid!!)
                ?.collection("info")
                ?.document(uid!!)

            firestore?.runTransaction { transaction ->
                var userInfoDTO = transaction.get(tsDocUserInfo!!).toObject(UserInfoDTO.UserInfo::class.java)

                userInfoDTO?.profileName = edit_profile_name_setting.text.toString()
                userInfoDTO?.plantName = edit_plant_name_setting.text.toString()
                userInfoDTO?.plantType = edit_plant_type_setting.text.toString()

                if (userInfoDTO != null) {
                    transaction.set(tsDocUserInfo, userInfoDTO)
                }
            }

        }

        return view
    }

    // 올린 이미지를 다운로드 받는 함수
    fun getProfileImage() {
        firestore?.collection("profileImages")?.document(uid!!)?.addSnapshotListener {
                documentSnapshot, firebaseFirestoreException ->

            // documentSnapshot이 null일 경우 바로 리턴
            if(documentSnapshot == null) return@addSnapshotListener

            // null이 아닐 경우 이미지 주소 값을 받아와서 Glide로 다운로드 받는다.
            if(documentSnapshot.data != null) {
                var url = documentSnapshot?.data!!["image"]
                Glide.with(activity!!).load(url).apply(RequestOptions().circleCrop()).into(view?.profile_image_setting!!)
            }
        }
    }
}