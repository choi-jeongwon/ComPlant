package com.example.complant.navigation

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
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = LayoutInflater.from(activity).inflate(R.layout.fragment_calendar, container, false)

        var startTimeCalendar = Calendar.getInstance()
        var endTimeCalendar = Calendar.getInstance()

        val currentYear = startTimeCalendar.get(Calendar.YEAR)
        val currentMonth = startTimeCalendar.get(Calendar.MONTH)
        val currentDate = startTimeCalendar.get(Calendar.DATE)

        // 초기화면으로 오늘 날짜를 선택 해서 보여준다.
        view.calendar_view.setSelectedDate(CalendarDay.today())

        view.calendar_view.addDecorators(
            SundayDecorator(), // 일요일을 빨간색으로 표시
            SaturdayDecorator(), // 토요일은 파란색으로 표시
            toDayDecorator(), // 오늘 날짜 크기를 키우고 굵은 색으로 표시
            MinDecorator(CalendarDay.today()) // 같은 달 중에 이미 지난 날들은 회색처리
        )
        
        view.calendar_view.setOnDateChangedListener { widget, date, selected ->
            if (date == CalendarDay.today()) {
                view.calendar_text.text = Calendar.getInstance().get(Calendar.DATE).toString()
            }

            //if(date )

        }
        return view
    }
}

// 토요일을 파란색으로 변경
class SaturdayDecorator : DayViewDecorator {
    var calendar : Calendar = Calendar.getInstance()

    override fun shouldDecorate(day: CalendarDay?): Boolean {
        day?.copyTo(calendar)
        var weekDay : Int? = calendar.get(Calendar.DAY_OF_WEEK)
        return weekDay == Calendar.SATURDAY
    }

    override fun decorate(view: DayViewFacade?) {
        view?.addSpan(ForegroundColorSpan(Color.BLUE))
    }
}


// 일요일을 빨간색으로 변경
class SundayDecorator : DayViewDecorator {
    var calendar : Calendar = Calendar.getInstance()

    override fun shouldDecorate(day: CalendarDay?): Boolean {
        day?.copyTo(calendar)
        var weekDay : Int? = calendar.get(Calendar.DAY_OF_WEEK)
        return weekDay == Calendar.SUNDAY
    }

    override fun decorate(view: DayViewFacade?) {
        view?.addSpan(ForegroundColorSpan(Color.RED))
    }
}

// 오늘 날짜를 굵게 표시
class toDayDecorator : DayViewDecorator {
    var date : CalendarDay = CalendarDay.today()

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
    var color : Int? = null
    var dates : HashSet<CalendarDay>? = null

    override fun shouldDecorate(day: CalendarDay?): Boolean {
        return dates!!.contains(day)
    }

    override fun decorate(view: DayViewFacade?) {
        view?.addSpan(DotSpan(5F, color!!));
    }
}

// 같은 달 중에 이미 지난 날들은 회색처리
class MinDecorator(min: CalendarDay): DayViewDecorator {
    val minDay = min
    override fun shouldDecorate(day: CalendarDay?): Boolean {
        return day?.month == minDay.month && day.day < minDay.day
    }

    override fun decorate(view: DayViewFacade?) {
        view?.addSpan(object :ForegroundColorSpan(Color.parseColor("#d2d2d2")){})
        view?.setDaysDisabled(true)
    }
}