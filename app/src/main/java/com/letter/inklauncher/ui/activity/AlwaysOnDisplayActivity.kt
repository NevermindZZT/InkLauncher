package com.letter.inklauncher.ui.activity

import android.content.pm.ActivityInfo
import android.content.sendBroadcast
import android.content.toast
import android.os.*
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import com.letter.inklauncher.R
import com.letter.inklauncher.databinding.ActivityAlwaysOnDisplayBinding
import com.letter.inklauncher.model.bean.Constants
import com.letter.inklauncher.service.CoreService
import com.letter.inklauncher.ui.fragment.aod.SimpleTimeFragment
import com.letter.inklauncher.utils.ChannelUtils
import com.letter.presenter.ViewPresenter

/**
 * AOD Activity
 * @property binding ActivityAlwaysOnDisplayBinding binding
 * @property clickTime Long 点击时间
 *
 * @author Letter(nevermindzzt@gmail.com)
 * @since 1.0.0
 */
class AlwaysOnDisplayActivity : AppCompatActivity(), ViewPresenter {
    private lateinit var binding: ActivityAlwaysOnDisplayBinding

    private var clickTime = 0L

    private var wakeLock: PowerManager.WakeLock? = null
    private val handler by lazy {
        Handler(Looper.getMainLooper())
    }

    private val toastRunnable by lazy {
        Runnable {
            toast(R.string.toast_aod_double_click_to_back_to_home)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAlwaysOnDisplayBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.R) {
            binding.root.windowInsetsController?.apply {
                hide(WindowInsets.Type.ime()
                        or WindowInsets.Type.systemGestures()
                        or WindowInsets.Type.systemBars())
            }
            setShowWhenLocked(true)
            setTurnScreenOn(true)
        } else {
            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
            window.addFlags(
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                        or WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
            window.attributes.layoutInDisplayCutoutMode =
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_ALWAYS
        }
        if (PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean("aod_landscape", false)) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        }
        val powerManager = (getSystemService(POWER_SERVICE) as PowerManager?)
        wakeLock = powerManager?.newWakeLock(
            PowerManager.ACQUIRE_CAUSES_WAKEUP or PowerManager.SCREEN_DIM_WAKE_LOCK,
            "TAG:")
        wakeLock?.acquire()

        initBinding()
    }

    override fun onResume() {
        CoreService.startService(this, CoreService.INTENT_FLOATING_BALL_HIDE)
        if (ChannelUtils.isMiReader(this)) {
            sendBroadcast(Constants.MI_READER_BROADCAST_FORCE_REFRESH)
        }
        super.onResume()
    }

    override fun onPause() {
        CoreService.startService(this, CoreService.INTENT_FLOATING_BALL)
        super.onPause()
    }

    override fun onDestroy() {
        wakeLock?.release()
        super.onDestroy()
    }

    private fun initBinding() {
        binding.let {
            it.lifecycleOwner = this
            it.viewPresenter = this
        }
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_layout, SimpleTimeFragment())
            .commit()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.main_layout -> {
                if (System.currentTimeMillis() - clickTime < DOUBLE_CLICK_TIME) {
                    handler.removeCallbacks(toastRunnable)
                    finish()
                } else {
                    handler.postDelayed(toastRunnable, TOAST_DELAY)
                }
                clickTime = System.currentTimeMillis()
            }
        }
    }

    companion object {
        private const val DOUBLE_CLICK_TIME = 500
        private const val TOAST_DELAY = (DOUBLE_CLICK_TIME + 100).toLong()
    }
}