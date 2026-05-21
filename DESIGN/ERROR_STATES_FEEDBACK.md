# ⚠️ Tahlil Error States & Feedback Mechanisms

## 📋 Error Design Overview

**Project**: Tahlil - Global Memorial Prayer Platform  
**Error Philosophy**: Compassionate, Clear, and Culturally Sensitive  
**Created**: May 20, 2026  
**Framework**: Human-Centered Error Prevention & Recovery  
**Cultural Approach**: Islamic Values of Patience and Guidance  

---

## 🕊️ Error Design Philosophy

### 🌟 Islamic Principles in Error Handling

**Core Values**:
1. **Sabr (Patience)**: Errors are opportunities for learning and growth
2. **Rahma (Compassion)**: Gentle guidance instead of harsh criticism  
3. **Hikmah (Wisdom)**: Clear explanations that educate users
4. **Tawfiq (Divine Assistance)**: Support users in completing their spiritual journey
5. **Ikhlas (Sincerity)**: Honest communication about issues and solutions

**Design Approach**:
- **Preventative**: Anticipate and prevent errors before they occur
- **Empathetic**: Acknowledge user frustration with understanding
- **Constructive**: Provide clear paths to resolution
- **Respectful**: Maintain dignity during error states
- **Cultural**: Honor diverse Islamic traditions in error messaging

### 🎯 Error Categories by Spiritual Context

#### Sacred Content Errors
- Memorial photo upload issues
- Prayer text loading problems  
- Audio recitation failures
- Community prayer session disconnections

#### User Journey Errors  
- Account creation difficulties
- Memorial information validation
- Prayer progress synchronization
- Social sharing failures

#### Technical System Errors
- Network connectivity issues
- Device compatibility problems
- Data storage limitations
- Performance degradation

---

## 🚨 Error State Design System

### 🎨 Visual Error Language

#### Error Color Palette
```css
/* Error state color system */
:root {
  /* Primary error colors - Respectful, not alarming */
  --error-primary: #C62828;        /* Deep red - serious issues */
  --error-secondary: #E57373;      /* Light red - minor issues */
  --error-background: #FFEBEE;     /* Very light red background */
  --error-border: #FFCDD2;         /* Light red borders */
  
  /* Warning colors - Gentle guidance */
  --warning-primary: #F57C00;      /* Orange - attention needed */
  --warning-secondary: #FFB74D;    /* Light orange - minor warnings */
  --warning-background: #FFF8E1;   /* Warm background */
  --warning-border: #FFECB3;       /* Light orange borders */
  
  /* Info colors - Helpful guidance */
  --info-primary: #1976D2;         /* Blue - information */
  --info-secondary: #64B5F6;       /* Light blue - secondary info */
  --info-background: #E3F2FD;      /* Light blue background */
  --info-border: #BBDEFB;          /* Light blue borders */
  
  /* Success recovery colors */
  --success-primary: #388E3C;      /* Green - successful resolution */
  --success-secondary: #81C784;    /* Light green - positive feedback */
  --success-background: #E8F5E8;   /* Light green background */
  --success-border: #C8E6C9;       /* Light green borders */
}

/* Cultural color adaptations */
.error-middle-east {
  --error-primary: #B71C1C;        /* Slightly deeper red for traditional feel */
  --warning-primary: #E65100;      /* Warmer orange for desert regions */
}

.error-southeast-asia {
  --error-primary: #D32F2F;        /* Brighter red for tropical visibility */
  --warning-primary: #FB8C00;      /* Vibrant orange for high-contrast environments */
}

.error-south-asia {
  --error-primary: #C62828;        /* Standard red with royal undertones */
  --warning-primary: #F57C00;      /* Saffron-influenced orange */
}
```

#### Error Typography
```css
/* Error message typography */
.error-message {
  font-family: 'Roboto', sans-serif;
  font-size: 16px;
  font-weight: 500;
  line-height: 1.5;
  color: var(--error-primary);
  margin: 8px 0;
}

.error-title {
  font-size: 20px;
  font-weight: 600;
  color: var(--error-primary);
  margin-bottom: 8px;
}

.error-description {
  font-size: 14px;
  font-weight: 400;
  color: var(--error-secondary);
  line-height: 1.6;
  margin-bottom: 16px;
}

/* Arabic error messages */
.error-message.arabic {
  font-family: 'Noto Sans Arabic', serif;
  direction: rtl;
  text-align: right;
  font-size: 18px;
  line-height: 1.8;
}

/* Error help text */
.error-help {
  font-size: 14px;
  font-weight: 400;
  color: #666666;
  font-style: italic;
  margin-top: 4px;
}
```

### 📱 Error Component Library

