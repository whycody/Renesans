package pl.renesans.renesans.settings.dialog

import android.view.View
import pl.renesans.renesans.data.SettingListItem

class SettingsListPresenterImpl(val view: SettingsListContract.SettingsListView): SettingsListContract.SettingsListPresenter {

    private var settingItemsList = listOf<SettingListItem>()

    override fun onCreate() {
        settingItemsList = view.getSettingItemsList()
    }

    override fun itemClicked(pos: Int) {
        view.radioBtnChoosed(pos)
    }

    override fun getItemCount(): Int {
        return settingItemsList.size
    }

    override fun onBindViewHolder(holder: SettingsListRowHolder, position: Int) {
        holder.setTitle(settingItemsList[position].settingItemTitle!!)
        if(settingItemsList[position].settingItemDescription!=null)
            holder.setDescription(settingItemsList[position].settingItemDescription!!)
        holder.setOnClickListener(position)
        if(position == settingItemsList.size - 1) holder.setUnderlineVisibility(View.GONE)
        else holder.setUnderlineVisibility(View.VISIBLE)
        holder.setRadioChecked(view.getDefaultSettingsItemPos() == position)
    }
}