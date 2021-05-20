package com.example.complant.navigation

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.complant.MainActivity
import com.example.complant.R
import com.example.complant.navigation.model.ContentDTO
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.android.synthetic.main.activity_add_photo.*
import kotlinx.android.synthetic.main.fragment_diary.view.*
import kotlinx.android.synthetic.main.item_diary.view.*


class DiaryFragment : Fragment() {

    var firestore : FirebaseFirestore? = null
    var uid : String? = null

    //    일지작성 버튼 내용 시작
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
            LayoutInflater.from(activity).inflate(R.layout.fragment_diary, container, false)
        val diary_btn_write = view.findViewById<View>(R.id.diary_btn_write) as Button
        diary_btn_write.setOnClickListener {
            mainActivity?.onChangeAddPhotoActivity()
        }

        firestore = FirebaseFirestore.getInstance()
        uid = FirebaseAuth.getInstance().currentUser?.uid

        view.fragment_diary_recyclerview.adapter = DiaryRecyclerViewAdapter()
        view.fragment_diary_recyclerview.layoutManager = LinearLayoutManager(activity)

        return view
    }
    //    일지작성 버튼 내용 끝

    // diary RecyclerView
    inner class DiaryRecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>(){
        var contentDTOs : ArrayList<ContentDTO> = arrayListOf()
        var contentUidList : ArrayList<String> = arrayListOf()

        init {


            firestore?.collection("images")?.orderBy("timestamp")?.addSnapshotListener { value: QuerySnapshot?, error: FirebaseFirestoreException? ->
                contentDTOs.clear()
                contentUidList.clear()
                for (snapshot in value!!.documents){
                    var item = snapshot.toObject(ContentDTO::class.java)
                    contentDTOs.add(item!!)
                    contentUidList.add(snapshot.id)
                }
                notifyDataSetChanged()
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            var view = LayoutInflater.from(parent.context).inflate(R.layout.item_diary,parent,false)
            return CustomViewHolder(view)
        }

        inner class CustomViewHolder(view: View) : RecyclerView.ViewHolder(view)

        override fun getItemCount(): Int {
            return contentDTOs.size
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            var viewholder = (holder as CustomViewHolder).itemView

            //UserId
            viewholder.diaryitem_profile_textview.text = contentDTOs!![position].userId

            //Image
            Glide.with(holder.itemView.context).load(contentDTOs!![position].imageUrl).into(viewholder.diaryitem_imageview_content)

            //Explain of content
            viewholder.diaryitem_explain_textview.text = contentDTOs!![position].explain

            //likes
            viewholder.diaryitem_favoritecounter_textview.text = "Likes " + contentDTOs!![position].favoriteCount

            //'좋아요'버튼이 눌렸을 때의 이벤트
            viewholder.diaryitem_favorite_imageview.setOnClickListener {
                favoriteEvent(position)
            }

            //'좋아요'카운터와 하트가 색칠되거나 비어있는 이벤트
            if(contentDTOs!![position].favorites.containsKey(uid)){
                //'좋아요'버튼 클릭한 부분
                viewholder.diaryitem_favorite_imageview.setImageResource(R.drawable.ic_favorite)
            }else{
                //'좋아요'버튼 아직 클릭하지 않은 경우
                viewholder.diaryitem_favorite_imageview.setImageResource(R.drawable.ic_favorite_border)
            }

//            //ProfileImage
//            Glide.with(holder.itemView.context).load(contentDTOs!![position].imageUrl).into(viewholder.diaryitem_profile_image)

        }
        fun favoriteEvent(position: Int){
            var tsDoc = firestore?.collection("images")?.document(contentUidList[position])
            firestore?.runTransaction { transaction ->

                var contentDTO = transaction.get(tsDoc!!).toObject(ContentDTO::class.java)

                if(contentDTO!!.favorites.containsKey(uid)){
                    // '좋아요' 버튼이 클릭되어 있을 때(취소하는 이벤트 발생)
                    contentDTO?.favoriteCount = contentDTO?.favoriteCount - 1
                    contentDTO?.favorites.remove(uid)
                }else {
                    // '좋아요' 버튼이 클릭되어 있지 않을 때(클릭하는 이벤트 발생)
                    contentDTO?.favoriteCount = contentDTO?.favoriteCount + 1
                    contentDTO?.favorites[uid!!] = true
                }
                transaction.set(tsDoc,contentDTO)
            }


        }
    }

}



