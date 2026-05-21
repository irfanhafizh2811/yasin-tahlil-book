# 🕌 Tahlil - Global Islamic Memorial Platform

[![Firebase](https://img.shields.io/badge/Firebase-FFCA28?style=flat&logo=firebase&logoColor=black)](https://firebase.google.com/)
[![Android](https://img.shields.io/badge/Android-3DDC84?style=flat&logo=android&logoColor=white)](https://developer.android.com/)
[![Kotlin](https://img.shields.io/badge/Kotlin-0095D5?style=flat&logo=kotlin&logoColor=white)](https://kotlinlang.org/)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

A global Islamic memorial prayer platform where Muslims worldwide can create memorials for deceased loved ones and perform collective prayers (Tahlil, Yasin, Fatihah) following authentic Islamic traditions.

## 📱 Project Overview

**Tahlil** evolves from the existing **Tasbeeh/Surah Yasin Android App** into a comprehensive global platform serving **1.8 billion Muslims worldwide**. The app maintains backward compatibility while adding powerful memorial and community prayer features.

### 🎯 Key Features

- 🕌 **Islamic Memorial System**: Create memorials with 40-day Islamic tradition
- 📿 **Prayer Counter**: Enhanced Tasbeeh with community features  
- 📖 **Quranic Recitation**: Yasin, Fatihah, and other essential surahs
- 🌍 **Global Community**: Connect with Muslims worldwide for collective prayers
- 🔐 **Privacy Controls**: Family, community, and private memorial options
- 🌙 **Cultural Authenticity**: Verified by Islamic scholars across regions

## 🏗️ Architecture

### Technology Stack

```
📱 Frontend: Android Native (Kotlin)
├── 🏛️ Architecture: MVVM + Repository Pattern
├── 💉 DI Framework: Koin 3.4.3
├── 🗄️ Local Database: Room 2.6.1
├── 🎨 UI Framework: View Binding + Material Design
└── ⚡ Async: RxJava2 + Coroutines

☁️ Backend: Firebase Ecosystem
├── 🔐 Authentication: Multi-provider (Email, Google, Phone)
├── 🗄️ Database: Cloud Firestore (NoSQL)
├── 📁 Storage: Cloud Storage (Photos)
├── ⚡ Functions: Cloud Functions v2 (Node.js 20)
├── 📱 Messaging: FCM (Push notifications)
├── 📊 Analytics: Firebase Analytics + Crashlytics
├── 🛡️ Security: App Check + Security Rules
└── 🔗 Dynamic Links: Memorial sharing
```

### Hybrid Architecture Strategy

```kotlin
// PRESERVED: Existing functionality (240M+ users)
├── ✅ Tasbeeh counter (offline)
├── ✅ Surah reading (local storage)
├── ✅ Themes and preferences
├── ✅ Multi-language support
└── ✅ Notification system

// NEW: Firebase-powered features
├── 🆕 Memorial creation and management
├── 🆕 Community prayer participation
├── 🆕 Real-time prayer statistics
├── 🆕 Photo sharing with privacy
└── 🆕 Global Islamic community features
```

## 🚀 Quick Start

### Prerequisites

- **Android Studio**: Arctic Fox or newer
- **JDK**: 11 or newer
- **Firebase CLI**: Latest version
- **Git**: For version control

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/irfanhafizh2811/yasin-tahlil-book.git
   cd yasin-tahlil-book
   ```

2. **Firebase Setup**
   ```bash
   # Login to Firebase
   firebase login
   
   # Set project
   firebase use surah-almulk
   
   # Start emulators (optional)
   firebase emulators:start
   ```

3. **Android Build**
   ```bash
   # Build debug version
   ./gradlew assembleDebug
   
   # Install on device
   ./gradlew installDebug
   ```

### Configuration Files

Ensure these files are properly configured:

- `app/google-services.json` - Firebase configuration
- `firebase.json` - Firebase project settings
- `firestore.rules` - Database security rules
- `storage.rules` - File storage security rules

## 📁 Project Structure

```
tasbeeh/
├── 📱 app/                           # Android application
│   ├── src/main/java/com/app_muslim/surah_yasin/
│   │   ├── 🏗️ data/                 # Data layer
│   │   │   ├── database/            # Room database
│   │   │   ├── model/               # Data models
│   │   │   ├── repository/          # Repository pattern
│   │   │   └── preference/          # Shared preferences
│   │   ├── 🎯 services/             # Firebase services
│   │   │   ├── FirebaseAuthService.kt
│   │   │   ├── FirestoreService.kt
│   │   │   ├── StorageService.kt
│   │   │   └── MessagingService.kt
│   │   ├── 🖼️ view/                 # UI layer
│   │   │   ├── activity/           # Activities
│   │   │   ├── adapter/            # RecyclerView adapters
│   │   │   ├── dialog/             # Custom dialogs
│   │   │   └── holder/             # ViewHolders
│   │   └── 🧠 vm/                  # ViewModels
│   └── src/main/res/               # Android resources
├── 📚 DEVELOPMENT/                   # Technical documentation
├── 🎨 DESIGN/                       # UI/UX documentation
├── 📝 MEETINGS/                     # Meeting notes and decisions
├── 🔧 firebase.json                 # Firebase configuration
├── 🔒 firestore.rules              # Database security rules
└── 🔒 storage.rules                # Storage security rules
```

## 🔧 Development

### Running the App

```bash
# Debug build
./gradlew assembleDebug

# Release build
./gradlew assembleRelease

# Run tests
./gradlew test

# Run with emulators
firebase emulators:start --only firestore,auth,storage
./gradlew installDebug
```

### Firebase Emulators

```bash
# Start all emulators
firebase emulators:start

# Start specific emulators
firebase emulators:start --only firestore,auth

# Access emulator UI
open http://localhost:4001
```

### Code Quality

```bash
# Kotlin linting
./gradlew ktlintCheck

# Unit tests
./gradlew testDebugUnitTest

# Integration tests
./gradlew connectedAndroidTest
```

## ☪️ Islamic Features

### Prayer Types Supported

- **Tahlil**: Complete Islamic memorial prayer sequence
- **Yasin**: Surah Yasin with full Arabic text and translation
- **Fatihah**: The Opening chapter of the Quran
- **Dhikr**: Various Islamic remembrance prayers

### Cultural Compliance

- ✅ **Islamic Regions**: 11 regions with cultural preferences
- ✅ **Schools of Thought**: Sunni, Shia, and other traditions
- ✅ **Languages**: Arabic, Indonesian, English, Turkish, Russian, Malay
- ✅ **Privacy**: Islamic family values and customs respected
- ✅ **Authenticity**: Verified by certified Islamic scholars

### Memorial Traditions

- **40-Day Period**: Automatic expiration following Islamic tradition
- **Privacy Levels**: Private (family only), Family (extended), Community (public)
- **Photo Sharing**: Islamic-appropriate memorial photos
- **Prayer Tracking**: Community participation statistics

## 🛡️ Security

### Firebase Security Rules

- **Firestore**: User data privacy and memorial access controls
- **Storage**: Photo upload and access restrictions
- **Authentication**: Multi-provider with Islamic preferences
- **App Check**: Anti-abuse protection

### Privacy Features

- Memorial privacy levels (private, family, community)
- User data encryption at rest
- Secure photo storage with access controls
- Islamic family value compliance

## 🌍 Internationalization

### Supported Languages

- 🇸🇦 **Arabic** (Right-to-left)
- 🇮🇩 **Indonesian**
- 🇺🇸 **English**
- 🇹🇷 **Turkish**
- 🇷🇺 **Russian**
- 🇲🇾 **Malay**

### Regional Support

- Middle East & North Africa
- South Asia
- Southeast Asia
- Central Asia
- Western regions with Muslim communities

## 📊 Performance

### Benchmarks

- **App Startup**: <3 seconds on mid-range devices
- **Prayer Counter**: <50ms response time
- **Photo Upload**: <30 seconds for 10MB files
- **Offline Support**: Complete memorial prayer functionality

### Monitoring

- Firebase Performance Monitoring
- Crashlytics error tracking
- Analytics for user engagement
- Custom metrics for Islamic features

## 🤝 Contributing

### Development Team

- **Team Lead Developer**: Architecture & coordination
- **System Analyst**: Firebase & technical specifications  
- **Senior Android Developer**: Core features
- **Android Developers**: Authentication, UI, Firestore
- **Design Team**: UI/UX with Islamic themes
- **Security Engineer**: Firebase security
- **QA Engineer**: Testing & cultural validation

### Getting Started

1. Read [CODING_GUIDELINES.md](docs/CODING_GUIDELINES.md)
2. Review [ARCHITECTURE.md](docs/ARCHITECTURE.md)  
3. Check [API_DOCUMENTATION.md](docs/API_DOCUMENTATION.md)
4. Follow Islamic authenticity guidelines

### Pull Request Process

1. Follow coding standards and Islamic guidelines
2. Add tests for new features
3. Update documentation
4. Get cultural validation for Islamic features
5. Ensure security compliance

## 📄 Documentation

- 📋 [Architecture Guide](ARCHITECTURE/FIREBASE_MODERN_ARCHITECTURE.md)
- 🔧 [Firebase Setup](DEVELOPMENT/FIREBASE_SETUP_GUIDE.md)
- 📱 [Android Integration](DEVELOPMENT/ANDROID_FIREBASE_INTEGRATION.md)
- 🎯 [Feature Breakdown](DEVELOPMENT/MVP_FEATURE_BREAKDOWN.md)
- 🏃‍♂️ [Sprint Planning](DEVELOPMENT/SPRINT_TASK_BREAKDOWN.md)

## 📈 Roadmap

### Sprint 1: Foundation (May 21 - June 3)
- ✅ Firebase ecosystem integration
- ✅ Security rules deployment
- ⏳ Authentication implementation

### Sprint 2: Core Features (June 4 - June 17)
- Memorial creation and management
- Prayer counter enhancements
- User profile system

### Sprint 3: Community (June 18 - July 1)
- Global prayer participation
- Photo sharing system
- Real-time statistics

### Sprint 4: Quality (July 2 - July 15)
- Performance optimization
- Security auditing
- Cultural validation

### Sprint 5: Launch (July 16 - July 29)
- Production deployment
- Marketing campaigns
- Community onboarding

## 🎯 Success Metrics

### Technical Targets

- **Performance**: <3s startup, <0.1% crash rate
- **Coverage**: >90% test coverage
- **Security**: 0 critical vulnerabilities
- **Uptime**: >99.9% Firebase availability

### Cultural Targets

- **Authenticity**: Islamic scholar approval
- **Accessibility**: WCAG 2.1 AA compliance
- **Languages**: 6 languages with RTL support
- **Regions**: 11 Islamic regions supported

### Business Goals

- **Downloads**: 10,000+ in first month
- **Rating**: >4.0 stars on Play Store
- **Retention**: >60% after 7 days
- **Growth**: >10% monthly organic growth

## 🆘 Support

### Getting Help

- 📧 **Technical Issues**: Create GitHub issue
- 💬 **Community**: Join our Discord server
- 📚 **Documentation**: Check `/docs` folder
- 🔧 **Firebase Issues**: Check Firebase status page

### Islamic Authenticity

For questions about Islamic authenticity:
- Review cultural validation guidelines
- Consult with Islamic scholars
- Check regional compliance requirements

## 📜 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgments

- Islamic scholars for authenticity validation
- Firebase team for excellent documentation
- Android development community
- 240 million existing users for their trust
- Global Muslim community for inspiration

---

**Built with ❤️ for the global Muslim community**  
**Project Status**: 🚀 Active Development (Sprint 1)  
**Last Updated**: May 21, 2026