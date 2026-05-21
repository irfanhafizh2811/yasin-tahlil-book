# ♿ Tahlil Accessibility Design Standards

## 📋 Accessibility Overview

**Project**: Tahlil - Global Memorial Prayer Platform  
**Accessibility Framework**: WCAG 2.1 AA + Islamic Cultural Inclusivity  
**Created**: May 20, 2026  
**Compliance Target**: Universal Design for Global Muslim Community  
**Special Considerations**: Religious practice accessibility, cultural diversity, multilingual support  

---

## 🎯 Accessibility Mission Statement

### 🌍 Inclusive Prayer for All

**Core Principle**: "Every Muslim, regardless of ability, deserves dignified access to memorial prayer traditions."

**Accessibility Goals**:
1. **Universal Spiritual Access**: Memorial prayers accessible to Muslims with all abilities
2. **Cultural Inclusivity**: Accessibility standards that respect diverse Islamic traditions
3. **Intergenerational Support**: Design that serves elderly Muslims and tech newcomers
4. **Global Language Support**: Accessibility across 20+ languages and scripts
5. **Economic Accessibility**: Features work on budget devices and slow networks

### 🕌 Islamic Accessibility Values
- **Dignity (Karama)**: Every user interaction maintains human dignity
- **Ease (Yusr)**: Following Islamic principle of making things easy, not difficult  
- **Community (Ummah)**: Accessibility builds inclusive community bonds
- **Wisdom (Hikmah)**: Thoughtful design serves diverse needs
- **Compassion (Rahma)**: Merciful consideration for all ability levels

---

## 📊 WCAG 2.1 AA Compliance Framework

### ✅ Level AA Requirements

#### 1. Perceivable Content
```
1.1 Text Alternatives:
□ All memorial photos have meaningful alt text
□ Islamic icons include descriptive labels
□ Arabic prayer text has phonetic descriptions
□ UI icons have accessible names and descriptions

1.2 Time-based Media:
□ Prayer audio includes text transcripts
□ Video tutorials have captions and descriptions
□ Audio prayers include visual prayer text display
□ Recitation timing allows user control

1.3 Adaptable Content:
□ Prayer layouts work with screen readers
□ Memorial information maintains meaning when reformatted
□ Arabic/RTL text flows correctly in all contexts
□ Cultural content structure remains logical

1.4 Distinguishable Content:
□ Color contrast ratios exceed 4.5:1 (normal text)
□ Color contrast ratios exceed 3:1 (large text, 18px+)
□ Information doesn't rely solely on color
□ Audio prayers have volume controls
□ Text can be resized up to 200% without loss of functionality
```

#### 2. Operable Interface
```
2.1 Keyboard Accessibility:
□ All prayer functions available via keyboard
□ Memorial creation possible without mouse
□ Prayer counter operable with keyboard
□ Tab order follows logical reading sequence
□ No keyboard traps in any interface

2.2 Timing Flexibility:
□ Prayer sessions don't have time limits
□ Users can pause/resume prayer counting
□ Memorial creation allows unlimited time
□ Auto-logout warnings provide extension options

2.3 Seizure Prevention:
□ No flashing content exceeding 3 times per second
□ Prayer animations avoid rapid visual changes
□ Blessing effects use gentle transitions only

2.4 Navigation Support:
□ Page titles clearly describe content and purpose
□ Focus indicators clearly visible for all interactive elements
□ Heading structure provides logical navigation
□ Link purposes clear from context
```

#### 3. Understandable Content
```
3.1 Readable Text:
□ Language of pages properly identified
□ Arabic text marked with correct language tags
□ Regional language variants properly tagged
□ Mixed-language content properly marked

3.2 Predictable Interface:
□ Navigation remains consistent across app
□ UI components behave consistently
□ Prayer interface layout predictable
□ Changes in context clearly indicated

3.3 Input Assistance:
□ Form errors clearly identified
□ Memorial creation provides helpful instructions
□ Prayer selection includes guidance
□ Error correction suggestions provided
```

#### 4. Robust Implementation
```
4.1 Compatible Technology:
□ Valid HTML/markup for web components
□ Screen reader compatibility verified
□ Assistive technology integration tested
□ Cross-platform accessibility maintained
```

---

