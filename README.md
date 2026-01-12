# Órarend - Android Wear OS Alkalmazás

Android Wear OS (okosóra) alkalmazás, amely megjeleníti a heti órarendet valós idejű visszaszámlálással.

## Funkciók

- **Órarend megjelenítés** - A heti órarend megjelenítése napok szerint
- **Valós idejű visszaszámlálás** - Másodpercenként frissülő visszaszámláló az aktuális óra végéig
- **Aktuális óra információ** - Tantárgy, tanár és terem megjelenítése
- **Következő óra előnézet** - A következő óra adatainak megjelenítése szünetben és óra alatt is
- **Csengetési rend** - 8 órás tanítási nap támogatása
- **Nap felismerés** - Automatikus napfelismerés és megfelelő órarend megjelenítés
- **Hétvége kezelés** - Hétvégén megfelelő üzenet megjelenítése

## Csengetési rend

| Óra | Időpont |
|-----|---------|
| 1. óra | 7:45 - 8:30 |
| 2. óra | 8:45 - 9:30 |
| 3. óra | 9:45 - 10:30 |
| 4. óra | 10:45 - 11:30 |
| 5. óra | 11:45 - 12:30 |
| 6. óra | 12:50 - 13:35 |
| 7. óra | 13:55 - 14:40 |
| 8. óra | 14:50 - 15:35 |

## Technikai részletek

- **Platform**: Android Wear OS (Wear OS by Google)
- **Min SDK**: 30 (Android 11)
- **Target SDK**: 34 (Android 14)
- **Nyelv**: Kotlin
- **UI Framework**: Compose for Wear OS
- **Build rendszer**: Gradle (Kotlin DSL)
- **Architektúra**: MVVM (Model-View-ViewModel)

## Projekt struktúra

```
app/
├── src/main/
│   ├── java/com/fayvince/orarend/
│   │   ├── model/
│   │   │   └── Schedule.kt          # Adatmodellek (Lesson, TimeSlot, Schedule)
│   │   ├── viewmodel/
│   │   │   └── ScheduleViewModel.kt # ViewModel valós idejű frissítéssel
│   │   ├── ui/
│   │   │   └── ScheduleScreen.kt    # Compose UI komponensek
│   │   └── MainActivity.kt          # Fő Activity
│   ├── res/
│   │   └── values/
│   │       └── strings.xml          # Szöveges erőforrások
│   └── AndroidManifest.xml
└── build.gradle.kts
```

## Build és futtatás

### Előfeltételek

- Android Studio Hedgehog vagy újabb
- JDK 8 vagy újabb
- Android SDK 34
- Wear OS emulátor vagy fizikai Wear OS eszköz

### Build parancsok

```bash
# Build debug verzió
./gradlew assembleDebug

# Build release verzió
./gradlew assembleRelease

# Clean build
./gradlew clean build
```

### Futtatás

1. Nyisd meg a projektet Android Studio-ban
2. Hozz létre egy Wear OS emulátort (vagy csatlakoztass egy fizikai eszközt)
3. Futtasd az alkalmazást (Run → Run 'app')

## Órarend testreszabása

Az órarendet a `Schedule.kt` fájlban lehet módosítani:

```kotlin
val lessons = listOf(
    Lesson("Tantárgy", "Tanár", "Terem", DayOfWeek.MONDAY, 1),
    // további órák...
)
```

## UI állapotok

Az alkalmazás különböző állapotokat kezel:

1. **Óra alatt**: Megjelenik az aktuális óra részletei és visszaszámláló
2. **Szünet**: Szünet üzenet és következő óra információ
3. **Hétvége**: Hétvége üzenet emoji-val
4. **Órarend után**: "Nincs több óra" üzenet

## Licenc

Ez a projekt oktatási célokra készült.