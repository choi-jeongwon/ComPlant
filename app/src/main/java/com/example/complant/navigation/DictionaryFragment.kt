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
import kotlinx.android.synthetic.main.activity_detail_dictionary.*
import kotlinx.android.synthetic.main.activity_detail_dictionary.view.*
import kotlinx.android.synthetic.main.fragment_dictionary.*
import kotlinx.android.synthetic.main.fragment_dictionary.view.*
import kotlinx.android.synthetic.main.item_dictionary.*
import kotlinx.android.synthetic.main.item_dictionary.plant_name
//import kotlinx.android.synthetic.main.fragment_dictionary.view.*
//import kotlinx.android.synthetic.main.fragment_dictionary.view.*
import kotlinx.android.synthetic.main.item_dictionary.view.*
import kotlinx.android.synthetic.main.item_dictionary.view.plant_name


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
        var plantDictionary: ArrayList<DictionaryDTO> = arrayListOf()

        init { // plantDictionary 의 문서를 불러온 뒤 DictionaryDTO 으로 변환해 ArrayList 에 담음

            firestore?.collection("plantDictionary")
                ?.addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                    // ArrayList 비워줌
                    plantDictionary.clear()

//                // Sometimes, This code return null of querySnapshot when it signout
//                if (querySnapshot == null) return@addSnapshotListener

                    for (snapshot in querySnapshot!!.documents) {
                        var item = snapshot.toObject(DictionaryDTO::class.java)
                        plantDictionary.add(item!!)
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

        // onCreateViewHolder 에서 만든 view와 실제 데이터를 연결
        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            var viewholder = (holder as CustomViewHolder).itemView

            viewholder.plant_name.text = plantDictionary[position].plant_name
            viewholder.plant_eng_name.text = plantDictionary[position].plant_eng_name


            // 검색 옵션 변수
            var searchOption = "plant_name"

            // 스피너 옵션에 따른 동작
            spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {
                }

                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    when (spinner.getItemAtPosition(position)) {
                        "식물 이름" -> {
                            searchOption = "plant_name"
                        }
                        "식물 영어이름" -> {
                            searchOption = "plant_eng_name"
                        }
                    }
                }
            }

            // 검색 옵션에 따라 검색
            searchBtn.setOnClickListener {
                (fragment_dictionary_recyclerview.adapter as DictionaryRecyclerViewAdapter).search(searchWord.text.toString(), searchOption)

            }







            viewholder.dictionary_cardview.setOnClickListener { v ->
                var intent = Intent(v.context, DetailDictionaryActivity::class.java)
                intent.putExtra("plant_name", plantDictionary!![position].plant_name)
                intent.putExtra("plant_image", plantDictionary!![position].plant_image)
                intent.putExtra("plant_water_cycle", plantDictionary!![position].plant_water_cycle)
                intent.putExtra("plant_explain", plantDictionary!![position].plant_explain)
                startActivity(intent)
            }

        }

        // 리사이클러뷰의 아이템 총 개수 반환
        override fun getItemCount(): Int {
            return plantDictionary.size
        }


        // 파이어스토어에서 데이터를 불러와서 검색어가 있는지 판단
        fun search(searchWord : String, option : String) {
            firestore?.collection("plantDictionary")?.addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                // ArrayList 비워줌
                plantDictionary.clear()

                for (snapshot in querySnapshot!!.documents) {
                    if (snapshot.getString(option)!!.contains(searchWord)) {
                        var item = snapshot.toObject(DictionaryDTO::class.java)
                        plantDictionary.add(item!!)
                    }
                }
                notifyDataSetChanged()
            }
        }


    }



}






