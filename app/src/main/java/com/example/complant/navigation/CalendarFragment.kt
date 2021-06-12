package com.example.complant.navigation

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CalendarView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.complant.MainActivity
import com.example.complant.R
import com.example.complant.navigation.model.CalendarDTO
import com.example.complant.navigation.model.WateringDTO
import com.example.complant.navigation.model.MainPageDTO
import com.example.complant.navigation.model.MessageDTO
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import com.prolificinteractive.materialcalendarview.spans.DotSpan
import kotlinx.android.synthetic.main.fragment_calendar.view.*
import kotlinx.android.synthetic.main.fragment_home.view.*
import kotlinx.android.synthetic.main.item_calendar.view.*
import java.util.*
import kotlin.collections.HashSet

class CalendarFragment : Fragment() {
    var mainActivity: MainActivity? = null
    var firestore: FirebaseFirestore? = null
    var auth: FirebaseAuth? = null
    var uid: String? = null

    var wateredCalendarInfo = CalendarDTO.Watered()

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
            LayoutInflater.from(activity).inflate(R.layout.fragment_calendar, container, false)
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        uid = FirebaseAuth.getInstance().currentUser?.uid
        var date: String? = null

        var timeCalendar = Calendar.getInstance()
        val currentYear = timeCalendar.get(Calendar.YEAR)
        val currentMonth = timeCalendar.get(Calendar.MONTH) + 1
        val currentDate = timeCalendar.get(Calendar.DATE)

        // 초기화면으로 오늘 날짜를 선택 해서 보여준다.
        view.calendar_view.setSelectedDate(CalendarDay.today())

        view.calendar_view.addDecorators(
            SundayDecorator(), // 일요일을 빨간색으로 표시
            SaturdayDecorator(), // 토요일은 파란색으로 표시
            //MinDecorator(CalendarDay.today()). // 같은 달 중에 이미 지난 날들은 회색처리
            toDayDecorator() // 오늘 날짜 크기를 키우고 굵은 색으로 표시
            //EventDecorator(Color.RED, Collections.singleton(CalendarDay.today())), // 특정 날짜 점 찍기
            //EventDecorator(Color.BLUE, Collections.singleton(CalendarDay.from(2021,6 - 1,12))) // 특정 날짜 점 찍기
        )

        view.calendar_view.setOnDateChangedListener { widget, date, selected ->
            // 달력의 오늘 날짜를 클릭했을 때
            if (date == CalendarDay.today()) {
                var today: String? =
                    currentYear.toString() + "년 " + currentMonth.toString() + "월 " + currentDate.toString() + "일"

                // 다이얼로그
                var builder = AlertDialog.Builder(context)
                builder.setTitle(today)
                builder.setMessage("오늘 물을 주었나요?")
                //builder.setIcon(R.mipmap.ic_launcher)

                // 버튼 클릭시에 무슨 작업을 할 것인가!
                var listener = object : DialogInterface.OnClickListener {
                    override fun onClick(p0: DialogInterface?, p1: Int) {
                        when (p1) {
                            DialogInterface.BUTTON_POSITIVE -> {
                                wateredCalendarInfo.uid = uid
                                wateredCalendarInfo.timestamp = System.currentTimeMillis()
                                wateredCalendarInfo.wateredYear = currentYear
                                wateredCalendarInfo.wateredMonth = currentMonth
                                wateredCalendarInfo.wateredDay = currentDate

                                if (currentMonth < 10 && currentDate < 10) {
                                    wateredCalendarInfo.wateredString =
                                        currentYear.toString() + "0" + currentMonth.toString() + "0" + currentDate.toString()
                                } else if (currentMonth < 10) {
                                    wateredCalendarInfo.wateredString =
                                        currentYear.toString() + "0" + currentMonth.toString() + currentDate.toString()
                                } else if (currentDate < 10) {
                                    wateredCalendarInfo.wateredString =
                                        currentYear.toString() + currentMonth.toString() + "0" + currentDate.toString()
                                } else {
                                    wateredCalendarInfo.wateredString =
                                        currentYear.toString() + currentMonth.toString() + currentDate.toString()
                                }

                                if (wateredCalendarInfo.uid != null && wateredCalendarInfo.wateredYear != null && wateredCalendarInfo.wateredMonth != null && wateredCalendarInfo.wateredDay != null) {
                                    firestore?.collection("calendar")?.document(uid!!)
                                        ?.collection("userWatered")
                                        ?.document(wateredCalendarInfo.wateredString!!)
                                        ?.set(wateredCalendarInfo)
                                }
                            }

                            DialogInterface.BUTTON_NEGATIVE -> {
                                // CalendarFragment -> WateringFragment 이동
                                var wateringFragment = WateringFragment()
                                activity?.supportFragmentManager?.beginTransaction()
                                    ?.add(R.id.main_content, wateringFragment)
                                    ?.addToBackStack("settingFragment")?.commit()
                            }
                        }
                    }
                }

                builder.setPositiveButton("예", listener)
                builder.setNegativeButton("아니오 (미루기)", listener)

                builder.show()
            }
        }

