# UI Design - Android Wear OS Órarend Alkalmazás

## Képernyő Elrendezések

Ez a dokumentum leírja az alkalmazás különböző UI állapotait és azok megjelenését.

---

## 1. Óra Alatt (Lesson In Progress)

Ez a nézet jelenik meg, amikor éppen folyik egy óra.

```
┌─────────────────────────┐
│    [Rendszer idő]       │  <- TimeText komponens
├─────────────────────────┤
│                         │
│   Jelenlegi óra         │  <- Kis szürke felirat
│                         │
│    MATEMATIKA           │  <- Nagy cím (Title1)
│                         │
│  ┌───────────────────┐  │
│  │ Tanár: Nagy T.    │  │  <- Card
│  │ Terem: E112       │  │
│  └───────────────────┘  │
│                         │
│       Hátra             │  <- Kis szürke felirat
│                         │
│      25:34              │  <- Nagy számláló (Display1)
│                         │  <- Kék szín
│                         │
│     Következő           │  <- Kis szürke felirat
│                         │
│  ┌───────────────────┐  │
│  │   Történelem      │  │  <- Card
│  │ Kovács M. • E201  │  │
│  └───────────────────┘  │
│                         │
└─────────────────────────┘
```

**Elemek:**
- Időszak: 7:45 - 8:30 (1. óra példa)
- Tantárgy: Nagy, vastag betűkkel
- Tanár és terem: Kártyán, jól olvashatóan
- Visszaszámláló: Percek:Másodpercek formátumban, kék színnel kiemelve
- Következő óra: Kis előnézet kártyán

---

## 2. Szünet (Break Time)

Ez a nézet jelenik meg szünetben, órák között.

```
┌─────────────────────────┐
│    [Rendszer idő]       │
├─────────────────────────┤
│                         │
│      Szünet             │  <- Title2
│                         │  <- Kék szín
│         ☕              │  <- Display1 méret emoji
│                         │
│                         │
│   Következő óra         │  <- Kis szürke felirat
│                         │
│  ┌───────────────────┐  │
│  │                   │  │
│  │   TÖRTÉNELEM      │  │  <- Title3
│  │                   │  │
│  │ Tanár: Kovács M.  │  │  <- Body2
│  │ Terem: E201       │  │
│  │                   │  │
│  │  8:45 - 9:30      │  │  <- Caption2, szürke
│  │                   │  │
│  └───────────────────┘  │
│                         │
└─────────────────────────┘
```

**Elemek:**
- "Szünet" felirat kávés emoji-val
- Következő óra teljes részletekkel
- Óra kezdési és befejezési ideje

---

## 3. Hétvége (Weekend)

Ez a nézet jelenik meg szombaton és vasárnap.

```
┌─────────────────────────┐
│    [Rendszer idő]       │
├─────────────────────────┤
│                         │
│                         │
│                         │
│      Hétvége            │  <- Title2
│                         │  <- Kék szín
│                         │
│         🎉              │  <- Display1 méret emoji
│                         │
│                         │
│                         │
│                         │
│                         │
└─────────────────────────┘
```

**Elemek:**
- Egyszerű "Hétvége" felirat
- Parti emoji

---

## 4. Nincs Több Óra (No More Lessons)

Ez a nézet jelenik meg, ha már vége az órarendnek aznap.

```
┌─────────────────────────┐
│    [Rendszer idó]       │
├─────────────────────────┤
│                         │
│                         │
│                         │
│   Nincs több óra        │  <- Title2
│                         │
│                         │
│         🏠              │  <- Display1 méret emoji
│                         │
│                         │
│                         │
│                         │
│                         │
└─────────────────────────┘
```

**Elemek:**
- "Nincs több óra" felirat
- Ház emoji

---

## Színek és Stílusok

### Szöveg Méretezések (Material Design for Wear OS)

