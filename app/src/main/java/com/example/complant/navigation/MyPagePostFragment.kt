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
import com.example.complant.navigation.model.UserInfoDTO
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_my_page_post.view.*
import kotlinx.android.synthetic.main.fragment_my_page_post.view.my_page_diaryitem_explain_textview
import kotlinx.android.synthetic.main.fragment_my_page_post.view.my_page_diaryitem_favoritecounter_textview
import kotlinx.android.synthetic.main.fragment_my_page_post.view.my_page_diaryitem_profile_image
import kotlinx.android.synthetic.main.fragment_my_page_post.view.my_page_diaryitem_profile_textview


class MyPagePostFragment : Fragment() {
    var mainActivity: MainActivity? = null
    var firestore: FirebaseFirestore? = null
    var previousUid: String? = null // 이전 페이지에서 넘어온 uid
    var auth: FirebaseAuth? = null
    var receivedTimestamp: Long? = null
    var uid: String? = null   // 현재 접속한 유저 uid

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity?
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
            LayoutInflater.from(activity).inflate(R.layout.fragment_my_page_post, container, false)
        firestore = FirebaseFirestore.getInstance()
        previousUid = arguments?.getString("destinationUid")
        auth = FirebaseAuth.getInstance()
        uid = auth?.currentUser?.uid // 현재 접속한 유저의 uid를 받아오는 변수

        //사용자 프로필사진 띄우기
        getProfileImage()

        // 사용자 게시물 사진 띄우기
        Glide.with(activity!!).load(arguments?.getString("imageUrl"))
            .into(view?.my_page_item_imageview_content!!)

        receivedTimestamp = arguments?.getLong("timestamp")
        view.my_page_diaryitem_favoritecounter_textview.setText("좋아요 " + arguments?.getInt("favoriteCount") + "개")
        view.my_page_diaryitem_explain_textview.setText(arguments?.getString("explain"))

        //profile name 띄우기
        firestore?.collection("userInfo")
            ?.document(previousUid!!)
            ?.collection("info")
            ?.document(previousUid!!)
            ?.addSnapshotListener { documentSnapshot, firebaseFirestoreException ->

                // documentSnapshot이 null일 경우 바로 리턴
                if (documentSnapshot == null) return@addSnapshotListener

                var userInfoDTO = documentSnapshot.toObject(UserInfoDTO.UserInfo::class.java)
                view.my_page_diaryitem_profile_textview.setText(userInfoDTO?.profileName)
            }


        // 추후 수정 예정 (댓글과 좋아요 버튼을 diary fragment에 있는 내용과 연결해야 함.)
        view.my_page_diaryitem_comment_imageview.setOnClickListener { v ->
            var intent = Intent(v.context, CommentActivity::class.java)
            intent.putExtra("contentUid", previousUid)
            startActivity(intent)
        }

        return view
    }

    fun getProfileImage() {
        firestore?.collection("profileImages")?.document(previousUid!!)
            ?.addSnapshotListener { documentSnapshot, firebaseFirestoreException ->

                // documentSnapshot이 null일 경우 바로 리턴
                if (documentSnapshot == null) return@addSnapshotListener

                // null이 아닐 경우 이미지 주소 값을 받아와서 Glide로 다운로드 받는다.
                if (documentSnapshot.data != null) {
                    var url = documentSnapshot?.data!!["image"]
                    Glide.with(activity!!).load(url).apply(RequestOptions().circleCrop())
                        .into(view?.my_page_diaryitem_profile_image!!)
                }
            }
    }
}