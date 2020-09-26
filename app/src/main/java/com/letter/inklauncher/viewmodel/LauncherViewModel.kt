package com.letter.inklauncher.viewmodel

import android.content.Context
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
            }
        )
    }

}