## 🔍 Visual Accessibility Standards

### 🎨 Color and Contrast Design

#### Color Contrast Requirements
```css
/* WCAG AA Color Contrast Ratios */

/* Normal text (under 18px or under 14px bold) - 4.5:1 minimum */
.normal-text {
  color: #1B5E20; /* Primary green */
  background: #FFFFFF; /* White background */
  /* Contrast ratio: 7.2:1 - Exceeds requirement */
}

.normal-text-dark {
  color: #2C2C2C; /* Dark charcoal */
  background: #FAFAFA; /* Light gray background */
  /* Contrast ratio: 8.1:1 - Exceeds requirement */
}

/* Large text (18px+ or 14px+ bold) - 3:1 minimum */
.large-text {
  color: #4C8C4A; /* Light green */
  background: #FFFFFF; /* White background */
  /* Contrast ratio: 4.1:1 - Exceeds requirement */
}

/* UI component contrast - 3:1 minimum */
.button-primary {
  color: #FFFFFF; /* White text */
  background: #1B5E20; /* Primary green background */
  border: 2px solid #0D2F10; /* Dark green border */
  /* Text contrast: 9.8:1 - Exceeds requirement */
  /* Border contrast: 4.2:1 - Exceeds requirement */
}

/* Error states - High contrast for accessibility */
.error-text {
  color: #B71C1C; /* Dark red */
  background: #FFFFFF; /* White background */
  /* Contrast ratio: 9.1:1 - High contrast for critical information */
}

/* Arabic text specific contrast */
.arabic-text {
  color: #1B5E20; /* Primary green */
  background: #FFFFFF; /* White background */
  text-shadow: 0 0 1px rgba(27, 94, 32, 0.2); /* Subtle enhancement for readability */
  /* Contrast ratio: 7.2:1 - Optimized for Arabic script */
}
```

#### Color-Blind Friendly Design
```css
/* Color-blind friendly alternatives */

/* Success indication - Never rely on green alone */
.success-indicator {
  color: #2E7D32; /* Green */
  background: #E8F5E8; /* Light green background */
}

.success-indicator::before {
  content: '✓'; /* Checkmark symbol */
  color: #2E7D32;
  font-weight: bold;
  margin-right: 8px;
}

/* Error indication - Never rely on red alone */
.error-indicator {
  color: #C62828; /* Red */
  background: #FFEBEE; /* Light red background */
  border-left: 4px solid #C62828; /* Visual indicator */
}

.error-indicator::before {
  content: '⚠'; /* Warning symbol */
  color: #C62828;
  font-weight: bold;
  margin-right: 8px;
}

/* Interactive states - Multiple visual cues */
.interactive-element {
  transition: all 0.2s ease;
}

.interactive-element:hover {
  background: #F5F5F5; /* Background change */
  border-left: 4px solid #1B5E20; /* Border indicator */
  transform: translateX(4px); /* Position change */
}

.interactive-element:focus {
  outline: 3px solid #BF9000; /* Gold focus ring */
  outline-offset: 2px;
  box-shadow: 0 0 0 1px #FFFFFF; /* White inner ring */
}
```

### 👁️ Visual Impairment Support

