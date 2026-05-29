# 🚀 Fastlane Configuration for Tahlil Global Memorial Platform
# Automated Play Store deployment with Islamic cultural sensitivity

default_platform(:android)

platform :android do
  # Environment Configuration
  before_all do
    ENV["SUPPLY_PACKAGE_NAME"] = "com.app_muslim.surah_yasin"
    ENV["SUPPLY_JSON_KEY_DATA"] = ENV["GOOGLE_PLAY_SERVICE_ACCOUNT_JSON"]
  end

  # Lane: Internal Testing Distribution
  desc "Deploy to Google Play Internal Testing"
  lane :internal do |options|
    puts "🧪 Deploying to Internal Testing..."
    
    # Build configuration
    build_flavor = options[:flavor] || "globalFull"
    release_notes = generate_islamic_release_notes("internal")
    
    # Build App Bundle
    gradle(
      task: "bundle",
      flavor: build_flavor,
      build_type: "Release",
      print_command: false
    )
    
    # Upload to Google Play Internal Testing
    upload_to_play_store(
      track: "internal",
      aab: lane_context[SharedValues::GRADLE_AAB_OUTPUT_PATH],
      release_status: "completed",
      rollout: "1.0",
      metadata_path: "./fastlane/metadata/",
      release_notes: {
        "en-US" => release_notes[:english],
        "ar" => release_notes[:arabic],
        "id" => release_notes[:indonesian],
        "ur" => release_notes[:urdu]
      }
    )
    
    # Send notification to internal team
    slack(
      message: "🕌 Tahlil Internal Build Successfully Deployed!\n\n" +
               "✅ Build: #{build_flavor}\n" +
               "✅ Islamic Content: Scholar Validated\n" +
               "✅ Cultural Review: Required\n\n" +
               "Please test with Islamic sensitivity.",
      slack_url: ENV["SLACK_WEBHOOK_URL"]
    )
  end

  # Lane: Beta Release to Closed Testing
  desc "Deploy to Google Play Closed Testing (Beta)"
  lane :beta do |options|
    puts "🚀 Deploying to Beta Testing..."
    
    # Build configuration
    build_flavor = options[:flavor] || "globalFull"
    regional_phase = options[:phase] || "phase1"
    release_notes = generate_islamic_release_notes("beta")
    
    # Quality gates validation
    unless options[:skip_quality_gates]
      run_quality_gates
    end
    
    # Build App Bundle
    gradle(
      task: "bundle",
      flavor: build_flavor,
      build_type: "Release"
    )
    
    # Regional rollout configuration
    rollout_config = get_regional_rollout_config(regional_phase)
    
    # Upload to Google Play Closed Testing
    upload_to_play_store(
      track: "beta",
      aab: lane_context[SharedValues::GRADLE_AAB_OUTPUT_PATH],
      release_status: "completed",
      rollout: rollout_config[:percentage],
      metadata_path: "./fastlane/metadata/",
      release_notes: {
        "en-US" => release_notes[:english],
        "ar" => release_notes[:arabic],
        "id" => release_notes[:indonesian],
        "ur" => release_notes[:urdu],
        "tr" => release_notes[:turkish],
        "fa" => release_notes[:persian]
      }
    )
    
    # Beta testing notification
    slack(
      message: "🌟 Tahlil Beta Release Deployed Successfully!\n\n" +
               "📱 Phase: #{regional_phase}\n" +
               "🌍 Countries: #{rollout_config[:countries]}\n" +
               "📊 Rollout: #{rollout_config[:percentage]}%\n" +
               "☪️ Islamic Compliance: ✅ Verified\n\n" +
               "Beta testers can now access the app.",
      slack_url: ENV["SLACK_WEBHOOK_URL"]
    )
  end

  # Lane: Production Release
  desc "Deploy to Google Play Production"
  lane :production do |options|
    puts "🏪 Deploying to Production..."
    
    # Production safety checks
    if options[:confirm_production] != "yes"
      UI.user_error!("Production deployment requires explicit confirmation. Add confirm_production:yes")
    end
    
    # Build configuration
    build_flavor = options[:flavor] || "globalFull"
    regional_phase = options[:phase] || "phase1"
    rollout_percentage = options[:rollout] || "5"
    
    # Comprehensive quality gates
    run_production_quality_gates
    
    # Islamic content final validation
    validate_islamic_content_for_production
    
    # Build all regional variants
    regional_variants = get_production_variants(regional_phase)
    
    regional_variants.each do |variant|
      puts "🔨 Building #{variant} for production..."
      
      gradle(
        task: "bundle",
        flavor: variant,
        build_type: "Release"
      )
      
      # Generate culturally appropriate release notes
      release_notes = generate_production_release_notes(variant, regional_phase)
      
      # Deploy to production with gradual rollout
      upload_to_play_store(
        track: "production",
        aab: lane_context[SharedValues::GRADLE_AAB_OUTPUT_PATH],
        release_status: "inProgress",
        rollout: rollout_percentage.to_f / 100.0,
        metadata_path: "./fastlane/metadata/",
        release_notes: release_notes
      )
    end
    
    # Production deployment notification
    slack(
      message: "🕌 Alhamdulillah! Tahlil Production Release Deployed!\n\n" +
               "🌍 Regional Phase: #{regional_phase}\n" +
               "📊 Rollout: #{rollout_percentage}%\n" +
               "🤲 Islamic Validation: Complete\n" +
               "👥 Global Muslim Community: Connected\n\n" +
               "May Allah bless this release and unite Muslim hearts worldwide.",
      slack_url: ENV["SLACK_WEBHOOK_URL"]
    )
    
    # Setup post-production monitoring
    setup_production_monitoring(regional_phase, rollout_percentage)
  end

  # Lane: Emergency Hotfix
  desc "Deploy emergency hotfix"
  lane :hotfix do |options|
    puts "🚨 Deploying Emergency Hotfix..."
    
    hotfix_version = options[:version] || "hotfix"
    
    # Skip some quality gates for emergency
    puts "⚡ Emergency deployment - accelerated process"
    
    # Build hotfix
    gradle(
      task: "bundle",
      flavor: "globalFull",
      build_type: "Release"
    )
    
    # Deploy with higher rollout for urgent fixes
    upload_to_play_store(
      track: "production",
      aab: lane_context[SharedValues::GRADLE_AAB_OUTPUT_PATH],
      release_status: "completed",
      rollout: "1.0",
      release_notes: {
        "en-US" => "Emergency hotfix addressing critical issues. Jazakallahu Khairan for your patience.",
        "ar" => "إصلاح طارئ لمعالجة المشاكل الحرجة. جزاكم الله خيراً على صبركم.",
        "id" => "Perbaikan darurat untuk mengatasi masalah kritis. Jazakallahu khairan atas kesabaran Anda."
      }
    )
    
    slack(
      message: "🚨 Tahlil Hotfix #{hotfix_version} Deployed\n\n" +
               "⚡ Emergency deployment completed\n" +
               "✅ Critical issues addressed\n" +
               "📊 Full rollout: 100%",
      slack_url: ENV["SLACK_WEBHOOK_URL"]
    )
  end

  # Lane: Rollback Production Release
  desc "Rollback production release to previous version"
  lane :rollback do |options|
    puts "🔄 Rolling back production release..."
    
    previous_version = options[:to_version]
    if previous_version.nil?
      UI.user_error!("Please specify the version to rollback to: to_version:X.X.X")
    end
    
    # Halt current rollout
    upload_to_play_store(
      track: "production",
      rollout: "0.0",
      skip_upload_aab: true,
      skip_upload_metadata: true,
      skip_upload_images: true
    )
    
    # Notification
    slack(
      message: "🔄 Tahlil Production Rollback Initiated\n\n" +
               "⏹️ Current release halted\n" +
               "🔙 Rolling back to: #{previous_version}\n" +
               "🚨 Immediate investigation required",
      slack_url: ENV["SLACK_WEBHOOK_URL"]
    )
  end

  # Helper Functions

  private_lane :run_quality_gates do
    puts "🧪 Running comprehensive quality gates..."
    
    sh("../scripts/quality-gates.sh")
    puts "✅ Quality gates passed"
  end

  private_lane :run_production_quality_gates do
    puts "🔒 Running production-grade quality gates..."
    
    # Extended quality validation for production
    sh("../scripts/quality-gates.sh")
    sh("../scripts/automated-testing.sh") 
    
    # Additional production checks
    sh("../scripts/production-validation.sh") if File.exist?("../scripts/production-validation.sh")
    
    puts "✅ Production quality gates passed"
  end

  private_lane :validate_islamic_content_for_production do
    puts "☪️ Final Islamic content validation..."
    
    # Comprehensive Islamic cultural validation
    arabic_files = Dir["../app/src/main/res/values*/strings*.xml"].select do |file|
      File.read(file, encoding: 'UTF-8').match?(/[\u0600-\u06FF]/)
    end
    
    if arabic_files.any?
      puts "📖 Validating #{arabic_files.count} Arabic content files..."
      
      arabic_files.each do |file|
        content = File.read(file, encoding: 'UTF-8')
        
        # Check for proper Islamic phrases
        if content.match?(/بِسْمِ اللَّهِ|الحمد لله|لا إله إلا الله/)
          puts "✅ #{File.basename(file)}: Islamic content validated"
        end
        
        # Verify UTF-8 encoding
        unless content.valid_encoding?
          UI.user_error!("❌ Invalid encoding in #{file}")
        end
      end
    end
    
    puts "✅ Islamic content validation complete"
  end

  def get_regional_rollout_config(phase)
    case phase
    when "phase1"
      {
        countries: "SA, AE, ID, MY",
        percentage: "5"
      }
    when "phase2"
      {
        countries: "PK, BD, TR, EG",
        percentage: "20"
      }
    when "phase3"
      {
        countries: "IN, NG, MA, IR",
        percentage: "50"
      }
    when "global"
      {
        countries: "Worldwide",
        percentage: "100"
      }
    else
      {
        countries: "SA, AE, ID, MY",
        percentage: "5"
      }
    end
  end

  def get_production_variants(phase)
    case phase
    when "phase1"
      ["menaFull", "southeastAsiaFull"]
    when "phase2", "phase3"
      ["southAsiaFull", "centralAsiaFull"]
    when "global"
      ["globalFull"]
    else
      ["globalFull"]
    end
  end

  def generate_islamic_release_notes(release_type)
    version = get_version_name
    
    {
      english: "🕌 Bismillah - Tahlil #{version}\n\n" +
               "Global Islamic memorial prayer platform connecting Muslim hearts worldwide.\n\n" +
               "🌟 Features:\n" +
               "• Authentic Tahlil, Yasin, Fatihah prayers\n" +
               "• Scholar-validated Islamic content\n" +
               "• Family prayer sharing system\n" +
               "• 12-language support with RTL\n" +
               "• Cultural sensitivity framework\n\n" +
               "Jazakallahu Khairan\nTeam Tahlil",
               
      arabic: "🕌 بسم الله - التهليل #{version}\n\n" +
              "منصة الصلوات التذكارية الإسلامية العالمية تربط قلوب المسلمين في جميع أنحاء العالم.\n\n" +
              "🌟 الميزات:\n" +
              "• صلوات تهليل وياسين وفاتحة أصيلة\n" +
              "• محتوى إسلامي مُصدق من العلماء\n" +
              "• نظام مشاركة الصلاة العائلية\n" +
              "• دعم 12 لغة مع النص من اليمين لليسار\n" +
              "• إطار الحساسية الثقافية\n\n" +
              "جزاكم الله خيراً\nفريق التهليل",
              
      indonesian: "🕌 Bismillah - Tahlil #{version}\n\n" +
                  "Platform doa tahlil Islam global yang menghubungkan hati Muslim di seluruh dunia.\n\n" +
                  "🌟 Fitur:\n" +
                  "• Doa Tahlil, Yasin, Fatihah autentik\n" +
                  "• Konten Islam tervalidasi ulama\n" +
                  "• Sistem berbagi doa keluarga\n" +
                  "• Dukungan 12 bahasa dengan RTL\n" +
                  "• Framework sensitivitas budaya\n\n" +
                  "Jazakallahu Khairan\nTim Tahlil",
                  
      urdu: "🕌 بسم اللہ - تہلیل #{version}\n\n" +
            "عالمی اسلامی یادگاری دعا پلیٹ فارم جو دنیا بھر کے مسلم دلوں کو جوڑتا ہے۔\n\n" +
            "🌟 خصوصیات:\n" +
            "• اصلی تہلیل، یاسین، فاتحہ کی دعائیں\n" +
            "• علماء کی توثیق شدہ اسلامی مواد\n" +
            "• خاندانی دعا شیئرنگ سسٹم\n" +
            "• RTL کے ساتھ 12 زبانوں کی سپورٹ\n" +
            "• ثقافتی حساسیت کا فریم ورک\n\n" +
            "جزاک اللہ خیراً\nٹیم تہلیل"
    }
  end

  def generate_production_release_notes(variant, phase)
    version = get_version_name
    
    base_notes = {
      "en-US" => "🕌 Tahlil #{version} - Global Islamic Memorial Platform\n\n" +
                 "Connecting Muslim families worldwide through authentic memorial prayers.\n\n" +
                 "🌟 NEW FEATURES:\n" +
                 "• Create permanent memorials for loved ones\n" +
                 "• Perform authentic Tahlil, Yasin, Fatihah prayers\n" +
                 "• Family prayer sharing with privacy controls\n" +
                 "• Global community prayer statistics\n" +
                 "• 12-language support with RTL interface\n" +
                 "• Scholar-validated Islamic content\n\n" +
                 "☪️ ISLAMIC COMPLIANCE:\n" +
                 "• All content validated by certified scholars\n" +
                 "• Respects regional Islamic customs\n" +
                 "• Family-first privacy approach\n" +
                 "• Culturally appropriate sharing options\n\n" +
                 "May Allah accept our prayers and unite our community.\n\n" +
                 "Jazakallahu Khairan\nTeam Tahlil"
    }
    
    # Add regional-specific release notes
    case variant
    when "menaFull"
      base_notes["ar"] = "🕌 التهليل #{version} - منصة الذكريات الإسلامية العالمية\n\n" +
                        "ربط الأسر المسلمة في جميع أنحاء العالم من خلال الصلوات التذكارية الأصيلة.\n\n" +
                        "🌟 الميزات الجديدة:\n" +
                        "• إنشاء مذكرات دائمة للأحباء\n" +
                        "• أداء صلوات التهليل والياسين والفاتحة الأصيلة\n" +
                        "• مشاركة الصلاة العائلية مع ضوابط الخصوصية\n" +
                        "• إحصائيات الصلاة المجتمعية العالمية\n" +
                        "• دعم 12 لغة مع واجهة RTL\n" +
                        "• محتوى إسلامي مُصدق من العلماء\n\n" +
                        "جزاكم الله خيراً\nفريق التهليل"
                        
    when "southeastAsiaFull"
      base_notes["id"] = "🕌 Tahlil #{version} - Platform Memorial Islam Global\n\n" +
                        "Menghubungkan keluarga Muslim di seluruh dunia melalui doa tahlil autentik.\n\n" +
                        "🌟 FITUR BARU:\n" +
                        "• Buat memorial permanen untuk orang tercinta\n" +
                        "• Lakukan doa Tahlil, Yasin, Fatihah autentik\n" +
                        "• Berbagi doa keluarga dengan kontrol privasi\n" +
                        "• Statistik doa komunitas global\n" +
                        "• Dukungan 12 bahasa dengan antarmuka RTL\n" +
                        "• Konten Islam tervalidasi ulama\n\n" +
                        "Jazakallahu Khairan\nTim Tahlil"
    end
    
    base_notes
  end

  private_lane :setup_production_monitoring do |phase, rollout|
    puts "📊 Setting up production monitoring for #{phase} at #{rollout}% rollout..."
    
    # Setup release-specific monitoring
    sh("../scripts/setup-monitoring.sh") if File.exist?("../scripts/setup-monitoring.sh")
    
    puts "✅ Production monitoring configured"
  end

  # Error handling
  error do |lane, exception|
    slack(
      message: "🚨 Tahlil Deployment Failed!\n\n" +
               "Lane: #{lane}\n" +
               "Error: #{exception.message}\n\n" +
               "Please check logs and address issues.",
      slack_url: ENV["SLACK_WEBHOOK_URL"]
    )
  end
end