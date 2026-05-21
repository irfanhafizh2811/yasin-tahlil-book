# 📐 Tahlil Responsive Layout Guidelines

## 📋 Responsive Design Overview

**Project**: Tahlil - Global Memorial Prayer Platform  
**Design System**: Mobile-First Responsive Framework  
**Created**: May 20, 2026  
**Breakpoints**: Material Design 3 + Islamic Cultural Adaptations  
**Testing Devices**: 15+ device configurations across regions  

---

## 📱 Device & Breakpoint Strategy

### 🎯 Primary Device Targets

#### Mobile Devices (Primary Focus)
```
Small Phones (320px - 480px):
├─ iPhone SE (375 x 667px) - 2nd Gen
├─ Samsung Galaxy A12 (360 x 800px)
├─ Xiaomi Redmi 9A (360 x 640px)
└─ Budget Android devices popular in developing regions

Large Phones (481px - 768px):
├─ iPhone 14 Pro (393 x 852px)
├─ Samsung Galaxy S23 (384 x 854px)
├─ Google Pixel 7 (412 x 915px)
└─ OnePlus 11 (412 x 919px)

Tablets (769px - 1024px):
├─ iPad Mini (768 x 1024px)
├─ Samsung Galaxy Tab A8 (800 x 1280px)
├─ Amazon Fire HD 10 (800 x 1280px)
└─ Budget tablets for family sharing

Desktop/Web (1025px+):
├─ Laptop (1366 x 768px) - Most common
├─ Desktop HD (1920 x 1080px)
├─ Ultrawide (2560 x 1440px)
└─ 4K displays (3840 x 2160px)
```

### 📏 Responsive Breakpoint System
```css
/* Mobile-First Breakpoint Strategy */

/* Extra Small: Budget phones and older devices */
@media (min-width: 320px) {
  .container { 
    padding: 12px;
    max-width: 100%;
  }
  .memorial-grid { grid-template-columns: 1fr; }
  .prayer-counter { width: 140px; height: 140px; }
  .text-size { font-size: 14px; }
}

/* Small: Standard smartphones */
@media (min-width: 375px) {
  .container { 
    padding: 16px;
    max-width: 100%;
  }
  .memorial-grid { grid-template-columns: 1fr 1fr; }
  .prayer-counter { width: 160px; height: 160px; }
  .text-size { font-size: 16px; }
}

/* Medium: Large phones and small tablets */
@media (min-width: 481px) {
  .container { 
    padding: 20px;
    max-width: 100%;
  }
  .memorial-grid { grid-template-columns: repeat(2, 1fr); }
  .prayer-counter { width: 180px; height: 180px; }
  .sidebar { display: none; } /* Still single column */
}

/* Large: Tablets in portrait */
@media (min-width: 768px) {
  .container { 
    padding: 24px;
    max-width: 1200px;
    margin: 0 auto;
  }
  .memorial-grid { grid-template-columns: repeat(3, 1fr); }
  .prayer-counter { width: 200px; height: 200px; }
  .sidebar { display: block; width: 280px; }
  .main-content { margin-left: 300px; }
}

/* Extra Large: Tablets in landscape and small desktops */
@media (min-width: 1024px) {
  .container { 
    padding: 32px;
    max-width: 1200px;
  }
  .memorial-grid { grid-template-columns: repeat(4, 1fr); }
  .prayer-counter { width: 240px; height: 240px; }
  .text-size { font-size: 18px; }
}

/* XXL: Large desktops and ultrawide */
@media (min-width: 1440px) {
  .container { 
    max-width: 1400px;
    padding: 40px;
  }
  .memorial-grid { grid-template-columns: repeat(5, 1fr); }
  .sidebar { width: 320px; }
  .main-content { margin-left: 340px; }
}
```

---

## 📱 Component Responsive Behavior

