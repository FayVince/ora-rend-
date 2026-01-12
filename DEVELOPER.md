# Android Wear OS Órarend Alkalmazás - Fejlesztői Dokumentáció

## Projekt áttekintés

Ez az alkalmazás egy teljes értékű Android Wear OS órarend kezelő, amely valós időben jeleníti meg az aktuális óra információit és visszaszámlálást.

## Architektúra

Az alkalmazás MVVM (Model-View-ViewModel) architektúrát követ:

### Model réteg (`model/Schedule.kt`)
- **Lesson**: Egy óra adatai (tantárgy, tanár, terem, nap, óra sorszám)
- **TimeSlot**: Egy tanítási óra időintervalluma (kezdés, befejezés)
- **Schedule**: Az órarend központi adatkezelője, tartalmazza:
  - A 8 órás csengetési rendet
  - Az összes heti órát (hétfőtől péntekig)
  - Segédfüggvényeket az aktuális/következő óra meghatározására

### ViewModel réteg (`viewmodel/ScheduleViewModel.kt`)
- **ScheduleViewModel**: Kezeli az alkalmazás állapotát
  - Másodpercenként frissíti az időt
  - Meghatározza az aktuális és következő órát
  - Kiszámítja a hátralevő időt
  - StateFlow-val publikálja az állapotot a UI felé

### View réteg (`ui/ScheduleScreen.kt`)
- **ScheduleScreen**: Compose-zal írt UI
  - ScalingLazyColumn használata Wear OS optimalizált megjelenítéshez
  - Különböző nézetek óra alatt / szünetben / hétvégén
  - TimeText a rendszeridő megjelenítéséhez
  - Kártyák a részletes információkhoz

## Órarend adatok

Az órarend a következőképpen van definiálva a `Schedule.kt` fájlban:

```kotlin
// Hétfő
Lesson("Matematika", "Nagy T.", "E112", DayOfWeek.MONDAY, 1),
Lesson("Történelem", "Kovács M.", "E201", DayOfWeek.MONDAY, 2),
// ... további órák
```

### Az órarend testreszabása

Az órarend módosításához szerkeszd a `Schedule.kt` fájl `lessons` listáját:

1. Nyisd meg: `app/src/main/java/com/fayvince/orarend/model/Schedule.kt`
2. Keresd meg a `val lessons = listOf(...)` részt
3. Adj hozzá, törölj vagy módosíts órákat a formátum megtartásával:

```kotlin
Lesson(
    subject = "Tantárgy neve",
    teacher = "Tanár neve", 
    room = "Terem azonosító",
    dayOfWeek = DayOfWeek.MONDAY, // MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY
    periodNumber = 1 // 1-8
)
```

## Csengetési rend módosítása

Ha a csengetési rendet kell módosítani, szerkeszd a `timeSlots` listát:

```kotlin
val timeSlots = listOf(
    TimeSlot(1, LocalTime.of(7, 45), LocalTime.of(8, 30)),
    // ... további időszakok
)
```

## UI állapotok

Az alkalmazás több különböző állapotot kezel:

### 1. Óra alatt
- Nagy betűvel a tantárgy neve
- Kártyán a tanár és terem
- Nagy számláló a hátralevő idővel (MM:SS formátumban)
- Alul kis kártyán a következő óra előnézete

### 2. Szünet
- "Szünet" felirat kávés emoji-val ☕
- Nagy kártyán a következő óra részletei
- Óra kezdési ideje

### 3. Hétvége
- "Hétvége" felirat parti emoji-val 🎉
- Nincs órarend információ

### 4. Iskolaidő után
- "Nincs több óra" felirat ház emoji-val 🏠

## Függőségek

A projekt a következő főbb függőségeket használja:

- **Wear OS compose**: 1.2.1
- **Compose BOM**: 2023.10.01
- **Activity Compose**: 1.8.1
- **Lifecycle**: 2.6.2
- **Android Gradle Plugin**: 8.1.4
- **Kotlin**: 1.9.22

## Build konfiguráció

### Minimum SDK: 30 (Android 11)
Wear OS 3.0+ eszközökhöz optimalizált.

### Target SDK: 34 (Android 14)
A legfrissebb Android funkciók támogatásával.

## Hibaelhárítás

### Build sikertelen Google repository elérése miatt
Ha a build sikertelen hálózati hibák miatt:
1. Ellenőrizd az internet kapcsolatot
2. Konfiguráld a proxy beállításokat ha szükséges
3. Használj VPN-t ha a Google Maven repository blokkolva van

### Emulátor lassú
Wear OS emulátorok erőforrásigényesek:
1. Adj több RAM-ot az emulátornak (2GB+)
2. Használj hardveres gyorsítást (Intel HAXM vagy AMD)
3. Csökkentsd az emulátor felbontását

### Időzóna problémák
Az alkalmazás a rendszer időzónáját használja. Ha rossz időket látsz:
1. Állítsd be az emulátor/eszköz időzónáját
2. Az emulátorban: Settings → System → Date & time

## Továbbfejlesztési lehetőségek

1. **Perzisztens adattárolás**: Room database használata az órarend tárolására
2. **Távoli szinkronizáció**: Firebase vagy API integráció az órarend frissítéséhez
3. **Widget**: Home screen widget az aktuális óra megjelenítésére
4. **Értesítések**: Óra kezdés előtti emlékeztetők
5. **Több órarend profil**: Különböző órarendek közötti váltás lehetősége
6. **Téma testreszabás**: Sötét/világos téma, színek módosítása
7. **Táplálkozás integráció**: Menza menü megjelenítése
8. **Házifeladat kezelés**: Egyszerű feladat lista funkció

## Tesztelés

### Emulátoron
1. Hozz létre egy Wear OS emulátort Android Studio-ban
2. Válassz Wear OS 3.0+ rendszerképet (API 30+)
3. Indítsd el az emulátort
4. Futtasd az alkalmazást

### Fizikai eszközön
1. Engedélyezd a fejlesztői módot az órán
2. Csatlakoztasd WiFi-n vagy Bluetooth-on keresztül
3. Telepítsd az alkalmazást az eszközre

### Idő szimulálása
A különböző állapotok teszteléséhez:
1. Módosítsd az eszköz rendszeridejét
2. Vagy módosítsd átmenetileg a `ScheduleViewModel` `LocalTime.now()` hívásait

## Licensz

Ez a projekt oktatási célokra készült.
