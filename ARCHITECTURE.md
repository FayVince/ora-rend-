# ARCHITECTURE.md - Alkalmazás Architektúra

## Áttekintés

Ez a dokumentum részletezi az Android Wear OS Órarend alkalmazás architektúráját.

## Architektúra Diagram

```
┌─────────────────────────────────────────────────────────────┐
│                        Presentation Layer                    │
│  ┌────────────────────────────────────────────────────────┐ │
│  │              MainActivity (Activity)                    │ │
│  │  • ComponentActivity                                    │ │
│  │  • setContent() - Compose entry point                  │ │
│  └────────────────────────────────────────────────────────┘ │
│                            ↓                                 │
│  ┌────────────────────────────────────────────────────────┐ │
│  │              ScheduleScreen (Composable)               │ │
│  │  • ScalingLazyColumn                                   │ │
│  │  • TimeText                                            │ │
│  │  • Card components                                     │ │
│  │  • Text components                                     │ │
│  │  • State observation via collectAsState()             │ │
│  └────────────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────┘
                            ↕ StateFlow
┌─────────────────────────────────────────────────────────────┐
│                        ViewModel Layer                       │
│  ┌────────────────────────────────────────────────────────┐ │
│  │           ScheduleViewModel (ViewModel)                │ │
│  │  • StateFlow<ScheduleState>                            │ │
│  │  • Coroutine scope                                     │ │
│  │  • Timer (1 second interval)                           │ │
│  │  • Business logic                                      │ │
│  │    - updateState()                                     │ │
│  │    - formatRemainingTime()                             │ │
│  └────────────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────┘
                            ↕ Data Access
┌─────────────────────────────────────────────────────────────┐
│                         Model Layer                          │
│  ┌────────────────────────────────────────────────────────┐ │
│  │               Schedule (Singleton Object)              │ │
│  │  • val lessons: List<Lesson>                           │ │
│  │  • val timeSlots: List<TimeSlot>                       │ │
│  │  • fun getLessonsForDay()                              │ │
│  │  • fun getCurrentPeriod()                              │ │
│  │  • fun getNextPeriod()                                 │ │
│  │  • fun isBreakTime()                                   │ │
│  └────────────────────────────────────────────────────────┘ │
│                            ↕                                 │
│  ┌──────────────────┐  ┌──────────────────┐               │
│  │  Lesson          │  │  TimeSlot        │               │
│  │  (data class)    │  │  (data class)    │               │
│  │  • subject       │  │  • periodNumber  │               │
│  │  • teacher       │  │  • startTime     │               │
│  │  • room          │  │  • endTime       │               │
│  │  • dayOfWeek     │  │  • isInPeriod()  │               │
│  │  • periodNumber  │  │  • getRemainingSeconds() │       │
│  └──────────────────┘  └──────────────────┘               │
└─────────────────────────────────────────────────────────────┘
```

## MVVM Pattern

### Model
**Felelősség**: Adatok és üzleti logika
- `Schedule` - Órarend adatok és logika
- `Lesson` - Óra adatszerkezet
- `TimeSlot` - Időszak adatszerkezet

### ViewModel
**Felelősség**: UI állapot kezelés és előkészítés
- `ScheduleViewModel` - Állapot kezelés
- `ScheduleState` - UI állapot data class

### View
**Felelősség**: UI megjelenítés és user interakciók
- `ScheduleScreen` - Compose UI
- `MainActivity` - Activity host

## Data Flow

```
┌──────────────┐
│ System Clock │
└──────┬───────┘
       │
       ↓ Every 1 second
┌──────────────────────────┐
│ ScheduleViewModel        │
│ • Coroutine timer        │
│ • updateState()          │
└──────┬───────────────────┘
       │
       ↓ Query current time
┌──────────────────────────┐
│ Schedule Singleton       │
│ • getCurrentPeriod()     │
│ • getNextPeriod()        │
│ • getLessonsForDay()     │
└──────┬───────────────────┘
       │
       ↓ Create new state
┌──────────────────────────┐
│ ScheduleState            │
│ • currentTime            │
│ • currentLesson          │
│ • nextLesson             │
│ • remainingSeconds       │
└──────┬───────────────────┘
       │
       ↓ Emit via StateFlow
┌──────────────────────────┐
│ ScheduleScreen           │
│ • collectAsState()       │
│ • Recompose UI           │
└──────────────────────────┘
```

## Component Interactions

### 1. Initialization
```
MainActivity.onCreate()
    → setContent { WearApp() }
        → ScheduleScreen(viewModel)
            → viewModel.init()
                → startTimeUpdate()
```

### 2. Update Cycle
```
Coroutine Timer (1 sec)
    → ViewModel.updateState()
        → LocalTime.now()
        → Schedule.getCurrentPeriod(now)
        → Schedule.getLessonsForDay(today)
        → _state.value = newState
            → StateFlow emission
                → ScheduleScreen recomposition
```

### 3. State Change
```
StateFlow<ScheduleState>
    → collectAsState() in ScheduleScreen
        → when (state) {
            isWeekend → Weekend UI
            currentLesson != null → Lesson UI
            isBreak → Break UI
            else → No lessons UI
          }
```

## Key Design Patterns