        // 주기 설정 버튼 클릭 시 wateringFragment로 이동
        view.btn_watering_setting.setOnClickListener {
            // CalendarFragment -> WateringFragment 이동
            var wateringFragment = WateringFragment()
            activity?.supportFragmentManager?.beginTransaction()
                ?.add(R.id.main_content, wateringFragment)
                ?.addToBackStack("settingFragment")?.commit()
        }

        firestore?.collection("watering")?.document(uid!!)?.addSnapshotListener { snapshot, e ->
            if (snapshot == null) return@addSnapshotListener
            var wateringDTO = snapshot.toObject(WateringDTO::class.java)

            if (wateringDTO?.wateringStartYear != null && wateringDTO?.wateringStartMonth != null && wateringDTO?.wateringStartDay != null && wateringDTO?.wateringIntervalDay != null) {

                if (wateringDTO?.wateringStartMonth!! < 10 && wateringDTO?.wateringStartDay!! < 10) {
                    date =
                        "물 주기 시작 : " + wateringDTO.wateringStartYear.toString() + "-0" + wateringDTO.wateringStartMonth.toString() + "-0" + wateringDTO.wateringStartDay.toString()
                } else if (wateringDTO?.wateringStartMonth!! < 10) {
                    date =
                        "물 주기 시작 : " + wateringDTO.wateringStartYear.toString() + "-0" + wateringDTO.wateringStartMonth.toString() + "-" + wateringDTO.wateringStartDay.toString()
                } else if (wateringDTO?.wateringStartDay!! < 10) {
                    date =
                        "물 주기 시작 : " + wateringDTO.wateringStartYear.toString() + "-" + wateringDTO.wateringStartMonth.toString() + "-0" + wateringDTO.wateringStartDay.toString()
                } else {
                    date =
                        "물 주기 시작 : " + wateringDTO.wateringStartYear.toString() + "-" + wateringDTO.wateringStartMonth.toString() + "-" + wateringDTO.wateringStartDay.toString()
                }

                view.calendar_date.setText(date)

                var interval: String? =
                    wateringDTO.wateringIntervalDay.toString() + "일에 한 번 물을 줍니다."
                view.calendar_interval.setText(interval)
            }
        }

        view.fragment_calendar.adapter = CalendarRecyclerViewAdaper()
        view.fragment_calendar.layoutManager = LinearLayoutManager(activity)

