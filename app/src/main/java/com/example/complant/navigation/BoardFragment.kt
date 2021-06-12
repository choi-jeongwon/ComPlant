package com.example.complant.navigation

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.complant.MainActivity
import com.example.complant.R
import com.example.complant.navigation.model.ContentDTO
import com.example.complant.navigation.model.ContentDTO2
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_board.view.*
import kotlinx.android.synthetic.main.fragment_diary.view.*
import kotlinx.android.synthetic.main.item_board.view.*
import kotlinx.android.synthetic.main.item_diary.view.*

class BoardFragment : Fragment() {
    var firestore: FirebaseFirestore? = null
    var uid: String? = null

    //    게시판 작성 버튼 내용 시작
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
        var view =
            LayoutInflater.from(activity).inflate(R.layout.fragment_board, container, false)
        val board_btn_write = view.findViewById<View>(R.id.board_btn_write) as Button
        board_btn_write.setOnClickListener {
            mainActivity?.onChangeAddPhotoActivity2()
        }

        firestore = FirebaseFirestore.getInstance()
        uid = FirebaseAuth.getInstance().currentUser?.uid

        view.fragment_board_recyclerview.adapter = BoardRecyclerViewAdapter()
        view.fragment_board_recyclerview.layoutManager = LinearLayoutManager(activity)

        return view
    }
    // 게시판 작성 버튼 내용 끝

    // board RecyclerView
    inner class BoardRecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        var contentDTOs2: ArrayList<ContentDTO2> = arrayListOf()
        var contentUidList: ArrayList<String> = arrayListOf()

        init {


            firestore?.collection("images2")?.orderBy("timestamp")
                ?.addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                    contentDTOs2.clear()
                    contentUidList.clear()
                    // Sometimes, This code return null of querySnapshot when it signout
                    if (querySnapshot == null) return@addSnapshotListener

                    for (snapshot in querySnapshot!!.documents) {
                        var item = snapshot.toObject(ContentDTO2::class.java)
                        contentDTOs2.add(item!!)
                        contentUidList.add(snapshot.id)
                    }
                    notifyDataSetChanged()
                }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            var view =
                LayoutInflater.from(parent.context).inflate(R.layout.item_board, parent, false)
            return CustomViewHolder(view)
        }

        inner class CustomViewHolder(view: View) : RecyclerView.ViewHolder(view)

        override fun getItemCount(): Int {

            return contentDTOs2.size
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            var viewholder = (holder as CustomViewHolder).itemView

            //UserId(게시판 작성자 아이디)
            viewholder.boarditem_profile_textview.text = contentDTOs2!![position].userId

            //ExplainTitle(게시판 글 제목)
            viewholder.boarditem_explain_textview_title.text = contentDTOs2!![position].explainTitle

            //likes
            viewholder.boarditem_favoritecounter_textview.text =
                "공감 " + contentDTOs2!![position].favoriteCount + "개"

            //'좋아요'버튼이 눌렸을 때의 이벤트
            viewholder.boarditem_favorite_imageview.setOnClickListener {
                favoriteEvent(position)
            }

            //'좋아요'카운터와 하트가 색칠되거나 비어있는 이벤트
            if (contentDTOs2!![position].favorites.containsKey(uid)) {
                //'좋아요'버튼 클릭한 부분
                viewholder.boarditem_favorite_imageview.setImageResource(R.drawable.ic_favorite)
            } else {
                //'좋아요'버튼 아직 클릭하지 않은 경우
                viewholder.boarditem_favorite_imageview.setImageResource(R.drawable.ic_favorite_border)
            }


            //작성자 Profile Image 가져오기
            firestore?.collection("profileImages")?.document(contentDTOs2[position].uid!!)
                ?.get()?.addOnCompleteListener { task ->
                    if (task.isSuccessful) {

                        val url = task.result!!["image"]
                        Glide.with(holder.itemView.context).load(url)
                            .apply(RequestOptions().circleCrop())
                            .into(viewholder.boarditem_profile_image)
                    }
                }


            //MyPageFragment 로 이동
            // 상대방의 프로필 이미지를 클릭하면 상대방의 유저 정보로 이동
            viewholder.boarditem_profile_image.setOnClickListener {

                val fragment = MyPageFragment()
                val bundle = Bundle()

                bundle.putString("destinationUid", contentDTOs2[position].uid)  // 이전 페이지에서 넘어온 값
                bundle.putString("userId", contentDTOs2[position].userId)   // 이메일 값

                fragment.arguments = bundle
                activity?.supportFragmentManager?.beginTransaction()
                    ?.replace(R.id.main_content, fragment)?.commit()
            }

            viewholder.boarditem_comment_imageview.setOnClickListener { v ->
                var intent = Intent(v.context, CommentActivity2::class.java)
                intent.putExtra("contentUid", contentUidList[position])
                startActivity(intent)
            }

            viewholder.board_select.setOnClickListener { v ->
                var intent = Intent(v.context, DetailBoardActivity::class.java)
                intent.putExtra("explainTitle", contentDTOs2!![position].explainTitle)
                intent.putExtra("explain", contentDTOs2!![position].explain)
                intent.putExtra("imageUrl", contentDTOs2!![position].imageUrl)
                startActivity(intent)
            }


        }

        fun favoriteEvent(position: Int) {
            var tsDoc = firestore?.collection("images2")?.document(contentUidList[position])
            firestore?.runTransaction { transaction ->

                var contentDTO2 = transaction.get(tsDoc!!).toObject(ContentDTO2::class.java)

                if (contentDTO2!!.favorites.containsKey(uid)) {
                    // '좋아요' 버튼이 클릭되어 있을 때(취소하는 이벤트 발생)
                    contentDTO2?.favoriteCount = contentDTO2?.favoriteCount - 1
                    contentDTO2?.favorites.remove(uid)
                } else {
                    // '좋아요' 버튼이 클릭되어 있지 않을 때(클릭하는 이벤트 발생)
                    contentDTO2?.favoriteCount = contentDTO2?.favoriteCount + 1
                    contentDTO2?.favorites[uid!!] = true
                }
                transaction.set(tsDoc, contentDTO2)
            }


        }
    }


}