# 📊 Flow Diagrams - Tahlil Global Platform

**Project**: Tahlil Global Platform  
**Design Phase**: User Journey Flow Documentation  
**Created**: May 21, 2026 (Post Design Workshop)  
**Design Team**: UX Researcher + UI/UX Team Lead  
**Status**: In Progress  

---

## 🎯 Flow Diagram Methodology

### 📋 **Flow Documentation Standards**

#### **Diagram Types**
1. **User Journey Flows**: End-to-end user experiences from entry to completion
2. **Process Flow Diagrams**: Step-by-step processes within features
3. **Decision Trees**: Logic flows for cultural adaptation and privacy
4. **System Integration Flows**: Technical flows for social media and community features
5. **Error & Edge Case Flows**: Alternative paths and error recovery

#### **Flow Notation System**
```
Symbols Used:
├── ┌────────┐ : Process/Screen
├── ◊ : Decision Point
├── ○ : Start/End Point  
├── → : User Action Flow
├── ⟷ : Bidirectional Flow
├── ╔════╗ : Critical Process
├── ╭─────╮ : Optional Process
└── ⚠️ : Error/Warning State
```

---

## 🗺️ **Primary User Journey Flows**

### 📱 **Flow A: Global User Onboarding Journey**

```
                            ○ START
                       App Download/Install
                              │
                              ▼
                    ┌─────────────────────┐
                    │   Welcome Screen    │
                    │  🌍 Choose Region   │
                    │  🕌 Tahlil Global   │
                    └──────────┬──────────┘
                              │ User taps region
                              ▼
               ┌──────────────────────────────────────┐
               │              ◊                      │
               │     Cultural Region Selection       │
               │                                     │
               ├─────┬────────┬────────┬─────────────┤
               ▼     ▼        ▼        ▼             ▼
    ┌─────────────┐ │  ┌────────┐ ┌─────────┐ ┌─────────────┐
    │Middle East  │ │  │SE Asia │ │S. Asia  │ │   Other     │
    │🇸🇦 Arabic   │ │  │🇮🇩 Indo │ │🇵🇰 Urdu │ │🌍 Global    │
    │Traditions   │ │  │Malay    │ │Hindi    │ │English      │
    └──────┬──────┘ │  └───┬────┘ └────┬────┘ └──────┬──────┘
           │        │      │           │             │
           └────────┼──────┼───────────┼─────────────┘
                    ▼      │           │
         ╔══════════════════╗          │
         ║  Cultural Setup  ║          │
         ║                 ║          │
         ║ 🕌 Tradition:    ║          │
         ║ ◉ Sunni         ║          │
         ║ ○ Shia          ║          │
         ║ ○ Regional      ║          │
         ║                 ║          │
         ║ 🗣️ Languages:    ║          │
         ║ Arabic + Local  ║          │
         ║                 ║          │
         ║ 📅 Calendar:     ║          │
         ║ Hijri + Greg    ║          │
         ╚════════┬════════╝          │
                  │                   │
                  └───────────────────┘
                            │
                            ▼
                   ┌─────────────────┐
                   │   Tutorial      │
                   │   Introduction  │
                   │ 📖 How Tahlil   │
                   │    Works        │
                   └─────────┬───────┘
                            │
                   ┌────────◊────────┐
                   │  Skip Tutorial? │
                   └─────┬─────┬─────┘
                      Yes│     │No
                         │     ▼
                         │ ╭─────────────╮
                         │ │ Guided Tour │
                         │ │ 🎥 Demo     │
                         │ │ 📱 Features │
                         │ ╰──────┬──────╯
                         │        │
                         └────────┘
                                  │
                                  ▼
                        ┌─────────────────┐
                        │ First Memorial  │
                        │ Creation Guide  │
                        │ 📸 Photo Help   │
                        │ 📝 Info Form    │
                        └─────────┬───────┘
                                 │
                        ┌────────◊────────┐
                        │ Add Photo Now?  │
                        └─────┬─────┬─────┘
                           Yes│     │Skip
                              ▼     │
                    ┌─────────────────┐  │
                    │   Photo Upload  │  │
                    │ 📷 Camera       │  │
                    │ 📁 Gallery      │  │
                    │ 🤖 AI Enhance   │  │
                    └─────────┬───────┘  │
                             │          │
                             └──────────┘
                                      │
                                      ▼
                            ┌─────────────────┐
                            │ Basic Info Form │
                            │ Name (Required) │
                            │ Relationship    │
                            │ Dates (Option.) │
                            └─────────┬───────┘
                                     │
                            ┌────────◊────────┐
                            │ Privacy Choice  │
                            │ Who can see?    │
                            └─┬─────┬─────┬───┘
                           Family│ Community│Public
                              ▼     ▼     ▼
                         ┌─────────────────┐
                         │ Privacy Setup   │
                         │ 🔒 Settings     │
                         │ 👥 Sharing      │
                         └─────────┬───────┘
                                  │
                                  ▼
                         ┌─────────────────┐
                         │ First Prayer    │
                         │ Guided Experience│
                         │ 📿 Choose Yasin │
                         │ 🎵 Audio Guide  │
                         └─────────┬───────┘
                                  │
                                  ▼
                         ┌─────────────────┐
                         │ Prayer Reading  │
                         │ 📖 Sacred Text  │
                         │ 🔊 Recitation   │
                         │ 📊 Progress     │
                         └─────────┬───────┘
                                  │
                                  ▼
                         ┌─────────────────┐
                         │ Completion      │
                         │ ✨ Celebration  │
                         │ 🤲 Reflection   │
                         │ 📤 Share Option │
                         └─────────┬───────┘
                                  │
                         ┌────────◊────────┐
                         │ Share Prayer?   │
                         └─────┬─────┬─────┘
                            Yes│     │No
                               ▼     │
                      ╭─────────────╮│
                      │Social Share ││
                      │🌍 Platforms ││
                      │📝 Message   ││
                      ╰──────┬──────╯│
                             │       │
                             └───────┘
                                   │
                                   ▼
                         ┌─────────────────┐
                         │Community Intro  │
                         │👥 Global Network│
                         │🕐 Prayer Times  │
                         │🌍 Participation │
                         └─────────┬───────┘
                                  │
                                  ▼
                            ○ END: Home Screen
                              Ready to Use!
```

