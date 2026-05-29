#!/bin/bash
# 📊 Tahlil Production Monitoring Setup
# Comprehensive monitoring for Islamic memorial platform

set -e

echo "📊 Setting up Tahlil Production Monitoring..."

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
PURPLE='\033[0;35m'
NC='\033[0m'

# Configuration
FIREBASE_PROJECT="${FIREBASE_PROJECT:-surah-almulk}"
ENVIRONMENT="${ENVIRONMENT:-production}"

echo -e "${BLUE}📋 Monitoring Configuration:${NC}"
echo "  🔥 Firebase Project: $FIREBASE_PROJECT"
echo "  🎯 Environment: $ENVIRONMENT"
echo ""

# Create monitoring configuration directories
mkdir -p monitoring/{dashboards,alerts,reports}

# Setup Firebase Performance Monitoring
echo -e "${PURPLE}⚡ Setting up Firebase Performance Monitoring${NC}"

cat > monitoring/firebase-performance-config.json << EOF
{
  "performanceMonitoring": {
    "enabled": true,
    "instrumentationKey": "tahlil-performance",
    "samplingRate": 0.1,
    "customTraces": [
      {
        "name": "memorial_creation_flow",
        "description": "Time to create a new memorial",
        "metrics": ["duration", "success_rate", "error_count"]
      },
      {
        "name": "prayer_submission",
        "description": "Prayer count submission performance",
        "metrics": ["duration", "batch_size", "network_latency"]
      },
      {
        "name": "photo_upload",
        "description": "Memorial photo upload performance",
        "metrics": ["duration", "file_size", "upload_speed"]
      },
      {
        "name": "islamic_content_validation",
        "description": "Time for cultural content validation",
        "metrics": ["validation_duration", "approval_rate"]
      },
      {
        "name": "community_interaction",
        "description": "Community sharing and interaction metrics",
        "metrics": ["share_duration", "invitation_success_rate"]
      }
    ],
    "screenTracking": {
      "enabled": true,
      "automaticScreenTracking": true,
      "screens": [
        "MemorialCreationScreen",
        "PrayerCounterScreen", 
        "CommunityScreen",
        "ProfileScreen",
        "SettingsScreen"
      ]
    },
    "networkRequestTracking": {
      "enabled": true,
      "httpMetrics": true,
      "responsePayloadSize": true
    }
  }
}
EOF

echo "  ⚡ Firebase Performance configuration created"

# Setup Firebase Analytics Custom Events
echo -e "${PURPLE}📊 Setting up Firebase Analytics${NC}"

cat > monitoring/firebase-analytics-events.json << EOF
{
  "customEvents": {
    "islamic_events": [
      {
        "name": "memorial_created",
        "description": "User creates a new memorial",
        "parameters": {
          "deceased_relationship": "string",
          "prayer_type": "string",
          "privacy_level": "string",
          "region": "string"
        }
      },
      {
        "name": "prayer_submitted",
        "description": "User submits prayers for memorial",
        "parameters": {
          "memorial_id": "string",
          "prayer_type": "string",
          "prayer_count": "number",
          "session_duration": "number"
        }
      },
      {
        "name": "family_member_invited",
        "description": "User invites family to memorial",
        "parameters": {
          "invitation_method": "string",
          "relationship": "string",
          "memorial_privacy": "string"
        }
      },
      {
        "name": "memorial_shared",
        "description": "Memorial shared with community",
        "parameters": {
          "sharing_platform": "string",
          "memorial_type": "string",
          "cultural_region": "string"
        }
      },
      {
        "name": "cultural_feature_used",
        "description": "Islamic cultural feature utilized",
        "parameters": {
          "feature_type": "string",
          "language": "string",
          "cultural_school": "string"
        }
      },
      {
        "name": "prayer_milestone_reached",
        "description": "Prayer count milestone achieved",
        "parameters": {
          "milestone_type": "string",
          "total_prayers": "number",
          "days_active": "number"
        }
      }
    ],
    "technical_events": [
      {
        "name": "app_performance_issue",
        "description": "Performance degradation detected",
        "parameters": {
          "issue_type": "string",
          "screen_name": "string",
          "device_info": "string"
        }
      },
      {
        "name": "offline_usage",
        "description": "App used while offline",
        "parameters": {
          "feature_used": "string",
          "offline_duration": "number",
          "sync_success": "boolean"
        }
      },
      {
        "name": "firebase_integration_error",
        "description": "Firebase service error occurred",
        "parameters": {
          "service_type": "string",
          "error_code": "string",
          "user_impact": "string"
        }
      }
    ]
  }
}
EOF

