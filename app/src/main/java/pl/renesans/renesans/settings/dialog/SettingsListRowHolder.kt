package pl.renesans.renesans.settings.dialog

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_setting_radio_row.view.*

class SettingsListRowHolder(itemView: View, val presenter: SettingsListContract.SettingsListPresenter)
    : RecyclerView.ViewHolder(itemView), SettingsListContract.SettingsListRowView {

    override fun setTitle(title: String) {
        itemView.settingTitle.text = title
    }

    override fun setDescription(description: String) {
        itemView.settingDescribe.settingDescribe.text = description
    }

    override fun setOnClickListener(pos: Int) {
        itemView.setOnClickListener{ presenter.itemClicked(pos) }
    }

    override fun setRadioChecked(checked: Boolean) {
        itemView.settingRadio.isChecked = checked
    }

    override fun setUnderlineVisibility(visibility: Int) {
        itemView.underline.visibility = visibility
    }
}