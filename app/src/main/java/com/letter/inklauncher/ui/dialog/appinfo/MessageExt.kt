package com.letter.inklauncher.ui.dialog.appinfo

import android.view.LayoutInflater
import android.view.View
import androidx.annotation.StringRes
import androidx.lifecycle.LifecycleOwner
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.letter.inklauncher.R
import com.letter.inklauncher.databinding.DialogMessageBinding

typealias MessageCallback = (MaterialDialog) -> Unit

val cancelCallback = object : MessageCallback {
    override fun invoke(dialog: MaterialDialog) {
        dialog.dismiss()
    }
}

fun MaterialDialog.message(
    layoutInflater: LayoutInflater,
    lifecycleOwner: LifecycleOwner,
    @StringRes messageRes: Int = 0,
    confirm: MessageCallback? = null,
    cancel: MessageCallback? = cancelCallback,
    message: String? = null
) {
    val binding = DialogMessageBinding.inflate(layoutInflater)
    val onClickListener = View.OnClickListener {
        when (it.id) {
            R.id.positive_button -> confirm?.invoke(this)
            R.id.negative_button -> cancel?.invoke(this)
        }
    }
    binding.let {
        it.lifecycleOwner = lifecycleOwner
        if (messageRes != 0) {
            it.message = context.getString(messageRes)
        } else {
            it.message = message
        }
        it.onClickListener = onClickListener
    }
    customView(view = binding.root, noVerticalPadding = true)
}