### 🤲 **Flow B: Daily Prayer Routine Journey**

```
                            ○ START
                        App Launch/Resume
                              │
                              ▼
                    ┌─────────────────────┐
                    │    Home Screen      │
                    │ 📱 Memorial Cards   │
                    │ 🌙 Context Banner   │
                    │ 👥 Community Info   │
                    └──────────┬──────────┘
                              │
                    ┌─────────◊──────────┐
                    │ Today's Context?   │
                    │ Special Day/Time?  │
                    └──┬─────────────┬───┘
                  Regular│      Special│(Malam Jumat, Anniversary, etc.)
                       ▼             ▼
              ┌─────────────────┐ ┌─────────────────┐
              │ Regular Cards   │ │ Special Banner  │
              │ 📸 All Memorials│ │ 🌙 Tonight:     │
              │ ⏰ Last Prayers │ │ Malam Jumat     │
              └─────────┬───────┘ │ 🎯 Suggested:   │
                       │         │ Yasin for Mom   │
                       │         └─────────┬───────┘
                       │                  │
                       └──────────────────┘
                               │
                               ▼
                    ┌─────────────────────┐
                    │ Memorial Selection  │
                    │ 📸 Photo Focus      │
                    │ Tap to Choose       │
                    └──────────┬──────────┘
                              │ User selects memorial
                              ▼
                    ┌─────────────────────┐
                    │ Memorial Detail     │
                    │ 👤 Full Profile     │
                    │ 📊 Prayer History   │
                    │ 🎯 Recommended      │
                    └──────────┬──────────┘
                              │
                    ┌─────────◊──────────┐
                    │ Prayer Type Choice │
                    └─┬────────┬────────┬─┘
                 Yasin│  Tahlil│   Dua │Custom
                     ▼        ▼       ▼
              ┌─────────┐ ┌─────────┐ ┌─────────┐
              │📿 Yasin │ │🤲 Tahlil│ │💫 Custom│
              │83 Verses│ │Memorial │ │Personal │
              │~15 mins │ │Prayer   │ │Choice   │
              └────┬────┘ └────┬────┘ └────┬────┘
                   │           │           │
                   └───────────┼───────────┘
                               │
                               ▼
                    ┌─────────◊──────────┐
                    │ Prayer Mode Choice │
                    └─┬─────────────┬────┘
                 Solo│     Community│
                     ▼             ▼
              ┌─────────────┐ ┌─────────────┐
              │ Solo Prayer │ │Join Session │
              │🤫 Private   │ │👥 Community │
              │📖 Personal  │ │🌍 Global    │
              └─────┬───────┘ │🕐 Scheduled │
                    │         └─────┬───────┘
                    │               │
                    └───────────────┘
                            │
                            ▼
                   ╔════════════════════╗
                   ║   Prayer Reading   ║
                   ║                   ║
                   ║ 📸 Memorial Photo ║
                   ║ 📖 Sacred Text    ║
                   ║ 🔊 Audio Option   ║
                   ║ 📊 Progress Track ║
                   ║ 👥 Community Info ║
                   ╚═════════┬══════════╝
                            │
                   ┌────────◊────────┐
                   │ Continue/Pause? │
                   └─┬────────┬─────┬─┘
               Continue│   Pause│  Quit│
                      ▼       ▼      ▼
               ┌─────────┐ ┌─────────┐ ┌─────────┐
               │Continue │ │Save     │ │Confirm  │
               │Reading  │ │Progress │ │Exit     │
               └────┬────┘ └────┬────┘ └────┬────┘
                    │           │           │
                    │ ┌─────────▼─────────┐ │
                    │ │   Resume Later    │ │
                    │ │ 🔖 Bookmark      │ │
                    │ │ ⏰ Set Reminder  │ │
                    │ └─────────┬─────────┘ │
                    │           │           │
                    └───────────┼───────────┘
                                │           │
                                └───────────┘
                                          │
                                          ▼
                                    Home Screen
                              (Prayer interrupted)
                    │
                    ▼ (Prayer completed)
            ┌─────────────────────┐
            │ Completion Screen   │
            │ ✨ Alhamdulillah!   │
            │ 📊 Prayer Stats     │
            │ 🤲 Reflection Space │
            │ ⭐ Spiritual Moment │
            └──────────┬──────────┘
                       │
            ┌─────────◊──────────┐
            │ Share This Prayer? │
            └─┬─────────────────┬─┘
         Share│               Keep│Private
             ▼                 ▼
    ┌─────────────────┐ ┌─────────────────┐
    │ Social Sharing  │ │ Personal Log    │
    │ 📤 Platforms    │ │ 📊 Statistics   │
    │ 📝 Message      │ │ 🤲 Reflection   │
    │ 🌍 Audience     │ │ 📅 History      │
    └─────────┬───────┘ └─────────┬───────┘
             │                   │
             └───────────────────┘
                       │
                       ▼
            ┌─────────────────────┐
            │ Post-Prayer Options │
            │ 🔄 Pray for Another │
            │ 👥 Family Sharing   │
            │ 📅 Set Next Reminder│
            │ 🏠 Return to Home   │
            └──────────┬──────────┘
                       │
            ┌─────────◊──────────┐
            │ Continue Praying?  │
            └─┬─────────────────┬─┘
         Yes│                No│
            ▼                 ▼
    ┌─────────────────┐      ○ END
    │ Select Next     │   Home Screen
    │ Memorial        │   Updated Status
    │ 🔄 Continue     │
    └─────────────────┘
            │
            ▼
    (Return to Memorial Selection)
```

