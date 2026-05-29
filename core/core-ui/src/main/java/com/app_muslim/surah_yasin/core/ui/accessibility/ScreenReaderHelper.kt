package com.app_muslim.surah_yasin.core.ui.accessibility

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.view.View
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityManager
import android.view.accessibility.AccessibilityNodeInfo
import androidx.core.content.ContextCompat
import androidx.core.view.AccessibilityDelegateCompat
import androidx.core.view.ViewCompat
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Screen Reader Helper for Islamic Prayer Platform
 * 
 * Comprehensive screen reader support optimized for Arabic content,
 * Islamic terminology, and cultural accessibility needs.
 * 
 * Features:
 * - Arabic pronunciation guides for non-Arabic screen readers
 * - Islamic terminology explanation in multiple languages
 * - Prayer progress announcements with spiritual context
 * - Memorial content read with appropriate reverence
 * - Multilingual accessibility support (Arabic, English, Indonesian, etc.)
 */
@Singleton
class ScreenReaderHelper @Inject constructor(
    private val context: Context,
    private val accessibilityManager: AccessibilityManager
) : TextToSpeech.OnInitListener {

    private var textToSpeech: TextToSpeech? = null
    private val _ttsState = MutableStateFlow(TTSState.NOT_INITIALIZED)
    val ttsState: StateFlow<TTSState> = _ttsState.asStateFlow()

    companion object {
        // Islamic terminology with pronunciation guides
        private val ISLAMIC_PRONUNCIATION_GUIDE = mapOf(
            "الله" to "Allah",
            "محمد" to "Muhammad",
            "صلى الله عليه وسلم" to "salla allahu alayhi wa sallam",
            "رضي الله عنه" to "radiya allahu anhu", 
            "رحمه الله" to "rahimahu allah",
            "إن شاء الله" to "in sha Allah",
            "الحمد لله" to "alhamdulillah",
            "سبحان الله" to "subhan Allah",
            "لا إله إلا الله" to "la ilaha illa Allah",
            "استغفر الله" to "astaghfiru Allah",
            "بسم الله الرحمن الرحيم" to "bismillahi rahmani rahim"
        )

        // Prayer type descriptions for screen readers
        private val PRAYER_DESCRIPTIONS = mapOf(
            "tahlil" to "Tahlil - Declaration of Allah's oneness",
            "fatihah" to "Al-Fatihah - The opening chapter of Quran",
            "yasin" to "Surah Ya-Sin - The heart of the Quran",
            "istighfar" to "Istighfar - Seeking Allah's forgiveness",
            "salawat" to "Salawat - Sending blessings upon Prophet Muhammad"
        )

        // Cultural accessibility constants
        private const val ARABIC_SPEECH_RATE = 0.8f // Slower for Arabic content
        private const val PRAYER_ANNOUNCEMENT_DELAY = 1000L
        private const val MEMORIAL_REVERENCE_PAUSE = 500L
    }

    init {
        initializeTextToSpeech()
    }

    /**
     * Initialize Text-to-Speech engine
     */
    private fun initializeTextToSpeech() {
        textToSpeech = TextToSpeech(context, this)
    }

    /**
     * TTS initialization callback
     */
    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            textToSpeech?.let { tts ->
                // Set Arabic locale if available, fallback to English
                val arabicLocale = Locale("ar")
                val result = tts.setLanguage(arabicLocale)
                
                if (result == TextToSpeech.LANG_MISSING_DATA || 
                    result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    tts.setLanguage(Locale.ENGLISH)
                }
                
                tts.setSpeechRate(ARABIC_SPEECH_RATE)
                setupUtteranceProgressListener()
                _ttsState.value = TTSState.READY
            }
        } else {
            _ttsState.value = TTSState.ERROR
        }
    }

    /**
     * Setup utterance progress listener for speech feedback
     */
    private fun setupUtteranceProgressListener() {
        textToSpeech?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {
                _ttsState.value = TTSState.SPEAKING
            }

            override fun onDone(utteranceId: String?) {
                _ttsState.value = TTSState.READY
            }

            override fun onError(utteranceId: String?) {
                _ttsState.value = TTSState.ERROR
            }
        })
    }

    /**
     * Configure View for comprehensive screen reader support
     */
    fun configureViewForScreenReader(
        view: View,
        contentDescription: String,
        role: AccessibilityRole = AccessibilityRole.DEFAULT,
        additionalInfo: AccessibilityAdditionalInfo? = null
    ) {
        ViewCompat.setAccessibilityDelegate(view, object : AccessibilityDelegateCompat() {
            override fun onInitializeAccessibilityNodeInfo(
                host: View,
                info: AccessibilityNodeInfoCompat
            ) {
                super.onInitializeAccessibilityNodeInfo(host, info)
                
                // Set content description
                info.contentDescription = contentDescription
                
                // Configure role-specific properties
                when (role) {
                    AccessibilityRole.PRAYER_COUNTER -> {
                        info.roleDescription = "Prayer counter button"
                        info.addAction(AccessibilityNodeInfoCompat.ACTION_CLICK)
                        info.isClickable = true
                    }
                    AccessibilityRole.MEMORIAL_CARD -> {
                        info.roleDescription = "Memorial card"
                        info.addAction(AccessibilityNodeInfoCompat.ACTION_CLICK)
                        info.isClickable = true
                    }
                    AccessibilityRole.ARABIC_TEXT -> {
                        info.roleDescription = "Arabic religious text"
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                            info.unwrap().extras.putString(
                                AccessibilityNodeInfo.EXTRA_DATA_TEXT_CHARACTER_LOCATION_KEY,
                                "arabic"
                            )
                        }
                    }
                    AccessibilityRole.PRAYER_PROGRESS -> {
                        info.roleDescription = "Prayer progress indicator"
                        additionalInfo?.let { additional ->
                            info.stateDescription = 
                                "${additional.currentProgress} of ${additional.targetProgress} completed"
                        }
                    }
                    AccessibilityRole.NAVIGATION_ITEM -> {
                        info.roleDescription = "Navigation item"
                        info.addAction(AccessibilityNodeInfoCompat.ACTION_CLICK)
                    }
                    AccessibilityRole.DEFAULT -> {
                        // Use default behavior
                    }
                }
                
                // Add cultural context if available
                additionalInfo?.culturalContext?.let { context ->
                    info.tooltipText = context
                }
                
                // Set language for Arabic content
                if (additionalInfo?.isArabicContent == true) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        info.unwrap().extras.putString(
                            AccessibilityNodeInfo.EXTRA_DATA_TEXT_CHARACTER_LOCATION_KEY,
                            "ar"
                        )
                    }
                }
            }
        })
    }

    /**
     * Announce prayer completion with Islamic reverence
     */
    fun announcePrayerCompletion(
        prayerType: String,
        count: Int,
        isArabicUser: Boolean = false
    ) {
        if (!accessibilityManager.isEnabled) return
        
        val prayerDescription = PRAYER_DESCRIPTIONS[prayerType.lowercase()] ?: prayerType
        val announcement = if (isArabicUser) {
            generateArabicPrayerAnnouncement(prayerType, count)
        } else {
            "Prayer completed. $prayerDescription. Count: $count. May Allah accept this prayer."
        }
        
        announceForAccessibility(announcement, isImportant = true)
    }

    /**
     * Announce memorial creation with cultural sensitivity
     */
    fun announceMemorialCreation(
        deceasedName: String,
        relationship: String,
        isArabicUser: Boolean = false
    ) {
        if (!accessibilityManager.isEnabled) return
        
        val announcement = if (isArabicUser) {
            "تم إنشاء تذكار للمرحوم $deceasedName. رحمة الله عليه. نسأل الله أن يتقبل دعواتنا."
        } else {
            "Memorial created for $deceasedName, your $relationship. " +
            "May Allah have mercy on their soul and accept our prayers."
        }
        
        announceForAccessibility(announcement, isImportant = true, addReverencePause = true)
    }

    /**
     * Announce community prayer joining
     */
    fun announceCommunityPrayerJoined(
        sessionName: String,
        participantCount: Int,
        isArabicUser: Boolean = false
    ) {
        if (!accessibilityManager.isEnabled) return
        
        val announcement = if (isArabicUser) {
            "انضممت إلى جلسة صلاة جماعية: $sessionName. عدد المشاركين: $participantCount"
        } else {
            "Joined community prayer session: $sessionName. " +
            "$participantCount Muslims praying together."
        }
        
        announceForAccessibility(announcement, isImportant = true)
    }

    /**
     * Provide Arabic pronunciation guide for screen readers
     */
    fun getArabicPronunciationGuide(arabicText: String): String {
        // Check for exact matches first
        ISLAMIC_PRONUNCIATION_GUIDE[arabicText]?.let { return it }
        
        // Check for partial matches (common phrases within longer text)
        ISLAMIC_PRONUNCIATION_GUIDE.forEach { (arabic, pronunciation) ->
            if (arabicText.contains(arabic)) {
                return arabicText.replace(arabic, pronunciation)
            }
        }
        
        // If no match found, provide general guidance
        return "Arabic text: $arabicText"
    }

    /**
     * Get cultural context description for accessibility
     */
    fun getCulturalContextDescription(elementType: CulturalElementType): String {
        return when (elementType) {
            CulturalElementType.MEMORIAL_PHOTO -> 
                "Memorial photo of deceased loved one. Displayed with Islamic respect."
            CulturalElementType.PRAYER_COUNTER -> 
                "Islamic prayer counter. Tap to count dhikr or Tahlil prayers."
            CulturalElementType.QURANIC_VERSE -> 
                "Verse from the Holy Quran. Sacred text from Allah."
            CulturalElementType.ISLAMIC_PATTERN -> 
                "Traditional Islamic geometric pattern. Decorative art without living figures."
            CulturalElementType.PRAYER_TIME -> 
                "Islamic prayer time notification. Five daily prayers are obligatory."
            CulturalElementType.HAJJ_CALENDAR -> 
                "Islamic lunar calendar date. Follows moon phases for religious observance."
            CulturalElementType.DUA_TEXT -> 
                "Islamic supplication prayer. Personal communication with Allah."
        }
    }

    /**
     * Configure live region for dynamic content updates
     */
    fun configureLiveRegion(
        view: View,
        liveRegionMode: LiveRegionMode = LiveRegionMode.POLITE
    ) {
        val mode = when (liveRegionMode) {
            LiveRegionMode.POLITE -> ViewCompat.ACCESSIBILITY_LIVE_REGION_POLITE
            LiveRegionMode.ASSERTIVE -> ViewCompat.ACCESSIBILITY_LIVE_REGION_ASSERTIVE
            LiveRegionMode.NONE -> ViewCompat.ACCESSIBILITY_LIVE_REGION_NONE
        }
        ViewCompat.setAccessibilityLiveRegion(view, mode)
    }

    /**
     * Announce text with TTS if available
     */
    fun speakText(
        text: String,
        isArabic: Boolean = false,
        priority: TTSPriority = TTSPriority.NORMAL
    ) {
        textToSpeech?.let { tts ->
            if (_ttsState.value == TTSState.READY) {
                val queueMode = when (priority) {
                    TTSPriority.URGENT -> TextToSpeech.QUEUE_FLUSH
                    TTSPriority.NORMAL -> TextToSpeech.QUEUE_ADD
                }
                
                val processedText = if (isArabic) {
                    getArabicPronunciationGuide(text)
                } else {
                    text
                }
                
                val utteranceId = "tahlil_tts_${System.currentTimeMillis()}"
                tts.speak(processedText, queueMode, null, utteranceId)
            }
        }
    }

    /**
     * Stop current speech
     */
    fun stopSpeaking() {
        textToSpeech?.stop()
        _ttsState.value = TTSState.READY
    }

    /**
     * Clean up resources
     */
    fun cleanup() {
        textToSpeech?.shutdown()
        textToSpeech = null
        _ttsState.value = TTSState.NOT_INITIALIZED
    }

    // Private helper methods
    
    private fun announceForAccessibility(
        text: String,
        isImportant: Boolean = false,
        addReverencePause: Boolean = false
    ) {
        if (addReverencePause) {
            // Add a brief pause for reverence when announcing memorial content
            android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                performAccessibilityAnnouncement(text, isImportant)
            }, MEMORIAL_REVERENCE_PAUSE)
        } else {
            performAccessibilityAnnouncement(text, isImportant)
        }
    }
    
    private fun performAccessibilityAnnouncement(text: String, isImportant: Boolean) {
        if (accessibilityManager.isEnabled) {
            val event = AccessibilityEvent.obtain().apply {
                eventType = AccessibilityEvent.TYPE_ANNOUNCEMENT
                getText().add(text)
                if (isImportant && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    // Set higher priority for important announcements
                    className = "important_announcement"
                }
            }
            accessibilityManager.sendAccessibilityEvent(event)
            event.recycle()
        }
    }
    
    private fun generateArabicPrayerAnnouncement(prayerType: String, count: Int): String {
        return when (prayerType.lowercase()) {
            "tahlil" -> "تم إكمال التهليل رقم $count. لا إله إلا الله"
            "fatihah" -> "تم إكمال الفاتحة رقم $count. فاتحة الكتاب"
            "yasin" -> "تم إكمال يس رقم $count. قلب القرآن"
            "istighfar" -> "تم إكمال الاستغفار رقم $count. أستغفر الله"
            "salawat" -> "تم إكمال الصلاة على النبي رقم $count. صلى الله عليه وسلم"
            else -> "تم إكمال الدعاء رقم $count"
        }
    }
}

