# 📊 Decision Log - Tahlil App Project

**Project**: Tahlil  
**Decision Authority**: Product Owner  
**Log Maintained By**: System Analyst  
**Last Updated**: May 20, 2026  

---

## 🎯 Decision Management Framework

### 📋 Decision Categories
- **Strategic (S)**: High-level product direction and business model
- **Technical (T)**: Architecture, platform, and technology choices  
- **Design (D)**: User experience, interface, and visual design
- **Process (P)**: Development methodology and team processes
- **Content (C)**: Religious content, features, and functionality

### 🚦 Decision Status
- ✅ **Approved**: Final decision made and documented
- 🔄 **Pending**: Under discussion, awaiting approval
- ⚠️ **Deferred**: Postponed to later phase
- 🚨 **Blocked**: Cannot proceed due to dependencies

---

## 📊 Decision Summary Dashboard

| Category | Total | Approved | Pending | Deferred | Blocked |
|----------|-------|----------|---------|----------|---------|
| **Strategic** | 3 | 3 | 0 | 0 | 0 |
| **Technical** | 3 | 3 | 0 | 0 | 0 |
| **Design** | 2 | 2 | 0 | 0 | 0 |
| **Process** | 0 | 0 | 0 | 0 | 0 |
| **Content** | 0 | 0 | 0 | 0 | 0 |
| **TOTAL** | 8 | 8 | 0 | 0 | 0 |

---

## ✅ Approved Decisions

### Strategic Decisions

#### DEC-001-S: Product Vision and Core Value Proposition
- **Date**: May 20, 2026
- **Meeting**: MEET-001 Product Strategy
- **Decision**: Create "Kenangan Doa" - a photo-centric memorial prayer app focusing on Yasin and Tahlil for Malam Jumat traditions
- **Decision Maker**: Product Owner
- **Participants**: PO, SA, BA, UX
- **Rationale**: 
  - Market research shows gap in emotional connection to digital prayer apps
  - Strong cultural tradition around Friday night prayers for deceased loved ones
  - Photo-first approach creates deeper user engagement and retention
- **Alternatives Considered**:
  - Generic Quran reading app with memorial features
  - Community-focused prayer app without personal memorials
- **Impact**: High - Defines entire product strategy and development focus
- **Success Criteria**: 95% photo upload rate, 70% weekly retention
- **Review Date**: June 20, 2026
- **Dependencies**: None

#### DEC-002-S: Business Model and Pricing Strategy
- **Date**: May 20, 2026
- **Meeting**: MEET-001 Product Strategy
- **Decision**: Freemium model with 4 tiers (Free, Premium, Family, Lifetime)
- **Decision Maker**: Product Owner (with BA recommendation)
- **Participants**: PO, BA, UX
- **Rationale**:
  - Freemium allows broad market penetration in price-sensitive segments
  - Family tier aligns with target user behavior (family-oriented prayers)
  - Lifetime option captures committed users and improves LTV
- **Pricing Structure**:
  ```
  Free: $0 (3 profiles, basic prayers)
  Premium: $2.99/month (unlimited profiles, audio, analytics)
  Family: $6.99/month (5 accounts, shared memorials)
  Lifetime: $29.99 (all premium features forever)
  ```
- **Impact**: High - Determines revenue strategy and feature distribution
- **Success Criteria**: 15% premium conversion rate within 6 months
- **Review Date**: August 1, 2026 (post-launch analysis)

#### DEC-003-S: Target Market and Geographic Focus
- **Date**: May 20, 2026
- **Meeting**: MEET-001 Product Strategy  
- **Decision**: Primary focus on Southeast Asian Muslim market (Indonesia, Malaysia)
- **Decision Maker**: Business Analyst (with PO approval)
- **Participants**: BA, PO
- **Rationale**:
  - High smartphone penetration in target demographics
  - Strong cultural alignment with Malam Jumat traditions
  - Manageable market size for initial launch and iteration
- **Market Size**: 240M Muslims in Southeast Asia, 70% smartphone users
- **Impact**: Medium - Influences language, cultural features, and marketing
- **Success Criteria**: 10,000 downloads in first month from target regions
- **Review Date**: September 1, 2026

---

### Technical Decisions

#### DEC-004-T: Platform Priority and Development Strategy
- **Date**: May 20, 2026
- **Meeting**: MEET-001 Product Strategy
- **Decision**: Android-first development with iOS following 6 months post-launch
- **Decision Maker**: System Analyst (with PO approval)
- **Participants**: SA, PO, BA
- **Rationale**:
  - 70% of target market uses Android devices
  - Faster development and iteration cycle on Android
  - Lower barrier to entry for users (sideloading if needed)
  - iOS market more price-sensitive, better to validate on Android first
- **Technical Specifications**:
  - Minimum SDK: API 24 (Android 7.0) - 95% market coverage
  - Target SDK: API 34 (Android 14)
  - Language: Kotlin with coroutines
- **Impact**: High - Determines development timeline and resource allocation
- **Success Criteria**: 85% device compatibility, <2 second app launch time
- **Review Date**: July 1, 2026 (iOS planning phase)
- **Dependencies**: Android development team assembly