### 📤 **Flow C: Social Prayer Sharing Journey**

```
                            ○ START
                      Prayer Completion
                              │
                              ▼
                    ┌─────────────────────┐
                    │ Completion Celebration│
                    │ ✨ Beautiful Animation│
                    │ 🤲 "Prayer Completed"│
                    │ 📊 Duration & Stats  │
                    └──────────┬──────────┘
                              │ Auto-progress after 3sec
                              ▼
                    ┌─────────────────────┐
                    │ Share Decision Point│
                    │ "Share this blessed │
                    │  moment?"           │
                    │ 📤 💝 🔒           │
                    └──────────┬──────────┘
                              │
                    ┌─────────◊──────────┐
                    │ User Choice?       │
                    └─┬─────┬──────┬─────┘
               Share│  Maybe│      │Private
                    ▼      │      ▼
          ┌─────────────────┐   │ ┌─────────────────┐
          │ Social Sharing  │   │ │ Keep Private    │
          │ Flow Continue   │   │ │ 📊 Personal Log │
          └─────────┬───────┘   │ │ 🤲 Reflection   │
                   │           │ └─────────┬───────┘
                   │           │          │
                   ▼           │          ▼
         ╔═══════════════════╗ │    ○ END: Home
         ║ Platform Selection ║ │    (Private mode)
         ║                  ║ │
         ║ Choose where to   ║ │
         ║ share:           ║ │
         ║                  ║ │
         ║ 📘 Facebook      ║ │
         ║ 💬 WhatsApp      ║ │
         ║ 🐦 Twitter       ║ │
         ║ 📱 Telegram      ║ │
         ║ 📧 Email         ║ │
         ║ 📋 Copy Link     ║ │
         ╚═════════┬════════╝ │
                  │          │
                  ▼          │
        ┌─────────────────────┐│
        │ Cultural Message    ││
        │ Template Selection  ││
        │                    ││
        │ 🌍 Choose Style:    ││
        │ ◉ Arabic Traditional││
        │ ○ Local Cultural   ││
        │ ○ Modern/Simple    ││
        │ ○ Custom Message   ││
        └─────────┬───────────┘│
                 │            │
                 ▼            │
        ┌─────────────────────┐│
        │ Message Customization│
        │                    ││
        │ 📝 Edit Text:       ││
        │ "الحمد لله، قرأت     ││
        │ سورة يس لأمي الحبيبة"││
        │                    ││
        │ "Alhamdulillah, I   ││
        │ just completed Yasin││
        │ for my beloved mom" ││
        │                    ││
        │ 🔤 Add Hashtags:    ││
        │ #Yasin #Prayer      ││
        │ #Mother #Memorial   ││
        └─────────┬───────────┘│
                 │            │
                 ▼            │
        ┌─────────────────────┐│
        │ Privacy & Content   ││
        │ Controls           ││
        │                    ││
        │ 📸 Include Photo?   ││
        │ ◉ Yes ○ No         ││
        │                    ││
        │ 👥 Audience:        ││
        │ ◉ Public           ││
        │ ○ Friends          ││
        │ ○ Family Only      ││
        │                    ││
        │ 🌍 Memorial Info:   ││
        │ ◉ Share Name       ││
        │ ○ Anonymous        ││
        └─────────┬───────────┘│
                 │            │
        ┌────────◊────────┐   │
        │ Final Review?   │   │
        │ Looks Good?     │   │
        └─┬────────┬─────┘    │
      Edit│     Post│          │
         ▼        ▼           │
┌─────────────┐ ┌─────────────┐│
│ Go Back to  │ │ Post to     ││
│ Edit/Cancel │ │ Platform    ││
└─────────────┘ └─────┬───────┘│
         │             │       │
         └─────────────┘       │
                               │
                               ▼
                    ┌─────────────────────┐
                    │ Posting Progress    │
                    │ 📤 Sharing...       │
                    │ 🔄 Platform API     │
                    └─────────┬───────────┘
                             │
                    ┌────────◊────────┐
                    │ Post Successful?│
                    └─┬────────┬─────┘
                Success│    Error│
                      ▼        ▼
            ┌─────────────────┐ ┌─────────────────┐
            │ Success Screen  │ │ Error Handling  │
            │ ✅ Shared!      │ │ ⚠️ Failed       │
            │ 👥 Visible on   │ │ 🔄 Retry?       │
            │    Platform     │ │ 📱 Save Draft?  │
            │ 💬 Engagement   │ └─────┬───────────┘
            └─────────┬───────┘       │
                     │               │
                     └───────────────┘
                             │
                             ▼
                    ┌─────────────────────┐
                    │ Community Response  │
                    │ 👥 Others may see   │
                    │ 💬 Comments/Replies │
                    │ 🤲 Prayer responses │
                    │ 📈 Community Impact │
                    └─────────┬───────────┘
                             │
                             ▼
                    ┌─────────────────────┐
                    │ Post-Sharing Options│
                    │ 📱 View on Platform │
                    │ 🔔 Notification Set │
                    │ 🤲 Continue Praying │
                    │ 🏠 Return to Home   │
                    └─────────┬───────────┘
                             │
                             ▼
                          ○ END
                    Home Screen Updated
                   (Sharing activity logged)
```

