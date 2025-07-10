package com.app_muslim.surah_yasin.data.preference

class CounterPreference(val corePreference: CorePreference) {

    companion object {
        const val SUBHANALLAH = "SUBHANALLAH"
        const val ALHAMDULILLAH = "ALHAMDULILLAH"
        const val LAILAHAILALLAH = "LAILAHAILALLAH"
        const val ALLAHU_AKBAR = "ALLAHU_AKBAR"
        const val ASTAGHFIRULLAH = "ASTAGHFIRULLAH"
        const val TARGET = "TARGET"
    }

    var subhanallah: Int
        set(value) = corePreference.setInt(SUBHANALLAH, value)
        get() = corePreference.getInt(SUBHANALLAH, 0)

    var alhamdulillah: Int
        set(value) = corePreference.setInt(ALHAMDULILLAH, value)
        get() = corePreference.getInt(ALHAMDULILLAH, 0)

    var lailahailallah: Int
        set(value) = corePreference.setInt(LAILAHAILALLAH, value)
        get() = corePreference.getInt(LAILAHAILALLAH, 0)

    var allahukkbar: Int
        set(value) = corePreference.setInt(ALLAHU_AKBAR, value)
        get() = corePreference.getInt(ALLAHU_AKBAR, 0)

    var astaghfirullah: Int
        set(value) = corePreference.setInt(ASTAGHFIRULLAH, value)
        get() = corePreference.getInt(ASTAGHFIRULLAH, 0)

    var target: Int
        set(value) = corePreference.setInt(TARGET, value)
        get() = corePreference.getInt(TARGET, 0)

}