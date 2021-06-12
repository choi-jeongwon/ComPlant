package com.example.complant.navigation

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.complant.MainActivity
import com.example.complant.R

class MyPagePostFragment : Fragment() {
    var mainActivity: MainActivity? = null
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
        var view = LayoutInflater.from(activity).inflate(R.layout.fragment_my_page_post, container, false)
        return view

    }
}