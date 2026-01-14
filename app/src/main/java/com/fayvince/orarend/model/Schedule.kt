package com.fayvince.orarend.model

import java.time.DayOfWeek
import java.time.LocalTime

/**
 * Represents a single lesson with its details
 */
data class Lesson(
    val subject: String,
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
    // Pre-compute seconds for performance optimization
    private val startSeconds = startTime.toSecondOfDay()
    private val endSeconds = endTime.toSecondOfDay()
    
    fun isInPeriod(time: LocalTime): Boolean {
        return !time.isBefore(startTime) && time.isBefore(endTime)
    }
    
    fun getRemainingSeconds(currentTime: LocalTime): Long {
        if (currentTime.isBefore(startTime)) {
            return 0L
        }
        val current = currentTime.toSecondOfDay()
        return maxOf(0, endSeconds - current).toLong()
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
        Lesson("Fizika", "Jedlik", DayOfWeek.MONDAY, 1),
        Lesson("Fizika", "Jedlik", DayOfWeek.MONDAY, 2),
        Lesson("Német", "11.A", DayOfWeek.MONDAY, 3),
        Lesson("Irodalom", "11.A", DayOfWeek.MONDAY, 4),
        Lesson("Tesi", "Tornaterem", DayOfWeek.MONDAY, 5),
        Lesson("Informatika", "i3", DayOfWeek.MONDAY, 6),
        Lesson("Angol", "Nyelvi 6", DayOfWeek.MONDAY, 7),
        Lesson("Honvédelem", "11.A", DayOfWeek.MONDAY, 8),
        
        // Tuesday (Kedd)
        
        Lesson("Osztályfőnöki", "11.A", DayOfWeek.TUESDAY, 3),
        Lesson("Angol", "Nyelvi 3", DayOfWeek.TUESDAY, 4),
        Lesson("Német", "9.C", DayOfWeek.TUESDAY, 5),
        Lesson("Matek", "11.A", DayOfWeek.TUESDAY, 6),
        Lesson("Töri", "11.A", DayOfWeek.TUESDAY, 7),
        

        // Wednesday (Szerda)
        Lesson("Informatika", "i3", DayOfWeek.WEDNESDAY, 1),
        Lesson("Informatika", "i3", DayOfWeek.WEDNESDAY, 2),
        Lesson("Ének", "Énekterem", DayOfWeek.WEDNESDAY, 3),
        Lesson("Töri", "11.A", DayOfWeek.WEDNESDAY, 4),
        Lesson("Angol", "Nyelvi 3", DayOfWeek.WEDNESDAY, 5),
        Lesson("Német", "11.A", DayOfWeek.WEDNESDAY, 6),
        Lesson("Matek", "11.A", DayOfWeek.WEDNESDAY, 7),
        Lesson("Matek", "11.A", DayOfWeek.WEDNESDAY, 8),

        // Thursday (Csütörtök)
        Lesson("Rajz", "Rajzterem", DayOfWeek.THURSDAY, 1),
        Lesson("Tesi", "Tornaterem", DayOfWeek.THURSDAY, 2),
        Lesson("Magyar", "11.A", DayOfWeek.THURSDAY, 3),
        Lesson("Angol", "Nyelvi 2!!!", DayOfWeek.THURSDAY, 4),
        Lesson("Német", "7.A", DayOfWeek.THURSDAY, 5),
        Lesson("Matek", "11.A", DayOfWeek.THURSDAY, 6),
        
        // Friday (Péntek)
        Lesson("Irodalom", "11.A", DayOfWeek.FRIDAY, 1),
        Lesson("Magyar", "11.A", DayOfWeek.FRIDAY, 2),
        Lesson("Informatika", "i3", DayOfWeek.FRIDAY, 3),
        Lesson("Matek", "11.A", DayOfWeek.FRIDAY, 4),
        Lesson("Töri", "11.A", DayOfWeek.FRIDAY, 5),
        Lesson("Tesi", "Tornaterem", DayOfWeek.FRIDAY, 6),
        Lesson("Fizika", "Vermes", DayOfWeek.FRIDAY, 7),
        Lesson("Fizika", "Vermes", DayOfWeek.FRIDAY, 8)

    )
    
    // Pre-computed cache: lessons grouped by day and sorted by period number
    // This avoids repeated filtering and sorting on every call to getLessonsForDay
    // Note: These caches assume the lessons list is immutable after object creation
    private val lessonsByDay: Map<DayOfWeek, List<Lesson>> = lessons
        .groupBy { it.dayOfWeek }
        .mapValues { (_, dayLessons) -> 
            dayLessons.sortedBy { it.periodNumber }
        }
    
    // Cache for quick lesson lookup by day and period number
    private val lessonsByDayAndPeriod: Map<DayOfWeek, Map<Int, Lesson>> = lessons
        .groupBy { it.dayOfWeek }
        .mapValues { (_, dayLessons) ->
            dayLessons.associateBy { it.periodNumber }
        }
    
    // Cache first and last time slots for quick access
    private val firstTimeSlot = timeSlots.first()
    private val lastTimeSlot = timeSlots.last()
    
    fun getLessonsForDay(dayOfWeek: DayOfWeek): List<Lesson> {
        return lessonsByDay[dayOfWeek] ?: emptyList()
    }
    
    fun getLessonForDayAndPeriod(dayOfWeek: DayOfWeek, periodNumber: Int): Lesson? {
        return lessonsByDayAndPeriod[dayOfWeek]?.get(periodNumber)
    }
    
    fun getCurrentPeriod(currentTime: LocalTime): TimeSlot? {
        return timeSlots.find { it.isInPeriod(currentTime) }
    }
    
    fun getNextPeriod(currentTime: LocalTime): TimeSlot? {
        return timeSlots.firstOrNull { currentTime.isBefore(it.startTime) }
    }
    
    fun isBreakTime(currentTime: LocalTime): Boolean {
        return getCurrentPeriod(currentTime) == null && 
               currentTime.isAfter(firstTimeSlot.startTime) &&
               currentTime.isBefore(lastTimeSlot.endTime)
    }
}
