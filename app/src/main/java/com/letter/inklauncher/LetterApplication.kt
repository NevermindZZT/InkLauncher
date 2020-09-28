package com.letter.inklauncher

import android.app.Application
import android.content.startService
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.PreferenceManager
import com.letter.inklauncher.service.FloatingBallService
import com.letter.inklauncher.service.NotificationService

/**
 * Application
 *
 * @author Letter(nevermindzt@gmail.com)
 * @since 1.0.0
 */
class LetterApplication : Application() {

    companion object {
        /**
         * Application 单例
         */
        private var instance: LetterApplication ?= null

        /**
         * 获取Application实例
         * @return LetterApplication Application实例
         */
        @JvmStatic
        fun instance() = instance!!
    }

    override fun onCreate() {
        super.onCreate()
        instance = this

        AppCompatDelegate.setDefaultNightMode(
            PreferenceManager.getDefaultSharedPreferences(this)
                .getString("theme_mode", "-1")?.toInt() ?: -1
        )

        startService(NotificationService::class.java)
        if (PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean("enable_floating_ball", false)) {
            FloatingBallService.startService(this)
        }

    }
}