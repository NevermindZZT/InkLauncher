package com.letter.inklauncher.adapter

import android.view.View
import androidx.databinding.BindingAdapter

@BindingAdapter("android:onLongClick")
fun View.setOnLongClickListener(func: ()->Boolean) {
    setOnLongClickListener {
        func()
    }
}