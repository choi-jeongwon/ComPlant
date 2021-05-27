package com.example.complant.navigation

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.complant.MainActivity
import com.example.complant.R
import com.example.complant.navigation.model.ContentDTO
import com.example.complant.navigation.model.MessageDTO
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_diary.view.*
import kotlinx.android.synthetic.main.fragment_message_list.*
import kotlinx.android.synthetic.main.fragment_message_list.view.*
import kotlinx.android.synthetic.main.item_message.view.*

class MessageListFragment : Fragment() {
    var mainActivity: MainActivity? = null
    var firestore : FirebaseFirestore? = null

    var str1 : String? = null
    var str2 : String? = null
    var str3 : String? = null
    var str4 : String? = null

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
        var view = LayoutInflater.from(activity).inflate(R.layout.fragment_message_list, container, false)
        firestore = FirebaseFirestore.getInstance()

        view.btn_new_message?.setOnClickListener {
            mainActivity?.goMessageSettingFragment()
        }

        str1 = arguments?.getString("message_date")
        str2 = arguments?.getString("message_start_time")
        str3 = arguments?.getString("message_end_time")
        str4 = arguments?.getString("message_contents_input")

        view.date.text = str1
        view.start.text = str2
        view.end.text = str3
        view.contentssss.text = str4




        view.fragment_message_recyclerview.adapter = MessageListRecyclerViewAdapter()
        view.fragment_message_recyclerview.layoutManager = LinearLayoutManager(activity)

        return view
    }

    inner class MessageListRecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>(){
        // messageDTO 클래스 ArrayList 생성
        var messageDTOs : ArrayList<MessageDTO> = arrayListOf()

        init {
            // firestore?.collection("messages")?.orderBy("timestamp")
            //                ?.addSnapshotListener { querySnapshot,  firebaseFirestoreException ->
            firestore?.collection("messages")?.addSnapshotListener { querySnapshot,  firebaseFirestoreException ->
                    // 배열 비움
                    messageDTOs.clear()

                    // querySnapshot이 null일 경우, 바로 종료시킨다.
                    if(querySnapshot == null) return@addSnapshotListener

                    for(snapshot in querySnapshot!!.documents){
                        var item = snapshot.toObject(MessageDTO::class.java)
                        messageDTOs.add(item!!)
                    }
                    notifyDataSetChanged()
                }
        }

        // xml 파일을 inflate하여 ViewHolder를 생성
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            var view = LayoutInflater.from(parent.context).inflate(R.layout.item_message, parent,false)
            return CustomViewHolder(view)
        }

        inner class CustomViewHolder(view: View) : RecyclerView.ViewHolder(view)

        override fun getItemCount(): Int {
            return messageDTOs.size
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            var viewholder = (holder as CustomViewHolder).itemView

            viewholder.message_day.text = messageDTOs!![position].date
            viewholder.message_start_time.text = messageDTOs!![position].startTime
            viewholder.message_end_time.text = messageDTOs!![position].endTime
            viewholder.message_contents.text = messageDTOs!![position].content


        }
    }
}