#### Input Validation Errors
```html
<!-- Memorial Name Validation -->
<div class="form-group error-state">
  <label for="memorial-name" class="form-label required">
    Memorial Name
    <span class="required-indicator">*</span>
  </label>
  
  <div class="input-container error">
    <input type="text" 
           id="memorial-name"
           name="name"
           class="form-input error"
           value="123"
           aria-invalid="true"
           aria-describedby="name-error name-help">
    <span class="error-icon" aria-hidden="true">⚠️</span>
  </div>
  
  <div id="name-error" class="error-message" role="alert">
    <div class="error-content">
      <h4 class="error-title">Name should contain letters only</h4>
      <p class="error-description">
        Memorial names should honor your loved one with letters and spaces. 
        Numbers and special characters are not appropriate for this sacred field.
      </p>
      <p class="error-description arabic" lang="ar">
        أسماء التذكار يجب أن تكرم أحباءك بالحروف والمسافات فقط
      </p>
    </div>
  </div>
  
  <div id="name-help" class="help-text">
    <p>✅ Good examples: Ahmad Ibn Muhammad, Fatima Zahra</p>
    <p>❌ Avoid: Ahmad123, F@tima, Special_Name</p>
  </div>
</div>

<!-- Date Validation with Cultural Sensitivity -->
<div class="form-group warning-state">
  <label for="passing-date" class="form-label required">
    Date of Passing
    <span class="required-indicator">*</span>
  </label>
  
  <div class="input-container warning">
    <input type="date" 
           id="passing-date"
           name="passingDate"
           class="form-input warning"
           value="2025-01-01"
           aria-invalid="false"
           aria-describedby="date-warning">
    <span class="warning-icon" aria-hidden="true">📅</span>
  </div>
  
  <div id="date-warning" class="warning-message" role="status">
    <div class="warning-content">
      <h4 class="warning-title">Future Date Selected</h4>
      <p class="warning-description">
        The date you selected is in the future. If this is correct, you may proceed. 
        If you meant to select a past date, please adjust accordingly.
      </p>
      <p class="warning-description arabic" lang="ar">
        التاريخ المحدد في المستقبل. إذا كان هذا صحيحاً، يمكنك المتابعة
      </p>
    </div>
  </div>
</div>
```

#### Photo Upload Errors
```html
<!-- Photo Upload Error States -->
<div class="photo-upload-container error-state">
  <div class="photo-upload-area error">
    <div class="upload-error-display">
      <div class="error-icon-large">📸</div>
      <h3 class="error-title">Photo Upload Failed</h3>
      <p class="error-description">
        We couldn't upload your memorial photo. This might be because:
      </p>
      
      <ul class="error-reasons">
        <li>The image file is too large (maximum 5MB)</li>
        <li>The file format isn't supported (use JPG, PNG, or WebP)</li>
        <li>Your internet connection was interrupted</li>
        <li>The image dimensions are too small (minimum 400x400 pixels)</li>
      </ul>
      
      <div class="error-suggestions">
        <h4>What you can do:</h4>
        <div class="suggestion-grid">
          <button class="suggestion-button" onclick="retryUpload()">
            <span class="button-icon">🔄</span>
            Try Upload Again
          </button>
          <button class="suggestion-button" onclick="chooseNewPhoto()">
            <span class="button-icon">📷</span>
            Choose Different Photo
          </button>
          <button class="suggestion-button" onclick="getUploadHelp()">
            <span class="button-icon">💡</span>
            Get Help
          </button>
        </div>
      </div>
    </div>
  </div>
  
  <div class="upload-progress" hidden>
    <div class="progress-bar">
      <div class="progress-fill" style="width: 0%"></div>
    </div>
    <p class="progress-text">Uploading your memorial photo...</p>
  </div>
</div>

<!-- Photo Quality Warning -->
<div class="photo-quality-warning warning-state">
  <div class="warning-header">
    <span class="warning-icon">📐</span>
    <h4 class="warning-title">Photo Quality Notice</h4>
  </div>
  
  <div class="warning-content">
    <p class="warning-description">
      Your photo will be displayed beautifully, but we notice it's quite small. 
      For the best memorial presentation, consider using a higher resolution image.
    </p>
    
    <div class="photo-comparison">
      <div class="current-photo">
        <img src="low-res-preview.jpg" alt="Current photo preview">
        <span class="photo-label">Current: 300x200 pixels</span>
      </div>
      <div class="recommended-arrow">→</div>
      <div class="recommended-photo">
        <div class="placeholder-recommendation">
          <span class="recommended-icon">✨</span>
          <span class="photo-label">Recommended: 800x600+ pixels</span>
        </div>
      </div>
    </div>
    
    <div class="warning-actions">
      <button class="button-secondary" onclick="keepCurrentPhoto()">
        Keep This Photo
      </button>
      <button class="button-primary" onclick="uploadBetterPhoto()">
        Upload Better Quality
      </button>
    </div>
  </div>
</div>
```

