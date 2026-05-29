# 🎨 Jetpack Compose Preview Guide - Tahlil App

## 📱 Cara Melihat Preview di Android Studio & Testing di HP

### ✅ Yang Sudah Ditambahkan

**File yang sudah dilengkapi dengan preview:**
- `/app/src/main/java/com/app_muslim/surah_yasin/ui/memorial/MemorialScreen.kt` ✅

**File template dan testing yang dibuat:**
- `/app/src/main/java/com/app_muslim/surah_yasin/ui/preview/ComposePreviewTemplate.kt` ✅
- `/app/src/main/java/com/app_muslim/surah_yasin/ui/preview/ComposeTestActivity.kt` ✅

---

## 🔍 Cara Melihat Preview di Android Studio

### 1. Buka File Compose Screen
```kotlin
// File: MemorialScreen.kt
// Scroll ke bawah untuk melihat fungsi preview
```

### 2. Aktifkan Preview Panel
- Buka file compose (.kt)
- Klik **View** → **Tool Windows** → **Compose Preview**
- Atau tekan tombol **Split** di pojok kanan atas editor

### 3. Preview yang Tersedia
- **Memorial Screen - Empty State** - Tampilan kosong
- **Memorial Screen - Loading State** - Tampilan loading
- **Memorial Screen - With Data** - Tampilan dengan data
- **Memorial Screen - Dark Theme** - Tema gelap
- **Memorial Card** - Komponen kartu individual
- **Tablet View** - Tampilan tablet
- **Landscape** - Tampilan landscape

### 4. Interaksi dengan Preview
- **Refresh Preview**: Klik tombol refresh di panel preview
- **Interactive Preview**: Klik "Interactive" untuk testing touch
- **Different Devices**: Gunakan dropdown untuk pilih device
- **Build & Refresh**: Jika error, build project dulu: `./gradlew assembleDebug`

---

## 📱 Cara Testing di HP/Emulator

### Method 1: Menggunakan ComposeTestActivity

**Langkah 1: Tambahkan ke AndroidManifest.xml**
```xml
<!-- Tambahkan di dalam <application> tag -->
<activity
    android:name=".ui.preview.ComposeTestActivity"
    android:exported="true"
    android:theme="@style/Theme.MaterialComponents.DayNight.NoActionBar">
    <intent-filter>
        <action android:name="android.intent.action.MAIN" />
        <category android:name="android.intent.category.LAUNCHER" />
    </intent-filter>
</activity>
```

**Langkah 2: Build dan Install**
```bash
# Build APK
./gradlew assembleDebug

# Install ke HP (via USB debugging) 
adb install app/build/outputs/apk/debug/app-debug.apk

# Atau install ke emulator
adb -e install app/build/outputs/apk/debug/app-debug.apk
```

**Langkah 3: Buka App**
- Buka app "Compose Test" di HP
- Pilih screen yang ingin dites
- Test berbagai orientasi dan interaksi

### Method 2: Menggunakan Main App
```bash
# Build dan jalankan app utama
./gradlew installDebug
```

---

## 🛠️ Menambahkan Preview ke Screen Baru

### Template Dasar
```kotlin
// 1. Tambahkan imports
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.app_muslim.surah_yasin.ui.theme.IslamicTheme

// 2. Buat preview function
@Preview(name = "Screen Name - Default")
@Composable
fun PreviewYourScreen() {
    IslamicTheme {
        Surface {
            YourScreen(
                // parameter sample data
                title = "Sample Title",
                isLoading = false,
                onButtonClick = {}
            )
        }
    }
}

// 3. Preview untuk state berbeda
@Preview(name = "Screen Name - Loading")
@Composable  
fun PreviewYourScreenLoading() {
    IslamicTheme {
        Surface {
            YourScreen(
                title = "Loading...",
                isLoading = true,
                onButtonClick = {}
            )
        }
    }
}

// 4. Preview untuk dark theme
@Preview(name = "Screen Name - Dark", uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewYourScreenDark() {
    IslamicTheme {
        Surface {
            YourScreen(
                title = "Dark Theme Test",
                isLoading = false,
                onButtonClick = {}
            )
        }
    }
}
```

### Preview untuk Device Khusus
```kotlin
// Tablet preview
@Preview(
    name = "Screen - Tablet",
    device = "spec:width=1280dp,height=800dp,dpi=240"
)

// Landscape preview
@Preview(
    name = "Screen - Landscape", 
    widthDp = 840,
    heightDp = 360
)

// Small phone preview
@Preview(
    name = "Screen - Small Phone",
    device = "spec:width=360dp,height=640dp,dpi=240"
)
```

---

## 🎯 Best Practices untuk Preview

### 1. **Data Sample yang Realistis**
```kotlin
// Gunakan data yang representatif
Memorial(
    id = "1",
    title = "Loving Memory of Grandfather",
    deceasedName = "Ahmad bin Abdullah", 
    totalPrayers = 45,
    description = "A wonderful grandfather"
)
```

### 2. **Test Berbagai State**
- ✅ Loading state
- ✅ Empty state  
- ✅ Error state
- ✅ Success with data
- ✅ Dark theme
- ✅ Different screen sizes

### 3. **Test Arabic Text**
```kotlin
// Test RTL layout
ArabicTextDisplay(
    text = "بِسْمِ اللَّهِ الرَّحْمَنِ الرَّحِيم",
    transliteration = "Bismillahi Rahmani Raheem"
)
```

### 4. **Performance Testing**
- Test di device nyata (bukan hanya emulator)
- Test dengan data banyak
- Test scroll performance
- Test memory usage

---

## 🚀 Workflow Development 

### 1. **Design in Preview First**
```
1. Buat screen basic
2. Tambahkan preview
3. Iterate design di preview
4. Test di device
5. Refine dan repeat
```

### 2. **Build & Test Cycle**
```bash
# Quick compile check
./gradlew :app:compileProductionDebugKotlin

# Build APK untuk testing
./gradlew assembleDebug

# Install dan test
adb install app/build/outputs/apk/debug/app-debug.apk
```

### 3. **Debugging Preview Issues**

**Preview tidak muncul:**
```bash
# Build project 
./gradlew assembleDebug

# Clear cache
./gradlew clean

# Sync gradle files
./gradlew --refresh-dependencies
```

**Error di Preview:**
- Check semua imports
- Pastikan IslamicTheme tersedia
- Check data sample valid
- Pastikan tidak ada external dependency dalam preview

---

## 📂 File Structure untuk Preview

```
app/src/main/java/com/app_muslim/surah_yasin/
├── ui/
│   ├── memorial/
│   │   └── MemorialScreen.kt (✅ With Previews)
│   ├── auth/
│   │   └── AuthScreen.kt (🔄 Add previews here)
│   ├── community/
│   │   └── CommunityScreen.kt (🔄 Add previews here)
│   ├── theme/
│   │   └── IslamicTheme.kt (✅ Theme ready)
│   └── preview/
│       ├── ComposePreviewTemplate.kt (✅ Template)
│       └── ComposeTestActivity.kt (✅ Phone testing)
```

---

## ✅ Next Steps

1. **Copy preview functions** dari MemorialScreen.kt ke screen lain
2. **Sesuaikan data sample** untuk setiap screen
3. **Test di Android Studio preview panel**
4. **Build dan test di HP** menggunakan ComposeTestActivity
5. **Iterate design** berdasarkan feedback dari testing

**Happy Coding! 🚀**