---

## 🌍 **Cultural Decision Trees**

### 🎨 **Cultural Adaptation Decision Tree**

```
                            User Opens App
                                 │
                                 ▼
                      ┌─────────◊──────────┐
                      │ First Time User?   │
                      └─┬────────────────┬─┘
                   New│            Returning│
                      ▼                   ▼
            ┌─────────────────┐    ┌─────────────────┐
            │ Region Detection│    │ Load User       │
            │ 📍 IP/Location  │    │ Preferences     │
            │ 🌍 Manual Choice│    │ 🎨 Theme        │
            └─────────┬───────┘    │ 🗣️ Language     │
                     │            │ 🕌 Tradition    │
                     ▼            └─────────┬───────┘
          ┌─────────────────────┐           │
          │ Cultural Setup Flow │           │
          │                    │           │
          │ Step 1: Region     │           │
          │ ┌─────────────────┐ │           │
          │ │🇸🇦 Middle East  │ │           │
          │ │🇮🇩 SE Asia     │ │           │
          │ │🇵🇰 South Asia  │ │           │
          │ │🇹🇷 Turkey      │ │           │
          │ │🇺🇸 Americas    │ │           │
          │ │🌍 Other       │ │           │
          │ └─────────────────┘ │           │
          └─────────┬───────────┘           │
                   │                       │
                   ▼                       │
      ┌──────────◊──────────┐             │
      │ Region Selected?    │             │
      └─┬─────────┬─────────┘             │
   Arabic│   Asian│   Other               │
        ▼        ▼        ▼               │
┌──────────┐ ┌──────────┐ ┌──────────┐    │
│Arabic    │ │Asian     │ │Global    │    │
│Focused   │ │Localized │ │Balanced  │    │
│Setup     │ │Setup     │ │Setup     │    │
└────┬─────┘ └────┬─────┘ └────┬─────┘    │
     │            │            │          │
     ▼            ▼            ▼          │
┌─────────────────────────────────────────┐│
│         Islamic Tradition              ││
│                                       ││
│ ◊ Sunni → Traditional Sunni Content   ││
│ ◊ Shia  → Appropriate Shia Practices  ││
│ ◊ Local → Regional Variations        ││
└─────────────────┬───────────────────────┘│
                 │                        │
                 ▼                        │
┌─────────────────────────────────────────┐│
│         Language Priority              ││
│                                       ││
│ Primary: Arabic (Sacred Text)          ││
│ Secondary: Regional Language           ││
│ Optional: English (Global)             ││
└─────────────────┬───────────────────────┘│
                 │                        │
                 ▼                        │
┌─────────────────────────────────────────┐│
│           Calendar System              ││
│                                       ││
│ ◉ Both Hijri & Gregorian             ││
│ ○ Hijri Primary                      ││
│ ○ Gregorian Primary                  ││
└─────────────────┬───────────────────────┘│
                 │                        │
                 └────────────────────────┘
                           │
                           ▼
                ┌─────────────────────┐
                │ Apply Cultural      │
                │ Preferences:        │
                │                    │
                │ 🎨 Color Scheme    │
                │ 📝 Typography      │
                │ 🕌 Icons & Symbols │
                │ 📅 Date Formats    │
                │ 🔢 Number Systems  │
                │ 📖 Prayer Content  │
                └─────────┬───────────┘
                         │
                         ▼
                ┌─────────────────────┐
                │ Localized App       │
                │ Ready for Use       │
                │ 🌍 Culturally       │
                │    Appropriate      │
                └─────────────────────┘
```

