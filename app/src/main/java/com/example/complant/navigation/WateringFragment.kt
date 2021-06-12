package com.example.complant.navigation

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.complant.MainActivity
import com.example.complant.R
import com.example.complant.navigation.model.WateringDTO
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_watering.view.*
import java.util.*

class WateringFragment : Fragment() {
    var mainActivity: MainActivity? = null
    var firestore: FirebaseFirestore? = null
    var auth: FirebaseAuth? = null
    var uid: String? = null

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
            LayoutInflater.from(activity).inflate(R.layout.fragment_watering, container, false)
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        var wateringDTO = WateringDTO()

        wateringDTO.uid = FirebaseAuth.getInstance().currentUser?.uid

        view.btn_watering_start_date?.setOnClickListener {
            var calendar = Calendar.getInstance()
            var year = calendar.get(Calendar.YEAR)
            var month = calendar.get(Calendar.MONTH)
            var day = calendar.get(Calendar.DAY_OF_MONTH)

            var listener = DatePickerDialog.OnDateSetListener { datePicker, i, i2, i3 ->
                if (i2 + 1 < 10 && i3 < 10) {
                    view.btn_watering_start_date.setText("${i}-0${i2 + 1}-0${i3}")
                } else if (i2 + 1 < 10)
                    view.btn_watering_start_date.setText("${i}-0${i2 + 1}-${i3}")
                else if (i3 < 10)
                    view.btn_watering_start_date.setText("${i}-${i2 + 1}-0${i3}")
                else
                    view.btn_watering_start_date.setText("${i}-${i2 + 1}-${i3}")

                wateringDTO.wateringStartYear = i
                wateringDTO.wateringStartMonth = i2 + 1
                wateringDTO.wateringStartDay = i3
            }

            var picker = DatePickerDialog(view.context, listener, year, month, day)
            picker.show()
        }

        view.btn_watering_contents_update?.setOnClickListener {
            wateringDTO.wateringIntervalDay =
                Integer.parseInt(view.watering_interval.text.toString())

            // 파이어베이스 DB에 등록
            if (wateringDTO.wateringStartYear != null && wateringDTO.wateringStartMonth != null && wateringDTO.wateringStartDay != null && wateringDTO.wateringIntervalDay != null) {
                firestore?.collection("watering")?.document(wateringDTO?.uid.toString())
                    ?.set(wateringDTO)
            }

            // 완료 하며 뒤로 가기
            mainActivity?.goBack()
        }
        return view
    }
}