### 1. Singleton Pattern
```kotlin
object Schedule {
    val lessons = listOf(...)
    val timeSlots = listOf(...)
    
    fun getCurrentPeriod(time: LocalTime): TimeSlot?
}
```
**Előnyök**:
- Egyetlen instance
- Globális elérés
- Memória hatékony

### 2. Observer Pattern (StateFlow)
```kotlin
// ViewModel
private val _state = MutableStateFlow(ScheduleState())
val state: StateFlow<ScheduleState> = _state.asStateFlow()

// View
val state by viewModel.state.collectAsState()
```
**Előnyök**:
- Reaktív UI
- Automatikus frissítés
- Type-safe

### 3. Repository Pattern (Ready for)
```kotlin
// Future enhancement
interface ScheduleRepository {
    suspend fun getSchedule(): Schedule
    suspend fun saveSchedule(schedule: Schedule)
}
```

## Dependency Graph

```
MainActivity
    ↓ depends on
ScheduleScreen
    ↓ depends on
ScheduleViewModel
    ↓ depends on
Schedule (Model)
    ↓ depends on
Lesson, TimeSlot (Data Classes)
```

## Threading Model

```
┌─────────────────────────────────────────┐
│           Main Thread (UI)              │
│  • Compose recomposition                │
│  • User interactions                    │
│  • StateFlow collection                 │
└─────────────────────────────────────────┘
                ↕ StateFlow
┌─────────────────────────────────────────┐
│       Coroutine (viewModelScope)        │
│  • Timer loop (delay 1000ms)            │
│  • State updates                        │
│  • Schedule queries                     │
└─────────────────────────────────────────┘
```

**Előnyök**:
- Main thread soha nem blokkolódik
- Smooth UI
- Hatékony frissítések

## State Management

### ScheduleState Properties
```kotlin
data class ScheduleState(
    val currentTime: LocalTime,      // Rendszeridő
    val currentDay: DayOfWeek,       // Aktuális nap
    val currentLesson: Lesson?,      // Jelenlegi óra
    val currentPeriod: TimeSlot?,    // Jelenlegi időszak
    val nextLesson: Lesson?,         // Következő óra
    val nextPeriod: TimeSlot?,       // Következő időszak
    val remainingSeconds: Long,      // Hátralevő másodpercek
    val isBreak: Boolean,            // Szünet?
    val isWeekend: Boolean,          // Hétvége?
    val isSchoolDay: Boolean         // Tanítási nap?
)
```

### State Transitions
```
Before School → Lesson 1 → Break → Lesson 2 → ... → After School
     ↓             ↓          ↓         ↓                 ↓
   State 1      State 2    State 3   State 4          State 5
```

## Error Handling

### Null Safety
```kotlin
val currentLesson = if (currentPeriod != null) {
    todayLessons.find { it.periodNumber == currentPeriod.periodNumber }
} else null
```

### Edge Cases
- Hétvégék: `isWeekend = true`
- Nincs több óra: `currentPeriod = null && nextPeriod = null`
- Lyukas óra: `currentPeriod != null && currentLesson = null`

## Scalability

### Horizontal Scaling
- **Több órarend profil**: List<Schedule>
- **Több felhasználó**: Map<UserId, Schedule>
- **Több év**: Map<Year, Schedule>

### Vertical Scaling
- **Database layer**: Room DB
- **Network layer**: Retrofit
- **Cache layer**: DataStore
- **Analytics**: Firebase

## Testing Strategy (Future)

```
┌─────────────────────────────────────────┐
│           Unit Tests                     │
│  • Schedule logic tests                  │
│  • TimeSlot tests                        │
│  • ViewModel tests                       │
└─────────────────────────────────────────┘
┌─────────────────────────────────────────┐
│         Integration Tests                │
│  • ViewModel + Model tests               │
│  • Data flow tests                       │
└─────────────────────────────────────────┘
┌─────────────────────────────────────────┐
│            UI Tests                      │
│  • Compose tests                         │
│  • Screenshot tests                      │
│  • Navigation tests                      │
└─────────────────────────────────────────┘
```

## Performance Considerations

### Optimization Points
1. **StateFlow**: Csak változások esetén frissít
2. **Immutable State**: Hatékony comparison
3. **Lazy Lists**: Csak látható elemek renderelése
4. **Singleton**: Egyetlen Schedule instance
5. **Coroutine**: Non-blocking timer

### Memory Usage
- **Schedule**: ~5KB (30 lessons + 8 timeslots)
- **ViewModel**: ~1KB (state + logic)
- **UI**: Compose automatikusan optimalizál

## Security Considerations

### Current
- Nincs érzékeny adat
- Lokális működés
- Nincs hálózati kommunikáció

### Future
- Auth handling (Firebase)
- Data encryption (SQLCipher)
- Secure API calls (SSL/TLS)
- ProGuard obfuscation

## Summary

Az alkalmazás egy tiszta, moduláris MVVM architektúrát követ:
- ✅ Separation of concerns
- ✅ Unidirectional data flow
- ✅ Reactive state management
- ✅ Type-safe
- ✅ Testable
- ✅ Scalable
- ✅ Maintainable

Az architektúra könnyen bővíthető új funkciókkal anélkül, hogy a meglévő kódot jelentősen módosítani kellene.
