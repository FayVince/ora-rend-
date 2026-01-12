package com.fayvince.orarend.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fayvince.orarend.model.Lesson
import com.fayvince.orarend.model.Schedule
import com.fayvince.orarend.model.TimeSlot
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime

data class ScheduleState(
    val currentTime: LocalTime = LocalTime.now(),
    val currentDay: DayOfWeek = LocalDate.now().dayOfWeek,
    val currentLesson: Lesson? = null,
    val currentPeriod: TimeSlot? = null,
    val nextLesson: Lesson? = null,
    val nextPeriod: TimeSlot? = null,
    val remainingSeconds: Long = 0,
    val isBreak: Boolean = false,
    val isWeekend: Boolean = false,
    val isSchoolDay: Boolean = true
)

class ScheduleViewModel : ViewModel() {
    private val _state = MutableStateFlow(ScheduleState())
    val state: StateFlow<ScheduleState> = _state.asStateFlow()
    
    init {
        startTimeUpdate()
    }
    
    private fun startTimeUpdate() {
        viewModelScope.launch {
            while (true) {
                updateState()
                delay(1000) // Update every second
            }
        }
    }
    
    private fun updateState() {
        val now = LocalTime.now()
        val today = LocalDate.now().dayOfWeek
        val isWeekend = today == DayOfWeek.SATURDAY || today == DayOfWeek.SUNDAY
        
        if (isWeekend) {
            _state.value = ScheduleState(
                currentTime = now,
                currentDay = today,
                isWeekend = true,
                isSchoolDay = false
            )
            return
        }
        
        val todayLessons = Schedule.getLessonsForDay(today)
        val currentPeriod = Schedule.getCurrentPeriod(now)
        val nextPeriod = Schedule.getNextPeriod(now)
        val isBreak = Schedule.isBreakTime(now)
        
        val currentLesson = if (currentPeriod != null) {
            todayLessons.find { it.periodNumber == currentPeriod.periodNumber }
        } else null
        
        val nextLesson = if (nextPeriod != null) {
            todayLessons.find { it.periodNumber == nextPeriod.periodNumber }
        } else null
        
        val remainingSeconds = if (currentPeriod != null) {
            currentPeriod.getRemainingSeconds(now)
        } else if (nextPeriod != null && !isBreak) {
            0
        } else {
            0
        }
        
        val isSchoolDay = now.isBefore(Schedule.timeSlots.last().endTime)
        
        _state.value = ScheduleState(
            currentTime = now,
            currentDay = today,
            currentLesson = currentLesson,
            currentPeriod = currentPeriod,
            nextLesson = nextLesson,
            nextPeriod = nextPeriod,
            remainingSeconds = remainingSeconds,
            isBreak = isBreak,
            isWeekend = false,
            isSchoolDay = isSchoolDay
        )
    }
    
    fun formatRemainingTime(seconds: Long): String {
        val minutes = seconds / 60
        val secs = seconds % 60
        return String.format("%02d:%02d", minutes, secs)
    }
}
