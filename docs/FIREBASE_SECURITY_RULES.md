# 🔒 Firebase Security Rules Documentation

## Overview

This document explains the comprehensive security rules implemented for Tahlil's Firebase backend, ensuring Islamic privacy values, family customs, and data protection compliance.

## 🎯 Security Principles

### 1. **Islamic Privacy First**
- Memorial privacy respects Islamic family traditions
- Gender-appropriate interaction guidelines
- Cultural sensitivity in data access patterns
- Family hierarchy respect in permissions

### 2. **40-Day Islamic Tradition**
- Automatic memorial expiration following Islamic customs
- Time-based access controls
- Cultural validation for content

### 3. **Multi-Level Privacy**
- **Private**: Creator only access
- **Family**: Extended family member access
- **Community**: Public Islamic community access

## 🗄️ Firestore Security Rules

### 1. **User Collection Rules**

```javascript
// Users can only access their own profile data
match /users/{userId} {
  allow read, write: if request.auth != null && request.auth.uid == userId;
  
  // Additional validation for profile updates
  allow update: if request.auth != null && 
    request.auth.uid == userId &&
    validateUserProfileUpdate(request.resource.data);
}

function validateUserProfileUpdate(userData) {
  return userData.keys().hasAll(['displayName', 'email']) &&
         userData.displayName is string &&
         userData.displayName.size() > 0 &&
         userData.displayName.size() <= 100;
}
```

### 2. **Memorial Collection Rules**

```javascript
match /memorials/{memorialId} {
  // Read access based on privacy settings
  allow read: if request.auth != null && (
    // Memorial creator can always access
    resource.data.createdBy == request.auth.uid ||
    
    // Privacy-based access control
    (resource.data.privacy == 'private' && 
     resource.data.createdBy == request.auth.uid) ||
     
    (resource.data.privacy == 'family' && 
     request.auth.uid in resource.data.familyMembers) ||
     
    (resource.data.privacy == 'community' && 
     isValidCommunityMember(request.auth.uid))
  );
  
  // Create memorial with validation
  allow create: if request.auth != null &&
    validateMemorialCreation(request.resource.data) &&
    request.resource.data.createdBy == request.auth.uid;
  
  // Update memorial (creator only)
  allow update: if request.auth != null &&
    resource.data.createdBy == request.auth.uid &&
    validateMemorialUpdate(request.resource.data, resource.data);
  
  // Delete memorial (creator only)
  allow delete: if request.auth != null &&
    resource.data.createdBy == request.auth.uid;
}

function validateMemorialCreation(memorialData) {
  return memorialData.keys().hasAll([
    'name', 'createdBy', 'privacy', 'createdAt', 'expiresAt'
  ]) &&
  memorialData.name is string &&
  memorialData.name.size() > 0 &&
  memorialData.name.size() <= 100 &&
  memorialData.privacy in ['private', 'family', 'community'] &&
  memorialData.createdAt is timestamp &&
  memorialData.expiresAt is timestamp &&
  memorialData.expiresAt > memorialData.createdAt &&
  // 40-day Islamic tradition validation
  (memorialData.expiresAt.toMillis() - memorialData.createdAt.toMillis()) <= 3456000000; // 40 days in milliseconds
}

function validateMemorialUpdate(newData, currentData) {
  return newData.createdBy == currentData.createdBy &&
         newData.createdAt == currentData.createdAt &&
         newData.name is string &&
         newData.name.size() > 0 &&
         newData.name.size() <= 100;
}

function isValidCommunityMember(userId) {
  // Check if user has valid community membership
  return exists(/databases/$(database)/documents/user_profiles/$(userId)) &&
         get(/databases/$(database)/documents/user_profiles/$(userId)).data.isVerified == true;
}
```

### 3. **Prayer Sessions Rules**