### 🖼️ Memorial Photo Cards
```css
/* Memorial card responsive scaling */
.memorial-card {
  width: 100%;
  aspect-ratio: 3/4;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.12);
  transition: all 0.3s ease;
}

/* Small phones: Single column, compact cards */
@media (max-width: 480px) {
  .memorial-card {
    margin-bottom: 16px;
    max-width: 280px;
    margin-left: auto;
    margin-right: auto;
  }
  
  .memorial-info {
    padding: 12px;
    font-size: 14px;
  }
  
  .memorial-name {
    font-size: 16px;
    line-height: 1.3;
  }
}

/* Standard phones: Two column grid */
@media (min-width: 481px) and (max-width: 767px) {
  .memorial-grid {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: 12px;
    padding: 16px;
  }
  
  .memorial-card {
    margin-bottom: 0;
  }
}

/* Tablets: Three column grid with more spacing */
@media (min-width: 768px) and (max-width: 1023px) {
  .memorial-grid {
    grid-template-columns: repeat(3, 1fr);
    gap: 20px;
    padding: 24px;
  }
  
  .memorial-card:hover {
    transform: translateY(-4px);
    box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
  }
}

/* Desktop: Four+ columns with enhanced interactions */
@media (min-width: 1024px) {
  .memorial-grid {
    grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
    gap: 24px;
    padding: 32px;
  }
  
  .memorial-card {
    transition: all 0.3s cubic-bezier(0.25, 0.46, 0.45, 0.94);
  }
  
  .memorial-card:hover {
    transform: translateY(-6px) scale(1.02);
    box-shadow: 0 12px 32px rgba(0, 0, 0, 0.2);
  }
}
```

### 📿 Prayer Counter Responsive Design
```css
/* Prayer counter scaling system */
.prayer-counter-container {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 20px;
  min-height: 200px;
}

.prayer-counter {
  border-radius: 50%;
  background: white;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  box-shadow: 0 8px 32px rgba(27, 94, 32, 0.15);
  cursor: pointer;
  transition: all 0.3s ease;
}

/* Small phones: Compact counter for thumb-friendly tapping */
@media (max-width: 480px) {
  .prayer-counter {
    width: 140px;
    height: 140px;
    min-width: 140px;
    min-height: 140px;
  }
  
  .counter-number {
    font-size: 32px;
    font-weight: 700;
  }
  
  .counter-progress-ring {
    width: 120px;
    height: 120px;
    stroke-width: 6px;
  }
  
  .prayer-counter:active {
    transform: scale(0.95);
  }
}

/* Standard phones: Standard size for comfortable use */
@media (min-width: 481px) and (max-width: 767px) {
  .prayer-counter {
    width: 160px;
    height: 160px;
  }
  
  .counter-number {
    font-size: 40px;
    font-weight: 700;
  }
  
  .counter-progress-ring {
    width: 140px;
    height: 140px;
    stroke-width: 8px;
  }
}

/* Tablets: Larger counter with enhanced visuals */
@media (min-width: 768px) and (max-width: 1023px) {
  .prayer-counter {
    width: 200px;
    height: 200px;
  }
  
  .counter-number {
    font-size: 48px;
    font-weight: 700;
  }
  
  .counter-progress-ring {
    width: 180px;
    height: 180px;
    stroke-width: 10px;
  }
  
  .prayer-counter:hover {
    transform: scale(1.05);
    box-shadow: 0 12px 40px rgba(27, 94, 32, 0.25);
  }
}

/* Desktop: Maximum size with full interaction effects */
@media (min-width: 1024px) {
  .prayer-counter {
    width: 240px;
    height: 240px;
  }
  
  .counter-number {
    font-size: 56px;
    font-weight: 700;
  }
  
  .counter-progress-ring {
    width: 220px;
    height: 220px;
    stroke-width: 12px;
  }
  
  .prayer-counter:hover {
    transform: scale(1.08);
    box-shadow: 0 16px 48px rgba(27, 94, 32, 0.3);
  }
  
  .prayer-counter:active {
    transform: scale(1.03);
  }
}
```