#### Prayer Session Errors
```html
<!-- Prayer Counter Sync Error -->
<div class="prayer-error-state">
  <div class="prayer-error-container">
    <div class="error-visual">
      <div class="prayer-counter-error">
        <div class="counter-error-icon">📿</div>
        <div class="sync-error-animation">
          <span class="sync-dot"></span>
          <span class="sync-dot"></span>
          <span class="sync-dot"></span>
        </div>
      </div>
    </div>
    
    <div class="error-content">
      <h3 class="error-title">Prayer Count Sync Issue</h3>
      <p class="error-description">
        We're having trouble saving your prayer progress to the cloud. 
        Your prayers are still being counted locally on your device.
      </p>
      
      <div class="current-status">
        <div class="status-item">
          <span class="status-icon">📱</span>
          <div class="status-content">
            <strong>On Your Device:</strong>
            <span>47 prayers counted ✅</span>
          </div>
        </div>
        <div class="status-item">
          <span class="status-icon">☁️</span>
          <div class="status-content">
            <strong>In the Cloud:</strong>
            <span>42 prayers saved ⏳</span>
          </div>
        </div>
      </div>
      
      <div class="error-actions">
        <button class="button-primary" onclick="retrySyncPrayers()">
          <span class="button-icon">🔄</span>
          Sync Prayers Now
        </button>
        <button class="button-secondary" onclick="continuePrayingOffline()">
          <span class="button-icon">📿</span>
          Continue Offline
        </button>
      </div>
      
      <p class="reassurance-message">
        Don't worry - your prayers are meaningful regardless of technical issues. 
        We'll sync everything when the connection improves.
      </p>
      <p class="reassurance-message arabic" lang="ar">
        لا تقلق - صلواتك ذات معنى بغض النظر عن المشاكل التقنية
      </p>
    </div>
  </div>
</div>

<!-- Audio Prayer Loading Error -->
<div class="audio-error-state">
  <div class="audio-error-container">
    <div class="error-visual">
      <div class="audio-error-icon">🔊</div>
      <div class="audio-wave-error">
        <span class="wave-line error"></span>
        <span class="wave-line error"></span>
        <span class="wave-line error"></span>
      </div>
    </div>
    
    <div class="error-content">
      <h3 class="error-title">Audio Recitation Unavailable</h3>
      <p class="error-description">
        The Surah Yasin audio recitation couldn't load. You can still read 
        the prayer text or try these alternatives:
      </p>
      
      <div class="alternative-options">
        <div class="option-card">
          <span class="option-icon">📖</span>
          <div class="option-content">
            <h4>Read Text Version</h4>
            <p>Follow along with the Arabic text and translation</p>
            <button class="option-button" onclick="showTextVersion()">
              Read Prayer Text
            </button>
          </div>
        </div>
        
        <div class="option-card">
          <span class="option-icon">📱</span>
          <div class="option-content">
            <h4>Download for Offline</h4>
            <p>Save audio for future use without internet</p>
            <button class="option-button" onclick="downloadAudio()">
              Download Audio
            </button>
          </div>
        </div>
        
        <div class="option-card">
          <span class="option-icon">🔄</span>
          <div class="option-content">
            <h4>Try Again Later</h4>
            <p>Recitation might be available when connection improves</p>
            <button class="option-button" onclick="retryAudioLoad()">
              Retry Loading
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</div>
```

---

## 🌐 Network & Connectivity Errors

### 📶 Offline State Design

#### Graceful Offline Experience
```html
<!-- Offline Mode Notification -->
<div class="offline-banner" role="alert" aria-live="assertive">
  <div class="offline-content">
    <span class="offline-icon">📴</span>
    <div class="offline-text">
      <strong>You're currently offline</strong>
      <span>Some features are limited, but your prayers are still being counted</span>
    </div>
    <button class="offline-dismiss" aria-label="Dismiss notification">×</button>
  </div>
</div>

<!-- Offline Memorial Creation -->
<div class="offline-memorial-state">
  <div class="offline-container">
    <div class="offline-visual">
      <span class="offline-icon-large">🕌</span>
      <div class="connection-indicator offline">
        <span class="connection-dot"></span>
        <span class="connection-dot"></span>
        <span class="connection-dot"></span>
      </div>
    </div>
    
    <div class="offline-content">
      <h3 class="offline-title">Creating Memorial Offline</h3>
      <p class="offline-description">
        You can create memorials even without internet. We'll save everything 
        on your device and sync it when you're back online.
      </p>
      
      <div class="offline-limitations">
        <h4>While offline, you can:</h4>
        <ul class="offline-can-do">
          <li>✅ Create new memorials</li>
          <li>✅ Count prayers (Tahlil, Al-Fatihah)</li>
          <li>✅ Read prayer texts</li>
          <li>✅ View existing memorials</li>
        </ul>
        
        <h4>When back online, you can:</h4>
        <ul class="offline-will-sync">
          <li>🔄 Sync all prayer counts</li>
          <li>🔄 Upload memorial photos</li>
          <li>🔄 Join community sessions</li>
          <li>🔄 Share with family</li>
        </ul>
      </div>
      
      <div class="offline-actions">
        <button class="button-primary" onclick="continueOffline()">
          Continue Creating Memorial
        </button>
        <button class="button-secondary" onclick="waitForConnection()">
          Wait for Connection
        </button>
      </div>
    </div>
  </div>
</div>

<!-- Connection Restored Notification -->
<div class="connection-restored-banner success-state" hidden>
  <div class="restored-content">
    <span class="success-icon">📶</span>
    <div class="restored-text">
      <strong>Connection restored!</strong>
      <span>Syncing your prayers and memorials now...</span>
    </div>
    <div class="sync-progress">
      <div class="progress-bar">
        <div class="progress-fill" style="width: 0%"></div>
      </div>
      <span class="sync-status">Syncing... 0/5 items</span>
    </div>
  </div>
</div>
```