/**
 * Accessibility role for semantic understanding
 */
enum class AccessibilityRole {
    DEFAULT,
    PRAYER_COUNTER,
    MEMORIAL_CARD,
    ARABIC_TEXT,
    PRAYER_PROGRESS,
    NAVIGATION_ITEM
}

/**
 * Cultural element types for context
 */
enum class CulturalElementType {
    MEMORIAL_PHOTO,
    PRAYER_COUNTER,
    QURANIC_VERSE,
    ISLAMIC_PATTERN,
    PRAYER_TIME,
    HAJJ_CALENDAR,
    DUA_TEXT
}

/**
 * Live region update modes
 */
enum class LiveRegionMode {
    NONE,
    POLITE,
    ASSERTIVE
}

/**
 * Text-to-speech priority levels
 */
enum class TTSPriority {
    NORMAL,
    URGENT
}

/**
 * Text-to-speech state
 */
enum class TTSState {
    NOT_INITIALIZED,
    READY,
    SPEAKING,
    ERROR
}

/**
 * Announcement types for screen reader priorities
 */
enum class AnnouncementType {
    PRAYER_COMPLETION,
    MEMORIAL_CREATION,
    COMMUNITY_JOIN,
    PROGRESS_UPDATE,
    NAVIGATION,
    ERROR,
    SUCCESS
}

/**
 * Additional accessibility information
 */
data class AccessibilityAdditionalInfo(
    val isArabicContent: Boolean = false,
    val culturalContext: String? = null,
    val currentProgress: Int? = null,
    val targetProgress: Int? = null
)