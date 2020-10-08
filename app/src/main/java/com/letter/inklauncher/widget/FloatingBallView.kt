package com.letter.inklauncher.widget

import android.accessibilityservice.AccessibilityService
import android.content.Context
import android.content.Intent
import android.content.sendBroadcast
import android.content.startActivity
import android.view.*
import android.widget.LinearLayout
import androidx.core.content.getSystemService
import com.letter.inklauncher.databinding.LayoutFloatingBallBinding
import com.letter.inklauncher.model.bean.Constants
import com.letter.inklauncher.utils.ChannelUtils

/**
 * 悬浮球View
 * @property binding LayoutFloatingBallBinding view binding
 * @property longPressed Boolean 是否处于长按状态
 * @property gestureDetector GestureDetector 手势检测
 * @constructor 构造一个悬浮球View
 *
 * @author Letter(nevermindzzt@gmail.com)
 * @since 1.0.0
 */
class FloatingBallView(context: Context) : LinearLayout(context), View.OnTouchListener {

    private var binding: LayoutFloatingBallBinding =
        LayoutFloatingBallBinding.inflate(LayoutInflater.from(context), this, true)

    private var longPressed = false

    private val gestureDetector = GestureDetector(
        context,
        object : GestureDetector.SimpleOnGestureListener() {

            override fun onSingleTapConfirmed(e: MotionEvent?): Boolean {
                back(context)
                return true
            }

            override fun onDoubleTap(e: MotionEvent?): Boolean {
                home(context)
                return true
            }

            override fun onLongPress(e: MotionEvent?) {
                longPressed = true
            }
        }
    )

    init {
        binding.floatingButton.setOnTouchListener(this)
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {

            }
            MotionEvent.ACTION_UP -> {
                longPressed = false
            }
            MotionEvent.ACTION_MOVE -> {
                val params = layoutParams as WindowManager.LayoutParams
                if (longPressed) {
                    params.x = (event.rawX - width / 2).toInt()
                    params.y = (event.rawY - height / 2).toInt()
                    context.getSystemService<WindowManager>()?.updateViewLayout(this, params)
                    invalidate()
                }
            }
        }
        return gestureDetector.onTouchEvent(event)
    }

    /**
     * 返回上一级
     * @param context Context context
     */
    private fun back(context: Context) {
        if (ChannelUtils.isMiReader(context)) {
            context.sendBroadcast(Constants.MI_READER_BROADCAST_BACK_KEY)
        } else {
            if (context is AccessibilityService) {
                context.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK)
            }
        }
    }

    /**
     * 返回桌面
     * @param context Context context
     */
    private fun home(context: Context) {
        context.startActivity(Intent.ACTION_MAIN) {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            addCategory(Intent.CATEGORY_HOME)
        }
    }
}