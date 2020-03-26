package pl.renesans.renesans.discover.recycler

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import pl.renesans.renesans.R

class DiscoverRecyclerAdapter(val context: Context, val presenter: DiscoverRecyclerPresenter):
    RecyclerView.Adapter<DiscoverRowHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiscoverRowHolder {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.item_discover_row, parent, false)
        return DiscoverRowHolder(view, presenter)
    }

    override fun getItemCount(): Int {
        return presenter.getItemCount()
    }

    override fun onBindViewHolder(holder: DiscoverRowHolder, position: Int) {
        presenter.onBindViewHolder(holder, position)
    }
}