#### DEC-005-T: Application Architecture Pattern
- **Date**: May 20, 2026
- **Meeting**: MEET-001 Product Strategy
- **Decision**: MVVM (Model-View-ViewModel) with Repository pattern
- **Decision Maker**: System Analyst
- **Participants**: SA, PO
- **Rationale**:
  - Scalable architecture for future feature additions
  - Clear separation of concerns for team development
  - Testable architecture supporting quality goals
  - Android best practices and Google recommendations
- **Technical Details**:
  ```
  Presentation Layer: Activities/Fragments + ViewModels
  Domain Layer: Use Cases + Repository interfaces
  Data Layer: Repository implementations + Data sources (Room + Network)
  ```
- **Impact**: Medium - Influences development approach and code maintainability
- **Success Criteria**: 80% test coverage, modular component structure
- **Review Date**: May 27, 2026 (Technical Architecture Meeting)
- **Dependencies**: Team training on MVVM patterns

#### DEC-006-T: Data Storage and Synchronization Strategy
- **Date**: May 20, 2026
- **Meeting**: MEET-001 Product Strategy
- **Decision**: Offline-first with Room database + Firebase Cloud sync
- **Decision Maker**: System Analyst
- **Participants**: SA, PO, BA
- **Rationale**:
  - Prayer apps must work offline (mosques, poor connectivity)
  - Photos and memorial data too important to lose
  - Family sharing requires cloud synchronization
  - Firebase provides reliable, scalable backend
- **Technical Implementation**:
  ```
  Local Storage: Room database (SQLite)
  Cloud Storage: Firebase Firestore for data + Firebase Storage for photos
  Sync Strategy: Conflict resolution with last-writer-wins for profiles
  Offline Mode: Full functionality except family sharing and community features
  ```
- **Impact**: High - Critical for user experience and data reliability
- **Success Criteria**: 99.9% data integrity, <3 second sync time
- **Review Date**: May 30, 2026 (Database schema finalization)
- **Dependencies**: Firebase project setup, photo compression strategy

---

### Design Decisions

#### DEC-007-D: Photo-Centric Design Philosophy
- **Date**: May 20, 2026
- **Meeting**: MEET-001 Product Strategy
- **Decision**: Memorial photos as hero elements in all interfaces
- **Decision Maker**: UI/UX Designer (with PO approval)
- **Participants**: UX, PO, BA
- **Rationale**:
  - Photos create emotional connection essential for retention
  - Visual memory triggers stronger than text-only memorials
  - Differentiates from generic prayer apps
  - Aligns with cultural importance of remembering deceased
- **Design Guidelines**:
  ```
  Photo Size: Minimum 120x120dp in lists, larger in detail views
  Photo Shape: Circular with Islamic-inspired borders
  Photo Quality: AI enhancement for old/low-quality images
  Photo Placement: Always above-the-fold, prominent positioning
  Default State: Elegant Islamic patterns if no photo available
  ```
- **Impact**: High - Defines core user experience and visual identity
- **Success Criteria**: 95% users upload photos, positive user feedback on emotional connection
- **Review Date**: May 24, 2026 (Design Presentation)
- **Dependencies**: Photo processing and enhancement capabilities

#### DEC-008-D: User Experience Complexity Guidelines
- **Date**: May 20, 2026
- **Meeting**: MEET-001 Product Strategy
- **Decision**: Maximum 3-tap rule for all core user actions
- **Decision Maker**: UI/UX Designer
- **Participants**: UX, PO, BA
- **Rationale**:
  - Target users include elderly family members (low tech literacy)
  - Prayer apps should be calming and simple, not frustrating
  - Reduces abandonment during emotional/spiritual moments
  - Industry best practice for spiritual/meditation apps
- **Implementation Guidelines**:
  ```
  Core Actions (3 taps max):
  - Start prayer for someone: Select person → Choose prayer → Begin
  - Add new memorial: FAB → Add photo → Save with basic info
  - Join community prayer: Home banner → Confirm join → Begin prayer
  
  Progressive Disclosure:
  - Advanced features hidden behind "More" options
  - Settings accessible but not prominent
  - Help/tutorials available but not intrusive
  ```
- **Impact**: Medium - Influences all interface design and user flow decisions
- **Success Criteria**: 90% task completion rate, <5% user support requests for navigation
- **Review Date**: During user testing sessions (May 29, 2026)
- **Dependencies**: User testing with target demographics

---

## 🔄 Pending Decisions

### 📅 **MEET-003 Strategic Decisions (May 21, 2026)**

#### DEC-009-S: Global Platform Strategy [CRITICAL]
- **Status**: 🔄 **PENDING** - Requires stakeholder approval
- **Meeting**: MEET-003 Stakeholder Alignment
- **Decision Maker**: CEO + Product Owner + Stakeholders
- **Options**: 
  - A) Global "Tahlil" platform
  - B) Maintain SE Asian scope with social features  
  - C) Phased global expansion approach
- **Impact**: **CRITICAL** - Affects all existing strategic decisions
- **Dependencies**: Budget approval, timeline extension, team expansion
- **Required By**: May 21, 2026
- **Affects Decisions**: DEC-002-S, DEC-003-S potentially overturned

