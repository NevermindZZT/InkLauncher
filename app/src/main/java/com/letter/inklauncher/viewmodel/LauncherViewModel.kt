package com.letter.inklauncher.viewmodel

import android.app.admin.DevicePolicyManager
import android.content.*
import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
import androidx.core.content.edit
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableList
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.preference.PreferenceManager
import com.blankj.utilcode.util.ShellUtils
import com.letter.inklauncher.LetterApplication
import com.letter.inklauncher.service.CoreService
import com.letter.utils.AppInfo
import com.letter.utils.AppUtils
import kotlin.Exception

private const val DOUBLE_CLICK_TIME = 500
private const val TAG = "LauncherViewModel"

/**
 * launcher view model
 * @property appList MutableLiveData<ObservableList<AppInfo>> 显示的应用信息列表
 * @property filter IntentFilter intent filter
 * @property receiver <no name provided> 广播接收器
 * @property hidedPackages List<String>需要隐藏的包名列表
 *
 * @author Letter(nevermindzzt@gmail.com)
 * @since 1.0.0
 */
class LauncherViewModel : ViewModel() {

    val appList: MutableLiveData<ObservableList<AppInfo>> = MutableLiveData(
        ObservableArrayList()
    )
    val showLockButton = MutableLiveData(true)
    val showHideApp by lazy {
        MutableLiveData(PreferenceManager.getDefaultSharedPreferences(LetterApplication.instance())
            .getBoolean("show_hided_app", false))
    }

    private var clickTime = 0L

    private val filter by lazy {
        IntentFilter().apply {
            addAction(Intent.ACTION_PACKAGE_ADDED)
            addAction(Intent.ACTION_PACKAGE_REPLACED)
            addAction(Intent.ACTION_PACKAGE_REMOVED)
            addAction(Intent.ACTION_PACKAGE_FULLY_REMOVED)

            addDataScheme("package")
        }
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            loadAppList(context!!)
        }
    }

    private val hidedPackages: MutableList<String>
    get() {
        val packages = mutableListOf(
        "com.moan.moanwm",
        "com.letter.inklauncher")
        if (LetterApplication.instance().packageName == "com.moan.moanwm") {
            packages.add("com.android.settings")
            packages.add("com.mgs.factorytest")
        }
        val hide = PreferenceManager.getDefaultSharedPreferences(LetterApplication.instance())
            .getString("hided_packages", null)
        hide?.split(":")?.forEach {
            packages.add(it)
        }
        return packages
    }

    /**
     * view resume
     * @param context Context context
     */
    fun onViewResume(context: Context) {
        val value = PreferenceManager.getDefaultSharedPreferences(context)
            .getBoolean("show_hided_app", false)
        if (showHideApp.value != value) {
            showHideApp.value = value
        }
    }

    /**
     * 加载应用列表
     * @param context Context context
     */
    @Synchronized
    fun loadAppList(context: Context) {
        appList.value?.clear()
        appList.value?.addAll(
            AppUtils.getAppInfoList(context, PackageManager.GET_ACTIVITIES).filter {
                it.hasMainActivity && (showHideApp.value == true || !hidedPackages.contains(it.packageName))
            }.sortedBy {
                it.name
            }
        )
    }

    /**
     * 是否为隐藏的app
     * @param packageName String 包名
     * @return Boolean {@code true}隐藏 {@code false}非隐藏
     */
    fun isHideApp(packageName: String?) =
        hidedPackages.contains(packageName)

    /**
     * 添加隐藏的app
     * @param packageName String 包名
     */
    private fun addHidedPackage(packageName: String) {
        var hide = PreferenceManager.getDefaultSharedPreferences(LetterApplication.instance())
            .getString("hided_packages", "")?.split(":")?.toMutableList()
        if (hide == null) {
            hide = mutableListOf()
        }
        hide.add(packageName)
        val hideStr = hide.joinToString(separator = ":")
        PreferenceManager.getDefaultSharedPreferences(LetterApplication.instance())
            .edit {
                putString("hided_packages", hideStr)
            }
    }

    /**
     * 移除隐藏的app
     * @param packageName String 包名
     */
    private fun removeHidedPackage(packageName: String) {
        val hide = PreferenceManager.getDefaultSharedPreferences(LetterApplication.instance())
            .getString("hided_packages", "")?.split(":")?.toMutableList()
        if (hide != null && hide.contains(packageName)) {
            hide.remove(packageName)
            val hideStr = hide.joinToString(separator = ":")
            PreferenceManager.getDefaultSharedPreferences(LetterApplication.instance())
                .edit {
                    putString("hided_packages", hideStr)
                }
        }
    }

    /**
     * 注册广播接收器
     * @param context Context context
     */
    fun registerBroadcast(context: Context) {
        context.registerReceiver(receiver, filter)
    }

    /**
     * 注销广播接收器
     * @param context Context context
     */
    fun unregisterBroadcast(context: Context) {
        context.unregisterReceiver(receiver)
    }

    /**
     * 启动服务
     * @param context Context context
     */
    fun startService(context: Context) {
        try {
            context.startService(CoreService::class.java)
        } catch (e: Exception) {

        }
    }

    /**
     * 双击锁屏
     * @param context Context context
     */
    fun onDoubleClickLockScreen(context: Context) {
        if (PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean("enable_double_click_lock", false)) {
            if (System.currentTimeMillis() - clickTime < DOUBLE_CLICK_TIME) {
                val devicePolicyManager =
                    context.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager?
                try {
                    devicePolicyManager?.lockNow()
                } catch (e: Exception) {}
            }
            clickTime = System.currentTimeMillis()
        }
    }

    /**
     * 卸载或者禁用app
     * @param context Context context
     * @param appInfo AppInfo? app info
     */
    fun onUninstallOrDisable(context: Context, appInfo: AppInfo?) {
        if (appInfo?.isSystem == true) {
            ShellUtils.execCmd("pm disable  ${appInfo.packageName}", false)
        } else {
            com.blankj.utilcode.util.AppUtils.uninstallApp(appInfo?.packageName)
        }
    }

    /**
     * 隐藏或者显示app
     * @param context Context context
     * @param appInfo AppInfo? app info
     */
    fun onHideOrShow(context: Context, appInfo: AppInfo?) {
        if (appInfo?.packageName != null) {
            if (isHideApp(appInfo.packageName)) {
                removeHidedPackage(appInfo.packageName!!)
            } else {
                addHidedPackage(appInfo.packageName!!)
                loadAppList(context)
            }
        }
    }
}