echo "  📊 Firebase Analytics events configuration created"

# Setup Crashlytics Custom Keys
echo -e "${PURPLE}🐛 Setting up Crashlytics Monitoring${NC}"

cat > monitoring/crashlytics-custom-keys.json << EOF
{
  "customKeys": {
    "user_context": [
      {
        "key": "user_language",
        "description": "User's selected language"
      },
      {
        "key": "cultural_region",
        "description": "User's cultural/regional setting"
      },
      {
        "key": "islamic_school",
        "description": "Selected Islamic school of thought"
      },
      {
        "key": "prayer_preferences",
        "description": "User's prayer type preferences"
      }
    ],
    "technical_context": [
      {
        "key": "firebase_project",
        "description": "Current Firebase project environment"
      },
      {
        "key": "app_version",
        "description": "Application version number"
      },
      {
        "key": "offline_mode",
        "description": "Whether app is in offline mode"
      },
      {
        "key": "last_sync_time",
        "description": "Last successful Firebase sync"
      }
    ],
    "feature_context": [
      {
        "key": "active_memorial_count",
        "description": "Number of active memorials"
      },
      {
        "key": "today_prayer_count",
        "description": "Prayers submitted today"
      },
      {
        "key": "last_used_feature",
        "description": "Last feature used before crash"
      }
    ]
  }
}
EOF

echo "  🐛 Crashlytics custom keys configuration created"

# Setup Monitoring Dashboard Configuration
echo -e "${PURPLE}📈 Setting up Monitoring Dashboards${NC}"

cat > monitoring/dashboards/tahlil-production-dashboard.json << EOF
{
  "dashboard": {
    "name": "Tahlil Production Dashboard",
    "description": "Comprehensive monitoring for Islamic memorial platform",
    "sections": [
      {
        "name": "Islamic Features Health",
        "widgets": [
          {
            "type": "metric",
            "title": "Daily Memorial Creations",
            "query": "custom_events.memorial_created",
            "visualization": "line_chart",
            "timeframe": "7d"
          },
          {
            "type": "metric", 
            "title": "Prayer Submissions",
            "query": "custom_events.prayer_submitted",
            "visualization": "bar_chart",
            "timeframe": "24h"
          },
          {
            "type": "metric",
            "title": "Cultural Feature Usage",
            "query": "custom_events.cultural_feature_used",
            "visualization": "pie_chart",
            "groupBy": "feature_type"
          },
          {
            "type": "metric",
            "title": "Regional Distribution",
            "query": "user_demographics.region",
            "visualization": "world_map"
          }
        ]
      },
      {
        "name": "Technical Performance",
        "widgets": [
          {
            "type": "performance",
            "title": "App Startup Time",
            "trace": "app_start",
            "percentile": 95,
            "threshold": 3000
          },
          {
            "type": "performance",
            "title": "Memorial Creation Flow",
            "trace": "memorial_creation_flow",
            "percentile": 90
          },
          {
            "type": "performance",
            "title": "Prayer Submission Performance", 
            "trace": "prayer_submission",
            "percentile": 90
          },
          {
            "type": "performance",
            "title": "Photo Upload Speed",
            "trace": "photo_upload",
            "percentile": 95
          }
        ]
      },
      {
        "name": "Quality & Reliability",
        "widgets": [
          {
            "type": "crashlytics",
            "title": "Crash-free Users",
            "metric": "crash_free_users",
            "threshold": 99.9
          },
          {
            "type": "crashlytics",
            "title": "ANR Rate",
            "metric": "anr_rate",
            "threshold": 0.1
          },
          {
            "type": "analytics",
            "title": "Daily Active Users",
            "metric": "daily_active_users"
          },
          {
            "type": "analytics",
            "title": "User Retention",
            "metric": "user_retention",
            "timeframes": ["1d", "7d", "30d"]
          }
        ]
      },
      {
        "name": "Firebase Services Health",
        "widgets": [
          {
            "type": "firestore",
            "title": "Firestore Operations",
            "metrics": ["reads", "writes", "deletes"],
            "timeframe": "24h"
          },
          {
            "type": "storage",
            "title": "Storage Usage",
            "metrics": ["uploads", "downloads", "bandwidth"],
            "timeframe": "24h"
          },
          {
            "type": "functions",
            "title": "Cloud Functions",
            "metrics": ["invocations", "errors", "duration"],
            "timeframe": "24h"
          },
          {
            "type": "auth",
            "title": "Authentication",
            "metrics": ["sign_ups", "sign_ins", "errors"],
            "timeframe": "24h"
          }
        ]
      }
    ]
  }
}
EOF