#### Network Error Recovery
```html
<!-- Network Timeout Error -->
<div class="network-error-state">
  <div class="network-error-container">
    <div class="error-animation">
      <div class="network-icon">📡</div>
      <div class="signal-bars">
        <span class="bar low"></span>
        <span class="bar medium"></span>
        <span class="bar high error"></span>
      </div>
    </div>
    
    <div class="error-content">
      <h3 class="error-title">Connection Slow or Unstable</h3>
      <p class="error-description">
        Your internet connection is having difficulties. We're trying to 
        maintain your prayer session and keep everything synced.
      </p>
      
      <div class="connection-quality">
        <div class="quality-indicator">
          <span class="quality-label">Connection Quality:</span>
          <div class="quality-bars">
            <span class="quality-bar active"></span>
            <span class="quality-bar active"></span>
            <span class="quality-bar"></span>
            <span class="quality-bar"></span>
          </div>
          <span class="quality-text">Poor</span>
        </div>
      </div>
      
      <div class="network-suggestions">
        <h4>Improve your connection:</h4>
        <ul class="suggestion-list">
          <li>📶 Move closer to your WiFi router</li>
          <li>📱 Switch from WiFi to mobile data (or vice versa)</li>
          <li>🔄 Close other apps using internet</li>
          <li>⏰ Try again during less busy network hours</li>
        </ul>
      </div>
      
      <div class="network-actions">
        <button class="button-primary" onclick="retryConnection()">
          <span class="button-icon">🔄</span>
          Test Connection
        </button>
        <button class="button-secondary" onclick="enableOfflineMode()">
          <span class="button-icon">📴</span>
          Work Offline
        </button>
      </div>
    </div>
  </div>
</div>
```

---

## 🔐 Security & Privacy Errors

### 🛡️ Authentication Errors

#### Login Security Errors
```html
<!-- Failed Login Attempt -->
<div class="security-error-state">
  <div class="security-error-container">
    <div class="security-visual">
      <span class="security-icon">🔒</span>
      <div class="security-status failed">
        <span class="status-dot"></span>
      </div>
    </div>
    
    <div class="error-content">
      <h3 class="error-title">Login Unsuccessful</h3>
      <p class="error-description">
        The email or password you entered doesn't match our records. 
        Your memorial prayers are precious to us, and we want to keep them secure.
      </p>
      
      <div class="security-help">
        <h4>Common solutions:</h4>
        <div class="help-grid">
          <div class="help-item">
            <span class="help-icon">📧</span>
            <div class="help-content">
              <strong>Check your email</strong>
              <p>Make sure you're using the email you signed up with</p>
            </div>
          </div>
          
          <div class="help-item">
            <span class="help-icon">🔑</span>
            <div class="help-content">
              <strong>Reset password</strong>
              <p>Get a secure link to create a new password</p>
            </div>
          </div>
          
          <div class="help-item">
            <span class="help-icon">📱</span>
            <div class="help-content">
              <strong>Try another device</strong>
              <p>Sometimes device settings can cause issues</p>
            </div>
          </div>
        </div>
      </div>
      
      <div class="security-actions">
        <button class="button-primary" onclick="resetPassword()">
          <span class="button-icon">🔑</span>
          Reset Password
        </button>
        <button class="button-secondary" onclick="tryDifferentEmail()">
          <span class="button-icon">📧</span>
          Try Different Email
        </button>
      </div>
      
      <div class="security-reassurance">
        <p>🛡️ Your memorial data is always encrypted and protected</p>
        <p class="arabic" lang="ar">بياناتك التذكارية محمية دائماً ومشفرة</p>
      </div>
    </div>
  </div>
</div>

<!-- Account Locked Security -->
<div class="account-locked-error">
  <div class="locked-container">
    <div class="locked-visual">
      <span class="locked-icon">🔐</span>
      <div class="timer-display">
        <span class="timer-number">15:00</span>
        <span class="timer-label">minutes remaining</span>
      </div>
    </div>
    
    <div class="error-content">
      <h3 class="error-title">Account Temporarily Locked</h3>
      <p class="error-description">
        For your account's security, we've temporarily locked access after 
        several unsuccessful login attempts. This protects your precious 
        memorial content from unauthorized access.
      </p>
      
      <div class="security-timeline">
        <div class="timeline-item completed">
          <span class="timeline-icon">⚠️</span>
          <div class="timeline-content">
            <strong>5 failed attempts detected</strong>
            <span>Account automatically locked for security</span>
          </div>
        </div>
        
        <div class="timeline-item current">
          <span class="timeline-icon">⏰</span>
          <div class="timeline-content">
            <strong>15-minute security lockout</strong>
            <span>Temporary protection period active</span>
          </div>
        </div>
        
        <div class="timeline-item upcoming">
          <span class="timeline-icon">🔓</span>
          <div class="timeline-content">
            <strong>Access restored automatically</strong>
            <span>No action needed from you</span>
          </div>
        </div>
      </div>
      
      <div class="lockout-actions">
        <button class="button-primary" onclick="requestPasswordReset()">
          <span class="button-icon">📧</span>
          Reset Password Now
        </button>
        <button class="button-secondary" onclick="waitForUnlock()">
          <span class="button-icon">⏰</span>
          Wait for Auto-Unlock
        </button>
      </div>
      
      <div class="security-contact">
        <p>Need immediate help? Contact our support team:</p>
        <p>📧 security@tahlilapp.com</p>
        <p>📞 +1-800-TAHLIL-HELP</p>
      </div>
    </div>
  </div>
</div>
```

