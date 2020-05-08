package pl.renesans.renesans.settings.dialog

import pl.renesans.renesans.data.SettingListItem

interface SettingsListContract {

    interface SettingsListView {

        fun getSettingItemsList(): List<SettingListItem>

        fun getDefaultSettingsItemPos(): Int

        fun radioBtnChoosed(mapModePos: Int)
    }

    interface SettingsListRowView {

        fun setTitle(title: String)

        fun setDescription(description: String)

        fun setOnClickListener(pos: Int)

        fun setRadioChecked(checked: Boolean)

        fun setUnderlineVisibility(visibility: Int)
    }

    interface SettingsListPresenter {

        fun onCreate()

        fun itemClicked(pos: Int)

        fun getItemCount(): Int

        fun onBindViewHolder(holder: SettingsListRowHolder, position: Int)
    }
}