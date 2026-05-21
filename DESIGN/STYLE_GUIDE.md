# 🎨 Tahlil Design Style Guide

## 📋 Style Guide Overview

**Project**: Tahlil - Global Memorial Prayer Platform  
**Version**: 1.0  
**Created**: May 20, 2026  
**Design Team**: Lead UI/UX + System Analyst  
**Implementation Ready**: Yes  

---

## 🎯 Design Philosophy

### 🌟 Core Design Principles

1. **Sacred Reverence**: Every element honors the spiritual nature of memorial prayers
2. **Photo-Centricity**: Memorial photos are the heart of every interaction
3. **Cultural Harmony**: Balances global appeal with local Islamic traditions
4. **Spiritual Accessibility**: Makes prayer accessible to Muslims worldwide
5. **Dignified Simplicity**: Clean, respectful design that focuses on the sacred

### 🕌 Islamic Design Values

- **Respect**: All design choices honor Islamic traditions and values
- **Unity**: Visual language connects global Muslim community
- **Authenticity**: Design reflects genuine Islamic spiritual practices
- **Modesty**: Elegant restraint in visual presentation
- **Contemplation**: Design encourages spiritual reflection and focus

---

## 🎨 Visual Identity System

### 📐 Logo Usage Guidelines

#### Primary Logo - Arabic Calligraphy
```
Usage: Primary brand representation
Context: App headers, marketing materials, official documents
Minimum Size: 32px height (digital), 12mm height (print)
Clear Space: 1x logo height on all sides

Color Variants:
├─ Primary Green (#1B5E20) on light backgrounds
├─ White (#FFFFFF) on dark backgrounds  
├─ Gold (#BF9000) for premium features
└─ Monochrome (#000000) for single-color applications

File Formats Available:
├─ .svg (primary - scalable vector)
├─ .png (transparent background)
├─ .jpg (solid background)
└─ .pdf (print applications)
```

#### Logo Variations by Context
```
App Icon: Simplified geometric version (512x512px)
Loading Screen: Animated calligraphy writing effect
Navigation: Compact horizontal version
Watermark: 30% opacity overlay version
Social Media: Square format with Islamic frame
Business Cards: Vertical stacked arrangement
```

### 🎨 Color Palette Specifications

#### Primary Color System
```css
/* Primary Colors */
--tahlil-primary: #1B5E20;           /* Deep Islamic Green */
--tahlil-primary-light: #4C8C4A;     /* Light Green */
--tahlil-primary-dark: #0D2F10;      /* Very Dark Green */

/* Secondary Colors */
--tahlil-secondary: #BF9000;         /* Elegant Gold */
--tahlil-secondary-light: #E6B033;   /* Light Gold */
--tahlil-secondary-dark: #8A6600;    /* Dark Gold */

/* Neutral Colors */
--tahlil-neutral-50: #FAFAFA;        /* Almost White */
--tahlil-neutral-100: #F5F5F5;       /* Very Light Gray */
--tahlil-neutral-200: #EEEEEE;       /* Light Gray */
--tahlil-neutral-300: #E0E0E0;       /* Medium Light Gray */
--tahlil-neutral-400: #BDBDBD;       /* Medium Gray */
--tahlil-neutral-500: #9E9E9E;       /* Gray */
--tahlil-neutral-600: #757575;       /* Dark Gray */
--tahlil-neutral-700: #616161;       /* Very Dark Gray */
--tahlil-neutral-800: #424242;       /* Almost Black */
--tahlil-neutral-900: #212121;       /* Black */

/* Semantic Colors */
--tahlil-success: #2E7D32;           /* Success Green */
--tahlil-warning: #F57C00;           /* Warning Orange */
--tahlil-error: #C62828;             /* Error Red */
--tahlil-info: #1565C0;              /* Info Blue */
```