        return view
    }

    inner class CalendarRecyclerViewAdaper : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        // CalendarDTO 클래스 ArrayList 생성
        var calendarDTOs: ArrayList<CalendarDTO.Watered> = arrayListOf()
        var stringDate: String? = null

        init {
            firestore?.collection("calendar")
                ?.document(uid!!)
                ?.collection("userWatered")
                ?.orderBy("wateredString", Query.Direction.DESCENDING)
                ?.addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                    // 배열 비움
                    calendarDTOs.clear()

                    // querySnapshot이 null일 경우, 바로 종료시킨다.
                    if (querySnapshot == null) return@addSnapshotListener

                    for (snapshot in querySnapshot!!.documents) {
                        var item = snapshot.toObject(CalendarDTO.Watered::class.java)
                        calendarDTOs.add(item!!)
                    }
                    notifyDataSetChanged()
                }
        }

        // xml 파일을 inflate하여 ViewHolder를 생성
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            var view =
                LayoutInflater.from(parent.context).inflate(R.layout.item_calendar, parent, false)
            return CustomViewHolder(view)
        }

        inner class CustomViewHolder(view: View) : RecyclerView.ViewHolder(view)

        override fun getItemCount(): Int {
            return calendarDTOs.size
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            var viewholder = (holder as CustomViewHolder).itemView

            if (calendarDTOs!![position].wateredMonth!! < 10 && calendarDTOs!![position].wateredDay!! < 10) {
                stringDate =
                    calendarDTOs!![position].wateredYear.toString() + ".0" + calendarDTOs!![position].wateredMonth.toString() + ".0" + calendarDTOs!![position].wateredDay.toString();
            } else if (calendarDTOs!![position].wateredMonth!! < 10) {
                stringDate =
                    calendarDTOs!![position].wateredYear.toString() + ".0" + calendarDTOs!![position].wateredMonth.toString() + "." + calendarDTOs!![position].wateredDay.toString();
            } else if (calendarDTOs!![position].wateredDay!! < 10) {
                stringDate =
                    calendarDTOs!![position].wateredYear.toString() + "." + calendarDTOs!![position].wateredMonth.toString() + ".0" + calendarDTOs!![position].wateredDay.toString();
            } else {
                stringDate =
                    calendarDTOs!![position].wateredYear.toString() + "." + calendarDTOs!![position].wateredMonth.toString() + "." + calendarDTOs!![position].wateredDay.toString();
            }

            viewholder.watered_date.text = stringDate

        }
    }
}

// 토요일을 파란색으로 변경
class SaturdayDecorator : DayViewDecorator {
    var calendar: Calendar = Calendar.getInstance()

    override fun shouldDecorate(day: CalendarDay?): Boolean {
        day?.copyTo(calendar)
        var weekDay: Int? = calendar.get(Calendar.DAY_OF_WEEK)
        return weekDay == Calendar.SATURDAY
    }

    override fun decorate(view: DayViewFacade?) {
        view?.addSpan(ForegroundColorSpan(Color.BLUE))
    }
}

// 일요일을 빨간색으로 변경
class SundayDecorator : DayViewDecorator {
    var calendar: Calendar = Calendar.getInstance()

    override fun shouldDecorate(day: CalendarDay?): Boolean {
        day?.copyTo(calendar)
        var weekDay: Int? = calendar.get(Calendar.DAY_OF_WEEK)
        return weekDay == Calendar.SUNDAY
    }

    override fun decorate(view: DayViewFacade?) {
        view?.addSpan(ForegroundColorSpan(Color.RED))
    }
}

// 오늘 날짜를 굵게 표시
class toDayDecorator : DayViewDecorator {
    var date: CalendarDay = CalendarDay.today()

    override fun shouldDecorate(day: CalendarDay?): Boolean {
        return day!!.equals(date)
    }

    override fun decorate(view: DayViewFacade?) {
        view?.addSpan(StyleSpan(Typeface.BOLD))
        view?.addSpan(RelativeSizeSpan(1.7f))
    }

    fun setDate(date: Date) {
        this.date = CalendarDay.from(date)
    }
}

// 같은 달 중에 오늘보다 지난 날들은 회색처리
//class MinDecorator(min: CalendarDay) : DayViewDecorator {
//    val minDay = min
//    override fun shouldDecorate(day: CalendarDay?): Boolean {
//        return day?.month == minDay.month && day.day < minDay.day
//    }
//
//    override fun decorate(view: DayViewFacade?) {
//        view?.addSpan(object : ForegroundColorSpan(Color.parseColor("#d2d2d2")) {})
//        view?.setDaysDisabled(true)
//    }
//}

// 특정 날짜에 점 표시
//class EventDecorator(var color: Int?, var dates: Set<CalendarDay>?) : DayViewDecorator {
//
//    override fun shouldDecorate(day: CalendarDay?): Boolean {
//        return dates!!.contains(day)
//    }
//
//    override fun decorate(view: DayViewFacade?) {
//        view?.addSpan(DotSpan(5F, color!!))
//    }
//}

