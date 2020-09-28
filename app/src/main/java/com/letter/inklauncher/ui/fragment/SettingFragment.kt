package com.letter.inklauncher.ui.fragment

import android.content.startService
import android.os.Bundle
import androidx.preference.*

import com.letter.inklauncher.R
import com.letter.inklauncher.service.FloatingBallService
import com.letter.inklauncher.service.NotificationService

/**
 * 设置Fragment
 * @author Letter(nevermindzzt@gmail.com)
 * @since 1.0.0
 */
class SettingFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.setting_preferences, rootKey)

        val themeModePreference = findPreference<SwitchPreference>("enable_floating_ball")
        themeModePreference?.setOnPreferenceChangeListener { _, newValue ->
            if (newValue as Boolean) {
                FloatingBallService.startService(requireContext())
            } else {
                FloatingBallService.stopService(requireContext())
            }
            true
        }
    }

    override fun onPreferenceTreeClick(preference: Preference?): Boolean {
        when (preference?.key) {
            "enable_back_to_home_notification" -> context?.startService(NotificationService::class.java)
        }
        return super.onPreferenceTreeClick(preference)
    }


}