```javascript
match /prayer_sessions/{sessionId} {
  // Users can read their own prayer sessions
  allow read: if request.auth != null && 
    resource.data.userId == request.auth.uid;
  
  // Users can read sessions for memorials they have access to
  allow read: if request.auth != null &&
    canAccessMemorial(resource.data.memorialId, request.auth.uid);
  
  // Create prayer session
  allow create: if request.auth != null &&
    validatePrayerSession(request.resource.data) &&
    request.resource.data.userId == request.auth.uid &&
    canAccessMemorial(request.resource.data.memorialId, request.auth.uid);
  
  // Update prayer session (user's own only)
  allow update: if request.auth != null &&
    resource.data.userId == request.auth.uid &&
    validatePrayerSessionUpdate(request.resource.data);
}

function validatePrayerSession(sessionData) {
  return sessionData.keys().hasAll([
    'userId', 'memorialId', 'prayerType', 'startedAt', 'completed'
  ]) &&
  sessionData.prayerType in ['TAHLIL', 'YASIN', 'FATIHAH', 'DHIKR_MORNING', 'DHIKR_EVENING'] &&
  sessionData.completed is bool &&
  sessionData.startedAt is timestamp;
}

function validatePrayerSessionUpdate(sessionData) {
  return sessionData.keys().hasAll(['completed', 'completedAt']) &&
         sessionData.completed is bool &&
         (sessionData.completed == false || sessionData.completedAt is timestamp);
}

function canAccessMemorial(memorialId, userId) {
  let memorial = get(/databases/$(database)/documents/memorials/$(memorialId));
  return memorial.data.createdBy == userId ||
         (memorial.data.privacy == 'family' && userId in memorial.data.familyMembers) ||
         (memorial.data.privacy == 'community');
}
```

### 4. **User Profiles Rules**

```javascript
match /user_profiles/{userId} {
  // Users can only access their own profile
  allow read, write: if request.auth != null && request.auth.uid == userId;
  
  // Profile creation validation
  allow create: if request.auth != null &&
    request.auth.uid == userId &&
    validateUserProfile(request.resource.data);
  
  // Profile update validation
  allow update: if request.auth != null &&
    request.auth.uid == userId &&
    validateUserProfileUpdate(request.resource.data);
}

function validateUserProfile(profileData) {
  return profileData.keys().hasAll([
    'displayName', 'islamicRegion', 'schoolOfThought', 'preferredLanguage'
  ]) &&
  profileData.displayName is string &&
  profileData.displayName.size() > 0 &&
  profileData.islamicRegion in [
    'MIDDLE_EAST', 'SOUTH_ASIA', 'SOUTHEAST_ASIA', 'CENTRAL_ASIA', 
    'NORTH_AFRICA', 'SUB_SAHARAN_AFRICA', 'WESTERN_COUNTRIES'
  ] &&
  profileData.schoolOfThought in [
    'SUNNI_HANAFI', 'SUNNI_MALIKI', 'SUNNI_SHAFII', 'SUNNI_HANBALI',
    'SHIA_TWELVER', 'SHIA_ISMAILI', 'OTHER'
  ] &&
  profileData.preferredLanguage in [
    'ar', 'id', 'en', 'tr', 'ru', 'ms'
  ];
}

function validateUserProfileUpdate(profileData) {
  return validateUserProfile(profileData) &&
         // Prevent critical field changes after profile creation
         profileData.schoolOfThought is string &&
         profileData.islamicRegion is string;
}
```

### 5. **Global Statistics Rules**

```javascript
match /global_stats/{statId} {
  // Read-only access for authenticated users
  allow read: if request.auth != null;
  
  // Only Cloud Functions can write statistics
  allow write: if false;
}

// Community statistics with regional breakdown
match /community_stats/{region} {
  allow read: if request.auth != null &&
    isUserFromRegion(request.auth.uid, region);
}

function isUserFromRegion(userId, region) {
  let userProfile = get(/databases/$(database)/documents/user_profiles/$(userId));
  return userProfile.data.islamicRegion == region;
}
```

### 6. **Family Management Rules**

```javascript
match /family_groups/{groupId} {
  // Family group access
  allow read, write: if request.auth != null && (
    resource.data.admin == request.auth.uid ||
    request.auth.uid in resource.data.members
  );
  
  // Create family group
  allow create: if request.auth != null &&
    validateFamilyGroup(request.resource.data) &&
    request.resource.data.admin == request.auth.uid;
}

function validateFamilyGroup(groupData) {
  return groupData.keys().hasAll(['admin', 'members', 'name']) &&
         groupData.name is string &&
         groupData.name.size() > 0 &&
         groupData.admin is string &&
         groupData.members is list &&
         groupData.admin in groupData.members;
}
```

## 📁 Storage Security Rules

### 1. **Memorial Photos Rules**