#### Privacy Permission Errors
```html
<!-- Photo Permission Denied -->
<div class="permission-error-state">
  <div class="permission-container">
    <div class="permission-visual">
      <span class="permission-icon">📷</span>
      <div class="permission-status denied">
        <span class="status-icon">🚫</span>
      </div>
    </div>
    
    <div class="error-content">
      <h3 class="error-title">Camera Permission Needed</h3>
      <p class="error-description">
        To add memorial photos, Tahlil needs access to your camera. 
        We respect your privacy and only use this to help you honor 
        your loved ones with dignity.
      </p>
      
      <div class="privacy-explanation">
        <h4>How we protect your photos:</h4>
        <ul class="privacy-list">
          <li>🔒 Photos are encrypted on your device</li>
          <li>🛡️ Only you control who sees memorial photos</li>
          <li>📱 Camera access only when you choose to take photos</li>
          <li>🗑️ You can delete photos anytime</li>
        </ul>
      </div>
      
      <div class="permission-steps">
        <h4>Enable camera access:</h4>
        <div class="steps-container">
          <div class="step">
            <span class="step-number">1</span>
            <div class="step-content">
              <strong>Tap "Settings" below</strong>
              <span>Opens your device settings</span>
            </div>
          </div>
          
          <div class="step">
            <span class="step-number">2</span>
            <div class="step-content">
              <strong>Find "Tahlil" in app list</strong>
              <span>Look for our app in permissions</span>
            </div>
          </div>
          
          <div class="step">
            <span class="step-number">3</span>
            <div class="step-content">
              <strong>Enable "Camera" permission</strong>
              <span>Allow access for memorial photos</span>
            </div>
          </div>
        </div>
      </div>
      
      <div class="permission-actions">
        <button class="button-primary" onclick="openDeviceSettings()">
          <span class="button-icon">⚙️</span>
          Open Settings
        </button>
        <button class="button-secondary" onclick="skipPhotoUpload()">
          <span class="button-icon">📝</span>
          Create Without Photo
        </button>
      </div>
      
      <div class="alternative-options">
        <p>Alternative: You can also choose existing photos from your gallery</p>
        <button class="alternative-button" onclick="openGallery()">
          <span class="button-icon">🖼️</span>
          Choose from Gallery
        </button>
      </div>
    </div>
  </div>
</div>
```

---

## 📱 Device & Platform Errors

### 📱 Device Compatibility Issues