### 📄 Navigation Responsive Patterns
```css
/* Bottom navigation for mobile, sidebar for desktop */
.navigation-container {
  position: relative;
}

/* Mobile: Bottom tab navigation */
@media (max-width: 767px) {
  .bottom-navigation {
    position: fixed;
    bottom: 0;
    left: 0;
    right: 0;
    height: 64px;
    background: white;
    border-top: 1px solid #E0E0E0;
    display: flex;
    justify-content: space-around;
    align-items: center;
    z-index: 100;
    box-shadow: 0 -4px 16px rgba(0, 0, 0, 0.1);
  }
  
  .nav-item {
    display: flex;
    flex-direction: column;
    align-items: center;
    padding: 4px 8px;
    min-width: 56px;
    border-radius: 8px;
    transition: all 0.2s ease;
  }
  
  .nav-icon {
    width: 24px;
    height: 24px;
    margin-bottom: 2px;
  }
  
  .nav-label {
    font-size: 10px;
    font-weight: 500;
    line-height: 1;
  }
  
  .sidebar {
    display: none;
  }
  
  .main-content {
    margin-bottom: 80px; /* Space for bottom nav */
  }
}

/* Tablet: Top navigation bar */
@media (min-width: 768px) and (max-width: 1023px) {
  .bottom-navigation {
    display: none;
  }
  
  .top-navigation {
    position: sticky;
    top: 0;
    height: 72px;
    background: white;
    border-bottom: 1px solid #E0E0E0;
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 0 24px;
    z-index: 100;
  }
  
  .nav-tabs {
    display: flex;
    gap: 32px;
  }
  
  .nav-tab {
    padding: 12px 16px;
    border-radius: 8px;
    font-weight: 500;
    transition: all 0.2s ease;
  }
  
  .sidebar {
    display: none;
  }
}

/* Desktop: Sidebar navigation */
@media (min-width: 1024px) {
  .bottom-navigation,
  .top-navigation {
    display: none;
  }
  
  .sidebar {
    position: fixed;
    left: 0;
    top: 0;
    width: 280px;
    height: 100vh;
    background: white;
    border-right: 1px solid #E0E0E0;
    display: flex;
    flex-direction: column;
    padding: 24px 0;
    z-index: 100;
    overflow-y: auto;
  }
  
  .sidebar-logo {
    padding: 0 24px 32px;
    border-bottom: 1px solid #F5F5F5;
    margin-bottom: 32px;
  }
  
  .sidebar-nav {
    flex: 1;
    padding: 0 12px;
  }
  
  .sidebar-nav-item {
    display: flex;
    align-items: center;
    padding: 12px 16px;
    margin-bottom: 4px;
    border-radius: 12px;
    font-weight: 500;
    color: #616161;
    transition: all 0.2s ease;
  }
  
  .sidebar-nav-item:hover {
    background: #F5F5F5;
    color: #1B5E20;
  }
  
  .sidebar-nav-item.active {
    background: #E8F5E8;
    color: #1B5E20;
  }
  
  .main-content {
    margin-left: 300px;
    min-height: 100vh;
  }
}
```

---

## 🌍 Cultural Layout Adaptations

### 🔄 RTL Language Support
```css
/* RTL layout support for Arabic, Urdu, Persian */
[dir="rtl"] .memorial-grid {
  direction: rtl;
}

[dir="rtl"] .memorial-card {
  text-align: right;
}

[dir="rtl"] .navigation-container {
  direction: rtl;
}

[dir="rtl"] .sidebar {
  left: auto;
  right: 0;
  border-left: 1px solid #E0E0E0;
  border-right: none;
}

[dir="rtl"] .main-content {
  margin-left: 0;
  margin-right: 300px;
}

[dir="rtl"] .arabic-text {
  text-align: right;
  direction: rtl;
  font-family: 'Noto Sans Arabic', 'Arabic UI Text', serif;
}

/* RTL responsive adjustments */
@media (max-width: 767px) {
  [dir="rtl"] .bottom-navigation {
    direction: rtl;
  }
  
  [dir="rtl"] .nav-item {
    direction: rtl;
  }
}

@media (min-width: 1024px) {
  [dir="rtl"] .sidebar {
    right: 0;
    left: auto;
  }
  
  [dir="rtl"] .main-content {
    margin-right: 300px;
    margin-left: 0;
  }
}
```

