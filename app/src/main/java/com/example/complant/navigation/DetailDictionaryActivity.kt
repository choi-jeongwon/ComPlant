package com.example.complant.navigation

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.complant.R
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.android.synthetic.main.activity_detail_dictionary.*
import kotlinx.android.synthetic.main.fragment_dictionary.*
import kotlinx.android.synthetic.main.fragment_dictionary.view.*
import kotlinx.android.synthetic.main.item_comment.view.*
import kotlinx.android.synthetic.main.item_dictionary.*

class DetailDictionaryActivity : AppCompatActivity() {
    var plant_name : String? = null
    var plant_water_cycle: String? = null
    var plant_explain: String? = null
    var plant_image: String? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_dictionary)


        plant_name = intent.getStringExtra("plant_name")
        plant_water_cycle = intent.getStringExtra("plant_water_cycle")
        plant_explain = intent.getStringExtra("plant_explain")
        plant_image = intent.getStringExtra("plant_image")

        plant_name_layout.setText(plant_name)
        plant_water_cycle_layout.setText(plant_water_cycle)
        plant_explain_layout.setText(plant_explain)

        Glide.with(this).load(plant_image).into(plant_image_layout)




//        FirebaseFirestore.getInstance()?.collection("plantDictionary")?.document()?.get()?.addOnCompleteListener { task ->
//            if(task.isSuccessful) {
//
//                val plant_image = task.result!!["plant_image"]
//                Glide.with(this).load(plant_image).into(plant_image_layout)
//            }
//        }
//        plant_image_layout.setImageURI()


    }



}