#### Unsupported Device Features
```html
<!-- Audio Not Supported -->
<div class="device-limitation-error">
  <div class="limitation-container">
    <div class="limitation-visual">
      <span class="device-icon">📱</span>
      <div class="feature-status">
        <span class="feature-icon">🔊</span>
        <span class="limitation-indicator">⚠️</span>
      </div>
    </div>
    
    <div class="error-content">
      <h3 class="error-title">Audio Features Limited</h3>
      <p class="error-description">
        Your device has limited audio capabilities, but you can still 
        experience beautiful memorial prayers through text and visual elements.
      </p>
      
      <div class="available-features">
        <h4>What works perfectly on your device:</h4>
        <div class="feature-grid">
          <div class="feature-available">
            <span class="feature-icon">📖</span>
            <div class="feature-content">
              <strong>Prayer Text</strong>
              <p>Arabic text with translations</p>
            </div>
          </div>
          
          <div class="feature-available">
            <span class="feature-icon">📿</span>
            <div class="feature-content">
              <strong>Prayer Counter</strong>
              <p>Digital tasbih for counting</p>
            </div>
          </div>
          
          <div class="feature-available">
            <span class="feature-icon">🖼️</span>
            <div class="feature-content">
              <strong>Memorial Photos</strong>
              <p>Beautiful photo displays</p>
            </div>
          </div>
          
          <div class="feature-available">
            <span class="feature-icon">👥</span>
            <div class="feature-content">
              <strong>Community</strong>
              <p>Connect with other users</p>
            </div>
          </div>
        </div>
      </div>
      
      <div class="limitation-workarounds">
        <h4>For audio prayers, you can:</h4>
        <ul class="workaround-list">
          <li>🎧 Use external speakers or headphones</li>
          <li>📱 Try on a different device</li>
          <li>💻 Use the web version on a computer</li>
          <li>📖 Follow along with written prayers</li>
        </ul>
      </div>
      
      <div class="limitation-actions">
        <button class="button-primary" onclick="continueWithText()">
          <span class="button-icon">📖</span>
          Continue with Text
        </button>
        <button class="button-secondary" onclick="learnMoreAudio()">
          <span class="button-icon">🎧</span>
          Audio Solutions
        </button>
      </div>
    </div>
  </div>
</div>

<!-- Storage Limitation -->
<div class="storage-error-state">
  <div class="storage-container">
    <div class="storage-visual">
      <div class="storage-meter">
        <div class="storage-fill" style="width: 95%"></div>
        <span class="storage-icon">📱</span>
      </div>
      <div class="storage-status">95% Full</div>
    </div>
    
    <div class="error-content">
      <h3 class="error-title">Device Storage Almost Full</h3>
      <p class="error-description">
        Your device is running low on storage space. This might affect 
        your ability to save new memorial photos or download prayer audio.
      </p>
      
      <div class="storage-breakdown">
        <h4>Storage needed for Tahlil features:</h4>
        <div class="storage-requirements">
          <div class="requirement-item">
            <span class="requirement-icon">🖼️</span>
            <div class="requirement-content">
              <strong>Memorial Photos</strong>
              <span>~2-5 MB per photo</span>
            </div>
          </div>
          
          <div class="requirement-item">
            <span class="requirement-icon">🔊</span>
            <div class="requirement-content">
              <strong>Prayer Audio</strong>
              <span>~15-25 MB per recitation</span>
            </div>
          </div>
          
          <div class="requirement-item">
            <span class="requirement-icon">💾</span>
            <div class="requirement-content">
              <strong>App Data</strong>
              <span>~50-100 MB for prayer history</span>
            </div>
          </div>
        </div>
      </div>
      
      <div class="storage-solutions">
        <h4>Free up space:</h4>
        <div class="solution-grid">
          <button class="solution-card" onclick="cleanAppCache()">
            <span class="solution-icon">🧹</span>
            <div class="solution-content">
              <strong>Clean App Cache</strong>
              <span>Safe cleanup of temporary files</span>
            </div>
          </button>
          
          <button class="solution-card" onclick="managePhotos()">
            <span class="solution-icon">📱</span>
            <div class="solution-content">
              <strong>Manage Device Photos</strong>
              <span>Move photos to cloud storage</span>
            </div>
          </button>
          
          <button class="solution-card" onclick="downloadLessAudio()">
            <span class="solution-icon">🔊</span>
            <div class="solution-content">
              <strong>Stream Audio</strong>
              <span>Don't download, just stream</span>
            </div>
          </button>
        </div>
      </div>
      
      <div class="storage-actions">
        <button class="button-primary" onclick="openDeviceStorage()">
          <span class="button-icon">📱</span>
          Open Device Storage
        </button>
        <button class="button-secondary" onclick="continueWithLimitations()">
          <span class="button-icon">📖</span>
          Continue Anyway
        </button>
      </div>
    </div>
  </div>
</div>
```

---

## 🔄 Recovery and Success States

### ✅ Error Resolution Feedback

