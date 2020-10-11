package com.letter.inklauncher.ui.fragment

import android.content.ComponentName
import android.content.startActivity
import android.os.Bundle
import android.provider.Settings
import androidx.preference.*

import com.letter.inklauncher.R
import com.letter.inklauncher.service.CoreService

/**
 * 设置Fragment
 * @author Letter(nevermindzzt@gmail.com)
 * @since 1.0.0
 */
class SettingFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.setting_preferences, rootKey)
    }

    override fun onPreferenceTreeClick(preference: Preference?): Boolean {
        when (preference?.key) {
            "enable_back_to_home_notification" -> CoreService.startService(requireContext(), CoreService.INTENT_NOTIFICATION)
            "enable_floating_ball" -> CoreService.startService(requireContext(), CoreService.INTENT_FLOATING_BALL)
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