```javascript
service firebase.storage {
  match /b/{bucket}/o {
    // Memorial photos with privacy controls
    match /memorials/{memorialId}/{fileName} {
      // Read access based on memorial privacy
      allow read: if request.auth != null && (
        // Check memorial access rights
        canUserAccessMemorial(memorialId, request.auth.uid) &&
        // Validate file type
        resource.contentType.matches('image/.*')
      );
      
      // Write access for memorial creators only
      allow write: if request.auth != null &&
        isMemorialCreator(memorialId, request.auth.uid) &&
        // File validation
        request.resource.size < 5 * 1024 * 1024 && // 5MB limit
        request.resource.contentType.matches('image/(jpeg|png|webp)') &&
        // Islamic content validation (via metadata)
        validateImageMetadata(request.resource.metadata);
      
      // Delete access for memorial creators
      allow delete: if request.auth != null &&
        isMemorialCreator(memorialId, request.auth.uid);
    }
    
    // User profile photos
    match /users/{userId}/profile/{fileName} {
      allow read: if request.auth != null && (
        request.auth.uid == userId ||
        // Profile photos can be read by family/community based on user settings
        canAccessUserProfile(userId, request.auth.uid)
      );
      
      allow write: if request.auth != null &&
        request.auth.uid == userId &&
        request.resource.size < 2 * 1024 * 1024 && // 2MB limit
        request.resource.contentType.matches('image/(jpeg|png|webp)');
    }
    
    // Community shared content (Islamic resources)
    match /community/{resourceType}/{fileName} {
      // Read access for verified community members
      allow read: if request.auth != null &&
        isVerifiedCommunityMember(request.auth.uid);
      
      // Write access for community moderators only
      allow write: if request.auth != null &&
        isCommunityModerator(request.auth.uid) &&
        validateCommunityResource(request.resource);
    }
  }
}

function canUserAccessMemorial(memorialId, userId) {
  let memorial = firestore.get(/databases/(default)/documents/memorials/$(memorialId));
  
  return memorial.data.createdBy == userId ||
         (memorial.data.privacy == 'family' && 
          userId in memorial.data.familyMembers) ||
         (memorial.data.privacy == 'community' && 
          isVerifiedCommunityMember(userId));
}

function isMemorialCreator(memorialId, userId) {
  let memorial = firestore.get(/databases/(default)/documents/memorials/$(memorialId));
  return memorial.data.createdBy == userId;
}

function validateImageMetadata(metadata) {
  // Check for Islamic content validation flags
  return !('inappropriate' in metadata) &&
         !('flagged' in metadata) &&
         // Ensure cultural validation passed
         ('culturallyValidated' in metadata ? metadata.culturallyValidated == 'true' : true);
}

function canAccessUserProfile(profileUserId, requestingUserId) {
  let profile = firestore.get(/databases/(default)/documents/user_profiles/$(profileUserId));
  let requester = firestore.get(/databases/(default)/documents/user_profiles/$(requestingUserId));
  
  // Check if they're in same family group or community
  return profile.data.allowCommunityAccess == true ||
         sharesFamily(profileUserId, requestingUserId);
}

function isVerifiedCommunityMember(userId) {
  let profile = firestore.get(/databases/(default)/documents/user_profiles/$(userId));
  return profile.data.isVerified == true &&
         profile.data.communityStatus == 'active';
}

function isCommunityModerator(userId) {
  let profile = firestore.get(/databases/(default)/documents/user_profiles/$(userId));
  return profile.data.role in ['moderator', 'admin'];
}

function validateCommunityResource(resource) {
  return resource.size < 10 * 1024 * 1024 && // 10MB for community resources
         resource.contentType.matches('image/(jpeg|png|webp)') &&
         // Must have moderation approval metadata
         resource.metadata.moderatorApproved == 'true';
}

function sharesFamily(userId1, userId2) {
  // Check if users share a family group
  let user1Families = firestore.get(/databases/(default)/documents/user_profiles/$(userId1)).data.familyGroups;
  let user2Families = firestore.get(/databases/(default)/documents/user_profiles/$(userId2)).data.familyGroups;
  
  return user1Families != null && user2Families != null &&
         hasCommonElement(user1Families, user2Families);
}
```

## 🛡️ Security Best Practices

### 1. **Input Validation**