### 🌏 Regional Layout Preferences
```css
/* Middle East: Traditional spacing and proportions */
.layout-middle-east {
  --base-spacing: 16px;
  --card-ratio: 3/4;
  --text-scale: 1.0;
}

@media (min-width: 768px) {
  .layout-middle-east {
    --base-spacing: 24px;
    --card-ratio: 4/5;
  }
}

/* Southeast Asia: Flowing, organic spacing */
.layout-southeast-asia {
  --base-spacing: 20px;
  --card-ratio: 2/3;
  --text-scale: 1.1;
}

.layout-southeast-asia .memorial-card {
  border-radius: 16px;
}

/* South Asia: Rich, detailed layouts */
.layout-south-asia {
  --base-spacing: 18px;
  --card-ratio: 3/4;
  --text-scale: 1.05;
}

.layout-south-asia .memorial-card {
  border: 2px solid #F5F5F5;
}

/* Africa: Warm, community-focused layouts */
.layout-africa {
  --base-spacing: 22px;
  --card-ratio: 1/1;
  --text-scale: 1.15;
}

.layout-africa .memorial-grid {
  gap: calc(var(--base-spacing) * 1.2);
}

/* Western: Clean, minimalist spacing */
.layout-western {
  --base-spacing: 24px;
  --card-ratio: 3/4;
  --text-scale: 1.0;
}

.layout-western .memorial-card {
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}
```

---

## 📏 Container & Grid Systems

### 📦 Container Specifications
```css
/* Base container system */
.container {
  width: 100%;
  margin-left: auto;
  margin-right: auto;
  padding-left: var(--container-padding);
  padding-right: var(--container-padding);
}

/* Container padding responsive scale */
:root {
  --container-padding: 16px;
}

@media (min-width: 481px) {
  :root { --container-padding: 20px; }
}

@media (min-width: 768px) {
  :root { --container-padding: 24px; }
  .container { max-width: 1200px; }
}

@media (min-width: 1024px) {
  :root { --container-padding: 32px; }
  .container { max-width: 1200px; }
}

@media (min-width: 1440px) {
  :root { --container-padding: 40px; }
  .container { max-width: 1400px; }
}

/* Fluid container for full-width sections */
.container-fluid {
  width: 100%;
  padding-left: var(--container-padding);
  padding-right: var(--container-padding);
}

/* Section containers with consistent spacing */
.section {
  padding-top: var(--section-padding);
  padding-bottom: var(--section-padding);
}

:root {
  --section-padding: 24px;
}

@media (min-width: 768px) {
  :root { --section-padding: 48px; }
}

@media (min-width: 1024px) {
  :root { --section-padding: 64px; }
}
```

### 🔲 Grid System Implementation
```css
/* Flexible grid system */
.grid {
  display: grid;
  gap: var(--grid-gap);
  width: 100%;
}

:root {
  --grid-gap: 16px;
}

@media (min-width: 768px) {
  :root { --grid-gap: 20px; }
}

@media (min-width: 1024px) {
  :root { --grid-gap: 24px; }
}

/* Grid column utilities */
.grid-1 { grid-template-columns: 1fr; }
.grid-2 { grid-template-columns: repeat(2, 1fr); }
.grid-3 { grid-template-columns: repeat(3, 1fr); }
.grid-4 { grid-template-columns: repeat(4, 1fr); }
.grid-auto { grid-template-columns: repeat(auto-fit, minmax(280px, 1fr)); }

/* Responsive grid classes */
@media (max-width: 480px) {
  .grid-responsive {
    grid-template-columns: 1fr;
  }
}

@media (min-width: 481px) and (max-width: 767px) {
  .grid-responsive {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (min-width: 768px) and (max-width: 1023px) {
  .grid-responsive {
    grid-template-columns: repeat(3, 1fr);
  }
}

@media (min-width: 1024px) {
  .grid-responsive {
    grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  }
}
```

