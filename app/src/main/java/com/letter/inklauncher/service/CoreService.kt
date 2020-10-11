package com.letter.inklauncher.service

import android.accessibilityservice.AccessibilityService
import android.app.NotificationManager
import android.content.*
import android.graphics.PixelFormat
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.view.accessibility.AccessibilityEvent
import androidx.preference.PreferenceManager
import com.blankj.utilcode.util.ShellUtils
import com.letter.inklauncher.R
import com.letter.inklauncher.ui.activity.AlwaysOnDisplayActivity
import com.letter.inklauncher.utils.ChannelUtils
import com.letter.inklauncher.utils.NotificationUtils
import com.letter.inklauncher.widget.FloatingBallView
import com.letter.utils.AccessibilityUtils
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

private const val TAG = "CoreService"

/**
 * core service
 *
 * @author Letter(nevermindzzt@gmail.com)
 * @since 1.0.0
 */
class CoreService : AccessibilityService(), View.OnClickListener {

    private val floatingBallView by lazy {
        FloatingBallView(this)
    }
    private lateinit var layoutParams: WindowManager.LayoutParams
    private var isFloatingBallAdded = false

    private val filter by lazy {
        IntentFilter().apply {
            addAction(Intent.ACTION_SCREEN_OFF)
        }
    }
    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (PreferenceManager.getDefaultSharedPreferences(this@CoreService)
                    .getBoolean("enable_aod", false)) {
                MainScope().launch {
                    startActivity(AlwaysOnDisplayActivity::class.java) {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                }
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.getStringExtra(INTENT_EXTRA)) {
            INTENT_NOTIFICATION -> {
                createHomeNotification()
            }
            INTENT_FLOATING_BALL -> {
                if (PreferenceManager.getDefaultSharedPreferences(this)
                        .getBoolean("enable_floating_ball", false)) {
                    showFloatingBall()
                } else {
                    hideFloatingBall()
                }
            }
            INTENT_FLOATING_BALL_HIDE -> {
                hideFloatingBall()
            }
            else -> {
                createHomeNotification()
                if (PreferenceManager.getDefaultSharedPreferences(this)
                        .getBoolean("enable_floating_ball", false)) {
                    showFloatingBall()
                } else {
                    hideFloatingBall()
                }
            }
        }
        return START_STICKY
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {

    }

    override fun onInterrupt() {

    }

    override fun onCreate() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(
                getString(R.string.notification_channel_intent_id),
                getString(R.string.notification_channel_intent_name),
                NotificationManager.IMPORTANCE_MAX)
        }

        registerReceiver(receiver, filter)

        super.onCreate()
    }

    override fun onDestroy() {
        hideFloatingBall()

        unregisterReceiver(receiver)
        super.onDestroy()
    }

    /**
     * 创建返回桌面通知
     */
    private fun createHomeNotification() {
        if (PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean("enable_back_to_home_notification", false)) {
            startForeground(-1, NotificationUtils.getHomeNotification(this))
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                stopForeground(-1)
            } else {
                stopForeground(true)
            }
        }
    }

    /**
     * 显示悬浮球
     */
    private fun showFloatingBall() {
        if (isFloatingBallAdded) {
            return
        }
        layoutParams = WindowManager.LayoutParams()
        val windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        layoutParams.apply {
            type =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                else
                    WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY
            format = PixelFormat.RGBA_8888
            flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
            gravity = Gravity.START or Gravity.TOP
            x = 0
            y = 500
            width = WindowManager.LayoutParams.WRAP_CONTENT
            height = WindowManager.LayoutParams.WRAP_CONTENT
        }

        isFloatingBallAdded = true
        windowManager.addView(floatingBallView, layoutParams)
        floatingBallView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
    }

    /**
     * 隐藏悬浮球
     */
    private fun hideFloatingBall() {
        if (!isFloatingBallAdded) {
            return
        }
        (getSystemService(Context.WINDOW_SERVICE) as WindowManager?)?.removeView(floatingBallView)
        isFloatingBallAdded = false
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.floating_button -> {
                if (ChannelUtils.isMiReader(this)) {
                    sendBroadcast("com.mogu.back_key")
                } else {
                    performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK)
                }
            }
        }
    }

    companion object {

        const val INTENT_EXTRA = "extra"
        const val INTENT_NOTIFICATION = "notification"
        const val INTENT_FLOATING_BALL = "floating_ball"
        const val INTENT_FLOATING_BALL_HIDE = "floating_ball_hide"

        private fun checkAccessibility(context: Context, func: (() -> Unit)? = null) {
            if (!AccessibilityUtils.isServiceEnabled(context, CoreService::class.java)) {
                try {
                    context.startActivity(Settings.ACTION_ACCESSIBILITY_SETTINGS)
                } catch (e: Exception) {
                    ShellUtils.execCmd(
                        "pm grant ${context.packageName} android.permission.BIND_ACCESSIBILITY_SERVICE",
                        false
                    )
                }
            }
            func?.invoke()
        }

        private fun checkOverlaysPermission(context: Context, func: (() -> Unit)? = null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (Settings.canDrawOverlays(context)) {
                    func?.invoke()
                } else {
                    try {
                        context.startActivity(Settings.ACTION_MANAGE_OVERLAY_PERMISSION) {
                            data = Uri.fromParts("package", context.packageName, null)
                        }
                    } catch (e: Exception) {
                        ShellUtils.execCmd(
                            "pm grant ${context.packageName} android.permission.SYSTEM_ALERT_WINDOW",
                            false
                        )
                        func?.invoke()
                    }
                }
            } else {
                func?.invoke()
            }
        }

        fun startService(context: Context, extra: String) {
            when (extra) {
                INTENT_FLOATING_BALL -> {
                    checkOverlaysPermission(context) {
                        context.startService(CoreService::class.java) {
                            putExtra(INTENT_EXTRA, extra)
                        }
                        if (!ChannelUtils.isMiReader(context)) {
                            checkAccessibility(context)
                        }
                    }
                }
                else -> {
                    context.startService(CoreService::class.java) {
                        putExtra(INTENT_EXTRA, extra)
                    }
                }
            }
        }
    }

}