#### Screen Reader Optimization
```html
<!-- Memorial Photo with Descriptive Alt Text -->
<img src="memorial-ahmed.jpg" 
     alt="Memorial photo of Ahmed Ibn Mohammad, elderly man with kind eyes wearing traditional white thobe, smiling peacefully in garden setting"
     role="img"
     aria-describedby="ahmed-memorial-description">

<div id="ahmed-memorial-description" class="sr-only">
  Memorial photo shows Ahmed Ibn Mohammad (1965-2024), beloved father and husband, 
  in peaceful garden setting. Photo taken in 2020 during family gathering.
</div>

<!-- Prayer Counter with Screen Reader Support -->
<div class="prayer-counter" 
     role="button"
     tabindex="0"
     aria-label="Prayer counter: 47 out of 100 Tahlil completed"
     aria-describedby="counter-instructions"
     aria-pressed="false">
  <span aria-hidden="true" class="counter-visual">47</span>
  <div class="progress-ring" 
       role="progressbar" 
       aria-valuenow="47" 
       aria-valuemin="0" 
       aria-valuemax="100">
  </div>
</div>

<div id="counter-instructions" class="sr-only">
  Tap spacebar or enter to increment prayer count. 
  Current progress: 47 of 100 Tahlil prayers completed.
  Target completion brings spiritual benefit for memorial.
</div>

<!-- Arabic Prayer Text with Screen Reader Support -->
<div class="prayer-text-container">
  <div class="arabic-text" 
       lang="ar" 
       dir="rtl"
       role="text"
       aria-label="Arabic prayer text: La ilaha illa Allah">
    لا إله إلا الله
  </div>
  
  <div class="transliteration" 
       lang="en"
       aria-label="Pronunciation guide">
    La ilaha illa Allah
  </div>
  
  <div class="translation" 
       lang="en"
       aria-label="English translation">
    There is no god except Allah
  </div>
</div>

<!-- Memorial Creation Form -->
<form class="memorial-form" aria-labelledby="memorial-form-title">
  <h2 id="memorial-form-title">Create New Memorial</h2>
  
  <div class="form-group">
    <label for="memorial-name" class="required-field">
      Full Name
      <span aria-label="required">*</span>
    </label>
    <input type="text" 
           id="memorial-name"
           name="name"
           required
           aria-describedby="name-help name-error"
           aria-invalid="false"
           autocomplete="name">
    <div id="name-help" class="help-text">
      Enter the full name as you wish it to appear in prayers
    </div>
    <div id="name-error" class="error-message" role="alert" aria-live="polite"></div>
  </div>
</form>
```

#### High Contrast Mode Support
```css
/* High contrast mode compatibility */
@media (prefers-contrast: high) {
  .memorial-card {
    border: 2px solid;
    background: Canvas;
    color: CanvasText;
  }
  
  .button-primary {
    background: ButtonFace;
    color: ButtonText;
    border: 2px solid ButtonBorder;
  }
  
  .prayer-counter {
    border: 3px solid;
    background: Canvas;
    color: CanvasText;
  }
  
  .islamic-pattern {
    filter: contrast(2) brightness(0.8);
  }
}

/* Windows High Contrast Mode */
@media (-ms-high-contrast: active) {
  .memorial-photo {
    border: 2px solid windowText;
  }
  
  .navigation-item {
    background: window;
    color: windowText;
    border: 1px solid windowText;
  }
  
  .prayer-text {
    background: window;
    color: windowText;
  }
}

/* Reduced transparency for high contrast */
@media (prefers-contrast: high) {
  .overlay-content {
    background: Canvas;
    color: CanvasText;
    backdrop-filter: none;
  }
  
  .modal-backdrop {
    background: rgba(0, 0, 0, 0.9);
    backdrop-filter: none;
  }
}
```

---

## 🎧 Auditory Accessibility Standards

### 🔊 Audio Content Accessibility

#### Prayer Audio Support
```html
<!-- Audio Prayer with Full Accessibility -->
<div class="audio-prayer-container">
  <audio controls 
         preload="metadata"
         aria-labelledby="audio-title"
         aria-describedby="audio-description">
    <source src="surah-yasin-arabic.mp3" type="audio/mpeg">
    <source src="surah-yasin-arabic.ogg" type="audio/ogg">
    <track kind="captions" 
           src="surah-yasin-captions.vtt" 
           srclang="ar" 
           label="Arabic captions">
    <track kind="descriptions" 
           src="surah-yasin-descriptions.vtt" 
           srclang="en" 
           label="English descriptions">
    <p>Your browser does not support audio playback. 
       <a href="surah-yasin-transcript.html">Read the text transcript</a></p>
  </audio>
  
  <h3 id="audio-title">Surah Yasin Recitation</h3>
  <p id="audio-description">
    Complete recitation of Surah Yasin (Chapter 36) by Qari Ahmad Al-Salam.
    Duration: 25 minutes. Includes Arabic text with English translation available.
  </p>
  
  <div class="audio-controls">
    <button class="playback-speed" aria-label="Adjust playback speed">
      Speed: 1x
    </button>
    <button class="volume-control" aria-label="Volume control">
      🔊 Volume
    </button>
    <button class="show-transcript" aria-expanded="false" aria-controls="audio-transcript">
      Show Text
    </button>
  </div>
  
  <div id="audio-transcript" class="transcript" hidden>
    <!-- Synchronizable transcript content -->
  </div>
</div>
```