#### Successful Recovery
```html
<!-- Prayer Sync Recovered -->
<div class="recovery-success-state">
  <div class="success-container">
    <div class="success-visual">
      <div class="success-icon-large">✅</div>
      <div class="recovery-animation">
        <span class="success-ripple"></span>
        <span class="success-ripple"></span>
        <span class="success-ripple"></span>
      </div>
    </div>
    
    <div class="success-content">
      <h3 class="success-title">Prayer Progress Restored!</h3>
      <p class="success-description">
        All your prayer counts have been successfully synced. 
        Your memorial prayers are now safely stored in the cloud.
      </p>
      
      <div class="recovery-summary">
        <div class="sync-results">
          <div class="sync-item">
            <span class="sync-icon">📿</span>
            <div class="sync-content">
              <strong>47 Tahlil prayers</strong>
              <span>Synced for Ahmed Ibn Mohammad</span>
            </div>
            <span class="sync-status">✅</span>
          </div>
          
          <div class="sync-item">
            <span class="sync-icon">📖</span>
            <div class="sync-content">
              <strong>1 Surah Yasin</strong>
              <span>Synced for Fatima Zahra</span>
            </div>
            <span class="sync-status">✅</span>
          </div>
          
          <div class="sync-item">
            <span class="sync-icon">🤲</span>
            <div class="sync-content">
              <strong>21 Personal Du'a</strong>
              <span>Synced for Hassan Ali</span>
            </div>
            <span class="sync-status">✅</span>
          </div>
        </div>
      </div>
      
      <div class="success-actions">
        <button class="button-primary" onclick="continuePraying()">
          <span class="button-icon">📿</span>
          Continue Praying
        </button>
        <button class="button-secondary" onclick="viewAllMemorials()">
          <span class="button-icon">🏠</span>
          View All Memorials
        </button>
      </div>
      
      <div class="success-reassurance">
        <p>🌟 Your prayers bring peace and blessings</p>
        <p class="arabic" lang="ar">صلواتك تجلب السلام والبركة</p>
      </div>
    </div>
  </div>
</div>

<!-- Memorial Created Successfully -->
<div class="memorial-success-state">
  <div class="memorial-success-container">
    <div class="success-visual">
      <div class="memorial-created-icon">🕌</div>
      <div class="blessing-particles">
        <span class="particle"></span>
        <span class="particle"></span>
        <span class="particle"></span>
        <span class="particle"></span>
      </div>
    </div>
    
    <div class="success-content">
      <h3 class="success-title">Memorial Created with Love</h3>
      <p class="success-description">
        You have successfully created a beautiful memorial for Ahmed Ibn Mohammad. 
        Your prayers and remembrance will honor their memory.
      </p>
      
      <div class="memorial-preview">
        <div class="preview-card">
          <img src="memorial-photo.jpg" alt="Memorial photo of Ahmed Ibn Mohammad" class="preview-photo">
          <div class="preview-content">
            <h4 class="memorial-name">Ahmed Ibn Mohammad</h4>
            <p class="memorial-dates">1965 - 2024</p>
            <p class="memorial-relationship">Beloved Father</p>
          </div>
        </div>
      </div>
      
      <div class="next-steps">
        <h4>Start honoring their memory:</h4>
        <div class="steps-grid">
          <button class="step-card" onclick="startPraying()">
            <span class="step-icon">📿</span>
            <div class="step-content">
              <strong>Begin Tahlil Prayers</strong>
              <span>Start with traditional 100 recitations</span>
            </div>
          </button>
          
          <button class="step-card" onclick="shareMemorial()">
            <span class="step-icon">👥</span>
            <div class="step-content">
              <strong>Share with Family</strong>
              <span>Invite others to join in prayers</span>
            </div>
          </button>
          
          <button class="step-card" onclick="setReminders()">
            <span class="step-icon">⏰</span>
            <div class="step-content">
              <strong>Set Prayer Reminders</strong>
              <span>Never miss important memorial dates</span>
            </div>
          </button>
        </div>
      </div>
      
      <div class="spiritual-message">
        <p>🤲 "And those who believe and whose descendants follow them in faith - We will join with them their descendants." - Quran 52:21</p>
        <p class="arabic" lang="ar">
          "والذين آمنوا واتبعتهم ذريتهم بإيمان ألحقنا بهم ذريتهم"
        </p>
      </div>
    </div>
  </div>
</div>
```

---

## 📊 Error Analytics & Feedback

