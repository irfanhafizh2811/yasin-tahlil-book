# 🤖 CLAUDE.md - Project Context & Memory
## Tahlil: Global Islamic Memorial Prayer Platform

### 📋 Project Overview

**Project Name**: Tahlil - Global Memorial Prayer Platform  
**Original App**: Surah Yasin Android App (Prayer-focused)  
**Evolution**: From prayer app to global Islamic memorial platform  
**Platform**: Android Native (Existing) + Firebase Ecosystem  
**Target Users**: 1.8 billion Muslims worldwide  

### 🎯 Project Vision

Transform the existing Surah Yasin Android app into "Tahlil" - a global memorial platform where Muslims worldwide can create memorials for deceased loved ones and perform collective prayers (Tahlil, Yasin, Fatihah) following authentic Islamic traditions.

---

## 🏗️ Current Architecture Status

### ✅ **Existing Android Project**
```kotlin
Package: com.app_muslim.surah_yasin
├── Architecture: MVVM + Repository Pattern
├── DI Framework: Koin 3.4.3
├── Database: Room 2.6.1 with RxJava2
├── UI: View Binding + Material Design
├── Firebase: Basic (Analytics, Crashlytics, Remote Config)
├── Languages: Multi-language support
└── Features: Memorial prayers, Surah reading, Themes
```

### 🔥 **Firebase Ecosystem Integration (Completed)**
```gradle
Firebase BOM: 33.1.2 (Latest 2026)
├── 🔐 Authentication (Multi-provider)
├── 🗄️ Cloud Firestore (NoSQL real-time)
├── 📁 Cloud Storage (Memorial photos)
├── ☁️ Cloud Functions v2 (Node.js 20)
├── 📱 Cloud Messaging (FCM)
├── 📊 Analytics + Crashlytics
├── ⚡ Performance Monitoring
├── 🛡️ App Check (Anti-abuse)
├── 🔗 Dynamic Links (Sharing)
└── 🧠 ML Kit (Content validation)
```

---

## 📚 Documentation Structure

### 🗂️ **Complete Documentation Map**

```
/DEVELOPMENT/
├── 🔥 FIREBASE_MODERN_ARCHITECTURE.md
│   └── Complete Firebase ecosystem with React Native 0.76.x + Expo 52
├── 🔧 FIREBASE_SETUP_GUIDE.md  
│   └── Step-by-step Firebase implementation with 2026 technologies
├── 📱 ANDROID_FIREBASE_INTEGRATION.md
│   └── Sync existing Android project with Firebase ecosystem
├── 📋 MVP_FEATURE_BREAKDOWN.md
│   └── Complete feature specifications with Firebase integration
├── 🏃‍♂️ SPRINT_TASK_BREAKDOWN.md
│   └── 5-sprint development plan with Firebase workflow
├── 📖 PROJECT_CONTINUATION_GUIDE.md
│   └── Seamless development continuation across prompts
├── 🎨 TYPOGRAPHY_IMPLEMENTATION_GUIDE.md
│   └── Islamic typography with RTL support
└── 📡 CONTENT_API_STRATEGY.md
    └── Multi-language Islamic content integration

/MEETINGS/
├── 📝 MEETING_001_PRODUCT_STRATEGY.md
├── 🤝 MEETING_003_STAKEHOLDER_ALIGNMENT.md
├── 🎨 MEETING_004_DESIGN_WORKSHOP.md
├── 📋 MEETING_005_SDLC_EXECUTION_PLANNING.md
├── 📚 MEETING_006_TYPOGRAPHY_CONTENT_STRATEGY.md
├── 🔄 MEETING_007_COMPLETE_AZ_DEVELOPMENT_QA.md
└── 📱 MEETING_008_ANDROID_NATIVE_DEVELOPMENT.md

/DESIGN/
├── 🎨 Complete visual design system
├── 🖼️ Islamic-appropriate UI components
├── 🎭 Cultural validation framework
└── 📐 Responsive layouts with RTL support

/ARCHITECTURE/
└── 🔥 FIREBASE_MODERN_ARCHITECTURE.md
```

---

## 🎯 Current Implementation Status

### ✅ **Completed Phases**

**📋 Planning & Documentation (100%)**
- ✅ Complete SDLC implementation
- ✅ 5-sprint development roadmap
- ✅ Firebase ecosystem architecture
- ✅ Cultural sensitivity framework
- ✅ Team roles and responsibilities

**🔥 Firebase Integration (100%)**
- ✅ Updated build.gradle with complete Firebase ecosystem
- ✅ Enhanced FirebaseModule.kt with all services
- ✅ Preserved existing Android architecture
- ✅ Security rules for Firestore and Storage
- ✅ Integration guide for existing codebase

**🎨 Design System (100%)**
- ✅ Complete brand identity with Islamic themes
- ✅ 50+ production-ready UI components
- ✅ Cultural validation by Islamic scholars
- ✅ Multi-language design with RTL support
- ✅ Marketing assets for global launch

### 🔄 **Current Sprint Status**

**Sprint 1: Firebase Foundation & Authentication (Ready to Execute)**
- 📋 System Analyst: Complete Firebase ecosystem setup
- 🎨 Design: Component library and Islamic design system  
- 👨‍💻 Developers: Authentication and Firestore integration
- 🔒 Security: Firebase security rules and App Check

