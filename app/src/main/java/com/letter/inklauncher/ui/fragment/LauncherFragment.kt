package com.letter.inklauncher.ui.fragment

import android.content.sendBroadcast
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.startActivity
import androidx.lifecycle.*
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.GridLayoutManager
import com.blankj.utilcode.util.AppUtils
import com.letter.inklauncher.R
import com.letter.inklauncher.adapter.BindingViewAdapter
import com.letter.inklauncher.databinding.FragmentLauncherBinding
import com.letter.inklauncher.model.bean.Constants
import com.letter.inklauncher.ui.activity.AlwaysOnDisplayActivity
import com.letter.inklauncher.ui.activity.SettingActivity
import com.letter.inklauncher.utils.ChannelUtils
import com.letter.inklauncher.viewmodel.LauncherViewModel
import com.letter.presenter.ItemClickPresenter
import com.letter.presenter.ItemLongClickPresenter

/**
 * launcher fragment
 * @property binding FragmentLauncherBinding view data binding
 * @property model LauncherViewModel view model
 *
 * @author Letter(nevermindzzt@gmail.com)
 * @since 1.0.0
 */
class LauncherFragment : Fragment(), ItemClickPresenter, ItemLongClickPresenter, View.OnClickListener {

    private lateinit var binding: FragmentLauncherBinding
    private val model by lazy {
        ViewModelProvider(requireActivity()).get(LauncherViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLauncherBinding.inflate(inflater)
        initBinding()
        initModel()
        model.loadAppList(requireContext())
        model.registerBroadcast(requireContext())
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        model.unregisterBroadcast(requireContext())
    }

//    override fun onResume() {
//        model.showLockButton.value = PreferenceManager.getDefaultSharedPreferences(requireContext())
//            .getBoolean("enable_aod", false)
//        super.onResume()
//    }

    private fun initBinding() {
        binding.let {
            it.lifecycleOwner = this@LauncherFragment.viewLifecycleOwner
            it.bottomToolbar.onClickListener = this@LauncherFragment
            it.bottomToolbar.vm = model
        }
    }

    private fun initModel() {
        model.apply {
            appList.observe(this@LauncherFragment.viewLifecycleOwner) {
                val adapter = BindingViewAdapter(
                    this@LauncherFragment.requireContext(),
                    R.layout.layout_launcher_app_item,
                    it,
                    model,
                    this@LauncherFragment,
                    this@LauncherFragment
                )
                binding.launcherView.apply {
                    this.adapter = adapter
                    layoutManager = GridLayoutManager(this@LauncherFragment.requireContext(), 4)
                }
            }
        }
    }

    override fun onItemClick(adapter: Any, position: Int) {
        AppUtils.launchApp(model.appList.value?.get(position)?.packageName)
    }

    override fun onItemLongClick(adapter: Any, position: Int): Boolean {
        startActivity(Settings.ACTION_APPLICATION_DETAILS_SETTINGS) {
            data = Uri.fromParts("package", model.appList.value?.get(position)?.packageName, null)
        }
        return true
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.setting_button -> startActivity(SettingActivity::class.java)
            R.id.clear_button -> {
                if (ChannelUtils.isMiReader(requireContext())) {
                    requireContext().sendBroadcast(Constants.MI_READER_BROADCAST_CLEAR_MEM)
                }
            }
            R.id.lock_button -> {
                startActivity(AlwaysOnDisplayActivity::class.java)
            }
        }
    }
}