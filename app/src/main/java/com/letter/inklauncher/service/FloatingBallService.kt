package com.letter.inklauncher.service

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
import com.letter.inklauncher.R
import com.letter.inklauncher.databinding.LayoutFloatingBallBinding

private const val TAG = "FloatingBallService"

/**
 * Floating ball service
 *
 * @author Letter(nevermindzzt@gmail.com)
 * @since 1.0.0
 */
class FloatingBallService : Service(), View.OnClickListener {

    private lateinit var binding: LayoutFloatingBallBinding

    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onCreate() {
        createFloatingBall()
        super.onCreate()
    }

    override fun onDestroy() {
        val windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        windowManager.removeView(binding.root)
        super.onDestroy()
    }

    private fun createFloatingBall() {
        val  layoutParams = WindowManager.LayoutParams()
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
            y = 0
            width = WindowManager.LayoutParams.WRAP_CONTENT
            height = WindowManager.LayoutParams.WRAP_CONTENT
        }

        binding = LayoutFloatingBallBinding.inflate(LayoutInflater.from(this.application))
        initBinding()
        windowManager.addView(binding.root, layoutParams)
        binding.root.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
    }

    private fun initBinding() {
        binding.let {
            it.onClickListener = this@FloatingBallService
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.floating_button -> {

            }
        }
    }

    companion object {
        fun startService(context: Context) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (Settings.canDrawOverlays(context)) {
                    context.startService(FloatingBallService::class.java)
                } else {
                    context.startActivity(Settings.ACTION_MANAGE_OVERLAY_PERMISSION) {
                        data = Uri.parse("package:" + context.packageName)
                    }
                }
            } else {
                context.startService(FloatingBallService::class.java)
            }
        }

        fun stopService(context: Context) {
            context.stopService(FloatingBallService::class.java)
        }
    }
}
