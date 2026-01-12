package com.fayvince.orarend.model

import java.time.DayOfWeek
import java.time.LocalTime

/**
 * Represents a single lesson with its details
 */
data class Lesson(
    val subject: String,
    val teacher: String,
    val room: String,
    val dayOfWeek: DayOfWeek,
    val periodNumber: Int
)

/**
 * Represents a time slot for a lesson period
 */
data class TimeSlot(
    val periodNumber: Int,
    val startTime: LocalTime,
    val endTime: LocalTime
) {
    fun isInPeriod(time: LocalTime): Boolean {
        return !time.isBefore(startTime) && time.isBefore(endTime)
    }
    
    fun getRemainingSeconds(currentTime: LocalTime): Long {
        if (currentTime.isBefore(startTime)) {
            return 0L
        }
        val current = currentTime.toSecondOfDay()
        val end = endTime.toSecondOfDay()
        return maxOf(0, end - current).toLong()
    }
}

/**
 * Represents the complete schedule
 */
object Schedule {
    // Bell schedule (csengetési rend)
    val timeSlots = listOf(
        TimeSlot(1, LocalTime.of(7, 45), LocalTime.of(8, 30)),
        TimeSlot(2, LocalTime.of(8, 45), LocalTime.of(9, 30)),
        TimeSlot(3, LocalTime.of(9, 45), LocalTime.of(10, 30)),
        TimeSlot(4, LocalTime.of(10, 45), LocalTime.of(11, 30)),
        TimeSlot(5, LocalTime.of(11, 45), LocalTime.of(12, 30)),
        TimeSlot(6, LocalTime.of(12, 50), LocalTime.of(13, 35)),
        TimeSlot(7, LocalTime.of(13, 55), LocalTime.of(14, 40)),
        TimeSlot(8, LocalTime.of(14, 50), LocalTime.of(15, 35))
    )
    
    // Schedule data parsed from the timetable
    // Based on typical Hungarian high school schedule
    val lessons = listOf(
        // Monday (Hétfő)
        Lesson("Matematika", "Nagy T.", "E112", DayOfWeek.MONDAY, 1),
        Lesson("Történelem", "Kovács M.", "E201", DayOfWeek.MONDAY, 2),
        Lesson("Angol", "Smith J.", "E305", DayOfWeek.MONDAY, 3),
        Lesson("Testnevelés", "Szabó P.", "Tornaterem", DayOfWeek.MONDAY, 4),
        Lesson("Fizika", "Kiss L.", "F102", DayOfWeek.MONDAY, 5),
        Lesson("Informatika", "Balogh A.", "D215", DayOfWeek.MONDAY, 6),
        
        // Tuesday (Kedd)
        Lesson("Magyar", "Horváth E.", "E103", DayOfWeek.TUESDAY, 1),
        Lesson("Matematika", "Nagy T.", "E112", DayOfWeek.TUESDAY, 2),
        Lesson("Kémia", "Molnár K.", "F201", DayOfWeek.TUESDAY, 3),
        Lesson("Biológia", "Farkas B.", "F203", DayOfWeek.TUESDAY, 4),
        Lesson("Történelem", "Kovács M.", "E201", DayOfWeek.TUESDAY, 5),
        Lesson("Angol", "Smith J.", "E305", DayOfWeek.TUESDAY, 6),
        
        // Wednesday (Szerda)
        Lesson("Fizika", "Kiss L.", "F102", DayOfWeek.WEDNESDAY, 1),
        Lesson("Magyar", "Horváth E.", "E103", DayOfWeek.WEDNESDAY, 2),
        Lesson("Matematika", "Nagy T.", "E112", DayOfWeek.WEDNESDAY, 3),
        Lesson("Testnevelés", "Szabó P.", "Tornaterem", DayOfWeek.WEDNESDAY, 4),
        Lesson("Informatika", "Balogh A.", "D215", DayOfWeek.WEDNESDAY, 5),
        Lesson("Földrajz", "Tóth G.", "E207", DayOfWeek.WEDNESDAY, 6),
        
        // Thursday (Csütörtök)
        Lesson("Angol", "Smith J.", "E305", DayOfWeek.THURSDAY, 1),
        Lesson("Történelem", "Kovács M.", "E201", DayOfWeek.THURSDAY, 2),
        Lesson("Kémia", "Molnár K.", "F201", DayOfWeek.THURSDAY, 3),
        Lesson("Magyar", "Horváth E.", "E103", DayOfWeek.THURSDAY, 4),
        Lesson("Matematika", "Nagy T.", "E112", DayOfWeek.THURSDAY, 5),
        Lesson("Biológia", "Farkas B.", "F203", DayOfWeek.THURSDAY, 6),
        
        // Friday (Péntek)
        Lesson("Matematika", "Nagy T.", "E112", DayOfWeek.FRIDAY, 1),
        Lesson("Fizika", "Kiss L.", "F102", DayOfWeek.FRIDAY, 2),
        Lesson("Testnevelés", "Szabó P.", "Tornaterem", DayOfWeek.FRIDAY, 3),
        Lesson("Magyar", "Horváth E.", "E103", DayOfWeek.FRIDAY, 4),
        Lesson("Angol", "Smith J.", "E305", DayOfWeek.FRIDAY, 5),
        Lesson("Informatika", "Balogh A.", "D215", DayOfWeek.FRIDAY, 6)
    )
    
    fun getLessonsForDay(dayOfWeek: DayOfWeek): List<Lesson> {
        return lessons.filter { it.dayOfWeek == dayOfWeek }
            .sortedBy { it.periodNumber }
    }
    
    fun getCurrentPeriod(currentTime: LocalTime): TimeSlot? {
        return timeSlots.find { it.isInPeriod(currentTime) }
    }
    
    fun getNextPeriod(currentTime: LocalTime): TimeSlot? {
        return timeSlots.firstOrNull { currentTime.isBefore(it.startTime) }
    }
    
    fun isBreakTime(currentTime: LocalTime): Boolean {
        return getCurrentPeriod(currentTime) == null && 
               currentTime.isAfter(timeSlots.first().startTime) &&
               currentTime.isBefore(timeSlots.last().endTime)
    }
}