echo "  📈 Production dashboard configuration created"

# Setup Alerting Rules
echo -e "${PURPLE}🚨 Setting up Alert Rules${NC}"

cat > monitoring/alerts/production-alerts.json << EOF
{
  "alertRules": [
    {
      "name": "High Crash Rate",
      "description": "Alert when crash rate exceeds threshold",
      "condition": "crashlytics.crash_free_users < 99.9",
      "severity": "critical",
      "channels": ["slack", "email", "sms"],
      "threshold": {
        "value": 99.9,
        "operator": "less_than",
        "timeframe": "15m"
      }
    },
    {
      "name": "Performance Degradation",
      "description": "Alert when app startup time degrades",
      "condition": "performance.app_start.p95 > 5000",
      "severity": "high",
      "channels": ["slack", "email"],
      "threshold": {
        "value": 5000,
        "operator": "greater_than",
        "timeframe": "30m"
      }
    },
    {
      "name": "Firebase Services Down",
      "description": "Alert when Firebase services are unavailable",
      "condition": "firebase.service.availability < 99.0",
      "severity": "critical",
      "channels": ["slack", "email", "sms"],
      "threshold": {
        "value": 99.0,
        "operator": "less_than",
        "timeframe": "5m"
      }
    },
    {
      "name": "Low Prayer Submissions",
      "description": "Alert when prayer submissions drop significantly",
      "condition": "custom_events.prayer_submitted.count < 100",
      "severity": "medium",
      "channels": ["slack"],
      "threshold": {
        "value": 100,
        "operator": "less_than",
        "timeframe": "1h"
      }
    },
    {
      "name": "High Error Rate",
      "description": "Alert when error rate exceeds threshold",
      "condition": "errors.rate > 1.0",
      "severity": "high",
      "channels": ["slack", "email"],
      "threshold": {
        "value": 1.0,
        "operator": "greater_than",
        "timeframe": "15m"
      }
    },
    {
      "name": "Cultural Validation Failures",
      "description": "Alert when Islamic content validation fails frequently",
      "condition": "custom_events.cultural_validation_failed.count > 10",
      "severity": "medium",
      "channels": ["slack"],
      "threshold": {
        "value": 10,
        "operator": "greater_than",
        "timeframe": "1h"
      }
    }
  ]
}
EOF

echo "  🚨 Alert rules configuration created"

# Setup Health Check Endpoints
echo -e "${PURPLE}🏥 Setting up Health Check Monitoring${NC}"

cat > monitoring/health-checks.json << EOF
{
  "healthChecks": [
    {
      "name": "Firebase Firestore",
      "endpoint": "firestore-health-check",
      "type": "firebase-function",
      "interval": "5m",
      "timeout": "30s",
      "expectedResponse": {
        "status": "healthy",
        "services": ["firestore", "auth"]
      }
    },
    {
      "name": "Firebase Storage",
      "endpoint": "storage-health-check", 
      "type": "firebase-function",
      "interval": "5m",
      "timeout": "30s",
      "expectedResponse": {
        "status": "healthy",
        "uploadAvailable": true,
        "downloadAvailable": true
      }
    },
    {
      "name": "Prayer Submission API",
      "endpoint": "prayer-submission-health",
      "type": "firebase-function",
      "interval": "2m",
      "timeout": "10s",
      "expectedResponse": {
        "status": "operational",
        "rateLimit": "ok"
      }
    },
    {
      "name": "Memorial Creation API",
      "endpoint": "memorial-creation-health",
      "type": "firebase-function", 
      "interval": "5m",
      "timeout": "15s",
      "expectedResponse": {
        "status": "operational",
        "validation": "active"
      }
    }
  ]
}
EOF

echo "  🏥 Health check configuration created"

# Setup Cultural Monitoring
echo -e "${PURPLE}☪️ Setting up Islamic Cultural Monitoring${NC}"

