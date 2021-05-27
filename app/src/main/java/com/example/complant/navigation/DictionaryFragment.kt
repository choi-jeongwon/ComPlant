package com.example.complant.navigation

import android.content.Intent
import android.os.Bundle
import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.complant.R
import com.example.complant.navigation.model.ContentDTO
import com.example.complant.navigation.model.DictionaryDTO
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_dictionary.*
import kotlinx.android.synthetic.main.fragment_dictionary.view.*
//import kotlinx.android.synthetic.main.fragment_dictionary.view.*
//import kotlinx.android.synthetic.main.fragment_dictionary.view.*
import kotlinx.android.synthetic.main.item_dictionary.view.*



class DictionaryFragment : Fragment() {

    var firestore : FirebaseFirestore? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = LayoutInflater.from(activity).inflate(R.layout.fragment_dictionary, container, false)

        // 파이어스토어 인스턴스 초기화
        firestore = FirebaseFirestore.getInstance()

        view.fragment_dictionary_recyclerview.adapter = DictionaryRecyclerViewAdapter()
        view.fragment_dictionary_recyclerview.layoutManager = LinearLayoutManager(activity)


        return view
    }

    inner class DictionaryRecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        // DictionaryDTO  클래스 ArrayList 생성
        var dictionaryDTOs: ArrayList<DictionaryDTO> = arrayListOf()


        init { // plantDictionary 의 문서를 불러온 뒤 DictionaryDTO 으로 변환해 ArrayList 에 담음

            firestore?.collection("plantDictionary")
                ?.addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                    // ArrayList 비워줌
                    dictionaryDTOs.clear()

//                // Sometimes, This code return null of querySnapshot when it signout
//                if (querySnapshot == null) return@addSnapshotListener

                    for (snapshot in querySnapshot!!.documents) {
                        var item = snapshot.toObject(DictionaryDTO::class.java)
                        dictionaryDTOs.add(item!!)
                    }
                    notifyDataSetChanged()
                }
        }

        // xml 파일을 inflate 하여 ViewHolder 를 생성
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            var view =
                LayoutInflater.from(parent.context).inflate(R.layout.item_dictionary, parent, false)
            return CustomViewHolder(view)
        }

        inner class CustomViewHolder(view: View) : RecyclerView.ViewHolder(view)

        // 리사이클러뷰의 아이템 총 개수 반환
        override fun getItemCount(): Int {
            return dictionaryDTOs.size
        }

        // onCreateViewHolder 에서 만든 view와 실제 데이터를 연결
        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            var viewholder = (holder as CustomViewHolder).itemView

            viewholder.plant_name.text = dictionaryDTOs!![position].plant_name


            viewholder.dictionary_cardview.setOnClickListener { v ->
                var intent = Intent(v.context, DetailDictionaryActivity::class.java)
                startActivity(intent)
            }
        }




    }



}






/*

//class DiaryFragment : Fragment() {
//
//    var firestore : FirebaseFirestore? = null
//    var uid : String? = null
//
//    //    일지작성 버튼 내용 시작
//    var mainActivity: MainActivity? = null
//
//    override fun onAttach(context: Context) {
//        super.onAttach(context)
//        mainActivity = activity as MainActivity?
//    }
//
//    override fun onDetach() {
//        super.onDetach()
//        mainActivity = null
//    }
//
//
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        var view =
//            LayoutInflater.from(activity).inflate(R.layout.fragment_diary, container, false)
//        val diary_btn_write = view.findViewById<View>(R.id.diary_btn_write) as Button
//        diary_btn_write.setOnClickListener {
//            mainActivity?.onChangeAddPhotoActivity()
//        }
//
//        firestore = FirebaseFirestore.getInstance()
//        uid = FirebaseAuth.getInstance().currentUser?.uid
//
//        view.fragment_diary_recyclerview.adapter = DiaryRecyclerViewAdapter()
//        view.fragment_diary_recyclerview.layoutManager = LinearLayoutManager(activity)

//        return view
//    }
//    //    일지작성 버튼 내용 끝
//
//    // diary RecyclerView
//    inner class DiaryRecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>(){
//        var contentDTOs : ArrayList<ContentDTO> = arrayListOf()
//        var contentUidList : ArrayList<String> = arrayListOf()
//
//        init {


//            firestore?.collection("images")?.orderBy("timestamp")
//                ?.addSnapshotListener { querySnapshot,  firebaseFirestoreException ->
//                    contentDTOs.clear()
//                    contentUidList.clear()
//                    // Sometimes, This code return null of querySnapshot when it signout
//                    if (querySnapshot == null) return@addSnapshotListener
//
//                    for (snapshot in querySnapshot!!.documents) {
//                        var item = snapshot.toObject(ContentDTO::class.java)
//                        contentDTOs.add(item!!)
//                        contentUidList.add(snapshot.id)
//                    }
//                    notifyDataSetChanged()
//                }
//        }

//        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
//            var view = LayoutInflater.from(parent.context).inflate(R.layout.item_diary,parent,false)
//            return CustomViewHolder(view)
//        }
//
//        inner class CustomViewHolder(view: View) : RecyclerView.ViewHolder(view)

//        override fun getItemCount(): Int {
//            return contentDTOs.size
//        }
//
//        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
//            var viewholder = (holder as CustomViewHolder).itemView

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



            //Profile Image 가져오기(14강 6:26)
            firestore?.collection("profileImages")?.document(contentDTOs[position].uid!!)
                ?.get()?.addOnCompleteListener { task ->
                    if (task.isSuccessful) {

                        val url = task.result!!["image"]
                        Glide.with(holder.itemView.context).load(url).apply(RequestOptions().circleCrop()).into(viewholder.diaryitem_profile_image)
                    }
                }


            //MyPageFragment 로 이동
            // 상대방의 프로필 이미지를 클릭하면 상대방의 유저 정보로 이동
            viewholder.diaryitem_profile_image.setOnClickListener {

                val fragment = MyPageFragment()
                val bundle = Bundle()

                bundle.putString("destinationUid", contentDTOs[position].uid)  // 이전 페이지에서 넘어온 값
                bundle.putString("userId", contentDTOs[position].userId)   // 이메일 값

                fragment.arguments = bundle
                activity?.supportFragmentManager?.beginTransaction()?.replace(R.id.main_content, fragment)?.commit()
            }


            viewholder.diaryitem_comment_imageview.setOnClickListener { v ->
                var intent = Intent(v.context, CommentActivity::class.java)
                intent.putExtra("contentUid", contentUidList[position])
                startActivity(intent)
            }


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
 */