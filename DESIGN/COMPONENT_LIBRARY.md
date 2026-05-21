# 🧩 Tahlil Component Library Specifications

## 📋 Component Library Overview

**Project**: Tahlil - Global Memorial Prayer Platform  
**Phase**: Design System Implementation  
**Created**: May 20, 2026  
**Designer**: Team Design  
**Developer Handoff**: Ready for Implementation  

---

## 🎨 Design System Foundation

### 🎯 Design Principles

1. **Cultural Sensitivity**: Respects Islamic traditions across diverse cultures
2. **Photo-Centricity**: Memorial photos are central to user experience
3. **Spiritual Reverence**: Maintains sacred, respectful atmosphere
4. **Global Accessibility**: Supports 20+ languages, RTL/LTR layouts
5. **3-Tap Rule**: Maximum 3 taps to complete any core action

### 📐 Grid System

```
Base Grid: 8px
Mobile Breakpoints:
- Small: 320px - 480px
- Medium: 481px - 768px
- Large: 769px+

Layout Margins:
- Small: 16px
- Medium: 24px
- Large: 32px

Column Gutters: 16px
```

---

## 🎨 Core Components

### 1. 📱 Navigation Components

#### 1.1 Bottom Navigation Bar
```kotlin
// TahlilBottomNavBar.kt
@Composable
fun TahlilBottomNavBar(
    selectedTab: NavTab,
    onTabSelected: (NavTab) -> Unit,
    modifier: Modifier = Modifier
)

Design Specs:
- Height: 72dp
- Background: Surface color with elevation 8dp
- Selected color: Primary
- Unselected color: OnSurface.copy(alpha = 0.6f)
- Icons: 24dp, Material Design Islamic-appropriate alternatives
- Labels: Caption style, 12sp
```

**Navigation Items**:
- 🏠 Home (Beranda)
- 📿 Prayer (Doa)  
- 👥 Community (Komunitas)
- 📖 Library (Perpustakaan)
- 👤 Profile (Profil)

#### 1.2 App Bar Components
```kotlin
@Composable
fun TahlilTopAppBar(
    title: String,
    navigationIcon: @Composable (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {},
    scrollBehavior: TopAppBarScrollBehavior? = null
)

Design Specs:
- Height: 64dp (small), 80dp (medium), 96dp (large)
- Background: Primary color
- Content color: OnPrimary
- Title: H6 style, 20sp, medium weight
- Elevation: 4dp
```

### 2. 🖼️ Photo Components

#### 2.1 Memorial Photo Card
```kotlin
@Composable
fun MemorialPhotoCard(
    photo: String,
    name: String,
    dates: String,
    prayerCount: Int,
    onPhotoClick: () -> Unit,
    onPrayerClick: () -> Unit,
    modifier: Modifier = Modifier
)

Design Specs:
- Card size: 280dp x 320dp
- Corner radius: 16dp
- Photo aspect ratio: 3:4
- Elevation: 8dp
- Overlay gradient: Black 0% to 60% opacity
```

#### 2.2 Photo Picker
```kotlin
@Composable
fun PhotoPickerComponent(
    selectedPhoto: Uri?,
    onPhotoSelected: (Uri) -> Unit,
    onCameraClick: () -> Unit,
    modifier: Modifier = Modifier
)

Features:
- Gallery selection
- Camera capture
- Photo cropping (3:4 ratio)
- Islamic frame overlays (optional)
```

#### 2.3 Photo Frame Overlay
```kotlin
@Composable
fun IslamicPhotoFrame(
    frameType: FrameType,
    modifier: Modifier = Modifier
)

Frame Types:
- GEOMETRIC_PATTERN: Islamic geometric border
- CALLIGRAPHY_BORDER: Arabic calligraphy frame
- MOSQUE_SILHOUETTE: Mosque silhouette overlay
- CRESCENT_STARS: Crescent and stars pattern
```

