package com.letter.inklauncher.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.letter.inklauncher.databinding.ActivityLauncherBinding
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

}