#### DEC-010-S: Business Model Transformation [CRITICAL]
- **Status**: 🔄 **PENDING** - Requires stakeholder approval
- **Meeting**: MEET-003 Stakeholder Alignment  
- **Decision Maker**: CEO + Product Owner
- **Options**:
  - A) Free platform (complete community service)
  - B) Maintain freemium model for sustainability
  - C) Hybrid approach (free core, premium social)
- **Impact**: **CRITICAL** - Complete revenue strategy change
- **Dependencies**: Alternative funding sources, sustainability model
- **Required By**: May 21, 2026
- **Affects Decisions**: DEC-002-S (Freemium model) may be overturned

#### DEC-011-S: Social Media Integration Strategy [HIGH]
- **Status**: 🔄 **PENDING** - Requires stakeholder approval
- **Meeting**: MEET-003 Stakeholder Alignment
- **Decision Maker**: Product Owner + Marketing Director
- **Options**:
  - A) Basic sharing (post prayer completion)
  - B) Deep integration (prayer streams, community feeds)
  - C) Platform-native social features only
- **Impact**: **HIGH** - Development complexity and user engagement
- **Dependencies**: Social media platform API partnerships
- **Required By**: May 21, 2026
- **Affects Decisions**: New feature scope expansion

#### DEC-012-S: Timeline and Resource Commitment [CRITICAL]
- **Status**: 🔄 **PENDING** - Requires executive approval
- **Meeting**: MEET-003 Stakeholder Alignment
- **Decision Maker**: CEO + Executive Sponsor
- **Options**:
  - A) 12-18 month global platform development
  - B) Maintain 6-month timeline with reduced scope
  - C) Phased approach: 6 month local + 12 month global
- **Impact**: **CRITICAL** - Budget and resource allocation
- **Dependencies**: Team expansion approval, infrastructure budget
- **Required By**: May 21, 2026
- **Affects Decisions**: All timeline-dependent decisions

---

## ⚠️ Deferred Decisions

*No deferred decisions at this time*

---

## 🚨 Blocked Decisions

*No blocked decisions at this time*

---

## 📈 Decision Impact Analysis

### High Impact Decisions (8)
- DEC-001-S: Product vision - Shapes entire development direction
- DEC-002-S: Business model - Determines revenue and feature strategy
- DEC-004-T: Platform priority - Affects timeline and resource allocation
- DEC-006-T: Data strategy - Critical for UX and reliability
- DEC-007-D: Photo-centric design - Defines core user experience

### Medium Impact Decisions (3)
- DEC-003-S: Target market - Influences features and marketing
- DEC-005-T: Architecture - Affects development efficiency
- DEC-008-D: UX complexity - Affects user adoption and satisfaction

### Low Impact Decisions (0)
*No low impact decisions recorded*

---

## 📊 Decision Quality Metrics

### ✅ Decision Speed
- **Average Decision Time**: 1 day (from discussion to approval)
- **Fastest Decision**: Same-day (design philosophy)
- **Longest Decision**: 1 day (business model - required BA analysis)

### 🎯 Decision Stability
- **Decisions Changed**: 0 (0%)
- **Decisions Overturned**: 0 (0%)
- **Quality Score**: 10/10 (no reversals or major changes)

### 🤝 Stakeholder Alignment
- **Unanimous Decisions**: 8 (100%)
- **Majority Decisions**: 0 (0%)
- **Contentious Decisions**: 0 (0%)

---

## 📅 Upcoming Decision Points

### 🎯 Week of May 22, 2026
- **Content Validation Process**: How to ensure Islamic authenticity
- **User Testing Methodology**: Approach for validating design decisions
- **Development Team Structure**: In-house vs. outsourced development

### 🎯 Week of May 29, 2026
- **Audio Content Strategy**: Qari selection and recording approach
- **Notification Strategy**: Timing and content for Malam Jumat reminders
- **Community Features Scope**: Level of social interaction to include

### 🎯 Week of June 5, 2026
- **Marketing Channel Strategy**: Primary user acquisition channels
- **Partnership Strategy**: Islamic organizations and content partnerships
- **Launch Timeline**: Final go-to-market schedule

---

## 📚 Decision Documentation Standards

### 📝 Required Information for All Decisions
1. **Context**: Why the decision was needed
2. **Options**: Alternatives considered with pros/cons
3. **Criteria**: How the decision was evaluated
4. **Stakeholders**: Who was involved in the decision
5. **Impact**: Expected effects on project success
6. **Measurements**: How success will be tracked
7. **Dependencies**: What other decisions or factors are affected
8. **Timeline**: When the decision takes effect and review dates

### 🔄 Review Process
- **Monthly Reviews**: Assess decision outcomes and adjust if needed
- **Milestone Reviews**: Major decision evaluation at each project phase
- **Post-Launch Analysis**: Full decision effectiveness assessment
- **Lessons Learned**: Document insights for future decision making

---

**Decision Log Maintained By**: System Analyst  
**Approval Authority**: Product Owner  
**Update Frequency**: After each decision  
**Next Review**: May 27, 2026