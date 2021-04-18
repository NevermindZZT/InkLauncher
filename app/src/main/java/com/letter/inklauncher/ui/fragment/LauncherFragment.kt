package com.letter.inklauncher.ui.fragment

import android.content.sendBroadcast
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.startActivity
import androidx.lifecycle.*
import androidx.recyclerview.widget.GridLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import com.blankj.utilcode.util.AppUtils
import com.letter.inklauncher.R
import com.letter.inklauncher.adapter.BindingViewAdapter
import com.letter.inklauncher.databinding.FragmentLauncherBinding
import com.letter.inklauncher.model.bean.Constants
import com.letter.inklauncher.ui.activity.AlwaysOnDisplayActivity
import com.letter.inklauncher.ui.activity.SettingActivity
import com.letter.inklauncher.ui.dialog.appinfo.AppInfoDialogCallback
import com.letter.inklauncher.ui.dialog.appinfo.MessageCallback
import com.letter.inklauncher.ui.dialog.appinfo.appInfo
import com.letter.inklauncher.ui.dialog.appinfo.message
import com.letter.inklauncher.utils.ChannelUtils
import com.letter.inklauncher.viewmodel.LauncherViewModel
import com.letter.presenter.ItemClickPresenter
import com.letter.presenter.ItemLongClickPresenter
import com.letter.utils.AppInfo

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

    override fun onResume() {
        super.onResume()
        model.onViewResume(requireContext())
    }

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
            showHideApp.observe(this@LauncherFragment.viewLifecycleOwner) {
                model.loadAppList(this@LauncherFragment.requireContext())
            }
        }
    }

    override fun onItemClick(adapter: Any, position: Int) {
        AppUtils.launchApp(model.appList.value?.get(position)?.packageName)
    }

    override fun onItemLongClick(adapter: Any, position: Int): Boolean {
//        startActivity(Settings.ACTION_APPLICATION_DETAILS_SETTINGS) {
//            data = Uri.fromParts("package", model.appList.value?.get(position)?.packageName, null)
//        }
        MaterialDialog(requireContext()).show {
            appInfo(
                layoutInflater,
                this@LauncherFragment.viewLifecycleOwner,
                model.appList.value?.get(position),
                model.isHideApp(model.appList.value?.get(position)?.packageName),
                object : AppInfoDialogCallback {
                    override fun invoke(dialog: MaterialDialog, appinfo: AppInfo?) {
                        onUninstallOrDisable(appinfo)
                        dialog.dismiss()
                    } },
                object : AppInfoDialogCallback {
                    override fun invoke(dialog: MaterialDialog, appinfo: AppInfo?) {
                        onHideOrShow(appinfo)
                        dialog.dismiss()
                    }
                }
            )
        }
        return true
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.time_view -> model.onDoubleClickLockScreen(requireContext())
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

    fun onUninstallOrDisable(appInfo: AppInfo?) {
        MaterialDialog(requireContext()).show {
            message(
                layoutInflater,
                this@LauncherFragment.viewLifecycleOwner,
                confirm = object : MessageCallback {
                    override fun invoke(dialog: MaterialDialog) {
                        model.onUninstallOrDisable(requireContext(), appInfo)
                        dialog.dismiss()
                    }

                },
                message = getString(if (appInfo?.isSystem == true) R.string.dialog_app_disable_confirm_text else R.string.dialog_app_uninstall_confirm_text)
                    .format(appInfo?.name)
            )
        }
    }

    fun onHideOrShow(appInfo: AppInfo?) {
        MaterialDialog(requireContext()).show {
            message(
                layoutInflater,
                this@LauncherFragment.viewLifecycleOwner,
                confirm = object : MessageCallback {
                    override fun invoke(dialog: MaterialDialog) {
                        model.onHideOrShow(requireContext(), appInfo)
                        dialog.dismiss()
                    }

                },
                message = getString(if (model.isHideApp(appInfo?.packageName))
                        R.string.dialog_app_show_confirm_text else R.string.dialog_app_hide_confirm_text)
                    .format(appInfo?.name)
            )
        }
    }
}