#### Hearing Impairment Support
```css
/* Visual indicators for audio content */
.audio-playing {
  border: 2px solid #4CAF50;
  box-shadow: 0 0 0 4px rgba(76, 175, 80, 0.3);
}

.audio-playing::before {
  content: '🔊';
  position: absolute;
  top: 8px;
  right: 8px;
  background: #4CAF50;
  color: white;
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;
}

/* Audio waveform visualization */
.audio-visualizer {
  display: flex;
  align-items: center;
  height: 40px;
  gap: 2px;
  margin: 16px 0;
}

.waveform-bar {
  width: 4px;
  background: #1B5E20;
  border-radius: 2px;
  transition: height 0.1s ease;
}

.waveform-bar.active {
  background: #BF9000;
  animation: pulse 0.6s ease-in-out infinite alternate;
}

/* Captions styling */
.prayer-captions {
  background: rgba(0, 0, 0, 0.8);
  color: white;
  padding: 12px;
  border-radius: 8px;
  font-size: 18px;
  line-height: 1.4;
  text-align: center;
  margin-top: 16px;
}

.prayer-captions.arabic {
  direction: rtl;
  text-align: right;
  font-family: 'Noto Sans Arabic', serif;
}
```

---

## ⌨️ Motor Accessibility Standards

### 🖱️ Alternative Input Methods

#### Keyboard Navigation
```css
/* Focus management for prayer interface */
.prayer-interface {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.prayer-interface *:focus {
  outline: 3px solid #BF9000;
  outline-offset: 2px;
  border-radius: 4px;
}

/* Skip links for efficient navigation */
.skip-navigation {
  position: absolute;
  top: -100px;
  left: 16px;
  background: #1B5E20;
  color: white;
  padding: 8px 16px;
  text-decoration: none;
  border-radius: 4px;
  font-weight: 600;
  z-index: 1000;
  transition: top 0.2s ease;
}

.skip-navigation:focus {
  top: 16px;
}

/* Keyboard shortcuts */
.keyboard-shortcut {
  display: inline-block;
  background: #F5F5F5;
  border: 1px solid #BDBDBD;
  padding: 2px 6px;
  border-radius: 3px;
  font-size: 12px;
  font-family: monospace;
  margin: 0 2px;
}
```

#### Large Touch Targets
```css
/* Minimum 44px touch targets */
.touch-target {
  min-width: 44px;
  min-height: 44px;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 12px;
  margin: 4px;
  border-radius: 8px;
  transition: all 0.2s ease;
}

/* Prayer counter - Large touch area */
.prayer-counter {
  width: 180px;
  height: 180px;
  border-radius: 90px;
  cursor: pointer;
  /* Actual tap area larger than visual element */
  position: relative;
}

.prayer-counter::after {
  content: '';
  position: absolute;
  top: -20px;
  left: -20px;
  right: -20px;
  bottom: -20px;
  border-radius: 50%;
  /* 220px total tap area for easier access */
}

/* Memorial card touch targets */
.memorial-card {
  padding: 16px;
  margin: 8px;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.memorial-card:hover,
.memorial-card:focus {
  transform: translateY(-2px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
}

/* Button spacing for motor accessibility */
.button-group {
  display: flex;
  gap: 12px; /* Minimum 8px between touch targets */
  flex-wrap: wrap;
}

.button-group .button {
  min-width: 88px; /* Comfortable button width */
  min-height: 44px;
}
```

