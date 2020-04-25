package pl.renesans.renesans.settings

import android.content.Context
import android.view.View
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import pl.renesans.renesans.R

class SettingsRowHolder(itemView: View, val context: Context, val presenter: SettingsContract.SettingsPresenter)
    : RecyclerView.ViewHolder(itemView), SettingsContract.SettingsRowView {

    override fun setSettingTitle(title: String) {
        itemView.findViewById<TextView>(R.id.settingTitle).text = title
    }

    override fun setSettingDescribe(describe: String) {
        itemView.findViewById<TextView>(R.id.settingDescribe).text = describe
    }

    override fun setCheckBoxChecked(checked: Boolean) {
        itemView.findViewById<CheckBox>(R.id.settingCheckBox).isChecked = checked
    }

    override fun setCheckBoxVisibility(visibility: Int) {
        itemView.findViewById<CheckBox>(R.id.settingCheckBox).visibility = visibility
    }

    override fun setUnderlineVisibility(visibility: Int) {
        itemView.findViewById<View>(R.id.underline).visibility = visibility
    }

    override fun setOnRowClickListener(pos: Int) {
        itemView.setOnClickListener{
            val checkBox = itemView.findViewById<CheckBox>(R.id.settingCheckBox)
            checkBox.performClick()
            presenter.itemClicked(pos, checkBox.isChecked)
        }
    }
}