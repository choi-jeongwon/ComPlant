package com.example.complant.navigation

import android.content.Context
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
import kotlinx.android.synthetic.main.fragment_my_information.view.*
import java.util.*

class HomeFragment : Fragment() {
    var mainActivity: MainActivity? = null
    var firestore: FirebaseFirestore? = null
    var auth: FirebaseAuth? = null
    var uid : String? = null

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
        var view = LayoutInflater.from(activity).inflate(R.layout.fragment_home, container, false)
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        uid = FirebaseAuth.getInstance().currentUser?.uid

        var mainMessageInfo = MainPageDTO()

        var calendar = Calendar.getInstance()
        var year = calendar.get(Calendar.YEAR)
        var month = calendar.get(Calendar.MONTH)
        var day = calendar.get(Calendar.DAY_OF_MONTH)

        val ONE_DAY = 24 * 60 * 60 * 1000

        view.main_account_button.setOnClickListener {
            mainActivity?.goMyPage()
        }

        var str : String? = arguments?.getString("content")
        mainMessageInfo.message = str
        mainMessageInfo.uid = uid

        if (mainMessageInfo.message != null) {
            firestore?.collection("mainPage")?.document(mainMessageInfo.uid.toString())
                ?.set(mainMessageInfo)
        }

        firestore?.collection("mainPage")?.document(uid!!)?.addSnapshotListener { snapshot, e ->
            if(snapshot == null) return@addSnapshotListener
            var mainPageDTO = snapshot.toObject(MainPageDTO::class.java)
            if (mainPageDTO?.message != null) {
                view.speech_box_text.setText(mainPageDTO?.message)
            }
        }

        // DB에서 profile name과 plant name을 가져와서 현재 정보를 보여준다.
        firestore?.collection("userInfo")
            ?.document(uid!!)
            ?.collection("info")
            ?.document(uid!!)
            ?.addSnapshotListener { snapshot, e ->
                if(snapshot == null) return@addSnapshotListener
                var userInfoDTO = snapshot.toObject(UserInfoDTO.UserInfo::class.java)
                view.profile_name.setText(userInfoDTO?.profileName)
                view.plant_name.setText(userInfoDTO?.plantName)
                view.plant_name2.setText(userInfoDTO?.plantName)
                view.plant_type.setText("( " + userInfoDTO?.plantType+ " )")

            }

        // 올린 이미지 가져오기
        getProfileImage()

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
                Glide.with(activity!!).load(url).apply(RequestOptions().circleCrop()).into(view?.plant_photo!!)
            }
        }
    }
}