### 3. 📿 Prayer Components

#### 3.1 Prayer Counter
```kotlin
@Composable
fun TahlilCounter(
    currentCount: Int,
    targetCount: Int,
    onCountIncrement: () -> Unit,
    onCountReset: () -> Unit,
    modifier: Modifier = Modifier
)

Design Specs:
- Counter circle: 160dp diameter
- Progress ring: 8dp width
- Tap area: 200dp (larger than visual)
- Haptic feedback: Light impact
- Animation: Scale 1.0 -> 1.1 -> 1.0 on tap
```

#### 3.2 Prayer Text Display
```kotlin
@Composable
fun PrayerTextCard(
    arabicText: String,
    transliteration: String?,
    translation: String?,
    language: Language,
    isRTL: Boolean,
    modifier: Modifier = Modifier
)

Typography:
- Arabic: 24sp, Noto Sans Arabic font
- Transliteration: 16sp, regular weight
- Translation: 14sp, body2 style
- Line height: 1.6x for readability
```

#### 3.3 Progress Indicator
```kotlin
@Composable
fun PrayerProgress(
    completed: Int,
    total: Int,
    showPercentage: Boolean = true,
    modifier: Modifier = Modifier
)

Visual Elements:
- Circular progress: Primary color
- Background track: Surface variant
- Percentage text: H4 style, center aligned
- Animation: Smooth progress updates
```

### 4. 💬 Social Components

#### 4.1 Share Button
```kotlin
@Composable
fun ShareMemorialButton(
    memorial: Memorial,
    platforms: List<SocialPlatform>,
    onShareClick: (SocialPlatform) -> Unit,
    modifier: Modifier = Modifier
)

Platforms:
- WhatsApp: Green #25D366
- Facebook: Blue #1877F2
- Twitter: Blue #1DA1F2
- Instagram: Gradient purple/pink
- Telegram: Blue #0088CC
```

#### 4.2 Community Prayer Feed
```kotlin
@Composable
fun CommunityFeedItem(
    user: User,
    memorial: Memorial,
    prayerCount: Int,
    timestamp: Long,
    onUserClick: () -> Unit,
    onMemorialClick: () -> Unit,
    modifier: Modifier = Modifier
)

Layout:
- User avatar: 40dp circle
- Memorial photo: 64dp x 80dp (3:4 ratio)
- Text content: Flexible width
- Privacy badge: 16dp when shown
```

### 5. 🌍 Cultural Components

#### 5.1 Language Selector
```kotlin
@Composable
fun LanguageSelector(
    currentLanguage: Language,
    availableLanguages: List<Language>,
    onLanguageSelected: (Language) -> Unit,
    modifier: Modifier = Modifier
)

Supported Languages:
- Arabic (العربية) - RTL
- English - LTR
- Indonesian (Bahasa Indonesia) - LTR
- Urdu (اردو) - RTL
- Turkish (Türkçe) - LTR
- Persian (فارسی) - RTL
- And 14 more languages
```

#### 5.2 Cultural Adaptation Card
```kotlin
@Composable
fun CulturalSetupCard(
    region: Region,
    traditions: List<Tradition>,
    onTraditionSelected: (Tradition) -> Unit,
    modifier: Modifier = Modifier
)

Regional Adaptations:
- Middle East: Traditional Islamic patterns
- Southeast Asia: Local Islamic art styles
- South Asia: Mughal-inspired designs
- Africa: Geometric Islamic patterns
- Europe/Americas: Modern minimalist approach
```

### 6. 📅 Date & Time Components

#### 6.1 Islamic Date Picker
```kotlin
@Composable
fun IslamicDatePicker(
    selectedDate: LocalDate,
    showHijriDate: Boolean,
    onDateSelected: (LocalDate) -> Unit,
    modifier: Modifier = Modifier
)

Features:
- Gregorian calendar
- Hijri calendar option
- Islamic holy days highlighted
- Respectful date formatting
```