- **Display1**: 40sp - Legnagyobb, számok és emoji-k
- **Title1**: 24sp - Fő cím (tantárgy név)
- **Title2**: 20sp - Másodlagos cím (állapot)
- **Title3**: 16sp - Harmadlagos cím (következő óra)
- **Body1**: 16sp - Normál szöveg
- **Body2**: 14sp - Kisebb szöveg (tanár, terem)
- **Caption1**: 14sp - Címkék
- **Caption2**: 12sp - Apró információk

### Színpaletta

- **Háttér**: Fekete (#000000) - Wear OS default
- **Előtér**: Fehér (#FFFFFF)
- **Primary**: Kék (#1E88E5) - Kiemelések, számláló
- **Szürke**: (#AAAAAA) - Címkék, apró szövegek
- **Kártya háttér**: Sötétszürke (#1E1E1E)

### Térközök

- **Padding (felső)**: 32dp - TimeText-hez hely
- **Padding (oldalak)**: 10dp
- **Padding (alsó)**: 32dp
- **Item spacing**: 4dp - Elemek közötti távolság
- **Card padding**: 12dp - Kártya belső padding

---

## Animációk és Interakciók

### Görgetés
- **ScalingLazyColumn**: Automatikus skálázás görgetéskor
- A középen lévő elemek nagyobbak, feljebb/lejjebb kisebbek

### Frissítés
- A visszaszámláló másodpercenként frissül
- Smooth animáció nélkül a teljesítmény érdekében
- Az állapot váltások (óra → szünet) azonnal történnek

### Érintés
- **Card-ok**: Kattinthatóak (jelenleg nincs akció)
- **Scroll**: Ujj gesture vagy korona használatával

---

## Képernyő Méretek

Az alkalmazás támogat minden Wear OS képernyőméretet:

### Kis kerek (Small Round)
- **Átmérő**: ~280dp
- **Felbontás**: 320x320px
- Példa: Fossil Sport, TicWatch E

### Közepes kerek (Medium Round)
- **Átmérő**: ~320dp
- **Felbontás**: 360x360px
- Példa: Samsung Galaxy Watch, Moto 360

### Nagy kerek (Large Round)
- **Átmérő**: ~360dp
- **Felbontás**: 400x400px
- Példa: Samsung Galaxy Watch 5

### Négyzet alakú (Square)
- **Méret**: 280x280dp - 320x320dp
- **Felbontás**: 320x320px - 360x360px
- Példa: Oppo Watch

---

## Reszponzív Design

### ScalingLazyColumn előnyei:
1. **Automatikus skálázás**: A görgetés során az elemek mérete dinamikusan változik
2. **Kerek képernyő támogatás**: A szövegek nem érnek a képernyő széléhez
3. **Optimális olvashatóság**: A középső elemek mindig a legnagyobb betűmérettel jelennek meg

### ContentPadding:
- Biztosítja, hogy a szövegek ne takarják el a rendszer UI elemeit
- A TimeText komponens számára hely a tetején
- Alsó padding a navigációs gesztusokhoz

---

## Accessibility (Akadálymentesítés)

### TalkBack támogatás
- Minden szöveges elem olvasható a képernyőolvasóval
- Logikus navigációs sorrend
- Jelentőségteljes content description-ök

### Kontrasztok
- Fehér szöveg fekete háttéren: WCAG AAA minősítés
- Kék kiemelések: AAA minősítés
- Kártyák: AA minősítés

### Betűméretek
- Nagy betűk az alapvető információkhoz
- Wear OS Material Design ajánlásoknak megfelelő méretezés

---

## Összefoglalás

Az UI design:
- ✅ **Egyszerű és tiszta**: Egy nézet, egy fő információ
- ✅ **Könnyen olvasható**: Nagy betűk, jó kontrasztok
- ✅ **Wear OS natív**: Material Design for Wear OS komponensek
- ✅ **Reszponzív**: Minden képernyőméretre optimalizált
- ✅ **Informatív**: Minden lényeges információ egy helyen
- ✅ **Real-time**: Másodpercenként frissülő számláló

Az alkalmazás követi a Wear OS design guideline-okat és biztosítja a legjobb felhasználói élményt okosórán.