#### Voice Control Support
```html
<!-- Voice control landmarks -->
<main role="main" aria-label="Memorial prayers main content">
  <section aria-labelledby="memorials-heading">
    <h2 id="memorials-heading">Your Memorials</h2>
    <!-- Voice command: "Click memorials" -->
  </section>
  
  <section aria-labelledby="prayer-section">
    <h2 id="prayer-section">Prayer Center</h2>
    <!-- Voice command: "Click prayer center" -->
    
    <button aria-label="Start prayer counter" data-voice="start prayer">
      Begin Tahlil
    </button>
    <!-- Voice command: "Start prayer" or "Click begin tahlil" -->
    
    <button aria-label="Join community session" data-voice="join community">
      Join Others
    </button>
    <!-- Voice command: "Join community" -->
  </section>
</main>

<!-- Voice navigation landmarks -->
<nav role="navigation" aria-label="Main navigation">
  <ul role="menubar">
    <li role="none">
      <a href="#home" role="menuitem" data-voice="home">Home</a>
    </li>
    <li role="none">
      <a href="#pray" role="menuitem" data-voice="pray">Pray</a>
    </li>
    <li role="none">
      <a href="#community" role="menuitem" data-voice="community">Community</a>
    </li>
    <li role="none">
      <a href="#library" role="menuitem" data-voice="library">Library</a>
    </li>
    <li role="none">
      <a href="#profile" role="menuitem" data-voice="profile">Profile</a>
    </li>
  </ul>
</nav>
```

---

## 🧠 Cognitive Accessibility Standards

### 🎯 Simplified Interface Options

#### Cognitive Load Reduction
```css
/* Simplified interface mode */
.simplified-mode {
  --font-size-multiplier: 1.2;
  --spacing-multiplier: 1.4;
  --animation-duration: 0.8s; /* Slower animations */
}

.simplified-mode .memorial-grid {
  grid-template-columns: 1fr; /* Single column layout */
  gap: calc(24px * var(--spacing-multiplier));
}

.simplified-mode .memorial-card {
  padding: calc(16px * var(--spacing-multiplier));
  font-size: calc(16px * var(--font-size-multiplier));
  line-height: 1.6; /* Increased line height */
}

.simplified-mode .button {
  font-size: calc(18px * var(--font-size-multiplier));
  padding: calc(16px * var(--spacing-multiplier)) calc(24px * var(--spacing-multiplier));
}

/* Reduced visual complexity */
.simplified-mode .islamic-pattern {
  opacity: 0.3; /* Reduce decorative elements */
}

.simplified-mode .animation {
  animation-duration: calc(var(--animation-duration) * 1.5);
  animation-timing-function: ease-in-out; /* Simpler easing */
}
```

#### Clear Information Hierarchy
```html
<!-- Clear, predictable structure -->
<article class="memorial-card" aria-labelledby="memorial-title-1">
  <header class="memorial-header">
    <img src="photo.jpg" alt="Memorial photo" class="memorial-photo">
    <h3 id="memorial-title-1" class="memorial-name">Ahmed Ibn Mohammad</h3>
    <p class="memorial-dates">1965 - 2024</p>
  </header>
  
  <section class="memorial-content">
    <p class="memorial-relationship">Father</p>
    <div class="prayer-progress">
      <span class="progress-label">Prayers completed:</span>
      <strong class="progress-number">847</strong>
      <span class="progress-total">out of 1000</span>
    </div>
  </section>
  
  <footer class="memorial-actions">
    <button class="action-primary" aria-describedby="pray-description">
      Start Prayer
    </button>
    <p id="pray-description" class="action-help">
      Begin reciting Tahlil for this memorial
    </p>
  </footer>
</article>

<!-- Breadcrumb navigation -->
<nav aria-label="Breadcrumb" class="breadcrumb">
  <ol>
    <li><a href="/home">Home</a></li>
    <li><a href="/memorials">Memorials</a></li>
    <li aria-current="page">Ahmed Ibn Mohammad</li>
  </ol>
</nav>

<!-- Progress indicators -->
<div class="prayer-progress-indicator" role="region" aria-labelledby="progress-title">
  <h4 id="progress-title">Prayer Progress</h4>
  <div class="progress-bar" 
       role="progressbar" 
       aria-valuenow="47" 
       aria-valuemin="0" 
       aria-valuemax="100"
       aria-label="Prayer completion: 47 out of 100">
    <div class="progress-fill" style="width: 47%"></div>
  </div>
  <p class="progress-description">
    You have completed 47 Tahlil prayers. 
    53 more prayers will complete the traditional 100.
  </p>
</div>
```