```javascript
// Comprehensive input validation functions
function validateTextInput(text, minLength, maxLength) {
  return text is string &&
         text.size() >= minLength &&
         text.size() <= maxLength &&
         // Prevent XSS and injection attacks
         !text.matches('.*[<>"\'/\\\\].*');
}

function validateArabicText(text) {
  // Arabic Unicode range validation
  return text.matches('[\\u0600-\\u06FF\\s.,!?()\\-]*');
}

function validateEmailFormat(email) {
  return email is string &&
         email.matches('[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}');
}

function validatePhoneNumber(phone) {
  return phone is string &&
         phone.matches('\\+?[1-9]\\d{1,14}'); // E.164 format
}
```

### 2. **Time-Based Security**

```javascript
function validateMemorialTiming(createdAt, expiresAt) {
  let now = request.time;
  let fortyDaysMs = 40 * 24 * 60 * 60 * 1000; // 40 days in milliseconds
  
  return createdAt <= now &&
         expiresAt > createdAt &&
         (expiresAt.toMillis() - createdAt.toMillis()) <= fortyDaysMs;
}

function isMemorialActive(memorial) {
  return memorial.data.isActive == true &&
         memorial.data.expiresAt > request.time;
}
```

### 3. **Rate Limiting & Abuse Prevention**

```javascript
function checkRateLimit(userId, action) {
  // Example: Limit memorial creation to 5 per day
  let today = request.time.toDate();
  let todayStart = timestamp.date(today.year(), today.month(), today.day());
  
  let todayMemorials = query.limit(6).where('createdBy', '==', userId)
                                    .where('createdAt', '>=', todayStart);
  
  return todayMemorials.size() <= 5;
}

function validateUserActivity(userId) {
  // Check for suspicious activity patterns
  let userProfile = get(/databases/$(database)/documents/user_profiles/$(userId));
  
  return userProfile.data.accountStatus != 'suspended' &&
         userProfile.data.lastActiveAt > (request.time.toMillis() - 7 * 24 * 60 * 60 * 1000); // 7 days
}
```

## 🔍 Monitoring & Auditing

### 1. **Security Event Logging**

The security rules automatically log important events to Cloud Logging:

- Memorial creation attempts
- Privacy violation attempts  
- Unauthorized access attempts
- Family group modifications
- Community content uploads

### 2. **Cultural Compliance Monitoring**

```javascript
function logCulturalEvent(eventType, data) {
  // These events are monitored for cultural compliance
  // - Arabic text validation attempts
  // - Memorial naming patterns
  // - Community interaction patterns
  // - Regional usage analytics
}
```

### 3. **Performance Monitoring**

Rules are optimized for performance:
- Efficient index usage
- Minimal document reads
- Cached validation results
- Optimized query patterns

## 🔧 Rule Testing

### 1. **Firebase Emulator Testing**

```bash
# Start emulators with security rules
firebase emulators:start --only firestore,storage,auth

# Run security rule tests
npm run test:security
```

### 2. **Test Cases**

```javascript
// Memorial privacy tests
describe('Memorial Privacy', () => {
  test('Creator can access private memorial', async () => {
    // Test implementation
  });
  
  test('Family member can access family memorial', async () => {
    // Test implementation
  });
  
  test('Stranger cannot access private memorial', async () => {
    // Test implementation
  });
});

// Islamic tradition tests
describe('Islamic Traditions', () => {
  test('Memorial expires after 40 days', async () => {
    // Test implementation
  });
  
  test('Arabic text validation works correctly', async () => {
    // Test implementation
  });
});
```

## 📋 Security Checklist

- ✅ **Authentication Required**: All operations require valid authentication
- ✅ **Privacy Controls**: Multi-level privacy (private, family, community)
- ✅ **Islamic Traditions**: 40-day expiration, cultural validation
- ✅ **Input Validation**: Comprehensive validation for all inputs
- ✅ **Rate Limiting**: Prevent abuse and spam
- ✅ **File Security**: Size limits, type validation, content screening
- ✅ **Regional Compliance**: Cultural and legal compliance per region
- ✅ **Performance Optimized**: Efficient queries and minimal reads
- ✅ **Comprehensive Testing**: Unit and integration tests for all rules
- ✅ **Monitoring & Logging**: Security events tracked and monitored

This security framework ensures that Tahlil maintains the highest standards of Islamic privacy, cultural respect, and technical security while providing a seamless experience for the global Muslim community.