#### Regional Color Adaptations
```css
/* Middle East Traditional */
--region-me-primary: #1B5E20;        /* Traditional Green */
--region-me-accent: #8D6E63;         /* Earth Brown */
--region-me-gold: #FFB300;           /* Desert Gold */

/* Southeast Asia */
--region-sea-primary: #2E7D32;       /* Emerald Green */
--region-sea-accent: #00695C;        /* Teal */
--region-sea-gold: #FFC107;          /* Tropical Gold */

/* South Asia */
--region-sa-primary: #4A148C;        /* Royal Purple */
--region-sa-accent: #6A1B9A;         /* Deep Purple */
--region-sa-gold: #FF8F00;           /* Saffron Gold */

/* Africa */
--region-af-primary: #BF360C;        /* Terracotta */
--region-af-accent: #5D4037;         /* Earth Brown */
--region-af-gold: #FF6F00;           /* Amber Gold */

/* Western Modern */
--region-west-primary: #263238;      /* Modern Charcoal */
--region-west-accent: #37474F;       /* Blue Gray */
--region-west-gold: #FFB74D;         /* Soft Gold */
```

### 📝 Typography System

#### Font Hierarchy
```css
/* Primary Font Stack */
font-family: 'Roboto', 'Noto Sans', system-ui, sans-serif;

/* Arabic Font Stack */
font-family: 'font_lpmq_isep_misbah', 'Arabic UI Text', 'Geeza Pro', serif;

/* Headings */
.heading-1 {
  font-size: 32px;
  line-height: 40px;
  font-weight: 700;
  letter-spacing: -0.5px;
}

.heading-2 {
  font-size: 28px;
  line-height: 36px;
  font-weight: 600;
  letter-spacing: -0.25px;
}

.heading-3 {
  font-size: 24px;
  line-height: 32px;
  font-weight: 600;
  letter-spacing: 0px;
}

.heading-4 {
  font-size: 20px;
  line-height: 28px;
  font-weight: 600;
  letter-spacing: 0px;
}

/* Body Text */
.body-large {
  font-size: 18px;
  line-height: 28px;
  font-weight: 400;
  letter-spacing: 0px;
}

.body-medium {
  font-size: 16px;
  line-height: 24px;
  font-weight: 400;
  letter-spacing: 0px;
}

.body-small {
  font-size: 14px;
  line-height: 20px;
  font-weight: 400;
  letter-spacing: 0px;
}

/* Arabic Text Styles */
.arabic-large {
  font-size: 24px;
  line-height: 36px;
  font-weight: 400;
  text-align: right;
  direction: rtl;
}

.arabic-medium {
  font-size: 18px;
  line-height: 28px;
  font-weight: 400;
  text-align: right;
  direction: rtl;
}

.arabic-small {
  font-size: 16px;
  line-height: 24px;
  font-weight: 400;
  text-align: right;
  direction: rtl;
}
```

#### Typography Usage Guidelines
```
Headings: Brand messaging, section titles, card headers
Body Text: Content, descriptions, instructions
Arabic Text: Prayers, Quranic verses, religious content
Caption: Metadata, timestamps, secondary information
Button Text: Action labels, navigation items
Input Labels: Form fields, search placeholders

Line Height Multiplier: 1.5x for readability
Character Limits:
- Headings: 60 characters maximum
- Body text: 75 characters per line
- Arabic text: 45 characters per line (RTL)
- Button labels: 25 characters maximum
```

---

## 📱 Component Visual Specifications

### 🎨 Button Design System

#### Primary Buttons
```css
.button-primary {
  background: var(--tahlil-primary);
  color: white;
  border-radius: 8px;
  padding: 12px 24px;
  font-weight: 600;
  font-size: 16px;
  min-height: 48px;
  min-width: 120px;
  box-shadow: 0px 2px 4px rgba(27, 94, 32, 0.2);
  transition: all 0.2s ease;
}

.button-primary:hover {
  background: var(--tahlil-primary-dark);
  transform: translateY(-1px);
  box-shadow: 0px 4px 8px rgba(27, 94, 32, 0.3);
}

.button-primary:active {
  transform: translateY(0px);
  box-shadow: 0px 1px 2px rgba(27, 94, 32, 0.4);
}
```

#### Secondary Buttons
```css
.button-secondary {
  background: transparent;
  color: var(--tahlil-primary);
  border: 2px solid var(--tahlil-primary);
  border-radius: 8px;
  padding: 10px 22px;
  font-weight: 600;
  font-size: 16px;
  min-height: 48px;
  min-width: 120px;
}

.button-secondary:hover {
  background: var(--tahlil-primary);
  color: white;
}
```

