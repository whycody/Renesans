package pl.renesans.renesans.settings

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import pl.renesans.renesans.R

class SettingsRowHolder(itemView: View, val context: Context, val presenter: SettingsContract.SettingsPresenter)
    : RecyclerView.ViewHolder(itemView), SettingsContract.SettingsRowView {

    override fun setSettingTitle(title: String) {
        itemView.findViewById<TextView>(R.id.settingTitle).text = title
    }

    override fun setupSettingDescription(visibility: Int, description: String?) {
        val settingDescription = itemView.findViewById<TextView>(R.id.settingDescription)
        settingDescription.visibility = visibility
        if(description != null) settingDescription.text = description
    }

    override fun setupCheckBox(visibility: Int, checked: Boolean) {
        val checkBox = itemView.findViewById<CheckBox>(R.id.settingCheckBox)
        checkBox.visibility = visibility
        checkBox.isChecked = checked
    }

    override fun setUnderlineVisibility(visibility: Int) {
        itemView.findViewById<View>(R.id.underline).visibility = visibility
    }

    override fun setupListDescription(visibility: Int, description: String?) {
        val listDescription = itemView.findViewById<TextView>(R.id.listDescription)
        listDescription.visibility = visibility
        if(description != null) listDescription.text = description
    }

    override fun setupSettingImage(visibility: Int, drawable: Drawable?) {
        val settingImage = itemView.findViewById<ImageView>(R.id.settingImage)
        settingImage.visibility = visibility
        if(drawable != null) settingImage.setImageDrawable(drawable)
    }

    override fun setOnRowClickListener(pos: Int) {
        itemView.setOnClickListener{
            val checkBox = itemView.findViewById<CheckBox>(R.id.settingCheckBox)
            checkBox.performClick()
            presenter.itemClicked(pos, checkBox.isChecked)
        }
    }
}