### 🔐 **Privacy Decision Tree**

```
                          User Creates Memorial
                                    │
                                    ▼
                         ┌─────────◊──────────┐
                         │ First Memorial?    │
                         └─┬────────────────┬─┘
                      Yes│              No│(Has Experience)
                         ▼                ▼
               ┌─────────────────┐  ┌─────────────────┐
               │ Privacy Tutorial │  │ Use Previous    │
               │ 🔒 Explain      │  │ Settings or     │
               │    Options      │  │ Modify?         │
               │ 👥 Show Impact  │  └─────────┬───────┘
               └─────────┬───────┘           │
                        │                   │
                        └───────────────────┘
                                 │
                                 ▼
                      ┌─────────◊──────────┐
                      │ Relationship Type? │
                      └─┬─────────┬────────┘
                 Immediate│  Extended│Distant
                 Family  │  Family  │
                        ▼         ▼        ▼
              ┌─────────────┐ ┌─────────┐ ┌─────────────┐
              │Suggest:     │ │Suggest: │ │Suggest:     │
              │🔒 Family    │ │👥 Comm- │ │🌍 Public    │
              │   Only      │ │  unity  │ │   Memorial  │
              └─────┬───────┘ └────┬────┘ └─────┬───────┘
                   │              │            │
                   └──────────────┼────────────┘
                                 │
                                 ▼
                      ┌─────────────────────┐
                      │ Privacy Options     │
                      │ Present Choices:    │
                      │                    │
                      │ 🔒 Family Only     │
                      │ • Only family sees │
                      │ • Private prayers  │
                      │ • Secure sharing   │
                      │                    │
                      │ 👥 Community       │
                      │ • Local Muslims    │
                      │ • Anonymous stats  │
                      │ • Respectful      │
                      │                    │
                      │ 🌍 Public Memorial │
                      │ • Global community │
                      │ • Search findable  │
                      │ • Social sharing   │
                      └─────────┬───────────┘
                               │
                    ┌─────────◊──────────┐
                    │ User Selects?      │
                    └─┬─────────┬────────┘
               Family│ Community│ Public
                    ▼         ▼        ▼
          ┌─────────────┐ ┌─────────┐ ┌─────────────┐
          │Family Mode: │ │Comm.    │ │Public Mode: │
          │• No sharing │ │Mode:    │ │• Full share │
          │• Encrypted  │ │• Limited│ │• Discovery  │
          │• Invites    │ │• Region │ │• Global vis │
          └─────┬───────┘ └────┬────┘ └─────┬───────┘
               │              │            │
               └──────────────┼────────────┘
                             │
                             ▼
                  ┌─────────────────────┐
                  │ Additional Controls │
                  │                    │
                  │ 📸 Photo Sharing:   │
                  │ ◉ Include in posts  │
                  │ ○ Text only        │
                  │                    │
                  │ 📅 Anniversary:     │
                  │ ◉ Auto reminders   │
                  │ ○ Manual only      │
                  │                    │
                  │ 👥 Family Access:   │
                  │ ◉ Can add prayers  │
                  │ ○ View only        │
                  └─────────┬───────────┘
                           │
                           ▼
                  ┌─────────────────────┐
                  │ Privacy Settings    │
                  │ Saved & Applied     │
                  │ ✅ Configured       │
                  │ 🔒 Secure          │
                  └─────────────────────┘
```

