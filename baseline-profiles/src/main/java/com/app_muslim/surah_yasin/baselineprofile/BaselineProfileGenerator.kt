package com.app_muslim.surah_yasin.baselineprofile

import androidx.benchmark.macro.junit4.BaselineProfileRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Until
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Baseline Profile Generator for Tahlil Memorial Platform
 * 
 * Generates optimized baseline profiles for:
 * - App startup optimization (<3s target)
 * - Prayer counter performance (<50ms target)
 * - Memorial creation and navigation flows
 * - Community features and Firebase operations
 * 
 * This significantly improves cold startup performance by pre-compiling
 * critical code paths that are commonly used during app initialization
 * and core user flows.
 */
@RunWith(AndroidJUnit4::class)
@LargeTest
class BaselineProfileGenerator {
    
    @get:Rule
    val baselineProfileRule = BaselineProfileRule()
    
    @Test
    fun generateBaselineProfile() = baselineProfileRule.collect(
        packageName = "com.app_muslim.surah_yasin",
        maxIterations = 3,
        includeInStartupProfile = true
    ) {
        // Start the app and wait for it to settle
        pressHome()
        startActivityAndWait()
        
        // 1. App Startup Flow - Critical for <3s startup target
        generateStartupProfile()
        
        // 2. Prayer Counter Flow - Critical for <50ms response target
        generatePrayerCounterProfile()
        
        // 3. Memorial Creation Flow - Core feature usage
        generateMemorialCreationProfile()
        
        // 4. Community Features Flow - Social engagement
        generateCommunityFeaturesProfile()
        
        // 5. Navigation Flow - Bottom navigation and screen transitions
        generateNavigationProfile()
        
        // 6. Firebase Operations Flow - Data loading and sync
        generateFirebaseOperationsProfile()
    }
    
    /**
     * Generate baseline profile for app startup optimization
     * Focuses on critical startup paths including:
     * - Application class initialization
     * - Hilt dependency injection setup
     * - Firebase initialization
     * - Main activity and compose setup
     */
    private fun generateStartupProfile() {
        // Wait for splash screen or initial loading
        device.wait(Until.hasObject(By.pkg("com.app_muslim.surah_yasin")), 5000)
        
        // Wait for main screen to load completely
        device.wait(Until.hasObject(By.desc("Main Navigation")), 3000)
        
        // Let the app settle to capture startup compilation
        device.waitForIdle(2000)
    }
    
    /**
     * Generate baseline profile for prayer counter performance
     * Critical for meeting <50ms response target
     */
    private fun generatePrayerCounterProfile() {
        // Navigate to prayer counter (if not on main screen)
        // Assuming prayer counter is accessible from main screen
        val prayerCounterButton = device.findObject(By.text("Start Prayer"))
        if (prayerCounterButton != null) {
            prayerCounterButton.click()
            device.waitForIdle(1000)
        }
        
        // Simulate rapid prayer counter interactions
        repeat(20) {
            val counterButton = device.findObject(By.desc("Prayer Counter"))
            if (counterButton != null) {
                counterButton.click()
                // Small delay to simulate realistic usage
                Thread.sleep(100)
            } else {
                // If prayer counter not found, look for alternative selectors
                val tapArea = device.findObject(By.desc("Tap to Count"))
                tapArea?.click()
                Thread.sleep(100)
            }
        }
        
        device.waitForIdle(1000)
    }
    
    /**
     * Generate baseline profile for memorial creation
     * Covers memorial form, photo upload, and Firebase operations
     */
    private fun generateMemorialCreationProfile() {
        // Navigate to memorial creation
        val createMemorialButton = device.findObject(By.text("Create Memorial"))
        if (createMemorialButton != null) {
            createMemorialButton.click()
            device.waitForIdle(2000)
            
            // Fill out memorial form
            val nameField = device.findObject(By.desc("Deceased Name"))
            nameField?.text = "Test Memorial"
            
            // Simulate form interactions
            val messageField = device.findObject(By.desc("Memorial Message"))
            messageField?.click()
            device.waitForIdle(500)
            
            // Simulate privacy selection
            val privacyButton = device.findObject(By.text("Family"))
            privacyButton?.click()
            device.waitForIdle(500)
            
            // Navigate back
            device.pressBack()
            device.waitForIdle(1000)
        }
    }
    
    /**
     * Generate baseline profile for community features
     * Includes leaderboards, statistics, and social interactions
     */
    private fun generateCommunityFeaturesProfile() {
        // Navigate to community tab
        val communityTab = device.findObject(By.text("Community"))
        if (communityTab != null) {
            communityTab.click()
            device.waitForIdle(2000)
            
            // Interact with community features
            val leaderboardCard = device.findObject(By.desc("Prayer Leaderboard"))
            leaderboardCard?.click()
            device.waitForIdle(1000)
            
            // Simulate leaderboard interactions
            val timeFrameFilter = device.findObject(By.text("This Week"))
            timeFrameFilter?.click()
            device.waitForIdle(500)
            
            val monthlyFilter = device.findObject(By.text("This Month"))
            monthlyFilter?.click()
            device.waitForIdle(500)
            
            device.pressBack()
            device.waitForIdle(1000)
        }
    }
    
    /**
     * Generate baseline profile for navigation flows
     * Covers bottom navigation and screen transitions
     */
    private fun generateNavigationProfile() {
        // Test all bottom navigation tabs
        val navigationTabs = listOf("Memorial", "Community", "Profile")
        
        navigationTabs.forEach { tabName ->
            val tab = device.findObject(By.text(tabName))
            if (tab != null) {
                tab.click()
                device.waitForIdle(1000)
                
                // Let screen load completely
                device.waitForIdle(1500)
            }
        }
        
        // Return to main tab
        val mainTab = device.findObject(By.desc("Home"))
        mainTab?.click()
        device.waitForIdle(1000)
    }
    
    /**
     * Generate baseline profile for Firebase operations
     * Includes data loading, caching, and synchronization
     */
    private fun generateFirebaseOperationsProfile() {
        // Simulate data refresh operations
        val refreshButton = device.findObject(By.desc("Refresh"))
        if (refreshButton != null) {
            refreshButton.click()
            device.waitForIdle(2000)
        }
        
        // Navigate through data-heavy screens to trigger Firebase operations
        val memorialList = device.findObject(By.desc("Memorial List"))
        if (memorialList != null) {
            memorialList.click()
            device.waitForIdle(2000)
            
            // Simulate scrolling to trigger lazy loading
            device.swipe(500, 800, 500, 200, 10)
            device.waitForIdle(1000)
            
            device.pressBack()
            device.waitForIdle(1000)
        }
        
        // Test search functionality
        val searchButton = device.findObject(By.desc("Search"))
        if (searchButton != null) {
            searchButton.click()
            device.waitForIdle(1000)
            
            val searchField = device.findObject(By.desc("Search Field"))
            searchField?.text = "test"
            device.waitForIdle(1000)
            
            device.pressBack()
            device.waitForIdle(1000)
        }
    }
}