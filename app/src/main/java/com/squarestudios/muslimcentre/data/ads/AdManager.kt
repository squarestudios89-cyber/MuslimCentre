package com.squarestudios.muslimcentre.data.ads

import android.content.Context
import android.content.SharedPreferences
import com.google.android.gms.ads.MobileAds
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AdManager(private val context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("muslim_centre_ads_prefs", Context.MODE_PRIVATE)

    private val _isAdFree = MutableStateFlow(prefs.getBoolean(KEY_IS_AD_FREE, false))
    val isAdFree: StateFlow<Boolean> = _isAdFree.asStateFlow()

    init {
        // Initialize Mobile Ads SDK
        try {
            MobileAds.initialize(context) {}
        } catch (_: Exception) {
            // Handle initialization failure gracefully
        }
    }

    fun setAdFree(adFree: Boolean) {
        prefs.edit().putBoolean(KEY_IS_AD_FREE, adFree).apply()
        _isAdFree.value = adFree
    }

    companion object {
        private const val KEY_IS_AD_FREE = "is_ad_free_user"

        /**
         * AdMob Test Banner Ad Unit ID provided by Google.
         * Replace this with your official AdMob Banner Ad Unit ID when linking your account.
         */
        const val TEST_BANNER_AD_UNIT_ID = "ca-app-pub-3940256099942544/6300978111"
    }
}