---

## 📐 Typography Responsive Scaling

### 📝 Text Size Responsive System
```css
/* Fluid typography system */
:root {
  /* Base font sizes */
  --text-xs: clamp(10px, 2.5vw, 12px);
  --text-sm: clamp(12px, 3vw, 14px);
  --text-base: clamp(14px, 3.5vw, 16px);
  --text-lg: clamp(16px, 4vw, 18px);
  --text-xl: clamp(18px, 4.5vw, 20px);
  --text-2xl: clamp(20px, 5vw, 24px);
  --text-3xl: clamp(24px, 6vw, 32px);
  --text-4xl: clamp(28px, 7vw, 40px);
  
  /* Arabic text scaling */
  --arabic-base: clamp(16px, 4vw, 18px);
  --arabic-lg: clamp(20px, 5vw, 24px);
  --arabic-xl: clamp(24px, 6vw, 28px);
  
  /* Line heights */
  --line-height-tight: 1.2;
  --line-height-normal: 1.5;
  --line-height-relaxed: 1.6;
}

/* Typography classes */
.text-xs { font-size: var(--text-xs); }
.text-sm { font-size: var(--text-sm); }
.text-base { font-size: var(--text-base); }
.text-lg { font-size: var(--text-lg); }
.text-xl { font-size: var(--text-xl); }
.text-2xl { font-size: var(--text-2xl); }
.text-3xl { font-size: var(--text-3xl); }
.text-4xl { font-size: var(--text-4xl); }

/* Arabic typography responsive */
.arabic-text {
  font-size: var(--arabic-base);
  line-height: var(--line-height-relaxed);
  font-family: 'Noto Sans Arabic', serif;
}

.arabic-lg {
  font-size: var(--arabic-lg);
}

.arabic-xl {
  font-size: var(--arabic-xl);
}

/* Responsive typography adjustments */
@media (max-width: 480px) {
  .memorial-name {
    font-size: var(--text-lg);
    line-height: var(--line-height-tight);
  }
  
  .memorial-dates {
    font-size: var(--text-sm);
  }
  
  .prayer-instruction {
    font-size: var(--text-base);
    line-height: var(--line-height-normal);
  }
}

@media (min-width: 768px) {
  .memorial-name {
    font-size: var(--text-xl);
  }
  
  .prayer-instruction {
    font-size: var(--text-lg);
    line-height: var(--line-height-relaxed);
  }
}
```

---

## 🖼️ Image & Media Responsive Handling

### 📸 Responsive Image System
```css
/* Base responsive image setup */
.responsive-image {
  width: 100%;
  height: auto;
  object-fit: cover;
  border-radius: 8px;
}

/* Memorial photo responsive behavior */
.memorial-photo {
  width: 100%;
  aspect-ratio: 3/4;
  object-fit: cover;
  border-radius: 12px;
}

/* Responsive srcset implementation */
.memorial-photo-responsive {
  width: 100%;
  height: auto;
}

/* Image optimization for different screen densities */
@media (-webkit-min-device-pixel-ratio: 2), 
       (min-resolution: 192dpi) {
  .memorial-photo {
    image-rendering: -webkit-optimize-contrast;
    image-rendering: crisp-edges;
  }
}

/* Progressive enhancement for WebP support */
@supports (background-image: url('test.webp')) {
  .memorial-photo-webp {
    background-size: cover;
    background-position: center;
    background-repeat: no-repeat;
  }
}

/* Lazy loading placeholders */
.image-placeholder {
  background: linear-gradient(135deg, #F5F5F5, #E0E0E0);
  border-radius: 12px;
  position: relative;
  overflow: hidden;
}

.image-placeholder::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255,255,255,0.4), transparent);
  animation: shimmer 1.5s ease-in-out infinite;
}

@keyframes shimmer {
  0% { left: -100%; }
  100% { left: 100%; }
}
```

