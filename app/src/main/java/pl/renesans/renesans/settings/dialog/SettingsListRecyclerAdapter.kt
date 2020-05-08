package pl.renesans.renesans.settings.dialog

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import pl.renesans.renesans.R

class SettingsListRecyclerAdapter(val context: Context, val presenter: SettingsListContract.SettingsListPresenter):
    RecyclerView.Adapter<SettingsListRowHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SettingsListRowHolder {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.item_setting_radio_row, parent, false)
        return SettingsListRowHolder(view, presenter)
    }

    override fun getItemCount(): Int {
        return presenter.getItemCount()
    }

    override fun onBindViewHolder(holder: SettingsListRowHolder, position: Int) {
        presenter.onBindViewHolder(holder, position)
    }
}