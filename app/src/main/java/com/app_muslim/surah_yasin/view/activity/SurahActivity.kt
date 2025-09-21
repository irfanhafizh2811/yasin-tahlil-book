package com.app_muslim.surah_yasin.view.activity

import android.os.Bundle
import android.util.Log
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.play.core.review.ReviewManagerFactory
import com.google.gson.Gson
import com.app_muslim.surah_yasin.R
import com.app_muslim.surah_yasin.data.model.Surah
import com.app_muslim.surah_yasin.data.model.surah.SurahInterface
import com.app_muslim.surah_yasin.data.model.surah.SurahQuran
import com.app_muslim.surah_yasin.data.preference.LanguagePreference
import com.app_muslim.surah_yasin.data.preference.SettingPreference
import com.app_muslim.surah_yasin.data.preference.SurahPreference
import com.app_muslim.surah_yasin.databinding.ActivitySurahBinding
import com.app_muslim.surah_yasin.extension.common.clazz
import com.app_muslim.surah_yasin.extension.context.getColorCompat
import com.app_muslim.surah_yasin.extension.context.readJsonAssetToString
import com.app_muslim.surah_yasin.extension.view.gone
import com.app_muslim.surah_yasin.extension.view.visible
import com.app_muslim.surah_yasin.utils.FontSize
import com.app_muslim.surah_yasin.utils.TextUtils
import com.app_muslim.surah_yasin.view.adapter.SurahAdapter
import com.app_muslim.surah_yasin.view.dialog.LanguageDialog
import com.app_muslim.surah_yasin.vm.LanguageViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class SurahActivity : BaseActivity() {

    private var surahName: String = TextUtils.BLANK
    private var data: Surah? = null
    private val surahAdapter by lazy { SurahAdapter() }
    private var isExpanded = false
    private var isReadLast = false
    private lateinit var binding: ActivitySurahBinding
    private lateinit var surah: SurahInterface
    private lateinit var currentFontSize: FontSize

    private val appBarTools by lazy { binding.appBarTools }
    private val ivBack by lazy { appBarTools.findViewById<ImageView>(R.id.iv_back) }
    private val tvSurah by lazy { appBarTools.findViewById<TextView>(R.id.tv_surah) }
    private val llMore by lazy { appBarTools.findViewById<LinearLayout>(R.id.ll_more) }
    private val tvPlusSize by lazy { appBarTools.findViewById<TextView>(R.id.tvPlusSize) }
    private val tvPlusSizeSymbol by lazy { appBarTools.findViewById<TextView>(R.id.tvPlusSizeSymbol) }
    private val tvMinusSize by lazy { appBarTools.findViewById<TextView>(R.id.tvMinusSize) }
    private val tvMinusSizeSymbol by lazy { appBarTools.findViewById<TextView>(R.id.tvMinusSizeSymbol) }
    private val scLatin by lazy { appBarTools.findViewById<SwitchCompat>(R.id.scLatin) }
    private val scTranslate by lazy { appBarTools.findViewById<SwitchCompat>(R.id.scTranslate) }
    private val ivLanguage by lazy { appBarTools.findViewById<ImageView>(R.id.iv_language) }
    private val tvLanguage by lazy { appBarTools.findViewById<TextView>(R.id.tv_language) }

    private val surahPref by inject<SurahPreference>()
    private val languagePref by inject<LanguagePreference>()
    private val settingPreference by inject<SettingPreference>()
    private val languageViewModel by inject<LanguageViewModel>()
    private val reviewManager by lazy {
        ReviewManagerFactory.create(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySurahBinding.inflate(layoutInflater)
        surahName = SurahQuran.YASIN.name
        isReadLast = intent.getBooleanExtra(READ_LAST_SURAH_INTENT_EXTRA, false)
        surah = com.app_muslim.surah_yasin.data.model.surah.SurahFactory.generate(
            SurahQuran.valueOf(surahName)
        )
        setContentView(binding.root)
        setSurahView()
        scrollLastRead()
        requestRatingReviewPlaystore()

        setupWebView()
        val htmlContent = createHtmlWithMutedYouTube()
        binding.webView.loadDataWithBaseURL("https://www.youtube.com", htmlContent, "text/html", "UTF-8", null)
    }

    private fun isMaxSize(): Boolean = currentFontSize == FontSize.HUGE
    private fun isMinSize(): Boolean = currentFontSize == FontSize.SMALL

    private fun setSurahView() = with(binding) {
        data = Gson().fromJson(readJsonAssetToString(surah.sourceJson), clazz<Surah>())
        observeViewModel()
        setupView()
        onUILabelColor()
    }

    private fun setupView() = with(binding) {
        rvSurah.also {
            val surahModel = com.app_muslim.surah_yasin.data.model.surah.SurahFactory.generate(
                SurahQuran.valueOf(surahName)
            )
            it.layoutManager = LinearLayoutManager(this@SurahActivity)
            it.adapter = surahAdapter.apply {
                onBindListener = { surah -> latestSurahPref(surah) }
                fontSize = FontSize.LARGE
                surahInterface = surahModel
                sync(data?.surah ?: listOf())
            }
        }
        progressBar.visible()
        ivBack.setOnClickListener { finish() }
        tvSurah.text = surahName.replace(TextUtils.UNDERSCORE, TextUtils.EMPTY_SPACE)
        llMore.setOnClickListener {
            if (!isExpanded) appBarTools.expand()
            else appBarTools.collapse()
            isExpanded = !isExpanded
        }
        tvPlusSize.setOnClickListener { onRaiseFont() }
        tvMinusSize.setOnClickListener { onLowerFont() }
        scLatin.setOnCheckedChangeListener { _, checked ->
            settingPreference.showQuranLatin = checked
            surahAdapter.apply {
                showLatinQuran = checked
            }.sync(data?.surah ?: listOf())
        }
        scTranslate.setOnCheckedChangeListener { _, checked ->
            settingPreference.showQuranTranslation = checked
            surahAdapter.apply {
                showTranslationQuran = checked
            }.sync(data?.surah ?: listOf())
        }
        ivLanguage.setOnClickListener {
            if (isExpanded) {
                appBarTools.expand()
                appBarTools.collapse()
                isExpanded = false
            }
            showLanguage()
        }
        tvLanguage.setOnClickListener {
            if (isExpanded) {
                appBarTools.expand()
                appBarTools.collapse()
                isExpanded = false
            }
            showLanguage()
        }
    }

    private fun observeViewModel() {
        languageViewModel.language.observe(this) {
            surahAdapter.language = it
            surahAdapter.sync(data?.surah ?: listOf())
        }
        languageViewModel.getLanguage()
    }

    private fun showLanguage() {
        LanguageDialog(this, languagePref.language)
            .selectedLanguage {
                languageViewModel.setupLanguage(it)
                surahAdapter.language = it
                surahAdapter.sync(data?.surah ?: listOf())
            }
            .show()
    }

    private fun scrollLastRead() = with(binding) {
        rvSurah.also {
            if (isReadLast && surahPref.lastReadVerse < surahAdapter.verses.size) {
                it.scrollToPosition(surahPref.lastReadVerse)
                lifecycleScope.launch {
                    delay(LAST_READ_DELAY_MILLIS)
                    Log.d("Surah Last Read:", true.toString())
                    surahAdapter.enableLastRead = true
                    progressBar.gone()
                }
            } else {
                surahAdapter.enableLastRead = false
                progressBar.gone()
            }
        }
    }

    private fun onUILabelColor() {
        surahAdapter.fontSize = settingPreference.fontSize
        val textColorPlus = getColorCompat(
            if (settingPreference.fontSize == FontSize.HUGE) R.color.themeUnselected
            else R.color.colorAccentPurple
        )
        val textColorMinus = getColorCompat(
            if (settingPreference.fontSize == FontSize.SMALL) R.color.themeUnselected
            else R.color.colorAccentPurple
        )
        tvPlusSize.setTextColor(textColorPlus)
        tvPlusSizeSymbol.setTextColor(textColorPlus)
        tvMinusSize.setTextColor(textColorMinus)
        tvMinusSizeSymbol.setTextColor(textColorMinus)
        scLatin.isChecked = settingPreference.showQuranLatin
        scTranslate.isChecked = settingPreference.showQuranTranslation
    }

    private fun onUpdateFont(isRaise: Boolean) {
        if (isMaxSize() && isRaise) return
        if (isMinSize() && !isRaise) return
        onUILabelColor()
        surahAdapter.sync(data?.surah ?: listOf())
    }

    private fun onRaiseFont() {
        currentFontSize = settingPreference.fontSize
        val fontSize = raiseFont(surahAdapter.fontSize.name)
        settingPreference.fontSize = fontSize
        surahAdapter.fontSize = fontSize
        onUpdateFont(true)
    }

    private fun onLowerFont() {
        currentFontSize = settingPreference.fontSize
        val fontSize = lowerFont(surahAdapter.fontSize.name)
        settingPreference.fontSize = lowerFont(surahAdapter.fontSize.name)
        surahAdapter.fontSize = fontSize
        onUpdateFont(false)
    }

    private fun raiseFont(fontSize: String): FontSize = when (FontSize.valueOf(fontSize)) {
        FontSize.HUGE -> FontSize.HUGE
        FontSize.LARGE -> FontSize.HUGE
        FontSize.REGULAR -> FontSize.LARGE
        FontSize.SMALL -> FontSize.REGULAR
    }

    private fun lowerFont(fontSize: String): FontSize = when (FontSize.valueOf(fontSize)) {
        FontSize.SMALL -> FontSize.SMALL
        FontSize.REGULAR -> FontSize.SMALL
        FontSize.LARGE -> FontSize.REGULAR
        FontSize.HUGE -> FontSize.LARGE
    }

    private fun latestSurahPref(surah: Pair<SurahInterface, Int>) = with(surah) {
        val numberAyah = surah.second
        if (numberAyah < first.verses) {
            surahPref.anyReadSurah = true
            surahPref.lastReadSurah =
                first.surahQuran.name.replace(TextUtils.UNDERSCORE, TextUtils.EMPTY_SPACE)
            surahPref.lastReadVerse = numberAyah - 1
        } else {
            surahPref.anyReadSurah = false
            surahPref.lastReadSurah = TextUtils.BLANK
            surahPref.lastReadVerse = 0
        }
    }

    override fun onBackPressed() {
        if (settingPreference.noHasSubmitRating) {
            requestRatingReviewPlaystore()
            return
        }
        super.onBackPressed()
    }

    private fun requestRatingReviewPlaystore() {
        if (settingPreference.noHasSubmitRating) {
            val request = reviewManager.requestReviewFlow()
            request.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val reviewInfo = task.result
                    val flow = reviewManager.launchReviewFlow(this, reviewInfo)
                    flow.addOnCompleteListener {
                        settingPreference.noHasSubmitRating = false
                    }
                } else {
                    task.exception?.let { it.printStackTrace() }
                }
            }
        }
    }

    private fun setupWebView() {
        val webSettings: WebSettings = binding.webView.settings

        // Enable JavaScript (required for YouTube)
        webSettings.javaScriptEnabled = true

        // Enable DOM storage
        webSettings.domStorageEnabled = true

        // Enable media playback without user gesture (required for autoplay)
        webSettings.mediaPlaybackRequiresUserGesture = false

        // Allow mixed content
        webSettings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW

        // Set user agent
        webSettings.userAgentString = "Mozilla/5.0 (Linux; Android 10; SM-G975F) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.120 Mobile Safari/537.36"

        // Enable zoom controls
        webSettings.setSupportZoom(true)
        webSettings.builtInZoomControls = true
        webSettings.displayZoomControls = false

        // Set cache mode
        webSettings.cacheMode = WebSettings.LOAD_DEFAULT

        // Enable hardware acceleration
        webSettings.setRenderPriority(WebSettings.RenderPriority.HIGH)

        // Set WebView client
        binding.webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                return false // Let WebView handle all URLs
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                // Apply comprehensive muting after page loads
                injectComprehensiveMuteScript()
            }
        }

        // Set WebChromeClient for media support
        binding.webView.webChromeClient = object : WebChromeClient() {
            override fun onPermissionRequest(request: android.webkit.PermissionRequest?) {
                request?.grant(request.resources)
            }
        }
    }

    private fun createHtmlWithMutedYouTube(): String {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <style>
                    body {
                        margin: 0;
                        padding: 0;
                        background-color: #000;
                        font-family: Arial, sans-serif;
                        overflow: hidden;
                    }
                    .container {
                        width: 100%;
                        height: 100vh;
                        display: flex;
                        flex-direction: column;
                    }
                    .video-container {
                        position: relative;
                        width: 100%;
                        height: 100%;
                        flex-grow: 1;
                    }
                    iframe {
                        position: absolute;
                        top: 0;
                        left: 0;
                        width: 100%;
                        height: 100%;
                        border: none;
                    }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="video-container">
                        <iframe
                            id="youtubePlayer"
                            src="https://www.youtube.com/embed/GFxvIiPmP40?mute=1&autoplay=1&controls=1&rel=0&enablejsapi=1&origin=https://www.youtube.com&playsinline=1"
                            title="YouTube video player"
                            frameborder="0"
                            allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture"
                            allowfullscreen>
                        </iframe>
                    </div>
                </div>
                
                <script>
                    // Global muting variables
                    let originalCreateElement = document.createElement;
                    let mutingEnabled = true;
                    
                    // Override document.createElement to mute new media elements
                    document.createElement = function(tagName) {
                        let element = originalCreateElement.call(this, tagName);
                        
                        if (mutingEnabled && (tagName.toLowerCase() === 'video' || tagName.toLowerCase() === 'audio')) {
                            element.muted = true;
                            element.volume = 0;
                            
                            // Override volume and muted properties
                            Object.defineProperty(element, 'volume', {
                                get: function() { return 0; },
                                set: function(val) { /* ignore volume changes */ }
                            });
                            
                            Object.defineProperty(element, 'muted', {
                                get: function() { return true; },
                                set: function(val) { /* keep muted */ }
                            });
                            
                            // Add event listeners to maintain muting
                            element.addEventListener('loadstart', function() {
                                this.muted = true;
                                this.volume = 0;
                            });
                            
                            element.addEventListener('canplay', function() {
                                this.muted = true;
                                this.volume = 0;
                            });
                        }
                        
                        return element;
                    };
                    
                    // Function to mute all existing media
                    function muteAllMedia() {
                        try {
                            // Mute all video and audio elements
                            document.querySelectorAll('video, audio').forEach(function(media) {
                                media.muted = true;
                                media.volume = 0;
                                
                                // Add event listeners to maintain muting
                                media.addEventListener('volumechange', function() {
                                    if (!this.muted) this.muted = true;
                                    if (this.volume > 0) this.volume = 0;
                                });
                            });
                            
                            // YouTube iframe communication
                            var iframe = document.getElementById('youtubePlayer');
                            if (iframe) {
                                iframe.contentWindow.postMessage('{"event":"command","func":"mute","args":""}', '*');
                                iframe.contentWindow.postMessage('{"event":"command","func":"setVolume","args":[0]}', '*');
                            }
                            
                        } catch(e) {
                            console.log('Muting error:', e);
                        }
                    }
                    
                    // Observer to watch for new media elements
                    var observer = new MutationObserver(function(mutations) {
                        mutations.forEach(function(mutation) {
                            mutation.addedNodes.forEach(function(node) {
                                if (node.nodeType === 1) { // Element node
                                    if (node.tagName === 'VIDEO' || node.tagName === 'AUDIO') {
                                        node.muted = true;
                                        node.volume = 0;
                                    }
                                    // Check children
                                    if (node.querySelectorAll) {
                                        var mediaElements = node.querySelectorAll('video, audio');
                                        mediaElements.forEach(function(media) {
                                            media.muted = true;
                                            media.volume = 0;
                                        });
                                    }
                                }
                            });
                        });
                    });
                    
                    // Start observing
                    observer.observe(document.body, {
                        childList: true,
                        subtree: true
                    });
                    
                    // Override Web Audio API
                    try {
                        if (window.AudioContext || window.webkitAudioContext) {
                            var AudioContextClass = window.AudioContext || window.webkitAudioContext;
                            var originalCreateGain = AudioContextClass.prototype.createGain;
                            
                            AudioContextClass.prototype.createGain = function() {
                                var gainNode = originalCreateGain.call(this);
                                gainNode.gain.value = 0;
                                return gainNode;
                            };
                        }
                    } catch(e) {
                        console.log('AudioContext override failed:', e);
                    }
                    
                    // Apply muting immediately and repeatedly
                    muteAllMedia();
                    
                    // Continuous muting
                    setInterval(muteAllMedia, 500);
                    
                    // Mute on various events
                    window.addEventListener('load', muteAllMedia);
                    document.addEventListener('DOMContentLoaded', muteAllMedia);
                    
                    // Iframe load event
                    document.getElementById('youtubePlayer').addEventListener('load', function() {
                        setTimeout(muteAllMedia, 100);
                    });
                </script>
            </body>
            </html>
        """.trimIndent()
    }

    private fun injectComprehensiveMuteScript() {
        val muteScript = """
            javascript:(function() {
                try {
                    // Mute all existing media
                    document.querySelectorAll('video, audio').forEach(function(media) {
                        media.muted = true;
                        media.volume = 0;
                        
                        // Override properties to maintain muting
                        Object.defineProperty(media, 'volume', {
                            get: function() { return 0; },
                            set: function(val) { /* ignore */ }
                        });
                        
                        Object.defineProperty(media, 'muted', {
                            get: function() { return true; },
                            set: function(val) { /* ignore */ }
                        });
                    });
                    
                    // YouTube-specific muting and autoplay
                    var iframe = document.querySelector('iframe');
                    if (iframe) {
                        iframe.contentWindow.postMessage('{"event":"command","func":"mute","args":""}', '*');
                        iframe.contentWindow.postMessage('{"event":"command","func":"setVolume","args":[0]}', '*');
                        iframe.contentWindow.postMessage('{"event":"command","func":"playVideo","args":""}', '*');
                    }
                    
                    // Try to click YouTube mute button
                    var muteButton = document.querySelector('.ytp-mute-button');
                    if (muteButton && !muteButton.classList.contains('ytp-muted')) {
                        muteButton.click();
                    }
                    
                } catch(e) {
                    console.log('Comprehensive mute failed:', e);
                }
            })();
        """

        binding.webView.evaluateJavascript(muteScript, null)

        // Re-apply muting after delays to catch late-loading content
        binding.webView.postDelayed({
            binding.webView.evaluateJavascript(muteScript, null)
        }, 1000)

        binding.webView.postDelayed({
            binding.webView.evaluateJavascript(muteScript, null)
        }, 3000)
    }

    override fun onResume() {
        super.onResume()
        binding.webView.onResume()
        // Re-apply muting when app resumes
        binding.webView.postDelayed({
            injectComprehensiveMuteScript()
        }, 500)
    }

    override fun onPause() {
        super.onPause()
        binding.webView.onPause()
    }

    override fun onDestroy() {
        binding.webView.destroy()
        super.onDestroy()
    }

    companion object {
        const val SURAH_INTENT_EXTRA = "SURAH_INTENT_EXTRA"
        const val READ_LAST_SURAH_INTENT_EXTRA = "READ_LAST_SURAH_INTENT_EXTRA"
        const val LAST_READ_DELAY_MILLIS = 300L
    }
}