---

## 🔧 **Technical Integration Flows**

### 📱 **Social Media Integration Flow**

```
                        User Completes Prayer
                               │
                               ▼
                    ┌─────────────────────┐
                    │ Share Trigger Point │
                    │ ✨ Post-completion  │
                    │ 🤲 Spiritual moment │
                    └─────────┬───────────┘
                             │
                    ┌────────◊────────┐
                    │ User Wants to   │
                    │ Share?          │
                    └─┬────────────┬──┘
                 Yes│           No│
                    ▼             ▼
         ╔══════════════════════╗ ○ END
         ║ Social Platform      ║ (No sharing)
         ║ Selection            ║
         ║                     ║
         ║ Available Platforms: ║
         ║ 📘 Facebook (if     ║
         ║    connected)       ║
         ║ 💬 WhatsApp (if     ║
         ║    available)       ║
         ║ 🐦 Twitter (if      ║
         ║    logged in)       ║
         ║ 📱 Telegram (if     ║
         ║    installed)       ║
         ║ 📧 Email (system)   ║
         ║ 📋 Copy Link        ║
         ╚═══════════┬═════════╝
                    │
         ┌─────────◊──────────┐
         │ Platform Selected? │
         └─┬─────────────────┬┘
      API│             Native│
   Platform│               Share│
          ▼                    ▼
┌─────────────────┐    ┌─────────────────┐
│ API Integration │    │ Native Sharing  │
│                │    │                │
│ 1. Check Auth   │    │ 1. System Share │
│ 2. Format Content│   │ 2. App Picker   │
│ 3. Upload Media  │    │ 3. Direct Post  │
│ 4. Post Message  │    └─────────┬───────┘
│ 5. Get Response  │              │
└─────────┬───────┘               │
         │                       │
         ▼                       │
┌─────────────────────┐           │
│ Platform-Specific   │           │
│ Formatting:         │           │
│                    │           │
│ Facebook:           │           │
│ • Rich media post   │           │
│ • Prayer context    │           │
│ • Privacy controls  │           │
│                    │           │
│ WhatsApp:           │           │
│ • Text + image      │           │
│ • Family sharing    │           │
│ • Personal tone     │           │
│                    │           │
│ Twitter:            │           │
│ • Hashtag optimize  │           │
│ • Thread if needed  │           │
│ • Character limit   │           │
└─────────┬───────────┘           │
         │                       │
         └───────────────────────┘
                     │
                     ▼
          ┌─────────────────────┐
          │ Content Preparation │
          │                    │
          │ 📝 Message Template │
          │ 📸 Photo Processing │
          │ 🏷️ Hashtag Addition │
          │ 🔐 Privacy Settings │
          │ 🌍 Audience Selection│
          └─────────┬───────────┘
                   │
          ┌────────◊────────┐
          │ Ready to Post?  │
          └─┬────────────┬──┘
       Post│         Edit│
          ▼             ▼
┌─────────────────┐ ┌─────────────────┐
│ Execute Posting │ │ Return to Edit  │
│                │ │ 📝 Modify Content│
│ 1. API Call     │ └─────────────────┘
│ 2. Monitor      │         │
│ 3. Handle Errors│         │
│ 4. Confirm Post │         │
└─────────┬───────┘         │
         │                 │
         ▼                 │
┌─────────────────────┐     │
│ Post Status Check   │     │
│                    │     │
│ ◊ Success?         │     │
│ ├─ Yes: Celebrate  │     │
│ ├─ No: Error Handle│     │
│ └─ Retry?: Option  │     │
└─────────┬───────────┘     │
         │                 │
         └─────────────────┘
                   │
                   ▼
          ┌─────────────────────┐
          │ Post-Sharing        │
          │ • Link to view post │
          │ • Engagement track  │
          │ • Community response│
          │ • Return to app     │
          └─────────────────────┘
```

