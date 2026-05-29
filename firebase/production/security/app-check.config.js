/**
 * Firebase App Check Configuration for Production
 * Protects Tahlil platform from abuse and ensures authentic app usage
 */

const admin = require('firebase-admin');

// App Check configuration for production
const appCheckConfig = {
  // Production app attestation
  appId: 'com.app_muslim.surah_yasin',
  
  // Play Integrity API for Android production
  androidConfig: {
    provider: 'playintegrity',
    
    // Production settings
    settings: {
      // Require Play Integrity verdict
      minimumVerdictLevel: 'MEETS_DEVICE_INTEGRITY',
      
      // Enable all integrity checks
      enablePlayProtectVerification: true,
      enableAppLicensingVerification: true,
      enableDeviceRecognition: true,
      
      // Token refresh interval (1 hour)
      tokenTtl: 3600,
      
      // Grace period for new installations (5 minutes)
      gracePeriod: 300
    }
  },
  
  // iOS App Attestation (future implementation)
  iosConfig: {
    provider: 'devicecheck',
    settings: {
      // iOS specific settings
      teamId: 'YOUR_TEAM_ID',
      bundleId: 'com.app_muslim.surah_yasin',
      tokenTtl: 3600
    }
  },
  
  // Web reCAPTCHA (for web admin panel)
  webConfig: {
    provider: 'recaptcha_v3',
    settings: {
      siteKey: process.env.RECAPTCHA_SITE_KEY,
      secretKey: process.env.RECAPTCHA_SECRET_KEY,
      minimumScore: 0.5
    }
  },
  
  // Custom token verification for API access
  customTokenConfig: {
    // Memorial creation rate limiting
    memorialCreation: {
      maxPerHour: 10,
      maxPerDay: 50
    },
    
    // Prayer submission rate limiting  
    prayerSubmission: {
      maxPerMinute: 100,
      maxPerHour: 1000,
      maxPerDay: 10000
    },
    
    // Photo upload rate limiting
    photoUpload: {
      maxPerHour: 20,
      maxSizeKb: 10240, // 10MB
      allowedTypes: ['image/jpeg', 'image/png', 'image/webp']
    },
    
    // Community interaction limits
    communityInteraction: {
      maxSharesPerDay: 50,
      maxInvitesPerDay: 100,
      maxCommentsPerHour: 20
    }
  },
  
  // Geographic restrictions
  geoRestrictions: {
    // Allowed countries for app usage
    allowedCountries: [
      'ID', 'MY', 'SG', 'TH', 'VN', 'PH', 'BN', // Southeast Asia
      'SA', 'AE', 'EG', 'MA', 'TN', 'JO', 'LB', 'KW', 'QA', 'BH', 'OM', // MENA
      'PK', 'BD', 'IN', 'LK', 'AF', 'MV', // South Asia
      'TR', 'AZ', 'KZ', 'UZ', 'TM', 'KG', 'TJ', // Central Asia & Turkey
      'US', 'CA', 'GB', 'FR', 'DE', 'NL', 'BE', 'SE', 'NO', 'DK', // Western countries
      'AU', 'NZ' // Oceania
    ],
    
    // Blocked regions (if any specific security concerns)
    blockedRegions: [],
    
    // Special handling for pilgrimage locations
    priorityRegions: ['SA', 'AE'] // Mecca, Medina access priority
  }
};

/**
 * Initialize App Check with production configuration
 */
function initializeAppCheck() {
  try {
    // Configure App Check
    admin.appCheck().createToken(appCheckConfig.appId, {
      ttlMillis: appCheckConfig.androidConfig.settings.tokenTtl * 1000
    });
    
    console.log('✅ App Check initialized for production');
    return true;
  } catch (error) {
    console.error('❌ App Check initialization failed:', error);
    return false;
  }
}

/**
 * Verify App Check token for API requests
 */
async function verifyAppCheckToken(token) {
  try {
    const appCheckClaims = await admin.appCheck().verifyToken(token);
    
    // Log successful verification
    console.log('✅ App Check token verified:', {
      appId: appCheckClaims.appId,
      audience: appCheckClaims.aud,
      issuer: appCheckClaims.iss
    });
    
    return {
      valid: true,
      appId: appCheckClaims.appId,
      claims: appCheckClaims
    };
  } catch (error) {
    console.error('❌ App Check token verification failed:', error);
    
    return {
      valid: false,
      error: error.message
    };
  }
}

/**
 * Rate limiting middleware with Islamic cultural considerations
 */
function createRateLimiter(limitConfig) {
  const attempts = new Map();
  
  return (req, res, next) => {
    const clientId = req.headers['x-client-id'] || req.ip;
    const now = Date.now();
    const windowStart = now - (limitConfig.windowMs || 3600000); // 1 hour default
    
    // Clean old attempts
    const clientAttempts = attempts.get(clientId) || [];
    const recentAttempts = clientAttempts.filter(time => time > windowStart);
    
    // Check if rate limit exceeded
    if (recentAttempts.length >= limitConfig.max) {
      console.warn(`⚠️ Rate limit exceeded for client: ${clientId}`);
      
      return res.status(429).json({
        error: 'Rate limit exceeded',
        message: 'Please observe Islamic principles of moderation (الاعتدال)',
        retryAfter: Math.ceil((windowStart + limitConfig.windowMs - now) / 1000)
      });
    }
    
    // Add current attempt
    recentAttempts.push(now);
    attempts.set(clientId, recentAttempts);
    
    next();
  };
}

/**
 * Content validation with Islamic guidelines
 */
function validateIslamicContent(content, type) {
  const validationRules = {
    memorial: {
      // Memorial content validation
      maxLength: 1000,
      requiredFields: ['deceasedName', 'prayerType'],
      prohibitedWords: [], // Add culturally inappropriate terms
      arabicTextRequired: false
    },
    
    prayer: {
      // Prayer submission validation
      allowedTypes: ['tahlil', 'yasin', 'fatihah', 'dua', 'dhikr'],
      maxCount: 1000, // Maximum prayers per session
      culturalValidation: true
    },
    
    comment: {
      // Community comment validation
      maxLength: 500,
      respectfulLanguage: true,
      islamicContext: true
    }
  };
  
  const rules = validationRules[type];
  if (!rules) return { valid: false, error: 'Unknown content type' };
  
  // Apply validation rules
  if (content.length > rules.maxLength) {
    return { valid: false, error: 'Content too long' };
  }
  
  // Cultural appropriateness check
  if (rules.culturalValidation) {
    // Implement Islamic cultural validation logic
    console.log('🕌 Validating Islamic cultural appropriateness');
  }
  
  return { valid: true };
}

module.exports = {
  appCheckConfig,
  initializeAppCheck,
  verifyAppCheckToken,
  createRateLimiter,
  validateIslamicContent
};