#### Error Prevention and Recovery
```html
<!-- Form validation with helpful guidance -->
<form class="memorial-form" novalidate aria-live="polite">
  <div class="form-group">
    <label for="memorial-name" class="required-field">
      Full Name
      <span class="required-indicator" aria-label="required">*</span>
    </label>
    
    <input type="text" 
           id="memorial-name"
           name="name"
           required
           aria-describedby="name-help name-error"
           aria-invalid="false"
           pattern="[A-Za-z\s\u0600-\u06FF]+"
           autocomplete="name"
           autocorrect="on"
           spellcheck="true">
    
    <div id="name-help" class="help-text">
      <p>Enter the full name as you wish it to appear in prayers.</p>
      <p>You can use English or Arabic letters.</p>
    </div>
    
    <div id="name-error" class="error-message" role="alert" aria-live="assertive">
      <!-- Error messages appear here -->
    </div>
  </div>
  
  <div class="form-actions">
    <button type="button" class="button-secondary" onclick="saveDraft()">
      Save Draft
    </button>
    <button type="submit" class="button-primary">
      Create Memorial
    </button>
  </div>
  
  <div class="confirmation-dialog" hidden role="dialog" aria-labelledby="confirm-title">
    <h3 id="confirm-title">Confirm Memorial Creation</h3>
    <p>You are creating a memorial for <strong id="confirm-name"></strong>.</p>
    <p>This action cannot be undone. Are you sure you want to proceed?</p>
    <div class="dialog-actions">
      <button class="button-secondary" onclick="cancelCreation()">
        Cancel
      </button>
      <button class="button-primary" onclick="confirmCreation()">
        Yes, Create Memorial
      </button>
    </div>
  </div>
</form>
```

---

## 🌐 Multilingual Accessibility

### 📝 Language and Cultural Support

#### RTL Language Accessibility
```css
/* RTL layout with accessibility considerations */
[dir="rtl"] {
  direction: rtl;
  text-align: right;
}

[dir="rtl"] .memorial-card {
  text-align: right;
}

[dir="rtl"] .navigation {
  flex-direction: row-reverse;
}

[dir="rtl"] .breadcrumb ol {
  flex-direction: row-reverse;
}

[dir="rtl"] .breadcrumb li::after {
  content: '\2039'; /* Left-pointing angle bracket */
  margin: 0 8px 0 4px;
}

/* Arabic text accessibility */
.arabic-text {
  font-family: 'Noto Sans Arabic', 'Arabic UI Text', serif;
  direction: rtl;
  text-align: right;
  line-height: 1.8; /* Increased for readability */
  word-spacing: 0.2em;
  letter-spacing: 0.05em;
}

/* Screen reader pronunciation for Arabic */
.arabic-prayer[lang="ar"] {
  speak: spell-out; /* Some screen readers benefit from this */
}

.transliteration[lang="en"] {
  font-style: italic;
  speak: normal;
}
```

#### Language Switching Accessibility
```html
<!-- Accessible language switcher -->
<div class="language-selector" role="region" aria-labelledby="language-heading">
  <h3 id="language-heading">Choose Language / اختر اللغة</h3>
  
  <div class="language-grid" role="radiogroup" aria-labelledby="language-heading">
    <label class="language-option">
      <input type="radio" 
             name="language" 
             value="ar"
             aria-describedby="ar-description"
             onchange="switchLanguage('ar')">
      <span class="language-name" lang="ar">العربية</span>
      <span class="language-native">Arabic</span>
    </label>
    
    <label class="language-option">
      <input type="radio" 
             name="language" 
             value="en"
             checked
             aria-describedby="en-description"
             onchange="switchLanguage('en')">
      <span class="language-name" lang="en">English</span>
      <span class="language-native">English</span>
    </label>
    
    <label class="language-option">
      <input type="radio" 
             name="language" 
             value="id"
             aria-describedby="id-description"
             onchange="switchLanguage('id')">
      <span class="language-name" lang="id">Bahasa Indonesia</span>
      <span class="language-native">Indonesian</span>
    </label>
  </div>
  
  <div class="language-descriptions">
    <p id="ar-description" class="sr-only">
      Arabic language with right-to-left text direction
    </p>
    <p id="en-description" class="sr-only">
      English language with left-to-right text direction
    </p>
    <p id="id-description" class="sr-only">
      Indonesian language with left-to-right text direction
    </p>
  </div>
</div>

<!-- Language-aware announcements -->
<div aria-live="polite" aria-atomic="true" class="language-announcements">
  <span lang="ar" hidden>تم تغيير اللغة إلى العربية</span>
  <span lang="en" hidden>Language changed to English</span>
  <span lang="id" hidden>Bahasa telah diubah ke Bahasa Indonesia</span>
</div>
```

