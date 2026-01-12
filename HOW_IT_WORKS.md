# Alkalmazás Működése - Részletes Leírás

## Áttekintés

Ez a dokumentum részletesen leírja, hogyan működik az Órarend Wear OS alkalmazás belülről.

## Időkezelés és Frissítési Ciklus

### Valós idejű frissítés

Az alkalmazás másodpercenként frissíti az állapotát:

```kotlin
private fun startTimeUpdate() {
    viewModelScope.launch {
        while (true) {
            updateState()
            delay(1000) // Frissítés másodpercenként
        }
    }
}
```

### Állapot meghatározása

Minden frissítésnél az alkalmazás:

1. **Meghatározza az aktuális időt**: `LocalTime.now()`
2. **Ellenőrzi a napot**: `LocalDate.now().dayOfWeek`
3. **Eldönti, hogy hétvége van-e**: `SATURDAY || SUNDAY`
4. **Megkeresi az aktuális időszakot**: `Schedule.getCurrentPeriod(now)`
5. **Megkeresi a következő időszakot**: `Schedule.getNextPeriod(now)`
6. **Kiszámítja a hátralevő időt**: `currentPeriod.getRemainingSeconds(now)`

## Csengetési Rend Logika

### TimeSlot osztály

A `TimeSlot` osztály három fő funkciót lát el:

```kotlin
data class TimeSlot(
    val periodNumber: Int,      // Óra sorszáma (1-8)
    val startTime: LocalTime,   // Kezdési idő
    val endTime: LocalTime      // Befejezési idő
)
```

#### 1. Időszak ellenőrzése
```kotlin
fun isInPeriod(time: LocalTime): Boolean {
    return !time.isBefore(startTime) && time.isBefore(endTime)
}
```
- Visszaadja, hogy az adott idő benne van-e az időszakban
- Például: 8:15 benne van a 7:45-8:30 időszakban

#### 2. Hátralevő idő számítása
```kotlin
fun getRemainingSeconds(currentTime: LocalTime): Long {
    if (currentTime.isBefore(startTime)) {
        return 0
    }
    val current = currentTime.toSecondOfDay()
    val end = endTime.toSecondOfDay()
    return maxOf(0, end - current)
}
```
- Kiszámítja, hány másodperc van hátra az óra végéig
- Például: 8:15-kor még 900 másodperc (15 perc) van hátra a 8:30-as óravégig

## Órarend Kezelés

### Óra megkeresése

Az aktuális óra megkeresése két lépésben történik:

1. **Időszak azonosítása**:
```kotlin
val currentPeriod = Schedule.getCurrentPeriod(now)
// például: TimeSlot(2, 8:45, 9:30)
```

2. **Óra megkeresése az órarendben**:
```kotlin
val currentLesson = todayLessons.find { 
    it.periodNumber == currentPeriod.periodNumber 
}
// például: Lesson("Matematika", "Nagy T.", "E112", MONDAY, 2)
```

### Szünetek kezelése

A szünet akkor van, ha:
- **NEM** vagyunk egy időszakban
- **ÉS** az időpont az első óra kezdése után van
- **ÉS** az időpont az utolsó óra vége előtt van

```kotlin
fun isBreakTime(currentTime: LocalTime): Boolean {
    return getCurrentPeriod(currentTime) == null && 
           currentTime.isAfter(timeSlots.first().startTime) &&
           currentTime.isBefore(timeSlots.last().endTime)
}
```

## UI Állapot Kezelés

### ScheduleState

Az alkalmazás állapota egy immutábilis data class-ban tárolódik:

```kotlin
data class ScheduleState(
    val currentTime: LocalTime,        // Aktuális idő
    val currentDay: DayOfWeek,         // Aktuális nap
    val currentLesson: Lesson?,        // Jelenlegi óra (ha van)
    val currentPeriod: TimeSlot?,      // Jelenlegi időszak (ha van)
    val nextLesson: Lesson?,           // Következő óra (ha van)
    val nextPeriod: TimeSlot?,         // Következő időszak (ha van)
    val remainingSeconds: Long,        // Hátralevő másodpercek
    val isBreak: Boolean,              // Szünet van-e
    val isWeekend: Boolean,            // Hétvége van-e
    val isSchoolDay: Boolean           // Iskolaidő van-e
)
```

### StateFlow

A ViewModel StateFlow-t használ az állapot publikálására:

```kotlin
private val _state = MutableStateFlow(ScheduleState())
val state: StateFlow<ScheduleState> = _state.asStateFlow()
```

A UI feliratkozik az állapot változásaira:

```kotlin
val state by viewModel.state.collectAsState()
```

## UI Renderelés

### Compose for Wear OS

Az UI Jetpack Compose-zal van implementálva, Wear OS specifikus komponenseket használva:

