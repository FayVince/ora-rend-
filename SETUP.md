# Telepítési és Konfigurációs Útmutató

## Előfeltételek

Az alkalmazás buildeléséhez és futtatásához a következőkre van szükséged:

### 1. Android Studio
- **Verzió**: Android Studio Hedgehog (2023.1.1) vagy újabb
- **Letöltés**: https://developer.android.com/studio

### 2. Android SDK
A következő komponensek szükségesek (Android Studio SDK Manager-ben):
- **Android SDK Platform 34** (Android 14.0)
- **Android SDK Build-Tools 34.0.0** vagy újabb
- **Android Emulator**
- **Intel x86 Emulator Accelerator (HAXM)** vagy **Android Emulator Hypervisor Driver**

### 3. Wear OS komponensek
- **Wear OS system image** (API 30 vagy újabb)
- Ajánlott: **Wear OS 3 - API 30** (x86)

### 4. Java Development Kit (JDK)
- **JDK 17** vagy újabb
- Android Studio általában tartalmaz egy beágyazott JDK-t

## Projekt megnyitása

### Lépések:

1. **Klónozd a repository-t**:
   ```bash
   git clone https://github.com/FayVince/ora-rend-.git
   cd ora-rend-
   ```

2. **Nyisd meg Android Studio-ban**:
   - Indítsd el az Android Studio-t
   - Válaszd: `File → Open`
   - Navigálj a projekt gyökérkönyvtárába
   - Kattints `OK`

3. **Gradle szinkronizálás**:
   - Az Android Studio automatikusan elindítja a Gradle szinkronizálást
   - Ha nem, kattints a `Sync Project with Gradle Files` gombra (elefánt ikon az eszköztáron)
   - Várj, amíg a függőségek letöltődnek (első alkalommal ez néhány percig tarthat)

## Emulátor beállítása

### Wear OS emulátor létrehozása:

1. **Device Manager megnyitása**:
   - `Tools → Device Manager`
   - Vagy kattints a `Device Manager` ikonra a jobb oldali eszköztáron

2. **Új eszköz létrehozása**:
   - Kattints a `Create Device` gombra
   - Válassz `Wear OS` kategóriát
   - Válassz egy eszközt (pl. "Wear OS Small Round")
   - Kattints `Next`

3. **System image kiválasztása**:
   - Válaszd az **API Level 30** vagy magasabb verziót
   - Ajánlott: **Wear OS 3.0 (API 30)** x86 image
   - Ha nincs letöltve, kattints a `Download` linkre
   - Kattints `Next`

4. **Emulátor konfigurálása**:
   - Add meg az eszköz nevét (pl. "Wear OS 3 Round")
   - Opcionálisan módosítsd a beállításokat:
     - **RAM**: minimum 2048 MB (ajánlott 4096 MB)
     - **VM heap**: 256 MB
   - Kattints `Finish`

## Alkalmazás futtatása

### Emulátoron:

1. **Emulátor indítása**:
   - Device Manager-ben válaszd ki a létrehozott Wear OS emulátort
   - Kattints a ▶️ (Play) ikonra
   - Várj, amíg az emulátor teljesen betöltődik

2. **Alkalmazás futtatása**:
   - Válaszd ki az `app` konfigurációt a felső eszköztárban
   - Kattints a ▶️ `Run` gombra (vagy nyomj `Shift + F10`)
   - Várd meg, amíg az alkalmazás települ és elindul

### Fizikai Wear OS eszközön:

1. **Fejlesztői mód engedélyezése**:
   - Nyisd meg a Settings-t az órán
   - Menj a `System → About → Versions`-hoz
   - Koppints 7-szer a `Build number`-re

2. **ADB debugging engedélyezése**:
   - Menj vissza a Settings-be
   - Válaszd a `Developer options`-t
   - Kapcsold be az `ADB debugging`-ot
   - Kapcsold be a `Debug over Wi-Fi`-t (opcionális)

3. **Eszköz csatlakoztatása**:
   - **USB-n keresztül**: 
     - Csatlakoztasd az órát számítógéphez USB-vel (ha támogatott)
   - **WiFi-n keresztül**:
     - Jegyezd fel az eszköz IP címét (látható a Developer options-ben)
     - Terminálban futtasd:
       ```bash
       adb connect <ESZKÖZ_IP_CÍME>:5555
       ```

4. **Alkalmazás telepítése**:
   - Válaszd ki az eszközt a céleszköz legördülő menüben
   - Kattints a `Run` gombra

## Build variánsok

### Debug build:
```bash
./gradlew assembleDebug
```
- A built APK helye: `app/build/outputs/apk/debug/app-debug.apk`

### Release build:
```bash
./gradlew assembleRelease
```
- A built APK helye: `app/build/outputs/apk/release/app-release-unsigned.apk`
- **Megjegyzés**: Release build-hez signing key szükséges

## Hibaelhárítás

### "SDK location not found"
**Megoldás**:
1. Hozz létre egy `local.properties` fájlt a projekt gyökerében:
   ```properties
   sdk.dir=/path/to/your/Android/Sdk
   ```
2. Linuxon/macOS-en általában: `~/Android/Sdk`
3. Windowson általában: `C:\Users\<USERNAME>\AppData\Local\Android\Sdk`

### "Gradle sync failed"
**Megoldás**:
1. Ellenőrizd az internetkapcsolatot
2. Invalidiáld a cache-t: `File → Invalidate Caches → Invalidate and Restart`
3. Töröld a `.gradle` mappát a projekt gyökerében és próbáld újra

### "Emulator: Process finished with exit code 139"
**Megoldás**:
1. Ellenőrizd, hogy a virtualizáció engedélyezve van a BIOS-ban
2. Linux: Telepítsd a KVM-et:
   ```bash
   sudo apt-get install qemu-kvm libvirt-daemon-system libvirt-clients bridge-utils
   ```
3. Windows: Telepítsd a HAXM-et vagy engedélyezd a Hyper-V-t

### Lassú emulátor
**Megoldás**:
1. Növeld az emulátor RAM-ját (Device Manager → Edit → Advanced Settings)
2. Engedélyezd a hardveres gyorsítást
3. Csökkentsd a grafikai minőséget (Graphics: Software vagy Automatic)

### "Compilation failed; see the compiler error output for details"
**Megoldás**:
1. Clean build futtatása:
   ```bash
   ./gradlew clean
   ./gradlew build
   ```
2. Ellenőrizd, hogy a megfelelő Java verzió van-e használatban:
   - `File → Project Structure → SDK Location → JDK location`

## Hasznos parancsok

### Gradle parancsok:
```bash
# Dependencies listázása
./gradlew dependencies

# Build info
./gradlew tasks

# Clean build
./gradlew clean build

# Telepítés eszközre
./gradlew installDebug

# Uninstall
./gradlew uninstallAll

# Lint ellenőrzés
./gradlew lint

# Tesztek futtatása
./gradlew test
```

### ADB parancsok:
```bash
# Csatlakoztatott eszközök listája
adb devices

# APK telepítése
adb install app/build/outputs/apk/debug/app-debug.apk

# Alkalmazás indítása
adb shell am start -n com.fayvince.orarend/.MainActivity

# Logcat megtekintése
adb logcat | grep OraRend

# Alkalmazás törlése
adb uninstall com.fayvince.orarend
```

## További segítség

- **Android Developers**: https://developer.android.com/wear
- **Wear OS Guides**: https://developer.android.com/training/wearables
- **Compose for Wear OS**: https://developer.android.com/training/wearables/compose

## Kapcsolat

Ha problémád van a projekttel, nyiss egy issue-t a GitHub repository-ban.