#### Floating Action Button
```css
.fab-primary {
  width: 56px;
  height: 56px;
  border-radius: 28px;
  background: var(--tahlil-secondary);
  color: white;
  box-shadow: 0px 4px 12px rgba(191, 144, 0, 0.4);
  display: flex;
  align-items: center;
  justify-content: center;
}
```

### 📄 Card Design Specifications

#### Memorial Photo Card
```css
.memorial-card {
  width: 280px;
  height: 320px;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0px 8px 24px rgba(0, 0, 0, 0.12);
  background: white;
  position: relative;
}

.memorial-photo {
  width: 100%;
  height: 240px;
  object-fit: cover;
  position: relative;
}

.memorial-overlay {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  height: 120px;
  background: linear-gradient(transparent, rgba(0, 0, 0, 0.7));
  display: flex;
  align-items: flex-end;
  padding: 16px;
}

.memorial-info {
  color: white;
  text-align: center;
  width: 100%;
}

.memorial-name {
  font-size: 18px;
  font-weight: 600;
  margin-bottom: 4px;
}

.memorial-dates {
  font-size: 14px;
  opacity: 0.9;
}
```

#### Prayer Counter Card
```css
.prayer-counter {
  width: 200px;
  height: 200px;
  border-radius: 100px;
  background: white;
  box-shadow: 0px 8px 32px rgba(27, 94, 32, 0.15);
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  cursor: pointer;
}

.counter-progress {
  position: absolute;
  width: 180px;
  height: 180px;
  border-radius: 90px;
  border: 8px solid var(--tahlil-neutral-200);
  border-top: 8px solid var(--tahlil-primary);
  transform: rotate(-90deg);
}

.counter-number {
  font-size: 48px;
  font-weight: 700;
  color: var(--tahlil-primary);
  z-index: 1;
}
```

### 🔍 Input Field Design

#### Text Input Fields
```css
.input-field {
  width: 100%;
  padding: 16px;
  border: 2px solid var(--tahlil-neutral-300);
  border-radius: 8px;
  font-size: 16px;
  background: white;
  transition: all 0.2s ease;
}

.input-field:focus {
  border-color: var(--tahlil-primary);
  outline: none;
  box-shadow: 0px 0px 0px 3px rgba(27, 94, 32, 0.1);
}

.input-label {
  font-size: 14px;
  font-weight: 600;
  color: var(--tahlil-neutral-700);
  margin-bottom: 8px;
  display: block;
}

.input-error {
  border-color: var(--tahlil-error);
  background: rgba(198, 40, 40, 0.05);
}

.input-error-message {
  color: var(--tahlil-error);
  font-size: 12px;
  margin-top: 4px;
}
```

---

## 📐 Layout and Spacing Guidelines

### 🔲 Grid System Specifications

#### Mobile Grid (320px - 768px)
```
Container Padding: 16px
Column Gutters: 16px
Margins: 16px left/right
Max Content Width: 100% - 32px

Breakpoints:
- Small: 320px - 480px (1 column)
- Medium: 481px - 768px (2 columns)
```

#### Desktop Grid (768px+)
```
Container Padding: 24px
Column Gutters: 24px
Margins: 24px left/right
Max Content Width: 1200px

Breakpoints:
- Large: 769px - 1024px (3 columns)
- XLarge: 1025px+ (4 columns)
```

### 📏 Spacing Scale
```css
/* Base spacing unit: 8px */
--space-xs: 4px;    /* 0.5 units */
--space-sm: 8px;    /* 1 unit */
--space-md: 16px;   /* 2 units */
--space-lg: 24px;   /* 3 units */
--space-xl: 32px;   /* 4 units */
--space-2xl: 48px;  /* 6 units */
--space-3xl: 64px;  /* 8 units */

/* Component spacing */
--spacing-card-padding: var(--space-md);
--spacing-section-gap: var(--space-xl);
--spacing-component-gap: var(--space-sm);
--spacing-screen-padding: var(--space-lg);
```

### 🎭 Elevation and Shadows