cat > monitoring/cultural-monitoring.json << EOF
{
  "culturalMonitoring": {
    "islamicCompliance": {
      "contentValidation": {
        "arabicTextAccuracy": {
          "enabled": true,
          "validationFrequency": "realtime",
          "scholarReview": "weekly"
        },
        "prayerAuthenticity": {
          "enabled": true,
          "verificationLevel": "strict",
          "approvalRequired": true
        },
        "culturalSensitivity": {
          "enabled": true,
          "regionalCustoms": true,
          "familyValues": true
        }
      },
      "userBehaviorMonitoring": {
        "appropriateUsage": {
          "enabled": true,
          "flagInappropriateBehavior": true,
          "communityModeration": true
        },
        "religiousObservance": {
          "prayerTiming": true,
          "islamicHolidays": true,
          "culturalEvents": true
        }
      }
    },
    "regionalAdaptation": {
      "languageSupport": {
        "arabic": "primary",
        "regional": ["id", "ms", "ur", "tr", "fa"],
        "international": ["en", "fr", "de", "es"]
      },
      "culturalCustoms": {
        "southeast_asia": "enabled",
        "middle_east": "enabled", 
        "south_asia": "enabled",
        "north_africa": "enabled"
      }
    }
  }
}
EOF

echo "  ☪️ Cultural monitoring configuration created"

# Setup Automated Reporting
echo -e "${PURPLE}📋 Setting up Automated Reporting${NC}"

cat > monitoring/reports/daily-report-template.md << 'EOF'
# 📊 Tahlil Daily Production Report

**Date**: {{DATE}}
**Environment**: Production
**Firebase Project**: surah-almulk

## 🎯 Key Metrics Summary

### 📱 User Engagement
- **Daily Active Users**: {{DAU}}
- **Memorial Creations**: {{MEMORIAL_CREATIONS}}
- **Prayer Submissions**: {{PRAYER_SUBMISSIONS}}
- **Family Invitations**: {{FAMILY_INVITATIONS}}

### ⚡ Performance Metrics
- **App Startup Time (P95)**: {{APP_STARTUP_P95}}ms
- **Memorial Creation Flow (P90)**: {{MEMORIAL_CREATION_P90}}ms
- **Prayer Submission (P90)**: {{PRAYER_SUBMISSION_P90}}ms
- **Crash-free Users**: {{CRASH_FREE_USERS}}%

### 🔥 Firebase Services
- **Firestore Operations**: {{FIRESTORE_OPS}}
- **Storage Usage**: {{STORAGE_USAGE}}GB
- **Function Invocations**: {{FUNCTION_INVOCATIONS}}
- **Authentication Events**: {{AUTH_EVENTS}}

### ☪️ Islamic Cultural Metrics
- **Arabic Content Validated**: {{ARABIC_VALIDATION_COUNT}}
- **Cultural Compliance Score**: {{CULTURAL_COMPLIANCE_SCORE}}%
- **Regional Prayer Preferences**: {{REGIONAL_PREFERENCES}}
- **Islamic Feature Usage**: {{ISLAMIC_FEATURE_USAGE}}

## 🚨 Alerts & Issues

### Critical Issues
{{CRITICAL_ISSUES}}

### Performance Issues  
{{PERFORMANCE_ISSUES}}

### Cultural Concerns
{{CULTURAL_CONCERNS}}

## 🎯 Recommendations

{{RECOMMENDATIONS}}

---
*Generated automatically by Tahlil Monitoring System*
EOF

# Create monitoring setup summary
echo ""
echo -e "${GREEN}📊 Monitoring Setup Completed Successfully!${NC}"
echo -e "${BLUE}📋 Setup Summary:${NC}"
echo "  ✅ Firebase Performance Monitoring configured"
echo "  ✅ Analytics custom events defined"
echo "  ✅ Crashlytics custom keys setup"
echo "  ✅ Production dashboard created"
echo "  ✅ Alert rules configured"
echo "  ✅ Health checks defined"
echo "  ✅ Cultural monitoring enabled"
echo "  ✅ Automated reporting prepared"
echo ""

echo -e "${BLUE}📁 Configuration Files Created:${NC}"
find monitoring -name "*.json" -o -name "*.md" | while read file; do
    echo "  📄 $file"
done

echo ""
echo -e "${YELLOW}🎯 Next Steps:${NC}"
echo "1. 🔥 Deploy Firebase monitoring configurations"
echo "2. 📊 Import dashboard to monitoring service"
echo "3. 🚨 Configure alert notification channels"
echo "4. 🏥 Deploy health check functions"
echo "5. ☪️ Setup cultural validation workflows"
echo "6. 📋 Schedule automated reports"

echo ""
echo -e "${GREEN}🚀 Tahlil monitoring system ready for production!${NC}"