#### 6.2 Prayer Time Display
```kotlin
@Composable
fun PrayerTimeCard(
    prayerTimes: PrayerTimes,
    currentTime: LocalTime,
    location: Location,
    modifier: Modifier = Modifier
)

Display Elements:
- Next prayer countdown
- All 5 daily prayers
- Sunrise/sunset times
- Location-based calculations
```

---

## 🎨 Design Tokens

### 📊 Color System

```kotlin
// TahlilColors.kt
object TahlilColors {
    // Primary Colors
    val Primary = Color(0xFF1B5E20)          // Deep Islamic Green
    val PrimaryVariant = Color(0xFF2E7D32)    // Medium Green
    val Secondary = Color(0xFFBF9000)         // Elegant Gold
    val SecondaryVariant = Color(0xFFFFB300)  // Bright Gold
    
    // Surface Colors
    val Surface = Color(0xFFFAFAFA)           // Light Gray
    val Background = Color(0xFFFFFFFF)        // Pure White
    val SurfaceVariant = Color(0xFFF5F5F5)    // Very Light Gray
    
    // Content Colors
    val OnPrimary = Color(0xFFFFFFFF)         // White on Primary
    val OnSecondary = Color(0xFF000000)       // Black on Secondary
    val OnSurface = Color(0xFF1C1C1C)         // Dark Gray
    val OnBackground = Color(0xFF1C1C1C)      // Dark Gray
    
    // Semantic Colors
    val Error = Color(0xFFD32F2F)             // Red for errors
    val Success = Color(0xFF388E3C)           // Green for success
    val Warning = Color(0xFFF57C00)           // Orange for warnings
    val Info = Color(0xFF1976D2)              // Blue for information
    
    // Cultural Variants
    object Regional {
        val MiddleEast = Color(0xFF1B5E20)     // Traditional Green
        val SoutheastAsia = Color(0xFF2E7D32)  // Emerald Green  
        val SouthAsia = Color(0xFF4A148C)      // Royal Purple
        val Africa = Color(0xFFBF360C)         // Terracotta
        val WestEurope = Color(0xFF263238)     // Modern Charcoal
    }
}
```

### 📝 Typography System

```kotlin
// TahlilTypography.kt
object TahlilTypography {
    val H1 = TextStyle(
        fontSize = 32.sp,
        fontWeight = FontWeight.Bold,
        lineHeight = 40.sp
    )
    
    val H2 = TextStyle(
        fontSize = 28.sp,
        fontWeight = FontWeight.Bold,
        lineHeight = 36.sp
    )
    
    val H3 = TextStyle(
        fontSize = 24.sp,
        fontWeight = FontWeight.SemiBold,
        lineHeight = 32.sp
    )
    
    val Body1 = TextStyle(
        fontSize = 16.sp,
        fontWeight = FontWeight.Normal,
        lineHeight = 24.sp
    )
    
    val Body2 = TextStyle(
        fontSize = 14.sp,
        fontWeight = FontWeight.Normal,
        lineHeight = 20.sp
    )
    
    val Caption = TextStyle(
        fontSize = 12.sp,
        fontWeight = FontWeight.Normal,
        lineHeight = 16.sp
    )
    
    // Arabic Text Styles
    val ArabicLarge = TextStyle(
        fontSize = 24.sp,
        fontWeight = FontWeight.Normal,
        lineHeight = 36.sp,
        fontFamily = FontFamily(Font(R.font.noto_sans_arabic))
    )
    
    val ArabicMedium = TextStyle(
        fontSize = 18.sp,
        fontWeight = FontWeight.Normal,
        lineHeight = 28.sp,
        fontFamily = FontFamily(Font(R.font.noto_sans_arabic))
    )
}
```

### 📐 Spacing System