#### Shadow Specifications
```css
/* Elevation levels */
--shadow-1: 0px 1px 2px rgba(0, 0, 0, 0.08);
--shadow-2: 0px 2px 4px rgba(0, 0, 0, 0.08);
--shadow-3: 0px 4px 8px rgba(0, 0, 0, 0.08);
--shadow-4: 0px 8px 16px rgba(0, 0, 0, 0.12);
--shadow-5: 0px 16px 32px rgba(0, 0, 0, 0.16);

/* Component-specific shadows */
.card-shadow { box-shadow: var(--shadow-2); }
.modal-shadow { box-shadow: var(--shadow-5); }
.button-shadow { box-shadow: var(--shadow-1); }
.floating-shadow { box-shadow: var(--shadow-4); }
```

---

## 🎬 Animation and Interaction Guidelines

### ⚡ Animation Specifications

#### Transition Timings
```css
/* Duration */
--duration-fast: 150ms;
--duration-normal: 250ms;
--duration-slow: 400ms;

/* Easing curves */
--ease-standard: cubic-bezier(0.4, 0.0, 0.2, 1);
--ease-decelerate: cubic-bezier(0.0, 0.0, 0.2, 1);
--ease-accelerate: cubic-bezier(0.4, 0.0, 1, 1);
```

#### Common Animations
```css
/* Fade in */
@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

/* Slide up */
@keyframes slideUp {
  from { 
    opacity: 0;
    transform: translateY(20px);
  }
  to { 
    opacity: 1;
    transform: translateY(0);
  }
}

/* Scale tap */
@keyframes scaleTap {
  0% { transform: scale(1); }
  50% { transform: scale(0.95); }
  100% { transform: scale(1); }
}

/* Prayer counter pulse */
@keyframes prayerPulse {
  0% { transform: scale(1); box-shadow: 0 0 0 0 rgba(27, 94, 32, 0.4); }
  70% { transform: scale(1.05); box-shadow: 0 0 0 10px rgba(27, 94, 32, 0); }
  100% { transform: scale(1); box-shadow: 0 0 0 0 rgba(27, 94, 32, 0); }
}
```

### 🔄 Interaction States

#### Button Interaction States
```css
/* Default state */
.interactive-default {
  transition: all var(--duration-fast) var(--ease-standard);
}

/* Hover state */
.interactive-hover {
  transform: translateY(-1px);
  box-shadow: var(--shadow-3);
}

/* Active/pressed state */
.interactive-active {
  transform: translateY(0px);
  box-shadow: var(--shadow-1);
}

/* Focus state (accessibility) */
.interactive-focus {
  outline: 3px solid rgba(27, 94, 32, 0.3);
  outline-offset: 2px;
}

/* Disabled state */
.interactive-disabled {
  opacity: 0.5;
  cursor: not-allowed;
  pointer-events: none;
}
```

---

## 🌐 Responsive Design Guidelines

### 📱 Mobile-First Approach

#### Viewport Breakpoints
```css
/* Mobile First Media Queries */
/* Small phones: 320px - 480px */
@media (min-width: 320px) {
  .container { padding: 16px; }
  .grid-cols { grid-template-columns: 1fr; }
}

/* Large phones: 481px - 768px */
@media (min-width: 481px) {
  .container { padding: 20px; }
  .grid-cols { grid-template-columns: 1fr 1fr; }
}

/* Tablets: 769px - 1024px */
@media (min-width: 769px) {
  .container { padding: 24px; }
  .grid-cols { grid-template-columns: repeat(3, 1fr); }
}

/* Desktop: 1025px+ */
@media (min-width: 1025px) {
  .container { 
    padding: 32px;
    max-width: 1200px;
    margin: 0 auto;
  }
  .grid-cols { grid-template-columns: repeat(4, 1fr); }
}
```

#### Component Responsive Behavior
```css
/* Memorial Card Responsive */
.memorial-card {
  width: 100%;
  max-width: 280px;
  margin: 0 auto;
}

@media (min-width: 481px) {
  .memorial-card {
    width: calc(50% - 8px);
    margin: 0;
  }
}

@media (min-width: 769px) {
  .memorial-card {
    width: calc(33.333% - 11px);
  }
}

/* Prayer Counter Responsive */
.prayer-counter {
  width: 160px;
  height: 160px;
}

@media (min-width: 481px) {
  .prayer-counter {
    width: 200px;
    height: 200px;
  }
}
```

---

## ♿ Accessibility Design Standards

### 🎯 Accessibility Requirements

