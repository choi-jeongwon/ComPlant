package com.example.complant.navigation

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.complant.MainActivity
import com.example.complant.R
import com.example.complant.navigation.model.MainPageDTO
import com.example.complant.navigation.model.MessageDTO
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_home.view.*


class HomeFragment : Fragment() {
    var mainActivity: MainActivity? = null
    var firestore: FirebaseFirestore? = null
    var auth: FirebaseAuth? = null
    var uid1 : String? = null

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
        uid1 = FirebaseAuth.getInstance().currentUser?.uid

        //var mainMessageInfo = MainPageDTO()

        view.main_account_button.setOnClickListener {
            mainActivity?.goMyPage()
        }

//        var str : String? = arguments?.getString("content")
//        mainMessageInfo.message = str
//        mainMessageInfo.uid = uid1
//
//        if (mainMessageInfo.message != null) {
//            firestore?.collection("mainPage")?.document(mainMessageInfo.uid.toString())
//                ?.set(mainMessageInfo)
//
//        }

        firestore?.collection("mainPage")?.document(uid1!!)?.addSnapshotListener { snapshot, e ->
            if(snapshot == null) return@addSnapshotListener
            var mainPageDTO = snapshot.toObject(MainPageDTO::class.java)
            if (mainPageDTO?.message != null) {
                view.speech_box_text.setText(mainPageDTO?.message)
            }

        }

        return view
    }

}