### 📈 Error Tracking System
```javascript
// Error tracking and analytics
const errorAnalytics = {
  // Track error occurrences
  trackError: function(errorType, context, userAction) {
    const errorData = {
      type: errorType,
      context: context,
      userAction: userAction,
      timestamp: new Date().toISOString(),
      userAgent: navigator.userAgent,
      url: window.location.href,
      userId: this.getCurrentUserId(),
      cultural_context: this.getUserCulturalSettings()
    };
    
    // Send to analytics service
    this.sendErrorReport(errorData);
  },
  
  // Common error categories
  errorTypes: {
    VALIDATION: 'form_validation_error',
    NETWORK: 'network_connectivity_error', 
    PERMISSION: 'device_permission_error',
    STORAGE: 'device_storage_error',
    AUDIO: 'audio_playback_error',
    PHOTO: 'photo_upload_error',
    SYNC: 'prayer_sync_error',
    AUTH: 'authentication_error'
  },
  
  // Error resolution tracking
  trackResolution: function(errorId, resolutionMethod, successful) {
    const resolutionData = {
      errorId: errorId,
      method: resolutionMethod,
      successful: successful,
      timestamp: new Date().toISOString(),
      timeToResolve: this.calculateResolutionTime(errorId)
    };
    
    this.sendResolutionReport(resolutionData);
  }
};

// Cultural context for errors
const culturalErrorContext = {
  getCulturalErrorMessage: function(errorType, userCulture) {
    const messages = {
      'photo_upload_failed': {
        'middle-east': 'We apologize for the difficulty in uploading your loved one\'s photo. Please try again.',
        'southeast-asia': 'Photo upload encountered an issue. Let\'s try a different approach.',
        'south-asia': 'The memorial photo couldn\'t be saved. We\'ll help you resolve this.',
        'western': 'Photo upload failed. Here are some solutions to try.'
      },
      'prayer_sync_error': {
        'middle-east': 'Your prayers are counted by Allah, even if our technology has difficulties.',
        'southeast-asia': 'Prayer counting continues offline. We\'ll sync when possible.',
        'south-asia': 'Your devotional counting continues. Technical sync will happen automatically.',
        'western': 'Prayer progress saved locally. Cloud sync will resume when connection improves.'
      }
    };
    
    return messages[errorType]?.[userCulture] || messages[errorType]?.['western'];
  }
};
```

### 🔄 Error Prevention Strategies
```javascript
// Proactive error prevention
const errorPrevention = {
  // Form validation before submission
  validateMemorialForm: function(formData) {
    const errors = [];
    
    if (!formData.name || formData.name.length < 2) {
      errors.push({
        field: 'name',
        type: 'required',
        message: 'Memorial name should be at least 2 characters long'
      });
    }
    
    if (formData.name && /\d/.test(formData.name)) {
      errors.push({
        field: 'name', 
        type: 'format',
        message: 'Memorial names should contain letters only, not numbers'
      });
    }
    
    if (formData.passingDate && new Date(formData.passingDate) > new Date()) {
      errors.push({
        field: 'passingDate',
        type: 'logic',
        message: 'Passing date cannot be in the future'
      });
    }
    
    return errors;
  },
  
  // Network quality detection
  checkNetworkQuality: function() {
    return new Promise((resolve) => {
      const startTime = Date.now();
      
      fetch('/api/ping', { 
        method: 'HEAD',
        cache: 'no-cache'
      }).then(() => {
        const duration = Date.now() - startTime;
        const quality = duration < 100 ? 'excellent' :
                       duration < 300 ? 'good' :
                       duration < 1000 ? 'fair' : 'poor';
        
        resolve({ quality, latency: duration });
      }).catch(() => {
        resolve({ quality: 'offline', latency: Infinity });
      });
    });
  },
  
  // Storage space monitoring
  checkStorageSpace: function() {
    if ('storage' in navigator && 'estimate' in navigator.storage) {
      return navigator.storage.estimate().then(estimate => {
        const usedPercentage = (estimate.usage / estimate.quota) * 100;
        return {
          available: estimate.quota - estimate.usage,
          usedPercentage: usedPercentage,
          warning: usedPercentage > 85,
          critical: usedPercentage > 95
        };
      });
    }
    
    return Promise.resolve({ available: null, warning: false });
  }
};
```

---

## ✅ Error Design Quality Checklist

### 📋 Error State Validation
```
Visual Design Checklist:
□ Error colors meet WCAG AA contrast requirements
□ Error icons are culturally appropriate and clear
□ Arabic error text properly formatted and accurate
□ Error animations are gentle and non-intrusive
□ Success states provide positive reinforcement
□ Recovery paths are visually prominent

Content Quality Checklist:
□ Error messages use compassionate, helpful language
□ Technical jargon avoided in user-facing messages
□ Solutions provided for every error state
□ Cultural sensitivity maintained across regions
□ Islamic values reflected in error communication
□ Multiple resolution paths offered when possible

Accessibility Checklist:
□ Error messages announce properly to screen readers
□ Focus management works correctly in error states
□ Keyboard navigation functional during errors
□ Error states work with voice control
□ High contrast mode compatible
□ Error recovery possible without mouse/touch

User Experience Checklist:
□ Error prevention implemented where possible
□ Recovery actions clearly labeled and accessible
□ Progress preserved during error resolution
□ Offline functionality gracefully handled
□ Network issues communicated transparently
□ User confidence maintained throughout errors
```

---

**Error State System Maintained By**: UX Design Team + QA Engineering + Islamic Cultural Consultants  
**Error Analytics**: Automated tracking + User feedback integration  
**Cultural Validation**: Regional user testing + Islamic scholar review  
**Continuous Improvement**: Monthly error pattern analysis and resolution optimization  
**Next Review**: Post-launch error pattern analysis (June 2026)  

*This comprehensive error state system ensures that even when things go wrong, users feel supported, respected, and guided toward resolution while maintaining the spiritual dignity of their memorial prayer experience.*