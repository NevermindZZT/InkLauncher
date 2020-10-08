package com.letter.inklauncher.ui.fragment

import android.content.ComponentName
import android.content.startActivity
import android.content.startService
import android.os.Bundle
import android.provider.Settings
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
            "wireless_setting" -> context?.startActivity(Settings.ACTION_WIRELESS_SETTINGS)
            "display_setting" -> context?.startActivity(Settings.ACTION_DISPLAY_SETTINGS)
            "date_setting" -> context?.startActivity(Settings.ACTION_DATE_SETTINGS)
            "accessibility_setting" -> context?.startActivity(Settings.ACTION_ACCESSIBILITY_SETTINGS)
            "input_method_setting" -> context?.startActivity(Settings.ACTION_INPUT_METHOD_SETTINGS)
            "device_manager_setting" -> context?.startActivity {
                component = ComponentName(
                    "com.android.settings",
                    "com.android.settings.DeviceAdminSettings"
                )
                action = "android.intent.action.VIEW"
            }
            "app_manager_setting" -> context?.startActivity(Settings.ACTION_APPLICATION_SETTINGS)
        }
        return super.onPreferenceTreeClick(preference)
    }


}