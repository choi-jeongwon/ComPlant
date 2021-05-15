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
import com.example.complant.MainActivity
import com.example.complant.R
import kotlinx.android.synthetic.main.activity_add_photo.*


class DiaryFragment : Fragment() {
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
        var rootview =
            LayoutInflater.from(activity).inflate(R.layout.fragment_diary, container, false) as ViewGroup
        val diary_btn_write = rootview.findViewById<View>(R.id.diary_btn_write) as Button
        diary_btn_write.setOnClickListener {
            mainActivity?.onChangeAddPhotoActivity()
        }
        return rootview
    }




}