---

## ⚠️ **Error & Edge Case Flows**

### 🔌 **Offline Mode Flow**

```
                           User Opens App
                                 │
                                 ▼
                      ┌─────────◊──────────┐
                      │ Internet Available?│
                      └─┬────────────────┬─┘
                   Yes│              No│
                      ▼                ▼
            ┌─────────────────┐  ┌─────────────────┐
            │ Online Mode     │  │ Offline Mode    │
            │ • Full features │  │ • Limited       │
            │ • Community     │  │ • Local only    │
            │ • Social        │  │ • Cached        │
            └─────────────────┘  └─────────┬───────┘
                     │                    │
                     │                    ▼
                     │          ┌─────────────────┐
                     │          │ Offline Banner  │
                     │          │ 📶 No connection│
                     │          │ 🔄 Retrying...  │
                     │          │ 📖 Cached only  │
                     │          └─────────┬───────┘
                     │                    │
                     │          ┌────────◊────────┐
                     │          │ User Action?    │
                     │          └─┬─────────────┬─┘
                     │      Prayer│       Other│
                     │            ▼            ▼
                     │   ┌─────────────────┐ ┌─────────────────┐
                     │   │ Offline Prayer  │ │ Limited Feature │
                     │   │ • Cached text   │ │ • Show message  │
                     │   │ • No community  │ │ • Explain need  │
                     │   │ • Save progress │ │ • Offer offline │
                     │   └─────────┬───────┘ └─────────────────┘
                     │            │                 │
                     │            ▼                 │
                     │   ┌─────────────────┐        │
                     │   │ Prayer Reading  │        │
                     │   │ (Offline Mode)  │        │
                     │   │ • Local content │        │
                     │   │ • Save to queue │        │
                     │   │ • Sync later    │        │
                     │   └─────────┬───────┘        │
                     │            │                │
                     │            ▼                │
                     │   ┌─────────────────┐       │
                     │   │ Completion      │       │
                     │   │ • Save locally  │       │
                     │   │ • Queue sharing │       │
                     │   │ • Sync reminder │       │
                     │   └─────────┬───────┘       │
                     │            │                │
                     └────────────┼────────────────┘
                                 │
                                 ▼
                      ┌─────────◊──────────┐
                      │ Connection Return? │
                      └─┬────────────────┬─┘
                   Yes│              No│
                      ▼                ▼
            ┌─────────────────┐  ┌─────────────────┐
            │ Sync Queued     │  │ Stay Offline    │
            │ • Upload prayers│  │ • Keep working  │
            │ • Sync progress │  │ • Local storage │
            │ • Share posts   │  │ • Retry later   │
            └─────────────────┘  └─────────────────┘
```