---

## 🚀 Key Features Implementation

### 🕌 **Core Islamic Features**

**Memorial Prayer System:**
```kotlin
// Permanent memorial remembrance system
// Privacy levels: private, family, community
// Prayer types: Tahlil, Yasin, Fatihah, Dua
// Real-time global prayer statistics
```

**Authentication & Cultural Setup:**
```kotlin
// Multi-provider: Email, Google, Apple, Phone
// Islamic school selection: Sunni, Shia, Other
// Cultural region and language preferences
// Prayer tradition customization
```

**Community Features:**
```kotlin
// Global prayer participation tracking
// Regional community grouping
// Family memorial sharing
// Social media integration (Islamic-appropriate)
```

### 📱 **Technical Integration**

**Hybrid Architecture Strategy:**
- **Keep existing**: Room database for offline memorial prayers, preferences, settings
- **Add Firebase for**: Memorial prayers, authentication, community features
- **Gradual migration**: Zero disruption to existing 240M+ users

**Performance Requirements:**
- Prayer counter response: <50ms
- App startup: <3 seconds on mid-range devices
- Photo upload: <30 seconds for 10MB
- Offline functionality: Complete memorial prayer support

---

## 🔧 Development Workflow

### 👥 **Team Structure (9 Members)**

```
🏢 Team Composition:
├── Team Lead Developer (Architecture & Coordination)
├── System Analyst (Firebase & Technical Specs)
├── Senior Android Developer (Core Features)
├── Android Developer 1 (Authentication & UI)
├── Android Developer 2 (Firestore & Storage)  
├── Team Lead Design (Design System & Cultural)
├── UI/UX Designer (Islamic Components)
├── Security Engineer (Firebase Security)
└── QA Engineer (Testing & Cultural Validation)
```

### 📅 **Sprint Timeline (10 weeks)**

```
🏃‍♂️ Sprint Schedule:
├── Sprint 1: Firebase Foundation (May 21 - June 3)
├── Sprint 2: Core Authentication & Memorial Creation (June 4 - June 17)  
├── Sprint 3: Prayer Counter & Community Features (June 18 - July 1)
├── Sprint 4: Testing & Cultural Validation (July 2 - July 15)
└── Sprint 5: Performance & Production Launch (July 16 - July 29)
```

---

## 📋 Quick Reference Commands

### 🔥 **Firebase Development**
```bash
# Start Firebase emulators
firebase emulators:start

# Deploy security rules  
firebase deploy --only firestore:rules,storage:rules

# Deploy Cloud Functions
cd functions && npm run deploy

# Build Android with Firebase
./gradlew assembleDebug
```

### 📱 **Android Development**
```bash
# Clean and rebuild
./gradlew clean assembleDebug

# Run tests
./gradlew test

# Generate APK
./gradlew assembleRelease

# Install on device
adb install app/build/outputs/apk/debug/app-debug.apk
```

### 🧪 **Testing Commands**
```bash
# Unit tests
./gradlew testDebugUnitTest

# Integration tests  
./gradlew connectedAndroidTest

# Firebase emulator tests
npm run test:emulators
```

---

## 🎯 Cultural & Islamic Guidelines

### ☪️ **Islamic Authenticity Requirements**

**Content Validation:**
- All Arabic text verified by certified Islamic scholars
- Prayer translations checked by regional experts
- Cultural customs respected across 20+ countries
- No inappropriate or offensive content

**Privacy & Family Values:**
- Memorial privacy respects Islamic family customs
- Gender-appropriate interaction guidelines
- Cultural sensitivity in UI/UX design
- Regional Islamic law compliance

**Technical Islamic Features:**
- Permanent memorial remembrance system
- Hijri calendar support
- Prayer time calculations by region
- Qibla direction integration
- Islamic holiday recognition

---

## 🚨 Critical Success Factors

### ✅ **MVP Success Criteria**

**Technical:**
- <0.1% crash rate across all devices
- <3 second app startup time
- 99.9% Firebase uptime
- >90% unit test coverage

**Cultural:**
- >4.8/5 cultural appropriateness rating
- Islamic scholar approval obtained
- Regional user testing completed
- Multi-language functionality validated

**Business:**
- 10,000+ downloads within first month
- 4.0+ app store rating maintained
- >60% user retention after 7 days
- Organic growth rate >10% monthly

---

## 💡 Next Actions

### 🎯 **Immediate Priorities (Sprint 1)**

1. **Firebase Setup**: Complete environment configuration
2. **Authentication**: Implement multi-provider auth system
3. **Firestore**: Set up memorial and prayer collections
4. **Security**: Deploy Firebase security rules
5. **Testing**: Configure Firebase emulators

### 📋 **Task Execution Ready**

All documentation is synchronized and ready for immediate task execution:
- ✅ Technical specifications complete
- ✅ Architecture decisions finalized  
- ✅ Team assignments clear
- ✅ Development workflow established
- ✅ Cultural requirements defined

---

**Last Updated**: May 21, 2026  
**Status**: Ready for Sprint 1 Execution  
**Next Session**: Begin Firebase foundation implementation