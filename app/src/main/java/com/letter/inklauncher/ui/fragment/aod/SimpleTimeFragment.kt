package com.letter.inklauncher.ui.fragment.aod

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.letter.inklauncher.databinding.FragmentSimpleTimeBinding

/**
 * 简单时钟AOD Fragment
 * @property binding FragmentSimpleTimeBinding binding
 *
 * @author Letter(nevermindzzt@gmail.com)
 * @since 1.0.0
 */
class SimpleTimeFragment : Fragment() {
    private lateinit var binding: FragmentSimpleTimeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSimpleTimeBinding.inflate(inflater)
        initBinding()
        return binding.root
    }

    private fun initBinding() {

    }
}