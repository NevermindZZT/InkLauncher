package com.letter.inklauncher.ui.dialog.appinfo

import android.view.LayoutInflater
import android.view.View
import androidx.lifecycle.LifecycleOwner
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.letter.inklauncher.R
import com.letter.inklauncher.databinding.DialogAppInfoBinding
import com.letter.utils.AppInfo

typealias AppInfoDialogCallback = ((MaterialDialog, AppInfo?) -> Unit)

fun MaterialDialog.appInfo(
    layoutInflater: LayoutInflater,
    lifecycleOwner: LifecycleOwner,
    appInfo: AppInfo? = null,
    isHide: Boolean = false,
    uninstall: AppInfoDialogCallback? = null,
    hide: AppInfoDialogCallback? = null,
    info: AppInfoDialogCallback? = null
): MaterialDialog {
    val binding = DialogAppInfoBinding.inflate(layoutInflater)
    val onClickListener = View.OnClickListener {
        when (it.id) {
            R.id.uninstall_button -> uninstall?.invoke(this, appInfo)
            R.id.hide_button -> hide?.invoke(this, appInfo)
            R.id.info_button -> info?.invoke(this, appInfo)
        }
    }
    binding.let {
        it.lifecycleOwner = lifecycleOwner
        it.appInfo = appInfo
        it.isHide = isHide
        it.onClickListener = onClickListener
    }
    customView(view = binding.root, noVerticalPadding = true)
    return this
}