package pl.renesans.renesans.settings

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import pl.renesans.renesans.R

class SettingsAdapter(val context: Context, val presenter: SettingsContract.SettingsPresenter):
    RecyclerView.Adapter<SettingsRowHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SettingsRowHolder {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.item_setting_row, parent, false)
        return SettingsRowHolder(view, context, presenter)
    }

    override fun getItemCount(): Int {
      return presenter.getItemCount()
    }

    override fun onBindViewHolder(holder: SettingsRowHolder, position: Int) {
        presenter.onBindViewHolder(holder, position)
    }
}