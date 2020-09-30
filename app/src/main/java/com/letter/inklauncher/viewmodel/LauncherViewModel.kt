package com.letter.inklauncher.viewmodel

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableList
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.letter.utils.AppInfo
import com.letter.utils.AppUtils

/**
 * launcher view model
 * @property appList MutableLiveData<ObservableList<AppInfo>> 显示的应用信息列表
 *
 * @author Letter(nevermindzzt@gmail.com)
 * @since 1.0.0
 */
class LauncherViewModel : ViewModel() {

    val appList: MutableLiveData<ObservableList<AppInfo>> = MutableLiveData(
        ObservableArrayList()
    )

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

        }
    }

    private val hidedPackages = listOf(
        "com.letter.inklauncher"
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

    fun registerBroadcast(context: Context) {
        context.registerReceiver(receiver, filter)
    }

    fun unregisterBroadcast(context: Context) {
        context.unregisterReceiver(receiver)
    }

}