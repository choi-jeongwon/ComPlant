package com.example.complant.navigation

import android.content.Context
import android.content.Intent
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.complant.LoginActivity
import com.example.complant.MainActivity
import com.example.complant.R
import com.example.complant.navigation.model.ContentDTO
import com.example.complant.navigation.model.FollowDTO
import com.example.complant.navigation.model.UserInfoDTO
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_my_information.view.*
import kotlinx.android.synthetic.main.fragment_my_page.view.*

class MyPageFragment : Fragment() {
    var mainActivity: MainActivity? = null
    var fragmentView: View? = null
    var firestore: FirebaseFirestore? = null
    var uid: String? = null
    var auth: FirebaseAuth? = null
    var currentUserUid: String? = null    // 유저 아이디를 비교해서 본인 계정인지 타인 계정인지 구별하기 위한 변수

    companion object { // static 선언
        var PICK_PROFILE_FROM_ALBUM = 10
    }

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
        fragmentView =
            LayoutInflater.from(activity).inflate(R.layout.fragment_my_page, container, false)
        uid = arguments?.getString("destinationUid") // 이전 화면에서 넘어온 값을 받아와서 uid에 넣음.
        firestore = FirebaseFirestore.getInstance() // 초기화
        auth = FirebaseAuth.getInstance()  // 초기화
        currentUserUid = auth?.currentUser?.uid // 현재 접속한 유저의 uid를 받아오는 변수

        if (uid == currentUserUid) {
            // MyPage (본인의 MyPageFragment일 때)
//            fragmentView?.temp_follow_button?.text = getString(R.string.signout) // 로그아웃 버튼
//            fragmentView?.temp_follow_button?.setOnClickListener {
//                activity?.finish() // activity 종료
//                startActivity(Intent(activity, LoginActivity::class.java)) // 현재 activity를 종료하고 LoginActivity를 호출
//                auth?.signOut() // firebase signout
//            }
            fragmentView?.temp_follow_button?.visibility = GONE // 내 페이지에서는 버튼이 보이지 않음
        } else {
            // OtherUserPage (타인의 MyPagFragment일 때)
            fragmentView?.temp_follow_button?.text = getString(R.string.follow) // follow 버튼
            var mainActivity = (activity as MainActivity)

            // 뒤로가기버튼 화살표버튼을 누르면 home으로 이동
            mainActivity?.toolbar_btn_back?.setOnClickListener {
                mainActivity.bottom_navigation.selectedItemId = R.id.action_home
            }

            // toolbar의 이미지는 없애고 username과 back버튼만 보이게 설정정
            mainActivity?.toolbar_title_image?.visibility = View.GONE
            mainActivity?.toolbar_username?.visibility = View.VISIBLE
            mainActivity?.toolbar_btn_back?.visibility = View.VISIBLE

            fragmentView?.temp_follow_button?.setOnClickListener {
                requestFollow()
            }
        }

        fragmentView?.my_page_main_recyclerview?.adapter = MyPageFragmentRecyclerViewAdapter()
        fragmentView?.my_page_main_recyclerview?.layoutManager =
            GridLayoutManager(activity!!, 3) // 3개씩 뜨도록 설정

        // 프로필 이미지 클릭 시 프로필 이미지 수정
        fragmentView?.my_page_main_profile_image?.setOnClickListener {
            var photoPickerIntent = Intent(Intent.ACTION_PICK) // 이미지 고르기
            photoPickerIntent.type = "image/*"
            activity?.startActivityForResult(photoPickerIntent, PICK_PROFILE_FROM_ALBUM)
        }

        //setting 클릭 시 SettingFragment로 이동
        fragmentView?.setting_button?.setOnClickListener {
            //   MyPageFragment -> SettingFragment 이동
            var settingFragment = SettingFragment()
            activity?.supportFragmentManager?.beginTransaction()
                ?.add(R.id.main_content, settingFragment)
                ?.addToBackStack("settingFragment")?.commit()
        }