```kotlin
// TahlilSpacing.kt
object TahlilSpacing {
    val XS = 4.dp
    val SM = 8.dp
    val MD = 16.dp
    val LG = 24.dp
    val XL = 32.dp
    val XXL = 48.dp
    val XXXL = 64.dp
    
    // Component-specific spacing
    val CardPadding = MD
    val ScreenPadding = LG
    val SectionSpacing = XL
    val ComponentSpacing = SM
}
```

### 🎭 Animation Specifications

```kotlin
// TahlilAnimations.kt
object TahlilAnimations {
    val FastEasing = FastOutSlowInEasing
    val StandardDuration = 300.milliseconds
    val LongDuration = 500.milliseconds
    
    // Common animations
    val FadeIn = fadeIn(
        animationSpec = tween(
            durationMillis = StandardDuration,
            easing = FastEasing
        )
    )
    
    val SlideUp = slideInVertically(
        animationSpec = tween(
            durationMillis = StandardDuration,
            easing = FastEasing
        ),
        initialOffsetY = { it / 4 }
    )
    
    val ScaleTap = scaleIn(
        animationSpec = tween(
            durationMillis = 100,
            easing = FastEasing
        ),
        initialScale = 0.95f
    )
}
```

---

## 🛠️ Implementation Guidelines

### 📋 Component Usage Rules

1. **Consistent Naming**: All components start with "Tahlil" prefix
2. **Modifier First**: Always include `modifier: Modifier = Modifier` parameter last
3. **State Hoisting**: Keep components stateless when possible
4. **Accessibility**: Include contentDescription for all images and icons
5. **RTL Support**: Test all components with RTL languages
6. **Cultural Testing**: Validate with regional design guidelines

### 🎨 Design Standards

```kotlin
// Component Template
@Composable
fun TahlilComponentName(
    // Required parameters first
    requiredParam: String,
    // Optional parameters
    optionalParam: String? = null,
    // Event handlers
    onEvent: () -> Unit = {},
    // Modifier always last
    modifier: Modifier = Modifier
) {
    // Implementation with proper spacing, colors, and typography
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = TahlilColors.Surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 8.dp
        )
    ) {
        // Component content
    }
}
```

### 🔍 Quality Checklist

**Before Development Handoff**:
- [ ] All components follow design system
- [ ] RTL layout support implemented
- [ ] Cultural adaptations considered
- [ ] Accessibility features included
- [ ] Animation specifications defined
- [ ] Error states designed
- [ ] Loading states specified
- [ ] Dark mode variants created
- [ ] Responsive behavior documented
- [ ] Islamic appropriateness validated

---

## 📦 Component Library Structure

```
/design-system
├── /components
│   ├── /navigation
│   │   ├── TahlilBottomNavBar.kt
│   │   └── TahlilTopAppBar.kt
│   ├── /photos
│   │   ├── MemorialPhotoCard.kt
│   │   ├── PhotoPickerComponent.kt
│   │   └── IslamicPhotoFrame.kt
│   ├── /prayers
│   │   ├── TahlilCounter.kt
│   │   ├── PrayerTextCard.kt
│   │   └── PrayerProgress.kt
│   ├── /social
│   │   ├── ShareMemorialButton.kt
│   │   └── CommunityFeedItem.kt
│   └── /cultural
│       ├── LanguageSelector.kt
│       └── CulturalSetupCard.kt
├── /tokens
│   ├── TahlilColors.kt
│   ├── TahlilTypography.kt
│   ├── TahlilSpacing.kt
│   └── TahlilAnimations.kt
└── /themes
    ├── TahlilTheme.kt
    └── RegionalThemes.kt
```

---

**Component Library Maintained By**: Team Design + System Analyst  
**Implementation Start**: May 24, 2026  
**Development Handoff**: May 27, 2026  
**Testing Phase**: May 29, 2026  

*This component library ensures consistent, culturally-appropriate, and accessible design implementation across the global Tahlil platform.*