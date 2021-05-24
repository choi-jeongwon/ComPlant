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
import kotlinx.android.synthetic.main.fragment_message_list.view.*
import kotlinx.android.synthetic.main.item_message.view.*

class MessageListFragment : Fragment() {
    var mainActivity: MainActivity? = null
    var firestore : FirebaseFirestore? = null

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



        view.fragment_message_recyclerview.adapter = MessageListRecyclerViewAdapter()
        view.fragment_message_recyclerview.layoutManager = LinearLayoutManager(activity)

        return view
    }

    inner class MessageListRecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>(){
        var messageDTOs : ArrayList<MessageDTO> = arrayListOf()

        init {
            firestore?.collection("messages")?.orderBy("timestamp")
                ?.addSnapshotListener { querySnapshot,  firebaseFirestoreException ->

                    messageDTOs.clear()

                    // querySnapshot이 null일 경우, 바로 종료시킨다.
                    if(querySnapshot == null) return@addSnapshotListener

                    for(snapshot in querySnapshot.documents!!){
                        messageDTOs.add(snapshot.toObject(MessageDTO::class.java)!!)
                    }
                    notifyDataSetChanged()
                }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            var view = LayoutInflater.from(parent.context).inflate(R.layout.item_message, parent,false)
            return CustomViewHolder(view)
        }

        inner class CustomViewHolder(view: View) : RecyclerView.ViewHolder(view)

        override fun getItemCount(): Int {
            return messageDTOs.size
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            var viewholder = holder.itemView

            viewholder.message_day.text = messageDTOs[position].date
            viewholder.message_start_time.text = messageDTOs[position].startTime
            viewholder.message_end_time.text = messageDTOs[position].endTime

        }
    }
}