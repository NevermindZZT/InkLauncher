package com.letter.inklauncher.ui.activity

import android.os.Bundle
import android.os.PowerManager
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.letter.inklauncher.R
import com.letter.inklauncher.databinding.ActivityAlwaysOnDisplayBinding
import com.letter.inklauncher.ui.fragment.aod.SimpleTimeFragment
import com.letter.presenter.ViewPresenter
import kotlinx.android.synthetic.main.activity_always_on_display.view.*

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAlwaysOnDisplayBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        window.addFlags(
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                    or WindowManager.LayoutParams.FLAG_FULLSCREEN)
        val powerManager = (getSystemService(POWER_SERVICE) as PowerManager?)
        val wakeLock = powerManager?.newWakeLock(
            PowerManager.ACQUIRE_CAUSES_WAKEUP or PowerManager.SCREEN_DIM_WAKE_LOCK,
            "TAG:")
        wakeLock?.acquire()

        initBinding()
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
                    finish()
                }
                clickTime = System.currentTimeMillis()
            }
        }
    }

    companion object {
        private const val DOUBLE_CLICK_TIME = 500
    }
}