### ❌ **Error Recovery Flow**

```
                            Error Occurs
                                 │
                                 ▼
                      ┌─────────◊──────────┐
                      │ Error Type?        │
                      └─┬─────────┬────────┘
                Network│     App│    Server│
                       ▼        ▼         ▼
            ┌─────────────┐ ┌─────────┐ ┌─────────────┐
            │Network Error│ │App Crash│ │Server Error │
            │🔌 No Wifi   │ │💥 Bug   │ │🔧 Overload  │
            │📶 Poor Sig  │ │🔋 Memory│ │⚠️ Maintenance│
            └─────┬───────┘ └────┬────┘ └─────┬───────┘
                 │              │            │
                 ▼              ▼            ▼
      ┌─────────────────┐ ┌─────────────┐ ┌─────────────────┐
      │ Network Recovery│ │App Recovery │ │ Server Recovery │
      │                │ │            │ │                │
      │ 1. Show offline │ │1. Crash log │ │ 1. Show status  │
      │    mode banner  │ │2. Restart  │ │ 2. Retry option │
      │ 2. Cache prayer │ │3. Restore  │ │ 3. Fallback     │
      │    content      │ │   state    │ │    content      │
      │ 3. Queue actions│ │4. Continue │ │ 4. Update user  │
      │ 4. Retry timer  │ │   safely   │ │                │
      └─────┬───────────┘ └─────┬───────┘ └─────┬───────────┘
             │                 │               │
             ▼                 ▼               ▼
    ┌─────────────────┐ ┌─────────────┐ ┌─────────────────┐
    │ User Options:   │ │ Recovery    │ │ User Options:   │
    │ • Continue      │ │ Complete    │ │ • Wait & retry  │
    │   offline       │ │ • Lost data?│ │ • Use offline   │
    │ • Retry now     │ │ • Restore   │ │ • Report issue  │
    │ • Change wifi   │ │   session   │ │ • Contact support│
    └─────┬───────────┘ └─────┬───────┘ └─────┬───────────┘
           │                 │               │
           └─────────────────┼───────────────┘
                            │
                            ▼
                   ┌─────────────────┐
                   │ Error Logged &  │
                   │ Reported for    │
                   │ App Improvement │
                   │ 📊 Analytics    │
                   │ 🐛 Bug Track    │
                   │ 👤 User Feedback│
                   └─────────────────┘
```

---

**Flow Diagrams Created By**: UX Researcher + UI/UX Team Lead  
**Technical Validation**: System Analyst review required  
**Cultural Validation**: Islamic Scholar Advisory Board review  
**Implementation**: Ready for development team handoff  
**Last Updated**: May 21, 2026 (Post Design Workshop)