---

## ⚡ Performance Optimization

### 🚀 Responsive Performance Guidelines
```css
/* Critical CSS for above-the-fold content */
.critical-layout {
  contain: layout style;
  will-change: auto;
}

/* Efficient grid rendering */
.memorial-grid {
  contain: layout;
  transform: translateZ(0); /* Force GPU acceleration */
}

/* Optimize scrolling performance */
.scroll-container {
  -webkit-overflow-scrolling: touch;
  scroll-behavior: smooth;
}

/* Reduce repaints during animations */
.animated-element {
  will-change: transform, opacity;
  backface-visibility: hidden;
}

/* Memory-efficient responsive images */
.efficient-image {
  content-visibility: auto;
  contain-intrinsic-size: 280px 373px;
}

/* Intersection observer for lazy loading */
.lazy-load {
  opacity: 0;
  transition: opacity 0.3s ease;
}

.lazy-load.loaded {
  opacity: 1;
}
```

### 📊 Performance Budgets by Device
```javascript
// Performance targets by device category
const performanceBudgets = {
  'mobile-budget': {
    maxBundleSize: '150KB',
    maxImageSize: '50KB',
    maxFonts: 2,
    criticalCSS: '14KB'
  },
  'mobile-standard': {
    maxBundleSize: '200KB',
    maxImageSize: '75KB',
    maxFonts: 3,
    criticalCSS: '18KB'
  },
  'tablet': {
    maxBundleSize: '300KB',
    maxImageSize: '100KB',
    maxFonts: 4,
    criticalCSS: '24KB'
  },
  'desktop': {
    maxBundleSize: '500KB',
    maxImageSize: '150KB',
    maxFonts: 6,
    criticalCSS: '32KB'
  }
};
```

---

## ✅ Responsive Design Checklist

### 📱 Device Testing Requirements
```
□ iPhone SE (375x667) - Budget smartphone testing
□ iPhone 14 (393x852) - Standard smartphone testing  
□ Samsung Galaxy A12 (360x800) - Android budget testing
□ iPad Mini (768x1024) - Tablet portrait testing
□ iPad Pro (1024x1366) - Large tablet testing
□ MacBook Air (1366x768) - Laptop testing
□ Desktop 1080p (1920x1080) - Desktop testing
□ 4K Display (3840x2160) - High-DPI testing
```

### 🌍 Cultural Layout Validation
```
□ RTL layout testing with Arabic content
□ Long text handling in German/Indonesian
□ Cultural spacing preferences per region
□ Regional color adaptations working
□ Islamic geometric patterns scaling properly
□ Memorial photo aspect ratios maintained
□ Prayer counter thumb-friendly on all devices
□ Navigation patterns culturally appropriate
```

### ⚡ Performance Validation
```
□ 60 FPS scrolling on all breakpoints
□ Smooth transitions between layouts
□ Image optimization for all screen densities
□ Font loading optimization
□ Critical CSS under performance budget
□ Lazy loading working properly
□ Memory usage optimized
□ Battery impact minimized on mobile
```

---

**Responsive Layout System Maintained By**: UI/UX Design Team + Front-end Developers  
**Testing Coverage**: 15+ device configurations across 5 regions  
**Performance Monitoring**: Automated testing pipeline  
**Optimization Target**: 60 FPS + <3s load time on 3G networks  
**Next Review**: After user testing completion (May 29, 2026)  

*This comprehensive responsive layout system ensures that the Tahlil platform provides an optimal experience across all devices while respecting cultural preferences and maintaining Islamic authenticity globally.*