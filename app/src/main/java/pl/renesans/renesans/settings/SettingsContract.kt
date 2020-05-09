package pl.renesans.renesans.settings

interface SettingsContract {

    interface SettingsView{

        fun refreshMapFragment()

        fun changedOptionOfMapLimit()

        fun notifyItemChangedAtPosition(pos: Int)
    }

    interface SettingsRowView {

        fun setSettingTitle(title: String)

        fun setSettingDescribe(describe: String)

        fun setCheckBoxChecked(checked: Boolean)

        fun setCheckBoxVisibility(visibility: Int)

        fun setUnderlineVisibility(visibility: Int)

        fun setOnRowClickListener(pos: Int)
    }

    interface SettingsPresenter {

        fun itemClicked(pos: Int, checkBoxValue: Boolean)

        fun getItemCount(): Int

        fun onBindViewHolder(holder: SettingsRowHolder, position: Int)
    }
}