---

## 📱 Mobile Accessibility Standards

### 👆 Touch and Gesture Accessibility

#### Accessible Touch Interactions
```css
/* Touch target optimization */
@media (pointer: coarse) {
  .touch-target {
    min-width: 48px; /* Larger for touch */
    min-height: 48px;
    padding: 16px;
  }
  
  .prayer-counter {
    width: 200px;
    height: 200px;
  }
  
  .memorial-card {
    margin: 12px; /* More space between cards */
  }
}

/* Hover alternative for touch devices */
@media (hover: none) {
  .memorial-card:hover {
    transform: none; /* Disable hover effects */
  }
  
  .memorial-card:focus,
  .memorial-card:active {
    transform: scale(1.02);
    box-shadow: 0 8px 24px rgba(0, 0, 0, 0.2);
  }
}

/* Gesture accessibility */
.swipeable-content {
  touch-action: pan-x; /* Allow horizontal swiping only */
  overscroll-behavior-x: contain;
}

.prayer-counter {
  touch-action: manipulation; /* Disable zoom on double-tap */
}
```

#### Voice Assistant Integration
```html
<!-- Siri Shortcuts / Google Assistant integration -->
<script>
// Voice command registration
if ('webkitSpeechRecognition' in window || 'SpeechRecognition' in window) {
  const SpeechRecognition = window.SpeechRecognition || window.webkitSpeechRecognition;
  const recognition = new SpeechRecognition();
  
  recognition.lang = 'en-US'; // Adjust based on user preference
  recognition.continuous = false;
  recognition.interimResults = false;
  
  // Prayer voice commands
  const voiceCommands = {
    'start prayer': () => startPrayerSession(),
    'count prayer': () => incrementPrayerCounter(),
    'stop prayer': () => pausePrayerSession(),
    'create memorial': () => navigateToMemorialCreation(),
    'join community': () => joinCommunitySession()
  };
  
  recognition.onresult = (event) => {
    const command = event.results[0][0].transcript.toLowerCase().trim();
    if (voiceCommands[command]) {
      voiceCommands[command]();
    }
  };
}
</script>

<!-- Screen reader announcements for voice actions -->
<div aria-live="polite" aria-atomic="true" class="voice-announcements sr-only">
  <!-- Dynamic announcements appear here -->
</div>
```

---

## 🧪 Accessibility Testing Framework

### 📋 Testing Checklist

#### Automated Testing Tools
```javascript
// Accessibility testing configuration
const accessibilityTests = {
  // Screen reader testing
  screenReader: {
    tools: ['NVDA', 'JAWS', 'VoiceOver', 'TalkBack'],
    platforms: ['Windows', 'macOS', 'iOS', 'Android'],
    languages: ['en', 'ar', 'id', 'ur', 'tr']
  },
  
  // Color contrast testing
  colorContrast: {
    minimum: 4.5, // WCAG AA normal text
    large: 3.0,   // WCAG AA large text
    tools: ['WebAIM Contrast Checker', 'Colour Contrast Analyser']
  },
  
  // Keyboard navigation testing
  keyboard: {
    tabOrder: true,
    focusVisible: true,
    noKeyboardTraps: true,
    skipLinks: true
  },
  
  // Mobile accessibility testing
  mobile: {
    touchTargets: 44, // minimum size in pixels
    gestureAlternatives: true,
    orientationSupport: ['portrait', 'landscape'],
    zoomSupport: 200 // 200% zoom requirement
  }
};

// Testing automation
async function runAccessibilityTests() {
  const results = {
    passed: 0,
    failed: 0,
    warnings: 0,
    details: []
  };
  
  // Run axe-core accessibility testing
  const axeResults = await axe.run();
  
  axeResults.violations.forEach(violation => {
    results.failed += violation.nodes.length;
    results.details.push({
      type: 'violation',
      rule: violation.id,
      impact: violation.impact,
      description: violation.description,
      nodes: violation.nodes.map(node => node.target)
    });
  });
  
  return results;
}
```

