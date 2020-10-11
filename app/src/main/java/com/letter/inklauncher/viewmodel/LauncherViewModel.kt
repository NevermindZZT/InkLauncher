package com.letter.inklauncher.viewmodel

import android.app.NotificationManager
import android.content.*
import android.content.pm.PackageManager
import android.os.Build
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableList
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.preference.PreferenceManager
import com.letter.inklauncher.R
import com.letter.inklauncher.service.CoreService
import com.letter.utils.AppInfo
import com.letter.utils.AppUtils

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
    val showLockButton = MutableLiveData(false)

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

    private val hidedPackages = listOf(
        "com.moan.moanwm",
        "com.letter.inklauncher",
        "com.android.settings",
        "com.mgs.factorytest"
    )

    /**
     * 加载应用列表
     * @param context Context context
     */
    @Synchronized
    fun loadAppList(context: Context) {
        appList.value?.clear()
        appList.value?.addAll(
            AppUtils.getAppInfoList(context, PackageManager.GET_ACTIVITIES).filter {
                it.hasMainActivity && !hidedPackages.contains(it.packageName)
            }.sortedBy {
                it.name
            }
        )
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
        context.startService(CoreService::class.java)
    }
}