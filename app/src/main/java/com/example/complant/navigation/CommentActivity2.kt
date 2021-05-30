package com.example.complant.navigation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.complant.R
import com.example.complant.navigation.model.ContentDTO
import com.example.complant.navigation.model.ContentDTO2
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_comment.*
import kotlinx.android.synthetic.main.activity_comment2.*
import kotlinx.android.synthetic.main.item_comment.view.*
import kotlinx.android.synthetic.main.item_comment2.view.*

class CommentActivity2 : AppCompatActivity() {
    var contentUid : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comment2)
        contentUid = intent.getStringExtra("contentUid")

        comment2_recyclerview.adapter = Comment2RecyclerviewAdapter()
        comment2_recyclerview.layoutManager = LinearLayoutManager(this)

        comment2_btn_send?.setOnClickListener {
            var comment2 = ContentDTO2.Comment()
            comment2.userId = FirebaseAuth.getInstance().currentUser?.email
            comment2.uid = FirebaseAuth.getInstance().currentUser?.uid
            comment2.comment = comment2_edit_message.text.toString()
            comment2.timestamp = System.currentTimeMillis()

            FirebaseFirestore.getInstance()
                .collection("images2")
                .document(contentUid!!)
                .collection("comments")
                .document()
                .set(comment2)

            comment2_edit_message.setText("")
        }
    }

    inner class Comment2RecyclerviewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

        var comments : ArrayList<ContentDTO2.Comment> = arrayListOf()
        init {
            FirebaseFirestore.getInstance()
                .collection("images2")
                .document(contentUid!!)
                .collection("comments")
                .orderBy("timestamp")
                .addSnapshotListener { querySnapshot,  firebaseFirestoreException ->
                    comments.clear()
                    if(querySnapshot == null)return@addSnapshotListener

                    for(snapshot in querySnapshot.documents!!){
                        comments.add(snapshot.toObject(ContentDTO2.Comment::class.java)!!)
                    }
                    notifyDataSetChanged()
                }
        }
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            var view = LayoutInflater.from(parent.context).inflate(R.layout.item_comment2,parent,false)
            return CustomViewHolder(view)
        }

        private inner class CustomViewHolder(view : View) : RecyclerView.ViewHolder(view)

        override fun getItemCount(): Int {
            return comments.size
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            var view = holder.itemView

            view.commentviewitem2_textview_comment.text = comments[position].comment
            view.commentviewitem2_textview_profile.text = comments[position].userId

            //댓글 옆에 Profile Image 가져오기
            FirebaseFirestore.getInstance()?.collection("profileImages")?.document(comments[position].uid!!)
                ?.get()?.addOnCompleteListener { task ->
                    if (task.isSuccessful) {

                        val url = task.result!!["image"]
                        Glide.with(holder.itemView.context).load(url).apply(RequestOptions().circleCrop()).into(view.commentviewitem2_imageview_profile)
                    }
                }



        }

    }


}