        // DB에서 profile name을 가져와서 보여준다.
        firestore?.collection("userInfo")
            ?.document(uid!!)
            ?.collection("info")
            ?.document(uid!!)
            ?.addSnapshotListener { snapshot, e ->
                if (snapshot == null) return@addSnapshotListener
                var userInfoDTO = snapshot.toObject(UserInfoDTO.UserInfo::class.java)
                view?.profile_name?.setText(userInfoDTO?.profileName)
            }

        getProfileImage()
        getFollowerAndFollowing()
        return fragmentView
    }

    // 올린 이미지를 다운로드 받는 함수
    fun getProfileImage() {
        firestore?.collection("profileImages")?.document(uid!!)
            ?.addSnapshotListener { documentSnapshot, firebaseFirestoreException ->

                // documentSnapshot이 null일 경우 바로 리턴
                if (documentSnapshot == null) return@addSnapshotListener

                // null이 아닐 경우 이미지 주소 값을 받아와서 Glide로 다운로드 받는다.
                if (documentSnapshot.data != null) {
                    var url = documentSnapshot?.data!!["image"]
                    Glide.with(activity!!).load(url).apply(RequestOptions().circleCrop())
                        .into(fragmentView?.my_page_main_profile_image!!)
                }
            }
    }

    // 나중에 수정할 것. requestFollow(), getFollowerAndFollowing() 수정하기
    fun requestFollow() {
        // Save data to my account
        var tsDocFollowing = firestore?.collection("users")?.document(currentUserUid!!)
        firestore?.runTransaction { transaction ->
            var followDTO = transaction.get(tsDocFollowing!!).toObject(FollowDTO::class.java)
            if (followDTO == null) { // followDTO 값이 아무것도 없을 때
                followDTO = FollowDTO()

                transaction.set(tsDocFollowing, followDTO) // 데이터가 DB에 담김
                return@runTransaction
            }

            if (followDTO.followings.containsKey(uid)) {
                // 나의 follow 리스트에 상대방의 uid가 이미 있으면 팔로우 취소 처리
                followDTO?.followingCount = followDTO?.followingCount - 1
                followDTO?.followings.remove(uid!!)
            } else {
                // 나의 follow 리스트에 상대방의 uid가 없으면 팔로우 처리
                followDTO?.followingCount = followDTO?.followingCount + 1
                followDTO?.followings[uid!!] = true
            }
            transaction.set(tsDocFollowing, followDTO)
            return@runTransaction
        }

        // Save data to third person
        var tsDocFollower = firestore?.collection("users")?.document(uid!!)
        firestore?.runTransaction { transaction ->
            var followDTO = transaction.get(tsDocFollower!!).toObject(FollowDTO::class.java)

            if (followDTO == null) {
                followDTO = FollowDTO()

                transaction.set(tsDocFollower, followDTO!!)
                return@runTransaction
            }

            // 상대방의 follow 리스트에 나의 uid가 이미 있으면 팔로우 취소 처리
            if (followDTO!!.followers.containsKey(currentUserUid)) {
                // It cancel my follower when I follow a third person
                followDTO!!.followerCount = followDTO!!.followerCount - 1
                followDTO!!.followers.remove(currentUserUid!!)

            } else {
                // 상대방의 follow 리스트에 나의 uid가 없으면 팔로우 처리
                // It add my follower when I don't follow a third person
                followDTO!!.followerCount = followDTO!!.followerCount + 1
                followDTO!!.followers[currentUserUid!!] = true
            }
            transaction.set(tsDocFollower, followDTO!!)
            return@runTransaction
        }
    }

    fun getFollowerAndFollowing() {
        firestore?.collection("users")?.document(uid!!)
            ?.addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
                if (documentSnapshot == null) return@addSnapshotListener
                var followDTO = documentSnapshot.toObject(FollowDTO::class.java)
                if (followDTO?.followingCount != null) {
                    fragmentView?.account_following_count?.text =
                        followDTO?.followingCount?.toString()
                }
                if (followDTO?.followerCount != null) {
                    fragmentView?.account_follower_count?.text =
                        followDTO?.followerCount?.toString()

                    // 나의 uid가 있을 경우
                    if (followDTO?.followers?.containsKey(currentUserUid!!)) {
                        fragmentView?.temp_follow_button?.text = getString(R.string.follow_cancel)
                        fragmentView?.temp_follow_button?.background?.setColorFilter(
                            ContextCompat.getColor(
                                activity!!,
                                R.color.colorLightGray
                            ), PorterDuff.Mode.MULTIPLY
                        )
                    } else {
                        if (uid != currentUserUid) { // 상대방의 mypagefragment일 때 백그라운드컬러 변경
                            fragmentView?.temp_follow_button?.text = getString(R.string.follow)
                            fragmentView?.temp_follow_button?.background?.colorFilter = null
                        }
                    }
                }
            }
    }

    inner class MyPageFragmentRecyclerViewAdapter :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        //contentDTOs를 담을 ArrayList
        var contentDTOs: ArrayList<ContentDTO> = arrayListOf()

        // 생성자로 데이터베이스의 값들을 읽어옴.
        init {
            // images 경로에서 query 만듦. 내 uid에 맞는 값만 읽어오기.
            firestore?.collection("images")?.whereEqualTo("uid", uid)
                ?.addSnapshotListener { querySnapshot, firebaseFirestoreException ->

                    // querySnapshot이 null일 경우, 바로 종료시킨다.
                    if (querySnapshot == null) return@addSnapshotListener

                    // 데이터를 담는다.
                    for (snapshot in querySnapshot.documents) {
                        //snapshot을 ContentDTO로 캐스팅한 다음에 contentDTOs에 담는다.
                        contentDTOs.add(snapshot.toObject(ContentDTO::class.java)!!)
                    }

                    // contentDTOs 사이즈 값에 따라 account_post_count 값 증가시킴.
                    fragmentView?.account_post_count?.text = contentDTOs.size.toString()
                    notifyDataSetChanged() // 리사이클뷰 새로고침
                }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            // 화면 폭의 1/3 값을 가져옴.
            var width = resources.displayMetrics.widthPixels / 3

            var imageview = ImageView(parent.context)
            imageview.layoutParams = LinearLayoutCompat.LayoutParams(width, width)
            return CustomViewHolder(imageview)
        }

        inner class CustomViewHolder(var imageview: ImageView) :
            RecyclerView.ViewHolder(imageview) {
        }

        override fun getItemCount(): Int {
            return contentDTOs.size
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            var imageview = (holder as CustomViewHolder).imageview

            // Glide로 이미지 다운로드
            Glide.with(holder.itemView.context).load(contentDTOs[position].imageUrl)
                .apply(RequestOptions().centerCrop()).into(imageview)

            holder.imageview.setOnClickListener {

                var myPagePostFragment = MyPagePostFragment()
                val bundle = Bundle()

                bundle.putString("explain", contentDTOs[position].explain)
                bundle.putString("imageUrl", contentDTOs[position].imageUrl)
                bundle.putString("userId", contentDTOs[position].userId)
                bundle.putInt("favoriteCount", contentDTOs[position].favoriteCount)
                bundle.putString("destinationUid", uid) // 각 유저 페이지의 정보 uid를 보낸다.
                bundle.putLong("timestamp",contentDTOs[position].timestamp!!) // 구분자인 timestamp를 보낸다.
                myPagePostFragment.arguments = bundle

                activity?.supportFragmentManager?.beginTransaction()
                    ?.add(R.id.main_content, myPagePostFragment)
                    ?.addToBackStack("settingFragment")?.commit()
            }
        }
    }
}