#### ScalingLazyColumn
```kotlin
ScalingLazyColumn(
    modifier = Modifier.fillMaxSize(),
    contentPadding = PaddingValues(top = 32.dp, ...),
    verticalArrangement = Arrangement.spacedBy(4.dp)
) {
    // Elemek
}
```

- **Automatikus skálázás**: A középen lévő elemek nagyobbak
- **Görgetés optimalizálás**: Wear OS gesztusokhoz optimalizált
- **Kerek képernyő támogatás**: Automatikusan igazodik a képernyő alakjához

#### Kondicionális renderelés

Az UI különböző állapotokat kezel `when` kifejezéssel:

```kotlin
when {
    state.isWeekend -> {
        // Hétvége UI
    }
    state.currentLesson != null -> {
        // Óra alatt UI
    }
    state.isBreak && state.nextLesson != null -> {
        // Szünet UI
    }
    else -> {
        // Nincs több óra UI
    }
}
```

## Időformázás

### Visszaszámláló formázása

A hátralevő idő MM:SS formátumban jelenik meg:

```kotlin
fun formatRemainingTime(seconds: Long): String {
    val minutes = seconds / 60
    val secs = seconds % 60
    return String.format("%02d:%02d", minutes, secs)
}
```

Példák:
- 125 másodperc → "02:05"
- 3661 másodperc → "61:01"
- 45 másodperc → "00:45"

## Teljesítmény Optimalizáció

### Hatékony frissítés

1. **Minimális újra-kompozíció**: Csak a változó értékek frissülnek
2. **StateFlow használata**: Compose automatikusan optimalizálja a frissítéseket
3. **Coroutine használata**: A háttér frissítés nem blokkolja a UI szálat

### Memória kezelés

1. **Object Singleton**: A `Schedule` object egyszer jön létre
2. **Immutable adatszerkezetek**: A `Lesson` és `TimeSlot` példányok nem változnak
3. **StateFlow**: Hatékony állapot megosztás

## Hibakezelés

### Hiányzó óra kezelése

Ha nincs óra egy adott időszakban (lyukas óra):

```kotlin
val currentLesson = if (currentPeriod != null) {
    todayLessons.find { it.periodNumber == currentPeriod.periodNumber }
} else null
```

A `find` `null`-t ad vissza, ha nincs találat, így:
- Az UI megjeleníti az időszakot de jelzi, hogy szabad óra

### Hétvégék

Hétvégeken automatikusan:
- Nincs órarend megjelenítés
- Speciális üzenet jelenik meg
- Nem próbál órákat keresni

### Időzóna kezelés

Az alkalmazás a rendszer időzónáját használja:
- `LocalTime.now()` - mindig a helyi idő
- `LocalDate.now()` - mindig a helyi dátum

## Testreszabási Pontok

### 1. Órarend módosítása
Fájl: `app/src/main/java/com/fayvince/orarend/model/Schedule.kt`
```kotlin
val lessons = listOf(
    // Add hozzá vagy módosítsd az órákat itt
)
```

### 2. Csengetési rend módosítása
Ugyanabban a fájlban:
```kotlin
val timeSlots = listOf(
    // Módosítsd az időszakokat itt
)
```

### 3. UI színek
Fájl: `app/src/main/res/values/colors.xml`
```xml
<color name="ic_launcher_background">#1E88E5</color>
```

### 4. Szövegek
Fájl: `app/src/main/res/values/strings.xml`
```xml
<string name="current_lesson">Jelenlegi óra</string>
```

## Jövőbeli Fejlesztések

### Perzisztens Tárolás
```kotlin
// Room database integráció
@Entity
data class LessonEntity(...)

@Dao
interface LessonDao {
    @Query("SELECT * FROM lessons WHERE dayOfWeek = :day")
    fun getLessonsForDay(day: DayOfWeek): Flow<List<LessonEntity>>
}
```

### Értesítések
```kotlin
// WorkManager használata időzített értesítésekhez
class LessonReminderWorker : Worker() {
    override fun doWork(): Result {
        // Értesítés küldése 5 perccel az óra kezdése előtt
    }
}
```

### Távoli Szinkronizáció
```kotlin
// Retrofit integráció
interface ScheduleApi {
    @GET("schedule")
    suspend fun getSchedule(): Schedule
}
```

## Összefoglalás

Az alkalmazás egy jól strukturált, hatékony és skálázható architektúrát használ:
- **Model**: Tiszta adatszerkezetek üzleti logikával
- **ViewModel**: Állapotkezelés és üzleti logika
- **View**: Reaktív UI Compose-zal

Az óránkénti frissítés, a valós idejű visszaszámlálás és a intelligens állapotkezelés együtt egy felhasználóbarát és megbízható alkalmazást eredményez.
