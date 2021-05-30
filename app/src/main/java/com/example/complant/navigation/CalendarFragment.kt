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
import androidx.fragment.app.Fragment
import com.example.complant.MainActivity
import com.example.complant.R
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import com.prolificinteractive.materialcalendarview.spans.DotSpan
import kotlinx.android.synthetic.main.fragment_calendar.view.*
import java.util.*
import kotlin.collections.HashSet

class CalendarFragment : Fragment() {
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
        var view =
            LayoutInflater.from(activity).inflate(R.layout.fragment_calendar, container, false)

        var timeCalendar = Calendar.getInstance()

        val currentYear = timeCalendar.get(Calendar.YEAR)
        val currentMonth = timeCalendar.get(Calendar.MONTH) + 1
        val currentDate = timeCalendar.get(Calendar.DATE)

        // 초기화면으로 오늘 날짜를 선택 해서 보여준다.
        view.calendar_view.setSelectedDate(CalendarDay.today())

        view.calendar_view.addDecorators(
            SundayDecorator(), // 일요일을 빨간색으로 표시
            SaturdayDecorator(), // 토요일은 파란색으로 표시
            toDayDecorator(), // 오늘 날짜 크기를 키우고 굵은 색으로 표시
            MinDecorator(CalendarDay.today()) // 같은 달 중에 이미 지난 날들은 회색처리
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
                            DialogInterface.BUTTON_POSITIVE ->
                                view.calendar_text.text = "positive"
                            DialogInterface.BUTTON_NEGATIVE ->
                                mainActivity?.goWateringFragment()
                        }
                    }
                }

                builder.setPositiveButton("예", listener)
                builder.setNegativeButton("아니오 (미루기)", listener)

                builder.show()
            }

//            if (date != CalendarDay.today()) { // 오늘이 아닌 다른 날짜를 클릭했을 때
//                var selection: String? = date.toString()
//                var selectedYear: String? = selection?.slice(IntRange(12, 15))
//                var selectedMonth: String? = selection?.slice(IntRange(17, 19))
//                var selectedDay: String? = selection?.slice(IntRange(21, 23))
//
//                var selectedDayTitle: String? =
//                    selectedYear + "년 " + selectedMonth + "월 " + selectedDay + "일"
//                // 다이얼로그
//                var builder1 = AlertDialog.Builder(context)
//                builder1.setTitle("1")
//                builder1.setMessage("물 주기 시작 날짜를 변경하시겠습니까?")
//                //builder.setIcon(R.mipmap.ic_launcher)
//
//                // 버튼 클릭시에 무슨 작업을 할 것인가!
//                var listener2 = object : DialogInterface.OnClickListener {
//                    override fun onClick(p0: DialogInterface?, p1: Int) {
//                        when (p1) {
//                            DialogInterface.BUTTON_POSITIVE ->
//                                mainActivity?.goWateringFragment()
//                            DialogInterface.BUTTON_NEGATIVE ->
//                                view.calendar_text.text = "negative"
//                        }
//                    }
//                }
//
//                builder1.setPositiveButton("예", listener2)
//                builder1.setNegativeButton("아니오", listener2)
//
//                builder1.show()
//            }

        }



        view.btn_watering_setting.setOnClickListener {
            mainActivity?.goWateringFragment()
        }

        return view
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

// 특정 날짜에 점 표시 (잘 안됨. 삭제 예정)
class EventDecorator : DayViewDecorator {
    var color: Int? = null
    var dates: HashSet<CalendarDay>? = null

    override fun shouldDecorate(day: CalendarDay?): Boolean {
        return dates!!.contains(day)
    }

    override fun decorate(view: DayViewFacade?) {
        view?.addSpan(DotSpan(5F, color!!));
    }
}

// 같은 달 중에 이미 지난 날들은 회색처리
class MinDecorator(min: CalendarDay) : DayViewDecorator {
    val minDay = min
    override fun shouldDecorate(day: CalendarDay?): Boolean {
        return day?.month == minDay.month && day.day < minDay.day
    }

    override fun decorate(view: DayViewFacade?) {
        view?.addSpan(object : ForegroundColorSpan(Color.parseColor("#d2d2d2")) {})
        view?.setDaysDisabled(true)
    }
}