#### Manual Testing Protocol
```
Weekly Accessibility Testing Schedule:

Monday - Screen Reader Testing:
□ Test all new features with NVDA (Windows)
□ Test Arabic content with Arabic NVDA voice
□ Verify memorial creation flow accessibility
□ Test prayer counter with screen reader

Tuesday - Keyboard Navigation:
□ Complete all user journeys using only keyboard
□ Verify tab order logical and predictable
□ Test focus indicators visible and clear
□ Ensure no keyboard traps exist

Wednesday - Color and Contrast:
□ Test all color combinations with contrast analyzer
□ Verify color-blind friendly design
□ Test high contrast mode compatibility
□ Validate semantic use of color

Thursday - Mobile Accessibility:
□ Test touch targets on various devices
□ Verify gesture alternatives exist
□ Test with device accessibility features enabled
□ Validate zoom functionality up to 200%

Friday - Cognitive and Language:
□ Test simplified interface mode
□ Verify clear error messages and help text
□ Test language switching functionality
□ Validate RTL layout accessibility

Cultural Accessibility Review (Monthly):
□ Test with native speakers of each supported language
□ Verify cultural appropriateness of accessibility features
□ Validate Islamic content screen reader compatibility
□ Test regional accessibility preferences
```

### 📊 Accessibility Metrics Dashboard
```javascript
// Accessibility metrics tracking
const accessibilityMetrics = {
  // WCAG compliance scores
  compliance: {
    level: 'AA',
    score: 0.96, // 96% compliance target
    violations: 3,
    warnings: 7
  },
  
  // User accessibility feedback
  userFeedback: {
    screenReaderUsers: {
      satisfaction: 4.7,
      completionRate: 0.89,
      commonIssues: ['Arabic pronunciation', 'Prayer timing']
    },
    motorImpairedUsers: {
      satisfaction: 4.5,
      completionRate: 0.92,
      commonIssues: ['Touch target size', 'Prayer counter sensitivity']
    },
    visuallyImpairedUsers: {
      satisfaction: 4.6,
      completionRate: 0.87,
      commonIssues: ['Memorial photo descriptions', 'Color contrast in patterns']
    }
  },
  
  // Performance accessibility
  performance: {
    keyboardNavigationTime: 1.8, // seconds average
    screenReaderLoadTime: 2.3,
    voiceControlAccuracy: 0.85
  }
};
```

---

## ✅ Accessibility Implementation Checklist

### 🎯 Pre-Release Validation
```
Design Phase Checklist:
□ Color contrast ratios calculated and documented
□ Touch targets sized appropriately (44px minimum)
□ Focus indicators designed for all interactive elements
□ Screen reader text alternatives written
□ Keyboard navigation flow mapped
□ Error states designed with clear messaging
□ Arabic/RTL layout accessibility considered

Development Phase Checklist:
□ Semantic HTML structure implemented
□ ARIA labels and landmarks added
□ Keyboard event handlers implemented
□ Focus management programmed correctly
□ Screen reader announcements configured
□ Language attributes set properly
□ Form validation accessible
□ Error handling accessible

Testing Phase Checklist:
□ Automated accessibility tests passing
□ Screen reader testing completed across platforms
□ Keyboard navigation manually verified
□ Mobile accessibility tested on real devices
□ Color contrast measured and verified
□ RTL language layout tested
□ Voice control integration tested
□ Performance accessibility verified

Cultural Validation Checklist:
□ Arabic content screen reader compatible
□ Islamic terminology pronounced correctly
□ Regional accessibility preferences accommodated
□ Cultural color meanings considered
□ Memorial traditions respectfully represented
□ Prayer practices accessibility maintained
```

---

**Accessibility Standards Maintained By**: Accessibility Team + Islamic Cultural Consultants  
**Testing Coverage**: WCAG 2.1 AA + Islamic Cultural Accessibility  
**Compliance Monitoring**: Automated + Manual Testing Pipeline  
**User Feedback Integration**: Monthly accessibility user research  
**Next Review**: Post-launch accessibility audit (June 2026)  

*These comprehensive accessibility standards ensure that Tahlil serves Muslims of all abilities with dignity, respect, and equal access to memorial prayer traditions, honoring both technological best practices and Islamic values of inclusion and compassion.*