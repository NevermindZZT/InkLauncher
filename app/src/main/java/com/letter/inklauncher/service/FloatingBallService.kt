package com.letter.inklauncher.service

import android.accessibilityservice.AccessibilityService
import android.app.Service
import android.content.*
import android.graphics.PixelFormat
import android.net.Uri
import android.os.Build
import android.os.IBinder
import android.provider.Settings
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityManager
import com.blankj.utilcode.util.ShellUtils
import com.letter.inklauncher.R
import com.letter.inklauncher.databinding.LayoutFloatingBallBinding
import com.letter.inklauncher.widget.FloatingBallView
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.lang.Math.abs

private const val TAG = "FloatingBallService"

/**
 * Floating ball service
 *
 * @author Letter(nevermindzzt@gmail.com)
 * @since 1.0.0
 */
class FloatingBallService : AccessibilityService(), View.OnClickListener {

    private lateinit var floatingBallView : FloatingBallView
    private lateinit var layoutParams: WindowManager.LayoutParams

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {

    }

    override fun onInterrupt() {

    }

    override fun onCreate() {
        createFloatingBall()
        super.onCreate()
    }

    override fun onDestroy() {
        (getSystemService(Context.WINDOW_SERVICE) as WindowManager?)?.removeView(floatingBallView)
        super.onDestroy()
    }

    private fun createFloatingBall() {
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

        floatingBallView = FloatingBallView(this)
        windowManager.addView(floatingBallView, layoutParams)
        floatingBallView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.floating_button -> {
                if (!performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK)) {
                    sendBroadcast("com.mogu.back_key")
                }
            }
        }
    }

    companion object {

        private fun checkAccessibility(context: Context, func: (() -> Unit)? = null) {
            try {
                context.startActivity(Settings.ACTION_ACCESSIBILITY_SETTINGS)
            } catch (e: Exception) {
                ShellUtils.execCmd(
                    "pm grant com.letter.inklauncher android.permission.BIND_ACCESSIBILITY_SERVICE",
                    false
                )
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
                            "pm grant com.letter.inklauncher android.permission.SYSTEM_ALERT_WINDOW",
                            false
                        )
                        func?.invoke()
                    }
                }
            } else {
                func?.invoke()
            }
        }

        fun startService(context: Context) {
            checkOverlaysPermission(context) {
                context.startService(FloatingBallService::class.java)
//                checkAccessibility(context)
            }
        }

        fun stopService(context: Context) {
            context.stopService(FloatingBallService::class.java)
        }
    }
}