#### Color Contrast Standards
```
WCAG AA Compliance:
- Normal text: 4.5:1 contrast ratio minimum
- Large text (18px+): 3:1 contrast ratio minimum
- Interactive elements: 3:1 contrast ratio minimum

Color-blind Friendly:
- Never rely solely on color to convey information
- Use icons, patterns, or text labels alongside color
- Test with deuteranopia and protanopia simulators
```

#### Focus Management
```css
/* Focus indicators */
.focus-visible {
  outline: 3px solid var(--tahlil-primary);
  outline-offset: 2px;
  border-radius: 4px;
}

/* Skip navigation links */
.skip-nav {
  position: absolute;
  top: -100px;
  left: 16px;
  background: var(--tahlil-primary);
  color: white;
  padding: 8px 16px;
  border-radius: 4px;
  text-decoration: none;
  z-index: 1000;
}

.skip-nav:focus {
  top: 16px;
}
```

#### Touch Target Sizing
```css
/* Minimum touch targets: 44px x 44px */
.touch-target {
  min-height: 44px;
  min-width: 44px;
  padding: 12px;
  cursor: pointer;
}

/* Spacing between touch targets: 8px minimum */
.touch-spacing {
  margin: 4px;
}
```

### 🔊 Screen Reader Support

#### Semantic HTML Structure
```html
<!-- Proper heading hierarchy -->
<h1>Tahlil - Memorial Prayers</h1>
<h2>Recent Memorials</h2>
<h3>Ahmed Ibn Mohammad</h3>

<!-- Accessible form labels -->
<label for="memorial-name">Memorial Name</label>
<input id="memorial-name" type="text" required>

<!-- Descriptive button text -->
<button aria-label="Start prayer for Ahmed Ibn Mohammad">
  Begin Prayer
</button>

<!-- Image alt text -->
<img src="memorial-photo.jpg" 
     alt="Memorial photo of Ahmed Ibn Mohammad, peaceful expression">
```

#### ARIA Labels and Descriptions
```html
<!-- Prayer counter with screen reader support -->
<div class="prayer-counter" 
     role="button"
     tabindex="0"
     aria-label="Prayer counter, current count 45 out of 100"
     aria-describedby="counter-instructions">
  <span aria-hidden="true">45</span>
</div>
<div id="counter-instructions" class="sr-only">
  Tap to increment prayer count. Target is 100 prayers.
</div>

<!-- Progress indicator -->
<div role="progressbar" 
     aria-valuenow="45" 
     aria-valuemin="0" 
     aria-valuemax="100"
     aria-label="Prayer progress: 45 of 100 completed">
</div>
```

---

## 🔍 Design Quality Checklist

### ✅ Visual Design Review
```
□ Logo usage follows brand guidelines
□ Color palette maintains WCAG AA contrast ratios  
□ Typography hierarchy is consistent and readable
□ Spacing follows 8px base unit system
□ Component designs match specifications
□ Responsive behavior tested on all breakpoints
□ Cultural adaptations implemented correctly
□ Islamic authenticity maintained throughout
□ Photo-centric design philosophy evident
□ Animation timings feel natural and purposeful
```

### 📱 Technical Implementation Check
```
□ CSS follows naming conventions
□ Component architecture is modular
□ RTL layout support implemented
□ Accessibility features fully functional
□ Performance optimized (animations, images)
□ Cross-browser compatibility verified
□ Mobile-first responsive design working
□ Dark mode variants available
□ Touch interactions properly sized
□ Loading states designed and implemented
```

### 🌍 Cultural Validation
```
□ Islamic scholars approved visual elements
□ Regional adaptations respect local customs
□ Arabic text properly formatted and accurate
□ Prayer content theologically sound
□ Cultural sensitivity maintained across regions
□ No inappropriate imagery or symbols
□ Memorial traditions honored appropriately
□ Community features culturally appropriate
□ Social sharing maintains spiritual dignity
□ Global accessibility achieved without diluting authenticity
```

---

**Style Guide Maintained By**: Lead UI/UX Designer + Design Team  
**Implementation Support**: System Analyst + Development Team  
**Version Control**: Updated with each design iteration  
**Next Review**: After user testing completion (May 29, 2026)  

*This comprehensive style guide ensures consistent, accessible, and culturally-appropriate implementation of the Tahlil design system across all platforms and regions.*