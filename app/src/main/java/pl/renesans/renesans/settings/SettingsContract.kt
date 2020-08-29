package pl.renesans.renesans.settings

import android.graphics.drawable.Drawable

interface SettingsContract {

    interface SettingsView{

        fun refreshMapFragment()

        fun changedOptionOfMapLimit()

        fun notifyItemChangedAtPosition(pos: Int)

        fun writeExternalStoragePermissionGranted()
    }

    interface SettingsRowView {

        fun setSettingTitle(title: String)

        fun setupSettingDescription(visibility: Int, description: String? = null)

        fun setupCheckBox(visibility: Int, checked: Boolean = false)

        fun setUnderlineVisibility(visibility: Int)

        fun setupListDescription(visibility: Int, description: String? = null)

        fun setupSettingImage(visibility: Int, drawable: Drawable? = null)

        fun setOnRowClickListener(pos: Int)
    }

    interface SettingsPresenter {

        fun onResume()

        fun itemClicked(pos: Int, checkBoxValue: Boolean)

        fun getItemCount(): Int

        fun onBindViewHolder(holder: SettingsRowHolder, position: Int)

        fun writeExternalStoragePermissionGranted()
    }
}