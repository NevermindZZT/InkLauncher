package com.letter.inklauncher.ui.activity

import android.content.sendBroadcast
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import androidx.lifecycle.ViewModelProvider
import com.letter.inklauncher.databinding.ActivityLauncherBinding
import com.letter.inklauncher.model.bean.Constants
import com.letter.inklauncher.utils.ChannelUtils
import com.letter.inklauncher.viewmodel.LauncherViewModel

/**
 * launcher activity
 * @property binding ActivityLauncherBinding data view binding
 * @property model LauncherViewModel view model
 *
 * @author Letter(nevermindzzt@gmail.com)
 * @since 1.0.0
 */
class LauncherActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLauncherBinding
    private val model by lazy {
        ViewModelProvider(this).get(LauncherViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLauncherBinding.inflate(layoutInflater)
        setContentView(binding.root)

        model.startService(this)
    }

    override fun onResume() {
        super.onResume()
        if (ChannelUtils.isMiReader(this)) {
            sendBroadcast(Constants.MI_READER_BROADCAST_FORCE_REFRESH)
        }
    }

    override fun dispatchKeyEvent(event: KeyEvent?) =
        if (event?.keyCode == KeyEvent.KEYCODE_BACK
            || event?.keyCode == KeyEvent.KEYCODE_HOME) {
            true
        } else {
            super.dispatchKeyEvent(event)
        }
}