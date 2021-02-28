package com.letter.inklauncher.ui.fragment

import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.startActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.core.content.edit
import androidx.fragment.app.startActivity
import androidx.preference.*

import com.letter.inklauncher.R
import com.letter.inklauncher.receiver.DeviceManagerReceiver
import com.letter.inklauncher.service.CoreService

private const val TAG = "SettingFragment"

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
            "enable_double_click_lock" -> requestDeviceManager()
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

    /**
     * 请求设备管理权限
     */
    private fun requestDeviceManager() {
        if (PreferenceManager.getDefaultSharedPreferences(requireContext())
                .getBoolean("enable_double_click_lock", false)) {
            val devicePolicyManager =
                requireContext().getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager?
            val componentName = ComponentName(requireContext(), DeviceManagerReceiver::class.java)
            if (devicePolicyManager?.isAdminActive(componentName) != true) {
                Log.d(TAG, "request permission")
                startActivity(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN) {
                    putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentName)
                }
            }
        }
    }

    /**
     * 判断是否已经授权设备管理器
     * @return Boolean {@code true}已授权 {@false} 未授权
     */
    private fun hasDeviceManagerPermission(): Boolean {
        val devicePolicyManager =
            requireContext().getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager?
        val componentName = ComponentName(requireContext(), DeviceManagerReceiver::class.java)
        return devicePolicyManager?.isAdminActive(componentName) == true
    }
}